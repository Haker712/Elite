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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import bean.classes.CustomDiscount;
import bean.classes.SaleProduct;

import com.example.myjazzyviewtest.JazzyViewPager;
import com.example.myjazzyviewtest.JazzyViewPager.TransitionEffect;
import com.example.myjazzyviewtest.OutlineContainer;

import custom.classes.DBCalss;

public class SalePage extends Activity 
{
	public static ArrayList<SaleProduct> saleProductList = new ArrayList<SaleProduct>();//for mJazzy Product list
	
	static ArrayList<CustomDiscount> customDiscountList = new ArrayList<CustomDiscount>();// for custom discount data

	String fromDis = null, toDis = null;//need Cashier Account
	
	JazzyViewPager mJazzy;
	MainAdapter mAda;
	Button btnGoToNext;
	ArrayAdapter  searchAdapter;
	EditText etsaleQty,etsaleDis;
	AutoCompleteTextView autoCompleteSearch;
	ImageButton btnMenuBack,btnMenuHome;
	ListView lstProduct;
	String  dis_amountStr = null;
	TextView txtProductType,txtsaleDate,txtPageName,txtAvailableAmt, txtMax, txtMin;
	LinearLayout layoutforSaleQty;
	Boolean saleqtyExitorNot= false;
	Boolean allFocOrNot = false;
	static int remainQty= 0;
	int totalsaleQty=0;
	String saleqtynullProduct;
	SaleProductAda lstProductAda;
	DecimalFormat formatter = new DecimalFormat("###,##0"); 
	DecimalFormat decimalFormatter = new DecimalFormat("0.00");
	ArrayList<String> searchShowList = new ArrayList<String>();// for search product
	ArrayList<SaleProduct> allSaleProductList = new ArrayList<SaleProduct>();
	ArrayList<ArrayList<SaleProduct>> allMenuList = new ArrayList<ArrayList<SaleProduct>>();	
	ArrayList<SaleProduct> tempList = new ArrayList<SaleProduct>();	
	ArrayList<String> insertedMenuItem = new ArrayList<String>();
	SimpleDateFormat fmtForTodayStr = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
	SimpleDateFormat fmtForDueStr = new SimpleDateFormat("yyyy-MM-dd");

	int currentMenuType = 0;			// 0 = Category , 1 = Sub Category , 2 = Item
	int totalMenuJazzyCount = 0;
	String previousMenuName = "";
	static double totalItemDiscounts=0;
	//int totalSaleQty =0;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.salepage);

