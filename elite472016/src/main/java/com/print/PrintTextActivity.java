package com.print;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.elite.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import bean.classes.SaleDataDetailInfo;
import custom.classes.DBCalss;


public class PrintTextActivity extends Activity {
	private TextView et_input;
	private Button bt_print,btnUnicode;// ���ִ�ӡ
	DecimalFormat commaSepFormat = new DecimalFormat("###,##0");
	
	String invoiceIDs;
	String customerID, cusName, cusPhone, cusAddress;
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy/MM/dd");
	
	String productName, qty;
	
	public static ArrayList<SaleDataDetailInfo> arrSaleDataDetailInfo=new ArrayList<SaleDataDetailInfo>();
	
	String saleManIDPrefs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StringBuilder str = new StringBuilder();
		double total = 0;
		
		SharedPreferences prefs = getSharedPreferences("SaleManPrefs", MODE_PRIVATE); 
		saleManIDPrefs = prefs.getString(MainActivity.saleManIdPrefs, "No name defined");
		
		getCustomerInformation();
		getSaleManData();
		
		String format_saleMan = "%1$-12s %2$-17s %3$-6s %4$-10s\n";
		String format_cusName = "%1$-14s %2$-25s %3$-3s %4$-3s\n";
		String format_cusPhone = "%1$-6s %2$-25s %3$-3s %4$-3s\n";
		String format_cusAdd = "%1$-8s %2$-32s %3$-2s %4$-3s\n\n";
		String format_title = "%1$-23s|%2$-7s|%3$-5s|%4$-10s\n\n";
        String format_content = "%1$-23s|%2$7s|%3$5s|%4$10s\n\n";
        String format_total = "%1$-9s %2$7s %3$16s %4$13s\n\n";
        String format_thankyou = "%1$-17s%2$11s%3$7s %4$10s\n\n";
             
        Calendar todayCal = Calendar.getInstance();
        String todayDate = fmtForDueStr.format(todayCal.getTime());
                 
        str.append(String.format(format_saleMan, "Sale Men ID:",saleManIDPrefs,"Date:",todayDate));
                
        str.append(String.format(format_cusName, "Customer Name:", cusName,"",""));
        
        str.append(String.format(format_cusPhone, "Phone:", cusPhone,"",""));
        
        str.append(String.format(format_cusAdd, "Address:", cusAddress,"",""));
        
		str.append(String.format(format_title, "Description", "  Q'ty", "Dis%", "  Amount"));
		Log.e("TEXT", str.toString());
		
		for(SaleDataDetailInfo product : arrSaleDataDetailInfo) {
			
			double totalAmt = (double)Math.round(product.getTotalAmt());
			total = total + totalAmt;
			
			str.append(String.format(format_content, product.getProductName(),
					product.getSaleQty(),
					product.getDisPercent(),
					commaSepFormat.format(totalAmt)));			
		}
	str.append(String.format(format_total, "", "", "Net Amount Total", commaSepFormat.format(total)));
	
	str.append(String.format(format_thankyou, "","Thank you!","",""));
			
		Log.e("Printed : ", str.toString());		
		PrintActivity.pl.printText(str.toString());
		startActivity(new Intent(PrintTextActivity.this, PrintActivity.class));
		finish();		
	}
	
	public void getCustomerInformation()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		

		String[] selCol = {"customerID","invoiceID"};
		cur = DBCalss.eliteDB.query("SaleData", selCol, null, null, null, null, null);

		while(cur.moveToNext())
		{	
			String customerID = cur.getString(cur.getColumnIndex("customerID"));
			String invoiceId = cur.getString(cur.getColumnIndex("invoiceID"));
			invoiceIDs = invoiceId;
			
			String[] selCol1={"customerID","customerName","Address","ph"};
			String[] arg={customerID};
			Cursor cur1;
			cur1=DBCalss.eliteDB.query("Customer", selCol1, "customerID LIKE ?", arg, null, null,null ,null );
			while(cur1.moveToNext())
			{
				
				String customerName=cur1.getString(cur1.getColumnIndex("customerName"));
				String customerAddress=cur1.getString(cur1.getColumnIndex("Address"));
				String customerPhone = cur1.getString(cur1.getColumnIndex("ph"));
				
				cusName = customerName;
				cusAddress = customerAddress;
				cusPhone = customerPhone;				
			}
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	private void getSaleManData()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;	
		String[] selCol = {"saleQty","discountPercent", "totalAmt","productName"};
		String[] args={invoiceIDs};
		cur = DBCalss.eliteDB.query("SaleDataDetail", selCol, "invoiceID LIKE ?", args, null, null, null);
		arrSaleDataDetailInfo.clear();
		while(cur.moveToNext())
		{				
			String saleQty = cur.getString(cur.getColumnIndex("saleQty"));
			String disPercent = cur.getString(cur.getColumnIndex("discountPercent"));
			double totalAmt = cur.getDouble(cur.getColumnIndex("totalAmt"));
			String productName=cur.getString(cur.getColumnIndex("productName"));
		
			SaleDataDetailInfo sDf = new SaleDataDetailInfo();
			sDf.setSaleQty(saleQty);
			sDf.setDisPercent(disPercent);
			sDf.setTotalAmt(totalAmt);
			sDf.setProductName(productName);
			arrSaleDataDetailInfo.add(sDf);			
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
}
