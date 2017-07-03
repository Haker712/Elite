package com.example.elite;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import bean.classes.CustomerInfo;
import bean.classes.InvoiceInfo;
import bean.classes.ProductDetailsInfo;
import custom.classes.DBCalss;

public class CreditCollectionTwo extends Activity {
	String chooseInvoiceID=CreditCollection.chooseInvoiceNo;  //  insert creditcollectionpagechooseinvoiceNo
	String chooseCustomerID=CustomerInfo.customer_ID;
	String productRecPersonName=CreditCollection.chooseProductRecPersonName; // insert creditcollectionpagechooseProductRecPersonName
	TextView txtInvoiceDetail,txtPersonName,txtInvoiceID;
	ListView lstProduct;
	TextView txtFinalTotalAmt,txtFinalAdvPayAmt,txtFinalDiscount,txtFinalNetAmt;
	Button btnBack,btnFinish;
	ArrayList<ProductDetailsInfo> arrProductDetailsInfo=new ArrayList<ProductDetailsInfo>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.creditcollectiontwo);
		
		hideScreenKeyboardInPageDisplay();
		registerIDs();
		getProductDetailsDataFromDB();
		setAdapter();
		getAllTotalDataForChoosedInvoice();
		catchEvents();
	}
	
	private void hideScreenKeyboardInPageDisplay() 
	{
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	private void catchEvents()
	{
		btnBack.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0)
			{
//				startActivity(new Intent(CreditCollectionTwo.this,CreditCollection.class));
//				finish();			
				Intent intent = getIntent();	
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

	private void registerIDs()
	{
		txtInvoiceDetail = (TextView)findViewById(R.id.creditcollectionTwo_txtInvoiceDetails);
		txtPersonName = (TextView)findViewById(R.id.creditcollectionTwo_txtPName);
		txtInvoiceID = (TextView)findViewById(R.id.creditcollectionTwo_txtInvoiceNo);
		lstProduct = (ListView)findViewById(R.id.creditcollectionTwo_lstProduct);
		txtFinalTotalAmt = (TextView)findViewById(R.id.creditcollectionTwo_txtAllTotalAmtNo);
		txtFinalAdvPayAmt = (TextView)findViewById(R.id.creditcollectionTwo_txtAdvanceTotalPayAmt);
		txtFinalDiscount= (TextView)findViewById(R.id.creditcollectionTwo_txtTotalDiscount);
		txtFinalNetAmt = (TextView)findViewById(R.id.creditcollectionTwo_txtTotalNetAmount);
		btnBack = (Button)findViewById(R.id.creditcollectionTwo_btnBack);
		btnFinish = (Button)findViewById(R.id.creditcollectionTwo_btnFinish);
		
		txtInvoiceID.setText(chooseInvoiceID);
		txtPersonName.setText(productRecPersonName);
	}
	@Override
	public void onBackPressed()
	{
		Intent intent = getIntent();	
		setResult(Activity.RESULT_OK, intent);

		super.onBackPressed();
	}

	private void getProductDetailsDataFromDB()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		

		String[] selCol = {"productName", "orderedDate", "orderQty", "totalDeliverQty", "itemDiscount", "price", "totalAmt", "invoiceNo", "customerID"};
		String[] args={chooseInvoiceID,chooseCustomerID};
		cur = DBCalss.eliteDB.query("CreditCollectionProduct", selCol, "invoiceNo LIKE ? AND customerID LIKE ? ", args, null, null, null);

		while(cur.moveToNext())
		{	
			String productName = cur.getString(cur.getColumnIndex("productName"));
			String orderedDate = cur.getString(cur.getColumnIndex("orderedDate"));
			String orderQty = cur.getString(cur.getColumnIndex("orderQty"));
			String totalDeliverQty = cur.getString(cur.getColumnIndex("totalDeliverQty"));
			String itemDiscount = cur.getString(cur.getColumnIndex("itemDiscount"));
			String price = cur.getString(cur.getColumnIndex("price"));
			String totalAmt = cur.getString(cur.getColumnIndex("totalAmt"));
			String invoiceNo = cur.getString(cur.getColumnIndex("invoiceNo"));
			String customerID=cur.getString(cur.getColumnIndex("customerId"));
			ProductDetailsInfo pDf = new ProductDetailsInfo();
			pDf.setProductName(productName);
			pDf.setOrderedDate(orderedDate);
			pDf.setOrderQty(orderQty);
			pDf.setTotalDeliverQty(totalDeliverQty);
			pDf.setItemDiscount(itemDiscount);
			pDf.setPrice(price);
			pDf.setTotalAmt(totalAmt);
			pDf.setInvoiceNo(invoiceNo);
			pDf.setCustomerID(customerID);

			arrProductDetailsInfo.add(pDf);
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	private void getAllTotalDataForChoosedInvoice()
	{
		for(InvoiceInfo ifo : CreditCollection.invoiceInfoList)
		{
			if(ifo.getInvoiceNo() == chooseInvoiceID)
			{
				txtFinalAdvPayAmt.setText(ifo.getAdvancePay());
				txtFinalTotalAmt.setText(ifo.getTotalAmt());
				txtFinalDiscount.setText(ifo.getVolumeDiscount());
				txtFinalNetAmt.setText(ifo.getNetAmt());
			}
		}
	}
	
	private void setAdapter()
	{
		productDetailsAdp pda=new productDetailsAdp(this);
		lstProduct.setAdapter(pda);
	}
	
	public class productDetailsAdp extends ArrayAdapter<ProductDetailsInfo>
	{
		private final Activity context;


		public productDetailsAdp(Activity context)
		{
			super(context, R.layout.creditcollection_custom_listrow2, arrProductDetailsInfo);
			this.context = context; 		
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.creditcollection_custom_listrow2, null, true);

			 TextView txtProductName = (TextView) rowView.findViewById(R.id.creditcollectiontwo_txtProductName);
			 TextView txtOrderedDate = (TextView) rowView.findViewById(R.id.creditcollectiontwo_txtOrderDate);
			 TextView txtOrderedQty = (TextView) rowView.findViewById(R.id.creditcollectiontwo_txtOrderQty);
			 TextView txtTotalDeliveredQty = (TextView) rowView.findViewById(R.id.creditcollectiontwo_txtTotalDeliverQty);
			 TextView txtPrice=(TextView) rowView.findViewById(R.id.creditcollectiontwo_txtPrice);
			 TextView txtItemDiscount=(TextView) rowView.findViewById(R.id.creditcollectiontwo_txtItemDiscount);
			 TextView txtTotalAmt=(TextView) rowView.findViewById(R.id.creditcollectiontwo_txtTotalAmt);
			 
			ProductDetailsInfo pDf = arrProductDetailsInfo.get(position);  
			txtProductName.setText(pDf.getProductName());
			txtOrderedDate.setText(pDf.getOrderedDate());
			txtOrderedQty.setText(pDf.getOrderQty());
			txtTotalDeliveredQty.setText(pDf.getTotalDeliverQty());
			txtPrice.setText(pDf.getPrice());
			txtItemDiscount.setText(pDf.getItemDiscount());
			txtTotalAmt.setText(pDf.getTotalAmt());
			
			return rowView;
		}
	}

}
