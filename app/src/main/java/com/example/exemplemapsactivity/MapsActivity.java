package com.example.exemplemapsactivity;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener  {

    GoogleMap mMap;
    long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; //en metre
    long MIN_TIME_BW_UPDATES = 1000 * 60 * 2; //en millisecond (2 mn)
    LocationManager locationManager;
    Context context;

    Location location;
    double _latitude;
    double _longitude;
    boolean checkGPS;
    boolean checkNetwork;
    String typePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        context = this;

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //On choisis le mode d'affichage
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //On choisi les gestures afficher à l'ecran
        // mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //on voit les building en 3d
        mMap.setBuildingsEnabled(true);

        //On utilise le systeme le localisation de google
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //on autorise sa géolocalisation
            mMap.setMyLocationEnabled(true);

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            if (locationManager != null) {

                // get GPS status
                checkGPS = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // get network provider status
                checkNetwork = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                //Mode gps
                if (checkGPS) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) context);

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                //mode par triangulation (bornes)
                else if (checkNetwork) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) context);

                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                //On verfie que l'on a bien une position
                if(location != null)
                {
                    Localisation(location);
                }
                else
                {
                    Init();
                }
            }
        }
        else
        {
            Toast.makeText(context,"Je n'ai pas l'autorisation",Toast.LENGTH_LONG).show();
            Init();
        }

    }

    private void Init()
    {
        // Add a marker in Sydney and move the camera
        LatLng paris = new LatLng(48.8566, 2.3522);

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(paris).title("Ici c'est paris"));

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris,15));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(paris, 15));
    }


    private void Localisation(Location location){
        //On instencie les variable global Lat et Long
        _latitude = location.getLatitude();
        _longitude = location.getLongitude();

        //on crée l'obj LatLong
        LatLng malocalisation = new LatLng(_latitude, _longitude);

        // On ajoute un marqueur
        mMap.addMarker(new MarkerOptions().position(malocalisation).title("je suis ici"));

        //On centre la camera sur la LatLong générée. v: zooming :1: World 5: Landmass/continent 10: City 15: Streets 20: Buildings
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(malocalisation,20));
    }


    @Override
    public void onLocationChanged(Location location) {
        //Efface les markers
        mMap.clear();

        Localisation(location);
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

}
