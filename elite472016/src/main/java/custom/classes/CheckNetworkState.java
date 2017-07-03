package custom.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetworkState 
{
	public 	static boolean isNetworkStatusAvialable(Context context) 
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null)
		{
			NetworkInfo netInfos = connectivityManager
					.getActiveNetworkInfo();
			if (netInfos != null)
				if (netInfos.isConnected())
					return true;
		}
		return false;
	}
}
