/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.topone.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.topone.powerreader.JJLApplication;
import com.topone.util.LoadImageAnsyc.ImageDownloadedCallBack;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

public class ImageUtils
{
	// public static String getThumbnailImagePath(String imagePath) {
	// String path = imagePath.substring(0, imagePath.lastIndexOf("/") + 1);
	// path += "th" + imagePath.substring(imagePath.lastIndexOf("/") + 1,
	// imagePath.length());
	// EMLog.d("msg", "original image path:" + imagePath);
	// EMLog.d("msg", "thum image path:" + path);
	// return path;
	// }
	public static void saveBitmap(String strPicName, Bitmap bitmap)
	{

		File file = new File(JJLApplication.getInstance().APP_STORE_PATH(), strPicName);
		if (file.exists())
			file.delete();
		try
		{
			file.createNewFile();
			FileOutputStream outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	
	public static void showUserAvatar(LoadImageAnsyc avatarLoader, ImageView iamgeView, String avatar)
	{
		if(avatar == null ||avatar.length()==0 )
			return;
		
		final String url_avatar =  avatar;
		iamgeView.setTag(url_avatar);
		if (url_avatar != null && !url_avatar.equals(""))
		{
			try
			{
				Bitmap bitmap = avatarLoader.loadImage(iamgeView, url_avatar,5);
				if (bitmap != null)
				{
					iamgeView.setImageBitmap(bitmap);
				}
					
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}
	}

	/*************************************************
	 * 具有回调函数的加载图片的函数
	 * 
	 * @param picLoader
	 * @param iamgeView
	 * @param imageUrl
	 * @param sampleize
	 * @param callback 加载成功好，图片设置给imageview
	 * @return
	 */
	public static Bitmap LoadUrlImageEx(LoadImageAnsyc picLoader, ImageView iamgeView, String imageUrl,int sampleize)
	{
		final String url_image =  imageUrl;
		iamgeView.setTag(url_image);
		if (url_image != null && !url_image.equals(""))
		{
			try
			{
				Bitmap bitmap = picLoader.loadImage(iamgeView, url_image,sampleize);
				if (bitmap != null)
				{
					iamgeView.setImageBitmap(bitmap);
					return bitmap;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		return null;
	}

	
	public static void LoadUrlImage(LoadImageAnsyc picLoader, ImageView iamgeView, String imageUrl,int sampleize)
	{
		final String url_image =  imageUrl;
		iamgeView.setTag(url_image);
		if (url_image != null && !url_image.equals(""))
		{
			try
			{
				Bitmap bitmap = picLoader.loadImage(iamgeView, url_image,sampleize);
				if (bitmap != null)
				{
					iamgeView.setImageBitmap(bitmap);
					
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}
	}
	
	
	   
	public static byte[] decodeBitmap(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;// 设置成了true,不占用内存，只获取bitmap宽高
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, 1024 * 800);
		opts.inJustDecodeBounds = false;// 这里�?定要将其设置回false，因为之前我们将其设置成了true
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inDither = false;
		opts.inPurgeable = true;
		opts.inTempStorage = new byte[16 * 1024];
		FileInputStream is = null;
		Bitmap bmp = null;
		ByteArrayOutputStream baos = null;
		try {
			is = new FileInputStream(path);
			bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
			double scale = getScaling(opts.outWidth * opts.outHeight,
					1024 * 600);
			Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,
					(int) (opts.outWidth * scale),
					(int) (opts.outHeight * scale), true);
			bmp.recycle();
			baos = new ByteArrayOutputStream();
			bmp2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			bmp2.recycle();
			return baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.gc();
		}
		return baos.toByteArray();
	}

	private static double getScaling(int src, int des) {
		/**
		 * 48 目标尺寸÷原尺�? sqrt�?方，得出宽高百分�? 49
		 */
		double scale = Math.sqrt((double) des / (double) src);
		return scale;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	
	//对图像进行线性灰度变�?
	public static Bitmap lineGrey(Bitmap image)   
    {  
        //得到图像的宽度和长度  
        int width = image.getWidth();  
        int height = image.getHeight();  
        //创建线�?�拉升灰度图�?  
        Bitmap linegray = null;  
        linegray = image.copy(Config.ARGB_8888, true);  
        //依次循环对图像的像素进行处理  
        for (int i = 0; i < width; i++) {  
            for (int j = 0; j < height; j++) {  
                //得到每点的像素�??  
                int col = image.getPixel(i, j);  
                int alpha = col & 0xFF000000;  
                int red = (col & 0x00FF0000) >> 16;  
                int green = (col & 0x0000FF00) >> 8;  
                int blue = (col & 0x000000FF);  
                // 增加了图像的亮度  
                red = (int) (1.1 * red + 30);  
                green = (int) (1.1 * green + 30);  
                blue = (int) (1.1 * blue + 30);  
                //对图像像素越界进行处�?  
                if (red >= 255)   
                {  
                    red = 255;  
                }  
  
                if (green >= 255) {  
                    green = 255;  
                }  
  
                if (blue >= 255) {  
                    blue = 255;  
                }  
                // 新的ARGB  
                int newColor = alpha | (red << 16) | (green << 8) | blue;  
                //设置新图像的RGB�?  
                linegray.setPixel(i, j, newColor);  
            }  
        }  
        return linegray;  
    }
	
	//图像灰度化：
	public static Bitmap bitmap2Gray(Bitmap bmSrc) {  
        // 得到图片的长和宽  
        int width = bmSrc.getWidth();  
        int height = bmSrc.getHeight();  
        // 创建目标灰度图像  
        Bitmap bmpGray = null;  
        bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);  
        // 创建画布  
        Canvas c = new Canvas(bmpGray);  
        Paint paint = new Paint();  
        ColorMatrix cm = new ColorMatrix();  
        cm.setSaturation(0);  
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);  
        paint.setColorFilter(f);  
        c.drawBitmap(bmSrc, 0, 0, paint);  
        return bmpGray;  
    }  
}
