package com.example.elite;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import custom.scroll.LockableScrollView;
import drawing.canvas.DrawingCanvas;

public class AdvancePayment extends Activity {

	TextView txtAdvPayment,txtAdvPayDate,txtProduct,txtDeliveryDate,txtOrderedQty,txtPrice,txtTotalAmt;
	ListView lstProduct;
	Spinner spn_InvoiceNo;
	TextView txtAllTotalAmt,txtAdvPayAmt,txtDiscount,txtNetAmt,txtRealDeliveryDate,txtCreditTerms;
	EditText etPaymentAmt;
	DrawingCanvas drawingCanvas;
	CheckBox chbApprove;
	Button btnConfirm,btnPrint,btnClear;
	LockableScrollView scrollView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.advancepaymentpage);
		
		hideScreenKeyboardInPageDisplay();
		registerIDs();
		
		catchEvents();
	}
	
	private void hideScreenKeyboardInPageDisplay()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);		
	}


	private void catchEvents() {

		btnClear.setOnClickListener(new OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				drawingCanvas.startNew();		
			}
		});
		
		drawingCanvas.setOnTouchListener(new OnTouchListener()
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					scrollView.setScrollingEnabled(false);
					break;				
				case MotionEvent.ACTION_UP:
					scrollView.setScrollingEnabled(true);
					break;				
				}
				return false;
			}
		});	
	
	}

	private void registerIDs() {
		txtAdvPayment = (TextView)findViewById(R.id.advpaymentpage_txtAdvancePayment);
		txtAdvPayDate = (TextView)findViewById(R.id.advpaymentpage_txtDate);
		txtProduct = (TextView)findViewById(R.id.advpaymentpage_txtProduct);
		txtDeliveryDate = (TextView)findViewById(R.id.advpaymentpage_txtDeliveryDate);
		txtOrderedQty = (TextView)findViewById(R.id.advpaymentpage_txtOrderQty);
		txtPrice = (TextView)findViewById(R.id.advpaymentpage_txtPrice);
		txtTotalAmt = (TextView)findViewById(R.id.advpaymentpage_txtTotalAmt);
		lstProduct = (ListView)findViewById(R.id.advpaymentpage_lstProduct);
		txtAllTotalAmt = (TextView)findViewById(R.id.advpaymentpage_txtTotalAmountNo);
		txtAdvPayAmt = (TextView)findViewById(R.id.advpaymentpage_txtAdvPayAmtNo);
		txtDiscount = (TextView)findViewById(R.id.advpaymentpage_txtDiscountNo);
		txtNetAmt = (TextView)findViewById(R.id.advpaymentpage_txtNetAmtNo);
		
		txtRealDeliveryDate = (TextView)findViewById(R.id.advpaymentpage_txtDeliDateNo);
		txtCreditTerms = (TextView)findViewById(R.id.advpaymentpage_txtCreditTermsNo);
		etPaymentAmt = (EditText)findViewById(R.id.advpaymentpage_txtPayAmtNo);
		drawingCanvas = (DrawingCanvas)findViewById(R.id.advpaymentpage_drawingCanvas);
		btnClear = (Button)findViewById(R.id.advpaymentpage_btnClear);
		chbApprove = (CheckBox)findViewById(R.id.advpaymentpage_chkApprove);
		btnConfirm = (Button)findViewById(R.id.advpaymentpage_btnConfirm);
		btnPrint = (Button)findViewById(R.id.advpaymentpage_btnPrint);
		
	}
	

}
