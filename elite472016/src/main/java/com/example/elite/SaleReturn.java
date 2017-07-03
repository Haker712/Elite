package com.example.elite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bean.classes.CustomerInfo;
import bean.classes.SaleReturnProduct;

import com.andraskindler.quickscroll.QuickScroll;
import com.quick.scroll.files.ContactAdapter;

import custom.classes.DBCalss;

public class SaleReturn extends ListActivity {
	Button btnHome, btnFromDate, btnToDate, btnShow, btnNext;
	TextView txtSaleReturn;
	ListView lstCustomer, saleReturnList;
	private AlertDialog alertDialog;
	EditText et_fromDate, et_toDate;
	AutoCompleteTextView customer_autotxtSearch;
	int mYear, mMonth, mDay;

	public static ArrayList<CustomerInfo> customerList = new ArrayList<CustomerInfo>();
	public static ArrayList<CustomerInfo> customerInfoList = new ArrayList<CustomerInfo>();
	public ArrayList<SaleReturnProduct> saleretrunProList = new ArrayList<SaleReturnProduct>();
	public static ArrayList<SaleReturnProduct> tmpList =  new ArrayList<SaleReturnProduct>();
	
	public static ArrayList<String> searchCustomer_ShowList = new ArrayList<String>();

	ArrayAdapter searchAdapter;
	String today, twoweeksago;
	SaleReturnAdp sradp;
	ContactAdapter cAdp;
	int dateDiff;
	public Date startDate, finishDate;
	public static int selectedItem = -1;
	public static String myCusID = "";

