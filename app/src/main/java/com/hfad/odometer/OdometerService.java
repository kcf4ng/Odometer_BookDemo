package com.hfad.odometer;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.content.ContextCompat;

import java.util.Random;

public class OdometerService extends Service {
    private final IBinder binder  = new OdometerBinder();
    private final Random random = new Random();
    private LocationListener  listener ;
    private LocationManager locationManager;
    public static  final  String PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;
    private  static double distanceInMeter ;
    private static Location lastLocation = null;



    public class OdometerBinder extends Binder{

        OdometerService getOdometer (){
            return OdometerService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //距離 追蹤
                if(lastLocation == null)
                {
                    lastLocation = location;
                }
                distanceInMeter += location.distanceTo(lastLocation);
                lastLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ContextCompat.checkSelfPermission(this,PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED){
            String provider = locationManager.getBestProvider(new Criteria(), true);
            if(provider != null){
                locationManager.requestLocationUpdates(provider,1000,1,listener);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return binder;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null  && listener != null) {
            if(ContextCompat.checkSelfPermission(this,PERMISSION_STRING) == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(listener);
            }
            locationManager = null;
            listener= null;

        }

    }

    public double getDistance(){
//        return random.nextDouble();

    return this.distanceInMeter / 1609.344 ;
    //將公尺轉換成英里(除以１６０９．３４４)
    }



}
