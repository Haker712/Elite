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
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bean.classes.CustomerInfo;
import bean.classes.PreOrderProduct;
import custom.classes.DBCalss;
import custom.classes.GetDevID;
import custom.scroll.LockableScrollView;
import drawing.canvas.DrawingCanvas;

public class AdvancePaymentPreOrder extends Activity {
	
	TextView txtAdvancePayPreOrder,txtInvoiceID,txtAdvPayDate,txtTotalAmt,txtAdvPayAmt,txtNetAmt;
	EditText etAdvPayAmt,etDeliveryDate;
	DrawingCanvas drawingCanvas;
	Button btnClear,btnConfirm,btnChooseDeliveryDate;
	CheckBox chbApprove;
	PreOrderProductAda adp;
	ListView lstProducts;
	int totalAmount =0;
	int totalDisAmount=0;
	String invoiceNo;
	LockableScrollView scrollView;
	DecimalFormat invoiceFormat = new DecimalFormat("0000");  
	DecimalFormat commaSepFormat = new DecimalFormat("###,##0"); 	
	SimpleDateFormat fmtForInvoiceTodayStr = new SimpleDateFormat("yyMMdd");
	SimpleDateFormat fmtForTodayStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd"); 	
	boolean hasSignFinish = false;	
	boolean isNullAdvPaymentAmt=false;
	String advPayAmount="",netAmount="";
	int mYear, mMonth, mDay;
	int i = 0;
	String res;
	String imgBase64Str;
	Bitmap myBitmap;
	String signImg;
	String expDeliveryDate="";
	
