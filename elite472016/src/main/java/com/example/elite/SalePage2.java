package com.example.elite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bean.classes.CustomerInfo;
import bean.classes.SaleProduct;

import com.print.Device;
import com.print.PrintActivity;
import com.print.PrintSettingActivity;
import com.print.PrinterClass;
import com.print.PrinterClassFactory;

import custom.classes.DBCalss;
import custom.classes.GetDevID;
import custom.scroll.LockableScrollView;
import drawing.canvas.DrawingCanvas;

public class SalePage2 extends Activity 
{
	final ArrayList<String> duplicatePrds= new ArrayList<String>();
	final static List<String> productIdlist= new ArrayList<String>();
	
	ListView lstProduct;
	DrawingCanvas drawingCanvas;
	Button btnClear, btnDone,btnPrint;
	double totalAmount;
	double totalAmtofVolumeDisItems;
	String cDisAmount="",cNetAmount="";
	LockableScrollView scrollView;
	TextView txtInvoiceNo, txtDueDate, txtNetAmt, txtTotal_Amt,txtSaleDate,txtRefund,txtPrinterStatus;
	EditText edtReceiptPersonName, edtPayAmount;
	CheckBox chkApprove;
	boolean hasSignFinish = false;	
	DecimalFormat invoiceFormat = new DecimalFormat("000");  
	DecimalFormat commaSepFormat = new DecimalFormat("###,##0"); 
	DecimalFormat decimalFormatter = new DecimalFormat("0.00");
	SimpleDateFormat fmtForInvoiceTodayStr = new SimpleDateFormat("yyMMdd");
	SimpleDateFormat fmtForTodayStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");
	byte[] data = null;
	String imgBase64Str;
	String duplicateProductID =null;
	String signImg;
	double netAmt;
	double pay_Amount;
	static int totalDuplicateProductQty =0;
	int i = 0;
	String invoiceNo;
	Bitmap myBitmap;
	double totalItemDisAndVolDisAmt=0;
	double totalAmtNoDiscount;
	boolean isPayAmtisEmpty= false;
	
	//Test Printer
	
	protected static final String TAG = "SalePage2";
	public static boolean checkState=true;
	private Thread tv_update;
	
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	Handler mhandler=null;
	Handler handler = null;
	
	//for bluetooth print position
	public int position = 0;
	
