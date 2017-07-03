package com.report;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.elite.MainActivity;
import com.example.elite.R;

import custom.classes.DBCalss;

public class SaleSummaryReport extends Fragment
{
	View view;
	ListView lstProduct;
	TextView txtDate;
	TextView txtSaleMan;
	TextView txtFinalTotalAmt,txtFinalTotalDiscountAmt,txtFinalNetSaleAmt,txttotalVolumeDiscount,txtNetSaleAmt;
	saleSummaryReportAdp adp;
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat formatter = new DecimalFormat("###,##0"); 
	DecimalFormat decimalFormatter = new DecimalFormat("0.00");
	ArrayList<bean.classes.SaleSummaryReport> arrSaleProductList=new ArrayList<bean.classes.SaleSummaryReport>();
	int inttotalAmt =0;
	int inttotalQty=0;
	int intSalePrice=0;
	
	double finalTotalAmt;
	double finalDiscountAmt;
	double finalNetSaleAmt;
	double inttotalVolumeDiscount;
	
	String saleManNamePrefs = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		 view = inflater.inflate(R.layout.salesummaryreport, container,false);
		 
		 SharedPreferences prefs = getActivity().getSharedPreferences("SaleManPrefs", Context.MODE_PRIVATE); 
		 saleManNamePrefs = prefs.getString(MainActivity.saleManNamePrefs, "No name defined");
		 
		 registerIDs();
		 getAllSaleDataforSaleSummaryReportFromDB();
		 setAdapter();
		 return view;
	}
	private void setAdapter()
	{
		adp=new saleSummaryReportAdp(getActivity());
		lstProduct.setAdapter(adp);
		
		txtFinalTotalAmt.setText(formatter.format(finalTotalAmt));
		txtFinalTotalDiscountAmt.setText(decimalFormatter.format(finalDiscountAmt));
		txtFinalNetSaleAmt.setText(decimalFormatter.format(finalNetSaleAmt));
		txttotalVolumeDiscount.setText(decimalFormatter.format(inttotalVolumeDiscount));
		txtNetSaleAmt.setText(decimalFormatter.format(finalNetSaleAmt - inttotalVolumeDiscount));
	}
	
	private void getAllSaleDataforSaleSummaryReportFromDB() 
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;	
		Cursor volumeDiscountCur;
		String[] selCol = {"productID","productName","sum(saleQty)","salePrice","sum(discountAmt)","sum(totalAmt)","isFoc"};
		String[] args={"false"};
		String[] sleColforVD= {"sum(volumediscountAmt)"};
		
	//	cur = DBCalss.eliteDB.query("SaleDataDetail", selCol, "isFoc = ?", args, "productID", null, null);
	//    cur = DBCalss.eliteDB.query("SaleDataDetail", selCol, null, null, null, null, null);
		String sql = "Select productID,productName,salePrice,sum(saleQty),sum(discountAmt),sum(totalAmt),isFoc from SaleDataDetail where isFoc='false' group by ProductID union all Select productID,productName,salePrice,Sum(saleQty),sum(discountAmt),sum(totalAmt),isFoc from SaleDataDetail where isFoc='true' group by ProductID";
		cur = DBCalss.eliteDB.rawQuery(sql,null);
		volumeDiscountCur = DBCalss.eliteDB.query("SaleData",sleColforVD, null, null, null, null, null);
		while(cur.moveToNext())
		{	
			String productId = cur.getString(cur.getColumnIndex("productID"));
			String productName = cur.getString(cur.getColumnIndex("productName"));
			String totalsaleQty = cur.getString(cur.getColumnIndex("sum(saleQty)"));
			String salePrice = cur.getString(cur.getColumnIndex("salePrice"));
			double totalDiscountAmt = cur.getDouble(cur.getColumnIndex("sum(discountAmt)"));
			double totalnetSaleAmt = cur.getDouble(cur.getColumnIndex("sum(totalAmt)"));
			String isFoc= cur.getString(cur.getColumnIndex("isFoc"));
			Log.e(isFoc,"IsFoc");			
			if(isFoc.equals("false"))
			{
				inttotalQty = (int) Double.parseDouble(totalsaleQty);
				intSalePrice = (int) Double.parseDouble(salePrice);
				inttotalAmt = inttotalQty * intSalePrice;
				finalTotalAmt += inttotalAmt;
			}
			else
			{
				inttotalQty = 0;
				intSalePrice = (int) Double.parseDouble(salePrice);
				inttotalAmt = inttotalQty * intSalePrice;
				finalTotalAmt += inttotalAmt;
			}
//			if(totalDiscountAmt != null)
//			{
//				finalDiscountAmt += Double.parseDouble(totalDiscountAmt);
//			}
			finalDiscountAmt += totalDiscountAmt;			
//			if(totalnetSaleAmt != null)
//			{
//				finalNetSaleAmt += Double.parseDouble(totalnetSaleAmt); 
//			}
			finalNetSaleAmt += totalnetSaleAmt; 
			
			bean.classes.SaleSummaryReport ssReport=new bean.classes.SaleSummaryReport();
			ssReport.setProductID(productId);
			ssReport.setProductName(productName);
			if(isFoc.equals("false"))
			{
				ssReport.setTotalSaleQty(totalsaleQty);
			}
			else
			{
				ssReport.setTotalFocQty(totalsaleQty);
			}
			
			ssReport.setTotalDiscount(totalDiscountAmt);
			ssReport.setTotalAmt(inttotalAmt+"");
			ssReport.setTotalNetSaleAmt(totalnetSaleAmt);
			
			arrSaleProductList.add(ssReport);
		}
		cur.close();
		
		while(volumeDiscountCur.moveToNext())
		{
			String totalVolumeDiscount= volumeDiscountCur.getString(volumeDiscountCur.getColumnIndex("sum(volumediscountAmt)"));
			if(totalVolumeDiscount != null)
			{
				inttotalVolumeDiscount= Integer.parseInt(totalVolumeDiscount); 
			}
		}
		volumeDiscountCur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	
	}
	