	String saleManIDPrefs = null;
	String saleManNamePrefs = null;
	String saleManPwdPrefs = null;	
	String locationCodePrefs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advance_payment_preorder);
		
		SharedPreferences prefs = getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE); 
		saleManIDPrefs = prefs.getString(MainActivity.saleManIdPrefs, "No name defined");
		saleManNamePrefs = prefs.getString(MainActivity.saleManNamePrefs,"No name defined" );
		saleManPwdPrefs = prefs.getString(MainActivity.passwordPrefs, "No name defined");	
		locationCodePrefs = prefs.getString(MainActivity.locationCodePrefs, "No name defined");	
		
		hideScreenKeyboardInPageDisplay();
		registerIDs();
		makeInvoiceNo();
		setAdapter();
		sumTotalValueAndCalculateVolumeDiscount();
		catchEvents();
	}
	public void onBackPressed()
	{
		startActivity(new Intent(AdvancePaymentPreOrder.this, PreOrder.class));
		finish();

		super.onBackPressed();
	}
	private void hideScreenKeyboardInPageDisplay() 
	{
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	private void sumTotalValueAndCalculateVolumeDiscount()
	{
		for(int i=0;i<PreOrder.preOrderProductList.size();i++)
		{
			if(PreOrder.preOrderProductList.get(i).getDiscountType().equals("V"))
			{
				totalDisAmount += Integer.parseInt(PreOrder.preOrderProductList.get(i).getTotalAmt());
			}
			else
			{
				totalDisAmount =0;
			}
			totalAmount += Integer.parseInt(PreOrder.preOrderProductList.get(i).getTotalAmt()); 
		}
		txtTotalAmt.setText(commaSepFormat.format(totalAmount));
		txtNetAmt.setText(commaSepFormat.format(totalAmount));
		HomePage.calculateVolumeDiscount(totalDisAmount,totalAmount);
	}

	private void registerIDs()
	{
		txtAdvancePayPreOrder = (TextView)findViewById(R.id.advpaymentpreorder_txtAdvancePaymentPreOrder);
		txtInvoiceID = (TextView)findViewById(R.id.advpaymentpreorder_txtInvNO);
		txtAdvPayDate = (TextView)findViewById(R.id.advpaymentpreorder_txtDate);
		txtTotalAmt = (TextView)findViewById(R.id.advpaymentpreorder_txtTotalAmountNo);
		txtAdvPayAmt = (TextView)findViewById(R.id.advpaymentpreorder_txtAdvPayAmtNo);
		txtNetAmt = (TextView)findViewById(R.id.advpaymentpreorder_txtNetAmtNo);
		etAdvPayAmt = (EditText)findViewById(R.id.advpaymentpreorder_editTextPayAmtNo);
		drawingCanvas = (DrawingCanvas)findViewById(R.id.advpaymentpreorder_drawingCanvas);
		btnClear =(Button)findViewById(R.id.advpaymentpreorder_btnClear);
		btnConfirm = (Button)findViewById(R.id.advpaymentpreorder_btnConfirm);
		chbApprove = (CheckBox)findViewById(R.id.advpaymentpreorder_chkApprove);
		scrollView = (LockableScrollView)findViewById(R.id.advpaymentpreorder_scrollView);
		lstProducts = (ListView) findViewById(R.id.advpaymentpage_lstProduct);
		btnChooseDeliveryDate=(Button) findViewById(R.id.btnDeliveryDate);
		etDeliveryDate =(EditText) findViewById(R.id.edDeliveryDate);
		
		txtAdvPayAmt.setText("0");
	}
	private void setAdapter()
	{
		adp=new PreOrderProductAda(this);
		lstProducts.setAdapter(adp);
	}
	
	public class PreOrderProductAda extends ArrayAdapter<PreOrderProduct>
	{
		private final Activity context;


		public PreOrderProductAda(Activity context)
		{
			super(context, R.layout.preorder_custom_list_row2, PreOrder.preOrderProductList);
			this.context = context; 		
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.preorder_custom_list_row2, null, true);

			TextView txtProductName = (TextView) rowView.findViewById(R.id.preOrderListRow2_txtProductName);
			 TextView txtOrderedQty = (TextView) rowView.findViewById(R.id.preOrderListRow2_txtDeliveryQty);			
			 TextView txtPrice = (TextView) rowView.findViewById(R.id.preOrderListRow2_txtPrice);
			 TextView txtTotalAmt = (TextView) rowView.findViewById(R.id.preOrderListRow2_txtTotalAmt);
			 Log.e("ALLLLSize",PreOrder.preOrderProductList.size()+"");
			PreOrderProduct pop= PreOrder.preOrderProductList.get(position);
			Log.e(pop.getProductName(), "productName");
			txtProductName.setText(pop.getProductName());
			txtPrice.setText(commaSepFormat.format(Double.parseDouble(pop.getSellingPrice())));
			txtOrderedQty.setText(pop.getTempOrderQty());
			txtTotalAmt.setText(commaSepFormat.format(Integer.parseInt(pop.getTotalAmt())));
			
			return rowView;
		}
	}

	private void catchEvents()
	{
		btnChooseDeliveryDate.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				final Calendar c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);


				DatePickerDialog dpd = new DatePickerDialog(AdvancePaymentPreOrder.this,
						new DatePickerDialog.OnDateSetListener() 
				{
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {


						etDeliveryDate.setText(dayOfMonth + "/"
								+ (monthOfYear + 1) + "/" + year);
						Log.e("Length>>>>>", etDeliveryDate.length() + "");
					 expDeliveryDate = (year + "-" + (monthOfYear +1) + "-" + dayOfMonth );
					 
					 Log.e("ExpDeliveryDate", expDeliveryDate)	;
					}
				}, mYear, mMonth, mDay);
				dpd.show();
			

			}
		});
		btnClear.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				drawingCanvas.startNew();		
			}
		});
		
		etAdvPayAmt.addTextChangedListener(new TextWatcher() 
		{
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
			{
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3)
			{
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) 
			{
				if(etAdvPayAmt.getText().toString().equals(""))
				{
					txtAdvPayAmt.setText("0");
				}
				else
				{
					txtAdvPayAmt.setText(commaSepFormat.format(Integer.parseInt(etAdvPayAmt.getText().toString())));
				}
				int advPayamt=0;
				if(!txtAdvPayAmt.getText().toString().trim().equals(""))
				{
					advPayamt = Integer.parseInt(txtAdvPayAmt.getText().toString().trim().replaceAll(",",""));
					int netAmount=0;
					if( advPayamt > totalAmount)
					{
						final AlertDialog diag3= new AlertDialog.Builder(AdvancePaymentPreOrder.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\n"+ "Advance Pay Amt must not be more than Total Amt" +"\n")		
						.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface diag2, int arg1) 
							{
								etAdvPayAmt.setText("");
							}							
						})							
						.create();

						diag3.show();
					}
					else
					{
						netAmount = totalAmount - advPayamt;
						txtNetAmt.setText(commaSepFormat.format(netAmount)+"");
					}
				}
			}
		});
		
		btnConfirm.setOnClickListener(new OnClickListener() 
		{
				@Override
				public void onClick(View v) 
				{
					final String errorField = checkValidation();

					if(!errorField.equals(""))
					{
						 if(errorField.contains("Please Choose Delivery Date"))
						{
							final AlertDialog diag3= new AlertDialog.Builder(AdvancePaymentPreOrder.this)
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
						 else if(errorField.equals("Advance Pay Amount"))
						{
							final AlertDialog diag3= new AlertDialog.Builder(AdvancePaymentPreOrder.this)
							.setTitle("Information")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage("\n Advance Pay Amount Is Empty \n")		
							.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface diag2, int arg1) 
								{
									isNullAdvPaymentAmt = true;
								}
							})	
							.create();

							diag3.show();
							diag3.setCancelable(false);
							diag3.setCanceledOnTouchOutside(false);
						}
						 else if(errorField.contains("Advance Pay Amt must not be more than Total Amt"))
						{
							final AlertDialog diag3= new AlertDialog.Builder(AdvancePaymentPreOrder.this)
							.setTitle("Information")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage("\n"+ "Advance Pay Amt must not be more than Total Amt" +"\n")		
							.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface diag2, int arg1) 
								{
									etAdvPayAmt.setText("");
								}							
							})							
							.create();

							diag3.show();
							diag3.setCancelable(false);
							diag3.setCanceledOnTouchOutside(false);

						}
						 else if(errorField.contains("Checkbox"))
						{
							final AlertDialog diag3= new AlertDialog.Builder(AdvancePaymentPreOrder.this)
							.setTitle("Information")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage("\n"+ "Please Click On Checkbox To Approve All Data Are Correct." +"\n")		
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
						 else if(errorField.contains("Your Sign"))
						 {
							 final AlertDialog diag3= new AlertDialog.Builder(AdvancePaymentPreOrder.this)
								.setTitle("Information")
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setMessage("\n"+ errorField +" is empty.\n")		
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
//						else
//						{
//							final AlertDialog diag3= new AlertDialog.Builder(AdvancePaymentPreOrder.this)
//							.setTitle("Information")
//							.setIcon(android.R.drawable.ic_dialog_alert)
//							.setMessage("\n" + errorField +" is empyt.\n")		
//							.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
//							{
//								@Override
//								public void onClick(DialogInterface diag2, int arg1) 
//								{
//
//								}
//							})					
//							.create();
//
//							diag3.show();
//							diag3.setCancelable(false);
//							diag3.setCanceledOnTouchOutside(false);
//						}
					}
					else
					{
						saveDataToPhone();
					}
				}

				private String checkValidation()
				{
					res="";				
					netAmount= txtNetAmt.getText().toString().trim().replaceAll(",","");
					String totalAmt= txtTotalAmt.getText().toString().trim().replaceAll(",","");
					int intTotalAmt=0;

					if(!hasSignFinish)
					{
						return "Your Sign";
					}
					
					else if(etDeliveryDate.getText().toString().equals(""))
					{
						Log.e("DeliveryDate","DeliveryDate");
						return "Please Choose Delivery Date";
					}
					
					else if(!chbApprove.isChecked())
					{
						Log.e("CheckBox", "CheckBox");
						return "Checkbox";
					}
					else if(etAdvPayAmt.getText().length() == 0)
					{
						if(isNullAdvPaymentAmt == true)
						{
							return "";
						}
						else
						{
							return "Advance Pay Amount";
						}
					}
					
					else if(etAdvPayAmt.getText().length() > 0)
					{
						if(!totalAmt.equals(""))
						{
							intTotalAmt= Integer.parseInt(totalAmt);
						}
						if(Integer.parseInt(etAdvPayAmt.getText().toString().trim()) > intTotalAmt)
						{
							return "Advance Pay Amt must not be more than Total Amt";
						}
					}
					return "";
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
	}
	
	 private void saveImageIntoGallery(Bitmap bitmap)
	{
		File sdCard = Environment.getExternalStorageDirectory();
		File directory = new File (sdCard.getAbsolutePath() + "/ScreenShot/PreOrder");
		directory.mkdirs();

		String filename = invoiceNo + ".jpg"; 
		File yourFile = new File(directory, filename);

		while (yourFile.exists()) {
			i++;   
			filename = "screenshot" + invoiceNo + ".jpg"; 
			yourFile = new File(directory, filename);
		} 

		String image=yourFile.toString();
		Log.e(image, "ImageOOO");


		FileOutputStream fos;
		try {
			fos = new FileOutputStream(yourFile, true);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			Toast.makeText(AdvancePaymentPreOrder.this, "Image saved to /sdcard/ScreenShot/screenshot/PreOrder" + invoiceNo + ".jpg", Toast.LENGTH_SHORT).show();
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
		bitmapOrg1.compress(Bitmap.CompressFormat.JPEG, 90, bao1);
		Log.e("here1", "here1");
		byte [] ba1 = bao1.toByteArray();

		imgBase64Str=Base64.encodeToString(ba1, Base64.NO_WRAP);
		Log.e("ImageBase64String",imgBase64Str.toString()+ "aa");
	}
	 
		private void saveDataToPhone() 
		{
			View v1 = getWindow().getDecorView().getRootView();
			v1.setDrawingCacheEnabled(true);
			myBitmap = v1.getDrawingCache();
			Log.e(myBitmap+"", "OutPutBitmap");
			saveImageIntoGallery(myBitmap);
			savePreOrderDataToDB();
		}					
	 
	
	private void savePreOrderDataToDB() 
	{
		Calendar todayCal = Calendar.getInstance();
		String todayDate = fmtForTodayStr.format(todayCal.getTime());	
		int totalAmtForInvoice = 0;
		
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();		

		for(PreOrderProduct pop : PreOrder.preOrderProductList)
		{
			ContentValues cv = new ContentValues();			

			cv.put("InvoiceID", invoiceNo);
			cv.put("ProductID", pop.getProductId());	
			cv.put("ProductName", pop.getProductName());
			cv.put("OrderQty", pop.getTempOrderQty());
			cv.put("Price", pop.getSellingPrice());		
			cv.put("TotalAmt", pop.getTotalAmt());		

			DBCalss.eliteDB.insert("PreOrderDetail", null, cv);	

			if(pop.getTotalAmt() != null)
			{
				totalAmtForInvoice += Integer.parseInt(pop.getTotalAmt());
			}
		}	
		
		advPayAmount =txtAdvPayAmt.getText().toString().trim().replaceAll(",","");
		netAmount= txtNetAmt.getText().toString().trim().replaceAll(",","");
		
		ContentValues cv = new ContentValues();			

		cv.put("InvoiceID", invoiceNo);
		cv.put("CustomerID", CustomerInfo.customer_ID);	
		cv.put("PreOrderDate", todayDate);
		cv.put("TotalAmt", totalAmtForInvoice);
		cv.put("AdvancePaymentAmt",advPayAmount.trim());
		cv.put("NetAmt",netAmount.trim());
		cv.put("InvoiceImg",imgBase64Str);
		cv.put("SignImg", saveSignIntoGallery());
		cv.put("salePersonID",saleManIDPrefs);
		cv.put("DeliveryDate", expDeliveryDate);
		cv.put("locationCode", locationCodePrefs);	
		cv.put("devID", GetDevID.getDevId(AdvancePaymentPreOrder.this));
		
		Log.e("ExpDeliveryDate1", expDeliveryDate);

		DBCalss.eliteDB.insert("PreOrder", null, cv);	

		DBCalss.eliteDB.setTransactionSuccessful();	
		DBCalss.eliteDB.endTransaction();	

		Toast.makeText(getApplicationContext(), "Saving Success ...", Toast.LENGTH_LONG).show();

		PreOrder.preOrderProductList.clear();

		startActivity(new Intent(AdvancePaymentPreOrder.this,HomePage.class));
		finish();
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
		File file = new File(imgSaved, "/"+ invoiceNo +".png");
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
		bitmapOrg1.compress(Bitmap.CompressFormat.JPEG, 90, bao1);
		Log.e("here1", "here1");
		byte [] ba1 = bao1.toByteArray();
		//	imgBase64Str=Base64.encodeToString(ba1, Base64.URL_SAFE);
		signImg=Base64.encodeToString(ba1, Base64.NO_WRAP);
		Log.e("ImageString",signImg.toString()+ "aa");

		return signImg;
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
			cur = DBCalss.eliteDB.query("PreOrder", selCol, null, null, null, null, null);

			while(cur.moveToNext())
			{	
				count = cur.getInt(cur.getColumnIndex("COUNT(*)"));
			}

			cur.close();
			DBCalss.eliteDB.setTransactionSuccessful();
			DBCalss.eliteDB.endTransaction();
			Calendar todayCal = Calendar.getInstance();
			String todayDate = fmtForInvoiceTodayStr.format(todayCal.getTime());
			txtAdvPayDate.setText(fmtForDueStr.format(todayCal.getTime()));
			invoiceNo = "SO"+saleManIDPrefs+ todayDate + invoiceFormat.format(count + 1);
			Log.e("New Invoice No",invoiceNo);
			txtInvoiceID.setText(invoiceNo);		
			Log.e(txtInvoiceID.getText().toString().trim(),"New Invoice");

		}

}
