package com.topone.powerreader;
import java.util.List;  

import com.topone.powerreader.R;

import android.content.Context;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.widget.BaseAdapter;  
import android.widget.RelativeLayout;
import android.widget.TextView;  
public class RecordListAdapter extends BaseAdapter{  
      
    private List<JJLChaxunRecordItem> mListItem;  
    private LayoutInflater mInflater=null;
      
    public RecordListAdapter(Context context,List<JJLChaxunRecordItem> listItem){  
        this.mListItem=listItem;  
        this.mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
          
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
            convertView = mInflater.inflate(R.layout.item_chaxun_record_list, null);  
            holder.rl_item_homeno=(RelativeLayout)convertView.findViewById(R.id.rl_item_homeno); 
            holder.lable_item_homeno =(TextView)convertView.findViewById(R.id.lable_item_homeno);  
            holder.tv_item_homeno =(TextView)convertView.findViewById(R.id.tv_item_homeno);  
            
            holder.rl_item_homename=(RelativeLayout)convertView.findViewById(R.id.rl_item_homename); 
            holder.lable_item_homename =(TextView)convertView.findViewById(R.id.lable_item_homename);  
            holder.tv_item_homename =(TextView)convertView.findViewById(R.id.tv_item_homename);  
            
            holder.rl_item_pdno=(RelativeLayout)convertView.findViewById(R.id.rl_item_pdno); 
            holder.lable_item_pdno=(TextView)convertView.findViewById(R.id.lable_item_pdno);  
            holder.tv_item_pdno =(TextView)convertView.findViewById(R.id.tv_item_pdno);  
            
            
            holder.rl_item_meterno=(RelativeLayout)convertView.findViewById(R.id.rl_item_meterno); 
            holder.lable_item_meterno =(TextView)convertView.findViewById(R.id.lable_item_meterno);  
            holder.tv_item_meterno =(TextView)convertView.findViewById(R.id.tv_item_meterno);  
            
            holder.rl_item_meterpoint=(RelativeLayout)convertView.findViewById(R.id.rl_item_meterpoint); 
            holder.lable_item_meterpoint =(TextView)convertView.findViewById(R.id.lable_item_meterpoint);  
            holder.tv_item_meterpoint =(TextView)convertView.findViewById(R.id.tv_item_meterpoint);  
            
            holder.rl_item_meteraddress=(RelativeLayout)convertView.findViewById(R.id.rl_item_meteraddress); 
            holder.lable_item_meteraddress =(TextView)convertView.findViewById(R.id.lable_item_meteraddress);  
            holder.tv_item_meteraddress =(TextView)convertView.findViewById(R.id.tv_item_meteraddress);  
            
            holder.rl_item_fixaddress=(RelativeLayout)convertView.findViewById(R.id.rl_item_fixaddress); 
            holder.lable_item_fixaddress=(TextView)convertView.findViewById(R.id.lable_item_fixaddress);  
            holder.tv_item_fixaddress=(TextView)convertView.findViewById(R.id.tv_item_fixaddress);  
            
            holder.rl_item_poweraddress=(RelativeLayout)convertView.findViewById(R.id.rl_item_poweraddress); 
            holder.lable_item_poweraddress =(TextView)convertView.findViewById(R.id.lable_item_poweraddress);  
            holder.tv_item_poweraddress =(TextView)convertView.findViewById(R.id.tv_item_poweraddress);  
            
            holder.rl_item_fault_reason=(RelativeLayout)convertView.findViewById(R.id.rl_item_fault_reason); 
            holder.lable_item_fault_reason =(TextView)convertView.findViewById(R.id.lable_item_fault_reason);  
            holder.tv_item_fault_reason =(TextView)convertView.findViewById(R.id.tv_item_fault_reason);  
            
            holder.rl_item_fault_type=(RelativeLayout)convertView.findViewById(R.id.rl_item_fault_type); 
            holder.lable_item_fault_type =(TextView)convertView.findViewById(R.id.lable_item_fault_type);  
            holder.tv_item_fault_type =(TextView)convertView.findViewById(R.id.tv_item_fault_type);  
            
            holder.rl_item_check_user=(RelativeLayout)convertView.findViewById(R.id.rl_item_check_user); 
            holder.lable_item_check_user =(TextView)convertView.findViewById(R.id.lable_item_check_user);  
            holder.tv_item_check_user =(TextView)convertView.findViewById(R.id.tv_item_check_user);  
            
            holder.rl_item_check_time=(RelativeLayout)convertView.findViewById(R.id.rl_item_check_time); 
            holder.lable_item_check_time =(TextView)convertView.findViewById(R.id.lable_item_check_time);  
            holder.tv_item_check_time =(TextView)convertView.findViewById(R.id.tv_item_check_time);  
            
            convertView.setTag(holder);  
        }else{  
            holder=(ViewHolder)convertView.getTag();  
        }  
        
