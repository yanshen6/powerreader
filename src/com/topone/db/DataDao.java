package com.topone.db;

import java.util.ArrayList;
import java.util.List;

import com.topone.powerreader.JJLChaxunRecordItem;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataDao {

	public static final String TABLE_NAME = "data_info";
	public static final String data_id ="data_id";
	public static final String data_homeno ="data_homeno";
	
	public static final  String data_homename ="data_homename";
	public static final  String data_pdno ="data_pdno";
	public static final  String data_meterno ="data_meterno";
	public static final  String data_meterpoint ="data_meterpoint";
	public static final  String data_meteraddress ="data_meteraddress";
	public static final  String data_fixaddress ="data_fixaddress";
	public static final  String data_poweraddress ="data_poweraddress";
	public static final  String data_faultReason ="data_faultReason";
	public static final  String data_faultType ="data_faultType";
	public static final  String data_checkuser ="data_checkuser";
	public static final  String data_checkTime ="data_checkTime";
	
	public static final  String data_adopt_datetime ="data_adopt_datetime";
	public static final  String data_meter_datetime ="data_meter_datetime";
	
	public static final  String data_total_power ="data_total_power";
	public static final  String data_ping_power ="data_ping_power";
	public static final  String data_gu_power ="data_gu_power";
	
	public  static final  String data_gps_lon="data_gps_lon";
	public  static final  String data_gps_lat="data_gps_lat";
	public  static final  String data_atime="atime";
	
	private DBOpenHelper m_dbHelper;
	public DataDao(Context contex) {
		m_dbHelper = DBOpenHelper.getInstance(contex);
	}
	
	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveRecordList(List<JJLChaxunRecordItem> recordList){
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		if(db.isOpen())
		{
			db.delete(TABLE_NAME, null, null);
			for(JJLChaxunRecordItem item:recordList){
				 if(item ==null)
					 continue;
				ContentValues content = new ContentValues ();
				
				content.put(DataDao.data_id , item.data_id);
				content.put(DataDao.data_homeno , item.data_homeno);
				
				content.put(DataDao.data_homename , item.data_homename);
				content.put(DataDao.data_pdno , item.data_pdno);
				content.put(DataDao.data_meterno , item.data_meterno);
				content.put(DataDao.data_meterpoint , item.data_meterpoint);
				content.put(DataDao.data_meteraddress , item.data_meteraddress);
				content.put(DataDao.data_fixaddress , item.data_fixaddress);
				content.put(DataDao.data_poweraddress , item.data_poweraddress);
				content.put(DataDao.data_faultReason , item.data_faultReason);
				content.put(DataDao.data_faultType , item.data_faultType);
				content.put(DataDao.data_checkuser , item.data_checkuser);
				content.put(DataDao.data_checkTime , item.data_checkTime);

				content.put(DataDao.data_atime , item.data_atime);
				
				content.put(DataDao.data_adopt_datetime , item.data_adopt_datetime);
				content.put(DataDao.data_meter_datetime , item.data_meter_datetime);
				content.put(DataDao.data_total_power , item.data_total_power);
				content.put(DataDao.data_ping_power , item.data_ping_power);
				content.put(DataDao.data_gu_power , item.data_gu_power);
				
				content.put(DataDao.data_gps_lon, item.data_gps_lon);
				content.put(DataDao.data_gps_lat, item.data_gps_lat);
				db.insert(TABLE_NAME, null, content);
			}
		}
	}
	
	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale") 
	public List<JJLChaxunRecordItem> getRecordList(){
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		List<JJLChaxunRecordItem> mapUser = new ArrayList<JJLChaxunRecordItem>();
		if(db.isOpen()){
			Cursor cursorDatas = db.rawQuery("select * from "+TABLE_NAME, null);
			while(cursorDatas.moveToNext()){
				
				JJLChaxunRecordItem item = new JJLChaxunRecordItem();
				

				item.data_id= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_id )); 
				item.data_homeno= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_homeno )); 
				
				item.data_homename= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_homename )); 
				 item.data_pdno= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_pdno )); 
				item.data_meterno= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_meterno )); 
				item.data_meterpoint= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_meterpoint )); 
				 item.data_meteraddress= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_meteraddress ));
				 item.data_fixaddress= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_fixaddress )); 
				item.data_poweraddress= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_poweraddress )); 
				 item.data_faultReason= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_faultReason ));
				item.data_faultType= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_faultType )); 
				item.data_checkuser= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_checkuser )); 
				item.data_checkTime= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_checkTime )); 
				
				item.data_adopt_datetime= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_adopt_datetime ));
				item.data_meter_datetime= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_meter_datetime ));
				item.data_total_power= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_total_power )); 
				item.data_ping_power= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_ping_power )); 
				item.data_gu_power= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_gu_power )); 
				
				item.data_gps_lon= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_gps_lon)); 
				item.data_gps_lat= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_gps_lat)); 
				item.data_atime= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_atime)); 
				
			
			
				mapUser.add(item);
			}
			cursorDatas.close();
			
		}
		return mapUser;
	}

	@SuppressLint("DefaultLocale") 
	public List<JJLChaxunRecordItem> getRecord(String rcordData,String strType){
		SQLiteDatabase db = m_dbHelper.getReadableDatabase();
		List<JJLChaxunRecordItem> itemList = new ArrayList<JJLChaxunRecordItem>();
		
		if(db.isOpen()){
			
			Cursor cursorDatas =null;
			if(strType ==null)
				cursorDatas = db.rawQuery("select * from "+TABLE_NAME+" where "+DataDao.data_id+"=?", new String[] {rcordData});
			else if(strType == "query_homeno")
			{
				cursorDatas = db.rawQuery("select * from "+TABLE_NAME+" where "+DataDao.data_homeno+"=?", new String[] {rcordData});
				
			}else if(strType == "query_homename")
			{
				cursorDatas = db.rawQuery("select * from "+TABLE_NAME+" where "+DataDao.data_homename+"=?", new String[] {rcordData});
				
			}else if(strType =="query_pdno")
			{
				cursorDatas = db.rawQuery("select * from "+TABLE_NAME+" where "+DataDao.data_pdno+"=?", new String[] {rcordData});
				
			}
			
			while(cursorDatas!=null && cursorDatas.moveToNext()){
				JJLChaxunRecordItem item = null;
				item = new JJLChaxunRecordItem();
				item.data_id= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_id )); 
				item.data_homeno= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_homeno )); 
				
				item.data_homename= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_homename )); 
				 item.data_pdno= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_pdno )); 
				item.data_meterno= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_meterno )); 
				item.data_meterpoint= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_meterpoint )); 
				 item.data_meteraddress= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_meteraddress ));
				 item.data_fixaddress= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_fixaddress )); 
				item.data_poweraddress= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_poweraddress )); 
				 item.data_faultReason= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_faultReason ));
				item.data_faultType= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_faultType )); 
				item.data_checkuser= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_checkuser )); 
				item.data_checkTime= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_checkTime )); 
				
				item.data_adopt_datetime= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_adopt_datetime )); 
				item.data_meter_datetime= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_meter_datetime ));
				item.data_total_power= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_total_power )); 
				item.data_ping_power= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_ping_power )); 
				item.data_gu_power= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_gu_power )); 
				
				item.data_gps_lon= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_gps_lon)); 
				item.data_gps_lat= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_gps_lat)); 
				item.data_atime= cursorDatas.getString(cursorDatas.getColumnIndex(DataDao.data_atime)); 
				itemList.add(item);
			
			}
			cursorDatas.close();
			
		}
		return itemList;
	}

	/**
	 * 删除�?个联系人
	 * @param username
	 */
	public void deleteRecord(String adoptTime){
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, "data_adopt_datetime"+ " = ?", new String[]{adoptTime});
		}
	}
	/**
	 * 保存�?个联系人
	 * @param user
	 */
	public void saveRecord(JJLChaxunRecordItem item){
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		ContentValues content = new ContentValues ();
		
		content.put(DataDao.data_id , item.data_id);
		content.put(DataDao.data_homeno , item.data_homeno);
		
		content.put(DataDao.data_homename , item.data_homename);
		content.put(DataDao.data_pdno , item.data_pdno);
		content.put(DataDao.data_meterno , item.data_meterno);
		content.put(DataDao.data_meterpoint , item.data_meterpoint);
		content.put(DataDao.data_meteraddress , item.data_meteraddress);
		content.put(DataDao.data_fixaddress , item.data_fixaddress);
		content.put(DataDao.data_poweraddress , item.data_poweraddress);
		content.put(DataDao.data_faultReason , item.data_faultReason);
		content.put(DataDao.data_faultType , item.data_faultType);
		content.put(DataDao.data_checkuser , item.data_checkuser);
		content.put(DataDao.data_checkTime , item.data_checkTime);
		content.put(DataDao.data_atime , item.data_atime);
		
		content.put(DataDao.data_adopt_datetime , item.data_adopt_datetime);
		content.put(DataDao.data_meter_datetime , item.data_meter_datetime);
		content.put(DataDao.data_total_power , item.data_total_power);
		content.put(DataDao.data_ping_power , item.data_ping_power);
		content.put(DataDao.data_gu_power , item.data_gu_power);
		
		content.put(DataDao.data_gps_lon, item.data_gps_lon);
		content.put(DataDao.data_gps_lat, item.data_gps_lat);
	
		
		if(db.isOpen()){
			db.insert(TABLE_NAME, null, content);
		}
	}
	

	/**
	 * ����message
	 * @param msgId
	 * @param values
	 */
	public void updateRecord(String pdno,ContentValues values){
		SQLiteDatabase db = m_dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.update(TABLE_NAME, values, DataDao.data_pdno + " = ?", new String[]{pdno});
		}
	}

}

