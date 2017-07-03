package com.example.elite;
import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bean.classes.CustomerInfo;
import bean.classes.Invoice;

import com.andraskindler.quickscroll.QuickScroll;
import com.quick.scroll.files.ContactAdapter;

import custom.classes.DBCalss;

@SuppressLint("DefaultLocale")
public class Customer extends ListActivity 
{
	ListView lstCustomer;
	Button btnSale,btnHome,btnPreOrder,btnDelivery;
	ArrayAdapter  searchAdapter;
	AutoCompleteTextView  customer_autotxtSearch;
	TextView  txtCustomerName,txtCustomerAddress,txtCustomerPhone,txtCustomerTownship,txtCustomerCTerm,txtCustomerCLimit,txtCustomerDueAmt,txtCustomerCAmt,txtCustomerPrepaidAmt,txtCustomerPaymentType;
	SQLiteDatabase db;
	public static final String PATH = "data/data/com.example.elite/databases/";
	public  static ArrayList<CustomerInfo> customerList=new ArrayList<CustomerInfo>();
	public static ArrayList<CustomerInfo> customerInfoList = new ArrayList<CustomerInfo>();
	public  static ArrayList<String> searchCustomer_ShowList = new ArrayList<String>();
	public static ArrayList<Invoice> invoiceList=new ArrayList<Invoice>();
	public static int selectedItem = -1;
	ContactAdapter adapter; 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer);		 
		hideScreenKeyboardInPageDisplay();
		registerIDs();
		clearAllData();		
		GetCustomerDataFromDB();
		setDataAll();
		setShowDataForCustomerList();
		setAdapter();
		makeQuickScroll();
		catchEvents();
	}

	private void setDataAll()
	{
		// TODO Auto-generated method stub
		customerInfoList.clear();
		customerInfoList.addAll(customerList);		
	}

	private void setShowDataForCustomerList()
	{
		// TODO Auto-generated method stub
		searchCustomer_ShowList.clear();
		boolean isInsertedflag;

		for(int i=0; i<customerList.size(); i++)
		{
			isInsertedflag = false;

			for(String insertedItem : searchCustomer_ShowList)
			{			
				if(customerList.get(i).getCustomerName().equalsIgnoreCase(insertedItem))
				{
					isInsertedflag = true;
					break;
				}
			}

			if(!isInsertedflag)
			{			
				searchCustomer_ShowList.add(customerList.get(i).getCustomerName());			
			}
		}
		searchAdapter = new ArrayAdapter<String>(this,R.layout.layout_for_autocompletext,searchCustomer_ShowList);
		customer_autotxtSearch.setAdapter(searchAdapter);		
	}

	private void setAdapter() {
		// TODO Auto-generated method stub

		searchAdapter = new ArrayAdapter<String>(this,R.layout.layout_for_autocompletext,searchCustomer_ShowList);
		customer_autotxtSearch.setAdapter(searchAdapter);	
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
	private void GetCustomerDataFromDB() {
		// TODO Auto-generated method stub
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		String sql = "SELECT customerID,UPPER(customerName) as customerName,customerTypeID,customerTypeName,Address,ph,township,creditTerm,creditLimit,creditAmt,dueAmt,prepaidAmt,paymentType,isInRoute  FROM  Customer ORDER BY customerName";
		Cursor cur = DBCalss.eliteDB.rawQuery(sql,null);		
		customerList.clear();		

		while(cur.moveToNext())
		{	
			CustomerInfo  customer = new  CustomerInfo();
			customer.setCustomerID(cur.getString(cur.getColumnIndex("customerID")));
			customer.setCustomerName(cur.getString(cur.getColumnIndex("customerName")));
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

			customerList.add(customer);			
		}

		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();	
		DBCalss.eliteDB.endTransaction();

	}

	@Override
	public void onBackPressed()
	{
		startActivity(new Intent(Customer.this,HomePage.class));
		finish();

		super.onBackPressed();
	}

	private void hideScreenKeyboardInPageDisplay()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);		
	}

	private void catchEvents()
	{
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
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				String selItemName = customer_autotxtSearch.getText().toString();
				Log.e("check Name",selItemName);
				GetSearchCustomerDataFromDB(selItemName);
				makeQuickScroll();

			}

		});

		btnHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(Customer.this, HomePage.class));
				finish();
			}
		});


		btnSale.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				if(txtCustomerName.getText().equals("---"))
				{
					final AlertDialog dia = new AlertDialog.Builder(Customer.this)
					.setTitle("Information")
					.setMessage("\nPlease Choose Customer\n")
					.setPositiveButton("OK", new DialogInterface.OnClickListener()
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
					if(!txtCustomerCTerm.getText().toString().equals(""))
					{
						Double cTerm = Double.parseDouble(txtCustomerCTerm.getText().toString());
						int creditTerm=	(int)Math.round(cTerm);
						CustomerInfo.creditTerm =creditTerm;
						Log.e("CreditTerm...",CustomerInfo.creditTerm+"");
					}
					else
					{
						CustomerInfo.creditTerm =0;
					}
					
					if(!txtCustomerCAmt.getText().toString().equals(""))
					{
						Double cAmt =Double.parseDouble(txtCustomerCAmt.getText().toString());
						int creditAmt = (int)Math.round(cAmt);
						CustomerInfo.creditAmount = creditAmt;
						Log.e("CreditAmt", CustomerInfo.creditAmount+"");
					}
					else
					{
						CustomerInfo.creditAmount = 0;
					}
					
					if(!txtCustomerCLimit.getText().toString().equals(""))
					{
						Double cLimit = Double.parseDouble(txtCustomerCLimit.getText().toString());
						int  creditLimit= (int) Math.round(cLimit);
						CustomerInfo.creditLimit = creditLimit;
						Log.e("CreditLimit", CustomerInfo.creditLimit +"");
					}
					else
					{
						CustomerInfo.creditLimit = 0;
					}
					
					startActivity(new Intent(Customer.this, SalePage.class));
					finish();
				}
			}

		});
		
		btnDelivery.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				/*if(txtCustomerName.getText().equals("---"))
				{
					final AlertDialog dia = new AlertDialog.Builder(Customer.this)
					.setTitle("Information")
					.setMessage("\nPlease Choose Customer\n")
					.setPositiveButton("OK", new DialogInterface.OnClickListener()
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
					if(!txtCustomerCTerm.getText().toString().equals(""))
					{
						Double cTerm = Double.parseDouble(txtCustomerCTerm.getText().toString());
						int creditTerm=	(int)Math.round(cTerm);
						CustomerInfo.creditTerm =creditTerm;
						Log.e("CreditTerm...",CustomerInfo.creditTerm+"");
					}
					else
					{
						CustomerInfo.creditTerm =0;
					}
					
					if(!txtCustomerCAmt.getText().toString().equals(""))
					{
						Double cAmt =Double.parseDouble(txtCustomerCAmt.getText().toString());
						int creditAmt = (int)Math.round(cAmt);
						CustomerInfo.creditAmount = creditAmt;
						Log.e("CreditAmt", CustomerInfo.creditAmount+"");
					}
					else
					{
						CustomerInfo.creditAmount = 0;
					}
					
					if(!txtCustomerCLimit.getText().toString().equals(""))
					{
						Double cLimit = Double.parseDouble(txtCustomerCLimit.getText().toString());
						int  creditLimit= (int) Math.round(cLimit);
						CustomerInfo.creditLimit = creditLimit;
						Log.e("CreditLimit", CustomerInfo.creditLimit +"");
					}
					else
					{
						CustomerInfo.creditLimit = 0;
					}
					Toast.makeText(Customer.this, "You can't use Delivery!", Toast.LENGTH_LONG).show();
				}*/
                startActivity(new Intent(Customer.this, DailyCheckList.class));//DailyCheckList
                finish();
			}

		});
		
		btnPreOrder.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				if(txtCustomerName.getText().equals("---"))
				{
					final AlertDialog dia = new AlertDialog.Builder(Customer.this)
					.setTitle("Information")
					.setMessage("\nPlease Choose Customer\n")
					.setPositiveButton("OK", new DialogInterface.OnClickListener()
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
					if(!txtCustomerCTerm.getText().toString().equals(""))
					{
						Double cTerm = Double.parseDouble(txtCustomerCTerm.getText().toString());
						int creditTerm=	(int)Math.round(cTerm);
						CustomerInfo.creditTerm =creditTerm;
						Log.e("CreditTerm...",CustomerInfo.creditTerm+"");
					}
					else
					{
						CustomerInfo.creditTerm =0;
					}
					
					if(!txtCustomerCAmt.getText().toString().equals(""))
					{
						Double cAmt =Double.parseDouble(txtCustomerCAmt.getText().toString());
						int creditAmt = (int)Math.round(cAmt);
						CustomerInfo.creditAmount = creditAmt;
						Log.e("CreditAmt", CustomerInfo.creditAmount+"");
					}
					else
					{
						CustomerInfo.creditAmount = 0;
					}
					
					if(!txtCustomerCLimit.getText().toString().equals(""))
					{
						Double cLimit = Double.parseDouble(txtCustomerCLimit.getText().toString());
						int  creditLimit= (int) Math.round(cLimit);
						CustomerInfo.creditLimit = creditLimit;
						Log.e("CreditLimit", CustomerInfo.creditLimit +"");
					}
					else
					{
						CustomerInfo.creditLimit = 0;
					}
					
//					startActivity(new Intent(Customer.this, PreOrder.class));
//					finish();
					Toast.makeText(Customer.this, "You can't use Pre-Order!", Toast.LENGTH_LONG).show();
				}
			}
		});

