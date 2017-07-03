package com.print;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.elite.HomePage;
import com.example.elite.R;
import com.example.elite.SalePage2;

public class PrintActivity extends Activity {
	public static PrinterClass pl=null;
	Button printBtn,exitBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_print_item);//pp activity_print_items
		//setListAdapter(new SimpleAdapter(this,getData("simple-list-item-2"),android.R.layout.simple_list_item_2,new String[]{"title", "description"},new int[]{android.R.id.text1, android.R.id.text2}));  
		
		printBtn = (Button) findViewById(R.id.print_btn);//pp code
		exitBtn = (Button) findViewById(R.id.exit_btn);
		printBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 Intent intent = new Intent();
			        intent.setClass(PrintActivity.this,
			    			PrintTextActivity.class);
			        startActivity(intent);
			        finish();
			}
		});
		
		exitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(PrintActivity.this, HomePage.class));
				finish();
			}
		});
		
		if(PrintActivity.pl.getState() != PrinterClass.STATE_CONNECTED)
		{
			SalePage2.checkState=false;
			finish();
		}
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data)
//	{
//		Log.e("Request Code", requestCode + "");
//		Log.e("Printer state", PrinterClass.STATE_CONNECTED + "");
//		switch (requestCode)
//		{
//		case 0:
//			if(PrintActivity.pl.getState() != PrinterClass.STATE_CONNECTED)
//			{
//				PrintActivity.this.finish();
//			} else {
//				PrintActivity.pl.printText("Test Message"+"\r\n");
//			}
//			break;
//		default:
//			break;
//		}
//	}
//	
//	
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		PrintActivity.pl.disconnect();
//		super.onDestroy();
//	}
//	
//	/**
//     * ��List���ѡ��ʱ����
//     */
//    protected void onListItemClick(ListView listView, View v, int position, long id) {
//        Map map = (Map)listView.getItemAtPosition(position);
//        //Toast toast = Toast.makeText(this, map.get("title")+" is selected.", Toast.LENGTH_LONG);
//        //toast.show();
//        Intent intent = new Intent();
//        intent.setClass(PrintActivity.this,
//    			PrintTextActivity.class);
//        startActivity(intent);
//        finish();
//        
//   /* 	Intent intent= new Intent();
//    	switch (position) {
//		case 0:
//			intent.setClass(PrintActivity.this,
//	    			PrintTextActivity.class);
//			break;
//		case 1:
//			intent.setClass(PrintActivity.this,
//	    			PrintImageActivity.class);
//			break;
//		case 2:
//			intent.setClass(PrintActivity.this,
//	    			PrintBarCodeActivity.class);
//			break;
//		case 3:
//			intent.setClass(PrintActivity.this,
//	    			PrintQrCodeActivity.class);
//			break;
//		case 4:
//			intent.setClass(PrintActivity.this,
//	    			PrintCmdActivity.class);
//			break;
//		default:
//			break;
//		}
//		////intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		startActivity(intent);*/
//    }
//	
//	/**
//     * ����SimpleAdapter�ĵڶ�������������ΪList<Map<?,?>>
//     * @param title
//     * @return
//     */
//    private List<Map<String, String>> getData(String title) {
//    	List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
//    	
//    		Map<String, String> map = new HashMap<String, String>();
//    		    		map.put("title", "print");
//    		map.put("description", "print");
//    		listData.add(map);
//    	
//    	return listData;
//    }
	@Override
	public void onBackPressed() {
		startActivity(new Intent(PrintActivity.this, HomePage.class));
		finish();
		super.onBackPressed();
	}
}
