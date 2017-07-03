package com.example.elite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
import bean.classes.DeliveryProduct;
import custom.classes.DBCalss;
import custom.classes.GetDevID;
import custom.scroll.LockableScrollView;
import drawing.canvas.DrawingCanvas;

public class Delivery2 extends Activity {
	
	TextView txtDeliverPage2,txtInvoiceID,txtTotalAmt,txtAdvPay,txtVolumeDiscount,txtNetamt,txtOrderedDate,txtRefund,txtPayAmt,txtRemaingAmt;
	EditText etPayAmt,etReceiptPersonName;
	DrawingCanvas drawingCanvas;
	ListView lstDelProduct;
	Button btnClear,btnConfirm,btnBack,btnPayAmtConfirm;
	LockableScrollView scrollView;
	CheckBox chbApprove;
	DeliverProductAda lstDeliverAda;
	int totalAmount = 0;
	int totalAmtofVolumeDisItems=0;
	boolean hasSignFinish = false;	
	DecimalFormat invoiceFormat = new DecimalFormat("00");  
	DecimalFormat commaSepFormat = new DecimalFormat("###,##0"); 	
	SimpleDateFormat fmtForInvoiceTodayStr = new SimpleDateFormat("yyMMdd");
	SimpleDateFormat fmtForTodayStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");
	String cDisAmount="",cNetAmount="";
	int totalItemDisAndVolDisAmt=0;
	int payAmt=0;
	int finalRemainingAmt=0;
	int netAmount=0;
	Bitmap myBitmap;
	String signImg;
	String newInvoiceNo;
	int i=0;
	String imgBase64Str;
	String res;
	