//		btnDelivery.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View arg0) 
//			{
//				if(txtCustomerName.getText().equals("---"))
//				{
//					final AlertDialog dia = new AlertDialog.Builder(Customer.this)
//					.setTitle("Information")
//					.setMessage("\nPlease Choose Customer\n")
//					.setPositiveButton("OK", new DialogInterface.OnClickListener()
//					{	
//						public void onClick(DialogInterface dialog, int whichButton) 
//						{
//
//						}
//					})
//					.create();
//
//					dia.show();
//				}
//				else
//				{
//					if(!txtCustomerCTerm.getText().toString().equals(""))
//					{
//						Double cTerm = Double.parseDouble(txtCustomerCTerm.getText().toString());
//						int creditTerm=	(int)Math.round(cTerm);
//						CustomerInfo.creditTerm =creditTerm;
//						Log.e("CreditTerm...",CustomerInfo.creditTerm+"");
//					}
//					else
//					{
//						CustomerInfo.creditTerm =0;
//					}
//					
//					if(!txtCustomerCAmt.getText().toString().equals(""))
//					{
//						Double cAmt =Double.parseDouble(txtCustomerCAmt.getText().toString());
//						int creditAmt = (int)Math.round(cAmt);
//						CustomerInfo.creditAmount = creditAmt;
//						Log.e("CreditAmt", CustomerInfo.creditAmount+"");
//					}
//					else
//					{
//						CustomerInfo.creditAmount = 0;
//					}
//					
//					if(!txtCustomerCLimit.getText().toString().equals(""))
//					{
//						Double cLimit = Double.parseDouble(txtCustomerCLimit.getText().toString());
//						int  creditLimit= (int) Math.round(cLimit);
//						CustomerInfo.creditLimit = creditLimit;
//						Log.e("CreditLimit", CustomerInfo.creditLimit +"");
//					}
//					else
//					{
//						CustomerInfo.creditLimit = 0;
//					}
//					
//					Log.e("Clicked CUS ID", CustomerInfo.customer_ID+"");
//					getAllInvliceListByCustomerID();
//					if(invoiceList.size() == 0)
//					{
//						Toast.makeText(getApplicationContext(), "No Delivery Products for this customer", Toast.LENGTH_LONG).show();
//					}
//					else
//					{
////						startActivity(new Intent(Customer.this, Delivery.class));
////						finish();
//						
//					}
//				}
//			}
//		
//		});

		lstCustomer.setOnItemClickListener(new OnItemClickListener()
		
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) 
			{
				selectedItem = position;
				
				CustomerInfo.customer_ID=customerInfoList.get(position).getCustomerID();
				txtCustomerName.setText(customerInfoList.get(position).getCustomerName());
				txtCustomerAddress.setText(customerInfoList.get(position).getCustomerAddress());
				txtCustomerCTerm.setText(customerInfoList.get(position).getCustomerCreditTerm());
				txtCustomerCLimit.setText(customerInfoList.get(position).getCustomerCreditLimit());
				txtCustomerCAmt.setText(customerInfoList.get(position).getCustomerCreditAmt());
				txtCustomerPrepaidAmt.setText(customerInfoList.get(position).getCustomerPrepaidAmt());
				txtCustomerPaymentType.setText(customerInfoList.get(position).getCustomerPaymentType());
				txtCustomerDueAmt.setText(customerInfoList.get(position).getCustomerDueAmt());

				if(customerInfoList.get(position).getCustomerPhone().trim().equals(""))
				{
					txtCustomerPhone.setText("---");
				}
				else
				{
					txtCustomerPhone.setText(customerInfoList.get(position).getCustomerPhone());
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void makeQuickScroll() 
	{
		Log.e("QuickScroll", "QuickScroll");
		Log.e("SELECT",selectedItem+"");
		adapter= new ContactAdapter(this,customerInfoList);
		setListAdapter(adapter);

		final ListView list = (ListView) lstCustomer.findViewById(android.R.id.list);
		final QuickScroll quickscroll = (QuickScroll) findViewById(R.id.quickscroll);
		quickscroll.init(QuickScroll.TYPE_INDICATOR_WITH_HANDLE, list, adapter, QuickScroll.STYLE_HOLO);
		quickscroll.setFixedSize(1);
		quickscroll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);
	}

	private void registerIDs()
	{
		lstCustomer = (ListView) findViewById(android.R.id.list);
		customer_autotxtSearch = (AutoCompleteTextView) findViewById(R.id.customer_autotxtSearch);
		btnSale = (Button) findViewById(R.id.customer_btnSale);
		btnHome = (Button) findViewById(R.id.report_btnHome);
		txtCustomerName = (TextView) findViewById(R.id.customer_txtName);
		txtCustomerAddress = (TextView) findViewById(R.id.customer_txtAddress);
		txtCustomerPhone = (TextView) findViewById(R.id.customer_txtPhone);
		txtCustomerTownship = (TextView) findViewById(R.id.customer_txtTownship);
		txtCustomerCTerm = (TextView) findViewById(R.id.customer_txtCreditTerm);
		txtCustomerCLimit = (TextView) findViewById(R.id.customer_txtCreditLimit);
		txtCustomerCAmt = (TextView) findViewById(R.id.customer_txtCreditAmt);
		txtCustomerPrepaidAmt = (TextView) findViewById(R.id.customer_txtPrepaidAmt);
		txtCustomerPaymentType = (TextView) findViewById(R.id.customer_txtPayType);
		txtCustomerDueAmt = (TextView) findViewById(R.id.customer_txtDueAmt);
		btnPreOrder = (Button)findViewById(R.id.customer_btnPreOrder);
		btnDelivery = (Button)findViewById(R.id.customer_btnDelivery);

	}
	private void clearAllData()
	{
		txtCustomerName.setText("---");
		txtCustomerAddress.setText("---");
		txtCustomerPhone.setText("---");
		txtCustomerTownship.setText("---");
		txtCustomerCTerm.setText("---");
		txtCustomerCLimit.setText("---");
		txtCustomerCAmt.setText("---");
		txtCustomerPrepaidAmt.setText("---");
		txtCustomerPaymentType.setText("---");
		txtCustomerDueAmt.setText("---");
	}
	
	private void getAllInvliceListByCustomerID() 
	{
		Log.e("Htoo", "Test1");
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;
		Log.e("Before fetching", CustomerInfo.customer_ID);
		String[] args = {CustomerInfo.customer_ID};
		String[] selCol = {"saleOrderNo","customerID","orderedDate"};
		cur = DBCalss.eliteDB.query("Delivery",selCol, "customerID LIKE ?", args, null, null, null);
		invoiceList.clear();
		while(cur.moveToNext())
		{	
			String invoiceID=cur.getString(cur.getColumnIndex("saleOrderNo"));
			String orderDate=cur.getString(cur.getColumnIndex("orderedDate"));
			
			Invoice invoice= new Invoice();
			invoice.setInvoiceID(invoiceID);
			invoice.setOrderDate(orderDate);
			invoiceList.add(invoice);
			Log.e("ARRInvoiceSize",invoiceList.size()+"");
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	
	}
}
