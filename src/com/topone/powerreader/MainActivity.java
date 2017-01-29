package com.topone.powerreader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSONObject;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import com.topone.btadmin.BTDeviceActivity;
import com.topone.btadmin.BTItem;
import com.topone.btadmin.BtService;
import com.topone.btadmin.PrinterClass;
import com.topone.db.DataDao;
import com.topone.loc.BaiduMapActivity;
//import com.topone.BTManager.BTClient;
//import com.topone.BTManager.BTDeviceActivity;
//import com.topone.BTManager.BTItem;
//import com.topone.BTManager.BTManage;
//import com.topone.BTManager.BluetoothMsg;
import com.topone.loc.LocService;
import com.topone.loc.PopupScrollPanel;
import com.topone.loc.ShowBigImageActivity;
import com.topone.loc.UtilTools;
import com.topone.loc.PopupScrollPanel.ScrollDataCallback;
import com.topone.powerreader.R;
import com.topone.powerreader.LoadDataFromServer.DataCallBack;
import com.topone.util.ExpandGridView;
import com.topone.util.ImageUtils;
import com.topone.util.LocalPrefInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	public final static String TYPE_elec_meter = "100";
	public final static String TYPE_elec_xx = "101";
	public final static String TYPE_elec_xxx = "102";

	private final static String MT_DATA_ZXYGZDN = "00010000";// 当前正向有功总电能
	private final static String MT_DATA_ZXYGFL1 = "00010100";// 当前正向有功峰
	private final static String MT_DATA_ZXYGFL2 = "00010200";// 当前正向有功平
	private final static String MT_DATA_ZXYGFL3 = "00010300";// 当前正向有功尖
	private final static String MT_DATA_ZXYGFL4 = "00010400";// 当前正向有功谷

	private final static String MT_DATA_BNRQ = "04000101";// 当前表内日期
	private final static String MT_DATA_BNSJ = "04000102";// 当前表内时间

	private final static String MT_DATA_1RDJSJ = "05060001";// 上1次日冻结时间
	private final static String MT_DATA_1RDJYGDN = "05060101";// 上1次日冻结正向有功电能数据
	private final static String MT_DATA_1RDJSJK = "0506FF01";// (上1次)日冻结数据块
	
	private final static String MT_DATA_PFLOWBLOCK = "0202FF00";// 电流数据块
	private final static String MT_DATA_PPOWERBLOCK = "0203FF00";// 瞬时有功功率数据块
	private final static String MT_DATA_DROPPOWERCOUNT = "03110000";// 掉电总次数
	private final static String MT_DATA_DROPPOWERLAST1 = "03110001";// （上1次）掉电时间
	
	private final static String MT_DATA_OPENCOVERCOUNT = "03300D00";// 开表盖次数
	
	public final int MAX_UP_IMG_NUM = 9;
	private String m_meterDatetime = "";
	protected HashMap<String, Object> m_dataMap = new HashMap<String, Object>();

	private boolean m_bAllreadyFull = false;

	protected PopupScrollPanel m_popScrollDev;
	protected PopupScrollPanel m_popScrollData;
	protected PopupScrollPanel m_popScrollFault;

	private Pair<String, String> m_devInfo;
	private Pair<String, AdoptData> m_dataInfo;
	private Pair<String, String> m_faultInfo;

	private TextView tv_devicetype_id;
	private TextView tv_devicedata_id;
	private TextView tv_fault_type;
	public static BtService m_btservice = null;
	private ImageView m_bluetoothIcon;
	private ProgressDialog m_dialog;
	private TextView tv_rcv_date_id;
	int LEN_START = 1, LEN_DEV = 6, LEN_CTRL = 1, LEN_LEN = 1, LEN_CS = 1,
			LEN_END = 1;
	private int m_nLenght = 0;

	private byte[] m_rcvBuffer = new byte[1024];

	private boolean m_bStartRcv = false;
	private ArrayList<Pair<String, String>> m_devList;
	private EleDevice m_devEle = null;
	private byte[] m_sendData = null;
	private EditText m_deviceNo;
	private com.topone.util.ExpandGridView mGridview;
	private GridPicAdapter mAdapter;
	private String m_szImei;
	protected boolean m_bTConn = false;
	private LocReceiver m_locReceiver;
	public static List<Bitmap> mObjects = new ArrayList<Bitmap>();
	private double m_dwLat, m_dwLon;
	private EditText m_dataComment;
	private ImageView m_gpsIcon;
	private TextView mtvSpp;
	private String m_strShowData = "";
	public boolean m_bSPPConn = false;
	public boolean m_bGPSConn = false;
	// public boolean m_bTConn = false;
	private final String TAG = MainActivity.class.getSimpleName();
	// private List<DeviceEntry> mEntries = new ArrayList<DeviceEntry>();
	DeviceEntry m_deviceEntry;
	private UsbManager mUsbManager;

	private static final int MESSAGE_REFRESH = 101;
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_REFRESH:
				refreshDeviceList();
				// mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH,
				// REFRESH_TIMEOUT_MILLIS);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}

	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		m_dialog = new ProgressDialog(MainActivity.this);

		m_devList = new ArrayList<Pair<String, String>>();
		m_devList.add(new Pair<String, String>(TYPE_elec_meter, "电表"));

		setView();

		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		m_szImei = TelephonyMgr.getDeviceId();

		mObjects.clear();

		m_popScrollDev.updateDataList(m_devList);

		ArrayList<Pair<String, String>> faultList = new ArrayList<Pair<String, String>>();
		faultList.add(new Pair<String, String>("0", "无"));
		faultList.add(new Pair<String, String>("1", "施工质量"));
		faultList.add(new Pair<String, String>("2", "表计故障"));
		faultList.add(new Pair<String, String>("3", "采集器故障"));
		faultList.add(new Pair<String, String>("4", "集中器故障"));
		faultList.add(new Pair<String, String>("5", "拆迁"));
		faultList.add(new Pair<String, String>("6", "门闭（拒进）"));
		faultList.add(new Pair<String, String>("7", "GPRS信号"));
		faultList.add(new Pair<String, String>("8", "未找到"));
		m_popScrollFault.updateDataList(faultList);

		openGPS();
		if (!m_bSPPConn)
			DeviceStateChange();

	}

	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(MESSAGE_REFRESH);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeMessages(MESSAGE_REFRESH);
	}

	/**
	 * 打开蓝牙设备
	 */
	private void openBtDevice() {
		m_btservice = new BtService(MainActivity.this, mhandler, m_scanHandler);
		if (!m_btservice.IsOpen()) {
			m_btservice.open(this);
		}
		String strBluetoothAdd = LocalPrefInfo.getInstance(MainActivity.this)
				.getPrefInfo(LocalPrefInfo.BT_ADDRESS);
		LocalPrefInfo.getInstance(MainActivity.this).setPrefInfo(
				LocalPrefInfo.BT_ADDRESS, "");
		if (m_btservice.IsOpen() && strBluetoothAdd != null
				&& strBluetoothAdd.trim().length() > 0) {
			m_btservice.connect(strBluetoothAdd);
			m_dialog.setMessage(MainActivity.this.getResources().getString(
					R.string.str_connecting));
			m_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			m_dialog.show();
		}

	}

	/**
	 * 打开GPS设备
	 */
	private void openGPS() {
		// 判断GPS是否可用
		// Log.i(TAG,
		// UtilTools.isGpsEnabled((LocationManager)getSystemService(Context.LOCATION_SERVICE))+"");
		if (!UtilTools
				.isGpsEnabled((LocationManager) getSystemService(Context.LOCATION_SERVICE))) {
			Toast.makeText(this, "GSP当前已禁用，请在您的系统设置屏幕启动。", Toast.LENGTH_LONG)
					.show();
			Intent callGPSSettingIntent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(callGPSSettingIntent);
			return;
		}

		// 启动服务
		startService(new Intent(this, LocService.class));
		// 注册广播
		m_locReceiver = new LocReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.topone.loc.LocService");
		registerReceiver(m_locReceiver, filter);
	}

	// 获取广播数据
	private class LocReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String strLon = bundle.getString("lon");
			if (strLon == null || strLon.trim().length() == 0)
				m_dwLon = 0;
			else
				m_dwLon = Double.parseDouble(strLon);

			String strLat = bundle.getString("lat");
			if (strLat == null || strLat.trim().length() == 0)
				m_dwLat = 0;
			else
				m_dwLat = Double.parseDouble(strLat);
			if (m_dwLon == 0 && m_dwLat == 0) {
				m_gpsIcon.setImageResource(R.drawable.gps_normal);
				return;
			}
			if (m_bGPSConn)
				m_gpsIcon.setImageResource(R.drawable.gps_normal);
			else
				m_gpsIcon.setImageResource(R.drawable.gps_conn);
			m_bGPSConn = !m_bGPSConn;
			// if(lon!=null&&!"".equals(lon)&&lat!=null&&!"".equals(lat)){
			// double distance=getDistance(Double.parseDouble(lat),
			// Double.parseDouble(lon), homeLat, homeLon);
			// editText.setText("目前经纬度\n经度："+lon+"\n纬度："+lat+"\n离宿舍距离："+java.lang.Math.abs(distance));
			// }else{
			// editText.setText("目前经纬度\n经度："+lon+"\n纬度："+lat);
			// }
		}
	}

	private void setView() {
		//TextView tv_data_list = (TextView) findViewById(R.id.tv_data_list);

		//tv_data_list.setOnClickListener(datalistListener);
		m_gpsIcon = (ImageView) findViewById(R.id.iv_gps_id);

		m_gpsIcon.setOnClickListener(mGettGpsListener);

		m_bluetoothIcon = (ImageView) findViewById(R.id.iv_bluetooth_id);

		m_bluetoothIcon.setOnClickListener(BTClickListener);

		final LinearLayout llDeviceType = (LinearLayout) findViewById(R.id.ll_devicetype_id);
		tv_devicetype_id = (TextView) findViewById(R.id.tv_devicetype_id);
		llDeviceType.setOnClickListener(deviceTypeSelListener());
		m_dataComment = (EditText) findViewById(R.id.et_tjournal_info_id);

		final LinearLayout llDataType = (LinearLayout) findViewById(R.id.ll_devicedata_id);
		tv_devicedata_id = (TextView) findViewById(R.id.tv_devicedata_id);
		llDataType.setOnClickListener(deviceDataSelListener());

		tv_fault_type = (TextView) findViewById(R.id.tv_fault_type);
		tv_fault_type.setOnClickListener(defaultTypeSelListener());

		Button btn_get_info = (Button) findViewById(R.id.btn_get_info);
		btn_get_info.setOnClickListener(mAdoptDeviceData);

		tv_rcv_date_id = (TextView) findViewById(R.id.tv_rcv_date_id);
		m_deviceNo = (EditText) findViewById(R.id.et_dev_num);
		String digists = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		m_deviceNo.setKeyListener(DigitsKeyListener.getInstance(digists));
		
		m_popScrollDev = new PopupScrollPanel(MainActivity.this,
				tv_devicetype_id);
		m_popScrollDev.setOnDataCallbak(new ScrollDataCallback() {
			@Override
			public void ProcessSelDate(Pair<String, String> stationInfoDate) {
				m_devInfo = stationInfoDate;
				UpdateDevTypeInfo();
			}
		});

		m_popScrollData = new PopupScrollPanel(MainActivity.this,
				tv_devicedata_id);
		m_popScrollData.setOnDataCallbak(new ScrollDataCallback() {
			@Override
			public void ProcessSelDate(Pair<String, String> stationInfoDate) {
				m_dataInfo = Pair.create(stationInfoDate.first, new AdoptData(
						stationInfoDate.second, ""));

				UpdateDataTypeInfo();

			}
		});

		m_popScrollFault = new PopupScrollPanel(MainActivity.this,
				tv_fault_type);
		m_popScrollFault.setOnDataCallbak(new ScrollDataCallback() {
			@Override
			public void ProcessSelDate(Pair<String, String> stationInfoDate) {
				m_faultInfo = stationInfoDate;
				UpdateFaultInfo();

			}
		});

		mGridview = (ExpandGridView) findViewById(R.id.gridview);
		mAdapter = new GridPicAdapter(this, mObjects);
		mGridview.setAdapter(mAdapter);

		TextView tv_submit_id = (TextView) findViewById(R.id.tv_submit_id);

		tv_submit_id.setOnClickListener(mSubmitDataListener);

		mtvSpp = (TextView) findViewById(R.id.tv_spp_id);
		mtvSpp.setOnClickListener(this);

		Button btn_reset_info = (Button) findViewById(R.id.btn_reset_info);
		btn_reset_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				m_dataMap.clear();
				m_deviceNo.setText("");
				m_dataComment.setText("");
				tv_fault_type.setText("无");
				m_strShowData = "";
				tv_rcv_date_id.setText("");
				mObjects.clear();
				updateImages();
			}
		});

		ImageView ivScan = (ImageView) findViewById(R.id.iv_scan_id);
		ivScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(MainActivity.this,
						CaptureActivity.class), 104);

			}
		});

		//主菜单
		Button btn_tongji_id = (Button) findViewById(R.id.btn_tongji_id);
		btn_tongji_id.setOnClickListener(this);
		
		Button btn_chaxun_id = (Button) findViewById(R.id.btn_chaxun_id);
		btn_chaxun_id.setOnClickListener(this);
		
		Button btn_daohang_id= (Button)findViewById(R.id.btn_daohang_id);
		btn_daohang_id.setOnClickListener(this);
		
		Button btn_bangzhu_id= (Button)findViewById(R.id.btn_bangzhu_id);
		btn_bangzhu_id.setOnClickListener(this);
		
		
	}

	OnClickListener mSubmitDataListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			String strdevNo = m_deviceNo.getText().toString().trim();
			if (strdevNo.length() == 0) {
				Toast.makeText(MainActivity.this, "请确认电表号信息！",Toast.LENGTH_LONG).show();
				return;
			}
			if (!tv_fault_type.getText().equals("无")) {
				m_dataMap.put("fault_comment", m_dataComment.getText().toString());
				m_dataMap.put("fault_type", tv_fault_type.getText().toString());
			} else if (tv_rcv_date_id.getText().toString().trim().length() == 0) {
				Toast.makeText(MainActivity.this, "未获取有效数据！", Toast.LENGTH_LONG).show();
				return;
			}
			
			m_dataMap.put("pda_dev_id", m_szImei);
			
			m_dataMap.put("data_dev_id", strdevNo);
			m_dataMap.put("data_dev_type",m_devInfo.second);
			if(m_dataInfo.second.RESPONSE_DATETIME == null || m_dataInfo.second.RESPONSE_DATETIME.trim().length()==0)
			{
				Calendar calendar = Calendar.getInstance(Locale.CHINA);
				Date date = calendar.getTime();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateString = dateFormat.format(date);
				m_dataInfo.second.RESPONSE_DATETIME = dateString;
			}
			
			m_dataMap.put("data_adopt_datetime", m_dataInfo.second.RESPONSE_DATETIME);
			
			
			m_dataMap.put("data_gps_lon", Double.toString(m_dwLon));
			m_dataMap.put("data_gps_lat", Double.toString(m_dwLat));
			if (mObjects.size() > 0)
				m_dataMap.put("data_image_list", mObjects);

			m_dialog.setMessage(MainActivity.this.getResources().getString(R.string.str_start_pub_info));
			m_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			m_dialog.setCancelable(false);// 按到对话框以外的地方不起作用，相当于模态对话框。按返回键也不起作用。
											// setCanceledOnTouchOutside(false);调用这个方法时，按对话框以外的地方不起作用。按返回键还起作用
			m_dialog.show();

			LoadDataFromServer task = new LoadDataFromServer(
					LoadDataFromServer.URL_savedata, m_dataMap);
			task.AsyncPostData(new DataCallBack() {
				@Override
				public void onDataCallBack(JSONObject data) {
					m_dialog.dismiss();
					if (data == null) {
						Toast.makeText(MainActivity.this, "请检查网络",
								Toast.LENGTH_SHORT).show();
						return;
					}

					try {
						int code = data.getInteger("status");
						switch (code) {
						case 1:// success
						{
							JSONObject json = data.getJSONObject("info");
							if (json != null) {
								final String strRes = json.getString("result");
								if (strRes.equalsIgnoreCase("OK")) {
									Toast.makeText(MainActivity.this, "提交成功",
											Toast.LENGTH_SHORT).show();
									m_dataMap.clear();
									m_dataComment.setText("");
									tv_fault_type.setText("无");
									m_strShowData = "";
									tv_rcv_date_id.setText("");
									mObjects.clear();
									updateImages();
								} else
									Toast.makeText(MainActivity.this, strRes,
											Toast.LENGTH_SHORT).show();
							}
							

						}
						case 0:// failed
						{
							String strMessage = data.getString("message");
							Toast.makeText(MainActivity.this, strMessage,
									Toast.LENGTH_SHORT).show();
							break;
						}
						}

					} catch (Exception e) {
						Toast.makeText(MainActivity.this,
								"返回信息出错:" + e.getMessage(), Toast.LENGTH_SHORT)
								.show();
					}
					
					JJLChaxunRecordItem item = new JJLChaxunRecordItem();
					//m_dataMap.put("total_power", Float.toString(fValue));
					item.data_pdno = (String) m_dataMap.get("data_dev_id");
					item.data_meterno = (String) m_dataMap.get("pda_dev_id");
					item.data_adopt_datetime = (String) m_dataMap.get("data_adopt_datetime");
					item.data_gps_lon = (String) m_dataMap.get("data_gps_lon");
					item.data_gps_lat = (String) m_dataMap.get("data_gps_lat");
					item.data_faultReason = m_dataMap.get("fault_comment")==null?"":(String) m_dataMap.get("fault_comment");
					item.data_faultType = m_dataMap.get("fault_type")==null?"":(String) m_dataMap.get("fault_type");
					
					DataDao dt = new DataDao(MainActivity.this);
					ContentValues content = new ContentValues ();
					
					content.put(DataDao.data_meterno , item.data_meterno);
					content.put(DataDao.data_adopt_datetime , item.data_adopt_datetime);
					
					content.put(DataDao.data_gps_lon , item.data_gps_lon);
					content.put(DataDao.data_gps_lat , item.data_gps_lat);
					
					content.put(DataDao.data_faultReason , item.data_faultReason);
					content.put(DataDao.data_faultType , item.data_faultType);
					content.put(DataDao.data_atime , String.valueOf(System.currentTimeMillis()/1000));
					if(m_dataMap.containsKey("total_power"))
						content.put(DataDao.data_total_power , (String)m_dataMap.get("total_power"));
					if(m_dataMap.containsKey("ping_power"))
						content.put(DataDao.data_ping_power , (String)m_dataMap.get("ping_power"));
					if(m_dataMap.containsKey("gu_power"))
						content.put(DataDao.data_gu_power , (String)m_dataMap.get("gu_power"));
				
					dt.updateRecord(item.data_pdno, content);

				}
			});
		}
	};

	OnClickListener mAdoptDeviceData = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (m_sendData == null) {
				Toast.makeText(MainActivity.this, "请选择数据类型", 1000).show();
				return;
			}
			if (m_deviceNo.getText().toString().trim().length() == 0) {
				Toast.makeText(MainActivity.this, "请输入设备号", 1000).show();
				return;
			}

			if (m_bSPPConn && mSerialIoManager != null) {
				byte[] f_data = CreateReadDataFrame(m_deviceNo.getText()
						.toString(), m_sendData);
				byte[] start = { (byte) 0xFE, (byte) 0xFE, (byte) 0xFE,
						(byte) 0xFE };
				mSerialIoManager.writeAsync(start);
				mSerialIoManager.writeAsync(f_data);
			}
			if (m_bTConn && m_btservice != null) {
				byte[] f_data = CreateReadDataFrame(m_deviceNo.getText()
						.toString(), m_sendData);
				byte[] start = { (byte) 0xFE, (byte) 0xFE, (byte) 0xFE,
						(byte) 0xFE };
				if (!m_btservice.write(start)) {
					Toast.makeText(MainActivity.this, "发送数据失败", 1000).show();
					return;
				}
				m_btservice.write(f_data);
			}

			if ((!m_bSPPConn || mSerialIoManager == null)
					&& (!m_bTConn || m_btservice == null)) {
				Toast.makeText(MainActivity.this, "未连接，请检查设备！", 1000).show();
				return;
			}

		}
	};

	OnClickListener mGettGpsListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(
					MainActivity.this);
			dialog.setTitle("GPS");
			dialog.setMessage("目前经纬度\n经度：" + m_dwLon + "\n纬度：" + m_dwLat + "");
			dialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			dialog.show();
		}
	};

	private void ReceiveDateFrame(byte[] rcvdata, int length) {

		if (!m_bStartRcv) {
			int npos = 0;
			for (int i = 0; i < length; i++) {
				byte b = rcvdata[i];
				if (b == 0x68) {
					m_bStartRcv = true;
					System.arraycopy(rcvdata, npos, m_rcvBuffer, m_nLenght,
							length - npos);
					m_nLenght += length - npos;
					break;
				}
				npos++;
			}
			if (!m_bStartRcv)
				return;

		} else {
			System.arraycopy(rcvdata, 0, m_rcvBuffer, m_nLenght, length);
			m_nLenght += length;
		}

		int frameLen_head = LEN_START + LEN_DEV + LEN_START + LEN_CTRL
				+ LEN_LEN;
		try {
			if (m_rcvBuffer[m_nLenght - 1] == 0x16
					&& m_rcvBuffer[9] > 0
					&& m_nLenght == (int) m_rcvBuffer[9] + frameLen_head
							+ LEN_CS + LEN_END)// 表示数据结束
			{
				if (m_rcvBuffer[0] != 0x68) {
					tv_rcv_date_id.setText("数据接收有误");
				}

				String strDevID = Integer.toHexString(m_rcvBuffer[LEN_START
						+ LEN_DEV - 1] & 0xFF)
						+ Integer.toHexString(m_rcvBuffer[LEN_START + LEN_DEV
								- 2] & 0xFF)
						+ Integer.toHexString(m_rcvBuffer[LEN_START + LEN_DEV
								- 3] & 0xFF)
						+ Integer.toHexString(m_rcvBuffer[LEN_START + LEN_DEV
								- 4] & 0xFF)
						+ Integer.toHexString(m_rcvBuffer[LEN_START + LEN_DEV
								- 5] & 0xFF)
						+ Integer.toHexString(m_rcvBuffer[LEN_START + LEN_DEV
								- 6] & 0xFF);

				if (m_rcvBuffer[LEN_START + LEN_DEV] != 0x68) {
					tv_rcv_date_id.setText("数据接收有误");
				}

				if (m_rcvBuffer[LEN_START + LEN_DEV + LEN_START] != 0x91) {
					tv_rcv_date_id.setText("数据接收有误");
				}

				int lenData = m_rcvBuffer[frameLen_head - 1];// data len

				int lenRcvData = lenData - 4;
				byte[] rcvValue = new byte[lenRcvData];
				System.arraycopy(m_rcvBuffer, frameLen_head + 4, rcvValue, 0,
						lenRcvData);
				tv_rcv_date_id
						.setText(ParseRcvInfo(rcvValue, m_dataInfo.first));
				byte cs = m_rcvBuffer[m_nLenght - 2];
				int subValue = 0;
				for (int k = 0; k < m_nLenght - 2; k++) {
					subValue += m_rcvBuffer[k];
				}

				byte calDS = (byte) (subValue % 256);
				if (calDS != cs)
					tv_rcv_date_id.setText("数据验证有误");

				m_bStartRcv = false;
				Arrays.fill(m_rcvBuffer, (byte) 0);
				m_nLenght = 0;
			} else {
				// do nothing
			}
		} catch (Exception e) {
			tv_rcv_date_id.setText("接收数据有误");
			m_bStartRcv = false;
			Arrays.fill(m_rcvBuffer, (byte) 0);
			m_nLenght = 0;
		}

	}

	private String ParseRcvInfo(byte[] data, String dataType) {
		byte[] newData = new byte[data.length];
		int j = 0;
		for (int i = 0; i < data.length; i++)
			newData[i] = (byte) (data[i] - 0x33);
		String strValue = "";
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		Date date = calendar.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String dateString = dateFormat.format(date);
		m_dataInfo.second.RESPONSE_DATETIME = dateString;
		
		switch (dataType) {
		case MT_DATA_ZXYGZDN:
			strValue = "当前电量为：";
			float fValue = parsePowerData(newData, newData.length);
			m_dataInfo.second.RESPONSE = Float.toString(fValue);
			
			strValue += Float.toString(fValue);
			strValue += "\r";
			m_dataMap.put("total_power", Float.toString(fValue));
			break;
		case MT_DATA_ZXYGFL1:
			strValue = "费率1电量：";
			float fValue1 = parsePowerData(newData, newData.length);
			m_dataInfo.second.RESPONSE = Float.toString(fValue1);
			strValue += Float.toString(fValue1);
			strValue += "\r";
			break;
		case MT_DATA_ZXYGFL2:
			strValue = "费率2电量：";
			float fValue2 = parsePowerData(newData, newData.length);
			m_dataInfo.second.RESPONSE = Float.toString(fValue2);
			strValue += Float.toString(fValue2);
			strValue += "\r";
			break;
		case MT_DATA_ZXYGFL3:
			strValue = "费率3电量：";
			float fValue3 = parsePowerData(newData, newData.length);
			m_dataInfo.second.RESPONSE = Float.toString(fValue3);
			strValue += Float.toString(fValue3);
			strValue += "\r";
		case MT_DATA_ZXYGFL4:
			strValue = "费率4电量：";
			float fValue4 = parsePowerData(newData, newData.length);
			m_dataInfo.second.RESPONSE = Float.toString(fValue4);
			strValue += Float.toString(fValue4);
			strValue += "\r";
			break;

		case MT_DATA_BNRQ:// 当前表内日期
			// strValue = "星期"+Integer.toHexString(newData[0] & 0xFF);
			strValue = Integer.toHexString(newData[1] & 0xFF);
			strValue = Integer.toHexString(newData[2] & 0xFF) + "-" + strValue;
			strValue = Integer.toHexString(newData[3] & 0xFF) + "-" + strValue;
			m_meterDatetime = strValue;
			m_dataInfo.second.RESPONSE = strValue;
			strValue = "表内日期为：" + strValue;
			strValue += "\r";
			break;
		case MT_DATA_BNSJ:// 当前表内时间
			strValue = Integer.toHexString(newData[0] & 0xFF);
			strValue = Integer.toHexString(newData[1] & 0xFF) + ":" + strValue;
			strValue = Integer.toHexString(newData[2] & 0xFF) + ":" + strValue;
			m_dataInfo.second.RESPONSE = strValue;
			
			m_meterDatetime += " " + strValue;
			m_dataMap.put("data_meter_datetime", m_meterDatetime);
			
			strValue = "表内时间为：" + strValue;
			strValue += "\r";
			break;

		case MT_DATA_1RDJSJ:
			strValue = Integer.toHexString(newData[0] & 0xFF) + "分";
			strValue = Integer.toHexString(newData[1] & 0xFF) + "时" + strValue;
			strValue = Integer.toHexString(newData[2] & 0xFF) + "日" + strValue;
			strValue = Integer.toHexString(newData[3] & 0xFF) + "月" + strValue;
			strValue = Integer.toHexString(newData[4] & 0xFF) + "年" + strValue;
			m_dataInfo.second.RESPONSE = strValue;
			strValue = "冻结时间为：" + strValue;
			break;
		case MT_DATA_1RDJYGDN: {
			strValue = "  正向有功总电能:";
			float fTotalValue = parsePowerData(newData, 4);
			strValue += Float.toString(fTotalValue) + "\n";

			byte[] rcvValue = new byte[4];

			System.arraycopy(newData, 4, rcvValue, 0, 4);
			float fEValue1 = parsePowerData(rcvValue, 4);
			strValue += "正向有功费率1电能:" + Float.toString(fEValue1) + "\n";

			System.arraycopy(newData, 8, rcvValue, 0, 4);
			float fEValue2 = parsePowerData(rcvValue, 4);
			strValue += "正向有功费率2电能:" + Float.toString(fEValue2) + "\n";

			System.arraycopy(newData, 12, rcvValue, 0, 4);
			float fEValue3 = parsePowerData(rcvValue, 4);
			strValue += "正向有功费率3电能:" + Float.toString(fEValue3) + "\n";

			System.arraycopy(newData, 16, rcvValue, 0, 4);
			float fEValue4 = parsePowerData(rcvValue, 4);
			strValue += "正向有功费率4电能:" + Float.toString(fEValue4) + "\n";
			m_dataInfo.second.RESPONSE = Float.toString(fTotalValue) + ";"
					+ Float.toString(fEValue1) + ";" + Float.toString(fEValue2)
					+ ";" + Float.toString(fEValue3) + ";"
					+ Float.toString(fEValue4);
		}
			break;
		case MT_DATA_1RDJSJK: {
			strValue = Integer.toHexString(newData[0] & 0xFF) + "分";
			strValue = Integer.toHexString(newData[1] & 0xFF) + "时" + strValue;
			strValue = Integer.toHexString(newData[2] & 0xFF) + "日" + strValue;
			strValue = Integer.toHexString(newData[3] & 0xFF) + "月" + strValue;
			strValue = Integer.toHexString(newData[4] & 0xFF) + "年" + strValue;
			strValue = "冻结时间为：" + strValue + "\n";
			byte[] rcvValue = new byte[4];
			int elePos = 6;
			System.arraycopy(newData, elePos, rcvValue, 0, 4);
			strValue += "正向有功费率1电能:"
					+ Float.toString(parsePowerData(rcvValue, 4)) + "\n";

			System.arraycopy(newData, elePos + 4, rcvValue, 0, 4);
			float fEValue2 = parsePowerData(rcvValue, 4);
			strValue += "正向有功费率2电能:" + Float.toString(fEValue2) + "\n";

			System.arraycopy(newData, elePos + 8, rcvValue, 0, 4);
			fEValue2 = parsePowerData(rcvValue, 4);
			strValue += "正向有功费率3电能:" + Float.toString(fEValue2) + "\n";

			System.arraycopy(newData, elePos + 12, rcvValue, 0, 4);
			fEValue2 = parsePowerData(rcvValue, 4);
			strValue += "正向有功费率4电能:" + Float.toString(fEValue2) + "\n";

			System.arraycopy(newData, elePos + 16, rcvValue, 0, 4);
			fEValue2 = parsePowerData(rcvValue, 4);
			strValue += "正向有功费率5电能:" + Float.toString(fEValue2) + "\n";

			int rvelePos = elePos + 21;
			System.arraycopy(newData, rvelePos, rcvValue, 0, 4);
			fEValue2 = parsePowerData(rcvValue, 4);
			strValue += "反向有功费率1电能:" + Float.toString(fEValue2) + "\n";

			System.arraycopy(newData, rvelePos + 4, rcvValue, 0, 4);
			fEValue2 = parsePowerData(rcvValue, 4);
			strValue += "反向有功费率2电能:" + Float.toString(fEValue2) + "\n";

			System.arraycopy(newData, rvelePos + 8, rcvValue, 0, 4);
			fEValue2 = parsePowerData(rcvValue, 4);
			strValue += "反向有功费率3电能:" + Float.toString(fEValue2) + "\n";

			System.arraycopy(newData, rvelePos + 12, rcvValue, 0, 4);
			fEValue2 = parsePowerData(rcvValue, 4);
			strValue += "反向有功费率4电能:" + Float.toString(fEValue2) + "\n";

			System.arraycopy(newData, rvelePos + 16, rcvValue, 0, 4);
			fEValue2 = parsePowerData(rcvValue, 4);
			strValue += "反向有功费率5电能:" + Float.toString(fEValue2) + "\n";

			// ////////////////////////////////////////////////
			int yougongPos = rvelePos + 29;
			System.arraycopy(newData, yougongPos, rcvValue, 0, 3);
			strValue += "有功功率1:" + parseStringPowerData(rcvValue, 3, 2) + "\n";

			System.arraycopy(newData, yougongPos + 3, rcvValue, 0, 3);
			strValue += "有功功率2:" + parseStringPowerData(rcvValue, 3, 2) + "\n";

			System.arraycopy(newData, yougongPos + 6, rcvValue, 0, 3);
			strValue += "有功功率3:" + parseStringPowerData(rcvValue, 3, 2) + "\n";

			System.arraycopy(newData, yougongPos + 9, rcvValue, 0, 3);
			strValue += "有功功率4:" + parseStringPowerData(rcvValue, 3, 2) + "\n";

			int wugongPos = yougongPos + 12;
			System.arraycopy(newData, wugongPos, rcvValue, 0, 3);
			strValue += "无功功率1:" + parseStringPowerData(rcvValue, 3, 2) + "\n";

			System.arraycopy(newData, wugongPos + 3, rcvValue, 0, 3);
			strValue += "无功功率2:" + parseStringPowerData(rcvValue, 3, 2) + "\n";

			System.arraycopy(newData, wugongPos + 6, rcvValue, 0, 3);
			strValue += "无功功率3:" + parseStringPowerData(rcvValue, 3, 2) + "\n";

			System.arraycopy(newData, wugongPos + 9, rcvValue, 0, 3);
			strValue += "有功功率4:" + parseStringPowerData(rcvValue, 3, 2) + "\n";

			m_dataInfo.second.RESPONSE = strValue;
		}
			break;
		}
		return (m_strShowData += strValue);
	}

	private float parsePowerData(byte[] data, int dataLen) {
		float fValue = 0.0f;
		fValue += 0.01f * Float.parseFloat(Integer.toHexString(data[0] & 0xFF));

		for (int i = 1; i < dataLen; i++) {
			fValue += Float.parseFloat(Integer.toHexString(data[i] & 0xFF))
					* Math.pow(10, (i - 1) * 2);
		}
		return fValue;
	}

	private String parseStringPowerData(byte[] data, int dataLen, int dotPos) {
		String fValue = "";
		for (int i = 0; i < dotPos; i++) {
			fValue = Integer.toHexString(data[i] & 0xFF) + fValue;
		}
		fValue = "." + fValue;

		for (int i = dotPos; i < dataLen; i++) {
			fValue = Integer.toHexString(data[i] & 0xFF) + fValue;
		}
		return fValue.toUpperCase();
	}

	private byte[] CreateReadDataFrame(String strDevID, byte[] data) {

		int frameLen = LEN_START + LEN_DEV + LEN_START + LEN_CTRL + LEN_LEN
				+ data.length + LEN_CS + LEN_END;
		byte[] frameData = new byte[frameLen];
		frameData[0] = 0x68;// frame start
		if (strDevID.length() % 2 == 1)
			strDevID = "0" + strDevID;

		for (int i = 0; i < 6; i++) // for device id
		{
			try {
				String subStr = strDevID.substring(strDevID.length() - i * 2
						- 2, strDevID.length() - i * 2);
				frameData[i + 1] = (byte) (Integer.parseInt(subStr, 16) & 0xff);
			} catch (IndexOutOfBoundsException ex) {
				frameData[i + 1] = 0x00;
			} catch (Exception e) {
				int ix = 0;
			}
		}

		frameData[LEN_START + LEN_DEV] = 0x68;// another start
		frameData[LEN_START + LEN_DEV + LEN_START] = 0x11;// read data
		frameData[LEN_START + LEN_DEV + LEN_START + LEN_CTRL] = (byte) (data.length & 0xff);
		for (int j = 0; j < data.length; j++) {
			frameData[LEN_START + LEN_DEV + LEN_START + LEN_CTRL + LEN_LEN + j] = (byte) (0x33 + data[data.length
					- j - 1]);
		}
		int frmaelen = LEN_START + LEN_DEV + LEN_START + LEN_CTRL + LEN_LEN
				+ data.length;
		byte subValue = 0;
		for (int k = 0; k < frmaelen; k++) {
			subValue += frameData[k];
		}
		byte cs = (byte) (subValue & 0xff);
		frameData[frmaelen] = cs;
		frameData[frmaelen + 1] = 0x16; // end

		return frameData;
	}

	private OnClickListener deviceDataSelListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_popScrollData.showAsDropDown(tv_devicedata_id);
			}
		};
	}

	private OnClickListener deviceTypeSelListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_popScrollDev.showAsDropDown(tv_devicetype_id);
			}
		};
	}

	private OnClickListener defaultTypeSelListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_popScrollFault.showAsDropDown(tv_fault_type);
			}
		};
	}

	public void UpdateDevTypeInfo() {
		tv_devicetype_id.post(new Runnable() {
			@Override
			public void run() {
				if (m_devInfo == null) {
					tv_devicetype_id.setText("");
					m_devEle = null;
					m_sendData = null;
				} else {
					tv_devicetype_id.setText(m_devInfo.second);
					m_devEle = CreateDeviceElement(m_devInfo.first);
					m_popScrollData.updateDataList(m_devEle.DATA_TYPE_LIST);
					m_popScrollDev.dismiss();
				}
			}

		});

	}

	public void UpdateDataTypeInfo() {
		tv_devicedata_id.post(new Runnable() {
			@Override
			public void run() {
				if (m_dataInfo == null) {
					tv_devicedata_id.setText("");
					m_sendData = null;
				} else {
					tv_devicedata_id.setText(m_dataInfo.second.REQUEST);
					m_sendData = CreateDeviceData(m_dataInfo.first);
					m_popScrollData.dismiss();
				}
			}

		});
	}

	public void UpdateFaultInfo() {
		tv_fault_type.post(new Runnable() {
			@Override
			public void run() {
				if (m_faultInfo == null) {
					tv_fault_type.setText("");
				} else {
					tv_fault_type.setText(m_faultInfo.second);
					m_popScrollFault.dismiss();
				}
			}

		});
	}

	public static String bytesToHexString(byte[] bytes, int length) {
		String result = "";
		if (length == 0)
			length = bytes.length;
		for (int i = 0; i < length; i++) {
			String hexString = Integer.toHexString(bytes[i] & 0xFF);
			if (hexString.length() == 1) {
				hexString = '0' + hexString;
			}
			result += hexString.toUpperCase();
		}
		return result;
	}

	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PrinterClass.MESSAGE_READ:
				if (msg.arg2 == -1) {
					Arrays.fill(m_rcvBuffer, (byte) 0);
					m_nLenght = 0;
					m_bStartRcv = false;
				} else {
					byte[] rcvData = (byte[]) msg.obj;
					Log.i("rev", bytesToHexString(rcvData, msg.arg2));
					ReceiveDateFrame(rcvData, msg.arg2);
				}
				break;
			case PrinterClass.MESSAGE_STATE_CHANGE:// 蓝牙连接状
				switch (msg.arg1) {
				case PrinterClass.STATE_CONNECTED:// 已经连接
					break;
				case PrinterClass.STATE_CONNECTING:// 正在连接
					m_dialog.setMessage(MainActivity.this.getResources()
							.getString(R.string.str_connecting));
					m_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					m_dialog.show();
					break;
				case PrinterClass.STATE_LISTEN:
				case PrinterClass.STATE_NONE:
					break;
				case PrinterClass.SUCCESS_CONNECT:
					m_dialog.dismiss();
					Toast.makeText(
							MainActivity.this,
							MainActivity.this.getResources().getString(
									R.string.str_succonnect), 1000).show();
					if (msg.arg2 == 0) {
						m_bluetoothIcon
								.setImageResource(R.drawable.blueth_conn);
						m_bTConn = true;
						LocalPrefInfo.getInstance(MainActivity.this)
								.setPrefInfo(LocalPrefInfo.BT_ADDRESS,
										msg.obj.toString());
					} else {
						m_bluetoothIcon
								.setImageResource(R.drawable.blueth_normal);
						m_bTConn = false;
						LocalPrefInfo.getInstance(MainActivity.this)
								.setPrefInfo(LocalPrefInfo.BT_ADDRESS, "");
					}
					break;
				case PrinterClass.FAILED_CONNECT:
					m_dialog.dismiss();
					Toast.makeText(
							MainActivity.this,
							MainActivity.this.getResources().getString(
									R.string.str_faileconnect), 1000).show();
					m_bluetoothIcon.setImageResource(R.drawable.blueth_normal);
					m_bTConn = false;
					LocalPrefInfo.getInstance(MainActivity.this).setPrefInfo(
							LocalPrefInfo.BT_ADDRESS, "");
					break;
				case PrinterClass.LOSE_CONNECT:
					Toast.makeText(
							MainActivity.this,
							MainActivity.this.getResources().getString(
									R.string.str_lose), 1000).show();
					m_bluetoothIcon.setImageResource(R.drawable.blueth_normal);
					m_bTConn = false;
					LocalPrefInfo.getInstance(MainActivity.this).setPrefInfo(
							LocalPrefInfo.BT_ADDRESS, "");
				}
				break;
			case PrinterClass.MESSAGE_WRITE:

				break;
			}
			// super.handleMessage(msg);
		}
	};

	private Handler m_scanHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				break;
			case 1:// 扫描完毕
				BTItem d = (BTItem) msg.obj;
				if (d != null) {
					if (!checkData(BTDeviceActivity.s_deviceList, d)) {
						BTDeviceActivity.s_deviceList.add(d);
					}
				}
				break;
			case 2:// 停止扫描
				break;
			}
		}
	};

	private boolean checkData(List<BTItem> list, BTItem d) {
		for (BTItem device : list) {
			if (device.getBluetoothAddress().equalsIgnoreCase(
					d.getBluetoothAddress())) {
				return true;
			}
		}
		return false;
	}


	private OnClickListener BTClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivityForResult(new Intent(MainActivity.this,
					BTDeviceActivity.class), 100);
		}
	};

	class EleDevice {
		public String DEV_NAME;
		public ArrayList<Pair<String, String>> DATA_TYPE_LIST;

		public EleDevice(String name) {
			DEV_NAME = name;
			DATA_TYPE_LIST = new ArrayList<Pair<String, String>>();
		}
	}

	public EleDevice CreateDeviceElement(String TYPE_ID) {
		EleDevice devType = null;
		switch (TYPE_ID) {
		case TYPE_elec_meter:
			devType = new EleDevice("电表");
			devType.DATA_TYPE_LIST.add(new Pair<String, String>(
					MT_DATA_ZXYGZDN, "当前正向有功总电能"));
			 
			devType.DATA_TYPE_LIST.add(new Pair<String, String>(MT_DATA_BNRQ,
					"表内日期"));
			devType.DATA_TYPE_LIST.add(new Pair<String, String>(MT_DATA_BNSJ,
					"表内时间"));
			devType.DATA_TYPE_LIST.add(new Pair<String, String>(MT_DATA_DROPPOWERCOUNT,"掉电总次数"));
			devType.DATA_TYPE_LIST.add(new Pair<String, String>(MT_DATA_DROPPOWERLAST1 ,"（上1次）掉电时间"));
			
			devType.DATA_TYPE_LIST.add(new Pair<String, String>(MT_DATA_OPENCOVERCOUNT,"开表盖次数"));
			
			devType.DATA_TYPE_LIST.add(new Pair<String, String>(MT_DATA_PFLOWBLOCK,"电流数据块"));
			devType.DATA_TYPE_LIST.add(new Pair<String, String>(MT_DATA_PPOWERBLOCK,"瞬时有功功率数据块"));
			
//			devType.DATA_TYPE_LIST.add(new Pair<String,String>(MT_DATA_1RDJSJ, "上1次日冻结时间"));
//			devType.DATA_TYPE_LIST.add(new Pair<String,String>(MT_DATA_1RDJYGDN, "上1次日冻结正向有功电能数据"));
//			devType.DATA_TYPE_LIST.add(new Pair<String, String>(MT_DATA_1RDJSJK, "(上1次)日冻结数据块"));
//			devType.DATA_TYPE_LIST.add(new Pair<String, String>(MT_DATA_ZXYGFL2, "当前正向有功费率2电能"));
//			devType.DATA_TYPE_LIST.add(new Pair<String,String>(MT_DATA_ZXYGFL4, "当前正向有功费率4电能"));
//			devType.DATA_TYPE_LIST.add(new Pair<String,String>(MT_DATA_ZXYGFL1, "当前正向有功费率1电能"));
//			devType.DATA_TYPE_LIST.add(new Pair<String,String>(MT_DATA_ZXYGFL3, "当前正向有功费率3电能"));
			break;
		case TYPE_elec_xx:
			devType = new EleDevice("xx");
			break;
		case TYPE_elec_xxx:
			devType = new EleDevice("xxx");
			break;
		}
		return devType;
	}

	protected byte[] CreateDeviceData(String dataType) {
		switch (dataType) {
		case MT_DATA_ZXYGZDN:
			byte[] value = { 0x00, 0x01, 0x00, 0x00 };
			return value;
		case MT_DATA_ZXYGFL2:
			byte[] value1 = { 0x00, 0x01, 0x02, 0x00 };
			return value1;
		case MT_DATA_ZXYGFL4:
			byte[] value2 = { 0x00, 0x01, 0x04, 0x00 };
			return value2;
		case MT_DATA_BNRQ:
			byte[] value3 = { 0x04, 0x00, 0x01, 0x01 };
			return value3;
		case MT_DATA_BNSJ:
			byte[] value4 = { 0x04, 0x00, 0x01, 0x02 };
			return value4;

		case MT_DATA_1RDJSJ:
			byte[] value5 = { 0x05, 0x06, 0x00, 0x01 };
			return value5;
		case MT_DATA_1RDJYGDN:
			byte[] value6 = { 0x05, 0x06, 0x01, 0x01 };
			return value6;
		case MT_DATA_1RDJSJK:
			byte[] value7 = { 0x05, 0x06, (byte) 0xff, 0x01 };
			return value7;
		}

		return null;
	}

	private long exitTime = 0;
	protected String m_capturePath;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static Bitmap getListBmpObject(int nIndex) {
		if (mObjects != null)
			return mObjects.get(nIndex);
		return null;
	}

	/**
	 * 话题组成员gridadapter
	 * 
	 * @author admin_new
	 * 
	 */
	private class GridPicAdapter extends BaseAdapter {
		private List<Bitmap> objects;
		Context context;

		// private LoadImageAnsyc avatarLoader;

		public GridPicAdapter(Context context, List<Bitmap> objects) {
			this.objects = objects;
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView,
				final ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_pubtjournal_pic, null);
			}
			ImageView iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_pic);
			if (position == getCount() - 1 && !m_bAllreadyFull) {

				// 添加图片图标
				iv_avatar.setImageResource(R.drawable.btn_add_pic_bg);
				convertView.setVisibility(View.VISIBLE);
				iv_avatar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 进入选图片页面
						MainActivity.this.showCamera();
					}
				});
			}

			else { // 普通item，显示图片

				Bitmap bitmap = objects.get(position);
				if (bitmap != null) {
					iv_avatar.setImageBitmap(bitmap);
				}
				iv_avatar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 打开图片显示，并可以删除图片

						Intent intent = new Intent(context,
								ShowBigImageActivity.class);
						intent.putExtra("EXTRA_SHOW_DEL_BUTTON", 1);
						intent.putExtra("EXTRA_IMAGE_TAG", position);
						// startActivity(new Intent(context,
						// ShowBigImageActivity.class));
						startActivityForResult(intent, 103);
					}

				});

			}
			return convertView;
		}

		@Override
		public int getCount() {
			if (m_bAllreadyFull)
				return objects.size();
			else
				return objects.size() + 1;

		}

		@Override
		public Object getItem(int position) {
			return objects.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	protected void showCamera() {
		final AlertDialog dlg = new AlertDialog.Builder(this).create();
		dlg.show();
		Window window = dlg.getWindow();

		// *** 主要就是在这里实现这种效果的.设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
		window.setContentView(R.layout.takephoto_dialog);

		// 为确认按钮添加事件,执行退出应用操作
		TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
		tv_paizhao.setText("拍照");
		tv_paizhao.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SdCardPath")
			public void onClick(View v) {

				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {

					Intent getImageByCamera = new Intent(
							"android.media.action.IMAGE_CAPTURE");
					// String out_file_path =
					// JJLApplication.getInstance().APP_STORE_PATH();
					// File dir = new File(out_file_path);
					// if (!dir.exists()) {
					// dir.mkdirs();
					// }
					// m_capturePath =
					// JJLApplication.getInstance().APP_STORE_PATH() +
					// System.currentTimeMillis() + ".jpg";
					// getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
					// Uri.fromFile(new File(m_capturePath)));
					// getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
					// 1);
					startActivityForResult(getImageByCamera, 101);
					dlg.cancel();
				} else {
					Toast.makeText(getApplicationContext(), "请确认已经插入SD卡",
							Toast.LENGTH_LONG).show();
				}

			}
		});
		// TextView tv_xiangce = (TextView)
		// window.findViewById(R.id.tv_content2);
		// tv_xiangce.setText("相册");
		// tv_xiangce.setOnClickListener(new View.OnClickListener()
		// {
		// public void onClick(View v)
		// {
		// Intent intent = new Intent(Intent.ACTION_PICK, null);
		// intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		// "image/*");
		// startActivityForResult(intent, 102);
		// dlg.cancel();
		// }
		// });
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			case 100: {// ��ʼ����
						// Bundle b=data.getExtras(); //dataΪB�лش���Intent
				// String str=b.getString("bt_address");//str��Ϊ�ش���ֵ
				// if(null==m_client)
				// m_client=new BTClient(BTManage.getInstance().getBtAdapter(),
				// detectedHandler);
				// m_client.connectBTServer(str);

			}
				break;
			case 101: {
				Bitmap image = null;
				Uri uri = data.getData();
				if (uri == null) {
					// use bundle to get data
					Bundle bundle = data.getExtras();
					if (bundle != null) {
						Bitmap photo = (Bitmap) bundle.get("data"); // get
																	// bitmap
						// spath :生成图片取个名字和路径包含类型
						image = photo;
						// saveImage(Bitmap photo, String spath);
					} else {
						Toast.makeText(getApplicationContext(), "err****",Toast.LENGTH_LONG).show();
						return;
					}
				} else {
					// 这个方法是根据Uri获取Bitmap图片的静态方法
					String imagePath = getRealPathFromURI(uri);
					image = BitmapFactory.decodeByteArray(
							ImageUtils.decodeBitmap(imagePath), 0,
							ImageUtils.decodeBitmap(imagePath).length);
				}

				if (image != null) {
					if (mObjects.size() < MAX_UP_IMG_NUM) {
						mObjects.add(image);
					}
					if (mObjects.size() == MAX_UP_IMG_NUM) {
						m_bAllreadyFull = true;
					}
				}

				updateImages();
			}
				break;

			case 102: {
				if (data != null) {
					// 取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意
					Uri mImageCaptureUri = data.getData();
					// 返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取
					if (mImageCaptureUri != null) {
						try {
							// 这个方法是根据Uri获取Bitmap图片的静态方法
							String imagePath = getRealPathFromURI(mImageCaptureUri);
							Bitmap image = BitmapFactory.decodeByteArray(
									ImageUtils.decodeBitmap(imagePath), 0,
									ImageUtils.decodeBitmap(imagePath).length);
							if (image != null) {
								if (mObjects.size() < MAX_UP_IMG_NUM) {
									mObjects.add(image);
								}
								if (mObjects.size() == MAX_UP_IMG_NUM) {
									m_bAllreadyFull = true;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
				updateImages();
			}
				break;
			case 103: {
				int imageTag = data.getIntExtra("EXTRA_IMAGE_TAG", -1);
				if (imageTag != -1) {
					mObjects.remove(imageTag);
					updateImages();
				}
			}
				break;
			case 104: {
				String strPdNo = data.getStringExtra("result");
				if (strPdNo != null) {
					m_deviceNo.setText(strPdNo);

				}
			}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	public void updateImages() {
		mAdapter.notifyDataSetChanged();
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////
	public String getRealPathFromURI(Uri contentUri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null,
				null, null);
		if (cursor.moveToFirst()) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (m_btservice != null)
			m_btservice.disconnect();
		// 注销服务
		unregisterReceiver(m_locReceiver);
		// 结束服务，如果想让服务一直运行就注销此句
		stopService(new Intent(this, LocService.class));
		super.onDestroy();
	}

	class AdoptData {
		public String REQUEST;
		public String RESPONSE;
		public String RESPONSE_DATETIME;

		public AdoptData(String a, String b) {
			REQUEST = a;
			RESPONSE = b;
		}
	}

	private void refreshDeviceList() {
		new AsyncTask<Void, Void, List<DeviceEntry>>() {
			@Override
			protected List<DeviceEntry> doInBackground(Void... params) {
				SystemClock.sleep(1000);
				final List<DeviceEntry> result = new ArrayList<DeviceEntry>();
				for (final UsbDevice device : mUsbManager.getDeviceList()
						.values()) {
					final List<UsbSerialDriver> drivers = UsbSerialProber
							.probeSingleDevice(mUsbManager, device);
					// Log.d(TAG, "Found usb device: " + device);
					if (drivers.isEmpty()) {
						// Log.d(TAG, "  - No UsbSerialDriver available.");
						result.add(new DeviceEntry(device, null));
					} else {
						for (UsbSerialDriver driver : drivers) {
							// Log.d(TAG, "  + " + driver);
							result.add(new DeviceEntry(device, driver));
						}
					}
				}
				return result;
			}

			@Override
			protected void onPostExecute(List<DeviceEntry> result) {
				if (result != null && result.size() > 0) {
					m_deviceEntry = result.get(0);
					if (m_deviceEntry == null)
						return;

				}
			}

		}.execute((Void) null);
	}

	private SerialInputOutputManager mSerialIoManager;
	private final ExecutorService mExecutor = Executors
			.newSingleThreadExecutor();

	private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {

		@Override
		public void onRunError(Exception e) {
			Log.d(TAG, "Runner stopped.");
		}

		@Override
		public void onNewData(final byte[] data) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ReceiveDateFrame(data, data.length);
				}
			});
		}
	};

	private void stopIoManager() {
		if (mSerialIoManager != null) {
			try {
				mSerialIoManager.stop();
				mSerialIoManager = null;
				m_bSPPConn = false;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mtvSpp.setTextColor(getResources().getColor(
								R.color.common_pure_white));
					}
				});
			} catch (Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "关闭串口失败",
								Toast.LENGTH_SHORT).show();
					}
				});

			}
		}
	}

	private void startIoManager() {
		if (m_deviceEntry != null && m_deviceEntry.driver != null) {
			try {
				m_deviceEntry.driver.open();
				m_deviceEntry.driver
						.setParameters(9600, 8, UsbSerialDriver.STOPBITS_1,
								UsbSerialDriver.PARITY_EVEN);
				mSerialIoManager = new SerialInputOutputManager(
						m_deviceEntry.driver, mListener);
				mExecutor.submit(mSerialIoManager);
				// Toast.makeText(getApplicationContext(),m_deviceEntry.device.getDeviceName()+"count"+result.size(),
				// Toast.LENGTH_SHORT).show();
				m_bSPPConn = true;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mtvSpp.setTextColor(getResources().getColor(
								R.color.btn_green_noraml));
					}
				});

			} catch (Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "打开串口失败",
								Toast.LENGTH_SHORT).show();
					}
				});

			}
		}

	}

	private void DeviceStateChange() {
		stopIoManager();
		startIoManager();
	}

	/** Simple container for a UsbDevice and its driver. */
	private static class DeviceEntry {
		public UsbDevice device;
		public UsbSerialDriver driver;

		DeviceEntry(UsbDevice device, UsbSerialDriver driver) {
			this.device = device;
			this.driver = driver;
		}
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
		case R.id.tv_spp_id: 
			{
				if (m_bSPPConn) 
					{
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								MainActivity.this);
						dialog.setTitle("SPP");
						dialog.setMessage("你确定要断开该串口连接吗?");
						dialog.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										stopIoManager();
										dialog.dismiss();
									}
								});
						dialog.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,int which) {	dialog.dismiss();}
								});    
						dialog.show();
					} else 
					{
						DeviceStateChange();
					}
				}	
			break;
			
		case R.id.btn_tongji_id:
			startActivity(new Intent(MainActivity.this,TongjiActivity.class).putExtra("pda_dev_id", m_szImei));
			break;
		case R.id.btn_chaxun_id:
			startActivity(new Intent(MainActivity.this,ChaxunActivity.class).putExtra("pda_dev_id", m_szImei));
			break;
		case R.id.btn_daohang_id:
			startActivity(new Intent(MainActivity.this,DaohangActivity.class));
			break;
		case R.id.btn_bangzhu_id:
			startActivity(new Intent(MainActivity.this,HelpActivity.class));
			break;
		}

	}

}
