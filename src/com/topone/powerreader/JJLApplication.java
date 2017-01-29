package com.topone.powerreader;



import android.app.Application;
import android.os.Environment;
import android.telephony.TelephonyManager;


public class JJLApplication extends Application {

	private static JJLApplication s_instance;
	@Override
	public void onCreate() {
		super.onCreate();
		s_instance = this;
	}
	public static JJLApplication getInstance()
	{
		if (null == s_instance)
		{
			
			s_instance = new JJLApplication();
		}
		return s_instance;
		
	}
	public String APP_STORE_PATH()
	{
		String strpath= "/sdcard/jjl/";//Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath()+"/jjl/" : "/mnt/sdcard/jjl/";
		return strpath;
	}
	


}