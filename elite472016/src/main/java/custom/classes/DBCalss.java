package custom.classes;



import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBCalss  extends SQLiteOpenHelper
{	
	public static final String DB_PATH = "data/data/com.example.elite/databases/";
	public static final String DB_NAME = "elite.db";
	//public static final String DB_NAME = "Elitebackup15.7.2016.db";
	public static SQLiteDatabase eliteDB;

	private final Context mycontext;

	public DBCalss(Context c)
	{
		super(c, DB_NAME, null, 1);
		this.mycontext = c;
	}
	
	public static void openDB()
	{
		String myPath = DB_PATH + DB_NAME;
		eliteDB = SQLiteDatabase.openDatabase(myPath, null,	SQLiteDatabase.OPEN_READWRITE);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2)
	{
		// TODO Auto-generated method stub

	}

	public void createDataBase() throws IOException 
	{

		boolean dbExist = checkDataBase();

		if(!dbExist)
		{
			this.getReadableDatabase();

			try
			{
				copyDataBase();
			}
			catch (IOException e) 
			{
				throw new Error("Error copying database");
			}
		}
	}

	private void copyDataBase() throws IOException
	{
		// Open your local db as the input stream
		InputStream myInput = mycontext.getAssets().open(DB_NAME);

		// Path to the just created empty db

		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the Output Stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transter bytes from the inputfile to the output file

		byte[] buffer = new byte[2500];

		int length;
		while ((length = myInput.read(buffer)) > 0) 
		{
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	private boolean checkDataBase() throws IOException
	{
		try 
		{
			String myPath = DB_PATH + DB_NAME;
			eliteDB = SQLiteDatabase.openDatabase(myPath, null,	SQLiteDatabase.OPEN_READWRITE);

		} 
		catch (SQLiteException e) 
		{
			e.printStackTrace();
		}

		if (eliteDB != null)
		{
			eliteDB.close();
		}

		return eliteDB != null ? true : false;
	}
	

	
	public static void delUploadData()
	{
		if(DBCalss.eliteDB == null || !DBCalss.eliteDB.isOpen())
		{
			DBCalss.openDB();
		}

		DBCalss.eliteDB.beginTransaction();	
		DBCalss.eliteDB.execSQL("DELETE FROM SaleMan");
		DBCalss.eliteDB.execSQL("DELETE FROM Discount");
		DBCalss.eliteDB.execSQL("DELETE FROM VolumeDiscount");
		DBCalss.eliteDB.execSQL("DELETE FROM Product");
		DBCalss.eliteDB.execSQL("DELETE FROM Zone");
		DBCalss.eliteDB.execSQL("DELETE FROM PreOrderProduct");
		DBCalss.eliteDB.execSQL("DELETE FROM Customer");
		DBCalss.eliteDB.execSQL("DELETE FROM CustomerCategory");
		DBCalss.eliteDB.execSQL("DELETE FROM Delivery");
		DBCalss.eliteDB.execSQL("DELETE FROM DeliveryProduct");
		DBCalss.eliteDB.execSQL("DELETE FROM NewCustomer");
		DBCalss.eliteDB.execSQL("DELETE FROM SaleData");
		DBCalss.eliteDB.execSQL("DELETE FROM SaleDataDetail");
		DBCalss.eliteDB.execSQL("DELETE FROM DailyCheckTable");
		DBCalss.eliteDB.execSQL("DELETE FROM DailyChecklist");
		DBCalss.eliteDB.execSQL("DELETE FROM PreOrder");
		DBCalss.eliteDB.execSQL("DELETE FROM PreOrderDetail");
		DBCalss.eliteDB.execSQL("DELETE FROM ProductDetail");
		DBCalss.eliteDB.execSQL("DElETE FROM InvoiceDetail");
		DBCalss.eliteDB.execSQL("DELETE FROM DeliveryReturnData");
		DBCalss.eliteDB.execSQL("DELETE FROM DeliveryReturnDataDetail");
		DBCalss.eliteDB.execSQL("DELETE FROM CreditCollectionProduct");
		DBCalss.eliteDB.execSQL("DELETE FROM CreditCollectionInfo");
		DBCalss.eliteDB.execSQL("DELETE FROM CreditInvoiceList");
		DBCalss.eliteDB.setTransactionSuccessful();	
		DBCalss.eliteDB.endTransaction();
		
	}
	
}
