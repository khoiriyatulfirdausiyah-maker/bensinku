package com.firda.bensinku;

import android.app.Activity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import org.json.*;
import android.os.Bundle;
import android.webkit.*;
import android.graphics.Color;
import android.net.Uri;
import android.content.*;
import android.provider.MediaStore;
import android.content.ContentValues;
import android.os.Environment;
import android.widget.Toast;
import android.webkit.JavascriptInterface;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private static final int FILE_CHOOSER = 1201;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(245,248,252));
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setAllowFileAccess(true);
        s.setAllowContentAccess(true); s.setMediaPlaybackRequiresUserGesture(false);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> cb, FileChooserParams params){
                if(filePathCallback!=null) filePathCallback.onReceiveValue(null);
                filePathCallback=cb;
                try { startActivityForResult(params.createIntent(), FILE_CHOOSER); return true; }
                catch(Exception e){ filePathCallback=null; return false; }
            }
        });
        webView.addJavascriptInterface(new AndroidBridge(), "Android");
        webView.loadUrl("file:///android_asset/index.html");
        setContentView(webView);
        if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2201);
        }
    }

    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==FILE_CHOOSER && filePathCallback!=null){
            Uri[] result=null;
            if(resultCode==RESULT_OK && data!=null){ Uri u=data.getData(); if(u!=null) result=new Uri[]{u}; }
            filePathCallback.onReceiveValue(result); filePathCallback=null;
        }
    }

    public class AndroidBridge {
        @JavascriptInterface public void saveBackup(String json){
            runOnUiThread(() -> {
                try {
                    String name="BensinKu-backup-"+System.currentTimeMillis()+".json";
                    ContentValues values=new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME,name);
                    values.put(MediaStore.Downloads.MIME_TYPE,"application/json");
                    if(android.os.Build.VERSION.SDK_INT>=29) values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                    Uri uri=getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,values);
                    if(uri==null) throw new Exception("Tidak bisa membuat file");
                    OutputStream os=getContentResolver().openOutputStream(uri);
                    os.write(json.getBytes(StandardCharsets.UTF_8)); os.close();
                    Toast.makeText(MainActivity.this,"Backup disimpan di Downloads",Toast.LENGTH_SHORT).show();
                } catch(Exception e){ Toast.makeText(MainActivity.this,"Gagal menyimpan backup",Toast.LENGTH_SHORT).show(); }
            });
        }

        @JavascriptInterface public void applyNotificationSettings(String json){
            runOnUiThread(() -> {
                try {
                    JSONObject root=new JSONObject(json);
                    JSONObject fuel=root.optJSONObject("fuel");
                    JSONObject price=root.optJSONObject("price");
                    JSONObject monthly=root.optJSONObject("monthly");
                    if(fuel!=null){
                        boolean en=fuel.optBoolean("enabled",false);
                        String tm=fuel.optString("time","18:00");
                        String[] a=tm.split(":"); int h=Integer.parseInt(a[0]), m=Integer.parseInt(a[1]);
                        ReminderScheduler.scheduleDaily(MainActivity.this,"fuel",en,h,m);
                    }
                    if(price!=null){
                        boolean en=price.optBoolean("enabled",false);
                        String tm=price.optString("time","08:00");
                        String[] a=tm.split(":"); int h=Integer.parseInt(a[0]), m=Integer.parseInt(a[1]);
                        if("daily".equals(price.optString("freq","weekly"))) ReminderScheduler.scheduleDaily(MainActivity.this,"price",en,h,m);
                        else ReminderScheduler.scheduleWeekly(MainActivity.this,"price",en,h,m);
                    }
                    if(monthly!=null){
                        boolean en=monthly.optBoolean("enabled",false);
                        String tm=monthly.optString("time","19:00");
                        String[] a=tm.split(":"); int h=Integer.parseInt(a[0]), m=Integer.parseInt(a[1]);
                        int day=monthly.optInt("day",1);
                        ReminderScheduler.scheduleMonthly(MainActivity.this,en,day,h,m);
                    }
                    Toast.makeText(MainActivity.this,"Pengaturan notifikasi diterapkan",Toast.LENGTH_SHORT).show();
                } catch(Exception e){ Toast.makeText(MainActivity.this,"Gagal menerapkan notifikasi",Toast.LENGTH_SHORT).show(); }
            });
        }
    }

    @Override public void onBackPressed() { if (webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
}
