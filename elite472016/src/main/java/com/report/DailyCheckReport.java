package com.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import bean.classes.DailyChecking;

import com.example.elite.DailyChecklistsReport;
import com.example.elite.MainActivity;
import com.example.elite.R;

import custom.classes.DBCalss;

public class DailyCheckReport extends Fragment{
	View view;
	private ListView dailyCheckListView;
	private TextView saleManTxt, dateTxt;
	String date;
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");
	private dailyCheckAda dailyCheckAda;
	private ArrayList<DailyChecking> dailyArrayList = new ArrayList<DailyChecking>();
	
	String saleManIDPrefs = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		 view = inflater.inflate(R.layout.daily_check_report, container,false);
		 
		 SharedPreferences prefs = getActivity().getSharedPreferences("SaleManPrefs", Context.MODE_PRIVATE); 
		 saleManIDPrefs = prefs.getString(MainActivity.saleManIdPrefs, "No name defined");
		 
		 dailyCheckListView = (ListView) view.findViewById(R.id.dcheck_listview);
		 
		 saleManTxt = (TextView) view.findViewById(R.id.sale_man_txt);
		 dateTxt = (TextView) view.findViewById(R.id.dreport_date_txt);
		 
		 Calendar todayCal = Calendar.getInstance();
		 date = fmtForDueStr.format(todayCal.getTime());
			
		 saleManTxt.setText(saleManIDPrefs);
		 dateTxt.setText(date);
		 getAllDataforDailyCheckReport();
		 
		 dailyCheckAda = new dailyCheckAda(getActivity());
		 dailyCheckListView.setAdapter(dailyCheckAda);
		 dailyCheckListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				DailyChecklistsReport.chooseCheckNo = dailyArrayList.get(pos).getDcheckNo();
				DailyChecklistsReport.chooseShopName = dailyArrayList.get(pos).getShopname();
				DailyChecklistsReport.chooseDealer = dailyArrayList.get(pos).getDistributor();
				DailyChecklistsReport.chooseState = dailyArrayList.get(pos).getState();
				DailyChecklistsReport.chooseCheckingTime = dailyArrayList.get(pos).getCheckingtime();
				
				startActivity(new Intent(DailyCheckReport.this.getActivity(),DailyChecklistsReport.class));				
			}
		});
		 return view;
	}
	
	private void getAllDataforDailyCheckReport()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		

		String[] selCol = {"checkno","checkingtime","shopname","location","shopstate"};
		cur = DBCalss.eliteDB.query("DailyCheckTable", selCol, null, null, null, null, null);

		//dailyArrayList.clear();
		
		while(cur.moveToNext())
		{	
			String checkNo = cur.getString(cur.getColumnIndex("checkno"));
			String checkingtime = cur.getString(cur.getColumnIndex("checkingtime"));
			String shopname = cur.getString(cur.getColumnIndex("shopname"));
			String location = cur.getString(cur.getColumnIndex("location"));
			String shopsate = cur.getString(cur.getColumnIndex("shopstate"));
			
			DailyChecking dailyChecking = new DailyChecking();
			dailyChecking.setDcheckNo(checkNo);
			dailyChecking.setCheckingtime(checkingtime);
			dailyChecking.setShopname(shopname);
			dailyChecking.setDistributor(location);
			dailyChecking.setState(shopsate);
			
			dailyArrayList.add(dailyChecking);
			Log.e(checkNo, "CheckNOOOO");
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	public class dailyCheckAda extends ArrayAdapter<DailyChecking>
	{
		private final Activity context;

		public dailyCheckAda(Activity context)
		{
			super(context, R.layout.daily_check_report_list, dailyArrayList);
			this.context = context; 
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.daily_check_report_list, null, true);			
			
			TextView noTxt=(TextView) rowView.findViewById(R.id.dcreport_no);
			TextView shopTxt=(TextView)rowView.findViewById(R.id.dcreport_shopname);
			TextView dealerTxt=(TextView)rowView.findViewById(R.id.dcreport_dealer);
			TextView checkingTimeTxt=(TextView)rowView.findViewById(R.id.dcreport_checktime);
			TextView stateTxt=(TextView)rowView.findViewById(R.id.dcreport_state);
						
			DailyChecking dChecking = dailyArrayList.get(position); 
			noTxt.setText(dChecking.getDcheckNo());
			shopTxt.setText(dChecking.getShopname());
			dealerTxt.setText(dChecking.getDistributor());
			checkingTimeTxt.setText(dChecking.getCheckingtime());
			stateTxt.setText(dChecking.getState());		
			
			return rowView;
		}
		
	}
}
