package com.topone.powerreader;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.topone.powerreader.HelpActivity.StringListAdapter;
import com.topone.powerreader.LoadDataFromServer.DataCallBack;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TongjiActivity extends Activity {

	private TextView tv_num;
	
	private Integer m_allcount = 0;
	private TextView[] list_label = new TextView[8];
	private TextView[] list_tv = new TextView[8];
	private TextView[] list_rate = new TextView[8];
	private RelativeLayout[] list_rl = new RelativeLayout[8];
	
	private void setCellValue(int index ,String strValue,Integer count)
	{
		if(index>7)
			return;
		list_rl[index].setVisibility(View.VISIBLE);
		list_label[index].setText(strValue);
		list_tv[index].setText(count.toString());
		list_rate[index].setText(((Integer)(count*100/m_allcount)).toString()+"%");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tongji);
		
		TextView tv_query_id = (TextView)findViewById(R.id.tv_query_id);
		tv_query_id.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			Refresh();	
			}
		});
		tv_num = (TextView)findViewById(R.id.tv_tag_num);
		list_rl[0] = (RelativeLayout)findViewById(R.id.rl_normal_num);	
		list_label[0] = (TextView)findViewById(R.id.label_normal_num);	
		list_tv[0] = (TextView)findViewById(R.id.tv_normal_num);
		list_rate[0] = (TextView)findViewById(R.id.tv_normal_rate);
		
		list_rl[1] = (RelativeLayout)findViewById(R.id.rl_shigong_id);	
		list_label[1] = (TextView)findViewById(R.id.label_shigong_num);		
		list_tv[1] = (TextView)findViewById(R.id.tv_shigong_num);
		list_rate[1] = (TextView)findViewById(R.id.tv_shigong_rate);
		
		list_rl[2] = (RelativeLayout)findViewById(R.id.rl_biaoji_id);	
		list_label[2] = (TextView)findViewById(R.id.label_biaoji_num);	
		list_tv[2] = (TextView)findViewById(R.id.tv_biaoji_num);
		list_rate[2] = (TextView)findViewById(R.id.tv_biaoji_rate);
		
		list_rl[3] = (RelativeLayout)findViewById(R.id.rl_caijiqi_id);	
		list_label[3] = (TextView)findViewById(R.id.label_caijiqi_num);	
		list_tv[3] = (TextView)findViewById(R.id.tv_caijiqi_num);
		list_rate[3] = (TextView)findViewById(R.id.tv_caijiqi_rate);
		
		list_rl[4] = (RelativeLayout)findViewById(R.id.rl_chaiqian_id);	
		list_label[4] = (TextView)findViewById(R.id.label_chaiqian_num);	
		list_tv[4] = (TextView)findViewById(R.id.tv_chaiqian_num);
		list_rate[4] = (TextView)findViewById(R.id.tv_chaiqian_rate);
		
		list_rl[5] = (RelativeLayout)findViewById(R.id.rl_menbi_id);	
		list_label[5] = (TextView)findViewById(R.id.label_menbi_num);	
		list_tv[5] = (TextView)findViewById(R.id.tv_menbi_num);
		list_rate[5] = (TextView)findViewById(R.id.tv_menbi_rate);
		
		list_rl[6] = (RelativeLayout)findViewById(R.id.rl_gpsrs_id);	
		list_label[6] = (TextView)findViewById(R.id.label_gprs_num);	
		list_tv[6] = (TextView)findViewById(R.id.tv_gprs_num);
		list_rate[6] = (TextView)findViewById(R.id.tv_gprs_rate);
		
		list_rl[7] = (RelativeLayout)findViewById(R.id.rl_notfound_id);	
		list_label[7] = (TextView)findViewById(R.id.label_notfound_num);	
		list_tv[7] = (TextView)findViewById(R.id.tv_notfound_num);
		list_rate[7] = (TextView)findViewById(R.id.label_notfound_rate);
		Refresh();
		
	}
	
	public void back(View view) {	
		
		finish();
	}
	
	public void Refresh()
	{
		for( int i=0;i<8;i++)
			list_rl[i].setVisibility(View.GONE);
		
		Map<String, Object> dataMap = new HashMap<String, Object>();	
		dataMap.put("pda_dev_id", getIntent().getStringExtra("pda_dev_id"));
		LoadDataFromServer task = new LoadDataFromServer(
				LoadDataFromServer.URL_queryFault, dataMap);
		task.AsyncPostData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				if (data == null) {
					Toast.makeText(TongjiActivity.this, "请检查网络",
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
							m_allcount = json.getInteger("all_count");
							tv_num.setText(m_allcount.toString());
							JSONArray recordlist = json.getJSONArray("record");
							int j=1;
							for(int i=0;i<recordlist.size();i++)
							{
								JSONObject cell = recordlist.getJSONObject(i);
								Integer typeNum =cell.getInteger("type_num");
								String faultType =cell.getString("fault_type");
								if(faultType==null ||faultType.trim().length()==0)
								{
									setCellValue(0,"正常",typeNum);
								}else
								{
									setCellValue(j++,faultType,typeNum);
								}
								
							}
						
							
//							tv_num.setText(allNum.toString());
//							
//							Integer nNum = json.getInteger("normal_num");
//							tv_normal_num .setText(nNum.toString());
//							tv_normal_rate .setText(((Integer)(nNum*100/allNum)).toString());
//							
//							nNum = json.getInteger("shigong_num");
//							tv_shigong_num .setText(nNum.toString());
//							tv_shigong_rate.setText(((Integer)(nNum*100/allNum)).toString());
//							
//							nNum = json.getInteger("biaoji_num");
//							tv_biaoji_num.setText(nNum.toString());
//							tv_biaoji_rate .setText(((Integer)(nNum*100/allNum)).toString());
//							
//							nNum = json.getInteger("caijiqi_num");
//							tv_caijiqi_num .setText(nNum.toString());
//							tv_caijiqi_rate .setText(((Integer)(nNum*100/allNum)).toString());
//							
//							nNum = json.getInteger("chaiqian_num");
//							tv_chaiqian_num .setText(nNum.toString());
//							tv_chaiqian_rate .setText(((Integer)(nNum*100/allNum)).toString());
//							
//							nNum = json.getInteger("menbi_num");
//							tv_menbi_num .setText(nNum.toString());
//							tv_menbi_rate.setText(((Integer)(nNum*100/allNum)).toString());
//							
//							nNum = json.getInteger("gprs_num");
//							tv_gprs_num .setText(nNum.toString());
//							tv_gprs_rate .setText(((Integer)(nNum*100/allNum)).toString());
//							
//							nNum = json.getInteger("notfound_num");
//							tv_notfound_num.setText(nNum.toString());
//							label_notfound_rate.setText(((Integer)(nNum*100/allNum)).toString());
						}
						return;

					}
					case 0:// failed
					{
						String strMessage = data.getString("message");
						Toast.makeText(TongjiActivity.this, strMessage,
								Toast.LENGTH_SHORT).show();
						break;
					}
					}

				} catch (Exception e) {
					Toast.makeText(TongjiActivity.this,
							"返回信息出错:" + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
