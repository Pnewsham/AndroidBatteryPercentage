package com.example.androidbatterypercentage;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView batteryPercent;
	
	 private void getBatteryPercentage() {
	 BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
         public void onReceive(Context context, Intent intent) {
             context.unregisterReceiver(this);
             int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
             int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
             int level = -1;
             if (currentLevel >= 0 && scale > 0) {
                 level = (currentLevel * 100) / scale;
             }
             batteryPercent.setText("Battery Level Remaining: " + level + "%");
             String levelString = Integer.toString(level);
             sendJson("TestUsername", levelString);
         }
     };	
     IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
     registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	 }
	
   /* @Override*/
    protected void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         batteryPercent = (TextView) this.findViewById(R.id.batteryLevel);
         getBatteryPercentage();
    }

//    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    //to sent to webapi
    
    protected void sendJson(final String Username, final String Battery) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost("http://127.0.0.1/api/");
                    json.put("Username", Username);
                    json.put("Battery", Battery);
                    StringEntity se = new StringEntity( json.toString());  
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    Log.d("Error", "Cannot Estabilish Connection");
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();      
    }
}