		hideScreenKeyboardInPageDisplay();//for hidden keyboard
		registerIDs();
		getProductFromDB();//for Product List for Product table in DB
		setAdapters();//for saleProduct listview
		setupJazziness(TransitionEffect.Stack); //mJazzy view
		setMenuIntoJazzyForCategory();//mJazzy Categoryview
		catchEvent();//process
	}
	
	private void getProductFromDB()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		Cursor cur;		

		String[] selCol = {"categoryId", "categoryName", "groupId", "productId", "productName", "totalQty","RemainingQty","SaleQty","sellingPrice", "purchasePrice", "groupName","discountType"};
		cur = DBCalss.eliteDB.query("Product", selCol, null, null, null, null, null);

		while(cur.moveToNext())
		{	
			String categoryID = cur.getString(cur.getColumnIndex("categoryId"));
			String categoryName = cur.getString(cur.getColumnIndex("categoryName"));
			String groupId = cur.getString(cur.getColumnIndex("groupId"));
			String productId = cur.getString(cur.getColumnIndex("productId"));
			String productName = cur.getString(cur.getColumnIndex("productName"));
			String sellingPrice = cur.getString(cur.getColumnIndex("sellingPrice"));
			String purchasePrice = cur.getString(cur.getColumnIndex("purchasePrice"));
			String groupName = cur.getString(cur.getColumnIndex("groupName"));
			String discountType=cur.getString(cur.getColumnIndex("discountType"));
			String totalQty=cur.getString(cur.getColumnIndex("totalQty"));
			String remainingQty=cur.getString(cur.getColumnIndex("RemainingQty"));
			String saleQty=cur.getString(cur.getColumnIndex("SaleQty"));
			Log.e(productName,"ProductName");
			Log.e(discountType,"DiscountType");

			SaleProduct sp = new SaleProduct();
			sp.setCategoryId(categoryID);
			sp.setCategoryName(categoryName);
			sp.setGroupId(groupId);
			sp.setProductId(productId);
			sp.setProductName(productName);
			sp.setSellingPrice(sellingPrice);
			sp.setSellingPrice2(sellingPrice);
			sp.setPurchasePrice(purchasePrice);
			sp.setGroupName(groupName);
			sp.setDiscountType(discountType);
			sp.setTotalQty(totalQty);
			Log.e(sp.getTotalQty(),"TotalQty");
			sp.setRemainingQty(remainingQty);
			Log.e(sp.getRemainingQty(),"RRRRRemainingQty");
			sp.setSaleQty(saleQty);
		
			allSaleProductList.add(sp);
			Log.e("ArrSizeFromDB",allSaleProductList.size() +"");
		}

		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}

	private void hideScreenKeyboardInPageDisplay()
	{
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);		
	}

	private void setMenuIntoJazzyForCategory()   
	{
		searchShowList.clear();
		boolean isInsertedflag;

		for(int i=0; i < allSaleProductList.size(); i++)
		{
			isInsertedflag = false;
			Log.e("Category", "Category");

			for(String insertedItem : searchShowList)
			{
				Log.e("Category 2", "Category 2");

				if(allSaleProductList.get(i).getCategoryName().equalsIgnoreCase(insertedItem))
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
				searchShowList.add(allSaleProductList.get(i).getCategoryName());// For search(AutoCompleteTextView)
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
		for(int i=0; i<allSaleProductList.size(); i++)
		{
			isInserted = false;
			Log.e("Category", "Category");
			for(String insertedItem : insertedMenuItem)
			{
				Log.e("Category 2", "Category 2");
				if(allSaleProductList.get(i).getCategoryName().equalsIgnoreCase(insertedItem))
				{
					Log.e("Category 3", "Category 3");
					isInserted = true;
					break;
				}
			}

			if(!isInserted)
			{
				Log.e("Category 4", "Category 4");
				tempList.add(allSaleProductList.get(i));
				insertedMenuItem.add(allSaleProductList.get(i).getCategoryName());
				count++;
				Log.e("i", i + "");
			}

			if(count == 4 || i == (allSaleProductList.size()-1))
			{
				allMenuList.add(tempList);
				tempList = new ArrayList<SaleProduct>();
				count = 0;
				totalMenuJazzyCount++;
			}
		}

		Log.e("menu size", allMenuList.size() + "");
	}

	@Override
	public void onBackPressed()
	{
		if(saleProductList.size() > 0)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SalePage.this);					
			builder.setTitle("Alert Message");
			builder.setMessage("Your selected Items will be delete!");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{	
					saleProductList.clear();
//					HomePage.discountPercent=0;
//					HomePage.itemDiscount=0;
					totalItemDiscounts=0;
					SalePage2.productIdlist.clear();
			
					startActivity(new Intent(SalePage.this, Customer.class));
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
			startActivity(new Intent(SalePage.this,Customer.class));
			 finish();
		}
	}

	private void setAdapters()
	{
		lstProductAda = new SaleProductAda(this);
		lstProduct.setAdapter(lstProductAda);
		searchAdapter = new ArrayAdapter<String>(this,R.layout.layout_for_autocompletext,searchShowList);
		autoCompleteSearch.setAdapter(searchAdapter);	
	}

	private void catchEvent()
	{
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
		
		lstProduct.setOnItemLongClickListener(new OnItemLongClickListener() 
		{
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,                                                                   
					int arg2, long arg3) {
				final int position = arg2;
				// TODO Auto-generated method stub
				
				final AlertDialog dia = new AlertDialog.Builder(SalePage.this)
				.setTitle("Information")
				.setMessage("\n Are you sure you want to delete.\n")
				.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{	
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						saleProductList.remove(position);
					    lstProductAda.notifyDataSetChanged();
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
		btnGoToNext.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v) 
			{
				Log.e(checksaleQtyExitorNot()+"","checkQtyExistorNot");
				if(saleProductList.size() == 0)
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(SalePage.this);					
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
						AlertDialog.Builder builder = new AlertDialog.Builder(SalePage.this);					
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
					{	Log.e(checkAllFocOrNot()+"","status");
						if(checkAllFocOrNot() == true)
						{
							Log.e("AllFoc","AllFoc");
							AlertDialog.Builder builder = new AlertDialog.Builder(SalePage.this);					
							builder.setTitle("Information");
							builder.setMessage("All Products must not be FOC");
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
							Log.e("SaleProductforTesting",saleProductList.size() +"");
							for(SaleProduct sp : saleProductList)
							{
								Log.e("GetFocforTesting",sp.getIsFoc());
								if(sp.getIsFoc().equals("false"))
								{
									Log.e("GetDiscountForTesting",sp.getDiscount()+"");
									totalItemDiscounts += sp.getDiscount();
								}
							}
							startActivity(new Intent(SalePage.this, SalePage2.class));
							finish();
						}

					}
				}
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
	private void registerIDs() 
	{
		btnGoToNext =(Button) findViewById(R.id.salePage_btnGoToNext);
		txtProductType = (TextView) findViewById(R.id.salePage_txtProductType);
		lstProduct = (ListView) findViewById(R.id.salePage_lstProduct);
		btnMenuBack = (ImageButton) findViewById(R.id.salePage_btnMenuBack);
		btnMenuHome=(ImageButton) findViewById(R.id.salePage_btnMenuHome);
		autoCompleteSearch = (AutoCompleteTextView) findViewById(R.id.salePage_autoTxtSearch);	
		txtsaleDate = (TextView) findViewById(R.id.txtDate);
		txtPageName=(TextView) findViewById(R.id.salePage_txtShowViewPagerPage);
		
		Calendar todayCal = Calendar.getInstance();
		txtsaleDate.setText("Sale Date : "+fmtForDueStr.format(todayCal.getTime()));
	}

	private void setupJazziness(TransitionEffect effect)
	{
		mJazzy = (JazzyViewPager) findViewById(R.id.jazzy_pager);
		mJazzy.setTransitionEffect(effect);
		mAda = new MainAdapter();
		mJazzy.setAdapter(mAda);
		mJazzy.setPageMargin(30);
	}

	
	public boolean checksaleQtyExitorNot()
	{
		for (SaleProduct sp : saleProductList)
		{
			Log.e(sp.getTempSaleQty()+"", "CCCCCCC");
			if(sp.getTempSaleQty().equals("0"))
			{
				saleqtyExitorNot = true;
				saleqtynullProduct= sp.getProductName();
				break;
			}
			else
			{
				saleqtyExitorNot = false;
			}
		}
		return saleqtyExitorNot;
	}
	
	public boolean checkAllFocOrNot()
	{
		for(SaleProduct sp : saleProductList)
		{
			Log.e(sp.getIsFoc(),"ISFOC");
			if(sp.getIsFoc().equals("false"))
			{
				allFocOrNot = false;
				break;
			}
			else
			{
				allFocOrNot = true;
			}
		}
		return allFocOrNot;
	}

	public class SaleProductAda extends ArrayAdapter<SaleProduct>
	{
		private final Activity context;


		public SaleProductAda(Activity context)
		{
			super(context, R.layout.sale_custom_list_row, saleProductList);
			this.context = context; 		
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
		{
			LayoutInflater inflater = context.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.sale_custom_list_row, null, true);

			final Button btnDiscount = (Button) rowView.findViewById(R.id.saleCustomListRow_btnDiscount);
			final TextView txtPrice = (TextView) rowView.findViewById(R.id.saleCustomListRow_txtPrice);
			TextView txtProductName = (TextView) rowView.findViewById(R.id.saleCustomListRow_txtProductName);
			final TextView txtTotalAmt = (TextView) rowView.findViewById(R.id.saleCustomListRow_txtTotalAmt);
			final Button btnSaleQty = (Button) rowView.findViewById(R.id.saleCustomList_btnSaleQty);
			final CheckBox chkFoc =(CheckBox) rowView.findViewById(R.id.chkFOC);

			int price=0;
			final SaleProduct sp = saleProductList.get(position); 
			price=(int) Double.parseDouble(sp.getSellingPrice());
			txtPrice.setText(formatter.format(price));
			txtProductName.setText(sp.getProductName());
			btnSaleQty.setText(sp.getTempSaleQty());
			//btnDiscount.setText(formatter.format(Double.parseDouble((sp.getcalculatediscount()))));
			//btnDiscount.setText(formatter.format(Double.parseDouble(sp.getDiscountPercent()))+"%");
			btnDiscount.setText(sp.getDiscountPercent()+"%"); //code prepare SMH
			txtTotalAmt.setText(decimalFormatter.format(sp.getTotalAmt()));
			
			if(sp.getIsFoc().equals("true"))
			{ 
				chkFoc.setChecked(true);
				btnDiscount.setText("0");
				txtTotalAmt.setText("0");
			}
			else
			{
				chkFoc.setChecked(false);
			}
			chkFoc.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(android.widget.CompoundButton buttonView,
						boolean isChecked) 
				{
					if(isChecked)
					{
						sp.setIsFoc("true");
						txtTotalAmt.setText("0");
						btnDiscount.setText("0");
					}
					else
					{
						sp.setIsFoc("false");
						txtTotalAmt.setText(formatter.format(sp.getTotalAmt()));
//						if(sp.getTotalAmt()!= null)
//						{
//							txtTotalAmt.setText(formatter.format(Double.parseDouble(sp.getTotalAmt())));
//						}
						if(sp.getcalculatediscount()!= null)
						{
							btnDiscount.setText(Double.parseDouble(sp.getcalculatediscount())+"");
						}
						
					}
				}
			});
			btnSaleQty.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0) 
				{
					final AlertDialog d = new AlertDialog.Builder(SalePage.this)					
					.setView(saleQtyDynamicLayout())
					.setTitle("Sale Quantity")
					.setPositiveButton("Confirm", null)
					.setNegativeButton("Cancel", null)
					.setCancelable(false)
					.create();
					d.setOnShowListener(new DialogInterface.OnShowListener()
					{
						@Override
						public void onShow(DialogInterface arg0) 
						{
							//final boolean status=SalePage2.hasDuplicates(sp.getProductId());
							txtAvailableAmt.setText(sp.getRemainingQty());
							
							Button positiveBtn = d.getButton(AlertDialog.BUTTON_POSITIVE);
							positiveBtn.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									int saleQty= 0;
									String ssaleQty= "";
									ssaleQty= etsaleQty.getText().toString().trim();
									int remainingQty=0;
									if(ssaleQty.equals(""))
									{
										Log.e("HERE","HERE");
										 AlertDialog diag3= new AlertDialog.Builder(SalePage.this)
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
//										if(status)
//										{
//											Log.e("true","true");
//											totalSaleQty += Integer.parseInt(ssaleQty);
//										}
//										else
//										{
//											Log.e("false","false");
//											totalSaleQty = Integer.parseInt(ssaleQty);
//										}
//										
										remainingQty = Integer.parseInt(sp.getRemainingQty());
										saleQty = Integer.parseInt(ssaleQty);
										
									//	Log.e(totalSaleQty +"", "TotalsaleQty");
										Log.e(remainingQty+"", "REmainingQTy");
										if(saleQty > remainingQty)
										{
											 AlertDialog diag3= new AlertDialog.Builder(SalePage.this)
											.setTitle("Alert Message!")
											.setMessage("\nInsufficent Quantity\n")		
											.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
											{
												@Override
												public void onClick(DialogInterface diag2, int arg1) 
												{
													//totalSaleQty = 0;
												}
											})
											.create();

											diag3.show();
										}
										else
										{
												Log.e(saleQty+"", "saleQty");
												if(saleQty == 0)
												{
													btnSaleQty.setText("0");
													btnDiscount.setText("0");
													txtTotalAmt.setText("0");
													sp.setTempSaleQty(saleQty +"");			
												}
												else 
												{
														sp.setTempSaleQty(saleQty +"");
														Log.e(sp.getRemainingQty()+"","RemainingQty");
														int saleQtyFromDB=0;
														int totalQty =0;
														if(sp.getRemainingQty() != null)
														{
															remainingQty=Integer.parseInt(sp.getRemainingQty());
														}
														if(sp.getTotalQty() != null )
														{
															totalQty = Integer.parseInt(sp.getTotalQty());
														}
														Log.e(sp.getSaleQty(),"SaleQQQQQ");
														if(!sp.getSaleQty().equals(""))
														{	
															saleQtyFromDB=Integer.parseInt(sp.getSaleQty());
														}
														Log.e(saleQtyFromDB+"", "SaleQtyfromDB");
														Log.e(remainingQty + "", "RemainingQty");
														
													
														remainQty = remainingQty - saleQty;
														btnSaleQty.setText(sp.getTempSaleQty());

														
														int totalAmount=0;
														int totalAmtNoDiscount=0;
														
														Log.e(HomePage.itemDiscountList.size()+"", "discountlistSize");
														double intsellingPrice=0;
														intsellingPrice= Double.parseDouble(sp.getSellingPrice());
														
														Log.e(sp.getProductId(),"PRODUCTID");
														Log.e(sp.getDiscountType(),"DiscountType");
														Log.e(saleQty+"","SaleQty");
														Log.e(sp.getSellingPrice(),"SellingPrice");
														//HomePage.calculateItemDiscount(sp.getProductId(),sp.getDiscountType(), saleQty,sp.getSellingPrice());
														//totalItemDiscounts += HomePage.itemDiscount;
														
														totalAmount= (int) ((intsellingPrice * saleQty));
														totalAmtNoDiscount = (int) ((intsellingPrice * saleQty));
														sp.setcalculatediscount(HomePage.itemDiscount+"");
														sp.setDiscount(0);
														sp.setDiscountPercent("0");
														sp.setTotalAmt(totalAmount);
														sp.setTotalAmtNoDiscount(totalAmtNoDiscount+"");
														if(sp.getIsFoc().equals("false"))
														{
															txtTotalAmt.setText(formatter.format(totalAmount)+"");
															btnDiscount.setText(formatter.format(Double.parseDouble(sp.getDiscountPercent()))+"%");
														}
														Log.e(sp.getIsFoc(),"ISSSSSFFFFFFOOOOCCCCC");
//															if(sp.getIsFoc().equals("false"))
//															{
//																if(sp.getDiscount() != null )
//																{
//																	Log.e("ADDDDDD","ADDDDDE");
//																	totalItemDiscounts += Integer.parseInt(sp.getDiscount());
//																}
//															}
															Log.e("TotalItemDiscoutADDDDDDDDDD",totalItemDiscounts+"");
												}
												d.dismiss();
										}
										}
								}
							});							
							Button negativeButton = d.getButton(AlertDialog.BUTTON_NEGATIVE);
							negativeButton.setOnClickListener(new OnClickListener()
							{
								
								@Override
								public void onClick(View arg0) 
								{
									d.dismiss();
								}
							});
							
						}
					});
					
					d.show();
				}
			}
					);
			
			btnDiscount.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {

					getCustomDiscountFromDB();
							
					final AlertDialog d = new AlertDialog.Builder(SalePage.this)					
					.setView(saleDisDynamicLayout())
					.setTitle("Sale Quantity")
					.setPositiveButton("Confirm", null)
					.setNegativeButton("Cancel", null)
					.setCancelable(false)
					.create();
					d.setOnShowListener(new DialogInterface.OnShowListener()
					{
						@Override
						public void onShow(DialogInterface arg0) 
						{							
							
							Button positiveBtn = d.getButton(AlertDialog.BUTTON_POSITIVE);
							positiveBtn.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View v)
								{

									double saleDis,saleMax, saleMin;
									String ssaleDis= "";
									ssaleDis= etsaleDis.getText().toString().trim();
																	
									if(ssaleDis.equals(""))
									{
										Log.e("HERE","HERE");
										 AlertDialog diag3= new AlertDialog.Builder(SalePage.this)
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
									else{
										
										saleDis = Double.parseDouble(ssaleDis);
										saleMax = Double.parseDouble(toDis);
										saleMin = Double.parseDouble(fromDis);
																		
									if(saleDis > saleMax){										
										 AlertDialog diag3= new AlertDialog.Builder(SalePage.this)
											.setTitle("Alert Message!")
											.setMessage("\nMax Discount\n")		
											.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
											{
												@Override
												public void onClick(DialogInterface diag2, int arg1) 
												{
													//totalSaleQty = 0;
												}
											})
											.create();

											diag3.show();
										}else if(saleDis < saleMin)
										{
											saleDis = Double.parseDouble(ssaleDis);
											 AlertDialog diag3= new AlertDialog.Builder(SalePage.this)
												.setTitle("Alert Message!")
												.setMessage("\nMin Discount\n")		
												.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener()
												{
													@Override
													public void onClick(DialogInterface diag2, int arg1) 
													{
														//totalSaleQty = 0;
													}
												})
												.create();

												diag3.show();
											}else{
												d.dismiss();
												btnDiscount.setText(ssaleDis + "%");
												sp.setDiscountPercent(saleDis+"");
												
												double disAmt = (Double.parseDouble(sp.getSellingPrice()) * Double.parseDouble(sp.getTempSaleQty()))* Double.parseDouble(ssaleDis) / 100;
												sp.setDiscount(disAmt);

												double totalAmount = Double.parseDouble(sp.getSellingPrice()) * Double.parseDouble(sp.getTempSaleQty());
												totalAmount -= totalAmount * Double.parseDouble(ssaleDis) / 100;
												sp.setTotalAmt(totalAmount);
												txtTotalAmt.setText(totalAmount + "");												
											}
									
									}
								}
							});
							
							Button negativeButton = d.getButton(AlertDialog.BUTTON_NEGATIVE);
							negativeButton.setOnClickListener(new OnClickListener()
							{
								
								@Override
								public void onClick(View arg0) 
								{
									d.dismiss();
								}
							});
							
						}
					});
					
					d.show();
				}
			});

			return rowView;
		}
	}
	
	public View saleDisDynamicLayout(){
		LayoutInflater inflater1=(LayoutInflater)SalePage.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row1=inflater1.inflate(R.layout.dynamiclayout_salediscount, null);
		
		etsaleDis =(EditText)row1.findViewById(R.id.etSaleDis);
		
		fromDis = String.valueOf(CustomDiscount.getFromDiscount());
		toDis = String.valueOf(CustomDiscount.getToDiscount());
		
		txtMax = (TextView) row1.findViewById(R.id.txtMax);
		txtMax.setText(fromDis+" % ");
		txtMin = (TextView) row1.findViewById(R.id.txtMin);
		txtMin.setText(toDis+" % ");
		return row1;
	}

	private void getCustomDiscountFromDB(){
		if (DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen()) {
			DBCalss.openDB();
		}
		customDiscountList.clear();
		DBCalss.eliteDB.beginTransaction();
		Cursor cur;

		String[] selCol = { "fromDiscount","toDiscount" };
		cur = DBCalss.eliteDB.query("CustomDiscount", selCol, null, null, null,
				null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				Double fromDiscount = Double.parseDouble(cur.getString(
						cur.getColumnIndex("fromDiscount")).trim());
				Double toDiscount = Double.parseDouble(cur.getString(
						cur.getColumnIndex("toDiscount")).trim());
					
				CustomDiscount vDiscount = new CustomDiscount();
				vDiscount.setFromDiscount(String.valueOf(fromDiscount));
				vDiscount.setToDiscount(String.valueOf(toDiscount));
				customDiscountList.add(vDiscount);
			}
		}
		cur.close();
		DBCalss.eliteDB.setTransactionSuccessful();
		DBCalss.eliteDB.endTransaction();
	}
	
	public View saleQtyDynamicLayout()
	{
		LayoutInflater inflater1=(LayoutInflater)SalePage.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row1=inflater1.inflate(R.layout.dynamiclayout_salequantity, null);
		layoutforSaleQty=(LinearLayout) row1.findViewById(R.id.layoutforSaleQty);
		etsaleQty =(EditText)row1.findViewById(R.id.etSaleQty);
		txtAvailableAmt = (TextView)row1.findViewById(R.id.txtAvailableAmt);
		
		return row1;
	}


	private void setMenuIntoJazzyForGroup(String categoryName) 
	{
		searchShowList.clear();
		boolean isInsertedflag=false;
		for(int i=0; i< allSaleProductList.size(); i++)
		{
			Log.e("group", "group");

			if(allSaleProductList.get(i).getCategoryName().equals(categoryName))
			{
				Log.e("group 1", "group 1");

				isInsertedflag = false;

				for(String insertedItem : searchShowList)
				{
					Log.e("group 2", allSaleProductList.get(i).getGroupName());

					if(allSaleProductList.get(i).getGroupName().equalsIgnoreCase(insertedItem))
					{
						Log.e("group 3", "group 3");

						isInsertedflag = true;
						break;
					}
				}

				if(!isInsertedflag)
				{
					Log.e("group 4", "group 4");					

					searchShowList.add(allSaleProductList.get(i).getGroupName());
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

		for(int i=0; i< allSaleProductList.size(); i++)
		{
			Log.e("group", "group");

			if(allSaleProductList.get(i).getCategoryName().equals(categoryName))
			{
				Log.e("group 1", "group 1");

				isInserted = false;

				for(String insertedItem : insertedMenuItem)
				{
					Log.e("group 2", allSaleProductList.get(i).getGroupName());

					if(allSaleProductList.get(i).getGroupName().equalsIgnoreCase(insertedItem))
					{
						Log.e("group 3", "group 3");

						isInserted = true;
						break;
					}
				}

				if(!isInserted)
				{
					Log.e("group 4", "group 4");

					tempList.add(allSaleProductList.get(i));
					insertedMenuItem.add(allSaleProductList.get(i).getGroupName());
					count++;								
				}							
			}

			if(count ==4 || i == (allSaleProductList.size()-1))
			{
				Log.e("group 4", "group 4");

				allMenuList.add(tempList);
				tempList = new ArrayList<SaleProduct>();
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
		for(int i=0; i<allSaleProductList.size(); i++)
		{
			Log.e("group", "group");

			if(allSaleProductList.get(i).getGroupName().equals(groupName))
			{
				Log.e("group 1", "group 1");

				isInsertedflag = false;

				for(String insertedItem : searchShowList)
				{
					Log.e("group 2", allSaleProductList.get(i).getCategoryId());

					if(allSaleProductList.get(i).getProductName().equalsIgnoreCase(insertedItem))
					{
						Log.e("group 3", "group 3");

						isInsertedflag = true;
						break;
					}
				}

				if(!isInsertedflag)
				{
					Log.e("group 4", "group 4");					

					searchShowList.add(allSaleProductList.get(i).getProductName());
				}							
			}
		}
		searchAdapter = new ArrayAdapter<String>(this,R.layout.layout_for_autocompletext,searchShowList);
		autoCompleteSearch.setAdapter(searchAdapter);
		allMenuList.clear();				
		totalMenuJazzyCount = 0;
		int count = 0;

		for(int i=0; i< allSaleProductList.size(); i++)
		{	
			Log.e("Sel product", groupName);

			if(allSaleProductList.get(i).getGroupName().equals(groupName))
			{
				Log.e("product1", "product1");

				tempList.add(allSaleProductList.get(i));
				Log.e(tempList.size()+"","TEMP");
				count++;
				Log.e("COUNT", count + "");
			}

			if(count == 4 || i == (allSaleProductList.size()-1))
			{
				Log.e("product2", "product2");

				allMenuList.add(tempList);
				tempList = new ArrayList<SaleProduct>();
				count = 0;
				totalMenuJazzyCount++;
			}
		}

		currentMenuType = 2;            // Current Menu Type is Product	
		mAda.notifyDataSetChanged();					
		txtProductType.setText("Item");
		Log.e("product size", allMenuList.size() + "");
	}

	private void setMenuIntoJazzyForItem(String productName)
	{
		allMenuList.clear();				
		totalMenuJazzyCount = 0;
		int count = 0;
		
		for(int i=0; i<allSaleProductList.size(); i++)
		{	
			Log.e("Sel product", productName);

			if(allSaleProductList.get(i).getProductName().equals(productName))
			{
				Log.e("product1", "product1");

				tempList.add(allSaleProductList.get(i));
				Log.e(tempList.size()+"","TEMP");
				count++;
				Log.e("COUNT", count + "");
			}
			
			if(count == 4 || i == (allSaleProductList.size()-1))
			{
				Log.e("product2", "product2");

				allMenuList.add(tempList);
				tempList = new ArrayList<SaleProduct>();
				count = 0;
				totalMenuJazzyCount++;
			}
		}
		currentMenuType = 2;            // Current Menu Type is Product	
		mAda.notifyDataSetChanged();					
		txtProductType.setText("Item");		
		Log.e("product size", allMenuList.size() + "");
	}
	private class MainAdapter extends PagerAdapter
	{
		@Override
		public Object instantiateItem(ViewGroup container, final int position) 
		{		
			Log.e(allMenuList.size()+"", "ARRSIZE");
			ArrayList<SaleProduct> tempList = allMenuList.get(position);
			Log.e(tempList.size()+"", "0000000");
			txtPageName.setText("1 of "+ allMenuList.size()+"");
			int count = 0;

			LayoutInflater inf = SalePage.this.getLayoutInflater();
			LinearLayout llRow = new LinearLayout(SalePage.this);
			llRow.setOrientation(LinearLayout.VERTICAL);
			llRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

			for(int j=0; j<4; j++)
			{
				LinearLayout llCol = new LinearLayout(SalePage.this);
				llCol.setOrientation(LinearLayout.HORIZONTAL);
				llCol.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

				for(int i=0; i < 1; i++)
				{
					Log.e(tempList.size()+"", "TEMPSIZE");
					if(count < tempList.size())
					{
						SaleProduct saleProduct = tempList.get(count);

						View row = inf.inflate(R.layout.cell_for_jzay, null);
						row.setId(count);

						if(currentMenuType == 0)		// Current Type is Category
						{
							Log.e("Category", "Category");

							TextView txt = (TextView) row.findViewById(R.id.cellForJzay_txtProductName);
							txt.setText(saleProduct.getCategoryName());
						}
						else if(currentMenuType == 1)	// Current Type is Group
						{
							Log.e("Group", "Group");

							TextView txt = (TextView) row.findViewById(R.id.cellForJzay_txtProductName);
							txt.setText(saleProduct.getGroupName());
						}
						else if(currentMenuType == 2)	// Current Type is Product	
						{
							TextView txt = (TextView) row.findViewById(R.id.cellForJzay_txtProductName);
							txt.setText(saleProduct.getProductName());
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
					int listRowIndex = mJazzy.getCurrentItem();
					int listColIndex = row.getId();	

					ArrayList<SaleProduct> tempList = allMenuList.get(listRowIndex);  

					if(currentMenuType == 0 && listColIndex < tempList.size())			// Current Type is Category
					{
						SaleProduct mOP = allMenuList.get(listRowIndex).get(listColIndex);	
						Log.e("selected Category", mOP.getCategoryName());
						setMenuIntoJazzyForGroup(mOP.getCategoryName());						
					}
					else if (currentMenuType == 1 && listColIndex < tempList.size())      // Current Type is Group
					{
						SaleProduct mOP = allMenuList.get(listRowIndex).get(listColIndex);	
						Log.e("selected Group", mOP.getGroupName());
						setMenuIntoJazzyForProduct(mOP.getGroupName());
					}
					else if (currentMenuType == 2 && listColIndex < tempList.size()) // Current Type is Product
					{
						SaleProduct mOP = allMenuList.get(listRowIndex).get(listColIndex);
						Log.e(mOP.getProductId(),"selectProductID");
						Log.e(mOP.getProductName(),"SelectProductName");
						Log.e(mOP.getSaleQty(), "SelectProductSaleQty");
						
						Log.e(saleProductList.size()+"", "ArrayListSize");
						setToLstProduct(mOP);	
					}
				}

//				private void setToLstProduct(SaleProduct mOP) 
//				{
//					saleProductList.add(mOP);
//					Log.e(saleProductList.size()+"","SaleProductList");
//					lstProductAda.notifyDataSetChanged();
//				}
			
				
				private void setToLstProduct(SaleProduct mOP) 
				{
					SaleProduct sp = new SaleProduct();
					sp.setProductId(mOP.getProductId());
					sp.setProductName(mOP.getProductName());
					sp.setSaleQty(mOP.getSaleQty());
					sp.setCategoryId(mOP.getCategoryId());
					sp.setCategoryName(mOP.getCategoryName());
					sp.setGroupId(mOP.getGroupId());
					sp.setGroupName(mOP.getGroupName());
					sp.setDiscountPercent("0");
					sp.setDiscount(0);
					sp.setSellingPrice(mOP.getSellingPrice());
					sp.setSellingPrice2(mOP.getSellingPrice());
					sp.setDiscountType(mOP.getDiscountType());
					sp.setRemainingQty(mOP.getRemainingQty());
					sp.setTotalQty(mOP.getTotalQty());
	
					saleProductList.add(sp);
					Log.e(saleProductList.size()+"","SaleProductList");
					lstProductAda.notifyDataSetChanged();
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
