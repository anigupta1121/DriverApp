package com.example.anirudh.driverapp;

import android.*;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity  {

    FirebaseDatabase mDataBase;
    DatabaseReference mrefReq;
    Location last;
    DatabaseReference mrefLoc;

    LocationManager locationManager;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    BroadcastReceiver receiver;


    ArrayList<Requests> reqList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mDataBase=FirebaseDatabase.getInstance();
        mrefReq =mDataBase.getReference().child("Driver").child("requests");
        mrefLoc=mDataBase.getReference().child("Driver").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!requestPermission()) {
            Location last = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (last != null) {
                mrefLoc.child("location").child("lat").setValue(last.getLatitude());
                mrefLoc.child("location").child("lng").setValue(last.getLongitude());
            }
            System.out.print("abcd");
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        }
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.refLayout);

        getData();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        refreshLayout.setRefreshing(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView=(RecyclerView)findViewById(R.id.recycler);
        adapter=new RecyclerAdapter(reqList,this);
        recyclerView.setLayoutManager(layoutManager);


       /* mrefReq.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("Child added",dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


    }

    private boolean requestPermission() {
        if (Build.VERSION.SDK_INT>=23 && ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);


            return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(this, MyService.class);
                startService(intent);
            }
            else {
                requestPermission();
            }
        }
    }


    void getData(){
        refreshLayout.setRefreshing(true);

        mrefReq.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reqList.removeAll(reqList);
                for(DataSnapshot k:dataSnapshot.getChildren()){
                    reqList.add(new Requests(
                             k.child("assigned").getValue().toString()
                            ,k.child("place").getValue().toString()
                            ,k.child("customerUID").getValue().toString()
                            ,k.getKey()));
                }
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(Main2Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (receiver==null){
            receiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    try {

                        String x = intent.getExtras().get("x").toString();
                        String y = intent.getExtras().get("x").toString();
                        mrefLoc.child("location").child("lat").setValue(x);
                        mrefLoc.child("location").child("lng").setValue(y);


                    }
                    catch (NullPointerException e){

                    }
                }
            };


        }
        registerReceiver(receiver,new IntentFilter("coordinates"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null)
            unregisterReceiver(receiver);
    }
}
