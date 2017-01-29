package com.topone.btadmin;

import java.util.ArrayList;
import java.util.List;


import com.topone.powerreader.MainActivity;
import com.topone.powerreader.R;
import com.topone.util.LocalPrefInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BTDeviceActivity extends Activity implements OnItemClickListener,
		View.OnClickListener {
	private ListView deviceListview;
	private BTDeviceAdapter adapter;
	private TextView tvSeach;
	protected boolean m_startCheckBTService = false;
	protected boolean m_bFlag = true;
	private ProgressBar m_scanProgressBar;
	private ProgressDialog m_dialog;
	public static List<BTItem> s_deviceList = new ArrayList<BTItem>();
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finddevice);
		// 鍏佽涓荤嚎绋嬭繛鎺ョ綉缁�
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		 m_dialog = new ProgressDialog(BTDeviceActivity.this);
		setView();
		//m_curBTAddress = LocalPrefInfo.getInstance(BTDeviceActivity.this).getPrefInfo(LocalPrefInfo.BT_ADDRESS);
		s_deviceList.clear();
		s_deviceList.addAll( MainActivity.m_btservice.getDeviceList());
		adapter.notifyDataSetChanged();
		thread_update.start();//鐘舵�佺嚎绋嬪惎鍔�
		
	}

	private void setView() {
		deviceListview = (ListView) findViewById(R.id.devicelist);
		deviceListview.setOnItemClickListener(this);
		adapter = new BTDeviceAdapter(BTDeviceActivity.this, s_deviceList);
		deviceListview.setAdapter(adapter);
		deviceListview.setOnItemClickListener(this);
		RelativeLayout rlScan = (RelativeLayout) findViewById(R.id.rl_footer_scan);
		rlScan.setOnClickListener(this);
		tvSeach = (TextView) findViewById(R.id.tv_scan_id);

     	m_scanProgressBar= (ProgressBar) findViewById(R.id.refresh_progressBar);
     	m_scanProgressBar.setVisibility(View.GONE);
	}
	private Thread thread_update = new Thread() 
	{
		public void run() 
		{
			
			while (m_bFlag) 
			{
				try 
				{
					Thread.sleep(200);
				} catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!m_startCheckBTService)
					continue;
				
				BTDeviceActivity.this.runOnUiThread(new Runnable() 
				{
	                public void run() 			
					{
						// TODO Auto-generated method stub
						if (MainActivity.m_btservice != null) 
						{							
							if (MainActivity.m_btservice.getState()== PrinterClass.STATE_CONNECTED) 
							{
								m_bFlag = false;
								Intent back = new Intent();
								back.putExtra("BACK_DATA_NAME", 1);
								 m_dialog.dismiss();
								setResult(RESULT_OK, back);
								MainActivity.m_btservice.stopScan();
								BTDeviceActivity.this.finish();		
							} else if (MainActivity.m_btservice.getState() == PrinterClass.STATE_CONNECTING) {
								//tvSeach.setText(BTDeviceActivity.this.getResources().getString(R.string.str_connecting));
								adapter.notifyDataSetChanged();
							} else if (MainActivity.m_btservice.getState() == PrinterClass.STATE_SCAN_STOP) {
								tvSeach.setText(BTDeviceActivity.this.getResources().getString(R.string.str_rescan));
								adapter.notifyDataSetChanged();
								m_scanProgressBar.setVisibility(View.GONE);								
							} else if (MainActivity.m_btservice.getState() == PrinterClass.STATE_SCANING) {
								tvSeach.setText(BTDeviceActivity.this.getResources().getString(R.string.str_scaning));
								adapter.notifyDataSetChanged();
							}else if (MainActivity.m_btservice.getState() == PrinterClass.STATE_SCANING) {
								tvSeach.setText(BTDeviceActivity.this.getResources().getString(R.string.str_scaning));
								adapter.notifyDataSetChanged();
							} else {
								
								//tvSeach.setText(BTDeviceActivity.this.getResources().getString(R.string.str_disconnected));
								 m_dialog.dismiss();
							}
						}
					}
				});
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		 
		 final BTItem item = (BTItem) adapter.getItem(position);
		 String strBluetoothAdd = LocalPrefInfo.getInstance(BTDeviceActivity.this).getPrefInfo(LocalPrefInfo.BT_ADDRESS);
		 m_dialog.setMessage(BTDeviceActivity.this.getResources().getString(R.string.str_connecting));
		 m_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
     	 m_dialog.show();
     	
		 if (MainActivity.m_btservice.getState()== PrinterClass.STATE_CONNECTED && strBluetoothAdd.equalsIgnoreCase(item.getBluetoothAddress())) 
		 {			
				 MainActivity.m_btservice.disconnect();
				 LocalPrefInfo.getInstance(BTDeviceActivity.this).setPrefInfo(LocalPrefInfo.BT_ADDRESS, "");
		 }
		 MainActivity.m_btservice.connect(item.getBluetoothAddress());
		 m_startCheckBTService = true;
		  
	}

	@Override
	public void onClick(View v) {
		if (!MainActivity.m_btservice.IsOpen()) {
			MainActivity.m_btservice.open(BTDeviceActivity.this);
		}
		switch (v.getId()) {
		case R.id.rl_footer_scan:
			
			
			m_scanProgressBar.setVisibility(View.VISIBLE);
			s_deviceList.clear();
			List<BTItem> tempdeviceList = MainActivity.m_btservice.getDeviceList();
			s_deviceList.addAll(tempdeviceList);
			MainActivity.m_btservice.scan();
			adapter.notifyDataSetChanged();
			m_startCheckBTService = true;
			break;
	
		}

	}

	public void back(View view) {
		m_bFlag = false;
		MainActivity.m_btservice.abortOprating();
		 m_dialog.dismiss();
		if(thread_update.isAlive())
			try {
				thread_update.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		finish();
	}

}
