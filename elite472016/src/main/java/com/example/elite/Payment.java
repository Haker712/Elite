package com.example.elite;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import bean.classes.CustomerInfo;

import com.andraskindler.quickscroll.QuickScroll;
import com.quick.scroll.files.ContactAdapter;

import custom.classes.DBCalss;


public class Payment extends ListActivity {

	Button btnHome, btnCreditCollection;
	TextView txtPayment, txtCustomerName, txtRealCustomerName, txtAddress,
			txtRealAddress, txtPhone, txtRealPhone, txtTownShip,
			txtRealTownShip;
	TextView txtCreditTerms, txtRealCreditTerms, txtCreditLimits,
			txtRealCreditLimits, txtCreditAmt, txtRealCreditAmt, txtDueAmt,
			txtRealDueAmt;
	TextView txtPrepaidAmt, txtRealPrepaidAmt, txtPayType, txtRealPayType;
	ListView lstCustomer;

	ArrayAdapter searchAdapter;
	AutoCompleteTextView customer_autotxtSearch;

	SQLiteDatabase db;
	public static final String PATH = "data/data/com.example.elite/databases/";
	public static ArrayList<CustomerInfo> customerList = new ArrayList<CustomerInfo>();
	public static ArrayList<CustomerInfo> customerInfoList = new ArrayList<CustomerInfo>();
	public static ArrayList<String> searchCustomer_ShowList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paymentpage);

		hideScreenKeyboardInPageDisplay();
		registerIds();
		clearAllData();
		GetCustomerDataFromDB();
		setDataAll();
		setShowDataForCustomerList();
		setAdapter();
		makeQuickScroll();
		catchEvents();
	}

	private void setAdapter() {
		searchAdapter = new ArrayAdapter<String>(this,
				R.layout.layout_for_autocompletext, searchCustomer_ShowList);
		customer_autotxtSearch.setAdapter(searchAdapter);
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
				// tempList.add(allSaleProductList.get(i));
				searchCustomer_ShowList.add(customerList.get(i)
						.getCustomerName());
			}
		}
		Log.e("Before", "Htoo Aung Hlaing");
		searchAdapter = new ArrayAdapter<String>(this,
				R.layout.layout_for_autocompletext, searchCustomer_ShowList);
		customer_autotxtSearch.setAdapter(searchAdapter);

	}

	private void setDataAll() {
		// TODO Auto-generated method stub
		customerInfoList.clear();
		customerInfoList.addAll(customerList);
	}

	private void GetCustomerDataFromDB()
	{
		// TODO Auto-generated method stub
		if (DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen()) {
			DBCalss.openDB();
	}

		DBCalss.eliteDB.beginTransaction();
		String sql = "SELECT customerID,UPPER(customerName) as customerName,customerTypeID,customerTypeName,Address,ph,township,creditTerm,creditLimit,creditAmt,dueAmt,prepaidAmt,paymentType,isInRoute  FROM  Customer ORDER BY customerName";
		Cursor cur = DBCalss.eliteDB.rawQuery(sql, null);
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
			
		Log.e("All Customer are ", customerList.size()+"");
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
		// db.close();

	}

	private void clearAllData() 
	{
		txtRealCustomerName.setText("---");
		txtRealAddress.setText("---");
		txtRealPhone.setText("---");
		txtRealTownShip.setText("---");
		txtRealCreditTerms.setText("---");
		txtRealCreditLimits.setText("---");
		txtRealCreditAmt.setText("---");
		txtRealDueAmt.setText("---");
		txtRealPrepaidAmt.setText("---");
		txtRealPayType.setText("---");
	}

	private void hideScreenKeyboardInPageDisplay() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	private void GetSearchCustomerDataFromDB(String cName)
	{		
		String Name = cName.trim();	
		Name= Name.toLowerCase();
		Log.e("checkname",Name);
		
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
	

	private void catchEvents() {
		customer_autotxtSearch.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				customer_autotxtSearch.setText("");
				setDataAll();
				clearAllData();
				makeQuickScroll();
			}
		});

		customer_autotxtSearch.setOnItemClickListener(new OnItemClickListener()
		{
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
		
		lstCustomer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				CustomerInfo.customer_ID=customerInfoList.get(position).getCustomerID();
				txtRealCustomerName.setText(customerInfoList.get(position).getCustomerName());
				txtRealAddress.setText(customerInfoList.get(position).getCustomerAddress());
				txtRealPhone.setText(customerInfoList.get(position).getCustomerPhone());
				txtRealTownShip.setText(customerInfoList.get(position).getCustomerTownShip());
				txtRealCreditTerms.setText(customerInfoList.get(position).getCustomerCreditTerm());
				txtRealCreditLimits.setText(customerInfoList.get(position).getCustomerCreditLimit());
				txtRealCreditAmt.setText(customerInfoList.get(position).getCustomerCreditAmt());
				txtRealDueAmt.setText(customerInfoList.get(position).getCustomerDueAmt());
				txtRealPrepaidAmt.setText(customerInfoList.get(position).getCustomerPrepaidAmt());
				txtRealPayType.setText(customerInfoList.get(position).getCustomerPaymentType());
			}
		});
		

		btnCreditCollection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{

				if(txtRealCustomerName.getText().equals("---"))
				{
					final AlertDialog dia = new AlertDialog.Builder(Payment.this)
					.setTitle("Information")
					.setMessage("\nChoose Customer\n")
					.setPositiveButton("OK", new DialogInterface.OnClickListener()
					{	
						public void onClick(DialogInterface dialog, int whichButton) 
						{
							
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
					{	
						public void onClick(DialogInterface dialog, int whichButton) 
						{

							
						}
					})
					.create();
					
					dia.show();
				}
				else
				{
					startActivity(new Intent(Payment.this, CreditCollection.class));
					finish();
				}
			}
		});
	}

	private void makeQuickScroll() 
	{
		Log.e("This is Log :","1");
		final ContactAdapter adapter = new ContactAdapter(this, customerInfoList);
		Log.e("This is Log :","2");
		setListAdapter(adapter);
		Log.e("This is Log :","3");

		final ListView list = (ListView) lstCustomer
				.findViewById(android.R.id.list);
		Log.e("This is Log :","4");

		final QuickScroll quickscroll = (QuickScroll) findViewById(R.id.quickscroll);
		Log.e("This is Log :","5");
		quickscroll.init(QuickScroll.TYPE_INDICATOR_WITH_HANDLE, list, adapter,
				QuickScroll.STYLE_HOLO);
		Log.e("This is Log :","6");
		quickscroll.setFixedSize(1);
		Log.e("This is Log :","7");
		quickscroll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
		Log.e("This is Log :","8");
	}

	private void registerIds()
	{
		btnHome = (Button) findViewById(R.id.paymentpage_btnHome);
		txtPayment = (TextView) findViewById(R.id.paymentpage_txtPayment);
		lstCustomer = (ListView) findViewById(android.R.id.list);
		btnCreditCollection = (Button) findViewById(R.id.paymentpage_btnCrdCollection);
		txtRealCustomerName = (TextView) findViewById(R.id.paymentpage_txtRealCustomerName);
		txtRealAddress = (TextView) findViewById(R.id.paymentpage_txtRealAddress);
		txtRealPhone = (TextView) findViewById(R.id.paymentpage_txtRealPhone);
		txtRealTownShip = (TextView) findViewById(R.id.paymentpage_txtRealTownShip);
		txtRealCreditTerms = (TextView) findViewById(R.id.paymentpage_txtRealCreditTerm);
		txtRealCreditLimits = (TextView) findViewById(R.id.paymentpage_txtRealCreditLimit);
		txtRealCreditAmt = (TextView) findViewById(R.id.paymentpage_txtRealCreditAmt);
		txtRealDueAmt = (TextView) findViewById(R.id.paymentpage_txtRealDueAmt);
		txtRealPrepaidAmt = (TextView) findViewById(R.id.paymentpage_txtRealPrepaidAmt);
		txtRealPayType = (TextView) findViewById(R.id.paymentpage_txtRealPaymentType);
		
		customer_autotxtSearch = (AutoCompleteTextView)findViewById(R.id.paymentpage_autotxtSearch);

	}

	public void onBackPressed() {
		startActivity(new Intent(Payment.this, HomePage.class));
		finish();

		super.onBackPressed();
	}
}
