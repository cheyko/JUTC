package com.jutcjm.jutc;

import android.*;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener, BusCheckFragment.OnInputListener {


    private static final String TAG = "MapsActivity";
    private static final int REQUEST_LOCATION = 1;
    private static final double SPEED_LIMIT = 12;
    public static final LatLng BUS_DEPOT = (new LatLng(18.012094,-76.798651));

    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    public LatLng busPointer,prevLocation, passengerPointer,busStopPointer;
    public int busNumber = 0;
    public NavigationView navigationView;
    public SupportMapFragment mapFragment;
    public android.support.v4.app.FragmentTransaction fragmentTransaction;
    public android.support.v4.app.Fragment fragmentChild;

    //public MapsActivity (LatLng onePlace){
      //  aPlace = onePlace;
    //}
    LocationManager locationManager;

    private DatabaseReference displacementRef,routeRef,scheduleRef;
    public List<Displacements> displacementsList,routeList;
    public List<RouteTrip> scheduleList;

    public double speedSum = 0,routeDistance,busDis,busCurSpeed, myLatti, myLongi,
            busCurAvgSpeed,intervalTime, currentDistanceTravelled = 0,prevDistTrav;

    public long timeTravelledThus, expectedTime;
    public Date currentTime;
    private BigDecimal lat, lon;
    public String outputString = new String("..");
    MarkerOptions personMarker,busMarker, busStopMarker;
    List<MarkerOptions> busMarkerList;
    public int token;

    @Override
    public void sendInput(int num){

        Log.d(TAG,"Input was recieved : "+ num);
        Toast.makeText(this , "Number entered was : " + num, Toast.LENGTH_LONG).show();
        busNumber = num;
        if (busNumber != 0) {
            /// call function to search firebase
            busSearchNumber(busNumber);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"));

        //passengerPointer = new LatLng(18.060550, -76.794140);
        busPointer = BUS_DEPOT;

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        personMarker = new MarkerOptions();
        busMarker = new MarkerOptions();
        busStopMarker = new MarkerOptions();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.aMap);
        mapFragment.getMapAsync(this);

        // getting input from firebase
        /*********************/

        displacementsList = new ArrayList<>();
        routeList = new ArrayList<>();
        scheduleList = new ArrayList<>();
        busMarkerList = new ArrayList<>();
        speedSum += SPEED_LIMIT;

        // Get a reference to our posts
        displacementRef = FirebaseDatabase.getInstance().getReference("displacements");
        routeRef = FirebaseDatabase.getInstance().getReference("routes");
        scheduleRef = FirebaseDatabase.getInstance().getReference("schedule");
        currentTime = Calendar.getInstance().getTime();
        token = 1;

    }

    @Override
    protected void onStart() {
        super.onStart();
        //      Attach a listener to read the data at our posts reference
        routeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                routeList.clear();
                for(DataSnapshot routeSnapshot : dataSnapshot.getChildren()){

                    Displacements routeDetails = routeSnapshot.getValue(Displacements.class);
                    routeList.add(routeDetails);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        displacementRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                displacementsList.clear();
                for(DataSnapshot routeSnapshot : dataSnapshot.getChildren()){

                    Displacements displacementDetails = routeSnapshot.getValue(Displacements.class);
                    displacementsList.add(displacementDetails);
                }

                if ( busNumber != 0){
                    busSearchNumber(busNumber);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }


        });

        scheduleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                scheduleList.clear();
                for(DataSnapshot routeSnapshot : dataSnapshot.getChildren()){

                    RouteTrip scheduleDetail = routeSnapshot.getValue(RouteTrip.class);
                    scheduleList.add(scheduleDetail);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //// bus stop reference needed (bus stop name and matching coordinates)
        /// and (place name and matching coordinates
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

        for (MarkerOptions bm : busMarkerList){
            mMap.addMarker(bm).remove();

        }


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            getLocation(); //passengerPointer = device location
        }


        if (busPointer == BUS_DEPOT){
            busMarker.position(busPointer).title("Buses are at the BUS-PARK");
        }else {
            busMarker.position(busPointer).title("Current Bus Location");

        }
        busMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_directions_bus_black_24dp));

        personMarker.position(passengerPointer).title("This is Your Location");
        personMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.twotone_directions_walk_black_18dp));

        if ((passengerPointer == prevLocation) || (token == 10)){
            Toast.makeText(this,"You are currently in the same location as before"+prevLocation,Toast.LENGTH_LONG).show();

            new CountDownTimer(5000, 100) {
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    mMap.addMarker(busMarker).showInfoWindow();
                    busMarkerList.add(busMarker);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busPointer, 16.0f));

                }
            }.start();

        }else if (token == 1){
            mMap.addMarker(personMarker).showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(passengerPointer, 16.0f));
            token = 10;

            new CountDownTimer(5000, 100) {
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    mMap.addMarker(busMarker).showInfoWindow();
                    busMarkerList.add(busMarker);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busPointer, 16.0f));

                }
            }.start();


        } else if (token == 7){
            Toast.makeText(this,"You are finding nearest busStop"+prevLocation,Toast.LENGTH_LONG).show();
            busStopMarker.position(busStopPointer).title("This is the nearest Bus Stop");

            mMap.addMarker(busStopMarker).showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busStopPointer,16.0f));


        }else if (token == 5){
            Toast.makeText(this,"You have requested your location only"+prevLocation,Toast.LENGTH_LONG).show();

            mMap.addMarker(personMarker).showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(passengerPointer, 16.0f));
            token = 10;

        }
        prevLocation = new LatLng(myLatti, myLongi);
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

           /* locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();

            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getLocation();
            }
            */

            token = 1;
            BusCheckFragment dialog = new BusCheckFragment();
            dialog.show(getSupportFragmentManager(),"MyCustomDialog1");

        }else if (itemId == R.id.myLocation){

            token = 5;
            Toast.makeText(this,"Finding Your Location",Toast.LENGTH_LONG).show();
            mapFragment.getMapAsync(this);

        } else if (itemId == R.id.placeGoingTo){

            busPointer =  new LatLng(18.011091, -76.797722);
            Toast.makeText(this,"Selected Place going to",Toast.LENGTH_LONG).show();
            mapFragment.getMapAsync(this);

        }else if (itemId == R.id.nearestBS){

            //busPointer =  new LatLng(18.011091, -76.797722);
            token = 7;

            //getNearestBS(passengerPointer);
            Toast.makeText(this,"Searching for nearest bus stop",Toast.LENGTH_LONG).show();
            mapFragment.getMapAsync(this);

        }else if (itemId == R.id.placeLeavingFrom){

            busPointer = new LatLng(18.017934, -76.792001);
            Toast.makeText(this,"Selected Place leaving From",Toast.LENGTH_LONG).show();
            mapFragment.getMapAsync(this);

        }
        mDrawerLayout.closeDrawer(GravityCompat.START,true);
        return true;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                myLatti = location.getLatitude();
                myLongi = location.getLongitude();

            } else  if (location1 != null) {

                myLatti = location.getLatitude();
                myLongi = location.getLongitude();

            } else  if (location2 != null) {
                myLatti = location.getLatitude();
                myLongi = location.getLongitude();

            }else{

                Toast.makeText(this,"Unable to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }

        passengerPointer = new LatLng(myLatti, myLongi);
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void isBefitting(double prevDist, double totalDistance,
                            long timeThus,long prevTime, double curDisTrav, double avgBusSpeed){

        double prevDistanceLeft = totalDistance - prevDist;
        long prevTimeLeft = (long) (prevDistanceLeft / (prevDist/prevTime));

        double distanceLeft = totalDistance - curDisTrav;
        long timeLeft = (long) (distanceLeft / avgBusSpeed);


        Toast.makeText(this," expected time left for bus: "+ timeLeft,Toast.LENGTH_LONG).show();
        long diff = prevTimeLeft - timeLeft;

        String testing = new String ();

        if (diff == (timeThus-prevTime)){//(calculatedTime == (expectingTime - timeThus)){

            Toast.makeText(this,"Bus on perfect timing",Toast.LENGTH_LONG).show();

        }else if(diff < (timeThus-prevTime)){//(calculatedTime > (expectingTime - timeThus)){

            // increase time to (stopwatch) because bus is decreasing its speed (it will take longer to reach now)

            long stopWatchChange = diff - (timeThus-prevTime);
            testing = "prevExpectedTime: " + prevTimeLeft + " expTime:  " + timeLeft + " timeThus: "
                    + timeThus + " real diff: " + diff;
            Toast.makeText(this,"Bus is running Late .... " + testing,Toast.LENGTH_LONG).show();

        }else if(diff > (timeThus-prevTime)){//(calculatedTime < (expectingTime - timeThus)){

            // decrease time to (stopwatch) because bus is increasing its speed (will reach faster)

            long stopWatchChange = diff - (timeThus-prevTime);
            testing = "prevExpectedTime: " + prevTimeLeft + " expTime:  " + timeLeft + " timeThus: "
                    + timeThus + " real diff: " + diff;
            Toast.makeText(this,"Bus is ahead of schedule .... " + testing,Toast.LENGTH_LONG).show();

        }
        expectedTime = timeLeft;
    }

    public void getNearestBS(LatLng yourLocation){

        /// get bus stop list and compute nearest bus stop
        /// set busStopMarker to nearest BS


    }


    public void busSearchNumber(int aBusNumber) {

        for (int i = 0; i < routeList.size(); i++) {
            if (routeList.get(i).getBusNumber() == aBusNumber) {

                routeDistance = routeList.get(i).getDistance();
            }
        }

        int fleetNum = 0;
        List<Displacements> aMovingBus = new ArrayList<>();

        for (int j = 0; j < scheduleList.size(); j++) {
            if (scheduleList.get(j).getBusNumber() == aBusNumber) { // and scheduleList.get(j).getEndtime > currentTime
                fleetNum = (scheduleList.get(j).getFleetNumber());
            }

        }

        for (int p = 0 ; p < displacementsList.size() ; p++ ){
            //Toast.makeText(this,"the bus was at .... " + displacementsList.get(p).getPoints().getLatti() +
            //        " , "+displacementsList.get(p).getPoints().getLongi(),Toast.LENGTH_LONG).show();
            if (displacementsList.get(p).getFleetNumber() == fleetNum){
                aMovingBus.add(displacementsList.get(p));
            }
        }

        int k = aMovingBus.size();
        List<PolylineOptions> polylinesOpt = new ArrayList<PolylineOptions>();
        //List<Polyline> polylines = new ArrayList<Polyline>();

        if (k <= 1) {

            outputString = "Bus is currently at the Depot";

            for(int ix = 0 ; ix < polylinesOpt.size();ix++)
            {
                mMap.addPolyline(polylinesOpt.get(ix)).remove();
                mMap.addMarker(busMarkerList.get(ix)).remove();
            }
            polylinesOpt.clear();

        } else {

            for (int j = 0; j < k - 1; j++) {

                BigDecimal x1 = new BigDecimal(aMovingBus.get(j).getPoints().getLatti());
                BigDecimal y1 = new BigDecimal(aMovingBus.get(j).getPoints().getLongi());
                BigDecimal x2 = new BigDecimal(aMovingBus.get(j + 1).getPoints().getLatti());
                BigDecimal y2 = new BigDecimal(aMovingBus.get(j + 1).getPoints().getLongi());
                intervalTime = aMovingBus.get(j + 1).getTimeThus() - aMovingBus.get(j).getTimeThus();

                //illustration of the simulation of bus moving.
                PolylineOptions newOption = new PolylineOptions().add(new LatLng(Double.parseDouble(x1.toString())
                        ,Double.parseDouble(y1.toString()))).add(new LatLng(Double.parseDouble(x2.toString())
                        ,Double.parseDouble(y2.toString()))).width(7).color(Color.GREEN).geodesic(true);

                //mMap.addPolyline(newOption);
                polylinesOpt.add(newOption);
                mMap.addPolyline(polylinesOpt.get(j));

                ///// Calculations made for optimization

                busDis = Haversine.distance(x1, y1, x2, y2);
                busCurSpeed = busDis / intervalTime;

                speedSum += busCurSpeed;
                prevDistTrav = currentDistanceTravelled;

                currentDistanceTravelled += busDis;
                busCurAvgSpeed = speedSum / (k);

                lat = x2;
                lon = y2;
            }

            timeTravelledThus = aMovingBus.get(k - 1).getTimeThus();
            long prevTime = aMovingBus.get(k - 2).getTimeThus();

            busPointer = new LatLng(Double.parseDouble(lat.toString()),Double.parseDouble(lon.toString()));
            isBefitting(prevDistTrav, routeDistance, timeTravelledThus, prevTime, currentDistanceTravelled, busCurAvgSpeed);
            outputString = "Bus is on the move";
        }

        //for(PolylineOptions poly : polylines)
        //{
            //polylines.add(this.mMap.addPolyline(new PolylineOptions()....));
            //mMap.addPolyline(poly);
        //}
        Toast.makeText(this, outputString, Toast.LENGTH_LONG).show();
        mapFragment.getMapAsync(this);
    }

}
