package com.example.elite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;

import json.string.maker.TLAJsonStringMaker;

import org.apache.http.client.ClientProtocolException;
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
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import custom.classes.CheckNetworkState;
import custom.classes.DBCalss;
import custom.classes.GetDevID;

public class MainActivity extends Activity 
{
	Button btnLogin;
	EditText edtUserName, edtUserPwd;
	LinearLayout layoutForClearDataUserNameAndPassword;
	EditText edtClearDataUserName, edtClearDataPassword;
	TextView txtClear;
	 AlertDialog d;
	String loginFlag ="";
	SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "SaleManPrefs" ;
	public static final String saleManIdPrefs = "saleManId";
	public static final String saleManNamePrefs = "saleManName";
	public static final String passwordPrefs = "password";
	public static final String locationCodePrefs = "locationCode";
	
	ImageView imageView1;//for manually backup db

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sharedpreferences = getSharedPreferences("SaleManPrefs", Context.MODE_PRIVATE);
		hideScreenKeyboardInPageDisplay();
		registerIDs();		
		catchEvents();
	}
	

	private void hideScreenKeyboardInPageDisplay()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);		
	}

	private void catchEvents()
	{
		//added by HAK for manually backup db
		imageView1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("Backup database")
				.setMessage("Are you sure want to do?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						backupDB();
					}
				})
				.setNegativeButton("No", null)
				.show();
			}
		});
		
		btnLogin.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v) 
			{
				loginFlag = "L";
				String error = checkValidation();

				if(error.equals(""))
				{
					boolean isExisting = checkSaleManInfoExistInDB();
					if(isExisting)
					{
						boolean isLoginSuccess = checkLogin();
						if(isLoginSuccess)
						{
							startActivity(new Intent(MainActivity.this, HomePage.class));
							finish();
						}
						else
						{
							final AlertDialog diag3= new AlertDialog.Builder(MainActivity.this)
							.setTitle("Login Fail")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setMessage("\nSale Man ID or Password Is Wrong.\n")		
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
						Log.e("here", "here");
						
						downLoadSaleManDataFromServer();
					}
				}
				else
				{
					final AlertDialog diag3= new AlertDialog.Builder(MainActivity.this)
					.setTitle("Information")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage("\nPlease Fill "+ error +".\n")		
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

			private String checkValidation()
			{
				if(edtUserName.getText().length() == 0)
				{
					return "Sale Man ID";
				}
				else if(edtUserPwd.getText().length() == 0)
				{
					return "Sale Man Password";
				}
				return "";
			}
			
			
		});
		
		txtClear.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				loginFlag = "C";
				d = new AlertDialog.Builder(MainActivity.this)					
				.setView(userNamePasswordLayout())
				.setTitle("Login")
				.setPositiveButton("Confirm", null)
				.setNegativeButton("Cancel", null)
				.create();
				d.setOnShowListener(new DialogInterface.OnShowListener()
				{
					@Override
					public void onShow(DialogInterface arg0) 
					{
						Button positiveBtn = d.getButton(AlertDialog.BUTTON_POSITIVE);
						positiveBtn.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								String error = checkValidationForClearData();

								if(error.equals(""))
								{
									boolean isExisting = checkSaleManInfoExistInDB();
									if(isExisting)
									{
										Log.e("IsExisting","IsExisting");
										boolean isLoginSuccess = checkLoginForClearData();
										Log.e("LoginStatus",isLoginSuccess+"");
										if(isLoginSuccess)
										{
											final AlertDialog diag= new AlertDialog.Builder(MainActivity.this)
											.setTitle("Warning!")
											.setIcon(android.R.drawable.ic_dialog_alert	)
											.setMessage("Are you sure you want to delete all Data?")
											.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
											{
												@Override
												public void onClick(DialogInterface diag2, int arg1) 
												{
													
														LogOut logout=new LogOut();
														logout.execute();
//													  DBCalss.delUploadData();
//													  deleteAllScreenShots();
														
														backupDB();
														
													  d.dismiss();
												}
											})
											
											.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener()
											{
												@Override
												public void onClick(DialogInterface arg0, int arg1) 
												{
													
												}
											})
											.create();
											diag.show();
											diag.setCancelable(false);
											diag.setCanceledOnTouchOutside(false);
										}
										else
										{
											final AlertDialog diag3= new AlertDialog.Builder(MainActivity.this)
											.setTitle("Login Fail")
											.setIcon(android.R.drawable.ic_dialog_alert)
											.setMessage("\nSale Man ID or Password Is Wrong.\n")		
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
										downLoadSaleManDataFromServer();
									}
								}
								else
								{
									final AlertDialog diag3= new AlertDialog.Builder(MainActivity.this)
									.setTitle("Information")
									.setIcon(android.R.drawable.ic_dialog_alert)
									.setMessage("\nPlease Fill "+ error +".\n")		
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
						});
						
					}
				});
				
				d.show();
			}
		});
	}
	
	public View userNamePasswordLayout()
	{
		LayoutInflater inflater1=(LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row1=inflater1.inflate(R.layout.dymamiclayoutforlogin, null);
		layoutForClearDataUserNameAndPassword=(LinearLayout) row1.findViewById(R.id.llClearData);
		edtClearDataUserName=(EditText)row1.findViewById(R.id.etUserName);
		edtClearDataPassword = (EditText)row1.findViewById(R.id.etPassword);

		return row1;
	}
	
	private boolean checkSaleManInfoExistInDB() 
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}				
		
		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;
		int countRow = 0;

		String[] selCol = {"COUNT(*)"};
		
		cur = DBCalss.eliteDB.query("SaleMan", selCol, null, null, null, null, null);

		int countColIndex = cur.getColumnIndex("COUNT(*)");		

		while(cur.moveToNext())
		{	
			countRow = cur.getInt(countColIndex);
		}

		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
		
		if(countRow == 0)
		{
			return false;
		}
		else
		{
			return true;
		}			
	}


	protected boolean checkLogin()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}
		
		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;
		int countRow = 0;

		String[] selCol = {"saleManID", "saleManPwd", "saleManName", "locationCode"};
		String[] whereArgs = {edtUserName.getText().toString().trim(), edtUserPwd.getText().toString().trim()};
		
		cur = DBCalss.eliteDB.query("SaleMan", selCol, "saleManID LIKE ? AND saleManPwd LIKE ?", whereArgs, null, null, null);

		String saleManID = null, saleManPwd = null, saleManName = null, locationCode = null;		

		while(cur.moveToNext())
		{	
			saleManID = cur.getString(cur.getColumnIndex("saleManID"));
			saleManPwd = cur.getString(cur.getColumnIndex("saleManPwd"));
			saleManName = cur.getString(cur.getColumnIndex("saleManName"));
			locationCode = cur.getString(cur.getColumnIndex("locationCode"));			
		}

		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
		
		if(cur.getCount() == 0)
		{
			return false;
		}
		else
		{
			saveSaleManInfoInStaticVar(saleManID, saleManPwd, saleManName, locationCode);			
			return true;
		}			
	}
	
	protected boolean checkLoginForClearData()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}
		
		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;
		int countRow = 0;

		String[] selCol = {"saleManID", "saleManPwd", "saleManName", "locationCode"};
		String[] whereArgs = {edtClearDataUserName.getText().toString().trim(), edtClearDataPassword.getText().toString().trim()};
		
		cur = DBCalss.eliteDB.query("SaleMan", selCol, "saleManID LIKE ? AND saleManPwd LIKE ?", whereArgs, null, null, null);

		String saleManID = null, saleManPwd = null, saleManName = null, locationCode = null;		

		while(cur.moveToNext())
		{	
			saleManID = cur.getString(cur.getColumnIndex("saleManID"));
			saleManPwd = cur.getString(cur.getColumnIndex("saleManPwd"));
			saleManName = cur.getString(cur.getColumnIndex("saleManName"));
			locationCode = cur.getString(cur.getColumnIndex("locationCode"));			
		}

		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
		
		if(cur.getCount() == 0)
		{
			return false;
		}
		else
		{
			saveSaleManInfoInStaticVar(saleManID, saleManPwd, saleManName, locationCode);			
			return true;
		}			
	}

	private class Login extends AsyncTask<Void, Void, String>
	{
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Login Checking ...");
			progressDialog.show();
			
			Log.e("pre exe", "pre exe");
		}

		@Override
		protected String doInBackground(Void... params)
		{
			String userdata = "";
			try 
			{
				URL url = new URL(DownloadDataFromServer.URL+"usr/checkuserlogin/");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setReadTimeout(3000);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");

				OutputStream outputStream = conn.getOutputStream();
				Log.e("Login Sending To Server", makeSendingString());
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

			Log.e("Login Status", userdata);
			return userdata;
		}

		private String makeSendingString() 
		{
			String saleManID = "";
			String saleManPwd ="";
			if(loginFlag.equals("L"))
			{
				 saleManID = edtUserName.getText().toString().trim();
				 saleManPwd = edtUserPwd.getText().toString().trim();
			}
			else if(loginFlag.equals("C"))		
			{
				 saleManID = edtClearDataUserName.getText().toString().trim();
				 saleManPwd = edtClearDataPassword.getText().toString().trim();
			}
		
			String devID = GetDevID.getDevId(MainActivity.this);

			ArrayList<String> keyList = new ArrayList<String>();
			keyList.add("userId");
			keyList.add("pwd");
			keyList.add("devID");
			keyList.add("userGroup");

			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add(saleManID);
			valueList.add(saleManPwd);
			valueList.add(devID);
		//	valueList.add("SaleMan");   
			valueList.add("CASHIER");

			return TLAJsonStringMaker.jsonStringMaker(keyList, valueList);
		}

		@Override
		protected void onPostExecute(String result)
		{			
			super.onPostExecute(result);	

			progressDialog.dismiss();
			
			if(result.contains("Oops"))
			{
				final AlertDialog diag3 = new AlertDialog.Builder(MainActivity.this)
				.setTitle("Information")
				.setMessage("\n"+ result + "\n")	
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface diag2, int arg1) 
					{

					}
				})
				.setNegativeButton("No", new android.content.DialogInterface.OnClickListener()
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
			else if(result.equals(""))
			{
				final AlertDialog diag3 = new AlertDialog.Builder(MainActivity.this)
				.setTitle("Information")
				.setMessage("\nSlow Connection.\n")	
				.setIcon(android.R.drawable.ic_dialog_alert)
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
				String status, saleManID, saleManPwd, saleManName, locationCode;

				try
				{
					JSONObject jObj = new JSONObject(result);
					status = jObj.getString("status");

					if(status.equalsIgnoreCase("success"))
					{
						saleManID = jObj.getString("userId");
						saleManPwd = jObj.getString("pwd");
						saleManName = jObj.getString("userName");
						locationCode = jObj.getString("locationCode");

						saveSaleManInfoInDB(saleManID, saleManPwd, saleManName, locationCode);
						saveSaleManInfoInStaticVar(saleManID, saleManPwd, saleManName, locationCode);
						
						if(loginFlag.equals("L"))
						{
							startActivity(new Intent(MainActivity.this, DownloadDataFromServer.class));
							finish();
						}
						else if(loginFlag.equals("C"))
						{
							final AlertDialog diag= new AlertDialog.Builder(MainActivity.this)
							.setTitle("Warning!")
							.setIcon(android.R.drawable.ic_dialog_alert	)
							.setMessage("Are you sure you want to delete all Data?")
							.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface diag2, int arg1) 
								{
									  DBCalss.delUploadData();
									  deleteAllScreenShots();
									  deleteAllSignImage();
									  d.dismiss();
								}
							})
							
							.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface arg0, int arg1) 
								{
									
								}
							})
							.create();
							diag.show();
							diag.setCancelable(false);
							diag.setCanceledOnTouchOutside(false);
						}
						
					}
					else
					{
						final AlertDialog diag3 = new AlertDialog.Builder(MainActivity.this)
						.setTitle("Information")
						.setIcon(android.R.drawable.ic_dialog_alert)
						//.setMessage("\nLogin Unsuccessful. Please Try Again.\n")	
						.setMessage("\n Because of " + status + "\n")
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
				catch (JSONException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void saveSaleManInfoInDB(String saleManID, String saleManPwd, String saleManName, String locationCode)
		{
			if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
			{
				DBCalss.openDB();
			}
			
			DBCalss.eliteDB.beginTransaction();
			
			ContentValues cv = new ContentValues();			

			cv.put("saleManID", saleManID.trim());
			cv.put("saleManPwd", saleManPwd.trim());	
			cv.put("saleManName", saleManName.trim());
			cv.put("locationCode", locationCode.trim());

			DBCalss.eliteDB.insert("SaleMan", null, cv);	
			
			DBCalss.eliteDB.setTransactionSuccessful();	
			DBCalss.eliteDB.endTransaction();	
		}
	}

	private void saveSaleManInfoInStaticVar(String saleManID, String saleManPwd, String saleManName, String locationCode)
	{
		SharedPreferences.Editor editor = sharedpreferences.edit();        
        editor.putString(saleManIdPrefs, saleManID);
        editor.putString(saleManNamePrefs,saleManName);
        editor.putString(passwordPrefs, saleManPwd);
        editor.putString(locationCodePrefs, locationCode);
        editor.commit();
	}
	
	private void registerIDs()
	{
		btnLogin = (Button) findViewById(R.id.login_btnLogin);
		edtUserName = (EditText) findViewById(R.id.login_userName);
		edtUserPwd = (EditText) findViewById(R.id.login_userPwd);
		txtClear = (TextView)findViewById(R.id.clrData);
		txtClear.setPaintFlags(txtClear.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		
		imageView1 = (ImageView) findViewById(R.id.imageView1);
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

	private void downLoadSaleManDataFromServer() 
	{
		if(CheckNetworkState.isNetworkStatusAvialable(MainActivity.this)==true)
		{
			Login login = new Login();
			login.execute();
		}
		else
		{
			Toast.makeText(getApplicationContext(),	"No Internet Connection", Toast.LENGTH_SHORT).show();
		}
	}
	
	private String checkValidationForClearData()
	{
		if(edtClearDataUserName.getText().length() == 0)
		{
			return "Sale Man ID";
		}
		else if(edtClearDataPassword.getText().length() == 0)
		{
			return "Sale Man Password";
		}
		return "";
	}
	
	private class LogOut extends AsyncTask<Void, Void, String>
	{
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Login Checking ...");
			progressDialog.show();
			
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
				conn.setReadTimeout(3000);
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

			String devID = GetDevID.getDevId(MainActivity.this);

			ArrayList<String> keyList = new ArrayList<String>();
			keyList.add("userId");
			keyList.add("pwd");
			keyList.add("devID");
			keyList.add("locationCode");

			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add(saleManIdPrefs);
			valueList.add(passwordPrefs);
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
				Toast.makeText(MainActivity.this,"Error in Clear Data Process" , 1).show();
			}
			else if(result.equals(""))
			{			
				Log.e("Contain", "Empty String");	
				Toast.makeText(MainActivity.this,"Error in Clear Data Process" , 1).show();
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

					  backupDB();//added by hak for better development
					  
					  DBCalss.delUploadData();
					  deleteAllScreenShots();
					  deleteAllSignImage();
					  finish();
					}
					else
					{
						Toast.makeText(MainActivity.this,"Data Clear Process is Successfully!" , 1).show();
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
}
