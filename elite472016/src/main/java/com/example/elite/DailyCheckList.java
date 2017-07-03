package com.example.elite;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bean.classes.CustomerInfo;
import bean.classes.DailyCheck;
import custom.classes.DBCalss;

public class DailyCheckList extends Activity{
	
	ArrayList<DailyCheck> dailyCheckArrayList = new ArrayList<DailyCheck>();
	
	ArrayList<String> mysterylist = new ArrayList<String>();
	ArrayList<String> levelList = new ArrayList<String>();
	
	private TextView shopNameTxt;
	private TextView dealerTxt;
	private TextView regionTxt;
	private TextView dateTxt;
	private TextView checkingTimeTxt;
	ListView dailyCheckListView;
	Button doneBtn;
	
	DailyCheckListAda dailyCheckListAda;
	
	//for GetView
	private Button levelBtn;
	private String levelStr;
	private Button remarkBtn;
	DailyCheck dailyCheck;
	
	//Dyanmic Layout
	EditText remarkEdit;
	private AlertDialog alert;
	//for dcheckNo;
	String dcheckNo;
	DecimalFormat dcheckFormat = new DecimalFormat("000");  
	SimpleDateFormat fmtForTodayStr = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");
	String date,checktime;	
	
	//Shop Data
	private String shopNameStr;
	private String shopAddressStr;
	private String shopRegionStr;
	
	String saleManIDPrefs = null;
	String saleManNamePrefs = null;
	String saleManPwdPrefs = null;	
	String locationCodePrefs = null;
	
