package com.topone.loc;

import com.topone.btadmin.BTItem;



public interface StatusBlueTooth  {
	final static int SEARCH_START=110;
	final static int SEARCH_END=112;
	final static int START_BOUND=113;
	final static int RUNNING_BOUND=114;
	final static int FINISH_BOUND=115;
	final static int CANCEL_BOUND = 116;
	
	final static int serverCreateSuccess=211;
	final static int serverCreateFail=212;
	final static int clientCreateSuccess=221;
	final static int clientCreateFail=222;
	final static int connectLose=231;
	
	void BTDeviceSearchStatus(int resultCode);
	void BTSearchFindItem(BTItem item);
	void BTConnectStatus(int result);
}
