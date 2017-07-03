package com.example.elite;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import bean.classes.SaleDataDetailInfo;

import com.report.SaleInvoiceReport;

import custom.classes.DBCalss;


public class SaleInvoiceReport2 extends Activity 
{
	SaleInvoiceReport saleInvRep;
	TextView txtinvoiceNo,txtNetAmt,txtDueDate,txtPayAmt,txtReceiptPerson,txtTotal,txtSaleDate,txtCustomerName,txtRefund;
	public static ArrayList<SaleDataDetailInfo> arrSaleDataDetailInfo=new ArrayList<SaleDataDetailInfo>();
	saleInvoiceAdp adapter;
	ListView lstProducts;
	ImageView imgSignImg;
	SimpleDateFormat fmtForInvoiceTodayStr = new SimpleDateFormat("yyMMdd");
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");
	DecimalFormat formatter = new DecimalFormat("###,##0"); 
	DecimalFormat decimalFormatter = new DecimalFormat("0.00");
	public static String chooseInvoice,totalDiscount,netAmt,dueDate,refund,payAmt,recPersonName,signImg,customerName;
	double totalAmtforInvoice;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sale_invoice_report2);
		registerIDs();
		getSaleDetailsDataFromDB();
		setAdapter();
		catchEvents();
	}
	
	private void setAdapter()
	{
		adapter = new saleInvoiceAdp(this);
		lstProducts.setAdapter(adapter);
	}

	private void catchEvents()
	{
		
	}
	
	private void registerIDs() 
	{
		lstProducts=(ListView) findViewById(R.id.saleinvoiceReport2_lstProduct);
		txtinvoiceNo=(TextView) findViewById(R.id.saleinvoiceReport2_txtInvoiceNo);
		txtNetAmt=(TextView) findViewById(R.id.saleinvoiceReport2_txtNetAmt);
		txtDueDate=(TextView) findViewById(R.id.saleinvoiceReport2_txtDueDate);
		txtPayAmt=(TextView) findViewById(R.id.saleinvoiceReport2_txtPayAmount);
		txtReceiptPerson=(TextView) findViewById(R.id.saleInvoiceReport2_txtReceiptPerson);
		imgSignImg=(ImageView) findViewById(R.id.saleInvoiceReport2_imgSignImage);
		txtRefund =(TextView)findViewById(R.id.saleinvoiceReport2_txtRefund);
		txtTotal=(TextView) findViewById(R.id.saleInvoiceReport2_txtTotalAmt);
		txtSaleDate=(TextView) findViewById(R.id.txtSaleDate);
		txtCustomerName=(TextView) findViewById(R.id.sale_invoice_report2_txtcustomerName);
		Calendar todayCal = Calendar.getInstance();
		String todayDate = fmtForInvoiceTodayStr.format(todayCal.getTime());
		txtSaleDate.setText("Sale Date : "+ fmtForDueStr.format(todayCal.getTime()));
		txtCustomerName.setText(customerName);
	}

	private void getSaleDetailsDataFromDB()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		
		Log.e(chooseInvoice, "ChooseInvoice");
		String[] selCol = {"invoiceID", "productID", "saleQty", "salePrice", "purchasePrice", "discountAmt", "totalAmt","productName","remark"};
		String[] args={chooseInvoice};
		cur = DBCalss.eliteDB.query("SaleDataDetail", selCol, "invoiceID LIKE ?", args, null, null, null);
		arrSaleDataDetailInfo.clear();
		while(cur.moveToNext())
		{	
			String invoiceID = cur.getString(cur.getColumnIndex("invoiceID"));
			String productID = cur.getString(cur.getColumnIndex("productID"));
			String saleQty = cur.getString(cur.getColumnIndex("saleQty"));
			String salePrice= cur.getString(cur.getColumnIndex("salePrice"));
			String purchasePrice= cur.getString(cur.getColumnIndex("purchasePrice"));
			String discountAmt= cur.getString(cur.getColumnIndex("discountAmt"));
			double totalAmt = cur.getDouble(cur.getColumnIndex("totalAmt"));
			String productName=cur.getString(cur.getColumnIndex("productName"));
			String remark = cur.getString(cur.getColumnIndex("remark"));
		
			SaleDataDetailInfo sDf = new SaleDataDetailInfo();
			sDf.setInvoiceID(invoiceID);
			sDf.setProductID(productID);
			sDf.setSaleQty(saleQty);
			sDf.setSalePrice(salePrice);
			sDf.setPurchasePrice(purchasePrice);
			sDf.setDiscountAmt(discountAmt);
			sDf.setTotalAmt(totalAmt);
			sDf.setProductName(productName);
			sDf.setRemark(remark);
			arrSaleDataDetailInfo.add(sDf);
			
			totalAmtforInvoice +=sDf.getTotalAmt()+Double.parseDouble(sDf.getDiscountAmt());
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	public class saleInvoiceAdp extends ArrayAdapter<SaleDataDetailInfo>
	{
		private final Activity context;

		public saleInvoiceAdp(Activity context)
		{
			super(context, R.layout.saleinvoicereport_custom_list_row2, arrSaleDataDetailInfo);
			this.context = context; 	
		}
		
		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.saleinvoicereport_custom_list_row2, null, true);
			
			TextView txtProductName = (TextView)rowView.findViewById(R.id.saleInvoiceReport2customLayout_txtProductName);
			TextView txtQty =(TextView) rowView.findViewById(R.id.saleInvoiceReport2customLayout_txtQty);
			TextView txtPrice = (TextView) rowView.findViewById(R.id.saleInvoiceReport2customLayout_txtPrice);
			TextView txtDiscount =(TextView) rowView.findViewById(R.id.saleInvoiceReport2customLayout_txtDiscount);
			TextView txtTotalAmt =(TextView) rowView.findViewById(R.id.saleInvoiceReport2customLayout_txtTotalAmt);
			TextView txtRemark = (TextView) rowView.findViewById(R.id.saleInvoiceReport2customLayout_txtRemark);//for Remark
			
			SaleDataDetailInfo sDetailInfo = arrSaleDataDetailInfo.get(position); 
			txtProductName.setText(sDetailInfo.getProductName());
			txtQty.setText(sDetailInfo.getSaleQty());
			int salePrice=0;
			double discount=0;
			double totalDiscountint=0;
			Log.e(sDetailInfo.getDiscountAmt(),"DDDDD");
			if(sDetailInfo.getDiscountAmt() != null)
			{
				discount = Double.parseDouble(sDetailInfo.getDiscountAmt());
			}
			salePrice= (int) Double.parseDouble(sDetailInfo.getSalePrice());
			txtPrice.setText(formatter.format(salePrice));
			txtDiscount.setText(decimalFormatter.format(discount));
			Log.e(sDetailInfo.getTotalAmt()+"","TotalAmttttt");
			txtTotalAmt.setText(decimalFormatter.format(sDetailInfo.getTotalAmt()));
			txtRemark.setText(sDetailInfo.getRemark());
			
			txtinvoiceNo.setText(chooseInvoice);
			Log.e(netAmt, "NetAmt");
			txtNetAmt.setText(netAmt);
			txtDueDate.setText(dueDate);
			txtPayAmt.setText(payAmt+"");
			txtTotal.setText(formatter.format(totalAmtforInvoice));
			txtRefund.setText(refund);
			txtReceiptPerson.setText(recPersonName);
			byte[] ImageShow=Base64.decode(signImg, Base64.DEFAULT);
			Bitmap mBitmap = BitmapFactory.decodeByteArray(ImageShow, 0, ImageShow.length);
			imgSignImg.setImageBitmap(mBitmap);
			
			return rowView;
		}
		
	}
}
