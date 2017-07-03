package com.example.elite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;

import json.string.maker.TLAJsonStringMaker;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import custom.classes.CheckNetworkState;
import custom.classes.DBCalss;
import custom.classes.GetDevID;

public class DownloadDataFromServer extends Activity
{
	//static String  URL= "http://192.168.10.100:4040/cmpolaris/"; //elite server IP address
	static String  URL= "http://192.168.11.62:4040/cmpolaris/"; // office IP address
	//static String  URL= "http://192.168.1.87:4040/cmpolaris/"; // office old IP address
	ProgressBar pbDownLoad; 
	TextView txtSuccess, txtPercent, txtCurrentTask, txtFail;
	Timer timer;
	Button btnredownload;
    String isSuccessCustomerFlag="false",isSuccessSaleProductFlag="false",isSuccessVolumeDisFlag="false",isSuccessCustomDisFlag="false",isSuccessZoneFlag="false",isSuccesscustomerCateogoryFlag="false",isSuccessPreOrderProductFlag="false",isSuccessDeliveryFlag="false",isSuccessCreditColletionFlag="false";
	int progress = 12;
	String status = "";
	int count = 0;
	
	String saleManIDPrefs = null;
	String saleManNamePrefs = null;
	String saleManPwdPrefs = null;	
	String locationCodePrefs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_data_page);
		
		SharedPreferences prefs = getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE); 
		saleManIDPrefs = prefs.getString(MainActivity.saleManIdPrefs, "No name defined");
		saleManNamePrefs = prefs.getString(MainActivity.saleManNamePrefs,"No name defined" );
		saleManPwdPrefs = prefs.getString(MainActivity.passwordPrefs, "No name defined");	
		locationCodePrefs = prefs.getString(MainActivity.locationCodePrefs, "No name defined");	
		
		registerIDs();
		downloadData();		
	    catchEvent();

	}

	private void checkDownloadDataAllSuccess() 
	{			
			GetConfirmGet conGet = new GetConfirmGet();
			conGet.execute();				
	}

	private void catchEvent() {
		
		btnredownload.setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View arg0) {				
				reDownloadData();				
			}
		});
	}

	private void reDownloadData() {
		
		Log.e("check...222","cu"+isSuccessCustomerFlag+"pr"+isSuccessSaleProductFlag+"vo"+isSuccessVolumeDisFlag+"item"+isSuccessCustomDisFlag+"zone"+isSuccessZoneFlag+"PreOrderProduct"+isSuccessPreOrderProductFlag+"CreditCollection"+ isSuccessCreditColletionFlag);
		if(isSuccessCustomerFlag.equals("false"))
		{
			GetCustomerListInRoute gCI1 = new GetCustomerListInRoute();
			gCI1.execute();
		}
		else if(isSuccessSaleProductFlag.equals("false"))
		{
			GetProductListForSale gPL1 = new GetProductListForSale();
			gPL1.execute();
		}		
		else if(isSuccessCustomDisFlag.equals("false"))
		{
			GetCustomDiscout gCDis=new GetCustomDiscout();
			gCDis.execute();
		}
		else if(isSuccessZoneFlag.equals("false"))
		{
			GetZone gZoneList1=new GetZone();
			gZoneList1.execute();
		}
		else if(isSuccesscustomerCateogoryFlag.equals("false"))
		{
			GetCustomerCategory gCustomerCat=new GetCustomerCategory();
			gCustomerCat.execute();
		}
		else if(isSuccessCustomerFlag.equals("true") && isSuccessSaleProductFlag.equals("true") && isSuccessVolumeDisFlag.equals("true") && isSuccessCustomDisFlag.equals("true") && isSuccessZoneFlag.equals("true") && isSuccesscustomerCateogoryFlag.equals("true") && isSuccessPreOrderProductFlag.equals("true") && isSuccessDeliveryFlag.equals("true"))
		{
			GetConfirmGet conGet = new GetConfirmGet();
			conGet.execute();	
			
		}
	}

	private void downloadData()
	{
		if(CheckNetworkState.isNetworkStatusAvialable(DownloadDataFromServer.this)==true)
		{
			GetCustomerListInRoute gCI = new GetCustomerListInRoute();
			gCI.execute();
		}
		else
		{
			Toast.makeText(getApplicationContext(),	"No Internet Connection", Toast.LENGTH_SHORT).show();
		}	
	}
	private class GetCustomerListInRoute extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			txtCurrentTask.setText("Downloading Customer Data ...");				
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String userdata = "";
			try 
			{				
				URL url = new URL(URL +"cus/getcuslistinroute");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(30000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				OutputStream outputStream = conn.getOutputStream();
				Log.e("Sending GetCustomer To Server", makeSendingString());
				outputStream.write(makeSendingString().getBytes());
				outputStream.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					return "Oops. Can't Connect To Server. Error Code : " + conn.getResponseCode() + "";
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String output;
				while ((output = br.readLine()) != null) 
				{
					userdata = output;
				}
				conn.disconnect();
			}
			catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("GetCustomerList Status", userdata);
			return userdata;
		}

		private String makeSendingString() 
		{
			String devID = GetDevID.getDevId(DownloadDataFromServer.this);

			ArrayList<String> keyList = new ArrayList<String>();
			keyList.add("userId");
			keyList.add("pwd");
			keyList.add("devID");
			keyList.add("locationCode");

			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add(saleManIDPrefs);
			valueList.add(saleManPwdPrefs);
			valueList.add(devID);
			valueList.add(locationCodePrefs);

			return TLAJsonStringMaker.jsonStringMaker(keyList, valueList);
		}

		@Override
		protected void onPostExecute(String result)
		{			
			super.onPostExecute(result);	


			if(result.contains("Oops"))
			{
				txtFail.setText(txtFail.getText() + "\nDownLoad Customer Info => "+ result + "!!!");	
				isSuccessCustomerFlag="false";
				errorMessageDialogShow();
			}
			else if(result.equals(""))
			{			
				txtFail.setText(txtFail.getText() + "\nDownLoad Customer Info => Slow Connection!!!");			
				isSuccessCustomerFlag="false";
				errorMessageDialogShow();
			}
			else
			{
				
				Log.e("else","else");
				String customerID, customerName, customerTypeID, customerTypeName, address, ph, creditTerm, creditLimit, creditAmt, dueAmt, prepaidAmt, paymentType, isInRoute;
				isSuccessCustomerFlag="false";
				try
				{
					JSONArray jsonResultArr = new JSONArray(result);

					for(int i=0; i<jsonResultArr.length(); i++)
					{
						Log.e("here","here");
						JSONObject jObj = jsonResultArr.getJSONObject(i);
						Log.e(jsonResultArr.getJSONObject(i).length()+"","ArrayLength");

						customerID = jObj.getString("customerID");
						Log.e(customerID,"CustomerID");
						customerName = jObj.getString("customerName");
						Log.e(customerName,"CustomerName");
						customerTypeID = jObj.getString("customerTypeID");
						Log.e(customerTypeID,"CustomerTypeID");
						customerTypeName = jObj.getString("customerTypeName");	
						Log.e(customerTypeName,"CustomerTypeName");
						address = jObj.getString("address");
						Log.e("Address",address);
						ph = jObj.getString("ph");
						Log.e("Phone",ph);
						creditTerm = jObj.getString("creditTerm");	
						Log.e("CreditTeam",creditTerm);
						creditLimit = jObj.getString("creditLimit");
						Log.e("CreditLimit",creditLimit);
						creditAmt = jObj.getString("creditAmt");
						Log.e("CreditAmt",creditAmt);
						dueAmt = jObj.getString("dueAmt");
						Log.e("DueAmt",dueAmt);
						prepaidAmt = jObj.getString("prepaidAmt");
						Log.e("PrepaidAmt",prepaidAmt);
						paymentType = jObj.getString("paymentType");
						Log.e("PaymentType",paymentType);
						isInRoute = jObj.getString("isInRoute");	
						Log.e(isInRoute,"IsInRoute");

						saveCustomerInfoInDB(customerID.trim(), customerName.trim(), customerTypeID.trim(), "",address.trim(), ph, creditTerm, creditLimit, creditAmt, dueAmt, prepaidAmt, paymentType, isInRoute);
						Log.e("Here123","Here123");
					}
					
					Log.e("Here234","here234");
					txtSuccess.setText(txtSuccess.getText() + "\nDownLoad Customer Info => Success");
					pbDownLoad.setProgress(pbDownLoad.getProgress() + 12);
					txtCurrentTask.setText("Download Customer Data Finish ...");
					txtPercent.setText(pbDownLoad.getProgress() + "%");
					isSuccessCustomerFlag ="true";
					Log.e("download customer flag",isSuccessCustomerFlag);
					GetProductListForSale gPL = new GetProductListForSale();
					gPL.execute();
				}
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void saveCustomerInfoInDB(String customerID, String customerName, String customerTypeID, String customerTypeName, String address, String ph, String creditTerm, String creditLimit, String creditAmt, String dueAmt, String prepaidAmt, String paymentType, String isInRoute) 
		{
			if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
			{
				DBCalss.openDB();
			}

			DBCalss.eliteDB.beginTransaction();

			ContentValues cv = new ContentValues();			

			cv.put("customerID", customerID);
			cv.put("customerName", customerName);	
			cv.put("customerTypeID", customerTypeID);
			cv.put("customerTypeName", customerTypeName);		
			cv.put("Address", address);
			cv.put("ph", ph);	
			cv.put("township", "");
			cv.put("creditTerm", creditTerm);		
			cv.put("creditLimit", creditLimit);			
			cv.put("creditAmt", creditAmt);
			cv.put("dueAmt", dueAmt);	
			cv.put("prepaidAmt", prepaidAmt);
			cv.put("paymentType", paymentType);		
			cv.put("isInRoute", isInRoute);
			cv.put("totalAmt","");		
			cv.put("totalPaidAmt","");
			
            Log.e("CustomerName",customerName);
			DBCalss.eliteDB.insert("Customer", null, cv);	
			DBCalss.eliteDB.setTransactionSuccessful();	
			DBCalss.eliteDB.endTransaction();
		}
	}
	
	private class GetCustomDiscout extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			txtCurrentTask.setText("Downloading Custom Discount Data ...");
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String userdata = "";
			try 
			{				
				URL url = new URL(URL+"pro/getcustomdiscountlist");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(13000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				OutputStream outputStream = conn.getOutputStream();
				Log.e("Sending Custom Discount To Server", makeSendingString());
				outputStream.write(makeSendingString().getBytes());
				outputStream.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					return "Oops. Can't Connect To Server. Error Code : " + conn.getResponseCode() + "";
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String output;
				while ((output = br.readLine()) != null) 
				{				
					userdata = output;
				}
				conn.disconnect();
			}
			catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("Customdiscount Status", userdata);
			return userdata;
		}

		private String makeSendingString() 
		{
			String devID = GetDevID.getDevId(DownloadDataFromServer.this);

			ArrayList<String> keyList = new ArrayList<String>();
			keyList.add("userId");
			keyList.add("pwd");
			keyList.add("devID");
			keyList.add("locationCode");

			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add(saleManIDPrefs);
			valueList.add(saleManPwdPrefs);
			valueList.add(devID);
			valueList.add(locationCodePrefs);			 			

			return TLAJsonStringMaker.jsonStringMaker(keyList, valueList);
		}

		@Override
		protected void onPostExecute(String result)
		{			
			super.onPostExecute(result);	

			if(result.contains("Oops"))
			{
				txtFail.setText(txtFail.getText() + "\nDownLoad Custom Discount Info => "+ result + "!!!");
				isSuccessCustomDisFlag = "false";
				errorMessageDialogShow();
			}
			else if(result.equals(""))
			{			
				txtFail.setText(txtFail.getText() + "\nDownLoad Custom Discount Info => Slow Connection!!!");	
				isSuccessCustomDisFlag = "false";
				errorMessageDialogShow();
			}
			else
			{
				String fromDiscount , toDiscount;
				isSuccessCustomDisFlag="false";
				try
				{					
					JSONObject jObj = new JSONObject(result);
						fromDiscount = jObj.getString("fromDiscount");
						toDiscount = jObj.getString("toDiscount");
						saveDiscountInfoInDB(fromDiscount, toDiscount);
					
					txtSuccess.setText(txtSuccess.getText() + "\nDownLoad Discount Info => Success");
					pbDownLoad.setProgress(pbDownLoad.getProgress() + 12);
					txtCurrentTask.setText("Download Discount Data Finish ...");
					txtPercent.setText(pbDownLoad.getProgress() + "%");
					isSuccessCustomDisFlag="true";
					Log.e("download Item flag",isSuccessCustomDisFlag);
					GetZone gZoneList=new GetZone();
					gZoneList.execute();
					
				}
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void saveDiscountInfoInDB(String fromDiscount, String toDiscount) 
		{
			if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
			{
				DBCalss.openDB();
			}

			DBCalss.eliteDB.beginTransaction();

			ContentValues cv = new ContentValues();			
			cv.put("fromDiscount", fromDiscount);
			cv.put("toDiscount", toDiscount);		
	
			DBCalss.eliteDB.insert("CustomDiscount", null, cv);	
			DBCalss.eliteDB.setTransactionSuccessful();	
			DBCalss.eliteDB.endTransaction();	
		}
	}
	private class GetProductListForSale extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			txtCurrentTask.setText("Downloading Sale Product Data ...");
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String userdata = "";
			try 
			{				
				URL url = new URL(URL+"pro/getproductlist");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(20000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				OutputStream outputStream = conn.getOutputStream();
				Log.e("Sending GetProduct To Server", makeSendingString());
				outputStream.write(makeSendingString().getBytes());
				outputStream.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					return "Oops. Can't Connect To Server. Error Code : " + conn.getResponseCode() + "";
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String output;
				while ((output = br.readLine()) != null) 
				{				
					userdata = output;
				}
				conn.disconnect();
			}
			catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.e("GetProduct Status", userdata);
			return userdata;
		}

		private String makeSendingString() 
		{
			String devID = GetDevID.getDevId(DownloadDataFromServer.this);

			ArrayList<String> keyList = new ArrayList<String>();
			keyList.add("userId");
			keyList.add("pwd");
			keyList.add("devID");
			keyList.add("locationCode");

			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add(saleManIDPrefs);
			valueList.add(saleManPwdPrefs);
			valueList.add(devID);
			valueList.add(locationCodePrefs);			 			

			return TLAJsonStringMaker.jsonStringMaker(keyList, valueList);
		}

		@Override
		protected void onPostExecute(String result)
		{			
			super.onPostExecute(result);	

			if(result.contains("Oops"))
			{
				txtFail.setText(txtFail.getText() + "\nDownLoad Sale Product Info => "+ result + "!!!");		
				isSuccessSaleProductFlag = "false";
				errorMessageDialogShow();
			}
			else if(result.equals(""))
			{			
				txtFail.setText(txtFail.getText() + "\nDownLoad Sale Product Info => Slow Connection!!!");	
				isSuccessSaleProductFlag = "false";
				errorMessageDialogShow();
			}
			else
			{
				String categoryID, categoryName, groupID, groupName, productID, productName, totalQty, sellingPrice, purchasePrice,discountType;
				isSuccessSaleProductFlag="false";
				try
				{
					JSONArray jsonResultArr = new JSONArray(result);

					for(int i=0; i<jsonResultArr.length(); i++)
					{
						JSONObject jObj = jsonResultArr.getJSONObject(i);

						categoryID = jObj.getString("categoryID").trim();
						categoryName = jObj.getString("categoryName").trim();
						groupID = jObj.getString("groupID").trim();
						groupName = jObj.getString("groupName").trim();						
						productID = jObj.getString("productID").trim();
						productName = jObj.getString("productName").trim();
						totalQty = jObj.getString("totalQty").trim();
						sellingPrice = jObj.getString("sellingPrice").trim();						
						purchasePrice = jObj.getString("purchasePrice").trim();
						discountType = jObj.getString("discountType").trim();
						
						saveProductInfoInDB(categoryID, categoryName, groupID, groupName, productID, productName, totalQty, sellingPrice, purchasePrice,discountType);
					}
					
					txtSuccess.setText(txtSuccess.getText() + "\nDownLoad Sale Product Info => Success");
					pbDownLoad.setProgress(pbDownLoad.getProgress() + 12);
					txtCurrentTask.setText("Download Sale Product Data Finish ...");
					txtPercent.setText(pbDownLoad.getProgress() + "%");
					isSuccessSaleProductFlag="true";	
					Log.e("download Sale Product flag",isSuccessSaleProductFlag);
					GetCustomDiscout gCustomDis = new GetCustomDiscout();
					gCustomDis.execute();
				}
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void saveProductInfoInDB(String categoryID,	String categoryName, String groupID, String groupName, String productID, String productName, String totalQty, String sellingPrice, String purchasePrice,String discountType) 
		{
			if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
			{
				DBCalss.openDB();
			}

			DBCalss.eliteDB.beginTransaction();

			ContentValues cv = new ContentValues();			

			cv.put("categoryID", categoryID);
			cv.put("categoryName", categoryName);	
			cv.put("groupID", groupID);
			cv.put("groupName", groupName);		
			cv.put("productID", productID);
			cv.put("productName", productName);	
			cv.put("totalQty", totalQty);
			cv.put("sellingPrice", sellingPrice);		
			cv.put("purchasePrice", purchasePrice);
			cv.put("discountType", discountType);
			cv.put("SaleQty","0");
			cv.put("RemainingQty", totalQty);
			DBCalss.eliteDB.insert("Product", null, cv);	
			DBCalss.eliteDB.setTransactionSuccessful();	
			DBCalss.eliteDB.endTransaction();	

		}
	}
	private class GetZone extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			txtCurrentTask.setText("Downloading Zone Data ...");
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String userdata = "";
			try 
			{				
				URL url = new URL(URL+"cus/getzonelist");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(13000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(makeSendingString().getBytes());
				outputStream.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					return "Oops. Can't Connect To Server. Error Code : " + conn.getResponseCode() + "";
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String output;
				while ((output = br.readLine()) != null) 
				{				
					userdata = output;
				}
				conn.disconnect();
			}
			catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("Get Zone Status", userdata);
			return userdata;
		}

		private String makeSendingString() 
		{
			String devID = GetDevID.getDevId(DownloadDataFromServer.this);
			
			ArrayList<String> keyList = new ArrayList<String>();
			keyList.add("userId");
			keyList.add("pwd");
			keyList.add("devID");
			keyList.add("locationCode");

			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add(saleManIDPrefs);
			valueList.add(saleManPwdPrefs);
			valueList.add(devID);
			valueList.add(locationCodePrefs);			 			

			return TLAJsonStringMaker.jsonStringMaker(keyList, valueList);
		}

		@Override
		protected void onPostExecute(String result)
		{			
			super.onPostExecute(result);	

			if(result.contains("Oops"))
			{
				txtFail.setText(txtFail.getText() + "\nDownLoad Zone Info => "+ result + "!!!");
				isSuccessZoneFlag= "false";
				errorMessageDialogShow();
			}
			else if(result.equals(""))
			{			
				txtFail.setText(txtFail.getText() + "\nDownLoad Zone Info => Slow Connection!!!");
				isSuccessZoneFlag = "false";
				errorMessageDialogShow();
			}
			else
			{
				String zoneCode,zoneName;
				isSuccessZoneFlag="false";

				try
				{
					JSONArray jsonResultArr = new JSONArray(result);

					for(int i=0; i<jsonResultArr.length(); i++)
					{
						JSONObject jObj = jsonResultArr.getJSONObject(i);

						zoneCode = jObj.getString("zoneNo");
						zoneName = jObj.getString("zoneName");
						Log.e(zoneCode, "ZoneCode");
						Log.e(zoneName,"ZoneName");
						saveZoneInfoInDB(zoneCode, zoneName);
					}
					
					txtSuccess.setText(txtSuccess.getText() + "\nDownLoad Zone Info => Success");
					pbDownLoad.setProgress(pbDownLoad.getProgress() + 12);
					txtCurrentTask.setText("Download Zone Data Finish ...");
					txtPercent.setText(pbDownLoad.getProgress() + "%");
					isSuccessZoneFlag="true";
					Log.e("download Zone flag",isSuccessZoneFlag);
					GetCustomerCategory gCustomerCategory=new GetCustomerCategory();
					gCustomerCategory.execute();
					
				}
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void saveZoneInfoInDB(String zoneCode,String zoneName) 
		{
			if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
			{
				DBCalss.openDB();
			}

			DBCalss.eliteDB.beginTransaction();

			ContentValues cv = new ContentValues();			
			cv.put("zoneCode",zoneCode);
			cv.put("zoneName", zoneName);
			
			DBCalss.eliteDB.insert("Zone", null, cv);	
			DBCalss.eliteDB.setTransactionSuccessful();	
			DBCalss.eliteDB.endTransaction();	
		}
	}
	
	private class GetCustomerCategory extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			txtCurrentTask.setText("Downloading CustomerCategory Data ...");
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String userdata = "";
			try 
			{				
				URL url = new URL(URL+"cus/getcustomercategorylist");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(13000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(makeSendingString().getBytes());
				outputStream.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					return "Oops. Can't Connect To Server. Error Code : " + conn.getResponseCode() + "";
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String output;
				while ((output = br.readLine()) != null) 
				{				
					userdata = output;
				}
				conn.disconnect();
			}
			catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("Get Customer Category Status", userdata);
			return userdata;
		}

		private String makeSendingString() 
		{
			String devID = GetDevID.getDevId(DownloadDataFromServer.this);

			ArrayList<String> keyList = new ArrayList<String>();
			keyList.add("userId");
			keyList.add("pwd");
			keyList.add("devID");
			keyList.add("locationCode");

			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add(saleManIDPrefs);
			valueList.add(saleManPwdPrefs);
			valueList.add(devID);
			valueList.add(locationCodePrefs);			 			

			return TLAJsonStringMaker.jsonStringMaker(keyList, valueList);
		}

		@Override
		protected void onPostExecute(String result)
		{			
			super.onPostExecute(result);	

			if(result.contains("Oops"))
			{
				txtFail.setText(txtFail.getText() + "\nDownLoad Customer Category Info => "+ result + "!!!");
				isSuccesscustomerCateogoryFlag="false";
				errorMessageDialogShow();
			}
			else if(result.equals(""))
			{			
				txtFail.setText(txtFail.getText() + "\nDownLoad Customer Category Info => Slow Connection!!!");
				isSuccesscustomerCateogoryFlag="false";
				errorMessageDialogShow();
			}
			else
			{
				String customerCategoryID,customerCategoryName;
				isSuccesscustomerCateogoryFlag="false";

				try
				{
					JSONArray jsonResultArr = new JSONArray(result);

					for(int i=0; i<jsonResultArr.length(); i++)
					{
						JSONObject jObj = jsonResultArr.getJSONObject(i);

						customerCategoryID = jObj.getString("customerCagNo");
						customerCategoryName = jObj.getString("customerCagName");
						Log.e(customerCategoryID, "customerCategoryID");
						Log.e(customerCategoryName,"customerCategoryName");
						saveCustomerCategoryToDB(customerCategoryID, customerCategoryName);
					}
					
					txtSuccess.setText(txtSuccess.getText() + "\nDownLoad CustomerCategory  Info => Success");
					pbDownLoad.setProgress(pbDownLoad.getProgress() + 12);
					txtCurrentTask.setText("Download Customer Category Data Finish ...");
					txtPercent.setText(pbDownLoad.getProgress() + "%");
					isSuccesscustomerCateogoryFlag="true";
					Log.e("customerCateogoryFlag",isSuccesscustomerCateogoryFlag);
//					GetProductListForPreOrder getProductforPreOrder=new GetProductListForPreOrder();
//					getProductforPreOrder.execute();
					checkDownloadDataAllSuccess();
//					
				}
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void saveCustomerCategoryToDB(String customerCategoryID,
				String customerCategoryName)
		{
			if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
			{
				DBCalss.openDB();
			}

			DBCalss.eliteDB.beginTransaction();

			ContentValues cv = new ContentValues();			
			cv.put("customerCategoryID",customerCategoryID);
			cv.put("customerCategoryName", customerCategoryName);
			
			DBCalss.eliteDB.insert("CustomerCategory", null, cv);	
			DBCalss.eliteDB.setTransactionSuccessful();	
			DBCalss.eliteDB.endTransaction();	
		}

		private void saveZoneInfoInDB(String zoneCode,String zoneName) 
		{
			if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
			{
				DBCalss.openDB();
			}

			DBCalss.eliteDB.beginTransaction();

			ContentValues cv = new ContentValues();			
			cv.put("zoneCode",zoneCode);
			cv.put("zoneName", zoneName);
			
			DBCalss.eliteDB.insert("Zone", null, cv);	
			DBCalss.eliteDB.setTransactionSuccessful();	
			DBCalss.eliteDB.endTransaction();	
		}
	}
	
	
	private class GetConfirmGet extends AsyncTask<Void, Void, String>
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();			
			txtCurrentTask.setText("Downloading Confirm  Data ...");
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String userdata = "";
			try 			
			{				
				URL url = new URL(URL+"usr/confirmget");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(13000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				OutputStream outputStream = conn.getOutputStream();
				Log.e("Sending GetConfirm To Server", makeSendingString());
				outputStream.write(makeSendingString().getBytes());
				outputStream.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					return "Oops. Can't Connect To Server. Error Code : " + conn.getResponseCode() + "";
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String output;
				while ((output = br.readLine()) != null) 
				{				
					userdata = output;
				}
				conn.disconnect();
			}
			catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("ComfirmGet Status", userdata);
			return userdata;
		}

		private String makeSendingString() 
		{
			String devID = GetDevID.getDevId(DownloadDataFromServer.this);

			ArrayList<String> keyList = new ArrayList<String>();
			keyList.add("userId");
			keyList.add("pwd");
			keyList.add("devID");
			keyList.add("locationCode");

			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add(saleManIDPrefs);
			valueList.add(saleManPwdPrefs);
			valueList.add(devID);
			valueList.add(locationCodePrefs);

			return TLAJsonStringMaker.jsonStringMaker(keyList, valueList);
		}

		@Override
		protected void onPostExecute(String result)
		{			
			super.onPostExecute(result);	


			if(result.contains("Oops"))
			{
				txtFail.setText(txtFail.getText() + "\nConfirmGet Info => "+ result + "!!!");
				errorMessageDialogShow();
			}
			else if(result.equals(""))
			{			
				txtFail.setText(txtFail.getText() + "\nConfirmGet Info => Slow Connection!!!");	
				errorMessageDialogShow();
			}
			else
			{
                  String status = "";				
				try
				{
					JSONObject jObj = new JSONObject(result);
					status = jObj.getString("status");
					if(status.equals("success"))
					{
					  txtSuccess.setText(txtSuccess.getText() + "\nConfirmGet Info => Success");
					  pbDownLoad.setProgress(pbDownLoad.getProgress() + 12);
					  txtCurrentTask.setText("ConfirmGet Finish ...");
					  txtPercent.setText(pbDownLoad.getProgress() + "%");					
					  startActivity(new Intent(DownloadDataFromServer.this, HomePage.class));
					  finish();
					}
				}
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		startActivity(new Intent(DownloadDataFromServer.this, MainActivity.class));
		finish();
	}

	private void registerIDs() 
	{
		pbDownLoad = (ProgressBar) findViewById(R.id.downLoad_pbDownLoad);
		pbDownLoad.setMax(100);	
		btnredownload= (Button) findViewById(R.id.invoiceCustomerList_btnPay);
		txtCurrentTask = (TextView) findViewById(R.id.downLoad_txtCurrentTask);
		txtPercent = (TextView) findViewById(R.id.downLoad_txtPercent);
		txtSuccess = (TextView) findViewById(R.id.downLoad_txtSuccess);
		txtFail = (TextView) findViewById(R.id.downLoad_txtFail);
	}
	
	private void errorMessageDialogShow()	
	{
		final AlertDialog diag3= new AlertDialog.Builder(DownloadDataFromServer.this)
		.setTitle("Alert  Message")
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage("\nDownload Data fail.Please Download Again!\n")		
		.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface diag2, int arg1) 
			{
										
			}
		})	
		.create();

		diag3.show();
	}
}
