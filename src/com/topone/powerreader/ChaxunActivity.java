package com.topone.powerreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.topone.db.DataDao;
import com.topone.loc.PopupScrollPanel;
import com.topone.loc.PopupScrollPanel.ScrollDataCallback;
import com.topone.powerreader.LoadDataFromServer.DataCallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChaxunActivity extends Activity {
	protected PopupScrollPanel m_popScroll;
	private ArrayList<Pair<String, String>> m_queryList;
	private TextView m_devicedata_id;
	private String m_queryType = "query_homeno";
	private ProgressDialog m_dialog;
	private EditText m_dev_num;
	private TextView m_address_id;
	private ListView m_recordlistView;
	public List<JJLChaxunRecordItem> m_dataList = new ArrayList<JJLChaxunRecordItem>();
	private RecordListAdapter m_adapter;
	
	private int m_nRecordSize = 0;
	private Timer m_timerSyncData;
	private TimerTask m_timerTaskSyncDataState;
	protected int m_lastSysncSize = 0;
	protected int m_curSyncSize =0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chaxun);
		m_dialog = new ProgressDialog(ChaxunActivity.this);
		m_queryList = new ArrayList<Pair<String, String>>();
		m_queryList.add(new Pair<String, String>("query_homeno", "户号"));
		m_queryList.add(new Pair<String, String>("query_homename", "户名"));
		m_queryList.add(new Pair<String, String>("query_pdno", "表号"));
		m_devicedata_id = (TextView) findViewById(R.id.tv_devicedata_id);
		m_popScroll = new PopupScrollPanel(ChaxunActivity.this, m_devicedata_id);
		m_popScroll.updateDataList(m_queryList);
		m_popScroll.setOnDataCallbak(new ScrollDataCallback() {
			@Override
			public void ProcessSelDate(Pair<String, String> stationInfoDate) {
				m_queryType = stationInfoDate.first;
				m_devicedata_id.setText(stationInfoDate.second);
				m_popScroll.dismiss();
			}
		});

		RelativeLayout rl_query_type = (RelativeLayout) findViewById(R.id.rl_query_type);
		rl_query_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				m_popScroll.showAsDropDown(m_devicedata_id);

			}
		});
		
		m_recordlistView = (ListView)findViewById(R.id.lv_record_list);
		m_recordlistView.setVisibility(View.GONE);
		m_adapter = new RecordListAdapter(ChaxunActivity.this, m_dataList);
		m_recordlistView.setAdapter(m_adapter);
		
		m_address_id = (TextView)findViewById(R.id.tv_address_id);
		m_address_id.setVisibility(View.VISIBLE);
		
		m_dev_num = (EditText) findViewById(R.id.et_dev_num);
	
		TextView tv_syncdata_id = (TextView) findViewById(R.id.tv_syncdata_id);
		tv_syncdata_id.setOnClickListener(m_synicListener);
		
		TextView tv_query_id = (TextView) findViewById(R.id.tv_query_id);
		tv_query_id.setOnClickListener(m_queryListener);
		

		m_timerSyncData = new Timer();
		m_timerTaskSyncDataState = new TimerTask()
		{
			// 表示1秒发送一次消息(第一个10表示首次运行启动时间，第二1表示中间间隔时间）
			public void run()
			{
				Message msg = new Message();
				msg.what = 0x1111;
				CheckTicketStatushandler.sendMessage(msg);
			}
		};
		
		
	}
	
	Handler CheckTicketStatushandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == 0x1111) // 表示本程序所发送的消息
			{				
				if(m_curSyncSize == m_lastSysncSize && m_curSyncSize<m_nRecordSize)
				{
					int nStep = 200;
					if(m_curSyncSize+nStep >m_nRecordSize)
						nStep = m_nRecordSize-m_curSyncSize;
					StartSyncRecordDatas(m_curSyncSize,nStep);
				}
				else if (m_lastSysncSize==m_nRecordSize)
				{
					m_dialog.dismiss();
					Toast.makeText(ChaxunActivity.this,"同步数据完成" ,Toast.LENGTH_SHORT).show();
					if (m_timerSyncData != null)
					{
						m_timerSyncData.cancel();
						m_timerSyncData = null;
					}
					if (m_timerTaskSyncDataState != null)
					{
						m_timerTaskSyncDataState.cancel();
						m_timerTaskSyncDataState = null;
					}
				}
			}
		}
	};

	private OnClickListener m_synicListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			m_dialog.setMessage(ChaxunActivity.this.getResources().getString(R.string.str_start_synic_info));
			m_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			m_dialog.setCancelable(false);// 按到对话框以外的地方不起作用，相当于模态对话框。按返回键也不起作用。
											// setCanceledOnTouchOutside(false);调用这个方法时，按对话框以外的地方不起作用。按返回键还起作用
			m_dialog.show();
			//获取数据总量，启动数据同步线程
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("query_type", "GET_DATA_SIZE");
			dataMap.put("pda_dev_id", getIntent().getStringExtra("pda_dev_id"));
			LoadDataFromServer task = new LoadDataFromServer(LoadDataFromServer.URL_queryInfo, dataMap);
			task.AsyncPostData(new DataCallBack() {
				@Override
				public void onDataCallBack(JSONObject data) {
					
					if (data == null) {
						m_dialog.dismiss();
						Toast.makeText(ChaxunActivity.this, "请检查网络",Toast.LENGTH_SHORT).show();
						return;
					}

					try {
						int code = data.getInteger("status");
						switch (code) {
						case 1:// success
						{
							JSONObject json = data.getJSONObject("info");
							if (json != null) {
								
							//m_address_id.setVisibility(View.GONE);
							
							int nSize = json.getInteger("record_size");
							
							m_nRecordSize = nSize;
							m_dialog.setMessage("数据总量为"+String.valueOf(nSize)+",同步开始...");
							m_curSyncSize = m_lastSysncSize = 0;
							m_timerSyncData.schedule(m_timerTaskSyncDataState, 100, 1000);
							}
							return;

						}
						case 0:// failed
						{
							m_dialog.dismiss();
							String strMessage = data.getString("message");
							Toast.makeText(ChaxunActivity.this, strMessage,Toast.LENGTH_SHORT).show();
							break;
						}
						}

					} catch (Exception e) {
						m_dialog.dismiss();
						Toast.makeText(ChaxunActivity.this,"返回信息出错:" + e.getMessage(),Toast.LENGTH_SHORT).show();
					}
				}
			});
			//1.线程中启动数据请求，每次请求数据200个，请求结束，数据计数叠加200，再请求下一个200.同时本线程将请求的数据进行本地数据同步...一直到结束
		}
	};
	
	private OnClickListener m_queryListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			m_dialog.setMessage(ChaxunActivity.this.getResources()
					.getString(R.string.str_start_query_info));
			m_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			m_dialog.setCancelable(false);// 按到对话框以外的地方不起作用，相当于模态对话框。按返回键也不起作用。
											// setCanceledOnTouchOutside(false);调用这个方法时，按对话框以外的地方不起作用。按返回键还起作用
			m_dialog.show();

			final Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("query_type", m_queryType);
			dataMap.put("query_data", m_dev_num.getText().toString());
			dataMap.put("pda_dev_id", getIntent().getStringExtra("pda_dev_id"));
			LoadDataFromServer task = new LoadDataFromServer(LoadDataFromServer.URL_queryInfo, dataMap);
			task.AsyncPostData(new DataCallBack() {
				@Override
				public void onDataCallBack(JSONObject data) {
					m_dialog.dismiss();
					if (data == null) {						
						 DataDao dao = new DataDao(ChaxunActivity.this);
						 List<JJLChaxunRecordItem> dataList= dao.getRecord((String)dataMap.get("query_data") ,m_queryType);
						 
						if(dataList.size()>0)
						{
							m_dataList.clear();
							m_recordlistView.setVisibility(View.VISIBLE);
							m_dataList.addAll(dataList);
							m_adapter.notifyDataSetChanged();
							Toast.makeText(ChaxunActivity.this, "网络未连接，显示本地数据",Toast.LENGTH_SHORT).show();
						}else
							Toast.makeText(ChaxunActivity.this, "请检查网络",Toast.LENGTH_SHORT).show();
						return;
					}

					try {
						int code = data.getInteger("status");
						switch (code) {
						case 1:// success
						{
							JSONObject json = data.getJSONObject("info");
							if (json != null) {
								
							m_recordlistView.setVisibility(View.VISIBLE);
							m_dataList.clear();
							JSONArray recordlist = json.getJSONArray("record");
							for(int i=0;i<recordlist.size();i++)
							{
								JJLChaxunRecordItem item = new JJLChaxunRecordItem();
								JSONObject cell = recordlist.getJSONObject(i);
								item.data_homeno = cell.getString("pd_homeno");
								item.data_homename = cell.getString("pd_homename");
								item.data_pdno = cell.getString("pd_no");
								item.data_meterno = cell.getString("handmeter_no");
								item.data_meterpoint = cell.getString("meter_point");
								item.data_meteraddress = cell.getString("handmeter_address");
								item.data_fixaddress = cell.getString("fix_address");
								item.data_poweraddress = cell.getString("pd_address");
								item.data_faultReason = cell.getString("fault_comment");
								item.data_faultType = cell.getString("fault_type");
								item.data_checkuser = cell.getString("staff_name");
								item.data_checkTime = cell.getString("sys_clock");
								m_dataList.add(item);
							}
							m_adapter.notifyDataSetChanged();

							}
							return;

						}
						case 0:// failed
						{
							String strMessage = data.getString("message");
							Toast.makeText(ChaxunActivity.this, strMessage,
									Toast.LENGTH_SHORT).show();
							
							
							break;
						}
						}

					} catch (Exception e) {
						Toast.makeText(ChaxunActivity.this,
								"返回信息出错:" + e.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	};
	protected boolean m_stopSync;

	public void back(View view) {

		finish();
	}

	protected void StartSyncRecordDatas(int curSyncSize, int nStep) {
		m_dialog.setMessage(ChaxunActivity.this.getResources()
				.getString(R.string.str_start_synic_info));
		m_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_dialog.setCancelable(false);// 按到对话框以外的地方不起作用，相当于模态对话框。按返回键也不起作用。
										// setCanceledOnTouchOutside(false);调用这个方法时，按对话框以外的地方不起作用。按返回键还起作用
		m_dialog.show();
		//获取数据总量，启动数据同步线程
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("query_type", "SYNC_DATA");
		dataMap.put("pda_dev_id", getIntent().getStringExtra("pda_dev_id"));
		dataMap.put("sync_start_index", String.valueOf(curSyncSize));
		dataMap.put("sync_count", String.valueOf(nStep));
		m_curSyncSize +=nStep;
		
		LoadDataFromServer task = new LoadDataFromServer(
				LoadDataFromServer.URL_queryInfo, dataMap);
		task.AsyncPostData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				
				if (data == null) {
					m_dialog.dismiss();
					Toast.makeText(ChaxunActivity.this, "请检查网络",Toast.LENGTH_SHORT).show();
					m_stopSync = true;
					return;
				}

				try {
					int code = data.getInteger("status");
					switch (code) {
					case 1:// success
					{
						JSONObject json = data.getJSONObject("info");
						if (json != null) {
							
							JSONArray recordlist = json.getJSONArray("record");
							for(int i=0;i<recordlist.size();i++)
							{
								JJLChaxunRecordItem item = new JJLChaxunRecordItem();
								JSONObject cell = recordlist.getJSONObject(i);
								item.data_id = cell.getString("id");
								item.data_homeno = cell.getString("pd_homeno");
								item.data_homename = cell.getString("pd_homename");
								item.data_pdno = cell.getString("pd_no");
								item.data_meterno = cell.getString("handmeter_no");
								item.data_meterpoint = cell.getString("meter_point");
								item.data_meteraddress = cell.getString("handmeter_address");
								item.data_fixaddress = cell.getString("fix_address");
								item.data_poweraddress = cell.getString("pd_address");
								item.data_faultReason = cell.getString("fault_comment");
								item.data_faultType = cell.getString("fault_type");
								item.data_checkuser = cell.getString("staff_name");
								item.data_checkTime = cell.getString("sys_clock");
								item.data_atime = cell.getString("atime");
								//get local data and then compare them
								  DataDao dao = new DataDao(ChaxunActivity.this);
							      List<JJLChaxunRecordItem> localItemList = dao.getRecord(item.data_id,null );
							      if(localItemList.size()==0)
							    	  dao.saveRecord(item);
							      else if(item.data_atime ==null || Integer.parseInt(localItemList.get(0).data_atime)> Integer.parseInt(item.data_atime))
							      {
							    	  Map<String, Object> m_dataMap = new HashMap<String, Object>();
							    	  
							      
							    		m_dataMap.put("pda_dev_id", localItemList.get(0).data_meterno);
							    		m_dataMap.put("fault_comment", localItemList.get(0).data_faultReason);
										m_dataMap.put("fault_type", localItemList.get(0).data_faultType);
										
										m_dataMap.put("data_adopt_datetime", localItemList.get(0).data_adopt_datetime);
										
										m_dataMap.put("data_gps_lon",  localItemList.get(0).data_gps_lon);
										m_dataMap.put("data_gps_lat",  localItemList.get(0).data_gps_lat);

										m_dataMap.put("total_power",  localItemList.get(0).data_total_power);
										m_dataMap.put("ping_power",  localItemList.get(0).data_ping_power);
										m_dataMap.put("gu_power",  localItemList.get(0).data_gu_power);
										m_dataMap.put("data_meter_datetime", localItemList.get(0).data_meter_datetime);
										LoadDataFromServer task = new LoadDataFromServer(LoadDataFromServer.URL_savedata, m_dataMap);
										task.AsyncPostData(new DataCallBack() {

											@Override
											public void onDataCallBack(
													JSONObject data) {
												// TODO Auto-generated method stub
												
											}
											
										});
							      }
							    	  
								
							}
							m_lastSysncSize = m_curSyncSize;
							m_dialog.setMessage("已同步"+String.valueOf(m_lastSysncSize)+"条数据.");
							m_dialog.dismiss();
						}
						return;

					}
					case 0:// failed
					{
						m_dialog.dismiss();
						String strMessage = data.getString("message");
						Toast.makeText(ChaxunActivity.this, strMessage,
								Toast.LENGTH_SHORT).show();
						m_stopSync = true;
						break;
					}
					}

				} catch (Exception e) {
					m_dialog.dismiss();
					Toast.makeText(ChaxunActivity.this,
							"返回信息出错:" + e.getMessage(),
							Toast.LENGTH_SHORT).show();
					m_stopSync = true;
				}
			}
		});
		
	}
}
