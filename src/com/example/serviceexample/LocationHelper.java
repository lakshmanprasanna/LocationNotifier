package com.example.serviceexample;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationHelper {
	
	private LocationManager locationManager;
	private Context aContext;
	private PackageManager pm;
	private Location newestLocation = null;
	public void initialize(Context context)
	{
		aContext = context;
		locationManager = (LocationManager) aContext.getSystemService(aContext.LOCATION_SERVICE);
	}
	public boolean isAvailable()
	{
		List<String> str = locationManager.getProviders(true);
			 
		if(str.size()>0)
			return true;
		else
			return false;
	}
	
	public boolean isGpsEnabled()
	{
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			return true;
		}
		else
			return false;
	}
	public boolean isNetworkEnabled()
	{
		if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			return true;
		}
		else
			return false;
	}
	
	private	LocationListener locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				System.out.println("hellllllllllllllllllllllllllllllllooooooooooooo");
				if(isBetterLocation(location,newestLocation))
				{
				  double lat = location.getLatitude();
				  double lon = location.getLongitude();
				  Log.i("Location", "Latitude"+" "+lat+" "+"Longitude"+lon);
				  locationManager.removeUpdates(locationListener);
				  Log.i("Location", "Locationupdates removed");
				}
				else
					newestLocation = location;
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
		   
		   
		  };
	
	public boolean startListening() 
	{
		
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location == null)
		{
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
		}
		if (location != null) {
			if (newestLocation == null) {
				newestLocation = location;
			} 
			else {
				if (location.getTime() > newestLocation.getTime()) {
					newestLocation = location;
				}
			}
		}
			
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(locationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
		return true;
		
	}
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	
}
