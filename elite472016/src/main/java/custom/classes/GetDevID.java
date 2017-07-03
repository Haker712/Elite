package custom.classes;

import android.content.Context;
import android.telephony.TelephonyManager;

public class GetDevID 
{
	public static String getDevId(Context context) 
	{		
		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);		
		return mTelephonyMgr.getDeviceId(); 
		
	//	simid =( mTelephonyMgr.getSimSerialNumber()==null) ? "" :mTelephonyMgr.getSimSerialNumber();		
	}

}
