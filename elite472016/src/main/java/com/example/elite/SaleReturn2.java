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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bean.classes.SaleReturnProduct;
import custom.classes.DBCalss;
import custom.scroll.LockableScrollView;
import drawing.canvas.DrawingCanvas;

public class SaleReturn2 extends Activity
{
	Button btnGotoCustomer,btnClear,btnDone,btnPrint,btnFromDate,btnToDate;
	TextView txtSaleReturn,txtTotalReturnQty;
	EditText et_ReturnPersonName,et_FromDate,et_ToDate;
	DrawingCanvas drawingCanvas; 
	CheckBox chbApprove;
	ListView lstProduct;
	boolean hasSignFinish = false;
	DecimalFormat commaSepFormat = new DecimalFormat("###,##0"); 	
	LockableScrollView scrollView;
	Bitmap myBitmap;
	int i = 0;
	int totalReturnQty =0;
	int updateQty = 0;
	String imgBase64Str;
	SaleReturnAdp sradp;
	SimpleDateFormat fmtForTodayStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");
	int mYear, mMonth, mDay;
	int returnQtyint=0;
	public static String myCusID = "";
	
	String saleManIDPrefs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sale_return_page_2);
		
		SharedPreferences prefs = getSharedPreferences("SaleManPrefs", MODE_PRIVATE); 
		saleManIDPrefs = prefs.getString(MainActivity.saleManIdPrefs, "No name defined");
		
		hideScreenKeyboardInPageDisplay();
		regisetIDs();
		catchEvents();
		setAdapter();
		sumTotalReturnQty();
	}
	
	private void sumTotalReturnQty() 
	{
		for(int i = 0; i < SaleReturn.tmpList.size(); i++)
		{
			if(SaleReturn.tmpList.get(i).getReturnQty() != null)
			{
				Log.e("NotNull", "NotNull");
				totalReturnQty += Integer.parseInt(SaleReturn.tmpList.get(i).getReturnQty()); 
				Log.e(totalReturnQty +"","TotalReturnQty");
			}
		}
		
		txtTotalReturnQty.setText(commaSepFormat.format(totalReturnQty));
	}

	private void setAdapter() 
	{
		sradp=new SaleReturnAdp(this);		
		lstProduct.setAdapter(sradp);
	}

	private void hideScreenKeyboardInPageDisplay() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	private void catchEvents() 
	{
		
		
		btnDone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				
				String errorField = checkValidation();
				Log.e("Check Error Field", errorField);
				if(!errorField.equals(""))
				{
					if(errorField.contains("Return Person Name"))
					{
						final AlertDialog diag3= new AlertDialog.Builder(SaleReturn2.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\n"+errorField+"\n")		
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
					if(errorField.contains("Your Sign"))
					{
						final AlertDialog diag3= new AlertDialog.Builder(SaleReturn2.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\n"+errorField+"\n")		
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
					if(errorField.contains("Please Click"))
					{
						final AlertDialog diag3= new AlertDialog.Builder(SaleReturn2.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\n"+errorField+"\n")		
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
					saveImageIntoGallery(myBitmap);
					Log.e("Before ","Save");
					saveSaleReturnData();
				}
			}

			private String checkValidation()
			{				
				 if(et_ReturnPersonName.getText().length() == 0)
				{
					return "Return Person Name is Empty Now!!";
				}
				 else if(!hasSignFinish)
				{
					return "Your Sign is Empty Now";
				}
				else if(!chbApprove.isChecked())
				{
					return "Please Click On Checkbox To Approve All Data Are Correct.";
				}
				Log.e("Here", "hrere");
				return "";				
			}
		});
		
		et_FromDate.setText(SaleReturn.myendDate);
		et_ToDate.setText(SaleReturn.myToday);
		
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
		
		
		btnFromDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Process to get Current Date
				final Calendar c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);

				// Launch Date Picker Dialog
				DatePickerDialog dpd = new DatePickerDialog(SaleReturn2.this,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {

								// Display Selected date in textbox
								et_FromDate.setText(dayOfMonth + "/"
										+ (monthOfYear + 1) + "/" + year);
								
							}
						}, mYear, mMonth, mDay);
				dpd.show();
			}
		});

		btnToDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final Calendar c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);
				DatePickerDialog toDate = new DatePickerDialog(SaleReturn2.this,
						new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								et_ToDate.setText(dayOfMonth + "/"
										+ (monthOfYear + 1) + "/" + year);
							}
						}, mYear, mMonth, mDay);
				toDate.show();
			}
		});
	
	}
	private void saveImageIntoGallery(Bitmap bitmap) 
	{
		 File sdCard = Environment.getExternalStorageDirectory();
		   File directory = new File (sdCard.getAbsolutePath() + "/ScreenShot/SaleReturn");
		   directory.mkdirs();
	      
	       String filename = "screenshot" + i + ".jpg"; 
	       File yourFile = new File(directory, filename);
	       
	       while (yourFile.exists()) {
	    	i++;   
	    	filename = "screenshot" + i + ".jpg"; 
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
			 Toast.makeText(SaleReturn2.this, "Image saved to /sdcard/ScreenShot/screenshot" + i + ".jpg", Toast.LENGTH_SHORT).show();
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
	
	private void saveSaleReturnData()
	{
		Calendar todayCal = Calendar.getInstance();
		String todayDate = fmtForTodayStr.format(todayCal.getTime());	
		
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();
		for(SaleReturnProduct srp : SaleReturn.tmpList)
		{
			Log.e(srp.getCustomerID(),"CustomerID");
			myCusID = srp.getCustomerID();
			Log.e(srp.getProductID(),"ProductID");
			Cursor cur;		
			String[] selCol = {"returnQty"};
			String[] args={srp.getCustomerID(),srp.getProductID()};
			cur = DBCalss.eliteDB.query("SaleReturnProduct", selCol, "customerID LIKE ? AND productID LIKE ?", args, null, null, null);

			while(cur.moveToNext())
			{	
				String returnQty= cur.getString(cur.getColumnIndex("returnQty"));
				Log.e(returnQty,"REturnQtyfromDB");
				if(returnQty != null )
				{
					Log.e("Notnull","Notnull");
					returnQtyint= Integer.parseInt(returnQty);
				}

				String[] args1={srp.getCustomerID(),srp.getProductID()};
						ContentValues cv1 =new ContentValues();
						cv1.put("returnQty", srp.getReturnQty());
						cv1.put("returnDeliveryDate", srp.getReturnDeliverDate());
						
						DBCalss.eliteDB.update("SaleReturnProduct", cv1, "customerID LIKE ? AND productID LIKE ?", args1);	
			}
		}
	
		ContentValues cv = new ContentValues();
		cv.put("customerID", myCusID);
		cv.put("salemanID",saleManIDPrefs);
		cv.put("saleReturnDate", todayDate);
		cv.put("totalReturnQty", txtTotalReturnQty.getText().toString().trim());
		cv.put("returnPersonName",et_ReturnPersonName.getText().toString().trim());
		cv.put("signImg",imgBase64Str);
		
		DBCalss.eliteDB.insert("SaleReturnDetail", null, cv);	

		DBCalss.eliteDB.setTransactionSuccessful();	
		DBCalss.eliteDB.endTransaction();	

		Toast.makeText(getApplicationContext(), "Saving Success ...", Toast.LENGTH_LONG).show();
		
		SaleReturn.tmpList.clear();
		
		startActivity(new Intent(SaleReturn2.this, HomePage.class));
		finish();
	}
	
	
	private void regisetIDs() {
		btnGotoCustomer = (Button)findViewById(R.id.saleReturn2_btnGotoCustomer);
		btnClear = (Button)findViewById(R.id.saleReturn2_btnClear);
		btnDone = (Button)findViewById(R.id.saleReturn2_btnDone);
		
		
		txtSaleReturn = (TextView)findViewById(R.id.saleReturn2_txtSaleReturn);
		et_FromDate = (EditText)findViewById(R.id.saleReturn2_et_FromDate);
		et_ToDate = (EditText)findViewById(R.id.saleReturn2_et_ToDate);
		txtTotalReturnQty = (TextView)findViewById(R.id.saleReturn2_txtTotalReturnQty);
		
		et_ReturnPersonName = (EditText)findViewById(R.id.saleReturn2_et_ReturnPersonName);
		
		drawingCanvas = (DrawingCanvas)findViewById(R.id.saleReturn2_drawingCanvas);
		chbApprove = (CheckBox)findViewById(R.id.saleReturn2_chkApprove);
		lstProduct = (ListView)findViewById(R.id.saleReturn2_lstProduct);
		
		scrollView = (LockableScrollView)findViewById(R.id.saleReturn2_scrollView);
		
		btnFromDate = (Button)findViewById(R.id.saleReturn2_btnFromDate);
		btnToDate = (Button)findViewById(R.id.saleReturn2_btnToDate);
	}
	
	public void onBackPressed() {
		startActivity(new Intent(SaleReturn2.this, SaleReturn.class));
		finish();

		super.onBackPressed();
	}
	
	public class SaleReturnAdp extends ArrayAdapter<SaleReturnProduct>
	{
		private final Activity context;

		public SaleReturnAdp(Activity context) 
		{
			
			super(context, R.layout.sale_return_custom_list_row, SaleReturn.tmpList);
			this.context = context;
		}
	
		@Override
		public View getView(final int position, View view, ViewGroup parent) 
		{		

			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(R.layout.sale_return_custom_list_row_page2, null, true);

			TextView txtProductName = (TextView) rowView
					.findViewById(R.id.saleReturnCustomList_txtProductName);
			TextView txtDeliverQty = (TextView) rowView
					.findViewById(R.id.saleReturnCustomList_txtDeliverQty);
			final TextView txtReturnQty = (TextView) rowView
					.findViewById(R.id.saleReturnCustomList_txtReturnQty);
			final TextView txtReturnDeliveryDate = (TextView) rowView
					.findViewById(R.id.saleReturnCustomList_txtReturnDeliveryDate);
		
			SaleReturnProduct srp = SaleReturn.tmpList.get(position);			

			txtProductName.setText(srp.getProductName());
			txtDeliverQty.setText(srp.getDeliverQty());
			txtReturnDeliveryDate.setText(srp.getReturnDeliverDate());
			txtReturnQty.setText(srp.getReturnQty());		

			return rowView;
		}
	}
}
