package com.topone.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION =1;
	private static DBOpenHelper s_instance = null;
	

	private String USER_TABEL_CREATE = "CREATE TABLE "
			+DataDao.TABLE_NAME+" ("		
			+DataDao.data_homeno+" TEXT, "
			+DataDao.data_homename+" TEXT, "
			+DataDao.data_pdno+" TEXT, "
			+DataDao.data_meterno+" TEXT, "
			+DataDao.data_meterpoint+" TEXT, "
			+DataDao.data_meteraddress+" TEXT, "
			+DataDao.data_fixaddress+" TEXT, "
			+DataDao.data_poweraddress+" TEXT, "	
			+DataDao.data_faultReason+" TEXT, "	
			+DataDao.data_faultType+" TEXT, "
			+DataDao.data_checkuser+" TEXT, "
			+DataDao.data_checkTime+" TEXT, "
			+DataDao.data_adopt_datetime+" TEXT, "
			+DataDao.data_meter_datetime+" TEXT, "
			
			+DataDao.data_total_power+" TEXT, "	
			+DataDao.data_ping_power+" TEXT, "	
			+DataDao.data_gu_power+" TEXT, "	
			+DataDao.data_gps_lon+" TEXT, "	
			+DataDao.data_gps_lat+" TEXT, "	
			+DataDao.data_atime+" TEXT, "	
			+DataDao.data_id+" TEXT Primary KEY);";
	


	
	public DBOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
		
	}

	public static DBOpenHelper getInstance(Context context){
		if(s_instance == null)
			s_instance = new DBOpenHelper(context.getApplicationContext());
		return s_instance;
	}
	
	private static String getUserDatabaseName() {
		return "jjl_topone.db";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(USER_TABEL_CREATE);
	}


	public void clearDB()
	{
		if(s_instance !=null)
		{
			try{
			SQLiteDatabase db = s_instance.getWritableDatabase();
			 if (db.isOpen()) 
			 {
		        db.delete( DataDao.TABLE_NAME, null, null);
		       
			 }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
       
	}
	public void closeDB(){
		if(s_instance !=null)
		{
			try{
			SQLiteDatabase db = s_instance.getWritableDatabase();
			db.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		s_instance = null;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
