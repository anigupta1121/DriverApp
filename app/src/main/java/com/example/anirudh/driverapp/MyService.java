package com.example.anirudh.driverapp;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class MyService extends Service {
    private int mNotificationId = 001;

    LocationManager locationManager;
    LocationListener listener;

    public MyService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(MyService.this, "SERVICE RUNNING", Toast.LENGTH_SHORT).show();


        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("My notification")
                                .setContentText("" + location.getLatitude() + " " + location.getLongitude());
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
                Intent i=new Intent("loc");
                i.putExtra("x",location.getLatitude()+"");
                i.putExtra("y",location.getLongitude()+"");
                sendBroadcast(i);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

                Toast.makeText(MyService.this, "GPS Enabled!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };


        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        locationManager.removeUpdates(listener);
    }
}