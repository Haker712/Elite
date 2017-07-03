package com.quick.scroll.files;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import bean.classes.CustomerInfo;

import com.andraskindler.quickscroll.Scrollable;
import com.example.elite.Customer;
import com.example.elite.R;



public class ContactAdapter extends BaseAdapter implements Scrollable {

	private final List<String> mContacts;
	private final List<String> mAddress;
	private final List<String> misInRoute;
	private final Context mContext;
	private ArrayList<CustomerInfo> myCustomerList;

	public ContactAdapter(final Context context, ArrayList<CustomerInfo> customerList) {
		mContext = context;	
		myCustomerList = customerList;
		mContacts = new ArrayList<String>();
		mAddress = new ArrayList<String>();
		misInRoute = new ArrayList<String>();
		
		for(int i=0;i<myCustomerList.size();i++)
		{
			 mContacts.add(myCustomerList.get(i).getCustomerName());
			 mAddress.add(myCustomerList.get(i).getCustomerAddress());
			 misInRoute.add(myCustomerList.get(i).getCustomerIsInRoute());
		}
		
		/*for (String name : Contacts.sContacts) {
			mContacts.add(name);
		}*/

	}

	@Override
	public int getCount() {
		return mContacts.size();
	}

	@Override
	public Object getItem(int position) {
		return mContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
//	public void setSelectedItem(int position) {
//	    selectedItem = position;
//	}
	
	
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		
		
		LinearLayout listLayout = new LinearLayout(mContext);
        listLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,AbsListView.LayoutParams.WRAP_CONTENT));      
        listLayout.setOrientation(LinearLayout.VERTICAL);
        TextView listText = new TextView(mContext);
        listText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        listText.setPadding(24, 10, 0, 10); 
        TextView listAddress = new TextView(mContext);
        listAddress.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        listAddress.setPadding(24, 10, 0, 10);       
        listText.setText(mContacts.get(position).trim());
        listAddress.setText("( "+mAddress.get(position).trim()+" )");
        
        if(misInRoute.get(position).equals("true"))
        {
        	listText.setTextColor(Color.parseColor("#f18522"));
        	listAddress.setTextColor(Color.parseColor("#f18522"));
        }
        else
        {
        	listText.setTextColor(Color.WHITE);
        	listAddress.setTextColor(Color.WHITE);        	
        }
        

        listLayout.addView(listText);
		listLayout.addView(listAddress);
		
		LinearLayout activeItem = (LinearLayout)  listLayout ;
		if (position == Customer.selectedItem)
		{
			activeItem.setBackgroundResource(R.drawable.blue);
			Customer.selectedItem = -1;
		}
	
		return listLayout;
	}

	@Override
	public String getIndicatorForPosition(int childposition, int groupposition) {
		return Character.toString(mContacts.get(childposition).charAt(0));
	}

	@Override
	public int getScrollPosition(int childposition, int groupposition) {
		return childposition;
	}

}
