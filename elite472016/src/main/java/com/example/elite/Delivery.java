package com.example.elite;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import bean.classes.DeliveryProduct;
import bean.classes.DeliveryProductDetail;
import bean.classes.Invoice;
import custom.classes.DBCalss;

public class Delivery extends Activity {

	TextView txtDelivery;
	static TextView txtOrderDate;
	Button btnFinish,btnCancel;
	ListView lstDelivery;
	Spinner spnInvoice;
	int clickPosForSpinner;
	String chooseInvoiceID;
	LinearLayout layoutforDeliverQty;
	DeliverProductAda lstDeliverAda;
	EditText etdeliverQty;
	int totalDeliveryQty=0;
	int totalAmount = 0;
	int deliverQty=0,returnDeliveryQty=0;
	int orderQty = 0;
	int remianingQty = 0;
	static int remainingAmtFromServer = 0;
	static int totalitemDiscounts=0;
	ArrayList<Invoice> invoiceList=new ArrayList<Invoice>();
	DecimalFormat formatter = new DecimalFormat("###,##0"); 
	InvoiceListSpinnerAdapter invoiceListSpinnerAdp;
	public static ArrayList<DeliveryProductDetail> deliverList = new ArrayList<DeliveryProductDetail>();
	ArrayList<DeliveryProduct> delProductList = new ArrayList<DeliveryProduct>();
	public static ArrayList<DeliveryProduct> tmpProList = new ArrayList<DeliveryProduct>();
	@Override 
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delivery_page);
		registerIDs();
		getAllInvoiceList();			
		setAdapter();
		catchEvents();

	}

	private void getAllInvoiceList() 
	{
		invoiceList = Customer.invoiceList;
	}

	private void setAdapter() 
	{
		invoiceListSpinnerAdp = new InvoiceListSpinnerAdapter(invoiceList);
		spnInvoice.setPrompt("Invoice List");
		spnInvoice.setAdapter(invoiceListSpinnerAdp);

		lstDeliverAda = new DeliverProductAda(this);
		lstDelivery.setAdapter(lstDeliverAda);
	}

	public class DeliverProductAda extends ArrayAdapter<DeliveryProduct>
	{
		private final Activity context;


		public DeliverProductAda(Activity context)
		{
			super(context, R.layout.delivery_list_row, delProductList);
			this.context = context; 		
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.delivery_list_row, null, true);

			final TextView txtProductName = (TextView) rowView.findViewById(R.id.delivery_txtProductName);
			TextView txtOrderQty = (TextView) rowView.findViewById(R.id.delivery_txtOrderQty);
			final TextView txtDeliverQty = (TextView) rowView.findViewById(R.id.delivery_txtDeliverQty);
			final TextView txtRemainingQty=(TextView) rowView.findViewById(R.id.delivery_txtRemainingQty);
			final TextView txtReturnDeliverQty = (TextView) rowView.findViewById(R.id.delivery_txtReturnDeliverQty);
			final TextView txtPrice = (TextView) rowView.findViewById(R.id.delivery_txtPrice);
			final TextView txtDiscount = (TextView) rowView.findViewById(R.id.delivery_txtDiscount);
			final TextView txttotalAmt = (TextView) rowView.findViewById(R.id.delivery_txtTotalAmt);
			final Button btnDeliver  = (Button)rowView.findViewById(R.id.delivery_btnDeliver);


			final DeliveryProduct delDetail =  delProductList.get(position);		

			txtProductName.setText(delDetail.getProductName());	
			txtOrderQty.setText(delDetail.getOrderQty());		
			txtPrice.setText(delDetail.getPrice());
			Log.e(delDetail.getPrice(),"delDetail.getPrice");
			Log.e(delDetail.getRemainingQty(),"delDetail.getRemainingQty");
			txtRemainingQty.setText(delDetail.getRemainingQty());

			btnDeliver.setOnClickListener(new OnClickListener() 
			{				
				@Override
				public void onClick(View v)
				{				
					showCustomDialog();					
				}

				private void showCustomDialog() 
				{
					AlertDialog.Builder builder;
					LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View layout = inflater.inflate(R.layout.deliver_custom_dialog, null);

					builder = new AlertDialog.Builder(Delivery.this);
					builder.setTitle("Deliver");
					builder.setView(layout);

					TextView txtProductName = (TextView) layout.findViewById(R.id.deliverDialog_txtProductName);
					TextView txtOrderQty = (TextView) layout.findViewById(R.id.deliverDialog_txtOrderQty);
					TextView txtProductLimit = (TextView)layout.findViewById(R.id.deliverDialog_txtProductLimit);
					TextView txtCustomerReturnQty  = (TextView) layout.findViewById(R.id.deliverDialog_txtReturnQty);

					final EditText etReturnDeliverQty = (EditText) layout.findViewById(R.id.deliverDialog_txtReturnDeliverQty);
					final EditText etDeliverQty = (EditText) layout.findViewById(R.id.deliverDialog_etTotalDeliverQty);
					final TextView txtTotaldeliverQty = (TextView) layout.findViewById(R.id.deliverDialog_txtTotalDeliverQty);
					etDeliverQty.addTextChangedListener(new TextWatcher() 
					{

						@Override
						public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
						{

						}
						@Override
						public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
								int arg3)
						{

						}
						@Override
						public void afterTextChanged(Editable arg0) 
						{
							if(delDetail.getOrderQty().equals("") || delDetail.getOrderQty() == null)
							{
								orderQty = 0;
							}
							else
							{
								orderQty = Integer.parseInt(delDetail.getOrderQty());
							}
							if(etDeliverQty.getText().toString().equals(""))
							{
								deliverQty = 0;
							}
							else
							{
								deliverQty = Integer.parseInt(etDeliverQty.getText().toString());	

								Log.e("Product Limit", delDetail.getProductLimit()+"");
								int productLimit = 0; 

								if(delDetail.getProductLimit() == null ||delDetail.getProductLimit().equals(""))
								{
									productLimit = 0;
								}
								else
								{
									productLimit = Integer.parseInt(delDetail.getProductLimit());
								}

								if(deliverQty > productLimit)
								{
									final AlertDialog diag4 = new AlertDialog.Builder(Delivery.this)
									.setTitle("Alert!")
									.setIcon(
											android.R.drawable.ic_dialog_alert)
											.setMessage("\nYour Deliver Qty is over Product Limit")
											.setPositiveButton("OK",new android.content.DialogInterface.OnClickListener() 
											{
												@Override
												public void onClick(DialogInterface diag2,int arg1) 
												{
													etDeliverQty.setText("");
												}
											}).create();

									diag4.show();
									diag4.setCancelable(false);
									diag4.setCanceledOnTouchOutside(false);
								}
							}

							 totalDeliveryQty = deliverQty +returnDeliveryQty;
							txtTotaldeliverQty.setText(totalDeliveryQty+"");	
						}
					});

					etReturnDeliverQty.addTextChangedListener(new TextWatcher() 
					{

						@Override
						public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
						{

						}

						@Override
						public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
								int arg3)
						{

						}

						@Override
						public void afterTextChanged(Editable arg0) 
						{
							int returnQty=0;
							if(etReturnDeliverQty.getText().toString().equals(""))
							{
								returnDeliveryQty = 0;								
							}
							else
							{								
								returnDeliveryQty = Integer.parseInt(etReturnDeliverQty.getText().toString());
								if(delDetail.getNeedToReturnDeliverQty() == null || delDetail.getNeedToReturnDeliverQty().equals(""))
								{
									returnQty = 0;
								}
								else
								{
									returnQty = Integer.parseInt(delDetail.getNeedToReturnDeliverQty());
								}

								if(returnDeliveryQty > returnQty)
								{
									final AlertDialog diag4 = new AlertDialog.Builder(Delivery.this)
									.setTitle("Alert!")
									.setIcon(
											android.R.drawable.ic_dialog_alert)
											.setMessage("\nYour Return Deliver Qty is over Customer Return Quantity")
											.setPositiveButton("OK",new android.content.DialogInterface.OnClickListener() 
											{
												@Override
												public void onClick(DialogInterface diag2,int arg1) 
												{
													etReturnDeliverQty.setText("");
												}
											}).create();

									diag4.show();
									diag4.setCancelable(false);
									diag4.setCanceledOnTouchOutside(false);
								}
							}			
							totalDeliveryQty = deliverQty + returnDeliveryQty;
							txtTotaldeliverQty.setText(totalDeliveryQty+"");	
						}
					});
					
					etDeliverQty.setText(delDetail.getDeliverQty());
					etReturnDeliverQty.setText(delDetail.getReturnedDeliverQty());
					txtProductName.setText(delDetail.getProductName());
					txtOrderQty.setText(delDetail.getOrderQty());
					txtCustomerReturnQty.setText(delDetail.getNeedToReturnDeliverQty());
					txtProductLimit.setText(delDetail.getProductLimit());


					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface arg0, int arg1) 
						{
							int orderQty = 0;
							int productLimit =0;
							int tempRemainingQty =0;
							double sellingPrice =0;
							orderQty = Integer.parseInt(delDetail.getOrderQty());					
								txtDeliverQty.setText(deliverQty+"");
								txtReturnDeliverQty.setText(returnDeliveryQty+"");
								sellingPrice = Double.parseDouble(delDetail.getPrice());
								if(HomePage.itemDiscountList.size() > 0)
								{
									HomePage.calculateItemDiscount(delDetail.getProductID(),delDetail.getDiscountType(),totalDeliveryQty,delDetail.getPrice());
									delDetail.setItemDiscount(HomePage.itemDiscount+"");
									txtDiscount.setText(formatter.format(HomePage.itemDiscount)+"");
									totalitemDiscounts += HomePage.itemDiscount ;
									totalAmount = (int)((sellingPrice * totalDeliveryQty) - HomePage.itemDiscount);
									delDetail.setTotalAmt(totalAmount+"");
									txttotalAmt.setText(formatter.format(Integer.parseInt(delDetail.getTotalAmt())));
								}
								else
								{
									totalAmount= (int) (sellingPrice * totalDeliveryQty);
									Log.e(totalAmount+"","TotalAmount");
									delDetail.setTotalAmt(totalAmount + "");
									txttotalAmt.setText(formatter.format(Integer.parseInt(delDetail.getTotalAmt())));
								}
								
								tmpProList.clear();
								DeliveryProduct dp2 = new DeliveryProduct();
								dp2.setProductID(delDetail.getProductID());
								dp2.setProductName(delDetail.getProductName());

								dp2.setOrderDate(delDetail.getOrderDate());
								dp2.setOrderQty(delDetail.getOrderQty());
								dp2.setDeliverQty(txtDeliverQty.getText().toString().trim());
								delDetail.setDeliverQty(dp2.getDeliverQty());
								dp2.setReturnedDeliverQty(returnDeliveryQty+"");
								delDetail.setReturnedDeliverQty(dp2.getReturnedDeliverQty());
								dp2.setPrice(delDetail.getPrice());
								dp2.setItemDiscount(delDetail.getItemDiscount());
								dp2.setDiscountType(delDetail.getDiscountType());
								dp2.setTotalAmt(delDetail.getTotalAmt());
								remianingQty = Integer.parseInt(delDetail.getRemainingQty());
								Log.e(remianingQty+"","REAA");
								Log.e(deliverQty +"","DeLIver");
								tempRemainingQty = remianingQty - deliverQty ;
								dp2.setRemainingQty(tempRemainingQty + "");
								txtRemainingQty.setText(dp2.getRemainingQty());
								
								tmpProList.add(dp2);

								btnDeliver.setBackgroundColor(Color.RED);

								remianingQty = 0;
								remianingQty = orderQty - deliverQty;

								if(remianingQty == 0)
								{
									Toast.makeText(Delivery.this, "You have delivered all order quantity", Toast.LENGTH_LONG).show();
								}
								else
								{
									Toast.makeText(Delivery.this, "Your delivered quantity is >> "+deliverQty +"\n\nYour Remain Deliver Qty is >> "+remianingQty, Toast.LENGTH_LONG).show();
								}								
						}

					});

					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
					{

						@Override
						public void onClick(DialogInterface arg0, int arg1)
						{

						}
					});

					builder.show();
				}
			});

			return rowView;
		}
	}

	public View deliverQtydynamicLayout()
	{
		LayoutInflater inflater1=(LayoutInflater)Delivery.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row1=inflater1.inflate(R.layout.dynamiclayout_deliverquantity, null);
		layoutforDeliverQty=(LinearLayout) row1.findViewById(R.id.layoutforSaleQty);
		etdeliverQty=(EditText)row1.findViewById(R.id.etDeliverQty);		

		return row1;
	}			

	public void onBackPressed()
	{
		startActivity(new Intent(Delivery.this, HomePage.class));
		finish();
		Customer.invoiceList.clear();
		super.onBackPressed();
	}

	private void getDeliveryDataFromDB(String myInvID) 
	{

		if (DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();

		String sql = "SELECT customerID,saleOrderNo,totalAmt,advancePay,remainingAmt FROM  Delivery WHERE trim(saleOrderNo) LIKE '"+myInvID+"'";
		Cursor cur = DBCalss.eliteDB.rawQuery(sql, null);
		Log.e("Cursor count in Delivery", cur.getCount() + "");


		while(cur.moveToNext())
		{
			String customerID = cur.getString(cur.getColumnIndex("customerID"));
			String invoiceID = cur.getString(cur.getColumnIndex("saleOrderNo"));			
			String totalAmt = cur.getString(cur.getColumnIndex("totalAmt"));			
			String advancePay = cur.getString(cur.getColumnIndex("advancePay"));		
			String remainingAmt= cur.getString(cur.getColumnIndex("remainingAmt"));

			DeliveryProductDetail dpd = new DeliveryProductDetail();
			dpd.setCustomerID(customerID);
			dpd.setInvoiceID(invoiceID);
			dpd.setTotalAmt(totalAmt);
			dpd.setAdvancePay(advancePay);
			dpd.setRemainingAmt(remainingAmt);
			
			if(dpd.getRemainingAmt() != null || !dpd.getRemainingAmt().equals(""))
			{
				remainingAmtFromServer = Integer.parseInt(dpd.getRemainingAmt());
			}
			
			String sql2 = "SELECT productID,productName,orderQty,remainingQty,returnDeliverQty,price,itemDiscount,totalAmt,invoiceID,discountType,productLimit,remainingQty FROM DeliveryProduct WHERE trim(invoiceID) LIKE '"+invoiceID+"'";
			Cursor curProduct = DBCalss.eliteDB.rawQuery(sql2, null);
			Log.e("Cursor count in DeliveryProduct", curProduct.getCount() + "");
			while(curProduct.moveToNext())
			{	
				String productID = curProduct.getString(curProduct.getColumnIndex("productID"));				
				String productName = curProduct.getString(curProduct.getColumnIndex("productName"));				
				String orderQty = curProduct.getString(curProduct.getColumnIndex("orderQty"));			
				String returnDeliverQty = curProduct.getString(curProduct.getColumnIndex("returnDeliverQty"));
				String price = curProduct.getString(curProduct.getColumnIndex("price"));
				String itemDiscount  = curProduct.getString(curProduct.getColumnIndex("itemDiscount"));
				String totalamt  = curProduct.getString(curProduct.getColumnIndex("totalAmt"));			
				String discountType = curProduct.getString(curProduct.getColumnIndex("discountType"));
				String productLimit = curProduct.getString(curProduct.getColumnIndex("productLimit"));
				String remainingQty = curProduct.getString(curProduct.getColumnIndex("remainingQty"));

				DeliveryProduct dp = new DeliveryProduct();
				dp.setProductID(productID);
				dp.setProductName(productName);
				dp.setOrderQty(orderQty);
				dp.setNeedToReturnDeliverQty(returnDeliverQty);
				dp.setPrice(price);
				dp.setItemDiscount(itemDiscount);
				dp.setTotalAmt(totalamt);
				dp.setDiscountType(discountType);
				dp.setProductLimit(productLimit);
				dp.setRemainingQty(remainingQty);

				delProductList.add(dp);
				Log.e("DiscountTypeeeeee", dp.getDiscountType());
				Log.e("Product Size", delProductList.size()+"");
			}

			deliverList.add(dpd);
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();

		lstDeliverAda.notifyDataSetChanged();
	}

	private void catchEvents() 
	{
		spnInvoice.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0,View arg1, int arg2,long arg3)
			{
				clickPosForSpinner=spnInvoice.getSelectedItemPosition();
				Invoice invoice=invoiceList.get(clickPosForSpinner);
				Log.e(invoice.getInvoiceID(), "Selected InvoiceID");
				Log.e("Order Date", invoice.getOrderDate());
				txtOrderDate.setText(invoice.getOrderDate());
				chooseInvoiceID=invoice.getInvoiceID();
				Log.e("Process Inv", chooseInvoiceID);
				deliverList.clear();
				delProductList.clear();
				tmpProList.clear();
				Log.e("After two", "Clear");
				getDeliveryDataFromDB(chooseInvoiceID);
				lstDeliverAda.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(
					AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View arg0) 
			{
				startActivity(new Intent(Delivery.this, Customer.class));
				finish();
			}
		});

		btnFinish.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Log.e(tmpProList.size()+"","ARRSize");
				if(tmpProList.size() == 0)
				{
					final AlertDialog diag3 = new AlertDialog.Builder
							(
									Delivery.this)
					.setTitle("Alert!")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(
							"\nPlease Select InvoiceID and Deliver Product.\n")
							.setPositiveButton(
									"OK",
									new android.content.DialogInterface.OnClickListener()
									{
										@Override
										public void onClick(
												DialogInterface diag2,
												int arg1) {

										}
									}).create();
					diag3.show();
					diag3.setCancelable(false);
					diag3.setCanceledOnTouchOutside(false);
				}
				else
				{
					Intent i= new Intent(Delivery.this, Delivery2.class);
					startActivityForResult(i, 1111);
				}
			}
		});	
	}

	public class InvoiceListSpinnerAdapter implements SpinnerAdapter
	{
		ArrayList<Invoice> invoiceSpinnerList;
		public InvoiceListSpinnerAdapter(ArrayList<Invoice> invoiceList)
		{
			this.invoiceSpinnerList = Customer.invoiceList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return invoiceSpinnerList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return invoiceSpinnerList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getView(int pos, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub

			return spinnerRow(pos);
		}

		public View spinnerRow(int pos)
		{
			TextView v = new TextView(Delivery.this);
			v.setText(Customer.invoiceList.get(pos).getInvoiceID());
			//	Log.e(invoiceList.get(pos).getInvoiceID(),"InvoiceID");
			//Log.e(v.getText().toString(), "INVOICEID");
			v.setPadding(10, 10, 0, 10);
			v.setTextSize(18);
			return v;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public View getDropDownView(int pos, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub


			return spinnerRow(pos);
		}
	}

	private void registerIDs() 
	{
		txtDelivery = (TextView)findViewById(R.id.deliverypage_txtDelivery);
		btnFinish = (Button)findViewById(R.id.deliverypage_btnFinish);
		btnCancel = (Button)findViewById(R.id.deliverypage_btnCancel);
		lstDelivery = (ListView)findViewById(R.id.deliverypage_lstProduct);
		spnInvoice = (Spinner)findViewById(R.id.spinnerInvoice);
		txtOrderDate =(TextView) findViewById(R.id.deliverypage_txtOrderedDate);
	}

}