        JJLChaxunRecordItem item = (JJLChaxunRecordItem) getItem(position);
        if(item.data_homeno!=null && item.data_homeno.trim().length()>0)
        {
        	holder.rl_item_homeno.setVisibility(View.VISIBLE);
        	holder.tv_item_homeno.setText(item.data_homeno);
        }
        
        if(item.data_homename!=null && item.data_homename.trim().length()>0)
        {
        	holder.rl_item_homename.setVisibility(View.VISIBLE);
        	holder.tv_item_homename.setText(item.data_homename);
        }
        
        if(item.data_checkTime!=null && item.data_checkTime.trim().length()>0)
        {
        	holder.rl_item_check_time.setVisibility(View.VISIBLE);
        	holder.tv_item_check_time.setText(item.data_checkTime);
        }
        if(item.data_checkuser!=null && item.data_checkuser.trim().length()>0)
        {
        	holder.rl_item_check_user.setVisibility(View.VISIBLE);
        	holder.tv_item_check_user.setText(item.data_checkuser);
        }

        if(item.data_faultReason!=null && item.data_faultReason.trim().length()>0)
        {
        	holder.rl_item_fault_reason.setVisibility(View.VISIBLE);
        	holder.tv_item_fault_reason.setText(item.data_faultReason);
        }
        if(item.data_faultType!=null && item.data_faultType.trim().length()>0)
        {
        	holder.rl_item_fault_type.setVisibility(View.VISIBLE);
        	holder.tv_item_fault_type.setText(item.data_faultType);
        }
        if(item.data_fixaddress!=null && item.data_fixaddress.trim().length()>0)
        {
        	holder.rl_item_fixaddress.setVisibility(View.VISIBLE);
        	holder.tv_item_fixaddress.setText(item.data_fixaddress);
        }
        if(item.data_meteraddress!=null && item.data_meteraddress.trim().length()>0)
        {
        	holder.rl_item_meteraddress.setVisibility(View.VISIBLE);
        	holder.tv_item_meteraddress.setText(item.data_meteraddress);
        }
        
        if(item.data_meterno!=null && item.data_meterno.trim().length()>0)
        {
        	holder.rl_item_meterno.setVisibility(View.VISIBLE);
        	holder.tv_item_meterno.setText(item.data_meterno);
        }
        if(item.data_pdno!=null && item.data_pdno.trim().length()>0)
        {
        	holder.rl_item_pdno.setVisibility(View.VISIBLE);
        	holder.tv_item_pdno.setText(item.data_pdno);
        }
        if(item.data_meterpoint!=null && item.data_meterpoint.trim().length()>0)
        {
        	holder.rl_item_meterpoint.setVisibility(View.VISIBLE);
        	holder.tv_item_meterpoint.setText(item.data_meterpoint);
        }
        if(item.data_poweraddress!=null && item.data_poweraddress.trim().length()>0)
        {
        	holder.rl_item_poweraddress.setVisibility(View.VISIBLE);
        	holder.tv_item_poweraddress.setText(item.data_poweraddress);
        }
        return convertView;  
    }  
  
      
     class ViewHolder{  
    	 //户号
    	 RelativeLayout rl_item_homeno;    	 
         TextView lable_item_homeno;  
         TextView tv_item_homeno;
         //户名
    	 RelativeLayout rl_item_homename;    	 
         TextView lable_item_homename;  
         TextView tv_item_homename;
         //终端号
    	 RelativeLayout rl_item_meterno;    	 
         TextView lable_item_meterno;  
         TextView tv_item_meterno;
         
         //表号
    	 RelativeLayout rl_item_pdno;    	 
         TextView lable_item_pdno;  
         TextView tv_item_pdno;
         //测量点
    	 RelativeLayout rl_item_meterpoint;    	 
         TextView lable_item_meterpoint;  
         TextView tv_item_meterpoint;
         //终端地址
    	 RelativeLayout rl_item_meteraddress;    	 
         TextView lable_item_meteraddress;  
         TextView tv_item_meteraddress;
         //安装地址
    	 RelativeLayout rl_item_fixaddress;    	 
         TextView lable_item_fixaddress;  
         TextView tv_item_fixaddress;
         //用电地址
    	 RelativeLayout rl_item_poweraddress;    	 
         TextView lable_item_poweraddress;  
         TextView tv_item_poweraddress;
         //故障原因
    	 RelativeLayout rl_item_fault_reason;    	 
         TextView lable_item_fault_reason;  
         TextView tv_item_fault_reason;
         //故障分类
    	 RelativeLayout rl_item_fault_type;    	 
         TextView lable_item_fault_type;  
         TextView tv_item_fault_type;
         //排查人
    	 RelativeLayout rl_item_check_user;    	 
         TextView lable_item_check_user;  
         TextView tv_item_check_user;
         //排查时间
    	 RelativeLayout rl_item_check_time;    	 
         TextView lable_item_check_time;  
         TextView tv_item_check_time;
     }  

}
