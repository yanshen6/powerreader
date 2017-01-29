package com.topone.btadmin;
import java.util.ArrayList;  
import java.util.List;  


import com.topone.powerreader.MainActivity;
import com.topone.powerreader.R;
import com.topone.util.LocalPrefInfo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.ImageView;
import android.widget.TextView;  
public class BTDeviceAdapter extends BaseAdapter{  
  
      
    private List<BTItem> mListItem;  
    private Context mcontext=null;  
    private LayoutInflater mInflater=null;
	private int m_selIndex =-1;  
      
    public BTDeviceAdapter(Context context,List<BTItem> listItem){  
        this.mcontext=context;  
        this.mListItem=listItem;  
        this.mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
          
    }  
    public void setCurSelectItem(int npos)
    {
    	m_selIndex = npos;
    }
    
      
    @Override  
    public int getCount() {  
          
        return mListItem.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
          
        return mListItem.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
          
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        ViewHolder holder=null;  
          
        if(convertView==null)
        {  
            holder=new ViewHolder();  
            convertView = mInflater.inflate(R.layout.item_simple_list_1, null);  
            holder.tv=(TextView)convertView.findViewById(R.id.tv_item_text_name); 
            holder.tv_tag =(TextView)convertView.findViewById(R.id.tv_item_text_tag);  
       	 
            convertView.setTag(holder);  
        }else{  
            holder=(ViewHolder)convertView.getTag();  
        }  
        String strValue ="";
        if (mListItem.get(position).getBluetoothType()== BluetoothDevice.BOND_BONDED)
        {        	
        	String strAddress = mListItem.get(position).getBluetoothAddress();
        	
        	String strBluetoothAdd = LocalPrefInfo.getInstance(this.mcontext).getPrefInfo(LocalPrefInfo.BT_ADDRESS);
        	if(strBluetoothAdd.equalsIgnoreCase(strAddress))
        		strValue+="[已连接]";
        	else
        		strValue+="[已配对]";        	
        }
    	if(m_selIndex == position) 
    	{
    		
    		if(MainActivity.m_btservice.getState() == PrinterClass.STATE_CONNECTING)
    		{
    			holder.tv_tag.setVisibility(View.VISIBLE);
    			holder.tv_tag.setText(mcontext.getResources().getString(R.string.str_connecting));
    		}
    			
    		convertView.setBackgroundResource(R.color.common_content_dark_blue);
    	}    		
        strValue += mListItem.get(position).getBuletoothName()+"("+mListItem.get(position).getBluetoothAddress()+")";
        holder.tv.setText(strValue );  
        return convertView;  
    }  
  
      
     class ViewHolder{  
         TextView tv;  
         TextView tv_tag;  
         ImageView ivunchecked;
     }  

}
