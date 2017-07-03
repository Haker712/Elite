package com.report;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import bean.classes.SaleProduct;

import com.example.elite.R;

import custom.classes.DBCalss;

public class ProductBalanceReport extends Fragment
{
	View view;
	ListView lstProduct;
	TextView txtDate;
	ProductBalanceAdp adp;
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat formatter = new DecimalFormat("###,##0"); 
	ArrayList<SaleProduct> arrSaleProductList=new ArrayList<SaleProduct>();
	int intdeliverQty =0;
	int inttotalQty=0;
	int intremainingQty=0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		 view = inflater.inflate(R.layout.productbalance_report, container,false);
		 registerIDs();
		 getAllDataforProductBalanceReportFromDB();
		 setAdapter();
		 return view;
	}
	private void setAdapter()
	{
		adp=new ProductBalanceAdp(getActivity());
		lstProduct.setAdapter(adp);
	}
	
	private void getAllDataforProductBalanceReportFromDB() 
	{

		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		

		String[] selCol = {"RemainingQty","SaleQty","productName","productId","totalQty","sellingPrice"};
		cur = DBCalss.eliteDB.query("Product", selCol, null, null, null, null, null);

		while(cur.moveToNext())
		{	
			String productId = cur.getString(cur.getColumnIndex("productId"));
			String productName = cur.getString(cur.getColumnIndex("productName"));
			String remainingQty = cur.getString(cur.getColumnIndex("RemainingQty"));
			String saleQty = cur.getString(cur.getColumnIndex("SaleQty"));
			String totalQty = cur.getString(cur.getColumnIndex("totalQty"));
			String sellingPrice = cur.getString(cur.getColumnIndex("sellingPrice"));
			
			inttotalQty = Integer.parseInt(totalQty);
			intremainingQty = Integer.parseInt(remainingQty);
			intdeliverQty = inttotalQty - intremainingQty;
			
			SaleProduct sp=new SaleProduct();
			sp.setProductId(productId);
			sp.setProductName(productName);
			sp.setRemainingQty(remainingQty);
			if(saleQty == null)
			{
				sp.setSaleQty("0");
			}
			else
			{
				sp.setSaleQty(intdeliverQty +"");
			}
			sp.setTotalQty(totalQty);
			sp.setSellingPrice(sellingPrice);
			
			arrSaleProductList.add(sp);
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	
	}
	private void registerIDs()
	{
		lstProduct=(ListView) view.findViewById(R.id.productBalanceReport_lstProduct);
		txtDate=(TextView) view.findViewById(R.id.productBalanceReport_txtDate);
		
		Calendar todayCal = Calendar.getInstance();
		txtDate.setText("Sale Date : "+fmtForDueStr.format(todayCal.getTime()));
	}
	
	public class ProductBalanceAdp extends ArrayAdapter<SaleProduct>
	{
		private final Activity context;

		public ProductBalanceAdp(Activity context)
		{
			super(context, R.layout.productbalancereport_custom_list_row, arrSaleProductList);
			this.context = context; 
			
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.productbalancereport_custom_list_row, null, true);
			
			TextView txtProductName=(TextView) rowView.findViewById(R.id.productBalanceReport_custom_txtProductName);
			TextView txtBringQty=(TextView)rowView.findViewById(R.id.productBalanceReport_custom_txtBringQty);
			TextView txtDeliverQty=(TextView)rowView.findViewById(R.id.productBalanceReport_custom_txtDeliveredQty);
			TextView txtRemainingQty=(TextView)rowView.findViewById(R.id.productBalanceReport_custom_txtRemainingQty);
			
			SaleProduct spInfo = arrSaleProductList.get(position); 
			txtProductName.setText(spInfo.getProductName());
			txtBringQty.setText(formatter.format(Integer.parseInt(spInfo.getTotalQty())));
			txtDeliverQty.setText(formatter.format(Integer.parseInt(spInfo.getSaleQty())));
			txtRemainingQty.setText(formatter.format(Integer.parseInt(spInfo.getRemainingQty())));
			
			return rowView;
		}
		
	}
}
