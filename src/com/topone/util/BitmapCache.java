package com.topone.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapCache  {
    private LruCache<String, Bitmap> mCache;
    private static BitmapCache _inst = new BitmapCache();
    private BitmapCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();   
        int maxSize =maxMemory/8;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }
    public static BitmapCache getInstance()
    {
    	return _inst;
    }
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        
        mCache.put(url, bitmap);
    }
}
