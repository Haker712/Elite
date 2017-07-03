package com.example.elite;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import bean.classes.CustomerCategory;
import bean.classes.Zone;
import custom.classes.DBCalss;

public class AddNewCustomer extends Activity
{
	Button btnAdd, btnCancel;
	EditText edtAddress, edtContactPerson, edtCustomerName, edtPhone, edtTownship,edZone;
	Spinner spinnerZone;
	Spinner spinnerCusCag;
	SimpleDateFormat fmtForInvoiceTodayStr = new SimpleDateFormat("yyMMdd");
	//SimpleDateFormat fmtForInvoiceTodayStr = new SimpleDateFormat("dd");
	SimpleDateFormat fmtForTodayStr = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
	DecimalFormat customerIDFormat = new DecimalFormat("00");
	ArrayList<Zone> arrZoneList=new ArrayList<Zone>();
	ArrayList<CustomerCategory> arrCustomerCagList=new ArrayList<CustomerCategory>();
	ZoneListSpinnerAdapter zoneListSpinnerAdp;
	CustomerCagListSpinnerAdapter customerCagSpinnerAdp;
	String customerID;
	int clickPosForSpinner;
	String chooseZone;
	String chooseCustomerCategory;
	
	String saleManIDPrefs = null;
	String saleManNamePrefs = null;
	String saleManPwdPrefs = null;	
	String locationCodePrefs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_customer);
		
		hideScreenKeyboardInPageDisplay();
		registerIDs();
		getAllZoneData();
		getAllCustomerCategoryData();
		setAdapter();
		catchEvents();
	}
	private void setAdapter()
	{
		zoneListSpinnerAdp = new ZoneListSpinnerAdapter(arrZoneList);
		spinnerZone.setPrompt("Zone List");
		spinnerZone.setAdapter(zoneListSpinnerAdp);
		
		customerCagSpinnerAdp = new CustomerCagListSpinnerAdapter(arrCustomerCagList);
		spinnerCusCag.setPrompt("Customer Categories");
		spinnerCusCag.setAdapter(customerCagSpinnerAdp);
	}
	
	private void getAllCustomerCategoryData() 
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		

		String[] selCol = {"CustomerCategoryID", "CustomerCategoryName"};
		cur = DBCalss.eliteDB.query("CustomerCategory", selCol, null, null, null, null, null);

		while(cur.moveToNext())
		{	
			String customerCagID=cur.getString(cur.getColumnIndex("CustomerCategoryID"));
			String customerCagName=cur.getString(cur.getColumnIndex("CustomerCategoryName"));
			CustomerCategory customercat= new CustomerCategory();
			customercat.setCustomerCategoryID(customerCagID);
			customercat.setCustomerCategoryName(customerCagName);
			arrCustomerCagList.add(customercat);
			Log.e(arrCustomerCagList.size()+"","arrCustomerCagListSize");
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	private void getAllZoneData()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		

		String[] selCol = {"zoneCode", "zoneName"};
		cur = DBCalss.eliteDB.query("Zone", selCol, null, null, null, null, null);

		while(cur.moveToNext())
		{	
			String zoneCode=cur.getString(cur.getColumnIndex("zoneCode"));
			String zoneName=cur.getString(cur.getColumnIndex("zoneName"));
			Zone zone= new Zone();
			zone.setZoneCode(zoneCode);
			zone.setZoneName(zoneName);
			Log.e(zone.getZoneCode(), "ZoneCode");
			Log.e(zone.getZoneName(),"ZoneName");
			arrZoneList.add(zone);
			Log.e(arrZoneList.size()+"","ArrZoneListSize");
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	private void catchEvents()
	{
		spinnerZone.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0,View arg1, int arg2,long arg3)
			{
				clickPosForSpinner=spinnerZone.getSelectedItemPosition();
				Zone zone=arrZoneList.get(clickPosForSpinner);
				Log.e(zone.getZoneCode(), "SelectZoneCode");
				Log.e(zone.getZoneName(),"SelectZoneName");
				chooseZone=zone.getZoneCode();
			}

			@Override
			public void onNothingSelected(
					AdapterView<?> arg0) 
			{
				// TODO Auto-generated method stub
			}

		});
		
		spinnerCusCag.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0,View arg1, int arg2,long arg3)
			{
				clickPosForSpinner=spinnerCusCag.getSelectedItemPosition();
				CustomerCategory cusCag=arrCustomerCagList.get(clickPosForSpinner);
				Log.e(cusCag.getCustomerCategoryID(), "selectCustomerCategory");
				chooseCustomerCategory=cusCag.getCustomerCategoryID();
			}

			@Override
			public void onNothingSelected(
					AdapterView<?> arg0) 
			{
				// TODO Auto-generated method stub
			}

		});
	
	
		btnAdd.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v) 
			{
				final String errorfield= checkValidation();
				if(!errorfield.equals(""))
				{
					if(errorfield.contains("Customer Name"))
					{
						final AlertDialog diag1= new AlertDialog.Builder(AddNewCustomer.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\n Customer Name is empty !\n")		
						.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface diag2, int arg1) 
							{
								
							}
						})	
						.create();

						diag1.show();
						diag1.setCancelable(false);
						diag1.setCanceledOnTouchOutside(false);
					}
					else if(errorfield.contains("Contact Person"))
					{
						final AlertDialog diag2= new AlertDialog.Builder(AddNewCustomer.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\n Contact Person Name is Empty ! \n")
						.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface arg0, int arg1) 
							{
								// TODO Auto-generated method stub
								
							}
						})
						.create();
						
						diag2.show();
						diag2.setCancelable(false);
						diag2.setCanceledOnTouchOutside(false);
					}
					
					else if(errorfield.contains("Phone"))
					{
						final AlertDialog diag4=new AlertDialog.Builder(AddNewCustomer.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\n Customer Phone No is Empty !\n")
						.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								
							}
						})
						.create();
						diag4.show();
						diag4.setCancelable(false);
						diag4.setCanceledOnTouchOutside(false);
					}
					
					else if(errorfield.contains("Address"))
					{
						final AlertDialog diag3=new AlertDialog.Builder(AddNewCustomer.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\n Customer Address is Empty !\n")
						.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								
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
					final AlertDialog diag3= new AlertDialog.Builder(AddNewCustomer.this)
					.setTitle("Information")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage("\nSure To Save This Customer?\n")		
					.setPositiveButton("YES", new android.content.DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface diag2, int arg1) 
						{
							createCustomerID();
							if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
							{
								DBCalss.openDB();
							}
							
							DBCalss.eliteDB.beginTransaction();
							
							ContentValues cv = new ContentValues();			
							cv.put("CustomerID", customerID);
							cv.put("name", edtCustomerName.getText().toString().trim());
							cv.put("phone", edtPhone.getText().toString().trim());	
							cv.put("address", edtAddress.getText().toString().trim());
							cv.put("contactPerson", edtContactPerson.getText().toString().trim());
							cv.put("Zone", chooseZone);
							cv.put("CustomerCategoryID",chooseCustomerCategory);

							DBCalss.eliteDB.insert("NewCustomer", null, cv);	
							
							DBCalss.eliteDB.setTransactionSuccessful();	
							DBCalss.eliteDB.endTransaction();	
							
							DBCalss.eliteDB.beginTransaction();
							
							ContentValues cv1=new ContentValues();
							cv1.put("customerID", customerID);
							cv1.put("customerName",edtCustomerName.getText().toString().trim());
							cv1.put("Address", edtAddress.getText().toString().trim());
							cv1.put("ph", edtPhone.getText().toString().trim());
							cv1.put("isInRoute","false");
							
							DBCalss.eliteDB.insert("Customer",null,cv1);
							
							DBCalss.eliteDB.setTransactionSuccessful();
							DBCalss.eliteDB.endTransaction();
							
							Toast.makeText(getApplicationContext(), "Saving Success ...", Toast.LENGTH_LONG).show();
							
							startActivity(new Intent(AddNewCustomer.this, HomePage.class));
							finish();				
						}
					})	
					.setNegativeButton("NO", new android.content.DialogInterface.OnClickListener()
					{					
						@Override
						public void onClick(DialogInterface dialog, int which) 
						{						
						}
					})
					.create();

					diag3.show();
					diag3.setCancelable(false);
					diag3.setCanceledOnTouchOutside(false);
				}
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				startActivity(new Intent(AddNewCustomer.this,HomePage.class));
				finish();
			}
		});
	}

	private void registerIDs()
	{
		btnAdd = (Button) findViewById(R.id.addNewCustomer_btnAdd);
		btnCancel = (Button) findViewById(R.id.addNewCustomer_btnCancel);
		edtAddress = (EditText) findViewById(R.id.addNewCustomer_txtAddress);
		edtContactPerson = (EditText) findViewById(R.id.addNewCustomer_txtContactPerson);
		edtCustomerName = (EditText) findViewById(R.id.addNewCustomer_txtCustomerName);
		edtPhone = (EditText) findViewById(R.id.addNewCustomer_txtPhone);
		spinnerZone=(Spinner) findViewById(R.id.spinnerZone);
		spinnerCusCag=(Spinner) findViewById(R.id.spinnerCustomerCategory);
		
		SharedPreferences prefs = getSharedPreferences("SaleManPrefs", MODE_PRIVATE); 
		saleManIDPrefs = prefs.getString(MainActivity.saleManIdPrefs, "No name defined");
		saleManNamePrefs = prefs.getString(MainActivity.saleManNamePrefs,"No name defined" );
		saleManPwdPrefs = prefs.getString(MainActivity.passwordPrefs, "No name defined");	
		locationCodePrefs = prefs.getString(MainActivity.locationCodePrefs, "No name defined");
	}
	
	private void createCustomerID()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		
		int count = 0;

		String[] selCol = {"COUNT(*)"};
		cur = DBCalss.eliteDB.query("NewCustomer", selCol, null, null, null, null, null);

		while(cur.moveToNext())
		{	
			count = cur.getInt(cur.getColumnIndex("COUNT(*)"));
		}

		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
		Calendar todayCal = Calendar.getInstance();
		String todayDate = fmtForInvoiceTodayStr.format(todayCal.getTime());
		Log.e(count+"", "TableDataCount");
		customerID = saleManIDPrefs+todayDate+customerIDFormat.format(count + 1);
		Log.e(customerID, "newcustomerID");
	}

	private void hideScreenKeyboardInPageDisplay()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);		
	}
	
	@Override
	public void onBackPressed()
	{
		startActivity(new Intent(AddNewCustomer.this, HomePage.class));
		finish();
		
		super.onBackPressed();
	}
	
	public class ZoneListSpinnerAdapter implements SpinnerAdapter
	{
		ArrayList<Zone> zoneSpinnerList;
		public ZoneListSpinnerAdapter(ArrayList<Zone> zoneList )
		{
			this.zoneSpinnerList = zoneList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return zoneSpinnerList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return zoneSpinnerList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getView(int pos, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			return spinnerRow(pos);
		}
		
		public View spinnerRow(int pos)
		{
			TextView v = new TextView(AddNewCustomer.this);
			v.setText(arrZoneList.get(pos).getZoneName());
			v.setPadding(10, 10, 0, 10);
			v.setTextSize(15);
			return v;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public View getDropDownView(int pos, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			
			return spinnerRow(pos);
		}
	}
	
	public class CustomerCagListSpinnerAdapter implements SpinnerAdapter
	{
		ArrayList<CustomerCategory> customerCagSpinnerlist;
		public CustomerCagListSpinnerAdapter(ArrayList<CustomerCategory> cusCaglist )
		{
			this.customerCagSpinnerlist = cusCaglist;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return customerCagSpinnerlist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return customerCagSpinnerlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getView(int pos, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			return spinnerRow(pos);
		}
		
		public View spinnerRow(int pos)
		{
			TextView v = new TextView(AddNewCustomer.this);
			v.setText(arrCustomerCagList.get(pos).getCustomerCategoryName());
			v.setPadding(10, 10, 0, 10);
			v.setTextSize(15);
			return v;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public View getDropDownView(int pos, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			
			return spinnerRow(pos);
		}
	}
	
	private String checkValidation()
	{			
		if(edtCustomerName.getText().toString().length() == 0)
		{
			return "Customer Name";
		}

		else if(edtContactPerson.getText().length() == 0)
		{
			return "Contact Person";
		}
		
		else if(edtPhone.getText().length() == 0)
		{
			return "Phone";
		}
		
		else if(edtAddress.getText().length() == 0)
		{
			return "Address";
		}

		return "";
	}

}
