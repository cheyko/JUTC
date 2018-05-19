package com.jutcjm.jutc;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener, BusCheckFragment.OnInputListener {


    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public LatLng busPointer;
    public LatLng passengerPointer;
    public int busNumber = 0;
    public NavigationView navigationView;
    public SupportMapFragment mapFragment;
    public android.support.v4.app.FragmentTransaction fragmentTransaction;
    public android.support.v4.app.Fragment fragmentChild;

    //public MapsActivity (LatLng onePlace){
      //  aPlace = onePlace;
    //}

    @Override
    public void sendInput(int num){

        Log.d(TAG,"Input was recieved : "+ num);
        Toast.makeText(this , "Number entered was : " + num, Toast.LENGTH_LONG).show();
        busNumber = num;
        if (busNumber != 0) {
            /// call function to search firebase
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));

        passengerPointer = new LatLng(18.011079, -76.742613);

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

/*        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.getString("busNumber1234") != null){
                Toast.makeText(getApplicationContext(),
                        "BusNumber choosen is: " + bundle.getString("busNumber1234"),
                        Toast.LENGTH_LONG).show();
            }
        }*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MarkerOptions personMarker = new MarkerOptions();
        MarkerOptions busMarker = new MarkerOptions();

        busMarker.position(busPointer).title("Current Bus Location");
        busMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_directions_bus_black_24dp));

        personMarker.position(passengerPointer).title("My Location");
        personMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.twotone_directions_walk_black_18dp));

        mMap.addMarker(busMarker);
        mMap.addMarker(personMarker);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(passengerPointer, 17.0f));

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busPointer, 17.0f));
    }

    /*@Override
    public void onBackPressed(){

    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.busCheck){

            //aPlace = new LatLng(18.011079, -76.742613);
            //Toast.makeText(this,"Selected Bus check",Toast.LENGTH_LONG).show();
            //mapFragment.getMapAsync(this);
            //fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.replace(R.id.map,new BusCheckFragment());
            //fragmentTransaction.commit();

            busPointer = new LatLng(18.017934, -76.792001);
            BusCheckFragment dialog = new BusCheckFragment();
            dialog.show(getSupportFragmentManager(),"MyCustomDialog1");

        }else if (itemId == R.id.placeGoingTo){

            busPointer =  new LatLng(18.011091, -76.797722);
            Toast.makeText(this,"Selected Place going to",Toast.LENGTH_LONG).show();
            mapFragment.getMapAsync(this);

        }else if (itemId == R.id.placeLeavingFrom){

            busPointer = new LatLng(18.017934, -76.792001);
            Toast.makeText(this,"Selected Place leaving From",Toast.LENGTH_LONG).show();
            mapFragment.getMapAsync(this);

        }
        mDrawerLayout.closeDrawer(GravityCompat.START,true);
        return true;
    }


}
