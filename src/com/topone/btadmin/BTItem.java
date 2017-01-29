package com.topone.btadmin;

import android.bluetooth.BluetoothDevice;

public class BTItem {
	private String buletoothName=null;
	private String bluetoothAddress=null;
	private int bluetoothType=-1;
	private BluetoothDevice m_device;
	private int nStatus = 0;
	
	public void setBuletoothDev(BluetoothDevice device)
	{
		m_device = device;
	}
	public String getBuletoothName() {
		return m_device.getName();
	}
	public void setStatus(int status)
	{
		this.nStatus = status;
	}
	public int getStatus()
	{
		return nStatus;
	}
//	public void setBuletoothName(String buletoothName) {
//		this.buletoothName = buletoothName;
//	}
	public String getBluetoothAddress() {
		return m_device.getAddress();
	}
//	public void setBluetoothAddress(String bluetoothAddress) {
//		this.bluetoothAddress = bluetoothAddress;
//	}
	public int getBluetoothType() {
		return m_device.getBondState();
	}
//	public void setBluetoothType(int bluetoothType) {
//		this.bluetoothType = bluetoothType;
//	}
	
}
