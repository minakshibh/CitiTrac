package com.qrpatrol.patrol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Display;
import android.webkit.WebView;
import android.widget.ImageView;

import com.qrpatrol.android.R;
import com.qrpatrol.util.Util;
 
public class ImageLoader {
     
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService; 
    private Context mCtx;
    
    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
        mCtx = context;
    }
     
//    final int stub_id=R.drawable.ic_launcher;
    public void DisplayImage(String url, ImageView imageView, String trigger, WebView webView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
        	if(trigger.equals("webview")){
        		 String html="<html><body><img src='{IMAGE_URL}' /></body></html>";
        		 // Convert bitmap to Base64 encoded image for web
        		    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        		    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        		    byte[] byteArray = byteArrayOutputStream.toByteArray();
        		    String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        		    String image = "data:image/png;base64," + imgageBase64;

        		    // Use image for the img src parameter in your html and load to webview
        		    html = html.replace("{IMAGE_URL}", image);
        		    
        		    
        		    webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", "");
        	}else
        		imageView.setImageBitmap(bitmap);
        }else
        {
            queuePhoto(url, imageView, trigger, webView);
            imageView.setImageResource(R.drawable.ic_launcher);
        }
    }
         
    private void queuePhoto(String url, ImageView imageView, String trigger, WebView webView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView, trigger, webView);
        executorService.submit(new PhotosLoader(p));
    }
     
    private Bitmap getBitmap(String url) 
    {
        File f=fileCache.getFile(url);
         
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
         
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Util.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }
 
    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
             
            //Find the correct scale value. It should be the power of 2.
            Display display = ((Activity) mCtx).getWindowManager().getDefaultDisplay();
			int width=display.getWidth();
			
            final int REQUIRED_SIZE=width;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
           /* while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }*/
             
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
     
    //Task for the queue
    private class PhotoToLoad
    {
        public String url, trigger;
        public ImageView imageView;
        public WebView webView;

        public PhotoToLoad(String u, ImageView i, String t, WebView w){
            url = u; 
            imageView =i;
            trigger = t;
            webView = w;
        }
    }
     
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
         
        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap bmp=getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            Activity a=(Activity)photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }
     
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
     
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p)
        {
        	bitmap=b;
        	photoToLoad=p;
        }
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
            	if(photoToLoad.trigger.equals("webview")){
            	 String html="<html><body><img src='{IMAGE_URL}' /></body></html>";
            	    // Convert bitmap to Base64 encoded image for web
            	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            	    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            	    byte[] byteArray = byteArrayOutputStream.toByteArray();
            	    String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            	    String image = "data:image/png;base64," + imgageBase64;

            	    // Use image for the img src parameter in your html and load to webview
            	    html = html.replace("{IMAGE_URL}", image);
            	    photoToLoad.webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", "");
            	}else
            		photoToLoad.imageView.setImageBitmap(bitmap);
            }
//            else
//                photoToLoad.imageView.setImageResource(stub_id);
        }
    }
 
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
 
}