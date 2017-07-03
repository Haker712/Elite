package com.example.elite;

import info.androidhive.slidingmenu.model.NavDrawerItem;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.report.ProductBalanceReport;
import com.report.SaleInvoiceReport;
import com.report.SaleSummaryReport;

public class Report extends Activity 
{
	private ListView mDrawerList;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private ArrayList<NavDrawerItem> navDrawerItems;
	Button btnHome;
	Spinner spReportNameList;
	ArrayAdapter<String> adp;
	ArrayList<String> reportNameList=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
	//	mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	//	mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		registerIDs();
		catchEvents();
	}

	private void registerIDs()
	{
		btnHome=(Button) findViewById(R.id.report_btnHome);
		spReportNameList=(Spinner) findViewById(R.id.report_spinner);
		
		reportNameList.add("Sale Invoice Report");
		reportNameList.add("Product Balance Report");
//		reportNameList.add("Daily CheckList Report");
		reportNameList.add("Sale Summary Report");
		adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,reportNameList);
		adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spReportNameList.setAdapter(adp);
		
//		FragmentTransaction tran1 = getFragmentManager().beginTransaction();
//		SaleInvoiceReport saleInvoiceFrag = new SaleInvoiceReport();
//		tran1.replace(R.id.report_fragment_layout, saleInvoiceFrag);
//		tran1.commit();
		
//		FragmentTransaction tran1 = getFragmentManager().beginTransaction();
//		ProductBalanceReport saleInvoiceFrag = new ProductBalanceReport();
//		tran1.replace(R.id.report_fragment_layout, saleInvoiceFrag);
//		tran1.commit();
	}

	@Override
	public void onBackPressed()
	{
		startActivity(new Intent(Report.this,HomePage.class));
		finish();
	}
	private void catchEvents() 
	{
		btnHome.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				startActivity(new Intent(Report.this,HomePage.class));
				finish();
			}
		});
		
		spReportNameList.setOnItemSelectedListener(new OnItemSelectedListener() 
		{

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) 
			{
				int pos= spReportNameList.getSelectedItemPosition();
				Log.e(pos+"", "SpinnerSelected");
				if(pos == 0)
				{
					FragmentTransaction tran1 = getFragmentManager().beginTransaction();
					SaleInvoiceReport saleInvoiceFrag = new SaleInvoiceReport();
					tran1.replace(R.id.report_fragment_layout, saleInvoiceFrag);
					tran1.commit();
				}
				else if(pos == 1)
				{
					FragmentTransaction tran1 = getFragmentManager().beginTransaction();
					ProductBalanceReport prodBalFrag = new ProductBalanceReport();
					tran1.replace(R.id.report_fragment_layout, prodBalFrag);
					tran1.commit();
				}
//				else if(pos == 2)
//				{
//					FragmentTransaction tran1 = getFragmentManager().beginTransaction();
//					DailyCheckReport prodBalFrag = new DailyCheckReport();
//					tran1.replace(R.id.report_fragment_layout, prodBalFrag);
//					tran1.commit();
//				}
				else if( pos == 2)
				{
					FragmentTransaction tran2 = getFragmentManager().beginTransaction();
					SaleSummaryReport saleSummaryFrag = new SaleSummaryReport();
					tran2.replace(R.id.report_fragment_layout,saleSummaryFrag);
					tran2.commit();
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
