package com.topone.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    private String local_image_path;
    private Context m_context= null;
    public FileUtil(Context context,String local_image_path) {
        this.local_image_path = local_image_path;
        m_context = context;
    }
 
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 保存图片到制定路�?
     * 
     * @param filepath
     * @param bitmap
     */
    public void saveBitmap(String filename, Bitmap bitmap) {
        if (!isExternalStorageWritable()) {
            Log.i(TAG, "SD卡不可用，保存失�?");
            return;
        }

        if (bitmap == null) {
            return;
        }
     
        try {
        	makeRootDirectory(local_image_path);
            File file = new File(local_image_path,filename);
            FileOutputStream outputstream = new FileOutputStream(file);
            if((filename.indexOf("png") != -1)||(filename.indexOf("PNG") != -1))  
            {  
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputstream);
            }  else{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputstream);
            }
            
            outputstream.flush();
            outputstream.close();
            
        } catch (FileNotFoundException e) {
            Log.i(TAG, e.getMessage());
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
        } 
    }


    public  void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }
    /**
     * 返回当前应用 SD 卡的绝对路径 like
     * /storage/sdcard0/Android/data/com.example.test/files
     */
    @SuppressLint("SdCardPath")
    public String getAbsolutePath() {
        File root = new File(local_image_path);
        if(!root.exists()){
            root.mkdirs();
            
        }
         
            
     return local_image_path;
         

    }

    @SuppressLint("SdCardPath")
    public boolean isBitmapExists(String filename) {
        File dir =new File(local_image_path);
         if(!dir.exists()){
             dir.mkdirs();
            }
                //context.getExternalFilesDir(null);
        File file = new File(dir, filename);

        return file.exists();
    }

  public boolean deletePrefixImageFiles(String strPrefix,String excptFile)
  {
	  boolean flag = false;
	  if (!isExternalStorageWritable()) {
          Log.i(TAG, "SD卡不可用，保存失�?");
          return flag;
      }
	  
	  File dir =new File(local_image_path);
      if(!dir.exists())
      {
          return flag;
	  }
	  if (!dir.isDirectory()) 
	  {
		  return flag;
	  }
	  
	  String[] fileList = dir.list();
	  for (int i = 0; i < fileList.length; i++) 
	  {
		  String strFileName = fileList[i];
		  if(excptFile.equalsIgnoreCase(strFileName))
			  continue;
		  
		  /* 取得扩展�? */
		  String strEnd = strFileName.substring(strFileName.lastIndexOf(".") + 1, strFileName.length()).toLowerCase();
			  
		  if(strFileName.startsWith(strPrefix) &&  (strEnd.equals("jpg") || strEnd.equals("gif") || strEnd.equals("png")
				  || strEnd.equals("jpeg") || strEnd.equals("bmp")))
		  {
			  File delFile = new File(local_image_path+strFileName);
			  if (delFile.exists()) 
			  {
				  delFile.delete();
				  
				  //删除缩略�?
				  String []strFileThumb = {local_image_path+strFileName};
				  m_context.getContentResolver().delete(Media.EXTERNAL_CONTENT_URI, Media.DATA + "=?",strFileThumb);
				  
			  }
		  }
	  }
	  return true;
  }
  
  public boolean deleteDirectoryFiles(String fileDirPath)
  {
	  boolean flag = false;
	  if (!isExternalStorageWritable()) {
          Log.i(TAG, "SD卡不可用，删除失�?");
          return flag;
      }
	  
	
	  
	  
	  File dir =new File(fileDirPath);
      if(!dir.exists())
      {
          return flag;
	  }
	  if (!dir.isDirectory()) 
	  {
		  return flag;
	  }
	  
	  
	  String[] fileList = dir.list();
	  for (int i = 0; i < fileList.length; i++) 
	  {
		  String strFileName = fileList[i];
		  
		  /* 取得扩展�? */
		  String strEnd = strFileName.substring(strFileName.lastIndexOf(".") + 1, strFileName.length()).toLowerCase();
			  
		  if(strEnd.equals("jpg") || strEnd.equals("gif") || strEnd.equals("png")
				  || strEnd.equals("jpeg") || strEnd.equals("bmp"))
		  {
			  File delFile = new File(local_image_path+strFileName);
			  if (delFile.exists()) 
			  {
				  if(!delFile.delete())
					  delFile.deleteOnExit();
	
//				  Cursor cursor =m_context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,  
//					        null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
//				  cursor.moveToFirst();  
//				  
//				  Uri mUri = Uri.parse("content://media/external/images/media");   
//				  Uri mImageUri = null;  
//				  
//				  while (!cursor.isAfterLast()) {  
//				      String data = cursor.getString(cursor  
//				              .getColumnIndex(MediaStore.MediaColumns.DATA));  
//
//			          Log.i(TAG, data);
//				      if (strFileName.equals(data)) {  
//				          int ringtoneID = cursor.getInt(cursor  
//				                  .getColumnIndex(MediaStore.MediaColumns._ID));  
//				          mImageUri = Uri.withAppendedPath(mUri, ""  
//				                  + ringtoneID);  
//				          break;  
//				      }  
//				      cursor.moveToNext();  
//				  } 
//				  
				  //删除缩略�?
				  String []strFileThumb ={Environment.getExternalStorageDirectory().getPath()+"/bison/"+strFileName};
				  m_context.getContentResolver().delete(Media.EXTERNAL_CONTENT_URI, Media.DATA + "= ?",strFileThumb);
				  //m_context.getContentResolver().delete(Media.EXTERNAL_CONTENT_URI, Media.DATA + "\""+local_image_path+strFileName+"\"",null);
					  
			  }
		  }else
		  {
			  //删除文件
			  File delFile = new File(local_image_path+strFileName);
			  if(delFile.isFile())
				if(!delFile.delete())
					delFile.deleteOnExit();
		  }
	  }
	  return true;
  }
 
  /**
   * 根据文件后缀名获得对应的MIME类型。
   * @param file
   */ 
  public static String getMIMEType(File file) { 
       
      String type="*/*"; 
      String fName = file.getName(); 
      //获取后缀名前的分隔符"."在fName中的位置。 
      int dotIndex = fName.lastIndexOf("."); 
      if(dotIndex < 0){ 
          return type; 
      } 
      /* 获取文件的后缀名*/ 
      String end=fName.substring(dotIndex,fName.length()).toLowerCase(); 
      if(end=="")return type; 
      //在MIME和文件类型的匹配表中找到对应的MIME类型。 
      for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？ 
          if(end.equals(MIME_MapTable[i][0])) 
              type = MIME_MapTable[i][1]; 
      }        
      return type; 
  } 
  private static final String[][] MIME_MapTable={ 
          //{后缀名，MIME类型} 
          {".3gp",    "video/3gpp"}, 
          {".apk",    "application/vnd.android.package-archive"}, 
          {".asf",    "video/x-ms-asf"}, 
          {".avi",    "video/x-msvideo"}, 
          {".bin",    "application/octet-stream"}, 
          {".bmp",    "image/bmp"}, 
          {".c",  "text/plain"}, 
          {".class",  "application/octet-stream"}, 
          {".conf",   "text/plain"}, 
          {".cpp",    "text/plain"}, 
          {".doc",    "application/msword"}, 
          {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"}, 
          {".xls",    "application/vnd.ms-excel"},  
          {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}, 
          {".exe",    "application/octet-stream"}, 
          {".gif",    "image/gif"}, 
          {".gtar",   "application/x-gtar"}, 
          {".gz", "application/x-gzip"}, 
          {".h",  "text/plain"}, 
          {".htm",    "text/html"}, 
          {".html",   "text/html"}, 
          {".jar",    "application/java-archive"}, 
          {".java",   "text/plain"}, 
          {".jpeg",   "image/jpeg"}, 
          {".jpg",    "image/jpeg"}, 
          {".js", "application/x-javascript"}, 
          {".log",    "text/plain"}, 
          {".m3u",    "audio/x-mpegurl"}, 
          {".m4a",    "audio/mp4a-latm"}, 
          {".m4b",    "audio/mp4a-latm"}, 
          {".m4p",    "audio/mp4a-latm"}, 
          {".m4u",    "video/vnd.mpegurl"}, 
          {".m4v",    "video/x-m4v"},  
          {".mov",    "video/quicktime"}, 
          {".mp2",    "audio/x-mpeg"}, 
          {".mp3",    "audio/x-mpeg"}, 
          {".mp4",    "video/mp4"}, 
          {".mpc",    "application/vnd.mpohun.certificate"},        
          {".mpe",    "video/mpeg"},   
          {".mpeg",   "video/mpeg"},   
          {".mpg",    "video/mpeg"},   
          {".mpg4",   "video/mp4"},    
          {".mpga",   "audio/mpeg"}, 
          {".msg",    "application/vnd.ms-outlook"}, 
          {".ogg",    "audio/ogg"}, 
          {".pdf",    "application/pdf"}, 
          {".png",    "image/png"}, 
          {".pps",    "application/vnd.ms-powerpoint"}, 
          {".ppt",    "application/vnd.ms-powerpoint"}, 
          {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"}, 
          {".prop",   "text/plain"}, 
          {".rc", "text/plain"}, 
          {".rmvb",   "audio/x-pn-realaudio"}, 
          {".rtf",    "application/rtf"}, 
          {".sh", "text/plain"}, 
          {".tar",    "application/x-tar"},    
          {".tgz",    "application/x-compressed"},  
          {".txt",    "text/plain"}, 
          {".wav",    "audio/x-wav"}, 
          {".wma",    "audio/x-ms-wma"}, 
          {".wmv",    "audio/x-ms-wmv"}, 
          {".wps",    "application/vnd.ms-works"}, 
          {".xml",    "text/plain"}, 
          {".z",  "application/x-compress"}, 
          {".zip",    "application/x-zip-compressed"}, 
          {"",        "*/*"}   
      }; 
}
