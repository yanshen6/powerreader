package com.topone.powerreader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.topone.powerreader.LoadDataFromServer.DataCallBack;
import com.topone.util.FileUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class HelpActivity extends Activity {
	private ProgressDialog m_dialog;
	private ListView m_listView;
	private List<String> m_datalist = new ArrayList<String>();
	private Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case 0:  
                System.out.println("jason.com");  
                break;  
            }  
        };  
    };  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_dialog = new ProgressDialog(HelpActivity.this);
		//if (GetVersion.GetSystemVersion() > 2.3) 
		{
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());

			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
					.build());
		}
		
		setContentView(R.layout.activity_help);
		m_listView = (ListView) findViewById(R.id.lv_record_list);
		StringListAdapter adpter = new StringListAdapter(HelpActivity.this,
				m_datalist);
		m_listView.setAdapter(adpter);
		m_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String fileName = m_datalist.get(position);
				File dir = new File(JJLApplication.getInstance().APP_STORE_PATH());
				if (!dir.exists())
					dir.mkdir();
				
				final File file = new File(dir, fileName);
				if (file.exists())
					openFile(file);
				else
				{
					 String urlFile = "http://www.huocheonline.com/UploadFile/document/"+URLEncoder.encode(fileName);
					 final String filePath = urlFile;
					 new Thread() {  
				            @Override  
				            public void run() {  
				                // TODO Auto-generated method stub  
				                super.run();  
				                try {
				                	
									downLoad(filePath,file);
									
										//
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				                Message msg = handler.obtainMessage();  
				                msg.what = 0;  
				                handler.sendMessage(msg);  
				            }  
				        }.start();  
				        
					
				}
			}
		});
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("pda_id", getIntent().getStringExtra("pda_id"));
		LoadDataFromServer task = new LoadDataFromServer(LoadDataFromServer.URL_queryDoc, dataMap);
		task.AsyncPostData(new DataCallBack() {
			@Override
			public void onDataCallBack(JSONObject data) {
				if (data == null) {
					Toast.makeText(HelpActivity.this, "请检查网络",
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
							JSONArray listDoc = json.getJSONArray("query_doc");
							if (listDoc == null)
								return;
							m_datalist.clear();
							for (int i = 0; i < listDoc.size(); i++) {
								if (null != listDoc.getString(i))
									m_datalist.add(listDoc.getString(i));
							}
							StringListAdapter adpter = new StringListAdapter(
									HelpActivity.this, m_datalist);
							m_listView.setAdapter(adpter);
						}
						return;

					}
					case 0:// failed
					{
						String strMessage = data.getString("message");
						Toast.makeText(HelpActivity.this, strMessage,
								Toast.LENGTH_SHORT).show();
						break;
					}
					}

				} catch (Exception e) {
					Toast.makeText(HelpActivity.this,"返回信息出错:" + e.getMessage(), Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

	}

	public void back(View view) {

		finish();
	}

	public class StringListAdapter extends BaseAdapter {

		private List<String> mListItem;
		private Context mcontext = null;
		private LayoutInflater mInflater = null;
		private int m_selIndex = -1;

		public StringListAdapter(Context context, List<String> listItem) {
			this.mcontext = context;
			this.mListItem = listItem;
			this.mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public void setCurSelectItem(int npos) {
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
			ViewHolder holder = null;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_power_record_list, null);
				holder.tv_datetime_id = (TextView) convertView
						.findViewById(R.id.tv_item_date);
				holder.tv_data_value = (TextView) convertView
						.findViewById(R.id.tv_item_data);
				holder.tv_data_value.setVisibility(View.GONE);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			String item = (String) getItem(position);
			holder.tv_datetime_id.setText(item);
			return convertView;
		}

		class ViewHolder {
			TextView tv_datetime_id;
			TextView tv_data_value;
		}

	}
	
	  
	  /**
	   * 打开文件
	   * @param file
	   */ 
	  private void openFile(File file){ 
	       
	      Intent intent = new Intent(); 
	      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	      //设置intent的Action属性 
	      intent.setAction(Intent.ACTION_VIEW); 
	      //获取文件file的MIME类型 
	      String type = FileUtil.getMIMEType(file); 
	      //设置intent的data和Type属性。 
	      intent.setDataAndType(/*uri*/Uri.fromFile(file), type); 
	      //跳转 
	      try{
	      startActivity(intent);   
	      }catch(Exception ex)
	      {
	    	  
	      }
	  }
	  
	  
	  
	  public long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
	        //这样的下载代码很多，我就不做过多的说明
	        int downloadCount = 0;
	        int currentSize = 0;
	        long totalSize = 0;
	        int updateTotalSize = 0;
	         
	        HttpURLConnection httpConnection = null;
	        InputStream is = null;
	        RandomAccessFile asf = null;
	        FileOutputStream fos = null;
	        int nLength=-1;
	        
	        try {
	            URL url = new URL(downloadUrl);
	            httpConnection = (HttpURLConnection)url.openConnection();
	            httpConnection.setRequestMethod("GET");
	            
//	            httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
//	            if(currentSize > 0) {
//	                httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
//	            }
	            httpConnection.setConnectTimeout(10000);
	            httpConnection.setReadTimeout(20000);
	            updateTotalSize = httpConnection.getContentLength();
	            if (httpConnection.getResponseCode() == 404) {
	                throw new Exception("fail!");
	            }
	            is = httpConnection.getInputStream();                  
	            fos = new FileOutputStream(saveFile, false);
	            byte buffer[] = new byte[4096];
	            int readsize = 0;
	            while((readsize = is.read(buffer)) > 0){
	                fos.write(buffer, 0, readsize);
	                totalSize += readsize;
	                //为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
	                if((downloadCount == 0)||(int) (totalSize*100/updateTotalSize)-10>downloadCount){
	                    downloadCount += 10;	                  
	                }                       
	            }
	        } finally {
	            if(httpConnection != null) {
	                httpConnection.disconnect();
	            }
	            if(is != null) {
	                is.close();
	            }
	            if(fos != null) {
	                fos.close();
	            }
	        }
	        return totalSize;
	    }
	  
	  /**
	     * 从服务器下载文件
	     * @param path 下载文件的地址
	     * @param FileName 文件名字
	     */
	    public void downLoad(final String path, final File saveFile) {
	    	m_dialog.setMessage(HelpActivity.this.getResources().getString(R.string.str_start_synic_file));
			m_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			m_dialog.setCancelable(false);// 按到对话框以外的地方不起作用，相当于模态对话框。按返回键也不起作用。
											// setCanceledOnTouchOutside(false);调用这个方法时，按对话框以外的地方不起作用。按返回键还起作用
			m_dialog.show();
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    URL url = new URL(path);
	                    
	                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	                    con.setReadTimeout(5000);
	                    con.setConnectTimeout(5000);
	                    con.setRequestProperty("Charset", "UTF-8");
	                    con.setRequestMethod("GET");
	                    if (con.getResponseCode() == 200) {
	                        InputStream is = con.getInputStream();//获取输入流
	                        FileOutputStream fileOutputStream = null;//文件输出流
	                        if (is != null) {
	                            fileOutputStream = new FileOutputStream(saveFile);//指定文件保存路径，代码看下一步
	                            byte[] buf = new byte[1024];
	                            int ch;
	                            while ((ch = is.read(buf)) != -1) {
	                                fileOutputStream.write(buf, 0, ch);//将获取到的流写入文件中
	                            }
	                           
	                        }
	                        if (fileOutputStream != null) {
	                            fileOutputStream.flush();
	                            fileOutputStream.close();
	                            m_dialog.dismiss();	                			
	                            openFile(saveFile);
	                        }else
	                        {
	                        	m_dialog.dismiss();
	                        	Toast.makeText(HelpActivity.this, "下载失败", 
	        							Toast.LENGTH_SHORT).show();
	                        }
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                    m_dialog.dismiss();
                    	Toast.makeText(HelpActivity.this, "下载失败", 
    							Toast.LENGTH_SHORT).show();
	                }
	            }
	        }).start();
	    }
}
