package com.report;

import java.text.DecimalFormat;
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
import bean.classes.SaleDataInfo;

import com.example.elite.MainActivity;
import com.example.elite.R;
import com.example.elite.SaleInvoiceReport2;

import custom.classes.DBCalss;

public class SaleInvoiceReport extends Fragment
{
	View view ;
	ListView lstReport;
	TextView txtSaleManID,txtDate;
	saleInvoiceAdp adp;
	DecimalFormat formatter = new DecimalFormat("###,##0"); 
	DecimalFormat decimalFormatter = new DecimalFormat("0.00");
	public static ArrayList<SaleDataInfo> arrSaleDataInfo=new ArrayList<SaleDataInfo>();
	
	String saleManNamePrefs = null;
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{ 	
			Log.e("SaleInvoiceReport","SaleInvoiceReport");
		 view = inflater.inflate(R.layout.sale_invoice_report, container,false);
		 
		 SharedPreferences prefs = getActivity().getSharedPreferences("SaleManPrefs", Context.MODE_PRIVATE); 
		 saleManNamePrefs = prefs.getString(MainActivity.saleManNamePrefs, "No name defined");
		 
		 registerIDs();
		 getAllDataforSaleInvoiceReport();
		 setAdapter();
		 catchEvents();
		 return view;
	}
	private void setAdapter()
	{
		adp=new saleInvoiceAdp(getActivity());
		lstReport.setAdapter(adp);
	}
	private void registerIDs() 
	{
		lstReport=(ListView) view.findViewById(R.id.saleInvoiceReport_lstreport);
		txtSaleManID=(TextView) view.findViewById(R.id.txtSaleManID);
		txtDate=(TextView) view.findViewById(R.id.txtDate);
		arrSaleDataInfo.clear();
		Log.e(arrSaleDataInfo.size()+"","ArraylistSize1");
		
		Calendar todayCal = Calendar.getInstance();
		txtDate.setText("Sale Date : "+fmtForDueStr.format(todayCal.getTime()));
		txtSaleManID.setText(saleManNamePrefs);
		
	}
	private void catchEvents() 
	{
		lstReport.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
					long arg3) 
			{
				SaleInvoiceReport2.chooseInvoice= arrSaleDataInfo.get(pos).getInvoiceID();
				SaleInvoiceReport2.totalDiscount=arrSaleDataInfo.get(pos).getDiscountAmt();
				SaleInvoiceReport2.netAmt=arrSaleDataInfo.get(pos).getNetAmt();
				SaleInvoiceReport2.dueDate=arrSaleDataInfo.get(pos).getDueDate();
				SaleInvoiceReport2.payAmt=arrSaleDataInfo.get(pos).getPayAmount();
				SaleInvoiceReport2.refund =arrSaleDataInfo.get(pos).getRefund();
				SaleInvoiceReport2.recPersonName=arrSaleDataInfo.get(pos).getRecPersonName();
				SaleInvoiceReport2.signImg=arrSaleDataInfo.get(pos).getSignImg();
				SaleInvoiceReport2.customerName=arrSaleDataInfo.get(pos).getCustomerName();
				Log.e(SaleInvoiceReport2.chooseInvoice, "ChooseInvoiceNo");
				startActivity(new Intent(SaleInvoiceReport.this.getActivity(),SaleInvoiceReport2.class));
			}
		}); 
	}
	
	private void getAllDataforSaleInvoiceReport()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		

		String[] selCol = {"invoiceID","customerID","saleDate","TotalAmtNoDiscount","netAmt","volumediscountAmt","payAmt","refundAmt","receitpPersonName", "signImg","salePersonID","dueDate","cashOrCredit","locationCode","devID","invoiceTime","totalVolandItemDisAmt","TotalAmtNoDiscount"};
		cur = DBCalss.eliteDB.query("SaleData", selCol, null, null, null, null, null);

		while(cur.moveToNext())
		{	
			String invoiceID = cur.getString(cur.getColumnIndex("invoiceID"));
			String customerID = cur.getString(cur.getColumnIndex("customerID"));
			String saleDate = cur.getString(cur.getColumnIndex("saleDate"));
			String totalAmt =cur.getString(cur.getColumnIndex("TotalAmtNoDiscount"));
			String netAmt = cur.getString(cur.getColumnIndex("netAmt"));
			String discountAmt = cur.getString(cur.getColumnIndex("volumediscountAmt"));
			String totalitemandVolumeDiscount = cur.getString(cur.getColumnIndex("TotalVolandItemDisAmt"));
			String totalAmtNoDiscount = cur.getString(cur.getColumnIndex("TotalAmtNoDiscount"));
			String payAmt = cur.getString(cur.getColumnIndex("payAmt"));
			String receiptPersonName = cur.getString(cur.getColumnIndex("receitpPersonName"));
			String signImg = cur.getString(cur.getColumnIndex("signImg"));
			String salePersonID=cur.getString(cur.getColumnIndex("salePersonID"));
			String DueDate=cur.getString(cur.getColumnIndex("dueDate"));
			String cashOrCredit=cur.getString(cur.getColumnIndex("cashOrCredit"));
			String locationCode=cur.getString(cur.getColumnIndex("locationCode"));
			String devID=cur.getString(cur.getColumnIndex("devID"));
			String invoiceTime=cur.getString(cur.getColumnIndex("invoiceTime"));
			String refundAmt=cur.getString(cur.getColumnIndex("refundAmt"));
			
			SaleDataInfo sdInfo = new SaleDataInfo();
			sdInfo.setInvoiceID(invoiceID);
			Log.e(sdInfo.getInvoiceID()+"","InvoiceID");
			sdInfo.setCustomerID(customerID);
			Log.e(sdInfo.getCustomerID()+"","CustomerID");
			sdInfo.setSaleDate(saleDate);
			sdInfo.setTotalAmt(totalAmt);
			sdInfo.setNetAmt(netAmt);
			sdInfo.setDiscountAmt(discountAmt);
			sdInfo.setPayAmount(payAmt);
			sdInfo.setRecPersonName(receiptPersonName);
			sdInfo.setSignImg(signImg);
			sdInfo.setSalePersonID(salePersonID);
			sdInfo.setDueDate(DueDate);
			sdInfo.setCashOrCredit(cashOrCredit);
			sdInfo.setLocationCode(locationCode);
			sdInfo.setDevID(devID);
			sdInfo.setInvoiceTime(invoiceTime);
			sdInfo.setRefund(refundAmt);
			sdInfo.setTotalAmtNoDiscount(totalAmtNoDiscount);
			sdInfo.setTotalitemandvolDisAmt(totalitemandVolumeDiscount);
			
			String[] selCol1={"customerID","customerName","Address"};
			String[] arg={customerID};
			Cursor cur1;
			cur1=DBCalss.eliteDB.query("Customer", selCol1, "customerID LIKE ?", arg, null, null,null ,null );
			while(cur1.moveToNext())
			{
				String customerName=cur1.getString(cur1.getColumnIndex("customerName"));
				String customerAddress=cur1.getString(cur1.getColumnIndex("Address"));
				
				sdInfo.setCustomerName(customerName);
				sdInfo.setAddress(customerAddress);
			}
			arrSaleDataInfo.add(sdInfo);
			Log.e(arrSaleDataInfo.size()+"","Report Array list Size");

		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	public class saleInvoiceAdp extends ArrayAdapter<SaleDataInfo>
	{
		private final Activity context;

		public saleInvoiceAdp(Activity context)
		{
			super(context, R.layout.saleinvoicereport_custom_list_row, arrSaleDataInfo);
			this.context = context; 
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.saleinvoicereport_custom_list_row, null, true);
			
			
			TextView txtCustomerName=(TextView) rowView.findViewById(R.id.saleInvoiceReport_txtCustomerName);
			TextView txtCustomerAddress=(TextView)rowView.findViewById(R.id.saleInvoiceReport_txtcustomerAddress);
			TextView txtTotalAmt=(TextView)rowView.findViewById(R.id.saleInvoiceReport_txtTotalAmt);
			TextView txtInvoiceID=(TextView)rowView.findViewById(R.id.saleInvoiceReport_txtInvoiceID);
			TextView txtTotalDiscount=(TextView)rowView.findViewById(R.id.saleInvoiceReport_txtTotalDiscount);
			TextView txtNetSaleAmt= (TextView) rowView.findViewById(R.id.saleInvoiceReport_txtNetSaleAmt);
			
			SaleDataInfo sDInfo = arrSaleDataInfo.get(position); 
			txtCustomerName.setText(sDInfo.getCustomerName());
			txtCustomerAddress.setText(sDInfo.getAddress());
			txtTotalAmt.setText(formatter.format(Double.parseDouble(sDInfo.getTotalAmtNoDiscount())));
			txtInvoiceID.setText(sDInfo.getInvoiceID());
			txtTotalDiscount.setText(decimalFormatter.format(Double.parseDouble(sDInfo.getTotalitemandvolDisAmt())));
			txtNetSaleAmt.setText(decimalFormatter.format(Double.parseDouble(sDInfo.getNetAmt())));
			txtSaleManID.setText(sDInfo.getSalePersonID());
			txtDate.setText(sDInfo.getSaleDate());
		
			
			return rowView;
		}
		
	}

}