	public static String myToday, myendDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sale_return_page);
		hideScreenKeyboardInPageDisplay();
		registerIDs();
		GetCustomerDataFromDB();
		setDataAll();
		setShowDataForCustomerList();
		setAdapter();
		makeQuickScroll();
		catchEvents();
	}

	private void setDataAll() {
		// TODO Auto-generated method stub
		customerInfoList.clear();
		customerInfoList.addAll(customerList);
	}

	private void setShowDataForCustomerList() {
		// TODO Auto-generated method stub
		searchCustomer_ShowList.clear();
		boolean isInsertedflag;

		for (int i = 0; i < customerList.size(); i++) {
			isInsertedflag = false;

			for (String insertedItem : searchCustomer_ShowList) {
				if (customerList.get(i).getCustomerName()
						.equalsIgnoreCase(insertedItem)) {
					isInsertedflag = true;
					break;
				}
			}

			if (!isInsertedflag) {
				searchCustomer_ShowList.add(customerList.get(i)
						.getCustomerName());
			}
		}
		searchAdapter = new ArrayAdapter<String>(this,
				R.layout.layout_for_autocompletext, searchCustomer_ShowList);
		customer_autotxtSearch.setAdapter(searchAdapter);

	}

	private void GetCustomerDataFromDB() {
		// TODO Auto-generated method stub
		if (DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen()) {
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();
		String sql = "SELECT customerID,UPPER(customerName) as customerName ,customerTypeID,customerTypeName,Address,ph,township,creditTerm,creditLimit,creditAmt,dueAmt,prepaidAmt,paymentType,isInRoute  FROM  Customer ORDER BY customerName";
		Cursor cur = DBCalss.eliteDB.rawQuery(sql, null);
		Log.e("Cursor count in Customer", cur.getCount() + "");
		customerList.clear();

		while (cur.moveToNext()) {
			CustomerInfo customer = new CustomerInfo();
			customer.setCustomerID(cur.getString(cur
					.getColumnIndex("customerID")));
			customer.setCustomerName(cur.getString(cur
					.getColumnIndex("customerName")));
			customer.setCustomerTypeID(cur.getString(cur
					.getColumnIndex("customerTypeID")));
			customer.setCustomerTypeName(cur.getString(cur
					.getColumnIndex("customerTypeName")));
			customer.setCustomerAddress(cur.getString(cur
					.getColumnIndex("Address")));
			customer.setCustomerPhone(cur.getString(cur.getColumnIndex("ph")));
			customer.setCustomerTownShip(cur.getString(cur
					.getColumnIndex("township")));
			customer.setCustomerCreditTerm(cur.getString(cur
					.getColumnIndex("creditTerm")));
			customer.setCustomerCreditLimit(cur.getString(cur
					.getColumnIndex("creditLimit")));
			customer.setCustomerCreditAmt(cur.getString(cur
					.getColumnIndex("creditAmt")));
			customer.setCustomerDueAmt(cur.getString(cur
					.getColumnIndex("dueAmt")));
			customer.setCustomerPrepaidAmt(cur.getString(cur
					.getColumnIndex("prepaidAmt")));
			customer.setCustomerPaymentType(cur.getString(cur
					.getColumnIndex("paymentType")));
			customer.setCustomerIsInRoute(cur.getString(cur
					.getColumnIndex("isInRoute")));

			customerList.add(customer);
		}

		Log.e("Customer List", customerList.size() + "");

		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();

	}

	private void getDataFromProuductbyID(String customer_ID) {
		String cusID = customer_ID.trim();
		cusID = cusID.toLowerCase();

		Log.e("CustomerID to retrieve", "SQLite" + cusID);

		if (DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen()) {
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Log.e("Start Date", sdf.format(startDate).toString());
		Log.e("End Date", sdf.format(finishDate).toString());

		String sql = "SELECT customerID,productID,productName,returnQty,deliverQty,deliveryDate  FROM  SaleReturnProduct WHERE trim(customerID) LIKE '"+ cusID+ "' AND deliveryDate BETWEEN '"+ sdf.format(finishDate)+ "' AND '"+ sdf.format(startDate)+ "'";

		Cursor cur = DBCalss.eliteDB.rawQuery(sql, null);

		saleretrunProList.clear();

		while (cur.moveToNext()) {
			SaleReturnProduct srp = new SaleReturnProduct();
			srp.setCustomerID(cur.getString(cur.getColumnIndex("customerID")));
			srp.setProductID(cur.getString(cur.getColumnIndex("productID")));
			srp.setProductName(cur.getString(cur.getColumnIndex("productName")));
			srp.setReturnQty(cur.getString(cur.getColumnIndex("returnQty")));		
			srp.setDeliverQty(cur.getString(cur.getColumnIndex("deliverQty")));
			srp.setDeliverDate(cur.getString(cur.getColumnIndex("deliveryDate")));

			saleretrunProList.add(srp);

		}

		Log.e("SaleReturnList", saleretrunProList.size() + "");

		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();

		sradp.notifyDataSetChanged();
	}

	private void setAdapter() {
		sradp = new SaleReturnAdp(this);
		saleReturnList.setAdapter(sradp);
		
		searchAdapter = new ArrayAdapter<String>(this,
				R.layout.layout_for_autocompletext, searchCustomer_ShowList);
	}

	private void makeQuickScroll() {
		cAdp = new ContactAdapter(this, customerInfoList);
		setListAdapter(cAdp);

		final ListView list = (ListView) lstCustomer
				.findViewById(android.R.id.list);

		final QuickScroll quickscroll = (QuickScroll) findViewById(R.id.quickscroll);
		quickscroll.init(QuickScroll.TYPE_INDICATOR_WITH_HANDLE, list, cAdp,
				QuickScroll.STYLE_HOLO);
		quickscroll.setFixedSize(1);
		quickscroll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
	}

	private void hideScreenKeyboardInPageDisplay() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	private void catchEvents() {

		Calendar now = Calendar.getInstance();
		today = now.get(Calendar.DATE) + "/" + (now.get(Calendar.MONTH) + 1)
				+ "/" + now.get(Calendar.YEAR);
		et_toDate.setText(today);
		now.add(Calendar.DATE, -14);
		twoweeksago = now.get(Calendar.DATE) + "/"
				+ (now.get(Calendar.MONTH) + 1) + "/" + now.get(Calendar.YEAR);
		et_fromDate.setText(twoweeksago);

		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				if(saleretrunProList.size() == 0)
				{
					final AlertDialog diag3 = new AlertDialog.Builder(
							SaleReturn.this)
					.setTitle("Alert!")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(
							"\nPlease Select Customer and Sale Return Product.\n")
							.setPositiveButton(
									"OK",
									new android.content.DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface diag2,
												int arg1) {

										}
									}).create();

					diag3.show();
					diag3.setCancelable(false);
					diag3.setCanceledOnTouchOutside(false);
				}
				else
				{
					startActivity(new Intent(SaleReturn.this, SaleReturn2.class));
					finish();
				}

			}
		});

		lstCustomer.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				selectedItem = position;
				Log.e("Pos is >>", position + "");
				Log.e("SElectItem", selectedItem + "");
				myCusID = customerInfoList.get(position).getCustomerID();
				Log.e("Selected Cus ID", myCusID);

				if (customerInfoList.get(position).getCustomerID().trim()
						.equals("")) {
					btnShow.setVisibility(View.GONE);
				}
				saleretrunProList.clear();
				cAdp.notifyDataSetChanged();
			}

		});

		btnShow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				//Clear Tmp List not to duplicate double time to next Activity
				tmpList.clear();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

				try {
					myToday = et_toDate.getText().toString();
					myendDate = et_fromDate.getText().toString();
					startDate = dateFormat.parse(myToday);
					finishDate = dateFormat.parse(myendDate);

					dateDiff = (int) ((startDate.getTime() - finishDate
							.getTime()) / (1000 * 60 * 60 * 24));
					Log.e("Date Diff", dateDiff + "days");
					System.out.println(dateDiff);
					if (dateDiff > 14) {
						Log.e("Condition", "One");
						Toast.makeText(
								SaleReturn.this,
								"You Choose Over 2 weeks,Please choose date between 2 weeks..",
								Toast.LENGTH_LONG).show();
					}
					if (dateDiff <= 14) {
						Log.e("Condition", "Two");

						if (myCusID.equalsIgnoreCase("")) {
							final AlertDialog diag3 = new AlertDialog.Builder(
									SaleReturn.this)
							.setTitle("Alert!")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage(
									"\nPlease Select One Customer.\n")
									.setPositiveButton(
											"OK",
											new android.content.DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface diag2,
														int arg1) {

												}
											}).create();

							diag3.show();
							diag3.setCancelable(false);
							diag3.setCanceledOnTouchOutside(false);
						} else {
							getDataFromProuductbyID(myCusID);
						}

					}

				} catch (ParseException e) {

					e.printStackTrace();
				}

			}

		});

		btnFromDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final Calendar c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);


				DatePickerDialog dpd = new DatePickerDialog(SaleReturn.this,
						new DatePickerDialog.OnDateSetListener() 
				{
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {


						et_fromDate.setText(dayOfMonth + "/"
								+ (monthOfYear + 1) + "/" + year);
						Log.e("Length>>>>>", et_fromDate.length() + "");

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
				DatePickerDialog toDate = new DatePickerDialog(SaleReturn.this,
						new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						et_toDate.setText(dayOfMonth + "/"
								+ (monthOfYear + 1) + "/" + year);
					}
				}, mYear, mMonth, mDay);
				toDate.show();
			}
		});
		
		
		customer_autotxtSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				customer_autotxtSearch.setText("");
				setDataAll();
				
				makeQuickScroll();

			}
		});

		customer_autotxtSearch
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub

						String selItemName = customer_autotxtSearch.getText()
								.toString();
						Log.e("check Name", selItemName);
						GetSearchCustomerDataFromDB(selItemName);
						makeQuickScroll();
					}

				});
	}
	
	private void GetSearchCustomerDataFromDB(String cName)
	{		
		String Name = cName.trim();	
		Name= Name.toLowerCase();
		Log.e("check name",Name);
		
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}
		
		DBCalss.eliteDB.beginTransaction();				
		String sql = "SELECT customerID,UPPER(customerName) as Name ,customerTypeID,customerTypeName,Address,ph,township,creditTerm,creditLimit,creditAmt,dueAmt,prepaidAmt,paymentType,isInRoute  FROM  Customer WHERE trim(customerName) LIKE '"+Name+"' ORDER BY customerName";
		Cursor cur = DBCalss.eliteDB.rawQuery(sql,null);
		customerInfoList.clear();		
       
		while(cur.moveToNext())
		{	
			CustomerInfo  customer = new  CustomerInfo();
			customer.setCustomerID(cur.getString(cur.getColumnIndex("customerID")));
			customer.setCustomerName(cur.getString(cur.getColumnIndex("Name")));
			customer.setCustomerTypeID(cur.getString(cur.getColumnIndex("customerTypeID")));
			customer.setCustomerTypeName(cur.getString(cur.getColumnIndex("customerTypeName")));
			customer.setCustomerAddress(cur.getString(cur.getColumnIndex("Address")));
			customer.setCustomerPhone(cur.getString(cur.getColumnIndex("ph")));
			customer.setCustomerTownShip(cur.getString(cur.getColumnIndex("township")));
			customer.setCustomerCreditTerm(cur.getString(cur.getColumnIndex("creditTerm")));
			customer.setCustomerCreditLimit(cur.getString(cur.getColumnIndex("creditLimit")));
			customer.setCustomerCreditAmt(cur.getString(cur.getColumnIndex("creditAmt")));
			customer.setCustomerDueAmt(cur.getString(cur.getColumnIndex("dueAmt")));
			customer.setCustomerPrepaidAmt(cur.getString(cur.getColumnIndex("prepaidAmt")));
			customer.setCustomerPaymentType(cur.getString(cur.getColumnIndex("paymentType")));
			customer.setCustomerIsInRoute(cur.getString(cur.getColumnIndex("isInRoute")));			
			
			customerInfoList.add(customer);			
		}
        Log.e("check len",customerInfoList.size()+"");
		for(int i=0;i<customerInfoList.size();i++)
		{
			
			Log.e("Name list",customerInfoList.get(i).getCustomerName());
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();	
		DBCalss.eliteDB.endTransaction();
	}

	public void onBackPressed() {
		startActivity(new Intent(SaleReturn.this, HomePage.class));
		finish();

		super.onBackPressed();
	}

	private void registerIDs() {
		btnHome = (Button) findViewById(R.id.saleReturn_btnHome);
		txtSaleReturn = (TextView) findViewById(R.id.salereturn_txtSalereturn);
		btnFromDate = (Button) findViewById(R.id.saleReturn_btnFromDate);
		btnToDate = (Button) findViewById(R.id.saleReturn_btnToDate);
		btnShow = (Button) findViewById(R.id.saleReturn_btnShow);
		lstCustomer = (ListView) findViewById(android.R.id.list);
		saleReturnList = (ListView) findViewById(R.id.saleReturn_List);
		et_fromDate = (EditText) findViewById(R.id.saleReturn_et_FromDate);
		et_toDate = (EditText) findViewById(R.id.saleReturn_et_ToDate);
		customer_autotxtSearch = (AutoCompleteTextView) findViewById(R.id.saleReturn_autotxtSearch);

		btnNext = (Button) findViewById(R.id.saleReturn_btnGoToNext);
	}

	public class SaleReturnAdp extends ArrayAdapter<SaleReturnProduct> {

		private final Activity context;

		public SaleReturnAdp(Activity context) {

			super(context, R.layout.sale_return_custom_list_row,
					saleretrunProList);
			this.context = context;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {

			LayoutInflater inflater = context.getLayoutInflater();
			View rowView = inflater.inflate(
					R.layout.sale_return_custom_list_row, null, true);

			TextView txtProductName = (TextView) rowView
					.findViewById(R.id.saleReturnCustomList_txtProductName);
			TextView txtDeliverQty = (TextView) rowView
					.findViewById(R.id.saleReturnCustomList_txtDeliverQty);

			final TextView txtReturnQty = (TextView) rowView
					.findViewById(R.id.saleReturnCustomList_txtReturnQty);
			final TextView txtReturnDeliveryDate = (TextView) rowView
					.findViewById(R.id.saleReturnCustomList_txtReturnDeliveryDate);
			final Button btnDeliver = (Button) rowView
					.findViewById(R.id.salereturnCustomList_btnDeliver);

			final SaleReturnProduct srp = saleretrunProList.get(position);

			txtProductName.setText(srp.getProductName());
			txtDeliverQty.setText(srp.getDeliverQty());
			Log.e("Test","ReturnQty");
			txtReturnQty.setText(srp.getReturnQty());
			
			int delQty = Integer.parseInt(srp.getDeliverQty());
			int retQty = 0;
			//Log.e("RetQty", srp.getReturnQty()+"TTT");
			if(srp.getReturnQty() == null)
			{
				retQty = 0;
			}
			else
			{
				retQty = Integer.parseInt(srp.getReturnQty());
			}
			
			int diff = delQty - retQty;
			if(diff == 0)
			{
				btnDeliver.setBackgroundColor(Color.parseColor("#FE7402"));
				btnDeliver.setText("All Delivered");
				btnDeliver.setEnabled(false);
			}

		
			if (btnDeliver != null) {
				if (saleretrunProList.get(position) == null) {
					btnDeliver.setVisibility(View.GONE);
				} else {
					btnDeliver.setVisibility(View.VISIBLE);
					btnDeliver.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							showCustomDialog();
						}

						private void showCustomDialog() {
							AlertDialog.Builder builder;

							// ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

							LayoutInflater inflater = (LayoutInflater) context
									.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							View layout = inflater.inflate(
									R.layout.sale_return_custom_dialog, null);

							builder = new AlertDialog.Builder(SaleReturn.this);
							builder.setTitle("Sale Return");
							builder.setView(layout);

							TextView txtProductName = (TextView) layout
									.findViewById(R.id.customDialog_txtProductName);
							TextView txtDeliverQty = (TextView) layout
									.findViewById(R.id.customDialog_txtDeliverQty);
							final EditText txtDialogReturnQty = (EditText) layout
									.findViewById(R.id.customDialog_txtReturnQty);
							final EditText txtDate = (EditText) layout
									.findViewById(R.id.customDialog_txtReturnDate);
							Button btnSetDate = (Button) layout
									.findViewById(R.id.customDialog_btnSetDate);

							final SaleReturnProduct sRP = saleretrunProList
									.get(position);

							txtProductName.setText(sRP.getProductName());
							txtDeliverQty.setText(sRP.getDeliverQty());

							btnSetDate
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {

									final Calendar c = Calendar
											.getInstance();
									mYear = c.get(Calendar.YEAR);
									mMonth = c.get(Calendar.MONTH);
									mDay = c.get(Calendar.DAY_OF_MONTH);
									DatePickerDialog dialogDate = new DatePickerDialog(
											SaleReturn.this,
											new DatePickerDialog.OnDateSetListener() {

												@Override
												public void onDateSet(
														DatePicker view,
														int year,
														int monthOfYear,
														int dayOfMonth) {
													txtDate.setText(dayOfMonth
															+ "/"
															+ (monthOfYear + 1)
															+ "/"
															+ year);
												}
											}, mYear, mMonth, mDay);
									dialogDate.show();

								}
							});

							builder.setPositiveButton("OK",new DialogInterface.OnClickListener() 
							{	@Override
								public void onClick(DialogInterface arg0, int arg1)
								{
									int returnQty =  0;
									if(txtDialogReturnQty.getText().toString().trim().equals("") && txtDate.getText().toString().trim().equals(""))
									{
										final AlertDialog diag4 = new AlertDialog.Builder(SaleReturn.this)
										.setTitle("Alert!")
										.setIcon(
												android.R.drawable.ic_dialog_alert)
												.setMessage(
														"\nYour ReturnQty and Return Delivery Date are empty now!\n")
														.setPositiveButton(
																"OK",
																new android.content.DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(
																			DialogInterface diag2,
																			int arg1) 
																	{
																		
																	}
																}).create();

										diag4.show();
										diag4.setCancelable(false);
										diag4.setCanceledOnTouchOutside(false);
									}
									if(!txtDialogReturnQty.getText().toString().trim().equals("") && txtDate.getText().toString().trim().equals(""))
									{
										final AlertDialog diag4 = new AlertDialog.Builder(SaleReturn.this)
										.setTitle("Alert!")
										.setIcon(
												android.R.drawable.ic_dialog_alert)
												.setMessage(
														"\nPlease Select Return Delivery Date from Calendar\n")
														.setPositiveButton(
																"OK",
																new android.content.DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(
																			DialogInterface diag2,
																			int arg1) 
																	{
																		
																	}
																}).create();

										diag4.show();
										diag4.setCancelable(false);
										diag4.setCanceledOnTouchOutside(false);
									}
									if(txtDialogReturnQty.getText().toString().trim().equals("") && !txtDate.getText().toString().trim().equals(""))
									{
										final AlertDialog diag4 = new AlertDialog.Builder(SaleReturn.this)
										.setTitle("Alert!")
										.setIcon(
												android.R.drawable.ic_dialog_alert)
												.setMessage(
														"\nPlease Enter Deliver Qty\n")
														.setPositiveButton(
																"OK",
																new android.content.DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(
																			DialogInterface diag2,
																			int arg1) 
																	{
																		
																	}
																}).create();

										diag4.show();
										diag4.setCancelable(false);
										diag4.setCanceledOnTouchOutside(false);
									}
									if(!txtDialogReturnQty.getText().toString().trim().equals(""))
									{
										returnQty = Integer.parseInt(txtDialogReturnQty.getText().toString());
									}
									int deliverQty = 0;
									Log.e(saleretrunProList.get(position).getDeliverQty(), "Deliver QTY");
									if(!saleretrunProList.get(position).getDeliverQty().toString().trim().equals(""))
									{
										deliverQty = Integer.parseInt(saleretrunProList.get(position).getDeliverQty().toString().trim());
									}
									
									if (returnQty > deliverQty) 
									{
										
										final AlertDialog diag4 = new AlertDialog.Builder(SaleReturn.this)
										.setTitle("Alert!")
										.setIcon(
												android.R.drawable.ic_dialog_alert)
												.setMessage(
														"\nYour ReturnQty is over DeliverQty!!\n")
														.setPositiveButton(
																"OK",
																new android.content.DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(
																			DialogInterface diag2,
																			int arg1) 
																	{
																		
																	}
																}).create();

										diag4.show();
										diag4.setCancelable(false);
										diag4.setCanceledOnTouchOutside(false);
									} else 
									{
										int originalRetQty = 0;
										if(saleretrunProList.get(position).getReturnQty() != null)
										{
											originalRetQty = Integer.parseInt(saleretrunProList.get(position).getReturnQty());
										}
										int retQtyfromDialog = 0;
										if(!txtDialogReturnQty.getText().toString().trim().equals(""))
										{
											retQtyfromDialog = Integer.parseInt(txtDialogReturnQty.getText().toString());
										}	
										int totalretQty = originalRetQty + retQtyfromDialog;
										Log.e("Total RetQty", totalretQty+"");
										
										//for TotalReturn Quantity check over deliverQty	
										
										if(totalretQty > deliverQty)
										{
											final AlertDialog diag4 = new AlertDialog.Builder(SaleReturn.this)
											.setTitle("Alert!")
											.setIcon(
													android.R.drawable.ic_dialog_alert)
													.setMessage(
															"\nYour Total ReturnQty is over DeliverQty!!\n")
															.setPositiveButton(
																	"OK",
																	new android.content.DialogInterface.OnClickListener() {
																		@Override
																		public void onClick(
																				DialogInterface diag2,
																				int arg1) 
																		{
																			
																		}
																	}).create();

											diag4.show();
											diag4.setCancelable(false);
											diag4.setCanceledOnTouchOutside(false);
										}
										if(totalretQty <= deliverQty && !txtDialogReturnQty.getText().toString().trim().equals("") && !txtDate.getText().toString().trim().equals(""))
										{
											SaleReturnProduct srpTmp = new SaleReturnProduct();
											srpTmp.setProductID(saleretrunProList.get(position).getProductID());
											srpTmp.setCustomerID(saleretrunProList.get(position).getCustomerID());
											srpTmp.setProductName(saleretrunProList.get(position).getProductName());
											srpTmp.setDeliverQty(saleretrunProList.get(position).getDeliverQty());
											srpTmp.setReturnQty(txtDialogReturnQty.getText().toString());
											srpTmp.setReturnDeliverDate(txtDate.getText().toString());									
											tmpList.add(srpTmp);
											
											tmpList.get(position).setReturnQty(totalretQty+"");
											txtReturnQty.setText(totalretQty+"");
											txtReturnDeliveryDate.setText(txtDate.getText().toString());
											btnDeliver.setBackgroundColor(Color.RED);
											btnDeliver.setEnabled(false);
											
											
										}										
									}

								}
							});
							builder.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface arg0, int arg1) {
									alertDialog.dismiss();
								}
							});

							alertDialog = builder.create();

							alertDialog.setCancelable(false);
							alertDialog.show();

							(SaleReturn.this)
							.getWindow()
							.addFlags(
									WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

						}
					});
				}
			}

			return rowView;
		}
	}

}
