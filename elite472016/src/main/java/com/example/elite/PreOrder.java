
package com.example.elite;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import bean.classes.Discount;
import bean.classes.PreOrderProduct;

import com.example.myjazzyviewtest.JazzyViewPager;
import com.example.myjazzyviewtest.JazzyViewPager.TransitionEffect;
import com.example.myjazzyviewtest.OutlineContainer;

import custom.classes.DBCalss;

public class PreOrder extends Activity {
	
	public static ArrayList<PreOrderProduct> preOrderProductList = new ArrayList<PreOrderProduct>();

	JazzyViewPager mJazzy;
	MainAdapter mAda;
	TextView txtPreOrder, txtNetAmount,txtPageName,txtProductType,txtTodayDate;
	Button btnCancel, btnFinish;
	PreOrderProductAda preOrderProductAda;
	ImageButton btnMenuBack,btnMenuHome;
	ListView preorderList;
	EditText etpreOrderQty;
	LinearLayout layoutforPreOrderQty;
	DecimalFormat formatter = new DecimalFormat("###,##0"); 
	AutoCompleteTextView autoCompleteSearch;
	String  dis_amountStr = null;
	ArrayList<String> insertedMenuItem = new ArrayList<String>();
	ArrayList<Discount> discountlist = new ArrayList<Discount>();
	ArrayList<PreOrderProduct> tempList = new ArrayList<PreOrderProduct>();
	ArrayList<PreOrderProduct> allPreOrderProductList = new ArrayList<PreOrderProduct>();
	int currentMenuType = 0; // 0 = Category , 1 = Sub Category , 2 = Item
	int totalMenuJazzyCount = 0;
	int mYear, mMonth, mDay;
	int totalAmt = 0;
	String previousMenuName = "";
	static int remainQty= 0;
	LinearLayout layoutforSaleQty;
	EditText etsaleQty;
	ArrayAdapter  searchAdapter;
	String saleqtynullProduct;
	Boolean saleqtyExitorNot= false;
	ArrayList<String> searchShowList = new ArrayList<String>();
	ArrayList<ArrayList<PreOrderProduct>> allMenuList = new ArrayList<ArrayList<PreOrderProduct>>();
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preorderpage);

		hideScreenKeyboardInPageDisplay();
		registerIDs();
		getPreOrderProdctFromDB();
		setAdapters();
		setupJazziness(TransitionEffect.CubeOut);
		setMenuIntoJazzyForCategory();
		
		catchEvent();
	}
	
	public void onBackPressed() 
	{
		if(preOrderProductList.size() > 0)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(PreOrder.this);					
			builder.setTitle("Alert Message");
			builder.setMessage("Your selected Items will be delete!");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{	
					preOrderProductList.clear();
					startActivity(new Intent(PreOrder.this, Customer.class));
					finish();
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
		else
		{
			startActivity(new Intent(PreOrder.this,Customer.class));
			finish();
		}
	}

	private void setMenuIntoJazzyForCategory() 
	{
		searchShowList.clear();
		boolean isInsertedflag;

		for(int i=0; i < allPreOrderProductList.size(); i++)
		{
			isInsertedflag = false;
			Log.e("Category", "Category");

			for(String insertedItem : searchShowList)
			{
				Log.e("Category 2", "Category 2");

				if(allPreOrderProductList.get(i).getCategoryName().equalsIgnoreCase(insertedItem))
				{
					Log.e("Category 3", "Category 3");

					isInsertedflag = true;
					break;
				}
			}
			if(!isInsertedflag)
			{
				Log.e("Category 4", "Category 4");

				//tempList.add(allSaleProductList.get(i));
				searchShowList.add(allPreOrderProductList.get(i).getCategoryName());
				//count++;

				Log.e("i", i + "");
			}
		}
		searchAdapter = new ArrayAdapter<String>(this,R.layout.layout_for_autocompletext,searchShowList);
		autoCompleteSearch.setAdapter(searchAdapter);
		allMenuList.clear();
		insertedMenuItem.clear();
		totalMenuJazzyCount = 0;
		int count = 0;
		boolean isInserted = false;

		for(int i=0; i<allPreOrderProductList.size(); i++)
		{
			isInserted = false;
			Log.e("Category", "Category");

			for(String insertedItem : insertedMenuItem)
			{
				Log.e("Category 2", "Category 2");
				Log.e("****",allPreOrderProductList.get(i).getCategoryName());
				if(allPreOrderProductList.get(i).getCategoryName().equalsIgnoreCase(insertedItem))
				{
					Log.e("Category 3", "Category 3");

					isInserted = true;
					break;
				}
			}

			if(!isInserted)
			{
				Log.e("Category 4", "Category 4");

				tempList.add(allPreOrderProductList.get(i));
				insertedMenuItem.add(allPreOrderProductList.get(i).getCategoryName());
				count++;

				Log.e("i", i + "");
			}

			if(count ==4 || i == (allPreOrderProductList.size()-1))
			{
				allMenuList.add(tempList);
				tempList = new ArrayList<PreOrderProduct>();
				count = 0;
				totalMenuJazzyCount++;
			}
		}

		Log.e("menu size", allMenuList.size() + "");
	
	}
	
	private void setMenuIntoJazzyForItem(String productName)
	{
		allMenuList.clear();				
		totalMenuJazzyCount = 0;
		int count = 0;
		
		for(int i=0; i<allPreOrderProductList.size(); i++)
		{	
			Log.e("Sel product", productName);

			if(allPreOrderProductList.get(i).getProductName().equals(productName))
			{
				Log.e("product1", "product1");

				tempList.add(allPreOrderProductList.get(i));
				Log.e(tempList.size()+"","TEMP");
				count++;
				Log.e("COUNT", count + "");
			}
			
			if(count == 4 || i == (allPreOrderProductList.size()-1))
			{
				Log.e("product2", "product2");

				allMenuList.add(tempList);
				tempList = new ArrayList<PreOrderProduct>();
				count = 0;
				totalMenuJazzyCount++;
			}
		}
		currentMenuType = 2;            // Current Menu Type is Product	
		mAda.notifyDataSetChanged();					
		txtProductType.setText("Item");		
		Log.e("product size", allMenuList.size() + "");
	}

	private void getPreOrderProdctFromDB()
	{

		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		

		String[] selCol = {"categoryID", "categoryName", "groupID", "productID", "productName","totalQty","sellingPrice", "purchasePrice", "groupName","discountType"};
		cur = DBCalss.eliteDB.query("PreOrderProduct", selCol, null, null, null, null, null);

		while(cur.moveToNext())
		{	
			String categoryID = cur.getString(cur.getColumnIndex("categoryID"));
			String categoryName = cur.getString(cur.getColumnIndex("categoryName"));
			String groupId = cur.getString(cur.getColumnIndex("groupID"));
			String productId = cur.getString(cur.getColumnIndex("productID"));
			String productName = cur.getString(cur.getColumnIndex("productName"));
			String sellingPrice = cur.getString(cur.getColumnIndex("sellingPrice"));
			String purchasePrice = cur.getString(cur.getColumnIndex("purchasePrice"));
			String groupName = cur.getString(cur.getColumnIndex("groupName"));
			String discountType=cur.getString(cur.getColumnIndex("discountType"));
			String totalQty=cur.getString(cur.getColumnIndex("totalQty"));
			Log.e(discountType,"DiscountType");

			PreOrderProduct pop = new PreOrderProduct();
			pop.setCategoryId(categoryID);
			pop.setCategoryName(categoryName);
			pop.setGroupId(groupId);
			pop.setProductId(productId);
			pop.setProductName(productName);
			pop.setSellingPrice(sellingPrice);
			pop.setPurchasePrice(purchasePrice);
			pop.setGroupName(groupName);
			pop.setDiscountType(discountType);
			pop.setTotalQty(totalQty);
			Log.e(pop.getTotalQty(),"TotalQty");

			allPreOrderProductList.add(pop);
			Log.e("Product Size Check", allPreOrderProductList.size()+"");
		}

		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	
	}

	private void hideScreenKeyboardInPageDisplay() 
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	private void catchEvent() 
	{
		preorderList.setOnItemLongClickListener(new OnItemLongClickListener() 
		{
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,                                                                   
					int arg2, long arg3) {
				final int position = arg2;
				// TODO Auto-generated method stub
				
				final AlertDialog dia = new AlertDialog.Builder(PreOrder.this)
				.setTitle("Information")
				.setMessage("\n Are you sure you want to delete.\n")
				.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{	
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						preOrderProductList.remove(position);
						preOrderProductAda.notifyDataSetChanged();
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{	
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						
					}
				})
				.create();				
				dia.show();
				
				return false;
			}
		});
		
		btnFinish.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v) 
			{
				Log.e(checksaleQtyExitorNot()+"","checkQtyExistorNot");
				if(preOrderProductList.size() == 0)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(PreOrder.this);					
					builder.setTitle("Information");
					builder.setMessage("Please Choose Item for Check Out!");
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which) 
						{	
							
						}
					});

					builder.show();
				}
				else
				{
					if(checksaleQtyExitorNot() == true)
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(PreOrder.this);					
						builder.setTitle("Information");
						builder.setMessage("Qty of"+" '"+ saleqtynullProduct +"' "+"must not be zero");
						builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int which) 
							{	
								
							}
						});

						builder.show();
					}
					else
					{
						startActivity(new Intent(PreOrder.this, AdvancePaymentPreOrder.class));
						finish();
					}
				}
			}
		});
		mJazzy.setOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int pos)
			{
				int i =0;
				i= pos +1;
				Log.e("sel", "sel" + pos);
				txtPageName.setText(i + " of " + allMenuList.size()+"");
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
					
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) 
			{
				
			}
		});
		
		autoCompleteSearch.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				autoCompleteSearch.setText("");
			}
		});
		
		autoCompleteSearch.setOnItemClickListener(new OnItemClickListener() 
		{

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{
				// TODO Auto-generated method stub
				String selItemName = autoCompleteSearch.getText().toString();
				Log.e("check type",selItemName);

				if(currentMenuType==0)
				{					
					setMenuIntoJazzyForGroup(selItemName);					
				}
				else if(currentMenuType == 1)
				{
					setMenuIntoJazzyForProduct(selItemName);
				}
				else if(currentMenuType ==2 )
				{
					setMenuIntoJazzyForItem(selItemName);
				}

				autoCompleteSearch.setText(selItemName);			

				Log.e("click", "click");								
			}
		});
		
		btnMenuBack.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				if(currentMenuType == 1)		// Will do if SubMenu
				{
					allMenuList.clear();
					setMenuIntoJazzyForCategory();

					currentMenuType = 0;			// Current Menu Type is Category
					mAda.notifyDataSetChanged();
					previousMenuName = "";

					txtProductType.setText("Category");
				}
				else if(currentMenuType == 2)			// Will do if Item
				{
					setMenuIntoJazzyForGroup(previousMenuName);
				}
			}
		});
		
		btnMenuHome.setOnClickListener(new OnClickListener()
		{	
			@Override
			public void onClick(View arg0)
			{
				allMenuList.clear();
				setMenuIntoJazzyForCategory();

				currentMenuType = 0;			// Current Menu Type is Category
				mAda.notifyDataSetChanged();
				previousMenuName = "";

				txtProductType.setText("Category");
			}
		});
	}
	
	public boolean checksaleQtyExitorNot()
	{
		for (PreOrderProduct pop : preOrderProductList)
		{
			Log.e(pop.getTempOrderQty()+"", "CCCCCCC");
			if(pop.getTempOrderQty().equals("0"))
			{
				saleqtyExitorNot = true;
				saleqtynullProduct= pop.getProductName();
				break;
			}
			else
			{
				saleqtyExitorNot= false;
			}
		}
		return saleqtyExitorNot;
	}

	private void setAdapters() 
	{
		preOrderProductAda = new PreOrderProductAda(this);
		preorderList.setAdapter(preOrderProductAda);
		searchAdapter = new ArrayAdapter<String>(this,R.layout.layout_for_autocompletext,searchShowList);
		autoCompleteSearch.setAdapter(searchAdapter);	
	}
	public class PreOrderProductAda extends ArrayAdapter<PreOrderProduct>
	{
		private final Activity context;


		public PreOrderProductAda(Activity context)
		{
			super(context, R.layout.preorder_custom_list_row, preOrderProductList);
			this.context = context; 		
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.preorder_custom_list_row, null, true);

			TextView txtProductName = (TextView) rowView.findViewById(R.id.preOrderListRow_txtProductName);
			final Button btnOrderedQty = (Button) rowView.findViewById(R.id.preOrderListRow_btnOrderedQty);			
			final TextView txtPrice = (TextView) rowView.findViewById(R.id.preOrderListRow_txtPrice);
			final TextView txtTotalAmt = (TextView) rowView.findViewById(R.id.preOrderListRow_txtTotalAmt);

			int price=0;
			Log.e(allPreOrderProductList.size()+"", "CCCCCCCCCC");
			final PreOrderProduct pop = preOrderProductList.get(position);  
			txtProductName.setText(pop.getProductName());
			btnOrderedQty.setText(pop.getTempOrderQty());

			
			btnOrderedQty.setOnClickListener(new OnClickListener() 
			{
					@Override
					public void onClick(View arg0) 
					{
						final AlertDialog d = new AlertDialog.Builder(PreOrder.this)					
						.setView(saleQtyDynamicLayout())
						.setTitle("Order Quantity")
						.setPositiveButton("Confirm", null)
						.setNegativeButton("Cancel", null)
						.create();
						d.setOnShowListener(new DialogInterface.OnShowListener() {
							
							@Override
							public void onShow(DialogInterface arg0) 
							{
								Button positiveBtn = d.getButton(AlertDialog.BUTTON_POSITIVE);
								positiveBtn.setOnClickListener(new OnClickListener()
								{
									@Override
									public void onClick(View v)
									{
										int intOrderQty= 0;
										String orderQty= "";
										orderQty= etsaleQty.getText().toString().trim();
										Log.e(orderQty, "AAAAAAAAA");
										int remainingQty=0;
										if(orderQty.equals(""))
										{
											Log.e("HERE","HERE");
											 AlertDialog diag3= new AlertDialog.Builder(PreOrder.this)
												.setTitle("Alert Message!")
												.setMessage("\nPlease Fill Quantity\n")		
												.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
												{
													@Override
													public void onClick(DialogInterface diag2, int arg1) 
													{
														
													}
												})
												.create();

												diag3.show();
										}
										else
										{
											intOrderQty =Integer.parseInt(orderQty);
													Log.e(intOrderQty+"", "intOrderQty");
													if(intOrderQty == 0)
													{
														btnOrderedQty.setText("0");
														txtTotalAmt.setText("0");
														pop.setTempOrderQty(intOrderQty +"");
													}
													else 
													{
														pop.setTempOrderQty(intOrderQty +"");
														btnOrderedQty.setText(pop.getTempOrderQty());
														int totalAmount=0;
														
														Log.e(HomePage.itemDiscountList.size()+"", "discountlistSize");
														
															Log.e("NoDiscount", "NoDiscount");
															double intsellingPrice=0;

															intsellingPrice= Double.parseDouble(pop.getSellingPrice());
															Log.e(intsellingPrice+"", "SellingPrice");
															totalAmount= (int) (intsellingPrice * intOrderQty);
															Log.e(totalAmount+"","TotalAmount");
															pop.setTotalAmt(totalAmount+"");
															txtTotalAmt.setText(formatter.format(Integer.parseInt(pop.getTotalAmt())));
													
													}
													d.dismiss();
											}
									}
								});
								
							}
						});
						
						d.show();
				}
			});
			
			price=(int) Double.parseDouble(pop.getSellingPrice());
			txtPrice.setText(formatter.format(price));
		
			if(btnOrderedQty.getText().toString()!= null)
			{	int preOrderQty = 0;
				preOrderQty = Integer.parseInt(btnOrderedQty.getText().toString().trim());
				
				totalAmt = preOrderQty * price;
			}
			txtTotalAmt.setText(formatter.format(totalAmt));
			Log.e(allPreOrderProductList.size()+"", "ArrSize");
			

			return rowView;
		}

		
	}
	
	public View saleQtyDynamicLayout()
	{
		LayoutInflater inflater1=(LayoutInflater)PreOrder.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row1=inflater1.inflate(R.layout.dynamiclayout_salequantity, null);
		layoutforSaleQty=(LinearLayout) row1.findViewById(R.id.layoutforSaleQty);
		etsaleQty=(EditText)row1.findViewById(R.id.etSaleQty);

		return row1;
	}
	
	public View preOrderQtyDynamicLayout() 
	{
		LayoutInflater inflater1=(LayoutInflater)PreOrder.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row1=inflater1.inflate(R.layout.dynamiclayout_preorderquantity, null);
		layoutforPreOrderQty=(LinearLayout) row1.findViewById(R.id.layoutforPreOrderQty);
		etpreOrderQty=(EditText)row1.findViewById(R.id.etPreOrderQty);

		return row1;
	}

	private void registerIDs()
	{
		txtPreOrder = (TextView)findViewById(R.id.preorderPage_txtPreOrder);
		txtNetAmount = (TextView)findViewById(R.id.preorderPage_txtAmountDigit);
		btnFinish = (Button)findViewById(R.id.preorderPage_btnFinish);
		preorderList = (ListView)findViewById(R.id.preorderPage_lstPreOrder);
		autoCompleteSearch = (AutoCompleteTextView)findViewById(R.id.preOrderPage_autoTxtSearch);
		txtPageName=(TextView) findViewById(R.id.preOrderPage_txtShowViewPagerPage);
		txtProductType = (TextView)findViewById(R.id.preOrderPage_txtProductType);
		btnMenuBack = (ImageButton) findViewById(R.id.salePage_btnMenuBack);
		btnMenuHome=(ImageButton) findViewById(R.id.salePage_btnMenuHome);
		txtTodayDate =(TextView) findViewById(R.id.preorderPage_txtOrderDate);
		
		Calendar todayCal = Calendar.getInstance();
		txtTodayDate.setText(fmtForDueStr.format(todayCal.getTime()));
	}
	
	private void setMenuIntoJazzyForGroup(String categoryName) 
	{
		Log.e("For", "Group");
		searchShowList.clear();
		boolean isInsertedflag=false;
		Log.e("PreOrderList Size", allPreOrderProductList.size()+"");
		for(int i=0; i<allPreOrderProductList.size(); i++)
		{
			Log.e("group", "group");
			Log.e("Check Cat name",allPreOrderProductList.get(i).getCategoryName());

			if(allPreOrderProductList.get(i).getCategoryName().equals(categoryName))
			{
				Log.e("group 1", "group 1");

				isInsertedflag = false;

				for(String insertedItem : searchShowList)
				{
					Log.e("group 2", allPreOrderProductList.get(i).getGroupName());

					if(allPreOrderProductList.get(i).getGroupName().equalsIgnoreCase(insertedItem))
					{
						Log.e("group 3", "group 3");

						isInsertedflag = true;
						break;
					}
				}

				if(!isInsertedflag)
				{
					Log.e("group 4", "group 4");					

					searchShowList.add(allPreOrderProductList.get(i).getGroupName());
				}							
			}
		}
		searchAdapter = new ArrayAdapter<String>(this,R.layout.layout_for_autocompletext,searchShowList);
		autoCompleteSearch.setAdapter(searchAdapter);
		allMenuList.clear();
		insertedMenuItem.clear();

		totalMenuJazzyCount = 0;
		int count = 0;
		boolean isInserted = false;

		for(int i=0; i< allPreOrderProductList.size(); i++)
		{
			Log.e("group", "group");
			Log.e("######",allPreOrderProductList.get(i).getCategoryName()+"");

			if(allPreOrderProductList.get(i).getCategoryName().equals(categoryName))
			{
				Log.e("group 1", "group 1");

				isInserted = false;

				for(String insertedItem : insertedMenuItem)
				{
					Log.e("group 2", allPreOrderProductList.get(i).getGroupName());

					if(allPreOrderProductList.get(i).getGroupName().equalsIgnoreCase(insertedItem))
					{
						Log.e("group 3", "group 3");

						isInserted = true;
						break;
					}
				}

				if(!isInserted)
				{
					Log.e("group 4", "group 4");

					tempList.add(allPreOrderProductList.get(i));
					insertedMenuItem.add(allPreOrderProductList.get(i).getGroupName());
					count++;								
				}							
			}

			if(count ==4 || i == (allPreOrderProductList.size()-1))
			{
				Log.e("group 4", "group 4");

				allMenuList.add(tempList);
				tempList = new ArrayList<PreOrderProduct>();
				count = 0;
				totalMenuJazzyCount++;
			}
		}

		currentMenuType = 1;			// Current Menu Type is Group
		mAda.notifyDataSetChanged();					
		previousMenuName = categoryName;
		txtProductType.setText("Group");  

		Log.e("group size", allMenuList.size() + "");
	}
	
	
	private void setMenuIntoJazzyForProduct(String groupName)
	{
		searchShowList.clear();
		boolean isInsertedflag=false;
		for(int i=0; i<allPreOrderProductList.size(); i++)
		{
			Log.e("group", "group");

			if(allPreOrderProductList.get(i).getGroupName().equals(groupName))
			{
				Log.e("group 1", "group 1");

				isInsertedflag = false;

				for(String insertedItem : searchShowList)
				{
					Log.e("group 2", allPreOrderProductList.get(i).getCategoryId());

					if(allPreOrderProductList.get(i).getProductName().equalsIgnoreCase(insertedItem))
					{
						Log.e("group 3", "group 3");

						isInsertedflag = true;
						break;
					}
				}

				if(!isInsertedflag)
				{
					Log.e("group 4", "group 4");					

					searchShowList.add(allPreOrderProductList.get(i).getProductName());

				}							
			}
		}
		searchAdapter = new ArrayAdapter<String>(this,R.layout.layout_for_autocompletext,searchShowList);
		autoCompleteSearch.setAdapter(searchAdapter);
		allMenuList.clear();				
		totalMenuJazzyCount = 0;
		int count = 0;

		for(int i=0; i< allPreOrderProductList.size(); i++)
		{	
			Log.e("Sel product", groupName);

			if(allPreOrderProductList.get(i).getGroupName().equals(groupName))
			{
				Log.e("product1", "product1");

				tempList.add(allPreOrderProductList.get(i));
				Log.e(tempList.size()+"","TEMP");
				count++;
				Log.e("COUNT", count + "");
			}

			if(count >4 || i == (allPreOrderProductList.size()-1))
			{
				Log.e("product2", "product2");

				allMenuList.add(tempList);
				tempList = new ArrayList<PreOrderProduct>();
				count = 0;
				totalMenuJazzyCount++;
			}
		}

		currentMenuType = 2;            // Current Menu Type is Product	
		mAda.notifyDataSetChanged();					
		txtProductType.setText("Item");
		Log.e("product size", allMenuList.size() + "");
	}
	
	

	private void setupJazziness(TransitionEffect effect) 
	{
		mJazzy = (JazzyViewPager) findViewById(R.id.jazzy_pager);
		mJazzy.setTransitionEffect(effect);
		mAda = new MainAdapter();
		mJazzy.setAdapter(mAda);
		mJazzy.setPageMargin(30);
	}

	
	private class MainAdapter extends PagerAdapter
	{
		@Override
		public Object instantiateItem(ViewGroup container, final int position) 
		{		
			Log.e(allMenuList.size()+"", "ARRSIZE");
			ArrayList<PreOrderProduct> tempList = allMenuList.get(position);
			Log.e(tempList.size()+"", "0000000");
			txtPageName.setText("1 of "+ allMenuList.size()+"");
			int count = 0;

			LayoutInflater inf = PreOrder.this.getLayoutInflater();
			LinearLayout llRow = new LinearLayout(PreOrder.this);
			llRow.setOrientation(LinearLayout.VERTICAL);
			llRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

			for(int j=0; j<4; j++)
			{
				LinearLayout llCol = new LinearLayout(PreOrder.this);
				llCol.setOrientation(LinearLayout.HORIZONTAL);
				llCol.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

				for(int i=0; i < 1; i++)
				{
					Log.e(tempList.size()+"", "TEMPSIZE");
					if(count < tempList.size())
					{
						
						PreOrderProduct preOrderProduct = tempList.get(count);

						View row = inf.inflate(R.layout.cell_for_jzay, null);
						row.setId(count);

						if(currentMenuType == 0)		// Current Type is Category
						{
							Log.e("Category", "Category");

							TextView txt = (TextView) row.findViewById(R.id.cellForJzay_txtProductName);
							Log.e("Category Name", preOrderProduct.getCategoryName());
							txt.setText(preOrderProduct.getCategoryName());
						}
						else if(currentMenuType == 1)	// Current Type is Group
						{
							Log.e("Group", "Group");

							TextView txt = (TextView) row.findViewById(R.id.cellForJzay_txtProductName);
							txt.setText(preOrderProduct.getGroupName());
						}
						else if(currentMenuType == 2)	// Current Type is Product	
						{
							TextView txt = (TextView) row.findViewById(R.id.cellForJzay_txtProductName);
							txt.setText(preOrderProduct.getProductName());
						}

						row.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 1f));						
						llCol.addView(row);
						count ++;

						catchEventOnRow(row);
					}
					else
					{
						View row = inf.inflate(R.layout.cell_for_jzay, null);
						row.setId(count);
						row.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 1f));

						llCol.addView(row);
						count ++;	

						catchEventOnRow(row);
					}
				}

				llRow.addView(llCol);	
			}


			container.addView(llRow,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mJazzy.setObjectForPosition(llRow, position);

			return llRow;
		}

		private void catchEventOnRow(final View row)
		{
			row.setOnClickListener(new OnClickListener()
			{				
				@Override
				public void onClick(View v) 
				{
					Log.e("Current Item", mJazzy.getCurrentItem()+"");
					Log.e("Current Column", row.getId()+"");
					int listRowIndex = mJazzy.getCurrentItem();
					int listColIndex = row.getId();	

					ArrayList<PreOrderProduct> tempList = allMenuList.get(listRowIndex);  

					if(currentMenuType == 0 && listColIndex < tempList.size())			// Current Type is Category
					{
						PreOrderProduct mOP = allMenuList.get(listRowIndex).get(listColIndex);	
						Log.e("selected Category", mOP.getCategoryName());
						setMenuIntoJazzyForGroup(mOP.getCategoryName());						
					}
					else if (currentMenuType == 1 && listColIndex < tempList.size())      // Current Type is Group
					{
						PreOrderProduct mOP = allMenuList.get(listRowIndex).get(listColIndex);	
						Log.e("selected Group", mOP.getGroupName());
						setMenuIntoJazzyForProduct(mOP.getGroupName());
					}
					else if (currentMenuType == 2 && listColIndex < tempList.size()) // Current Type is Product
					{
						PreOrderProduct mOP = allMenuList.get(listRowIndex).get(listColIndex);
						Log.e(mOP.getProductId(),"selectProductID");
						Log.e(mOP.getProductName(),"SelectProductName");
						//setToLstProduct(mOP);	
						Log.e(preOrderProductList.size()+"", "ArrayListSize");
						int check=0;
						if( preOrderProductList.size() > 0)
						{
							for(int i=0; i< preOrderProductList.size(); i++)	
							{
								PreOrderProduct sp = preOrderProductList.get(i);
								String productId= sp.getProductId();
								Log.e(productId, "ArrProductId");
								if(productId.equals(mOP.getProductId()))
								{
									Log.e("here1", "here1");
									AlertDialog.Builder builder = new AlertDialog.Builder(PreOrder.this);					
									builder.setTitle("Alert Message");
									builder.setMessage("This Item is already Choosed.");
									builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
									{
										public void onClick(DialogInterface dialog, int which) 
										{	

										}
									});
									builder.show();
									check ++;
									break;
								}
							}
							Log.e(check+"","check");
							if(check == 0)
							{
								setToLstProduct(mOP);
							}
						}
						else
						{
							setToLstProduct(mOP);
						}
					}
				}

				private void setToLstProduct(PreOrderProduct mOP) 
				{
					preOrderProductList.add(mOP);
					Log.e(preOrderProductList.size()+"","PreOrderProductList");
					preOrderProductAda.notifyDataSetChanged();
				}
			});
		}

		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object obj)
		{
			Log.e("DESTROY", "DESTROY");
			container.removeView(mJazzy.findViewFromObject(position));
		}

		@Override
		public int getCount() 
		{
			return totalMenuJazzyCount;
		}

		@Override
		public boolean isViewFromObject(View view,Object obj)
		{
			if (view instanceof OutlineContainer) 
			{
				return ((OutlineContainer) view).getChildAt(0) == obj;
			} 
			else 
			{
				return view == obj;
			}
		}
	
	}
}