//	private void getAllFocDataforSaleSummaryReportFromDB()
//	{
//		DBCalss.eliteDB.beginTransaction();	
//		Cursor curFoc1;
//		String[] selColforFoc1 = {"productID","productName","isFoc","sum(saleQty)"};
//		String[] argforFoc1={"true"};
//		curFoc1 =DBCalss.eliteDB.query("SaleDataDetail", selColforFoc1,"isFoc = ?",argforFoc1,"productID",null,null);
//		while(curFoc1.moveToNext())
//		{
//			String totalfocQty = curFoc1.getString(curFoc1.getColumnIndex("sum(saleQty)"));
//			String productID= curFoc1.getString(curFoc1.getColumnIndex("productID"));
//			String productName = curFoc1.getString(curFoc1.getColumnIndex("productName"));
//			
//			Log.e(arrSaleProductList.size()+"","ARRSaleProductListSize");
//			for(bean.classes.SaleSummaryReport ssR : arrSaleProductList)
//			{
//				Log.e(ssR.getProductID(),"GetProductID");
//				bean.classes.SaleSummaryReport ssR1= new bean.classes.SaleSummaryReport();
//				if(!ssR.getProductID().equals(productID))
//				{
//					ssR1.setProductID(productID);
//					Log.e("NOtSameProduct", ssR.getProductID());
//					ssR1.setProductName(productName);
//					ssR1.setTotalFocQty(totalfocQty);
//					arrSaleProductList.add(ssR1);
//					
//				}
//			}
//		}
//		
//		curFoc1.close();
//		DBCalss.eliteDB.setTransactionSuccessful();
//		DBCalss.eliteDB.endTransaction();
//	}
//	

	private void registerIDs()
	{
		lstProduct=(ListView) view.findViewById(R.id.salesummaryReport_lstreport);
		txtDate=(TextView) view.findViewById(R.id.txtDate);
		txtSaleMan =(TextView) view.findViewById(R.id.txtSaleManID);
		Calendar todayCal = Calendar.getInstance();
		txtDate.setText("Sale Date : "+fmtForDueStr.format(todayCal.getTime()));
		txtSaleMan.setText(saleManNamePrefs);
		
		
		 txtFinalTotalAmt=(TextView) view.findViewById(R.id.txtFinalSaleAmt);
		 txtFinalTotalDiscountAmt=(TextView) view.findViewById(R.id.txtFinalDiscountAmt);
		 txtFinalNetSaleAmt=(TextView) view.findViewById(R.id.txtFinalNetSaleAmt);
		 txttotalVolumeDiscount =(TextView) view.findViewById(R.id.txtFinalVolumeDiscountAmt);
		 txtNetSaleAmt= (TextView) view.findViewById(R.id.txtFinalNetSale);
	}
	
	public class saleSummaryReportAdp extends ArrayAdapter<bean.classes.SaleSummaryReport>
	{
		private final Activity context;

		public saleSummaryReportAdp(Activity context)
		{
			super(context, R.layout.salesummaryreport_custom_list_row, arrSaleProductList);
			this.context = context; 
			
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.salesummaryreport_custom_list_row, null, true);
			
			TextView txtProductName=(TextView) rowView.findViewById(R.id.salesummaryreport_txtitemname);
			TextView txttotalQty=(TextView)rowView.findViewById(R.id.salesummaryreport_txttotalqty);
			TextView txttotalAmt=(TextView)rowView.findViewById(R.id.salesummaryreport_txttotalamount);
			TextView txttotalDiscountAmt=(TextView)rowView.findViewById(R.id.salesummaryreport_txttotalDiscount);
			TextView txttotalnetSaleAmt= (TextView) rowView.findViewById(R.id.salesummaryreport_txtnetsaleamt);
			TextView txttotalFocQty=(TextView)rowView.findViewById(R.id.salesummaryreport_txttotalFOCQty);
		
			bean.classes.SaleSummaryReport ssReport = arrSaleProductList.get(position); 
			txtProductName.setText(ssReport.getProductName());
			if(ssReport.getTotalSaleQty() == null)
			{
				txttotalQty.setText("0");
			}
			else
			{
				txttotalQty.setText(formatter.format(Integer.parseInt(ssReport.getTotalSaleQty())));
			}
			if(ssReport.getTotalAmt() == null)
			{
				txttotalAmt.setText("0");
			}
			else
			{
				txttotalAmt.setText(ssReport.getTotalAmt());
			}
			txttotalDiscountAmt.setText(decimalFormatter.format(ssReport.getTotalDiscount()));

			txttotalnetSaleAmt.setText(decimalFormatter.format(ssReport.getTotalNetSaleAmt()));
			
			if(ssReport.getTotalFocQty() == null)
			{
				txttotalFocQty.setText("0");
			}
			else
			{
				txttotalFocQty.setText(decimalFormatter.format(Double.parseDouble(ssReport.getTotalFocQty())));
			}
		
			return rowView;
		}
		
	}


}
