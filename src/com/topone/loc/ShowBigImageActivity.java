package com.topone.loc;



import com.topone.powerreader.MainActivity;
import com.topone.powerreader.R;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;


/**
 * 显示缩略图的大图。首先查找本地图片，如果找不到到服务器获取�?? 1.本类用于聊天界面的发图并显示�?
 * 2.经过扩展，本类可以用于显示程序中其他类型的图，与chat依赖库无关�??
 * 
 * @author ysl
 * ?
 */
public class ShowBigImageActivity extends  Activity
{
	private PhotoView m_photoView;
	private Bitmap m_bitmap;
	private boolean isDownloaded;
	private RelativeLayout m_image_bacg;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.activity_show_big_image);
		super.onCreate(savedInstanceState);

		m_photoView = (PhotoView) findViewById(R.id.image);
		
		m_image_bacg =(RelativeLayout)findViewById(R.id.image_bacg);
		boolean bHideBacg = getIntent().getBooleanExtra("EXTRA_HIDE_BLACK_BKGROUND", false);
		if(bHideBacg)
			m_image_bacg.setVisibility(View.GONE);
		
		Bitmap localBmp = null;

		// 是否显示删除按钮
		int nShowDelBtn = getIntent().getExtras().getInt("EXTRA_SHOW_DEL_BUTTON");
		if (nShowDelBtn == 1)
		{
			final int nImageTag = getIntent().getIntExtra("EXTRA_IMAGE_TAG", -1);
			if (nImageTag != -1)
			{
				localBmp = MainActivity.getListBmpObject(nImageTag);
			}

			Button btn_del = (Button) findViewById(R.id.btn_del_id);
			btn_del.setVisibility(View.VISIBLE);
			btn_del.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent();
					intent.putExtra("EXTRA_IMAGE_TAG", nImageTag);
					setResult(RESULT_OK, intent);
					finish();
				}
			});
		}

		// 内存中已有图片，直接显示
		if (localBmp != null)
		{
			m_bitmap = localBmp;
			m_photoView.setImageBitmap(m_bitmap);
		}
		
		m_photoView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}
	
	@Override
	public void onBackPressed()
	{
		if (isDownloaded)
			setResult(RESULT_OK);
		finish();
	}
	@Override
	protected void onDestroy()
	{
//		// TODO Auto-generated method stub
//		if(m_bitmap !=null && !m_bitmap.isRecycled())
//		{
//			m_bitmap.recycle();
//			m_bitmap = null;
//		}
		super.onDestroy();
	}
}