	String saleManIDPrefs = null;
	String saleManNamePrefs = null;
	String saleManPwdPrefs = null;	
	String locationCodePrefs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{ 
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salepage_2);
		hideScreenKeyboardInPageDisplay();// for hidden keyboard		
		registerIDs();//register 
		autorunHandler();//for printer
		setInitialData();//initialize 
		makeInvoiceNo();//do invoice 
		setAdapters();// sale product list
		sumTotalValueAndCalculateVolumeDis();		
		catchEvents();	
	}
	
	private void autorunHandler() 
	{
		mhandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MESSAGE_STATE_CHANGE:// è“�ç‰™è¿žæŽ¥çŠ¶
					switch (msg.arg1) {
					case PrinterClass.STATE_CONNECTED:// å·²ç»�è¿žæŽ¥
						break;
					case PrinterClass.STATE_CONNECTING:// æ­£åœ¨è¿žæŽ¥
						break;
					case PrinterClass.STATE_LISTEN:
					case PrinterClass.STATE_NONE:
						break;
					case PrinterClass.SUCCESS_CONNECT:
						break;
					case PrinterClass.FAILED_CONNECT:
						
						break;
					case PrinterClass.LOSE_CONNECT:
						Log.i(TAG, "LOSE_CONNECT");
					}
					break;
				case MESSAGE_READ:
					// sendFlag = false;//ç¼“å†²åŒºå·²ï¿½?
					break;
				case MESSAGE_WRITE:// ç¼“å†²åŒºæœªï¿½?
					// sendFlag = true;
					break;
				}
			}
		};
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					break;
				case 1:// æ‰«æ��å®Œæ¯•
					//PrintActivity.pl.stopScan();
					Device d=(Device)msg.obj;
					if(d!=null)
					{
						if(PrintSettingActivity.deviceList==null)
						{
							PrintSettingActivity.deviceList=new ArrayList<Device>();
						}
						
						if(!checkData(PrintSettingActivity.deviceList,d))
						{
							PrintSettingActivity.deviceList.add(d);
						}
					}
					break;
				case 2:// å�œæ­¢æ‰«æ��
					break;
				}
			}
		};		
		
		//Printer Status Update
		tv_update = new Thread() {
			public void run() {
				while (true) {
					if(checkState)
					{
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					txtPrinterStatus.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (PrintActivity.pl != null) {
								if (PrintActivity.pl.getState() == PrinterClass.STATE_CONNECTED) {
									txtPrinterStatus.setText(SalePage2.this
											.getResources().getString(
													R.string.str_connected));
								} else if (PrintActivity.pl.getState() == PrinterClass.STATE_CONNECTING) {
									txtPrinterStatus.setText(SalePage2.this
											.getResources().getString(
													R.string.str_connecting));
								} else if(PrintActivity.pl.getState() == PrinterClass.LOSE_CONNECT
										||PrintActivity.pl.getState() == PrinterClass.FAILED_CONNECT){
									checkState=false;
									txtPrinterStatus.setText(SalePage2.this
											.getResources().getString(
													R.string.str_disconnected));
									Intent intent=new Intent();
									intent.setClass(SalePage2.this,PrintSettingActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent);
								}else{
									txtPrinterStatus.setText(SalePage2.this
											.getResources().getString(
													R.string.str_disconnected));
								}
							}
						}
					});
					}
				}
			}
		};
		
	}
	
	 private boolean checkData(List<Device> list,Device d)
	    {
	    	for (Device device : list) {
				if(device.deviceAddress.equals(d.deviceAddress))
				{
					return true;
				}
			}
	    	return false;
	    } 


	    @Override
	    protected void onRestart() {
	    	// TODO Auto-generated method stub
	    	checkState=true;
	    	super.onRestart();
	    }

	private void setInitialData() 
	{
		// TODO Auto-generated method stub
		//txtDiscountAmt.setText("0");
		txtDueDate.setText("0");
		txtNetAmt.setText("0");
		txtTotal_Amt.setText("0");		 
	}

	private void makeInvoiceNo() 
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}
		
		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		
		int count = 0;

		String[] selCol = {"COUNT(*)"};
		cur = DBCalss.eliteDB.query("SaleData", selCol, null, null, null, null, null);

		while(cur.moveToNext())
		{	
			count = cur.getInt(cur.getColumnIndex("COUNT(*)"));
		}

		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
		Calendar todayCal = Calendar.getInstance();
		String todayDate = fmtForInvoiceTodayStr.format(todayCal.getTime());
		txtSaleDate.setText("Sale Date : "+fmtForDueStr.format(todayCal.getTime()));
		invoiceNo = saleManIDPrefs + todayDate + invoiceFormat.format(count + 1);
		txtInvoiceNo.setText(invoiceNo);				
		todayCal.add(todayCal.DATE, CustomerInfo.creditTerm);
		String due_date=fmtForDueStr.format(todayCal.getTime());		
		txtDueDate.setText(due_date);

	}

	@Override
	public void onBackPressed()
	{
		totalAmtofVolumeDisItems =0;
		SalePage.totalItemDiscounts =0;
		startActivity(new Intent(SalePage2.this,SalePage.class));
		finish();
		super.onBackPressed();
	}

	private void makeSlidingDrawer()
	{
		SlideDrawerPage sld = new SlideDrawerPage(this);
		sld.makeSlidingDrawer();
	}

	private void catchEvents() 
	{
		edtPayAmount.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) 
			{
				double refund;
				String payAmount=edtPayAmount.getText().toString().trim();
				if(!payAmount.equals(""))
				{
					pay_Amount=Integer.parseInt(payAmount);
					if(pay_Amount > netAmt)
					{
						refund= pay_Amount - netAmt;
						txtRefund.setText(decimalFormatter.format(refund));
					}
					else
					{
						txtRefund.setText("0");
					}
				}
			}
		});
		btnClear.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				drawingCanvas.startNew();		
				hasSignFinish = false;
			}
		});

		drawingCanvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					scrollView.setScrollingEnabled(false);
					break;				
				case MotionEvent.ACTION_UP:
					hasSignFinish = true;
					scrollView.setScrollingEnabled(true);
					break;				
				}
				return false;
			}
		});

		btnDone.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v) 
			{
				final String errorField = checkValidation();

				if(!errorField.equals(""))
				{
					if(errorField.contains("No Credit"))
					{
						final AlertDialog diag3= new AlertDialog.Builder(SalePage2.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\nYou are not credit customer.Must be paid by cash.\n")		
						.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface diag2, int arg1) 
							{
								
							}
						})	
						.create();

						diag3.show();
						diag3.setCancelable(false);
						diag3.setCanceledOnTouchOutside(false);
					}
					else if(errorField.contains("Insufficient Credit"))
					{
							final AlertDialog diag3= new AlertDialog.Builder(SalePage2.this)
							.setTitle("Information")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage("\n"+ "Your Credit Amt is Insufficient.Please pay by Cash." +"\n")		
							.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface diag2, int arg1) 
								{

								}							
							})							
							.create();

							diag3.show();
							diag3.setCancelable(false);
							diag3.setCanceledOnTouchOutside(false);
					}
					
					else if(errorField.contains("Please Click"))
					{
							final AlertDialog diag3= new AlertDialog.Builder(SalePage2.this)
							.setTitle("Information")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage("\n"+ errorField +"\n")		
							.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface diag2, int arg1) 
								{

								}							
							})							
							.create();

							diag3.show();
							diag3.setCancelable(false);
							diag3.setCanceledOnTouchOutside(false);
					}
					else if(errorField.contains("Pay Amount"))
					{
						final AlertDialog diag3= new AlertDialog.Builder(SalePage2.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\n Pay Amount is Empty! \n")		
						.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface diag2, int arg1) 
							{
								isPayAmtisEmpty = true;
							}							
						})							
						.create();

						diag3.show();
						diag3.setCancelable(false);
						diag3.setCanceledOnTouchOutside(false);
					}
					else
					{
							final AlertDialog diag3= new AlertDialog.Builder(SalePage2.this)
							.setTitle("Information")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage("\n" + errorField +" is empyt.\n")		
							.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface diag2, int arg1) 
								{
								}
							})					
							.create();

							diag3.show();
							diag3.setCancelable(false);
							diag3.setCanceledOnTouchOutside(false);
						}
				}
				else
				{
					View v1 = getWindow().getDecorView().getRootView();
					v1.setDrawingCacheEnabled(true);
					myBitmap = v1.getDrawingCache();
										
					Log.e(myBitmap+"", "OutPutBitmap");
					saveInvoiceImageIntoGallery(myBitmap);
					saveSaleData();
					startActivity(new Intent(SalePage2.this, HomePage.class));
					finish();
				}
			}
		});		
		
		btnPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final String errorField = checkValidation();

				if(!errorField.equals(""))
				{
					if(errorField.contains("No Credit"))
					{
						final AlertDialog diag3= new AlertDialog.Builder(SalePage2.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\nYou are not credit customer.Must be paid by cash.\n")		
						.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface diag2, int arg1) 
							{
								
							}
						})	
						.create();

						diag3.show();
						diag3.setCancelable(false);
						diag3.setCanceledOnTouchOutside(false);
					}
					else if(errorField.contains("Insufficient Credit"))
					{
							final AlertDialog diag3= new AlertDialog.Builder(SalePage2.this)
							.setTitle("Information")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage("\n"+ "Your Credit Amt is Insufficient.Please pay by Cash." +"\n")		
							.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface diag2, int arg1) 
								{

								}							
							})							
							.create();

							diag3.show();
							diag3.setCancelable(false);
							diag3.setCanceledOnTouchOutside(false);
					}
					
					else if(errorField.contains("Please Click"))
					{
							final AlertDialog diag3= new AlertDialog.Builder(SalePage2.this)
							.setTitle("Information")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage("\n"+ errorField +"\n")		
							.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface diag2, int arg1) 
								{

								}							
							})							
							.create();

							diag3.show();
							diag3.setCancelable(false);
							diag3.setCanceledOnTouchOutside(false);
					}
					else if(errorField.contains("Pay Amount"))
					{
						final AlertDialog diag3= new AlertDialog.Builder(SalePage2.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\n Pay Amount is Empty! \n")		
						.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface diag2, int arg1) 
							{
								isPayAmtisEmpty = true;
							}							
						})							
						.create();

						diag3.show();
						diag3.setCancelable(false);
						diag3.setCanceledOnTouchOutside(false);
					}
					else
					{
							final AlertDialog diag3= new AlertDialog.Builder(SalePage2.this)
							.setTitle("Information")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage("\n" + errorField +" is empyt.\n")		
							.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface diag2, int arg1) 
								{
								}
							})					
							.create();

							diag3.show();
							diag3.setCancelable(false);
							diag3.setCanceledOnTouchOutside(false);
						}
				}
				else
				{
					View v1 = getWindow().getDecorView().getRootView();
					v1.setDrawingCacheEnabled(true);
					myBitmap = v1.getDrawingCache();
					Log.e(myBitmap+"", "OutPutBitmap");
					saveInvoiceImageIntoGallery(myBitmap);
					saveSaleData();
					PrintActivity.pl=PrinterClassFactory.create(position, SalePage2.this, mhandler,handler);
					SalePage2.checkState=true;
					Intent intent= new Intent();
			    	intent.setClass(SalePage2.this,PrintActivity.class);
					startActivity(intent);							
					startActivity(new Intent(SalePage2.this, PrintSettingActivity.class));
					finish();
				}
			}
		});			
	}
	
	private String checkValidation()
	{			
		if(!hasSignFinish)
		{
			return "Your Sign";
		}

		else if(edtReceiptPersonName.getText().length() == 0)
		{
			return "Receipt Person Name";
		}
		
		else if(!chkApprove.isChecked())
		{
			return "Please Click On Checkbox To Approve All Data Are Correct.";
		}
		
		else if( pay_Amount < netAmt)
		{
			if(CustomerInfo.creditLimit == 0)
			{
				return "No Credit";
			}
			else if(CustomerInfo.creditLimit > 0)
			{
				double remainingAmt;
				remainingAmt = netAmt - pay_Amount ;
				if(remainingAmt > CustomerInfo.creditAmount)
				{
					 return "Insufficient Credit";
				}
			}
		}

		else if(edtPayAmount.getText().length() == 0)
		{
			if(isPayAmtisEmpty == true)
			{
				return "";
			}
			else
			{
				return "Pay Amount";
			}
		}

		return "";				
	}
	
	private void saveInvoiceImageIntoGallery(Bitmap bitmap)
	{
		File sdCard = Environment.getExternalStorageDirectory();
		File directory = new File (sdCard.getAbsolutePath() + "/ScreenShot/Sale");
		directory.mkdirs();

		String filename =  invoiceNo + ".jpg"; 
		File yourFile = new File(directory, filename);

		while (yourFile.exists()) {
			i++;   
			filename = invoiceNo + ".jpg"; 
			yourFile = new File(directory, filename);
		} 

		String image=yourFile.toString();
		Log.e(image, "ImageOOO");


		FileOutputStream fos;
		try {
			fos = new FileOutputStream(yourFile, true);
			bitmap.compress(CompressFormat.PNG, 40, fos);//change 100 to 40
						
			fos.flush();
			fos.close();
			Toast.makeText(SalePage2.this, "Image saved to /sdcard/ScreenShot/Sale" + invoiceNo + ".jpg", Toast.LENGTH_SHORT).show();
			i++;
			//sendMail(filePath);
		} catch (FileNotFoundException e) {
			Log.e("GREC", e.getMessage(), e);
		} catch (IOException e) {
			Log.e("GREC", e.getMessage(), e);
		}

		Bitmap bitmapOrg1= BitmapFactory.decodeFile(image);
		ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
		Log.e(bitmapOrg1.toString()+"","BitMap1");
		Log.e(bao1.toString(),"BAO");
		bitmapOrg1.compress(Bitmap.CompressFormat.JPEG, 40, bao1); //change 90 to 40
		 
		Log.e("here1", "here1");
		byte [] ba1 = bao1.toByteArray();

		imgBase64Str=Base64.encodeToString(ba1, Base64.NO_WRAP);
		Log.e("ImageBase64String",imgBase64Str.toString()+ "aa");
	}
	
	private String  saveSignIntoGallery()
	{
		Bitmap bm = Bitmap.createBitmap(drawingCanvas.getMeasuredWidth(),
				drawingCanvas.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas bigcanvas = new Canvas(bm);
		Paint paint = new Paint();
		int iHeight = bm.getHeight();
		bigcanvas.drawBitmap(bm, 0, iHeight, paint);
		drawingCanvas.draw(bigcanvas);
		
		File sdCard = Environment.getExternalStorageDirectory();
		File file = new File (sdCard.getAbsolutePath() + "/SignImage/Sale");
		file.mkdirs();

		String filename =  invoiceNo + ".jpg"; 
		File yourFile = new File(file, filename);
		
		String image= yourFile.toString();
		Log.e(image,"ImageSS");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(yourFile, true);
			bm.compress(Bitmap.CompressFormat.PNG, 50, fos);
			fos.flush();
			fos.close();
			bm.recycle();
			Toast.makeText(SalePage2.this, "Image saved to /sdcard/SignImage/Sale" + invoiceNo + ".jpg", Toast.LENGTH_SHORT).show();			
		} catch (FileNotFoundException e) {
			Log.e("GREC", e.getMessage(), e);
		} catch (IOException e) {
			Log.e("GREC", e.getMessage(), e);
		}
		
		Bitmap bitmapOrg1= BitmapFactory.decodeFile(image);
		ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
		Log.e(bitmapOrg1.toString()+"","BitMap1");
		Log.e(bao1.toString(),"BAO");
		bitmapOrg1.compress(Bitmap.CompressFormat.JPEG, 90, bao1);
		Log.e("here1", "here1");
		byte [] ba1 = bao1.toByteArray();
		bitmapOrg1.compress(Bitmap.CompressFormat.JPEG, 90, bao1);
		Log.e("here1", "here1");
		signImg=Base64.encodeToString(ba1, Base64.NO_WRAP);
		Log.e("ImageString",signImg.toString()+ "aa");

		return signImg;
		}
	private void sumTotalValueAndCalculateVolumeDis()
	{
		for(int i=0;i<SalePage.saleProductList.size();i++)
		{ 
			if(SalePage.saleProductList.get(i).getIsFoc().equals("false"))
			{
				if(SalePage.saleProductList.get(i).getDiscountType().equals("V"))
				{
					totalAmtofVolumeDisItems += SalePage.saleProductList.get(i).getTotalAmt();
				}
//				else
//				{
//					totalAmtofVolumeDisItems =0;
//				}
			
				totalAmount +=SalePage.saleProductList.get(i).getTotalAmt(); 
				totalAmtNoDiscount += Double.parseDouble(SalePage.saleProductList.get(i).getTotalAmtNoDiscount());
				Log.e(totalAmtNoDiscount+"","TotalAmtNoDiscount");
			}
		}
		
		//txtDiscountAmt.setText(commaSepFormat.format(HomePage.volumeDiscount));
		
		Log.e(SalePage.totalItemDiscounts+"","SalePageTotalItemDis");
		
		totalItemDisAndVolDisAmt = SalePage.totalItemDiscounts;
		
		txtTotal_Amt.setText(commaSepFormat.format(totalAmount+totalItemDisAndVolDisAmt));
		netAmt = totalAmount;
		txtNetAmt.setText(decimalFormatter.format(netAmt));
	}
	private void saveSaleData() 
	{
		Calendar todayCal = Calendar.getInstance();
		String todayDate = fmtForTodayStr.format(todayCal.getTime());	
		String currentTime = fmtForTodayStr.format(todayCal.getTime());
		double totalAmtForInvoice;
		int saleQty = 0;
		int remainingQty=0;
		int remainingQtyFromDB=0;
		Log.e(SalePage.saleProductList.size()+"", "ArrlistSize");
		for (SaleProduct sp : SalePage.saleProductList)
		{
			if(sp.getRemainingQty() != null)
			{
				remainingQtyFromDB = Integer.parseInt(sp.getRemainingQty());
			}
			if(sp.getTempSaleQty() != null)
			{
				//saleQty += Integer.parseInt(sp.getTempSaleQty());
				boolean status=hasDuplicates(sp.getProductId(),sp.getTempSaleQty());
				Log.e(status+"","DuplicateStatus");
				if(status == true)
				{
					//saleQty += Integer.parseInt(sp.getTempSaleQty());
					saleQty = totalDuplicateProductQty;
				}
				else
				{
					saleQty = Integer.parseInt(sp.getTempSaleQty());
				}
				
			}
			Log.e("SaleQtyforTesting",saleQty+"");
			 remainingQty = remainingQtyFromDB - saleQty;
			 Log.e(remainingQtyFromDB + "", "FinalRemainingFromDB");
			 Log.e(saleQty + "", "FinalSaleQty");
			 Log.e(remainingQty +"", "FinalRemainingQty");
			 sp.setSaleQty(saleQty+"");
			 sp.setRemainingQty(remainingQty+"");
		}

		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();

		for(SaleProduct sp : SalePage.saleProductList)
		{
			
			String arg[]= {sp.getProductId()};
			ContentValues cv=new ContentValues();
			Log.e(sp.getRemainingQty(),"SP.getRemainingQty()");
			cv.put("RemainingQty", sp.getRemainingQty());
			cv.put("SaleQty", sp.getSaleQty());

			DBCalss.eliteDB.update("Product", cv, "productId LIKE ?", arg);
		}

		for(SaleProduct sp : SalePage.saleProductList)
		{
			ContentValues cv = new ContentValues();			

			cv.put("invoiceID", invoiceNo);
			cv.put("productID", sp.getProductId());	
			cv.put("productName", sp.getProductName());
			cv.put("saleQty", sp.getTempSaleQty());
			cv.put("salePrice", sp.getSellingPrice());
			cv.put("purchasePrice", sp.getPurchasePrice());			
			cv.put("isFoc",sp.getIsFoc());
			
			if(sp.getIsFoc().equals("false"))
			{
				cv.put("discountAmt", decimalFormatter.format(sp.getDiscount()));
				cv.put("discountPercent", sp.getDiscountPercent());
				cv.put("totalAmt", decimalFormatter.format(sp.getTotalAmt()));
			}
			else
			{
				cv.put("discountAmt", "0");
				cv.put("totalAmt", "0");
			}
			
			if (sp.getRemark() != null) {
				cv.put("remark", sp.getRemark());
			}
			else {
				cv.put("remark", "");
			}

			DBCalss.eliteDB.insert("SaleDataDetail", null, cv);			
		}	
		cNetAmount= txtNetAmt.getText().toString().trim().replaceAll(",","");

		ContentValues cv = new ContentValues();			

		cv.put("invoiceID", invoiceNo);
		cv.put("customerID", CustomerInfo.customer_ID);	
		cv.put("saleDate", todayDate);
		cv.put("netAmt", decimalFormatter.format(netAmt));
		cv.put("TotalAmtNoDiscount",decimalFormatter.format(totalAmtNoDiscount));
		cv.put("volumediscountAmt", cDisAmount.trim());
		Log.e(totalItemDisAndVolDisAmt+"","Totalllllllll");
		cv.put("TotalVolandItemDisAmt", decimalFormatter.format(totalItemDisAndVolDisAmt));
		cv.put("refundAmt", txtRefund.getText().toString().trim().replaceAll(",",""));

		cv.put("payAmt", edtPayAmount.getText().toString().trim());
		cv.put("receitpPersonName",edtReceiptPersonName.getText().toString().trim());	
		cv.put("InvoiceImg",imgBase64Str);
		cv.put("signImg",saveSignIntoGallery());
		cv.put("salePersonID",saleManIDPrefs);
		cv.put("dueDate",txtDueDate.getText().toString().trim());
		if(edtPayAmount.getText().toString().length() == 0)
		{
			cv.put("cashOrCredit","R");
		}
		else if(Double.parseDouble(edtPayAmount.getText().toString()) < Double.parseDouble(cNetAmount.toString()))
		{
			cv.put("cashOrCredit","R");
		}

		else if(Double.parseDouble(edtPayAmount.getText().toString())==Double.parseDouble(cNetAmount.toString()))
		{
			cv.put("cashOrCredit","C");
		}
		else
		{
			cv.put("cashOrCredit","C");
		}		
		cv.put("locationCode", locationCodePrefs);	
		cv.put("devID", GetDevID.getDevId(SalePage2.this));
		cv.put("invoiceTime", currentTime);

		DBCalss.eliteDB.insert("SaleData", null, cv);	

		DBCalss.eliteDB.setTransactionSuccessful();	
		DBCalss.eliteDB.endTransaction();	

		Toast.makeText(getApplicationContext(),"Saving Success ...", Toast.LENGTH_LONG).show();

		SalePage.saleProductList.clear();
		SalePage.totalItemDiscounts = 0;
		totalAmtofVolumeDisItems=0;
		duplicatePrds.clear();
		productIdlist.clear();				
	}

	private void hideScreenKeyboardInPageDisplay()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);		
	}

	private void setAdapters() 
	{
		lstProduct.setAdapter(new CustomList(this));
		txtTotal_Amt.setText(totalAmount+"");
	}

	private void registerIDs() 
	{
		lstProduct = (ListView)findViewById(R.id.productBalanceReport_lstProduct);
		drawingCanvas = (DrawingCanvas) findViewById(R.id.salePage2_drawingCanvas);
		btnClear = (Button) findViewById(R.id.salePage2_btnClear);
		btnDone = (Button) findViewById(R.id.salePage2_btnDone);
		txtInvoiceNo = (TextView) findViewById(R.id.salePage2_txtInvoiceNo);
		scrollView = (LockableScrollView) findViewById(R.id.saleCutomList2_scrollView);
		chkApprove = (CheckBox) findViewById(R.id.salePage2_chkApprove);
		//txtDiscountAmt = (TextView) findViewById(R.id.salePage2_txtDiscount);
		txtDueDate = (TextView) findViewById(R.id.saleinvoiceReport2_txtDueDate);
		txtNetAmt = (TextView)findViewById(R.id.salePage2_txtNetAmt);
		txtTotal_Amt = (TextView)findViewById(R.id.salePage2_txtTotalAmt);
		edtReceiptPersonName = (EditText)findViewById(R.id.salePage2_edtReceiptPersonName);
		edtPayAmount = (EditText) findViewById(R.id.salePage2_edtPayAmount);
		txtSaleDate = (TextView) findViewById(R.id.productBalanceReport_txtDate);
		txtRefund =(TextView) findViewById(R.id.salePage2_txtRefund);
		btnPrint = (Button) findViewById(R.id.salePage2_btnPrint);
		Log.e(SalePage.saleProductList.size()+"", "ArrlistSize");
		
		txtPrinterStatus = (TextView)findViewById(R.id.txtPrinterStatus);
		
		SharedPreferences prefs = getSharedPreferences("SaleManPrefs", MODE_PRIVATE); 
		saleManIDPrefs = prefs.getString(MainActivity.saleManIdPrefs, "No name defined");
		saleManNamePrefs = prefs.getString(MainActivity.saleManNamePrefs,"No name defined" );
		saleManPwdPrefs = prefs.getString(MainActivity.passwordPrefs, "No name defined");	
		locationCodePrefs = prefs.getString(MainActivity.locationCodePrefs, "No name defined");	
	}

	public class CustomList extends ArrayAdapter<SaleProduct>
	{
		private final Activity context;

		public CustomList(Activity context)
		{
			super(context, R.layout.sale_custom2_list_row, SalePage.saleProductList);
			this.context = context; 
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.sale_custom2_list_row, null, true);

			TextView txtDiscount = (TextView) rowView.findViewById(R.id.sale2customLayout_txtDiscount);
			TextView txtPrice = (TextView) rowView.findViewById(R.id.sale2customLayout_txtPrice);
			TextView txtProductName = (TextView) rowView.findViewById(R.id.sale2customLayout_txtProductName);
			TextView txtTotalAmt = (TextView) rowView.findViewById(R.id.sale2customLayout_txtTotalAmt);
			TextView txtSaleQty = (TextView) rowView.findViewById(R.id.sale2customLayout_txtQty);
			final TextView txtRemark = (TextView) rowView.findViewById(R.id.sale2customLayout_txtRemark);//added by Hak for serial remark
			SaleProduct sp = SalePage.saleProductList.get(position); 
			int price=(int)Math.round(Double.parseDouble(sp.getSellingPrice()));			          
			txtSaleQty.setText(sp.getTempSaleQty());
			txtPrice.setText(commaSepFormat.format(price));
			txtProductName.setText(sp.getProductName());
			if(sp.getIsFoc().equals("true"))
			{
				txtDiscount.setText("0");
				txtTotalAmt.setText("0");
			}
			else
			{
				txtDiscount.setText(decimalFormatter.format(sp.getDiscount()));
				txtTotalAmt.setText(decimalFormatter.format(sp.getTotalAmt()));
			}
			
			//added by Hak for serial remark
			txtRemark.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					LayoutInflater layoutInflater = (LayoutInflater) context.getLayoutInflater();
                    final View view = layoutInflater.inflate(R.layout.dialog_box_remark, null);

                    final SaleProduct sp = SalePage.saleProductList.get(position); 
                    
                    final EditText remarkEditText = (EditText) view.findViewById(R.id.remark);
                    remarkEditText.setText(sp.getRemark());
                    final TextView messageTextView = (TextView) view.findViewById(R.id.message);

                    final AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setView(view)
                            .setTitle("Remark")
                            .setPositiveButton("Confirm", null)
                            .setNegativeButton("Cancel", null)
                            .create();
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button confirmButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            confirmButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (remarkEditText.getText().toString().length() == 0) {

                                        messageTextView.setText("You must specify.");
                                        return;
                                    }
                                    String remark = remarkEditText.getText().toString();
                                    sp.setRemark(remark);
                                    
                                    txtRemark.setText(remark);

                                    alertDialog.dismiss();
                                }
                            });
                        }
                    });
                    alertDialog.show();
                }
            });
			
			txtRemark.setText(sp.getRemark());
            if(txtRemark.getText().length() == 0){
            	txtRemark.setText("Enter here");
            }

			return rowView;
		}

	}
	
	static public  boolean hasDuplicates(String productID,String tempSaleQty)
	{ 
		Log.e(productIdlist.size() + "", "ProductIdlistSize");
			if(productIdlist.size() > 0)
			{
				if(productIdlist.contains(productID))
				{
					Log.e("true","true");
					if(tempSaleQty != null)
					{
						totalDuplicateProductQty =0;
						for(SaleProduct sp : SalePage.saleProductList)
						{
							if(sp.getProductId().equals(productID))
							{
								Log.e("TestingForTempSaleQty",sp.getTempSaleQty());
								totalDuplicateProductQty += Integer.parseInt(sp.getTempSaleQty());
								Log.e(totalDuplicateProductQty+"","TotalDuplicateProductQty");
							}
						}					
					}
				
					return true;
				}
				else
				{
					productIdlist.add(productID);
				}
			}
			else
			{
				Log.e("Add","Add");
				productIdlist.add(productID);
			}
	
	        return false;
	}

}
