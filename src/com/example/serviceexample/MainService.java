package com.example.serviceexample;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;




public class MainService extends Service implements AccelerometerListener, ConnectionCallbacks, OnConnectionFailedListener {

	private final String TAG = "MainService";
	public SharedPreferences sharedPref;
	public final AccelerometerListener Listener = this;
	private static Looper threadLooper = null;
	private LocationHelper locationHelper;
	private boolean isGPSAvailable = false;
	private GoogleApiClient locationClient;
	private LocationRequest locationRequest;
	private Vibrator v;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate()
	{
		Log.i("OnCreate", "Creating");
		sharedPref = this.getSharedPreferences("Running",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("RunningFlag", 1);
		editor.commit();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i("OnstartCommad", "Starting");
		v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		/*if(AccelerometerManager.isSupported(this))
		{
			AccelerometerManager.startListening(this);
		}*/
		Thread thread = new ThreadClass();
		thread.start();
		if( GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS)
		{
			isGPSAvailable = true;
			locationClient = new GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();
		}
		else {
			isGPSAvailable = false;
			locationHelper = new LocationHelper();
			locationHelper.initialize(getApplicationContext());
		}
		return START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		Log.i("OnDestroy", "Destroying");
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("RunningFlag", 0);
		editor.commit();
		
		threadLooper.quit();
	}

	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShake(float force) {
		
		v.vibrate(100);
		
		Log.i(TAG, "MotionDetected");
		if(isGPSAvailable) {
			locationClient.connect();
		}
		else
			locationHelper.startListening();
	}
	
	private class ThreadClass extends Thread {
		@Override
        public void run() {
            Looper.prepare();
            if(AccelerometerManager.isSupported(getApplicationContext()))
    		{
    			AccelerometerManager.startListening(Listener);
    		}
            threadLooper = Looper.myLooper();
            Looper.loop();  // loop until "quit()" is called.

            if(AccelerometerManager.isListening())
    		{
    			AccelerometerManager.stopListening();
    		}
          
        }
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}
	
	private LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			System.out.println(location);
			locationClient.disconnect();
		}
		
	};

	@Override
	public void onConnected(Bundle connectionHint) {
		
		Log.i(TAG, "Connected");
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, listener);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}
	

}
