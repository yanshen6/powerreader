package com.topone.util;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片异步加载�?
 * 
 * @author Leslie.Fang
 * 
 */
public class LoadImageAnsyc {
    // �?大线程数
    private static final int MAX_THREAD_NUM = 5;
    // �?级内存缓存基�? LruCache
    private BitmapCache bitmapCache;
    // 二级文件缓存
    private FileUtil fileUtil;
    
 
    // 线程�?
    private ExecutorService threadPools = null;

    public LoadImageAnsyc(Context context, String local_image_path) {
        bitmapCache = BitmapCache.getInstance();
        fileUtil = new FileUtil(context, local_image_path);
        threadPools = Executors.newFixedThreadPool(MAX_THREAD_NUM); 
    }
    

    /*
     * If set to a nSampSize > 1, requests the decoder to subsample the original image, returning a smaller image to save memory.
     *  The sample size is the number of pixels in either dimension that correspond to a single pixel in the decoded bitmap. 
     *  For example, inSampleSize == 4 returns an image that is 1/4 the width/height of the original, and 1/16 the number 
     *  of pixels. Any value <= 1 is treated the same as 1. Note: the decoder uses a final value based on powers of 2, 
     *  any other value will be rounded down to the nearest power of 2.
     */
    @SuppressLint("HandlerLeak")
    public Bitmap loadImage(final ImageView imageView, final String imageUrl,final int nSampSize) 
    {
    	
        final String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        final String filepath = fileUtil.getAbsolutePath() + filename;

        // 先从内存中拿
        Bitmap bitmap = bitmapCache.getBitmap(imageUrl);

        if (bitmap != null) {
            Log.i("loadImage", "image exists in memory");
            return bitmap;
        }

        // 从文件中�?
        if (fileUtil.isBitmapExists(filename)) 
        {
            Log.i("loadImage", "image exists in file" + filename);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            bitmap = BitmapFactory.decodeFile(filepath,options);
            // 先缓存到内存
            //bitmapCache.putBitmap(imageUrl, bitmap);
            return bitmap;
        }

    
        return null;
    }

 
    /**
     * 图片下载完成回调接口
     * 
     */
    public interface ImageDownloadedCallBack {
        void onImageDownloaded(ImageView imageView, Bitmap bitmap);
    }

   
 
}