	String saleManIDPrefs = null;
	String saleManNamePrefs = null;
	String saleManPwdPrefs = null;	
	String locationCodePrefs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delivery_page2);
		
		SharedPreferences prefs = getSharedPreferences("SaleManPrefs", MODE_PRIVATE); 
		saleManIDPrefs = prefs.getString(MainActivity.saleManIdPrefs, "No name defined");
		saleManNamePrefs = prefs.getString(MainActivity.saleManNamePrefs,"No name defined" );
		saleManPwdPrefs = prefs.getString(MainActivity.passwordPrefs, "No name defined");	
		locationCodePrefs = prefs.getString(MainActivity.locationCodePrefs, "No name defined");	
		
		hideScreenKeyboardInPageDisplay();		
		registerIDs();	
		makeInvoiceNo();
		sumTotalValueAndCalculateVolumeDis();
		setAdapters();
		catchEvent();
	}

	public void onBackPressed()
	{
		Intent intent = getIntent();	
		setResult(Activity.RESULT_OK, intent);
		
		super.onBackPressed();
	}
	
	private void hideScreenKeyboardInPageDisplay()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);		
	}
	
	private void sumTotalValueAndCalculateVolumeDis()
	{
		int advancePay = 0;
		
		for(int i=0;i < Delivery.tmpProList.size();i++)
		{
			if(Delivery.tmpProList.get(i).getDiscountType() == null || Delivery.tmpProList.get(i).getDiscountType().equals(""))
			{
				totalAmtofVolumeDisItems =0;
			}
			else
			{
				if(Delivery.tmpProList.get(i).getDiscountType().equals("V"))
				{
					totalAmtofVolumeDisItems += Integer.parseInt(Delivery.tmpProList.get(i).getTotalAmt());
				}
				else
				{
					totalAmtofVolumeDisItems =0;
				}
			}
			
				totalAmount += Integer.parseInt(Delivery.tmpProList.get(i).getTotalAmt()); 
		}
		
		HomePage.calculateVolumeDiscount(totalAmtofVolumeDisItems,totalAmount);
	
		if(Delivery.deliverList.get(0).getAdvancePay().equals(""))
		{
			txtAdvPay.setText("0");
		}
		else
		{
			advancePay=Integer.parseInt(Delivery.deliverList.get(0).getAdvancePay());
			txtAdvPay.setText(commaSepFormat.format(advancePay));
		}
		
		netAmount= totalAmount - HomePage.volumeDiscount;
		
		txtVolumeDiscount.setText(commaSepFormat.format(HomePage.volumeDiscount));
		totalItemDisAndVolDisAmt = HomePage.volumeDiscount + Delivery.totalitemDiscounts;
		Log.e(totalItemDisAndVolDisAmt+"", "TotalItemDiscountAndVolumeDis");
		txtNetamt.setText(commaSepFormat.format(netAmount));
		txtTotalAmt.setText(commaSepFormat.format(totalAmount));
		calculatRemainingAmt();
	}
	
	private void calculatRemainingAmt()
	{
		Log.e(Delivery.remainingAmtFromServer+"","RemainingAmtFromServer");
		Log.e(payAmt+"","PayAmount");
		finalRemainingAmt = Delivery.remainingAmtFromServer - ( totalItemDisAndVolDisAmt + payAmt);
		txtRemaingAmt.setText(commaSepFormat.format(finalRemainingAmt)+"");
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
		cur = DBCalss.eliteDB.query("DeliveryReturnData", selCol, null, null, null, null, null);

		while(cur.moveToNext())
		{	
			count = cur.getInt(cur.getColumnIndex("COUNT(*)"));
		}

		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
		Calendar todayCal = Calendar.getInstance();
		String todayDate = fmtForInvoiceTodayStr.format(todayCal.getTime());
		newInvoiceNo = "OS"+saleManIDPrefs + todayDate + invoiceFormat.format(count + 1);
		txtInvoiceID.setText(newInvoiceNo);				
	}
	
	private void catchEvent()
	{	
		btnPayAmtConfirm.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				int refund =0;
				if(!etPayAmt.getText().toString().trim().equals(""))
				{
					payAmt =Integer.parseInt(etPayAmt.getText().toString().trim());
					calculatRemainingAmt();
					Log.e(finalRemainingAmt+"","FinalRemainingAmt");
					if(payAmt > finalRemainingAmt)
					{
						refund = payAmt - finalRemainingAmt;
					}
				}
				else
				{
					payAmt= 0;
					calculatRemainingAmt();
				}
				txtPayAmt.setText(commaSepFormat.format(payAmt));
				txtRefund.setText(commaSepFormat.format(refund));
			}
		});
		
		txtOrderedDate.setText(Delivery.txtOrderDate.getText().toString());
		btnClear.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				drawingCanvas.startNew();		
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
		
		btnBack.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				Intent intent = getIntent();	
		    	setResult(Activity.RESULT_OK, intent);
		    	Delivery2.this.finish();
			}
		});
		
		btnConfirm.setOnClickListener(new OnClickListener() 
		{		
			@Override
			public void onClick(View v) 
			{
				String errorField = checkValidation();

				if(!errorField.equals(""))
				{
					if(errorField.contains("No Credit"))
					{
						final AlertDialog diag3= new AlertDialog.Builder(Delivery2.this)
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
						final AlertDialog diag3= new AlertDialog.Builder(Delivery2.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\n"+ "You are not credit customer.Must be paid by cash." +"\n")		
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

					else if(errorField.contains("Please Click On Checkbox To Approve All Data Are Correct."))
					{
						final AlertDialog diag3= new AlertDialog.Builder(Delivery2.this)
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
					else
					{
						final AlertDialog diag3= new AlertDialog.Builder(Delivery2.this)
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
					saveDataToPhone();
				}
			}

			private void saveDataToPhone() 
			{
				View v1 = getWindow().getDecorView().getRootView();
				v1.setDrawingCacheEnabled(true);
				myBitmap = v1.getDrawingCache();
				Log.e(myBitmap+"", "OutPutBitmap");
				saveImageIntoGallery(myBitmap);
				saveSaleData();
			}							

			private String checkValidation()
			{			
				cNetAmount= txtNetamt.getText().toString().trim().replaceAll(",","");
				
				if(!hasSignFinish)
				{
					return "Your Sign";
				}

				else if(etReceiptPersonName.getText().length() == 0)
				{
					return "Receipt Person Name";
				}
				
				else if(!chbApprove.isChecked())
				{
					return "Please Click On Checkbox To Approve All Data Are Correct.";
				}
				
				else if(payAmt == 0 || payAmt < netAmount)
				{
					if(CustomerInfo.creditLimit == 0)
					{
						return "No Credit";
					}
					else if(CustomerInfo.creditLimit > 0)
					{
						int remainingAmt = 0;
						remainingAmt = netAmount - payAmt ;
						if(remainingAmt > CustomerInfo.creditAmount)
						{
							 return "Insufficient Credit";
						}
					}
				}
				
				else if(etPayAmt.getText().length() == 0)
				{
					return "Pay Amount";
				}
				
				return "";
			}
		});
	}
	
	private void saveSaleData() 
	{
		Calendar todayCal = Calendar.getInstance();
		String todayDate = fmtForTodayStr.format(todayCal.getTime());	
		String currentTime = fmtForTodayStr.format(todayCal.getTime());
		int totalAmtForInvoice = 0;


		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();
	
		for(DeliveryProduct dp : Delivery.tmpProList)
		{
			ContentValues cv = new ContentValues();			
			
			Log.e(dp.getProductID(), "InsertProductID");
			Log.e(dp.getProductName(),"InsertProductName");
			Log.e(dp.getOrderQty(),"InsertOrderQty");
			Log.e(dp.getPrice(),"InsertPrice");
			Log.e(dp.getItemDiscount(),"InsertItemDiscount");
			Log.e(dp.getTotalAmt(),"InsertTotalAmt");
			cv.put("SaleOrderNo",Delivery.deliverList.get(0).getInvoiceID());
			cv.put("DeliveryInvoiceID",txtInvoiceID.getText().toString().trim());
			cv.put("productID", dp.getProductID());	
			cv.put("productName", dp.getProductName());
			cv.put("deliveredQty", dp.getDeliverQty());
			cv.put("salePrice", dp.getPrice());	
			cv.put("discountAmt", dp.getItemDiscount());
			cv.put("totalAmt", dp.getTotalAmt());			

			DBCalss.eliteDB.insert("DeliveryReturnDataDetail", null, cv);	

			
			String[] args={Delivery.deliverList.get(0).getInvoiceID(),dp.getProductID()};
			
			ContentValues cvUpd=new ContentValues();
			cvUpd.put("deliverQty", dp.getDeliverQty());
			cvUpd.put("returnDeliverQty", dp.getReturnedDeliverQty());
			cvUpd.put("remainingQty",dp.getRemainingQty());
			DBCalss.eliteDB.update("DeliveryProduct", cvUpd, "invoiceID LIKE ? AND productID LIKE ?",args);
			
			String[] arg2= { Delivery.deliverList.get(0).getInvoiceID()};
			
			ContentValues cvUpd2=new ContentValues();
			cvUpd2.put("remainingAmt", txtRemaingAmt.getText().toString().trim().replaceAll(",", ""));
			DBCalss.eliteDB.update("Delivery", cvUpd2, "saleOrderNo LIKE ? ",arg2);
			
			if(dp.getTotalAmt() != null)
			{
				totalAmtForInvoice += Integer.parseInt(dp.getTotalAmt());
			}
		}	

		cDisAmount= txtVolumeDiscount.getText().toString().trim().replaceAll(",","");
		cNetAmount= txtNetamt.getText().toString().trim().replaceAll(",","");

		ContentValues cv1 = new ContentValues();			
		Log.e(txtInvoiceID.getText().toString().trim(), "InsertInvoiceID");
		Log.e(CustomerInfo.customer_ID ,"InsertCustomerID");
		Log.e(todayDate,"InsertTodayDate");
		Log.e(totalAmtForInvoice+"" ,"InserttotalAmt");
		Log.e(cDisAmount.trim(),"InsertDiscountAmt");
		Log.e(etPayAmt.getText().toString(),"InsertPayAmt");
		Log.e(imgBase64Str, "InsertInvoiceImg");
		Log.e(saveSignIntoGallery(), "SignImg");
		cv1.put("DeliveryInvoiceID", txtInvoiceID.getText().toString().trim());
		cv1.put("SaleOrderNo", Delivery.deliverList.get(0).getInvoiceID());
		cv1.put("customerID", CustomerInfo.customer_ID);	
		cv1.put("deliveredDate", todayDate);
		cv1.put("totalAmt", totalAmtForInvoice);
		cv1.put("discountAmt",totalItemDisAndVolDisAmt);

		cv1.put("payAmt", etPayAmt.getText().toString().trim());
		cv1.put("receitpPersonName",etReceiptPersonName.getText().toString().trim());	
		cv1.put("InvoiceImg",imgBase64Str);
		cv1.put("signImg", saveSignIntoGallery());
		Log.e(saveSignIntoGallery(),"SaveSignImage");
		cv1.put("salePersonID",saleManIDPrefs);
		
		if(etPayAmt.getText().toString().length() == 0)
		{
			cv1.put("cashOrCredit","R");
		}
		else if(Integer.parseInt(etPayAmt.getText().toString()) < Integer.parseInt(cNetAmount.toString()))
		{
			cv1.put("cashOrCredit","R");
		}

		else if(Integer.parseInt(etPayAmt.getText().toString())==Integer.parseInt(cNetAmount.toString()))
		{
			cv1.put("cashOrCredit","C");
		}
		else
		{
			cv1.put("cashOrCredit","C");
		}		
		cv1.put("locationCode", locationCodePrefs);	
		cv1.put("devID", GetDevID.getDevId(Delivery2.this));
		cv1.put("invoiceTime", currentTime);
		cv1.put("dueDate", todayDate);

		DBCalss.eliteDB.insert("DeliveryReturnData", null, cv1);	
		
		DBCalss.eliteDB.setTransactionSuccessful();	
		DBCalss.eliteDB.endTransaction();	

		Toast.makeText(getApplicationContext(), "Saving Success ...", Toast.LENGTH_LONG).show();

		Delivery.deliverList.clear();

		startActivity(new Intent(Delivery2.this,HomePage.class));
		finish();
	}
	
	private void setAdapters() 
	{
		Log.e("D2 Size",Delivery.tmpProList.size()+"");
		lstDeliverAda = new DeliverProductAda(this);
		lstDelProduct.setAdapter(lstDeliverAda);
	}
	public class DeliverProductAda extends ArrayAdapter<DeliveryProduct>
	{
		private final Activity context;

		public DeliverProductAda(Activity context) 
		{
			super(context, R.layout.delivery_list_row_two,Delivery.tmpProList);
			this.context = context;
			Log.e("A", "AA");
		}
	
		@Override
		public View getView(final int position, View view, ViewGroup parent) 
		{		
			Log.e("POSITION",Delivery.tmpProList.get(position).getProductName());
			Log.e("In", "GetView");
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.delivery_list_row_two, null, true);

			TextView txtProductName = (TextView) rowView
					.findViewById(R.id.delivery2_txtProductName);
			final TextView txtDeliverQty = (TextView) rowView
					.findViewById(R.id.delivery2_txtDeliverQty);
			TextView txtReturnDeliverQty = (TextView)rowView.findViewById(R.id.delivery2_txtReturnDeliverQty);
			TextView txtTotalAmt = (TextView)rowView.findViewById(R.id.delivery2_txtTotalAmt);
			
			DeliveryProduct dp = Delivery.tmpProList.get(position);

			txtProductName.setText(dp.getProductName());
			txtDeliverQty.setText(dp.getDeliverQty());
			txtReturnDeliverQty.setText(dp.getReturnedDeliverQty());
			txtTotalAmt.setText(dp.getTotalAmt());
			return rowView;
		}
	}

	private void registerIDs()
	{
		txtRefund =(TextView) findViewById(R.id.deliveryPage2_txtRefund);
		txtDeliverPage2 = (TextView)findViewById(R.id.deliveryPage2_txtDelivery);
		txtInvoiceID = (TextView)findViewById(R.id.deliveryPage2_txtInvoiceID1);
		txtTotalAmt = (TextView)findViewById(R.id.deliveryPage2_txtTotalAmt);
		txtAdvPay = (TextView)findViewById(R.id.deliveryPage2_txtAdvPay);
		txtVolumeDiscount = (TextView)findViewById(R.id.deliveryPage2_txtVolumeDiscount);
		txtNetamt = (TextView)findViewById(R.id.deliveryPage2_txtNetAmt);
		etPayAmt = (EditText)findViewById(R.id.deliveryPage2_etPayAmt);
		etReceiptPersonName = (EditText)findViewById(R.id.deliveryPage2_etReceiptName);
		drawingCanvas = (DrawingCanvas)findViewById(R.id.deliveryPage2_drawingCanvas);
		btnBack =(Button) findViewById(R.id.deliveryPage2_btnBack);
		btnClear = (Button)findViewById(R.id.deliveryPage2_btnClear);
		btnConfirm = (Button)findViewById(R.id.deliveryPage2_btnConfirm);
		chbApprove = (CheckBox)findViewById(R.id.deliveryPage2_chkApprove);
		txtOrderedDate =(TextView) findViewById(R.id.deliverypage2_txtOrderedDate);
		lstDelProduct = (ListView)findViewById(R.id.salePage_lstProduct);
		scrollView = (LockableScrollView)findViewById(R.id.deliveryPage2_scrollView);	
		btnPayAmtConfirm =(Button) findViewById(R.id.deliveryPage2_btnPayAmtConfirm);
		txtRemaingAmt = (TextView) findViewById(R.id.deliveryPage2_txtRemainingAmt);
		txtPayAmt =(TextView) findViewById(R.id.deliveryPage2_txtPayAmt);
	}
	
	 private void saveImageIntoGallery(Bitmap bitmap)
		{
			File sdCard = Environment.getExternalStorageDirectory();
			File directory = new File (sdCard.getAbsolutePath() + "/ScreenShot/Delivery");
			directory.mkdirs();

			String filename =  txtInvoiceID.getText().toString().trim() + ".jpg"; 
			File yourFile = new File(directory, filename);

			while (yourFile.exists()) 
			{  
				i++; 
				filename =  txtInvoiceID.getText().toString().trim()+ i  + ".jpg"; 
				yourFile = new File(directory,filename);
			} 

			String image=yourFile.toString();
			Log.e(image, "ImageOOO");


			FileOutputStream fos;
			try {
				fos = new FileOutputStream(yourFile, true);
				bitmap.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
				Toast.makeText(Delivery2.this, "Image saved to /sdcard/ScreenShot/screenshot" + txtInvoiceID.getText().toString().trim() + ".jpg", Toast.LENGTH_SHORT).show();
				
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
			String imgSaved = Environment.getExternalStorageDirectory()
					.toString();
			// OutputStream fOut = null;
			File file = new File(imgSaved, "/"+ txtInvoiceID.getText().toString().trim() +".png");
			String image= file.toString();
			try {
				FileOutputStream fos = new FileOutputStream(file);

				bm.compress(Bitmap.CompressFormat.PNG, 50, fos);
				Log.e("Log6", "Log6");
				fos.flush();
				fos.close();
				bm.recycle();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(imgSaved!=null)
			{
				Log.e("Notnul","Notnull");
				Toast savedToast = Toast.makeText(getApplicationContext(), 
						"Drawing saved to Gallery!", Toast.LENGTH_SHORT);
				savedToast.show();
			}
			else{
				Toast unsavedToast = Toast.makeText(getApplicationContext(), 
						"Oops! Image could not be saved.", Toast.LENGTH_SHORT);
				unsavedToast.show();
			}
			Log.e(imgSaved, "Image");
			Log.e(image,"ImageSS");
			Bitmap bitmapOrg1= BitmapFactory.decodeFile(image);
			ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
			Log.e(bitmapOrg1.toString()+"","BitMap1");
			Log.e(bao1.toString(),"BAO");
			bitmapOrg1.compress(Bitmap.CompressFormat.JPEG, 90,bao1);
			Log.e("here1", "here1");
			byte [] ba1 = bao1.toByteArray();
			//	imgBase64Str=Base64.encodeToString(ba1, Base64.URL_SAFE);
			signImg=Base64.encodeToString(ba1,Base64.NO_WRAP);
			Log.e("ImageString",signImg.toString()+ "aa");

			return signImg;
		}
}
