package com.example.elite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import bean.classes.CustomerInfo;
import bean.classes.InvoiceInfo;
import custom.classes.DBCalss;
import custom.scroll.LockableScrollView;
import drawing.canvas.DrawingCanvas;

public class CreditCollection extends Activity {

	TextView txtCreditCollection,txtPersonName,txtInvoiceNo,txtInvoiceDate,txtPrice,txtInvoiceAmt;
	ListView lstInvoice;
	TextView txtTotalAmt,txtTotalPaidAmt,txtTotalUnPaidAmt,txtTotalRemainAmt;
	TextView txtDeliveryDate,txtCreditTerms;
	EditText etPayerName;
	InvoiceAdapter lstInvoiceAda;
	DrawingCanvas drawingCanvas; 
	Button btnViewInvoice,btnClear,btnConfirm,btnPrint;
	CheckBox chbApprove;
	LockableScrollView scrollView;
	static String chooseInvoiceNo;
	static String chooseProductRecPersonName;
	public static ArrayList<InvoiceInfo> invoiceInfoList = new ArrayList<InvoiceInfo>();
	public ArrayList<CreditCollectionTemp> arrcrdTemp=new ArrayList<CreditCollectionTemp>();
	int totalAmount=0;
	int paidAmount=0;
	int remainingAmount=0;
	String paidcustomerID=null ;
	String imgBase64Str;
	String paidAmtforEachVoucher= "0";
	String check = null;
	String isPaidOrNot= "";
	int i=1;
	boolean hasSignFinish = false;
	Bitmap myBitmap;
	SimpleDateFormat fmtForInvoiceTodayStr = new SimpleDateFormat("yyMMdd");
	SimpleDateFormat fmtForTodayStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.creditcollectionpage);		
		hideScreenKeyboardInPageDisplay();
		registerIDs();
		Log.e(CustomerInfo.customer_ID, "SelectCustomerID");
		paidcustomerID =CustomerInfo.customer_ID;
		getDataFromInvoiceByID(CustomerInfo.customer_ID);
		setAdapters();
		catchEvents();
	}

	private void hideScreenKeyboardInPageDisplay() 
	{
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	private void  getDataFromInvoiceByID(String customerID)
	{
		String cusID = customerID.trim();	
		cusID= cusID.toLowerCase();
		Log.e("CUS ID", cusID);
		Calendar todayCal = Calendar.getInstance();
		String dueDate = null;
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();				
		String sql = "SELECT invoiceNo,invoiceDate,netAmt,advancePay,customerID,totalAmt,productReceiptPersonName  FROM  CreditInvoiceList WHERE trim(customerID) LIKE '"+cusID+"'";
		Cursor cur = DBCalss.eliteDB.rawQuery(sql,null);
		invoiceInfoList.clear();		

		while(cur.moveToNext())
		{	
			InvoiceInfo invoice = new InvoiceInfo();
			invoice.setInvoiceNo(cur.getString(cur.getColumnIndex("invoiceNo")));
			invoice.setInvoiceDate(cur.getString(cur.getColumnIndex("invoiceDate")));
			invoice.setNetAmt(cur.getString(cur.getColumnIndex("netAmt")));
			invoice.setAdvancePay(cur.getString(cur.getColumnIndex("advancePay")));
			invoice.setTotalAmt(cur.getString(cur.getColumnIndex("totalAmt")));
			invoice.setProductReceiptPersonName(cur.getString(cur.getColumnIndex("productReceiptPersonName")));
			
			try 
			{
				Date invoiceDate = fmtForDueStr.parse(invoice.getInvoiceDate());
				todayCal.setTime(invoiceDate);
				
				todayCal.add(todayCal.DATE,CustomerInfo.creditTerm);
				dueDate= fmtForDueStr.format(todayCal.getTime());
				Log.e("InvoiceDate",invoice.getInvoiceDate());
				Log.e("CreditTerm", CustomerInfo.creditTerm+"");
				Log.e("result date", fmtForDueStr.format(todayCal.getTime()));
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			invoice.setDueDate(dueDate);
			Log.e(invoice.getPayAmount(), "PaidAmount");
			invoiceInfoList.add(invoice);			
		}
		Log.e("check len",invoiceInfoList.size()+"");
		for(int i=0;i<invoiceInfoList.size();i++)
		{
			Log.e("Name list",invoiceInfoList.get(i).getInvoiceNo());
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();	
		DBCalss.eliteDB.endTransaction(); 	

	}
	
	private void setAdapters()
	{
		lstInvoiceAda = new InvoiceAdapter(this);
		lstInvoice.setAdapter(lstInvoiceAda);
	}

	@Override
	public void onBackPressed() 
	{
		startActivity(new Intent(CreditCollection.this,Payment.class));
		super.onBackPressed();
	}

	private void catchEvents() 
	{
		Calendar now = Calendar.getInstance();
		String today = now.get(Calendar.DATE) + "/" + (now.get(Calendar.MONTH) + 1)
				+ "/" + now.get(Calendar.YEAR);
		
		txtDeliveryDate.setText(today);
		lstInvoice.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
					long arg3) 
			{
				chooseInvoiceNo= invoiceInfoList.get(pos).getInvoiceNo();
				Log.e(chooseInvoiceNo, "ChooseInvoiceNo");
				Intent i= new Intent(CreditCollection.this, CreditCollectionTwo.class);
				startActivityForResult(i, 1111);
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

		btnConfirm.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				if(check == null)
				{
					final AlertDialog diag3= new AlertDialog.Builder(CreditCollection.this)
					.setTitle("Information")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage("\nPlease do Paid\\Unpaid Process\n")		
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
					String errorField = checkValidation();
					if(!errorField.equals(""))
					{
						if(errorField.contains("Payer Name"))
						{
							final AlertDialog diag3= new AlertDialog.Builder(CreditCollection.this)
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
							final AlertDialog diag3= new AlertDialog.Builder(CreditCollection.this)
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
							final AlertDialog diag3= new AlertDialog.Builder(CreditCollection.this)
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
						Log.e(myBitmap+"","OutPutBitmap");
						saveImageIntoGallery(myBitmap);
						savePaidInvoiceDataToDB();
						Toast.makeText(getApplicationContext(), "Successfully Saved To DB", 1).show();
						startActivity(new Intent(CreditCollection.this,HomePage.class));
					}  
				}
				
			}
			private String checkValidation() 
			{
				if(etPayerName.getText().length() == 0)
				{
					return "Payer Name is Empty Now";
				}
				else if(!hasSignFinish)
				{
					return "Your Sign is Empty Now!!";
				}
				else if(!chbApprove.isChecked())
				{
					return "Please Click On Checkbox To Approve All Data Are Correct.";
				}
				Log.e("Here", "hrere");
				return "";
			}
		});
	}

	public class InvoiceAdapter extends ArrayAdapter<InvoiceInfo>
	{
		private final Activity context;


		public InvoiceAdapter(Activity context)
		{
			super(context, R.layout.creditcollection_custom_listrow, invoiceInfoList);
			this.context = context; 		
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.creditcollection_custom_listrow, null, true);

			TextView txtInvoiceNo = (TextView) rowView.findViewById(R.id.invoiceCustomList_txtInvoiceNo);
			TextView txtInvoiceDate = (TextView) rowView.findViewById(R.id.invoiceCustomList_txtInvoiceDate);
			TextView txtInvoiceAmount = (TextView) rowView.findViewById(R.id.invoiceCustomList_txtTotalAmount);
			TextView txtDueDate=(TextView) rowView.findViewById(R.id.invoiceCustomList_txtDueDate);
			final TextView txtPaidAmt=(TextView) rowView.findViewById(R.id.invoiceCustomList_txtPaidAmt);
			final Button btnPay=(Button) rowView.findViewById(R.id.invoiceCustomerList_btnPay);
			final InvoiceInfo sp = invoiceInfoList.get(position);  
			txtInvoiceNo.setText(sp.getInvoiceNo());
			txtInvoiceDate.setText(sp.getInvoiceDate());
			Log.e(sp.getNetAmt(),"NetAmount1");
			txtInvoiceAmount.setText(sp.getNetAmt());
			txtDueDate.setText(sp.getDueDate());
			Log.e("Pay Amount", sp.getPayAmount()+"");
			if(sp.getPayAmount() != null)
			{
				int payAmt=0;
				int netAmt=0;
				payAmt = Integer.parseInt(sp.getPayAmount().toString().trim());
				netAmt = Integer.parseInt(sp.getNetAmt().toString().trim());
				Log.e(payAmt +"","PayAmtofInvoice");
				Log.e(netAmt + "" , "NetAmtofInvoice");
				if(payAmt < netAmt)
				{
					btnPay.setText("PartialPaid");
					btnPay.setTextColor(Color.WHITE);
					btnPay.setBackgroundColor(Color.YELLOW);
					btnPay.setEnabled(true);
				}
				else if ( payAmt == netAmt)
				{
					btnPay.setText("Paid");
					btnPay.setBackgroundColor(Color.RED);
					btnPay.setTextColor(Color.WHITE);
					btnPay.setEnabled(false);
				}
			
				paidAmount = Integer.parseInt(sp.getPayAmount());
			}
			chooseProductRecPersonName=sp.getProductReceiptPersonName();
			Log.e(sp.getNetAmt(),"NetAmount");
			if(sp.getNetAmt() != null)
			{
				totalAmount += Integer.parseInt(sp.getNetAmt());
			}
			btnPay.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					final Activity context;
					final int selPos = lstInvoice.getPositionForView((View)v.getParent());
					showCustomDialog(invoiceInfoList.get(selPos).getInvoiceNo(),invoiceInfoList.get(selPos).getNetAmt());
				}
				
				private void showCustomDialog(String invoiceNo,final String netAmt)
				{
					AlertDialog.Builder builder;
					LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View layout = inflater.inflate(R.layout.dynamiclayout_creditcollection, null);

					builder = new AlertDialog.Builder(CreditCollection.this);
					builder.setTitle("Payment");
					builder.setView(layout);
					
					final TextView txtInvoiceNo = (TextView) layout.findViewById(R.id.txtInvoiceNo);
					final TextView txtInvoiceAmount = (TextView) layout.findViewById(R.id.txtAmt);
					
					final EditText etPayAmt = (EditText) layout.findViewById(R.id.etPayAmt);
					final RadioGroup rdoGroup = (RadioGroup) layout.findViewById(R.id.paymenttype_rdogroup);
					
					txtInvoiceNo.setText(invoiceNo);
					txtInvoiceAmount.setText(netAmt);
					
					rdoGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
					{
						
						@Override
						public void onCheckedChanged(RadioGroup group, int arg1) 
						{
							if(group.getCheckedRadioButtonId() == R.id.rdo_Cash)
							{	
								Log.e("Check1", "Check1");
								etPayAmt.setText(netAmt);
								isPaidOrNot = "true";
							}
							if(group.getCheckedRadioButtonId() == R.id.rdo_Custom)
							{
								Log.e("Check2", "Check2");
								etPayAmt.setText("");
								isPaidOrNot = "false";
							}
							
						}
					});
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
					{
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) 
						{
							paidAmtforEachVoucher = etPayAmt.getText().toString().trim();
							CreditCollectionTemp crdTemp=new CreditCollectionTemp();
							Log.e(isPaidOrNot, "SSSS");
							if(isPaidOrNot.equals("true")) 
							{
								txtPaidAmt.setText(paidAmtforEachVoucher);
								btnPay.setText("Paid");
								btnPay.setBackgroundColor(Color.RED);
								btnPay.setEnabled(false);
								Log.e(invoiceInfoList.size()+"", "ReinsertInvoiceInfoList");
							
								crdTemp.customerID=CustomerInfo.customer_ID;
								crdTemp.paidAmt = paidAmtforEachVoucher;
								crdTemp.paymentStatus = "P";
								crdTemp.invoiceNo= txtInvoiceNo.getText().toString().trim();
								crdTemp.invoiceAmount = txtInvoiceAmount.getText().toString().trim();
								crdTemp.isSelected = true;
								
								check = "clicked";

								arrcrdTemp.add(crdTemp);
								Log.e(arrcrdTemp.size()+"", "ARRSIZE");

								for(CreditCollectionTemp crdTemp1 : arrcrdTemp)
								{
									if(crdTemp1.isSelected == true)
									{
										paidAmount += Integer.parseInt(crdTemp1.paidAmt);
										crdTemp1.isSelected = false;
										calculateTotals();
									}
								}
								Log.e(paidAmount+"","PayAmount222");
							}
							else if(isPaidOrNot.equals("false"))
							{
								txtPaidAmt.setText(paidAmtforEachVoucher);
								btnPay.setText("PartialPaid");
								btnPay.setBackgroundColor(Color.YELLOW);
								btnPay.setEnabled(false);
								Log.e(invoiceInfoList.size()+"", "ReinsertInvoiceInfoList");
								crdTemp.customerID=CustomerInfo.customer_ID;
								crdTemp.paidAmt = paidAmtforEachVoucher;
								crdTemp.paymentStatus = "C";
								crdTemp.invoiceNo= txtInvoiceNo.getText().toString().trim();
								crdTemp.invoiceAmount = txtInvoiceAmount.getText().toString().trim();
								crdTemp.isSelected = true;
								
								check = "clicked";

								arrcrdTemp.add(crdTemp);
								Log.e(arrcrdTemp.size()+"", "ARRSIZE");

								for(CreditCollectionTemp crdTemp1 : arrcrdTemp)
								{
									if(crdTemp1.isSelected == true)
									{
										paidAmount += Integer.parseInt(crdTemp1.paidAmt);
										crdTemp1.isSelected = false;
										calculateTotals();
									}
								}
								Log.e(paidAmount+"","PayAmount222");
							}
						
						}
					
					});
					
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1)
						{
							
						}
					});
					
				builder.show();

				
				}
			});

			calculateTotals();
			return rowView;

		}
	}

	public void savePaidInvoiceDataToDB()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();

		for(CreditCollectionTemp ifoTemp : arrcrdTemp)
		{
			String args[] = {ifoTemp.customerID,ifoTemp.invoiceNo};

			ContentValues cv = new ContentValues();	
			Log.e(ifoTemp.customerID, "Customerid111");
			Log.e(ifoTemp.invoiceNo, "Save Invoice No");
			Log.e(ifoTemp.invoiceAmount,"paidAmount111");
			Log.e(ifoTemp.paymentStatus,"Paid STATUS");
			cv.put("paidAmt", ifoTemp.invoiceAmount);
			cv.put("paidstatus", ifoTemp.paymentStatus);
			// for tmp CreditCollection ID
			cv.put("creditCollInvoiceID", "");

			DBCalss.eliteDB.update("CreditInvoiceList", cv,"customerID LIKE ? AND invoiceNo LIKE ?", args);
		}
		
		Log.e("paidCustomerID",paidcustomerID);
		String customerID="";
		String args[] = {paidcustomerID};
		String selcol[]= {"customerID"};
		
		Cursor cur=DBCalss.eliteDB.query("PaidCreditCollection",selcol, "customerID LIKE ?", args,null,null,null,null);

		while(cur.moveToNext())
		{	
			 customerID = cur.getString(cur.getColumnIndex("customerID"));
		}
		cur.close();
		Log.e("customerIDfromDB",customerID);
		ContentValues cv = new ContentValues();
		cv.put("payerName", etPayerName.getText().toString());
		cv.put("payerSignImg" , imgBase64Str);
		cv.put("customerID" , paidcustomerID);
		
		if(!customerID.equals(""))
		{
			Log.e("update","update");
			DBCalss.eliteDB.update("PaidCreditCollection", cv, "customerID LIKE ?", args);
		}
		else
		{
			Log.e("insert", "insert");
			DBCalss.eliteDB.insert("PaidCreditCollection", null , cv);
		}
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	private void saveImageIntoGallery(Bitmap bitmap)
	{
		 File sdCard = Environment.getExternalStorageDirectory();
		   File directory = new File (sdCard.getAbsolutePath() + "/ScreenShot/CreditCollection");
		   directory.mkdirs();
	    
	       String filename = "screenshotCreditCollection" + i + ".jpg"; 
	       File yourFile = new File(directory, filename);
	       
	       while (yourFile.exists()) { 
	    	   i++;
	    	filename = "screenshotCreditCollection" + i + ".jpg"; 
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
			 Toast.makeText(CreditCollection.this, "Image saved to /sdcard/ScreenShot/screenshot" + paidcustomerID + ".jpg", Toast.LENGTH_SHORT).show();
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

	private void calculateTotals()
	{
		Log.e(paidAmount +"", "Payamount");
		remainingAmount = totalAmount - paidAmount ;
		txtTotalAmt.setText(totalAmount+"");
		txtTotalPaidAmt.setText(paidAmount+"");
		txtTotalRemainAmt.setText(remainingAmount +"");
	}

	private void registerIDs()
	{
		txtCreditCollection = (TextView)findViewById(R.id.creditcollection_txtCreditCollection);
		txtPrice = (TextView)findViewById(R.id.creditcollection_txtPrice);			
		lstInvoice = (ListView)findViewById(R.id.creditcollection_lstProduct);		
		txtTotalAmt = (TextView)findViewById(R.id.creditcollection_txtTotalAmtNo);
		txtTotalPaidAmt = (TextView)findViewById(R.id.creditcollection_txtTotalPaidAmtNo);
		txtTotalRemainAmt = (TextView)findViewById(R.id.creditcollection_txtTotalRemAmtNo);
		txtDeliveryDate = (TextView)findViewById(R.id.creditcollection_txtDeliveryDateNo);
		etPayerName = (EditText)findViewById(R.id.creditcollection_txtPayerName);
		drawingCanvas = (DrawingCanvas)findViewById(R.id.creditcollection_drawingCanvas);
		btnClear = (Button)findViewById(R.id.creditcollection_btnClear);
		btnConfirm = (Button)findViewById(R.id.creditcollection_btnConfirm);
	
		chbApprove = (CheckBox)findViewById(R.id.creditcollection_chkApprove);			
		scrollView = (LockableScrollView)findViewById(R.id.creditcollection_scrollView);

		txtTotalAmt.setText("0");
		txtTotalPaidAmt.setText("0");
		txtTotalRemainAmt.setText("0");
	}
	
//	private void makeInvoiceNo() 
//	{
//		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
//		{
//			DBCalss.openDB();
//		}
//		
//		DBCalss.eliteDB.beginTransaction();	
//		Cursor cur;		
//		int count = 0;
//
//		String[] selCol = {"COUNT(*)"};
//		cur = DBCalss.eliteDB.query("SaleData",selCol, null, null, null, null, null);
//
//		while(cur.moveToNext())
//		{	
//			count = cur.getInt(cur.getColumnIndex("COUNT(*)"));
//		}
//
//		cur.close();
//		DBCalss.eliteDB.setTransactionSuccessful();
//		DBCalss.eliteDB.endTransaction();
//		Calendar todayCal = Calendar.getInstance();
//		String todayDate = fmtForInvoiceTodayStr.format(todayCal.getTime());
//		txtSaleDate.setText("Sale Date : "+fmtForDueStr.format(todayCal.getTime()));
//		invoiceNo = LoginSaleManInfo.saleManID.trim() + todayDate + invoiceFormat.format(count + 1);
//		txtInvoiceNo.setText(invoiceNo);				
//		todayCal.add(todayCal.DATE, CustomerInfo.creditTerm);
//		String due_date=fmtForDueStr.format(todayCal.getTime());		
//		txtDueDate.setText(due_date);
//
//	}


}
