package com.example.een;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSService extends Service {
    private LocationManager locationMgr;
    private String provider;
    private SharedPreferences settings;
    public GPSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e("Service","Service");
        settings = getSharedPreferences("GPS",0);
        initLocationProvider();
        whereAmI();
        return Service.START_STICKY;
    }
    @Override
    public void onDestroy() {
        //handler.removeCallbacks(showTime);
        super.onDestroy();
    }
    private void initLocationProvider() {
        locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationMgr.getBestProvider(criteria, true);
    }
    private void whereAmI() {
        //取得上次已知的位置
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationMgr.getLastKnownLocation(provider);
        getLocation(location);

        int minTime = 5000;//ms
        int minDist = 5;//meter
        locationMgr.requestLocationUpdates(provider, minTime, minDist, locationListener);
    }
    private void getLocation(Location location)
    {
        if(location != null ) {
            Double longitude = location.getLongitude();    //取得經度
            Double latitude = location.getLatitude();    //取得緯度
            Log.e("Change", "" + longitude);
            Log.e("Change", "" + latitude);
            settings.edit().putString("longitude", longitude + "").apply();
            settings.edit().putString("latitude", latitude + "").apply();
        }
    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            getLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("Status","Status");
        }
    };
}
