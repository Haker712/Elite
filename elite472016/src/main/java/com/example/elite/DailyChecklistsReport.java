package com.example.elite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import bean.classes.DailyCheckingDetails;
import custom.classes.DBCalss;

public class DailyChecklistsReport extends Activity{

	public static String chooseCheckNo,chooseShopName,chooseDealer,chooseState,chooseCheckingTime;
	private TextView shopNameTxt;
	private TextView dealerTxt;
	private TextView stateTxt;
	private TextView dateTxt;
	private TextView checkingTimeTxt;
	private ListView listView;
	
	private String date;
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");
	private ArrayList<DailyCheckingDetails> detailsArrayList = new ArrayList<DailyCheckingDetails>();
	private DailyCheckDetailsAda dailyCheckDetailsAda;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daily_check_lists_activity);
		
		shopNameTxt = (TextView) findViewById(R.id.shop_name_txt);
		dealerTxt = (TextView) findViewById(R.id.dealer_txt);
		stateTxt = (TextView) findViewById(R.id.region_txt);
		dateTxt = (TextView) findViewById(R.id.date_txt);
		checkingTimeTxt = (TextView) findViewById(R.id.checking_time_txt);
		listView = (ListView) findViewById(R.id.dailychecklist);
		
		Calendar todayCal = Calendar.getInstance();
		date = fmtForDueStr.format(todayCal.getTime());
		
		shopNameTxt.setText(chooseShopName);
		dealerTxt.setText(chooseDealer);
		stateTxt.setText(chooseState);
		dateTxt.setText(date);
		checkingTimeTxt.setText(chooseCheckingTime);
		
		getDailyCheckDetailsFromDB();
		dailyCheckDetailsAda = new DailyCheckDetailsAda(DailyChecklistsReport.this);
		listView.setAdapter(dailyCheckDetailsAda);
	}
	
	private void getDailyCheckDetailsFromDB()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;				
		String[] selCol = {"mysterytask","situation","level","remark"};
		String[] args={chooseCheckNo};
		cur = DBCalss.eliteDB.query("DailyChecklist", selCol, "dcheckNo LIKE ?", args, null, null, null);
		
		while(cur.moveToNext())
		{	
			String mySteryTaskStr = cur.getString(cur.getColumnIndex("mysterytask"));
			String situation = cur.getString(cur.getColumnIndex("situation"));
			String level = cur.getString(cur.getColumnIndex("level"));
			String remark = cur.getString(cur.getColumnIndex("remark"));

			DailyCheckingDetails dcheckDetails = new DailyCheckingDetails();
			dcheckDetails.setMysteryStr(mySteryTaskStr);
			dcheckDetails.setSituationStr(situation);
			dcheckDetails.setLevelStr(level);
			dcheckDetails.setRemarkStr(remark);
			
			detailsArrayList.add(dcheckDetails);			
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	public class DailyCheckDetailsAda extends ArrayAdapter<DailyCheckingDetails>{
		private final Activity context;
		CharSequence[] level = {"POOR", "AVERAGE", "GOOD","VERY GOOD","EXCELLENT"};
		
		public DailyCheckDetailsAda(Activity context)
		{
			super(context, R.layout.daily_check_lists_view, detailsArrayList);
			this.context = context; 			
		}
		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.daily_check_lists_view, null, true);

			TextView mysteryTxt=(TextView) rowView.findViewById(R.id.mysteryTxt);
			TextView situationTxt = (TextView) rowView.findViewById(R.id.situationTxt);
			TextView levelTxt = (TextView) rowView.findViewById(R.id.levelTxt);
			TextView remarkTxt = (TextView) rowView.findViewById(R.id.remarkTxt);
			
			DailyCheckingDetails dcDetails = detailsArrayList.get(position);
			mysteryTxt.setText(dcDetails.getMysteryStr());
			situationTxt.setText(dcDetails.getSituationStr());
			//int levelPos = Integer.parseInt(dcDetails.getLevelStr());
			levelTxt.setText(dcDetails.getLevelStr());
			remarkTxt.setText(dcDetails.getRemarkStr());
			
			return rowView;
		}
	}
	
}
