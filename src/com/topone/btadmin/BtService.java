package com.topone.btadmin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.topone.btadmin.BlueToothService.OnReceiveDataHandleEvent;
import com.topone.powerreader.MainActivity;
import com.topone.powerreader.R;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class BtService implements PrinterClass {

	Context context;
	Handler mhandler, handler;
	public static BlueToothService mBTService = null;

	public BtService(Context _context, Handler _mhandler, Handler _handler) {
		context = _context;
		mhandler = _mhandler;
		handler = _handler;
		mBTService = new BlueToothService(context, mhandler);

		mBTService.setOnReceive(new OnReceiveDataHandleEvent() {
			@Override
			public void OnReceive(final BluetoothDevice device) {
				// TODO Auto-generated method stub
				if (device != null) {					
					Message msg = new Message();
					msg.what = 1;
					msg.obj = device;
					handler.sendMessage(msg);
					setState(STATE_SCANING);
				} else {
					Message msg = new Message();
					msg.what = 8;
					handler.sendMessage(msg);
				}
			}
		});
	}

	@Override
	public boolean open(Context context) {
		// TODO Auto-generated method stub
		mBTService.OpenDevice();
		return true;
	}

	@Override
	public boolean close(Context context) {
		// TODO Auto-generated method stub
		mBTService.CloseDevice();
		return false;
	}

	@Override
	public void scan() {
		// TODO Auto-generated method stub
		if (!mBTService.IsOpen()) {// �ж������Ƿ��
			mBTService.OpenDevice();
			return;
		}
		if (mBTService.getState() == STATE_SCANING)
			return;

		new Thread() {
			public void run() {
				mBTService.ScanDevice();
			}
		}.start();
	}

	@Override
	public boolean connect(String device) {
		// TODO Auto-generated method stub
		if (mBTService.getState() == STATE_SCANING) {
			stopScan();
		}
		if (mBTService.getState() == STATE_CONNECTING) {
			return false;
		}
		if(mBTService.getState() == STATE_CONNECTED)
		{
			mBTService.DisConnected();
		}
		mBTService.ConnectToDevice(device);// ��������
		return true;
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		mBTService.DisConnected();
		return true;
	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return mBTService.getState();
	}

	@Override
	public boolean write(byte[] bt) {
		if(getState()!= PrinterClass.STATE_CONNECTED)
		{
			Toast toast = Toast.makeText(context, context.getResources().getString(R.string.str_lose), Toast.LENGTH_SHORT);
	        toast.show();
			return false;
		}
		mBTService.write(bt);
		return true;
	}

	@Override
	public boolean IsOpen() {
		// TODO Auto-generated method stub
		return mBTService.IsOpen();
	}

	@Override
	public void stopScan() {
		// TODO Auto-generated method stub
		if (MainActivity.m_btservice.getState() == PrinterClass.STATE_SCANING) {
			mBTService.StopScan();
			mBTService.setState(PrinterClass.STATE_SCAN_STOP);
		}
	}

	@Override
	public void setState(int state) {
		// TODO Auto-generated method stub
		mBTService.setState(state);
	}

	@Override
	public List<BTItem> getDeviceList() {
		List<BTItem> devList = new ArrayList<BTItem>();
		// TODO Auto-generated method stub
		Set<BluetoothDevice> devices = mBTService.GetBondedDevice();
		for (BluetoothDevice bluetoothDevice : devices) {
			BTItem d = new BTItem();
			d.setBuletoothDev(bluetoothDevice);			
			devList.add(d);
		}
		return devList;
	}
	public void abortOprating()
	{
		if (mBTService.getState() == STATE_SCANING) {
			stopScan();
		}
		if (mBTService.getState() == STATE_CONNECTING) {
			disconnect();
		} ;
	}

}