	//for levleBtn
	CharSequence[] level = {"POOR", "AVERAGE", "GOOD","VERY GOOD","EXCELLENT"};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daily_checklist_activity);
		
		SharedPreferences prefs = getSharedPreferences("SaleManPrefs", MODE_PRIVATE); 
		saleManIDPrefs = prefs.getString(MainActivity.saleManIdPrefs, "No name defined");
		saleManNamePrefs = prefs.getString(MainActivity.saleManNamePrefs,"No name defined" );
		saleManPwdPrefs = prefs.getString(MainActivity.passwordPrefs, "No name defined");	
		locationCodePrefs = prefs.getString(MainActivity.locationCodePrefs, "No name defined");	
		
		mysterylist.add("How is MPT Signage?");
		mysterylist.add("Using MPT Vingl or not?");
		mysterylist.add("Removing MPT advertisement or not?");
		mysterylist.add("Did they get any promotion material from MPT?");
		mysterylist.add("How is KPIs?");
		mysterylist.add("How is MPT SIM sale?");
		mysterylist.add("While customer buying mobile, what kind of SIM card do they choice?");
		mysterylist.add("Customer interesting level to MPT?");
		mysterylist.add("Do they have well product knowledge or not?");
		mysterylist.add("If Any kind of MPT promotion, customer interesting or not?");
		mysterylist.add("How is the network converage situation?");
		mysterylist.add("How is competitor SIM sale?");
		mysterylist.add("Can handle customer complain?");
		
		levelList.add("POOR");
		levelList.add("AVERAGE");
		levelList.add("GOOD");
		levelList.add("VERYGOOD");
		levelList.add("EXCELLENT");
		
		Calendar todayCal = Calendar.getInstance();
		date = fmtForDueStr.format(todayCal.getTime());
		checktime = fmtForTodayStr.format(todayCal.getTime());
		
		getShopFromDB();
		makedCheckNo();
				
		dailyCheckListView = (ListView) findViewById(R.id.dailychecklist);
		shopNameTxt = (TextView) findViewById(R.id.shop_name_txt);
		shopNameTxt.setText(shopNameStr+","+shopAddressStr);
		dealerTxt = (TextView) findViewById(R.id.dealer_txt);
		dealerTxt.setText(locationCodePrefs);
		regionTxt = (TextView) findViewById(R.id.region_txt);
		regionTxt.setText(dcheckNo);
		dateTxt = (TextView) findViewById(R.id.date_txt);
		dateTxt.setText(date);
		checkingTimeTxt = (TextView) findViewById(R.id.checking_time_txt);
		checkingTimeTxt.setText(checktime);
		doneBtn = (Button) findViewById(R.id.dailycheck_doneBtn);
		
		for(int i=0;i<mysterylist.size();i++){
			dailyCheck = new DailyCheck();
			dailyCheck.setMysteryshoppingtask(mysterylist.get(i));
			dailyCheck.setSituation("YES");
			dailyCheck.setLevel(0);
			dailyCheck.setRemark("remark");	
			dailyCheckArrayList.add(dailyCheck);
		}
		
		catchEvents();
	}
	
	private void makedCheckNo() 
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}		
		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		
		int count = 0;
		String[] selCol = {"COUNT(*)"};
		cur = DBCalss.eliteDB.query("DailyCheckTable", selCol, null, null, null, null, null);
		while(cur.moveToNext())
		{	
			count = cur.getInt(cur.getColumnIndex("COUNT(*)"));
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
		dcheckNo = saleManIDPrefs+ dcheckFormat.format(count + 1);
	}
	
	private void getShopFromDB(){
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
			{
				DBCalss.openDB();
			}		
			DBCalss.eliteDB.beginTransaction();	
			String[] selCol={"customerID","customerName","Address"};
			String[] arg={CustomerInfo.customer_ID};
			Cursor cur;
			cur=DBCalss.eliteDB.query("Customer", selCol, "customerID LIKE ?", arg, null, null,null ,null );
			while(cur.moveToNext())
			{
				shopNameStr=cur.getString(cur.getColumnIndex("customerName"));
				shopAddressStr=cur.getString(cur.getColumnIndex("Address"));				
			}
			cur.close();
			DBCalss.eliteDB.setTransactionSuccessful();
			DBCalss.eliteDB.endTransaction();
	}
	
	private void saveDailyCheckListData(){
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();
				
		//for(int j=0;j<dailyCheckArrayList.size();j++){
		for(DailyCheck dailyCheck:dailyCheckArrayList){
			ContentValues cv = new ContentValues();	
			//DailyCheck dailyCheck = dailyCheckArrayList.get(j);
			cv.put("dcheckNo", dcheckNo);
			cv.put("shopId",CustomerInfo.customer_ID);	
			cv.put("shopName", shopNameStr+","+shopAddressStr);
			cv.put("dealer",locationCodePrefs);
			cv.put("region","Zone1");
			cv.put("date", date);			
			cv.put("checkTime",checktime);
			cv.put("mysterytask",dailyCheck.getMysteryshoppingtask());
			cv.put("situation",dailyCheck.getSituation());
			if(dailyCheck.getSituation().equals("YES")){
				String str = (String) level[dailyCheck.getLevel()];
				cv.put("level",str);
			}else{
				cv.put("level","NULL");
			}			
			cv.put("remark",dailyCheck.getRemark());
			DBCalss.eliteDB.insert("DailyChecklist", null, cv);			
			}		
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	private void saveDailyCheckData(){
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();
		
		ContentValues cv = new ContentValues();	
		cv.put("checkno", dcheckNo);
		Log.e(dcheckNo, "CheckNO");
		cv.put("checkdate", date);
		Log.e(date, "CheckDate");
		cv.put("checkingtime", checktime);
		Log.e(checktime, "CheckTime");
		cv.put("shopname", shopNameStr+","+shopAddressStr);
		cv.put("location", locationCodePrefs);
		cv.put("shopstate", "ZONE 1");
		
		DBCalss.eliteDB.insert("DailyCheckTable", null, cv);	
		
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	private void catchEvents(){
		dailyCheckListAda = new DailyCheckListAda(DailyCheckList.this);
		dailyCheckListView.setAdapter(dailyCheckListAda);
		
		doneBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				saveDailyCheckData();
				saveDailyCheckListData();
				Toast.makeText(getApplicationContext(), "Daily Checklist Saving Successfully", Toast.LENGTH_LONG).show();
				startActivity(new Intent(DailyCheckList.this, HomePage.class));
				finish();
			}
		});
	}	

	public class DailyCheckListAda extends ArrayAdapter<DailyCheck>{
		private final Activity context;

		public DailyCheckListAda(Activity context)
		{
			super(context, R.layout.daily_check_list_row, dailyCheckArrayList);
			this.context = context; 			
		}
		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.daily_check_list_row, null, true);

			TextView mysteryTxt=(TextView) rowView.findViewById(R.id.check_list_mystery);
			CheckBox situationCheck = (CheckBox) rowView.findViewById(R.id.situation_check);
			levelBtn = (Button) rowView.findViewById(R.id.levelBtn);
			remarkBtn = (Button) rowView.findViewById(R.id.remark_btn);
			final DailyCheck dcheck = dailyCheckArrayList.get(position);
		
			mysteryTxt.setText(dcheck.getMysteryshoppingtask());
			levelBtn.setText(level[dcheck.getLevel()]);
			levelBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(dcheck.getSituation().equals("YES")){						
					
			        AlertDialog.Builder alt_bld = new AlertDialog.Builder(DailyCheckList.this);
			        alt_bld.setSingleChoiceItems(level, -1, new DialogInterface
			        		.OnClickListener() {
			            public void onClick(DialogInterface dialog, int item) {
			            	
			            	dcheck.setLevel(item);
			            	dailyCheckListAda.notifyDataSetChanged();
			            	levelBtn.setText(level[item]);			            	
			            	
//			                Toast.makeText(getApplicationContext(),
//			                    "Phone Model = "+level[item], Toast.LENGTH_SHORT).show();			               
					        alert.dismiss();
			            }
			        });
			        alert = alt_bld.create();
			        alert.show();
					}else{
						AlertDialog.Builder alt_bld = new AlertDialog.Builder(DailyCheckList.this);
						alt_bld.setMessage("Please check your situation!");
						alt_bld.create();
						alt_bld.show();
					}
				}
			});
			remarkBtn.setText(dcheck.getRemark());
			
			if(dcheck.getSituation().equals("YES"))
			{ 
				situationCheck.setChecked(true);	
			}
			else
			{
				situationCheck.setChecked(false);
				levelBtn.setText("NULL");
			}
			situationCheck.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(android.widget.CompoundButton buttonView,
						boolean isChecked) 
				{
					if(isChecked)
					{
						dcheck.setSituation("YES");
						levelBtn.setText(level[dcheck.getLevel()]);
						Toast.makeText(DailyCheckList.this, "check "+dcheck.getSituation(), Toast.LENGTH_LONG).show();						
					}
					else
					{
						dcheck.setSituation("NO");
						levelBtn.setText("NULL");
						Toast.makeText(DailyCheckList.this, "check "+dcheck.getSituation(), Toast.LENGTH_LONG).show();						
					}
					dailyCheckListAda.notifyDataSetChanged();
				}
			});

			remarkBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final AlertDialog d = new AlertDialog.Builder(DailyCheckList.this)					
					.setView(dialyRemarkDynamicLayout())
					.setTitle("Remark")
					.setPositiveButton("Confirm", null)
					.setNegativeButton("Cancel", null)
					.setCancelable(false)
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

									
									String remarkText= "";
									remarkText = remarkEdit.getText().toString().trim();
																										
									if(remarkText.equals(""))
									{
										Log.e("HERE","HERE");
										 AlertDialog diag3= new AlertDialog.Builder(DailyCheckList.this)
											.setTitle("Alert Message!")
											.setMessage("\nPlease Fill Remark\n")		
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
									else{
										dcheck.setRemark(remarkText);
										dailyCheckListAda.notifyDataSetChanged();
										remarkBtn.setText(dcheck.getRemark());
										Toast.makeText(DailyCheckList.this, "Hello"+remarkText, Toast.LENGTH_LONG).show();
										d.dismiss();
									}
								}
							});
							
							Button negativeButton = d.getButton(AlertDialog.BUTTON_NEGATIVE);
							negativeButton.setOnClickListener(new OnClickListener()
							{
								
								@Override
								public void onClick(View arg0) 
								{
									d.dismiss();
								}
							});
							
						}
					});
					
					d.show();
				}
			});
					
			return rowView;
		}				
	}
	
	public View dialyRemarkDynamicLayout(){
		LayoutInflater inflater1=(LayoutInflater)DailyCheckList.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=inflater1.inflate(R.layout.dynamiclayout_dailyremark, null);
		
		remarkEdit = (EditText) view.findViewById(R.id.remark_edit);		
		
		return view;
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(DailyCheckList.this, Customer.class));
		finish();
	}
}
