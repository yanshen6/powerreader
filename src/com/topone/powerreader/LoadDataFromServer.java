package com.topone.powerreader;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import android.graphics.Bitmap;
import android.widget.Toast;

/**
 * 图片异步加载类
 * 
 * @author Leslie.Fang
 * 
 */
public class LoadDataFromServer
{
	public static final String URL_queryInfo = "http://www.huocheonline.com/Apps/Home/jjl/jjlquerydata.php";
	public static final String URL_queryDoc = "http://www.huocheonline.com/Apps/Home/jjl/jjlquerydoc.php";
	public static final String URL_queryFault = "http://www.huocheonline.com/Apps/Home/jjl/jjlqueryfault.php";

	public static final String URL_savedata = "http://www.huocheonline.com/Apps/Home/jjl/jjlgatherdata.php";
	private String url;
	private Map<String, Object> map = null;
	private List<String> members = new ArrayList<String>();

	public LoadDataFromServer(String url, Map<String, Object> dataMap)
	{
		this.url = url;
		this.map = dataMap;
	}

	public LoadDataFromServer(String url, Map<String, Object> map, List<String> members)
	{
		this.url = url;
		this.map = map;
		this.members = members;
	}



	private String jsonTokener(String in)
	{
		// consume an optional byte order mark (BOM) if it exists
		if (in != null && in.startsWith("\ufeff"))
		{
			in = in.substring(1);
		}
		return in;
	}

	/**
	 * 网路访问调接口
	 * 
	 */
	public interface DataCallBack
	{
		void onDataCallBack(JSONObject data);
	}
	
	public boolean AsyncPostData(final DataCallBack dataCallBack)
	{
		RequestParams params = new RequestParams();

		try{
			
			map.put("app_id", "201618888023");
			map.put("app_token", "jjl_zxcdswddsdfd1223@sd");
			final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
			Set<String> keys = map.keySet();
			if (keys != null)
			{
				Iterator<String> iterator = keys.iterator();
				while (iterator.hasNext())
				{
					String key = (String) iterator.next();
					if (key.equals("files"))
					{

						Map<String ,String> listFile =(Map<String ,String> ) map.get("files");
						Set<String> fileKeys = listFile .keySet();
						for (Object object : fileKeys)
						{
							File file =new File(listFile.get(object));
							params.put(object.toString(), file);
						}					

					}else if(key.equals("data_image_list"))
					{
						
						File dir =new File(JJLApplication.getInstance().APP_STORE_PATH());
						boolean breslt = dir.exists();
				        if(!breslt){    
				        	breslt = dir.mkdirs();
				        	
				        	}
						int i = 0;
						List<Bitmap> listBitmap = (List<Bitmap>)map.get(key);
						String strFiles = "";
						for (Bitmap objBitmap : listBitmap)
						{
							//将压缩后的bitmap保存为图片文件
							String saveImgPath = JJLApplication.getInstance().APP_STORE_PATH()+"img_tmp_"+i+".png";
							File imgFile =new File(saveImgPath);
							FileOutputStream fos = new FileOutputStream(imgFile);
							objBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
							fos.flush();
							fos.close();
							File upLoadImag = new File(saveImgPath);							
							params.put("img_tmp_"+i,upLoadImag,contentType);
							strFiles+=saveImgPath+";";
							i++;
						}
						//map.put("data_files", strFiles);
					}
					else
					{
						String value = (String) map.get(key);
						params.put(key,value);
					}
				}
			}
		}catch(FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.post(url, params,new AsyncHttpResponseHandler()
		{
			
			@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
				{
					JSONObject callbackJsonObject = new JSONObject();
					if (statusCode == 200)
					{
						
						String builder_BOM = jsonTokener(new String(responseBody));
						System.out.println("返回数据是------->>>>>>>>" + builder_BOM);
						//Toast.makeText(MainActivity.this, m_dataInfo.second.RESPONSE_DATETIME, Toast.LENGTH_LONG).show();
						
						try
						{
							callbackJsonObject = JSONObject.parseObject(builder_BOM);							
						} catch (JSONException e)
						{
							e.printStackTrace();
							callbackJsonObject.put("status", 0);
							callbackJsonObject.put("message", "提交数据失败");
						}

					} else
					{

						callbackJsonObject.put("status", 0);
						callbackJsonObject.put("message", "提交数据失败："+ statusCode);
					
					}
					dataCallBack.onDataCallBack(callbackJsonObject);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,byte[] responseBody, Throwable error)
				{
					error.printStackTrace();// 把错误信息打印出轨迹来
					System.out.println("onFailure>" + error.getMessage());
					dataCallBack.onDataCallBack(null);
				}
			});
		
		return true;
	}

	public void AsyncHttpClientGet(String url)
	{
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		
		client.get(url, params, new AsyncHttpResponseHandler()
		{
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,byte[] responseBody)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,byte[] responseBody, Throwable error)
			{
				// TODO Auto-generated method stub
				
			}
		});
	}
	

}
