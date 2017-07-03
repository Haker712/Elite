package com.example.elite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import bean.classes.Discount;
import bean.classes.SaleProduct;
import bean.classes.VolumeDiscount;
import custom.classes.CheckNetworkState;
import custom.classes.DBCalss;
import custom.classes.GetDevID;

public class HomePage extends Activity {
	Button btnCustomer, btnAddNewCustomer, btnSaleReturn, btnUploadServer,
			btnPayment, btnReport, btnReissue;
	TextView txtTodayDate, txtUserName;
	SimpleDateFormat fmtForTodayStr = new SimpleDateFormat("yyyy-MM-dd");

	static ArrayList<Discount> itemDiscountList = new ArrayList<Discount>();
	static ArrayList<VolumeDiscount> volumeDiscountList = new ArrayList<VolumeDiscount>();
	ArrayList<SaleProduct> productListFromDB = new ArrayList<SaleProduct>();
	int discountAmtIntforEachItem = 0;
	static double discountPercent;
	static int volumeDiscount;
	static int itemDiscount = 0;
	double itemPriceInt = 0;
	String saleManIDPrefs = null;
	String saleManNamePrefs = null;
	String saleManPwdPrefs = null;	
	String locationCodePrefs = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage);
		SharedPreferences prefs = getSharedPreferences("SaleManPrefs", MODE_PRIVATE); 
		saleManIDPrefs = prefs.getString(MainActivity.saleManIdPrefs, "No name defined");
		saleManNamePrefs = prefs.getString(MainActivity.saleManNamePrefs,"No name defined" );
		saleManPwdPrefs = prefs.getString(MainActivity.passwordPrefs, "No name defined");	
		locationCodePrefs = prefs.getString(MainActivity.locationCodePrefs, "No name defined");	
		registerIDs();
		setSaleManNameAndTodayDate();
		getAllItemDiscountFromDB();
		getVolumeDisFromDB();
		catchEvents();
	}

	private void getAllItemDiscountFromDB() {
		if (DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen()) {
			DBCalss.openDB();
		}
		itemDiscountList.clear();
		DBCalss.eliteDB.beginTransaction();
		Cursor curDis;
		String[] selColDis = { "stockNo", "DiscountPercent",
				"StartDiscountQty", "EndDiscountQty" };
		curDis = DBCalss.eliteDB.query("Discount", selColDis, null, null, null,
				null, null);

		while (curDis.moveToNext()) {
			String productID = curDis.getString(curDis
					.getColumnIndex("stockNo"));
			String discountPercent = curDis.getString(curDis
					.getColumnIndex("DiscountPercent"));
			String startDiscountQty = curDis.getString(curDis
					.getColumnIndex("StartDiscountQty"));
			String endDiscountQty = curDis.getString(curDis
					.getColumnIndex("EndDiscountQty"));

			Discount disClass = new Discount();
			disClass.setstockNo(productID);
			disClass.setdiscountPercent(discountPercent);
			disClass.setstartDisQty(startDiscountQty);
			disClass.setendDisQty(endDiscountQty);
			itemDiscountList.add(disClass);
			Log.e(itemDiscountList.size() + "", "DicountArraySize");
			Log.e(disClass.getstockNo(), "StockNo");
			Log.e(disClass.getdiscountPercent(), "DisPercent");
		}
		curDis.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}

	public static void calculateItemDiscount(String productID,
			String discountType, int saleQty, String sellingPrice) {
		Log.e("ITemDiscount", "ItemDiscount");
		Log.e(productID, "ProductIDItemDis");
		Log.e(discountType, "DiscountTypeItemDis");
		Log.e(saleQty + "", "SaleQtyItemDis");
		Log.e(sellingPrice + "", "SellingPriceItemDis");
		int discountAmtIntforEachItem = 0;

		double itemPriceInt = 0;

		for (Discount dis : itemDiscountList) {
			Log.e(dis.getstockNo(), "StockNo");
			if (dis.getstockNo().equals(productID)) 
			{
				Log.e("Here1", "Here1");
				if (discountType.equals("I"))
				{
					Log.e("Here2", "Here2");
					if (saleQty >= Integer.parseInt(dis.getstartDisQty())
							&& saleQty <= Integer.parseInt(dis.getendDisQty())) 
					{
						Log.e("Here3", "Here3");
						Log.e(dis.getdiscountPercent(), "Disocuntpercent");
						String dis_amountStr = dis.getdiscountPercent();
						discountPercent = Double.parseDouble(dis_amountStr);

						itemPriceInt = Double.parseDouble(sellingPrice);
						Log.e("ItemPriceInt", itemPriceInt + "");
						Log.e("DiscountAmtInt", discountPercent + "");
						if (itemPriceInt == 1) 
						{
							discountAmtIntforEachItem = (int) (itemPriceInt * saleQty);
							itemDiscount = (int) (Math
									.round(discountAmtIntforEachItem
											* discountPercent) / 100.00);
							Log.e("HomePage.discountAmtInt1234", itemDiscount
									+ "");
							break;
						} else {
							discountAmtIntforEachItem = (int) Math
									.round((itemPriceInt * discountPercent) / 100.00);
							Log.e("DisAmtForOneItem", discountAmtIntforEachItem
									+ "");
							Log.e("SaleQty", saleQty + "");
							itemDiscount = discountAmtIntforEachItem * saleQty;
							Log.e("HomePage.discountAmtInt1234", itemDiscount
									+ "");
							break;
						}
					} else {
						Log.e("ELSE", "ELSE");
						itemDiscount = 0;
					}
				} else {
					Log.e("Else12", "Else12");
					itemDiscount = 0;
				}
			} else {
				Log.e("Else3", "Else3");
				itemDiscount = 0;
			}
		}
	}

	private void getVolumeDisFromDB() {
		if (DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen()) {
			DBCalss.openDB();
		}
		volumeDiscountList.clear();
		DBCalss.eliteDB.beginTransaction();
		Cursor cur;

		String[] selCol = { "fromAmount", "toAmount", "percent" };
		cur = DBCalss.eliteDB.query("VolumeDiscount", selCol, null, null, null,
				null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				VolumeDiscount vDiscount = new VolumeDiscount();
				int fromAmount = Integer.parseInt(cur.getString(
						cur.getColumnIndex("fromAmount")).trim());
				int toAmount = Integer.parseInt(cur.getString(
						cur.getColumnIndex("toAmount")).trim());
				Double percent = Double.parseDouble(cur.getString(
						cur.getColumnIndex("percent")).trim());
				vDiscount.setPercent(percent);
				vDiscount.setFromAmount(fromAmount);
				vDiscount.setToAmount(toAmount);
				volumeDiscountList.add(vDiscount);
			}
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}

	public static void calculateVolumeDiscount(int totalAmtofVolumeDisItems,
			int totalAmt) {
		if (totalAmt != 0) {
			Log.e("Test..", "loop...");
			Log.e("VolumeDiscsoutListSize",volumeDiscountList.size() +"");
			if (volumeDiscountList.size() > 0) {
				for (int j = 0; j < volumeDiscountList.size(); j++) {
					double percent = 0;

					Integer fromAmount = volumeDiscountList.get(j)
							.getFromAmount();
					Integer toAmount = volumeDiscountList.get(j).getToAmount();

					percent = volumeDiscountList.get(j).getPercent();

					if (totalAmtofVolumeDisItems >= fromAmount
							&& totalAmtofVolumeDisItems <= toAmount) {
						if (percent != 0) {
							volumeDiscount = (int) Math
									.round((totalAmtofVolumeDisItems * percent) / 100);
						} else {
							volumeDiscount = 0;
						}
						break;
					} else
					{
						volumeDiscount = 0;
					}

				}
			}
		}
	}

	private void setSaleManNameAndTodayDate() 
	{
		Calendar todayCal = Calendar.getInstance();
		String todayDate = fmtForTodayStr.format(todayCal.getTime());

		txtTodayDate.setText(txtTodayDate.getText() + todayDate);
		txtUserName.setText(txtUserName.getText()
				+ saleManNamePrefs);
	}

	private void catchEvents() {
		btnCustomer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomePage.this, Customer.class));
				finish();
			}
		});

		btnAddNewCustomer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(HomePage.this, AddNewCustomer.class));
				finish();
			}
		});

		btnPayment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// startActivity(new Intent(HomePage.this,Payment.class));
				// finish();
			}
		});

		btnSaleReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// startActivity(new Intent(HomePage.this,SaleReturn.class));
				// finish();
			}
		});

		btnUploadServer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final AlertDialog diag3 = new AlertDialog.Builder(HomePage.this)
				.setTitle("Alert  Message")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("\nAre you sure you want to upload data to server?\n")
				.setPositiveButton(
						"Yes",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface diag2,
									int arg1) 
							{
								startActivity(new Intent(HomePage.this,
										UploadDataToServer.class));
								finish();
							}
						})
				.setNegativeButton(
						"No",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface diag2,
									int arg1) {
							}
						}).create();

		       diag3.show();
			}
		});
		btnReissue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final AlertDialog diag3 = new AlertDialog.Builder(HomePage.this)
						.setTitle("Alert  Message")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage("\nAre You Sure Download Again?\n")
						.setPositiveButton(
								"Yes",
								new android.content.DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface diag2,
											int arg1) {
										redownloadProductList();
									}
								})
						.setNegativeButton(
								"No",
								new android.content.DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface diag2,
											int arg1) {
									}
								}).create();

				diag3.show();
			}
		});

		btnReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(HomePage.this, Report.class));
				finish();
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		final AlertDialog diag3 = new AlertDialog.Builder(this)
				.setTitle("Log Out")
				.setMessage("\nDo you want to logout?\n")
				.setPositiveButton("Yes",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface diag2, int arg1) {
								startActivity(new Intent(HomePage.this,
										MainActivity.class));
								finish();
							}
						})
				.setNegativeButton("No",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface diag2, int arg1) {
							}
						}).create();

		diag3.show();
		diag3.setCancelable(false);
		diag3.setCanceledOnTouchOutside(false);
	}

	private void registerIDs() {
		btnCustomer = (Button) findViewById(R.id.home_btnCustomer);
		btnAddNewCustomer = (Button) findViewById(R.id.home_btnAddNewCustomer);
		btnSaleReturn = (Button) findViewById(R.id.home_btnSaleReturn);
		txtUserName = (TextView) findViewById(R.id.home_txtUserName);
		txtTodayDate = (TextView) findViewById(R.id.home_txtTodayDate);
		btnUploadServer = (Button) findViewById(R.id.home_btnUploadServer);
		btnPayment = (Button) findViewById(R.id.home_btnPayment);
		btnReport = (Button) findViewById(R.id.home_btnReport);
		btnReissue = (Button) findViewById(R.id.btnReissue);
		discountPercent = 0;
		itemDiscount = 0;
		volumeDiscount = 0;
		
			
	}


	private void redownloadProductList()
	{
		if(CheckNetworkState.isNetworkStatusAvialable(HomePage.this)==true)
			{
				GetProductListForSale sVAC = new GetProductListForSale();
				sVAC.execute();
			}
		else
		{
			Toast.makeText(getApplicationContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
		}
	}
	private void getAllProductFromDB()
	{
		if (DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen()) 
		{
			DBCalss.openDB();
		}
		DBCalss.eliteDB.beginTransaction();
		Cursor cur;
	
	String[] selCol = {"categoryId", "categoryName", "groupId","groupName", "productId", "productName", "totalQty","RemainingQty","SaleQty","sellingPrice", "purchasePrice", "groupName","discountType"};
	cur = DBCalss.eliteDB.query("Product", selCol, null, null, null, null, null);
	while(cur.moveToNext())
	{
		String categoryIDfromDB = cur.getString(cur.getColumnIndex("categoryId"));
		String categoryNameFromDB = cur.getString(cur.getColumnIndex("categoryName"));
		String groupIdfromDB = cur.getString(cur.getColumnIndex("groupId"));
		String groupNameFromDB = cur.getString(cur.getColumnIndex("groupName"));
		String productIdfromDB = cur.getString(cur.getColumnIndex("productId"));
		String productNamefromDB = cur.getString(cur.getColumnIndex("productName"));
		String totalQtyfromDB = cur.getString(cur.getColumnIndex("totalQty"));
		String remainingQtyfromDB = cur.getString(cur.getColumnIndex("RemainingQty"));
		
		SaleProduct sp = new SaleProduct();
		sp.setCategoryId(categoryIDfromDB);
		sp.setCategoryName(categoryNameFromDB);
		sp.setGroupId(groupIdfromDB);
		sp.setGroupName(groupNameFromDB);
		sp.setProductId(productIdfromDB);
		sp.setProductName(productNamefromDB);
		sp.setTotalQty(totalQtyfromDB);
		sp.setRemainingQty(remainingQtyfromDB);
		productListFromDB.add(sp);
		Log.e("ArrayListSizeFromDatabase",productListFromDB.size()+"");
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	private class GetProductListForSale extends AsyncTask<Void, Void, String>
	
	{
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
		}

	@Override
	protected String doInBackground(Void... params)
		{
			String userdata = "";
		try 
			{
				URL url = new URL(DownloadDataFromServer.URL+"pro/getproductlist");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(10000);
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
		
	String saleManID = saleManIDPrefs;
	String saleManPwd = saleManPwdPrefs;
	String devID = GetDevID.getDevId(HomePage.this);
	String locationCode = locationCodePrefs;

	ArrayList<String> keyList = new ArrayList<String>();
	keyList.add("userId");
	keyList.add("pwd");
	keyList.add("devID");
	keyList.add("locationCode");

	ArrayList<String> valueList = new ArrayList<String>();
	valueList.add(saleManID);
	valueList.add(saleManPwd);
	valueList.add(devID);
	valueList.add(locationCode);

	return TLAJsonStringMaker.jsonStringMaker(keyList, valueList);
	}

	@Override
	protected void onPostExecute(String result)
	{
	super.onPostExecute(result);

	if(result.contains("Oops"))
		{
		errorMessageDialogShow();
		}
	else if(result.equals(""))
		{
		errorMessageDialogShow();
		}
	else
		{
		String categoryID, categoryName, groupID, groupName, productID, productName, totalQty, sellingPrice, purchasePrice,discountType;
	try
		{
		getAllProductFromDB();
		JSONArray jsonResultArr = new JSONArray(result);
		
		for(int i=0; i<jsonResultArr.length(); i++)
			{
				Log.e(jsonResultArr.length() + "","JsonLength");
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
		
				checkReIssueProductDuplicateOrNew(categoryID,categoryName,groupID, groupName, productID, productName, totalQty, sellingPrice, purchasePrice,discountType);
				
			}
		Toast.makeText(HomePage.this, "Successfully Save Re Issues",1).show();
		}
	catch (JSONException e) 
	{
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	}
	}
	
	private void checkReIssueProductDuplicateOrNew(String categoryID,String categoryName, String groupID, String groupName, String productID, String productName, String totalQty, String sellingPrice, String purchasePrice,String discountType) 
	{
		int addedtotalQty =0;
		int addedtotalremainingQty=0;
		String returnStatus="";
		
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}
		
		DBCalss.eliteDB.beginTransaction();
		
		Log.e(productListFromDB.size() +"","ProductListArraySize");
		for(SaleProduct sp : productListFromDB)
		{
			Log.e("REIssueLoop","ReIssueLoop");
			Log.e("CategoryIDFromDB",sp.getCategoryId());
			Log.e("CategoryIDFromServer",categoryID);
			Log.e("CategoryNameFromDB",sp.getCategoryName());
			Log.e("CategoryNameFromServer",categoryName);
			Log.e("GroupIDFromDB",sp.getGroupId());
			Log.e("GroupIDFromServer",groupID);
			Log.e("GroupNameFromDB",sp.getGroupName());
			Log.e("GroupNameFromServer",groupName);
			Log.e("ProductIDFromDB",sp.getProductId());
			Log.e("ProductIDFromSErver",productID);
			Log.e("ProductNameFromDB",sp.getProductName());
			Log.e("ProductNameFromSErver",productName);
			if(categoryID.equalsIgnoreCase(sp.getCategoryId()) && groupID.equalsIgnoreCase(sp.getGroupId()) && productID.equalsIgnoreCase(sp.getProductId()))
			{
				returnStatus= "duplicate";
				addedtotalQty = Integer.parseInt(sp.getTotalQty());
				addedtotalremainingQty = Integer.parseInt(sp.getRemainingQty());
				Log.e("RETURNSTATUS",returnStatus);
				break;
			}
		}
		
		if(returnStatus.equals("duplicate"))
		{
			Log.e("DuplicateProduct","DuplicateProduct");
			addedtotalQty += Integer.parseInt(totalQty);
			addedtotalremainingQty += Integer.parseInt(totalQty);
			Log.e(productID,"CHECKPRODUCT");
			
			String args[] ={productID};
			
			ContentValues cv = new ContentValues();

			cv.put("totalQty",addedtotalQty);
			cv.put("RemainingQty",addedtotalremainingQty);
			Log.e("Update","Update");
			DBCalss.eliteDB.update("Product", cv, "productId = ?", args);
			String sql ="Update Product set totalQty ='"+addedtotalQty+ "', RemainingQty ='"+addedtotalremainingQty+"' WHERE productId LIKE '"+ productID+"'"; 
			DBCalss.eliteDB.execSQL(sql);
		}
		else
		{
			Log.e("NewProduct","NewProduct");
			
			ContentValues cv1 = new ContentValues();
			Log.e("CATID",categoryID);
			Log.e("CATNAME",categoryName);
			Log.e("GRDID",groupID);
			Log.e("GRDNAME",groupName);
			cv1.put("categoryId",categoryID);
			cv1.put("categoryName",categoryName);
			cv1.put("groupId",groupID);
			cv1.put("groupName",groupName);
			cv1.put("productId",productID);
			cv1.put("productName",productName);
			cv1.put("totalQty",totalQty);
			cv1.put("sellingPrice",sellingPrice);
			cv1.put("purchasePrice",purchasePrice);
			cv1.put("discountType",discountType);
			cv1.put("SaleQty","0");
			cv1.put("RemainingQty",totalQty);
			
			DBCalss.eliteDB.insert("Product", null, cv1);
		}
		
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	}
	private void errorMessageDialogShow() 
	{
		final AlertDialog diag3 = new AlertDialog.Builder(HomePage.this)
				.setTitle("Alert  Message")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("\nDownload Data fail.Please Download Again!\n")
				.setPositiveButton("OK",
						new android.content.DialogInterface.OnClickListener() 
				{
							@Override
							public void onClick(DialogInterface diag2, int arg1) 
							{
								
							}
				}).create();

		diag3.show();
	}
}
