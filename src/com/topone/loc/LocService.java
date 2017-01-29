package com.topone.loc;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

public class LocService extends Service {
	ArrayList<LocCellInfo> cellIds = null;
	private LocGPS gps = null;
	private boolean threadDisable = false;
	private final static String TAG = LocService.class.getSimpleName();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		gps = new LocGPS(LocService.this);
		cellIds = UtilTools.init(LocService.this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!threadDisable) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if (gps != null) { 
						Location location = gps.getLocation();
						// ���gps�޷���ȡ��γ�ȣ����û�վ��λ��ȡ
						if (location == null) {
							//Log.v(TAG, "gps location null");
							// 2.���ݻ�վ��Ϣ��ȡ��γ��
							try {
								location = UtilTools.callGear(LocService.this,
										cellIds);
							} catch (Exception e) {
								location = null;
								e.printStackTrace();
							}
							if (location == null) {
								//Log.v(TAG, "cell location null");
							}
						}

						// ���͹㲥
						Intent intent = new Intent();
						intent.putExtra("lat",
								location == null ? "" : location.getLatitude()
										+ "");
						intent.putExtra("lon",
								location == null ? "" : location.getLongitude()
										+ "");
						intent.setAction("com.topone.loc.LocService");
						sendBroadcast(intent);
					}

				}
			}
		}).start();
	}

	@Override
	public void onDestroy() {

		threadDisable = true;
		if (cellIds != null && cellIds.size() > 0) {
			cellIds = null;
		}
		if (gps != null) {
			gps.closeLocation();
			gps = null;
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
