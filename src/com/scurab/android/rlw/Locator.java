package com.scurab.android.rlw;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

public class Locator {

    private Context mContext;

    private LocationManager mLocationManager;

    public interface OnLocationListener {
	public void onLocationFound(String provider, Location l);
    }

    public Locator(Context c) {
	mContext = c;
    }

    public boolean isGeolocationEnabled() {
	if (mLocationManager == null) {
	    mLocationManager = (LocationManager) mContext
		    .getSystemService(Context.LOCATION_SERVICE);
	}

	Criteria c = new Criteria();
	// c.setAccuracy(Criteria.ACCURACY_FINE); ignore wifi/gsm and use just
	// gps
	c.setAccuracy(Criteria.ACCURACY_COARSE);
	List<String> list = mLocationManager.getProviders(c, true);
	return list != null && list.size() > 0;
    }

    /**
     * @param listener
     *            for callback result of location update, can be null
     * @return lastKnownLocation from network or passive provider
     */
    @SuppressLint("NewApi")
    public void getMyLocation(final OnLocationListener listener) {
	if (listener == null) {
	    throw new IllegalArgumentException("Null listener!");
	}
	Location result = null;
	if (isGeolocationEnabled()) {
	    Criteria c = new Criteria();
	    final String provider = mLocationManager.getBestProvider(c, true);
	    if (mLocationManager
		    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
		result = mLocationManager
			.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	    } else if (mLocationManager
		    .isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
		result = mLocationManager
			.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
	    }
	    final LocationListener lh = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status,
			Bundle extras) {
		    if (Build.VERSION.SDK_INT < 9) {
			mLocationManager.removeUpdates(this);
		    }
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
		    listener.onLocationFound(provider, location);
		    if (Build.VERSION.SDK_INT < 9) {
			mLocationManager.removeUpdates(this);
		    }
		}
	    };
	    if (Build.VERSION.SDK_INT >= 9) {
		mLocationManager.requestSingleUpdate(c, lh,
			mContext.getMainLooper());
	    } else {
		if (provider != null) {
		    mLocationManager.requestLocationUpdates(provider, 0L, 0.0f,
			    lh, mContext.getMainLooper());
		}
	    }
	}
    }
}