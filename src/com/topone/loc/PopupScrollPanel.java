package com.topone.loc;

import java.util.ArrayList;

import com.topone.loc.PopupScrollDevType.SeatInfo;
import com.topone.powerreader.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopupScrollPanel extends PopupWindow
{

	private ScrollDataCallback m_scrollDataCallback = null;
	private Pair<String, String> m_dataInfo = new Pair<String, String>("","");
	private ArrayList<Pair<String, String>> nameList = new ArrayList<Pair<String, String> >();
	private myArrayAdapter adapter;  
	@SuppressWarnings("deprecation")
	public PopupScrollPanel(final Context mContext, View parent)
	{

		View view = View.inflate(mContext,  R.layout.popup_scroll_roomseat, null);
		//view.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
		
			//ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.push_top_in));
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.MATCH_PARENT);
		//setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);

		update();


		ListView lvTypePicer = (ListView)view.findViewById(R.id.lv_string_list);
		adapter = new myArrayAdapter(mContext,R.layout.item_simple_list, nameList);  
		 lvTypePicer.setAdapter(adapter);  
		    //lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);//选择效果  
		  
		    //listView注册�?个元素点击事件监听器  
		 lvTypePicer.setOnItemClickListener(new OnItemClickListener() {  
		        @Override  
		        public void onItemClick(AdapterView<?> parent, View view, int position, long id){  
		        	m_dataInfo =nameList.get(position);
					if(m_scrollDataCallback!=null)
						m_scrollDataCallback.ProcessSelDate(nameList.get(position));
		        }  
		    }); 
	}
	
	public void updateDataList(ArrayList<Pair<String, String>> list)
	{
		nameList.clear();
		nameList.addAll(list);
		adapter.notifyDataSetChanged();
		if(m_scrollDataCallback!=null)
			m_scrollDataCallback.ProcessSelDate(nameList.get(0));
		
	}
	public void setOnDataCallbak(ScrollDataCallback scrollDataCallback)
	{
		m_scrollDataCallback  = scrollDataCallback;
	}

	public interface ScrollDataCallback
	{
		void ProcessSelDate(Pair<String, String> stationInfoDate);
	}
	
	public class DevDataInfo
	{
		public String m_strRoom;
		public String m_strSeat;
		public String m_strType;
		
		public  DevDataInfo(String strRoom,String strSeat,String strtype)
		{
			m_strRoom = strRoom;
			m_strSeat = strSeat;
			m_strType = strtype;
		}
	}
	
	public class myArrayAdapter extends ArrayAdapter
	{
		
		private LayoutInflater layoutInflater;
		private int res;

		public myArrayAdapter(Context context, int resource, ArrayList<Pair<String, String>> nameList)
		{
			super(context, resource, nameList);
			this.res = resource;
			layoutInflater = LayoutInflater.from(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO Auto-generated method stub
			 if (convertView == null) {
		            convertView = layoutInflater.inflate(res, null);
		        }
			TextView tv = (TextView)convertView.findViewById(R.id.tv_item_text_name);
			tv.setText(nameList.get(position).second);
			return convertView;
		}
	}
}
