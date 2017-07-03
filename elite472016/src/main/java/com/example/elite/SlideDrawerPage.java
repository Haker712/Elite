package com.example.elite;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import bean.classes.SlideProduct;

public class SlideDrawerPage 
{
	Activity activity;
	ListView lstSlideProduct;
	TextView txtBringQty, txtDeliverQty, txtProductName, txtRemainQty;
	
	ArrayList<SlideProduct> slideProductList = new ArrayList<SlideProduct>();
	
	public SlideDrawerPage(Activity activity) 
	{
		this.activity = activity;
	}
	
	public void makeSlidingDrawer()
	{
		registerIDs();
		addDataIntoList();
		setAdapters();
	}
	
	private void registerIDs()
	{
		lstSlideProduct = (ListView) activity.findViewById(R.id.slide_lstSlideProduct);
	}

	private void setAdapters()
	{
		lstSlideProduct.setAdapter(new CustomList(activity));
	}

	private void addDataIntoList()
	{
		SlideProduct sp = new SlideProduct();
		sp.setBringQty("2000");
		sp.setDelivreQty("1000");
		sp.setProductName("CDMA 800 (5000 Ks)");
		sp.setRemainQty("1000");
		slideProductList.add(sp);
		
		SlideProduct sp2 = new SlideProduct();
		sp2.setBringQty("2000");
		sp2.setDelivreQty("1000");
		sp2.setProductName("CDMA 800 (5000 Ks)");
		sp2.setRemainQty("1000");
		slideProductList.add(sp2);
		
		SlideProduct sp3 = new SlideProduct();
		sp3.setBringQty("2000");
		sp3.setDelivreQty("1000");
		sp3.setProductName("CDMA 800 (5000 Ks)");
		sp3.setRemainQty("1000");
		slideProductList.add(sp3);
		
		SlideProduct sp4 = new SlideProduct();
		sp4.setBringQty("2000");
		sp4.setDelivreQty("1000");
		sp4.setProductName("CDMA 800 (5000 Ks)");
		sp4.setRemainQty("1000");
		slideProductList.add(sp4);
		
		SlideProduct sp5 = new SlideProduct();
		sp5.setBringQty("2000");
		sp5.setDelivreQty("1000");
		sp5.setProductName("CDMA 800 (5000 Ks)");
		sp5.setRemainQty("1000");
		slideProductList.add(sp5);
		
		SlideProduct sp6 = new SlideProduct();
		sp6.setBringQty("2000");
		sp6.setDelivreQty("1000");
		sp6.setProductName("CDMA 800 (5000 Ks)");
		sp6.setRemainQty("1000");
		slideProductList.add(sp6);
		
		SlideProduct sp7 = new SlideProduct();
		sp7.setBringQty("2000");
		sp7.setDelivreQty("1000");
		sp7.setProductName("CDMA 800 (5000 Ks)");
		sp7.setRemainQty("1000");
		slideProductList.add(sp7);
		
		SlideProduct sp8 = new SlideProduct();
		sp8.setBringQty("2000");
		sp8.setDelivreQty("1000");
		sp8.setProductName("CDMA 800 (5000 Ks)");
		sp8.setRemainQty("1000");
		slideProductList.add(sp8);
		
		SlideProduct sp9 = new SlideProduct();
		sp9.setBringQty("2000");
		sp9.setDelivreQty("1000");
		sp9.setProductName("CDMA 800 (5000 Ks)");
		sp9.setRemainQty("1000");
		slideProductList.add(sp9);
		
		SlideProduct sp10 = new SlideProduct();
		sp10.setBringQty("2000");
		sp10.setDelivreQty("1000");
		sp10.setProductName("CDMA 800 (5000 Ks)");
		sp10.setRemainQty("1000");
		slideProductList.add(sp10);
		
		SlideProduct sp11 = new SlideProduct();
		sp11.setBringQty("2000");
		sp11.setDelivreQty("1000");
		sp11.setProductName("CDMA 800 (5000 Ks)");
		sp11.setRemainQty("1000");
		slideProductList.add(sp11);
	}

	public class CustomList extends ArrayAdapter<SlideProduct>
	{
		private final Activity context;
		
		
		public CustomList(Activity context)
		{
			super(context, R.layout.slide_drawer_row, slideProductList);
			this.context = context; 		
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.slide_drawer_row, null, true);
			
			TextView txtBringQty = (TextView) rowView.findViewById(R.id.slideRow_txtBringQty);
			TextView txtDeliverQty = (TextView) rowView.findViewById(R.id.slideRow_txtDeliverQty);
			TextView txtProductName = (TextView) rowView.findViewById(R.id.slideRow_txtProductName);
			TextView txtRemainQty = (TextView) rowView.findViewById(R.id.slideRow_txtRemainQty);
						
			SlideProduct sp = slideProductList.get(position);
			
			txtBringQty.setText(sp.getBringQty());
			txtDeliverQty.setText(sp.getDelivreQty());
			txtProductName.setText(sp.getProductName());
			txtRemainQty.setText(sp.getRemainQty());
						
			return rowView;
		}
	}
}
