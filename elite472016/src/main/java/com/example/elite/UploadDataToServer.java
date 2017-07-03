package com.example.elite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

import json.string.maker.TLAJsonStringMaker;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import bean.classes.NewCustomerInfo;
import custom.classes.CheckNetworkState;
import custom.classes.DBCalss;
import custom.classes.GetDevID;

public class UploadDataToServer extends Activity{	
	ProgressBar pbDownLoad; 
	TextView txtSuccess, txtPercent, txtCurrentTask, txtFail;
	Timer timer;
	String isSuccessNewCustomerUpload="false",isSuccessSaleDataUpload="false",isSuccessCreditCollectionDataUpload="false",isSuccessPreOrderDataUpload="false",isSuccessDeliveryDataUpload= "false",isSuccessRemainingProductUpload = "false";
	int progress = 12;
	String status = "";
	Button btnReUpload,btnCancel;
	private int readTime = 1000;
		
	String saleManIDPrefs = null;
	String saleManNamePrefs = null;
	String saleManPwdPrefs = null;	
	String locationCodePrefs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_data_page);
		
		SharedPreferences prefs = getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE); 
		saleManIDPrefs = prefs.getString(MainActivity.saleManIdPrefs, "No name defined");
		saleManNamePrefs = prefs.getString(MainActivity.saleManNamePrefs,"No name defined" );
		saleManPwdPrefs = prefs.getString(MainActivity.passwordPrefs, "No name defined");	
		locationCodePrefs = prefs.getString(MainActivity.locationCodePrefs, "No name defined");	
		
		registerIDs();
		uploadData();
		catchEvents();
	}
	
	
	private void catchEvents() {
		btnReUpload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				reupload();
			}
			
		});
		
		btnCancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(UploadDataToServer.this, HomePage.class));
				finish();
			}
		});		
	}
    private void checkuploadAllSuccess()
    {
    	if(CheckNetworkState.isNetworkStatusAvialable(UploadDataToServer.this)==true)
		{
    		LogOut logout = new LogOut();
			logout.execute();		
		}
		else
		{		
			  showAlertDialog(UploadDataToServer.this, "No Internet Connection",
						"You don't have internet connection.", false);
		}							
    }
    
	private void reupload()
	{
		if(isSuccessNewCustomerUpload.equals("false"))
		{
			if(CheckNetworkState.isNetworkStatusAvialable(UploadDataToServer.this)==true)
			{
				SaveAddNewCustomer sVAC2 = new SaveAddNewCustomer();
				sVAC2.execute();	
			}
			else
			{
				  showAlertDialog(UploadDataToServer.this, "No Internet Connection",
							"You don't have internet connection.", false);
			}				
		}
		else if(isSuccessSaleDataUpload.equals("false"))
		{
			if(CheckNetworkState.isNetworkStatusAvialable(UploadDataToServer.this)==true)
			{
				SaveSaleProductList sSPL2 = new SaveSaleProductList();
				sSPL2.execute();
			}
			else
			{
				  showAlertDialog(UploadDataToServer.this, "No Internet Connection",
							"You don't have internet connection.", false);
			}				
		}	
		else if(isSuccessRemainingProductUpload.equals("false"))
		{
			if(CheckNetworkState.isNetworkStatusAvialable(UploadDataToServer.this)==true)
			{
				SendRemainingProductToServer srps = new SendRemainingProductToServer();
				srps.execute();
			}
			else
			{
				  showAlertDialog(UploadDataToServer.this, "No Internet Connection",
							"You don't have internet connection.", false);
			}				
		}
	}
	
	private void uploadData()
	{
		if(CheckNetworkState.isNetworkStatusAvialable(UploadDataToServer.this)==true)
		{
			SaveAddNewCustomer sVAC = new SaveAddNewCustomer();
			sVAC.execute();			
		}
		else
		{
			  showAlertDialog(UploadDataToServer.this, "No Internet Connection",
						"You don't have internet connection.", false);
		}	
	}
	private void registerIDs() 
	{
		pbDownLoad = (ProgressBar) findViewById(R.id.upLoad_pbDownLoad);
		pbDownLoad.setMax(100);		
		txtCurrentTask = (TextView) findViewById(R.id.upLoad_txtCurrentTask);
		txtPercent = (TextView) findViewById(R.id.upLoad_txtPercent);
		txtSuccess = (TextView) findViewById(R.id.upLoad_txtSuccess);
		txtFail = (TextView) findViewById(R.id.upLoad_txtFail);
		btnReUpload = (Button) findViewById(R.id.invoiceCustomerList_btnPay);
		btnCancel = (Button) findViewById(R.id.report_btnHome);		
	}
	private class SaveAddNewCustomer extends AsyncTask<Void, Void, String>
	{
		private ProgressDialog progressDialog;
		ArrayList<NewCustomerInfo> customerInfo = new ArrayList<NewCustomerInfo>();//valueLit
		private int count = 0;

		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			progressDialog = new ProgressDialog(UploadDataToServer.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Sending To Server ...");
			progressDialog.show();

			getAllNewCustomerInDB();
		}

		private void getAllNewCustomerInDB() 
		{
			if(!DBCalss.eliteDB.isOpen())
			{
				DBCalss.openDB();
			}
			DBCalss.eliteDB.beginTransaction();	
			Cursor cur;			

			String[] selCol = {"CustomerID","name", "phone", "address", "contactPerson","zone","CustomerCategoryID"};			

			cur = DBCalss.eliteDB.query("NewCustomer", selCol, null, null, null, null, null);
			int idColIndex= cur.getColumnIndex("CustomerID");
			int nameColIndex = cur.getColumnIndex("name");		
			int phoneColIndex = cur.getColumnIndex("phone");			
			int addressColIndex = cur.getColumnIndex("address");		
			int contactPersonColIndex = cur.getColumnIndex("contactPerson");		
			int zoneColIndex=cur.getColumnIndex("zone");
			int customerCategoyrIndex=cur.getColumnIndex("CustomerCategoryID");
		
			String idStr,nameStr, phoneStr, addressStr, contactPerson,zone,customercategory;

			while(cur.moveToNext())
			{	
				idStr=cur.getString(idColIndex);
				Log.e(idStr, "SendCustomerName");
				nameStr = cur.getString(nameColIndex);
				phoneStr = cur.getString(phoneColIndex);
				addressStr = cur.getString(addressColIndex);
				contactPerson = cur.getString(contactPersonColIndex);
				zone=cur.getString(zoneColIndex);
				customercategory=cur.getString(customerCategoyrIndex);

				NewCustomerInfo nCInfo = new NewCustomerInfo(idStr,nameStr, phoneStr, addressStr, contactPerson,zone,customercategory);
				customerInfo.add(nCInfo);
			}
			
			Cursor curCount;		
			
			String[] selCount = {"COUNT(*)"};
			curCount = DBCalss.eliteDB.query("NewCustomer", selCount, null, null, null, null, null);

			while(cur.moveToNext())
			{	
				count = cur.getInt(cur.getColumnIndex("COUNT(*)"));
			}
			
			curCount.close();
			cur.close();
			DBCalss.eliteDB.setTransactionSuccessful();
			DBCalss.eliteDB.endTransaction();
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String sendingString = "";
			String userdata = "";
			
			try 
			{
				URL url = new URL(DownloadDataFromServer.URL +"cus/savenewcus");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(readTime*count);//change 3000 to 5000
				conn.setConnectTimeout(readTime*count);//change 3000 to 5000
//				conn.setReadTimeout(5000);//change 3000 to 5000
//				conn.setConnectTimeout(5000);//change 3000 to 5000
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				OutputStream outputStream = conn.getOutputStream();
				Log.e(customerInfo.size() + "", "BBBB");
				if(customerInfo.size() == 0)
				{
					sendingString = "[]";
				}
				else
				{
					sendingString = makeSendingString(customerInfo);
				}

				Log.e("Sending Add New Customer To Server", sendingString);
				outputStream.write(sendingString.getBytes());//NewCustomer
				outputStream.flush();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
				{
					return "Oops.Can't Connect To Server. Error Code : " + conn.getResponseCode() + "";
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String output;
				while ((output = br.readLine()) != null) 
				{				
					userdata = output;
					Log.e(userdata, "NewCustomer userdata");
				}
				conn.disconnect();
				Log.e(userdata, "NewCustomer userdata Disconnect");
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
			Log.e("Add New Customer Status", userdata);
			return userdata;
		}

		private String makeSendingString(ArrayList<NewCustomerInfo> customerInfo2) 
		{
			ArrayList<String> keyList = new ArrayList<String>();
			keyList.add("customerID");
			keyList.add("customerName");
			keyList.add("phone");
			keyList.add("address");
			keyList.add("userId");
			keyList.add("contactPerson");
			keyList.add("zone");
			keyList.add("customerCagNo");

			ArrayList<ArrayList<String>> valueList = new ArrayList<ArrayList<String>>();

			for(int i=0; i<customerInfo2.size(); i++)
			{
				ArrayList<String> customerInfoList = new ArrayList<String>();
				NewCustomerInfo nCInfo = customerInfo2.get(i);
				customerInfoList.add(nCInfo.getid());
				customerInfoList.add(nCInfo.getName());
				customerInfoList.add(nCInfo.getPhone());
				customerInfoList.add(nCInfo.getAddress());
				customerInfoList.add(saleManIDPrefs);
				customerInfoList.add(nCInfo.getContactPerson());
				customerInfoList.add(nCInfo.getZone());
				customerInfoList.add(nCInfo.getCustomerCategory());
				Log.e(nCInfo.getid(),"ID");
				Log.e(nCInfo.getName(),"Name");
				Log.e(nCInfo.getPhone(),"Phone");
				Log.e(nCInfo.getZone(),"Zone");
				Log.e(nCInfo.getCustomerCategory(),"CustomerCategory");
				valueList.add(customerInfoList);
				Log.e(customerInfoList.size()+"","SendingArraySize");
			}

			return TLAJsonStringMaker.jsonArrayStringMaker(keyList, valueList);
		}

		@Override
		protected void onPostExecute(String result)
		{			
			super.onPostExecute(result);	

			progressDialog.dismiss();

			if(result.contains("Oops"))
			{
				txtFail.setText(txtFail.getText() + "\nSending Add New Customer To Server => "+ result + "!!!");	
				isSuccessNewCustomerUpload="false";
				errorMessageDialogShow();
			}
			else if(result.equals(""))
			{
				txtFail.setText(txtFail.getText() + "\n Sending Add New Customer Info To Server => Slow Connection!!!");	
				isSuccessNewCustomerUpload="false";
				errorMessageDialogShow();
			}
			else
			{
				String status;
				JSONArray duplicateIdListJsonArray;
				try
				{
					JSONObject jObj = new JSONObject(result);
					status = jObj.getString("status");			
					duplicateIdListJsonArray = jObj.getJSONArray("duplicateIdList");
					if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
					{
						DBCalss.openDB();
					}

					DBCalss.eliteDB.beginTransaction();

					for (int i = 0; i < duplicateIdListJsonArray.length(); i++) {
				        JSONObject idjJsonObject = duplicateIdListJsonArray.getJSONObject(i);				        
				        String duplicateId = idjJsonObject.getString("id");		
				        String arg[]= {duplicateId};
						ContentValues cv=new ContentValues();					
						cv.put("DuplicateId", "YES");
						DBCalss.eliteDB.update("NewCustomer", cv, "CustomerID LIKE ?", arg);
					}
					 DBCalss.eliteDB.setTransactionSuccessful();
				     DBCalss.eliteDB.endTransaction();
		            
				   
					if(status.equalsIgnoreCase("success"))
					{
						isSuccessNewCustomerUpload="true";
						txtSuccess.setText(txtSuccess.getText() + "\nSaving New Customer Info => Success");
						pbDownLoad.setProgress(pbDownLoad.getProgress() + 20);
						txtCurrentTask.setText("Saving Customer Data Finish ...");
						txtPercent.setText(pbDownLoad.getProgress() + "%");
						
						if(CheckNetworkState.isNetworkStatusAvialable(UploadDataToServer.this)==true)
						{
							SaveSaleProductList sSPL = new SaveSaleProductList();
							sSPL.execute();							
						}
						else
						{	
							  showAlertDialog(UploadDataToServer.this, "No Internet Connection",
										"You don't have internet connection.", false);
						}	
					}
					else
					{
						isSuccessNewCustomerUpload="false";
						txtFail.setText(status);
						errorMessageDialogShow();
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
	
	private class SaveSaleProductList extends AsyncTask<Void, Void, String>
	{
		private int count =0;
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();	

			//	getAllSaleDataFromDB();
		}	
		
		private String getAllSaleDataFromDB() 
		{			
			String resultJSonString = "[{";

			if(!DBCalss.eliteDB.isOpen())
			{
				DBCalss.openDB();
			}

			DBCalss.eliteDB.beginTransaction();	
			Cursor cur, innerCur;			

			String[] selCol = {"customerID", "saleDate","refundAmt","invoiceID", "totalAmtNoDiscount", "TotalVolandItemDisAmt", "payAmt", "receitpPersonName", "signImg","InvoiceImg","salePersonID", "dueDate", "cashOrCredit", "locationCode", "devID", "invoiceTime"};			

			cur = DBCalss.eliteDB.query("SaleData", selCol, null, null, null, null, null);

			String customerID, saleDate, invoiceID, receiptPersonName, signImg , salePersonID, dueDate, cashOrCredit, locationCode, devID, invoiceTime;
			String invoiceImg;
			int totalAmtNoDiscount, discountAmt, payAmt, refundAmt;

			while(cur.moveToNext())
			{	
				customerID = cur.getString(cur.getColumnIndex("customerID"));
				saleDate = cur.getString(cur.getColumnIndex("saleDate"));
				invoiceID = cur.getString(cur.getColumnIndex("invoiceID"));
				totalAmtNoDiscount = cur.getInt(cur.getColumnIndex("TotalAmtNoDiscount"));
				discountAmt = cur.getInt(cur.getColumnIndex("TotalVolandItemDisAmt"));
				payAmt = cur.getInt(cur.getColumnIndex("payAmt"));
				receiptPersonName = cur.getString(cur.getColumnIndex("receitpPersonName"));
				signImg = cur.getString(cur.getColumnIndex("signImg"));		
				invoiceImg = cur.getString(cur.getColumnIndex("InvoiceImg"));		
				salePersonID = cur.getString(cur.getColumnIndex("salePersonID"));
				dueDate = cur.getString(cur.getColumnIndex("dueDate"));				
				cashOrCredit = cur.getString(cur.getColumnIndex("cashOrCredit"));
				locationCode = cur.getString(cur.getColumnIndex("locationCode"));
				devID = cur.getString(cur.getColumnIndex("devID"));
				invoiceTime = cur.getString(cur.getColumnIndex("invoiceTime"));
				refundAmt = cur.getInt(cur.getColumnIndex("refundAmt"));
				Log.e("SalePersonID", salePersonID);
				Log.e("dueDate",dueDate);
				Log.e("cashorCredit",cashOrCredit);
				Log.e("LocationCode",locationCode);
				Log.e("devID",devID);
				Log.e("InvoiceTime",invoiceTime);
				Log.e("TotalItemandVolumeDisssssss",discountAmt+"");

				resultJSonString += "\"customerID\":\"" + customerID + "\",\"saleDate\":\"" + saleDate + "\",\"invoiceID\":\"" + invoiceID + "\",\"totalAmt\":" + totalAmtNoDiscount + ",\"totalDiscountAmt\":" + discountAmt + ",\"payAmt\":" + payAmt + ",\"refundAmt\":" + refundAmt + ",\"receitpPersonName\":\"" + receiptPersonName + "\",\"signImg\":\"" + signImg + "\",\"invoiceImg\":\"" + invoiceImg + "\",\"salePersonID\":\"" + salePersonID + "\",\"dueDate\":\"" + dueDate + "\",\"cashOrCredit\":\"" + cashOrCredit + "\",\"locationCode\":\"" + locationCode + "\",\"devID\":\"" + devID + "\",\"invoiceTime\":\"" + invoiceTime + "\",\"saleProduct\":[{";
				
				String[] selColDetail = {"productID", "saleQty", "salePrice", "purchasePrice", "discountAmt", "totalAmt", "discountPercent","isFoc","remark"};			
				String[] whereArgs = {invoiceID};

				Log.e("invoide id", invoiceID);

				innerCur = DBCalss.eliteDB.query("SaleDataDetail", selColDetail, "invoiceID LIKE ?", whereArgs, null, null, null);

				String productID,isFoc,remark;
				int saleQty, salePrice, purchasePrice, discountAmtDetail, totalAmtDetail;
				double discountPercent;

				while(innerCur.moveToNext())
				{	
					productID = innerCur.getString(innerCur.getColumnIndex("productID"));
					saleQty = innerCur.getInt(innerCur.getColumnIndex("saleQty"));
					salePrice = innerCur.getInt(innerCur.getColumnIndex("salePrice"));
					purchasePrice = innerCur.getInt(innerCur.getColumnIndex("purchasePrice"));
					discountAmtDetail = innerCur.getInt(innerCur.getColumnIndex("discountAmt"));
					totalAmtDetail = innerCur.getInt(innerCur.getColumnIndex("totalAmt"));
					discountPercent= innerCur.getDouble(innerCur.getColumnIndex("discountPercent"));
					isFoc= innerCur.getString(innerCur.getColumnIndex("isFoc"));
					remark = innerCur.getString(innerCur.getColumnIndex("remark"));
					
					Log.e("FOC CHECK", isFoc+"@@@@@@@@@");
					

					resultJSonString += "\"productID\":\"" + productID + "\",\"saleQty\":" + saleQty + ",\"salePrice\":" + salePrice + ",\"purchasePrice\":" + purchasePrice + ",\"discountAmt\":" + discountAmtDetail + ",\"totalAmt\":" + totalAmtDetail +",\"discountPercent\":" + discountPercent + ",\"isFOC\":\"" + isFoc.toString() + "\",\"remark\":\"" + remark.toString() + "\"},{";
					
					//resultJSonString += "\"productID\":\"" + productID + "\",\"saleQty\":" + saleQty + ",\"salePrice\":" + salePrice + ",\"purchasePrice\":" + purchasePrice + ",\"discountAmt\":" + discountAmtDetail + ",\"totalAmt\":" + totalAmtDetail +",\"discountPercent\":" + discountPercent + ",\"isFOC\":\"" + isFoc.toString() + "\"},{";
				

					Log.i("DATAFORSALEDETAIL>>", resultJSonString);
				}

				innerCur.close();	

				resultJSonString = resultJSonString.substring(0, resultJSonString.length() - 2);		// remove ,
				resultJSonString += "]},{";
			}

			resultJSonString = resultJSonString.substring(0, resultJSonString.length() - 2);		// remove ,
			resultJSonString += "]";
			
			Cursor curCount;		
			
			String[] selCount = {"COUNT(*)"};
			curCount = DBCalss.eliteDB.query("SaleData", selCount, null, null, null, null, null);

			while(cur.moveToNext())
			{	
				count = cur.getInt(cur.getColumnIndex("COUNT(*)"));
			}
			
			curCount.close();
			cur.close();
			DBCalss.eliteDB.setTransactionSuccessful();
			DBCalss.eliteDB.endTransaction();

			return resultJSonString;
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String sendingStr = "";
			String userdata = "";

			try 
			{
				String resultJsonString = getAllSaleDataFromDB();
								
				writeToFile(resultJsonString);

				URL url = new URL(DownloadDataFromServer.URL + "sale/savesaledata");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(readTime*count);
				conn.setConnectTimeout(readTime*count);			
//				conn.setReadTimeout(150000);
//				conn.setConnectTimeout(150000);		
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				OutputStream outputStream = conn.getOutputStream();

				if(!resultJsonString.contains("customerID"))
				{
					sendingStr = "[]";
				}
				else
				{
					sendingStr = resultJsonString;
				}
				
				JSONArray jsonArray = new JSONArray();
				try {
					jsonArray = new JSONArray(sendingStr);	
					Log.e("Sending Sale Data to Server",jsonArray+"");
				} catch (Exception e) {
					e.printStackTrace();
				}

				Log.e("Sending Sale Data to Server",sendingStr);
				outputStream.write(jsonArray.toString().getBytes());
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
					Log.e(userdata, "SaveSaleProductList userdata");
				}
				conn.disconnect();
				Log.e(userdata, "SaveSaleProductList userdata Disconnect");
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

			Log.e("Save Sale Data Status", userdata);
			return userdata;		 
		}


		private void writeToFile(String resultJsonString) 
		{
			String DB_PATH = "data/data/com.example.elite/databases/data.txt";
			
			
			PrintWriter pw;
			try 
			{
				pw = new PrintWriter(new FileOutputStream(new File(DB_PATH)), true);
				pw.println(resultJsonString);

				// Close the streams
				pw.flush();
				pw.close();
			}
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(String result)
		{			
			super.onPostExecute(result);

			Log.e("Save Sale Data Return", result);

			if(result.contains("Oops"))
			{
				isSuccessSaleDataUpload="false";
				txtFail.setText(txtFail.getText() + "\nSending Sale Data to Server => "+ result + "!!!");
				errorMessageDialogShow();
			}
			else if(result.equals(""))
			{
				isSuccessSaleDataUpload="false";
				txtFail.setText(txtFail.getText() + "\nSending Sale Data to Server => Slow Connection!!!");	
				errorMessageDialogShow();
			}
			else
			{
				String status;	
				JSONArray duplicateIdListJsonArray;
				try
				{					
					JSONObject jObj = new JSONObject(result);
					status = jObj.getString("status");		
					duplicateIdListJsonArray = jObj.getJSONArray("duplicateIdList");
					if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
					{
						DBCalss.openDB();
					}

					DBCalss.eliteDB.beginTransaction();

					for (int i = 0; i < duplicateIdListJsonArray.length(); i++) {
				        JSONObject idjJsonObject = duplicateIdListJsonArray.getJSONObject(i);				        
				        String duplicateId = idjJsonObject.getString("id");		
				        String arg[]= {duplicateId};
						ContentValues cv=new ContentValues();					
						cv.put("DuplicateId", "YES");
						DBCalss.eliteDB.update("SaleData", cv, "invoiceID LIKE ?", arg);
					}
					 DBCalss.eliteDB.setTransactionSuccessful();
				     DBCalss.eliteDB.endTransaction();
					
					if(status.equalsIgnoreCase("success"))
					{
						isSuccessSaleDataUpload="true";
						txtSuccess.setText(txtSuccess.getText() + "\nSaving SaleProductList Info => Success");
						pbDownLoad.setProgress(pbDownLoad.getProgress() + 20);
						txtCurrentTask.setText("Saving SaleProductList Data Finish ...");
						txtPercent.setText(pbDownLoad.getProgress() + "%");	
						
						if(CheckNetworkState.isNetworkStatusAvialable(UploadDataToServer.this)==true)
						{
							SendRemainingProductToServer sendRemain = new SendRemainingProductToServer();
							sendRemain.execute();			
						}
						else
						{
							showAlertDialog(UploadDataToServer.this, "No Internet Connection",
										"You don't have internet connection.", false);
						}							
					}
					else if(status.equalsIgnoreCase("fail")){
						Toast.makeText(UploadDataToServer.this, "Upload send fail.", Toast.LENGTH_LONG).show();
					}
					else
					{
						isSuccessSaleDataUpload="false";
						txtFail.setText(status);
						errorMessageDialogShow();
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
		
	private class SendRemainingProductToServer extends AsyncTask<Void, Void, String>
	{
		private int count = 0;
		
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();	
		}	

		private String getAllRemainingProductFromDB() 
		{			
			String resultJSonString = "{";

			if(!DBCalss.eliteDB.isOpen())
			{
				DBCalss.openDB();
			}

			DBCalss.eliteDB.beginTransaction();	
			Cursor cur;			

			String[] selCol = {"productId", "productName", "RemainingQty","sellingPrice"};			

			cur = DBCalss.eliteDB.query("Product", selCol, null, null, null, null, null);

			String productId, productName,devID;
			int remainingQty,salePrice;
			devID = GetDevID.getDevId(getApplicationContext());
			
			
			resultJSonString += "\"salePersonID\":\"" + saleManIDPrefs+ "\",\"locationCode\":\"" + locationCodePrefs + "\",\"devID\":\"" + devID +"\",\"productlist\":[{";
			
			while(cur.moveToNext())
			{	
					productId = cur.getString(cur.getColumnIndex("productId"));
					productName = cur.getString(cur.getColumnIndex("productName"));
					remainingQty = cur.getInt(cur.getColumnIndex("RemainingQty"));
					salePrice = cur.getInt(cur.getColumnIndex("sellingPrice"));
				

					resultJSonString += "\"productID\":\"" + productId + "\",\"productName\":\"" + productName + "\",\"remainingQty\":"+ remainingQty+",\"sellingPrice\":" + salePrice+ "},{";

					Log.i("Data", resultJSonString);
				
			}		

			resultJSonString = resultJSonString.substring(0, resultJSonString.length() - 2);		// remove ,
			resultJSonString += "]}";
			
			Cursor curCount;		
			
			String[] selCount = {"COUNT(*)"};
			curCount = DBCalss.eliteDB.query("Product", selCount, null, null, null, null, null);

			while(cur.moveToNext())
			{	
				count = cur.getInt(cur.getColumnIndex("COUNT(*)"));
			}
			
			curCount.close();
			cur.close();
			DBCalss.eliteDB.setTransactionSuccessful();
			DBCalss.eliteDB.endTransaction();
			
			Log.e("Remaining Products JSON", resultJSonString);

			return resultJSonString;
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String sendingStr = "";
			String userdata = "";

			try 
			{
				String resultJsonString = getAllRemainingProductFromDB();
				
				writeToFile(resultJsonString);

				URL url = new URL(DownloadDataFromServer.URL + "sale/saveremainingdata");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(readTime*count);
				conn.setConnectTimeout(readTime*count);
//				conn.setReadTimeout(5000);
//				conn.setConnectTimeout(5000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				OutputStream outputStream = conn.getOutputStream();

				if(!resultJsonString.contains("productID"))
				{
					sendingStr = "{}";
				}
				else
				{
					sendingStr = resultJsonString;
				}

				Log.e("Sending Remaining Products to Server",sendingStr);
				outputStream.write(sendingStr.getBytes());
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
					Log.e(userdata, "SendRemainingProductToServer userdata");
				}
				conn.disconnect();
				Log.e(userdata, "SendRemainingProductToServer userdata Disconnect");
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

			Log.e("Save Remaining Product Status", userdata);
			return userdata;		 
		}


		private void writeToFile(String resultJsonString) 
		{
			String DB_PATH = "data/data/com.example.elite/databases/data.txt";
			
			
			PrintWriter pw;
			try 
			{
				pw = new PrintWriter(new FileOutputStream(new File(DB_PATH)), true);
				pw.println(resultJsonString);

				// Close the streams
				pw.flush();
				pw.close();
			}
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(String result)
		{			
			super.onPostExecute(result);

			Log.e("Save Remaining Products to Server", result);

			if(result.contains("Oops"))
			{
				isSuccessRemainingProductUpload="false";
				txtFail.setText(txtFail.getText() + "\nSending Remaining Products to Server => "+ result + "!!!");
				errorMessageDialogShow();
			}
			else if(result.equals(""))
			{
				isSuccessRemainingProductUpload="false";
				txtFail.setText(txtFail.getText() + "\nSending Remaining Products to Server => Slow Connection!!!");	
				errorMessageDialogShow();
			}
			else
			{
				String status;				
				try
				{					
					JSONObject jObj = new JSONObject(result);
					status = jObj.getString("status");				
					
					if(status.equalsIgnoreCase("success"))
					{
						isSuccessRemainingProductUpload="true";
						txtSuccess.setText(txtSuccess.getText() + "\nSaving Remaining Products To Server => Success");
						pbDownLoad.setProgress(pbDownLoad.getProgress() + 20);
						txtCurrentTask.setText("Saving Remaining Products Finish ...");
						txtPercent.setText(pbDownLoad.getProgress() + "%");		
						
						checkuploadAllSuccess();
						
					}	
				else
					{
						isSuccessRemainingProductUpload="false";
						txtFail.setText(status);
						errorMessageDialogShow();
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

	
	private class LogOut extends AsyncTask<Void, Void, String>
	{
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			progressDialog = new ProgressDialog(UploadDataToServer.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Login Checking ...");
			progressDialog.show();
			
			txtCurrentTask.setText("LogOut ...");
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String userdata = "";
			try 
			{	
				URL url = new URL(DownloadDataFromServer.URL+"usr/logout");  
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(readTime*5);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				OutputStream outputStream = conn.getOutputStream();
				Log.e("Logout", makeSendingString());
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

			Log.e("LogOut", userdata);
			return userdata;
		}

		private String makeSendingString() 
		{			
			String devID = GetDevID.getDevId(UploadDataToServer.this);
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

			progressDialog.dismiss();
			Log.e("Resutlt", result+"HAH");
			Log.e("Logout","Post Execute");

			if(result.contains("Oops"))
			{
				Log.e("Contain", "Oops");
				txtFail.setText(txtFail.getText() + "\nLogOut => "+ result + "!!!");
			
				
			}
			else if(result.equals(""))
			{			
				Log.e("Contain", "Empty String");
				txtFail.setText(txtFail.getText() + "\nLogOut => Slow Connection!!!");				
			}
			else
			{
				String status = "";
				
				try
				{
					JSONObject jObj = new JSONObject(result);
					status = jObj.getString("status");
					
					Log.e("STATUS", status);
					
					if(status.equals("success"))
					{
						Log.e("In Process", "Checking DB BK");
					  txtSuccess.setText(txtSuccess.getText() + "\nLogOut => Success");
					  pbDownLoad.setProgress(pbDownLoad.getProgress() + 30);
					  txtCurrentTask.setText("LogOut Finish ...");
					  txtPercent.setText(pbDownLoad.getProgress() + "%");
					  
					  Log.e("Before Backup DB", "ELite");
					  // db backUp					  
					  backupDB();
					  
					  DBCalss.delUploadData();
					  deleteAllScreenShots();
					  deleteAllSignImage();
					  startActivity(new Intent(UploadDataToServer.this,MainActivity.class));
					  finish();
					}
					else
					{
						txtFail.setText(status);
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
	
	@SuppressLint("SdCardPath")
	private void backupDB() 
	{
		Calendar now = Calendar.getInstance();
		String today = now.get(Calendar.DATE) + "." + (now.get(Calendar.MONTH) + 1)
				+ "." + now.get(Calendar.YEAR);
	    try {
	        File sd = Environment.getExternalStorageDirectory();
	        File data = Environment.getDataDirectory();
	       
	        if(sd.exists())
	        {
	        	 Toast.makeText(getApplicationContext(), "SD card is Inserted to Device",
		                    Toast.LENGTH_SHORT).show();
	        }
	   
	        if (sd.canWrite()) 
	        {	        	
	        	Toast.makeText(getApplicationContext(), "SD Can Write Started",
	                    Toast.LENGTH_SHORT).show();
	            String currentDBPath = "/data/com.example.elite/databases/elite.db"; 
	           
	            String backupDBPath = "Elitebackup"+today+".db";
	            File currentDB = new File(data, currentDBPath);
	            
	            String folderPath="mnt/sdcard/ElitedbBackup";
	            File f= new File(folderPath);
	            f.mkdir();
	            Log.e("AA", "aa");
	            File backupDB = new File(f, backupDBPath);
	            Log.e("BB", "bb");
	            FileChannel src = new FileInputStream(currentDB).getChannel();
	            Log.e("CC", "cc");
	            FileChannel dst = new FileOutputStream(backupDB).getChannel();
	            Log.e("DD", "dd");
	            dst.transferFrom(src, 0, src.size());
	            Log.e("EE", "ee");
	            src.close();
	            dst.close();
	            Log.e("FF", "ff");
	            Toast.makeText(getApplicationContext(), "Backup Successful!",
	                    Toast.LENGTH_SHORT).show();

	        }
	    }
	    catch(Exception e)
	    {
	    	Toast.makeText(getApplicationContext(), "Cannot backup",
                    Toast.LENGTH_SHORT).show();
	    }
	
	}
	
	private void deleteAllScreenShots()
	{
		Log.e("Delete","Delete");
		 File sdCard = Environment.getExternalStorageDirectory();
		File file=new File(sdCard.getAbsolutePath() + "/ScreenShot/");
		file.mkdirs();
		Log.e(file + "", "filePath");
		if( file.isDirectory())
		{
			Log.e("Directory", "Directory");
			try {
				removeDirectory(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		boolean isDeleteorNot=file.delete();
		Log.e(isDeleteorNot+"", "ReturnStatus");
		file.deleteOnExit();
	}
	
	private void deleteAllSignImage()
	{
		Log.e("SignImgDelete","SignImgDelete");
		 File sdCard = Environment.getExternalStorageDirectory();
		File file=new File(sdCard.getAbsolutePath() + "/SignImage/");
		file.mkdirs();
		Log.e(file + "", "filePath");
		if( file.isDirectory())
		{
			Log.e("Directory", "Directory");
			try {
				removeDirectory(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		boolean isDeleteorNot=file.delete();
		Log.e(isDeleteorNot+"", "ReturnStatusSign");
		file.deleteOnExit();
	}
	
	private static void removeDirectory(File directory) throws IOException
	{
	    File[] files = directory.listFiles();

	    //
	    // for all items in the directory...
	    //
	    for (int n = 0; n < files.length; ++n) {
	      File nextFile = files[n];

	      //
	      // if it's a directory, delete sub-directories and files before
	      // removing the empty directory
	      //
	      if (nextFile.isDirectory())
	        removeDirectory(nextFile);

	      //
	      // otherwise just delete the file - do NOT prune the directory
	      // in advance
	      //
	      else
	        removeFile(nextFile);
	    }

	    //
	    // now that everything's gone, delete the specified directory
	    //
	    if (!directory.delete()) {
	      Object[] filler = { directory.getAbsolutePath() };
	      String message = "DeleteFailed";
	      throw new IOException(message);
	    }
	  }
	
	private static void removeFile(File file) throws IOException
	{
	    //
	    // make sure the file exists, then delete it
	    //

	    if (!file.exists())
	      throw new FileNotFoundException(file.getAbsolutePath());

	    if (!file.delete()) {
	      Object[] filler = { file.getAbsolutePath() };
	      String message = "DeleteFailed";
	      throw new IOException(message);
	    }
	  }
	private void errorMessageDialogShow()	
	{
		final AlertDialog diag3= new AlertDialog.Builder(UploadDataToServer.this)
		.setTitle("Alert  Message")
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage("\nUpload Data fail.Please Upload Again!\n")		
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
	public void showAlertDialog(Context context, String title, String message, Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		alertDialog.setTitle(title);
		alertDialog.setMessage(message);		
		alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}

}
