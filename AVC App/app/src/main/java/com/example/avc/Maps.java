package com.example.avc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Maps extends Fragment {
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };

    private static final int INITIAL_REQUEST=1337;

    private MapView mMapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private MarkerOptions options = new MarkerOptions();
    private ArrayList<Pair<LatLng,String>> latlngs = new ArrayList<>();

    public Maps() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(PackageManager.PERMISSION_GRANTED!= ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)){
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }

        View rootView = inflater.inflate(R.layout.maps, container, false);
        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        options.infoWindowAnchor(0.0f,0.0f);


        InputStream is = getResources().openRawResource(R.raw.hospital_locations);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                double latitude = explrObject.getDouble("latitude");
                double longitude = explrObject.getDouble("longitude");
                String hospitalName = explrObject.getString("hospitalName");
                latlngs.add(new Pair<>(new LatLng(latitude, longitude), hospitalName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);

                for (Pair<LatLng,String> pair : latlngs) {
                    options.position(pair.first);
                    options.title(pair.second);
                    options.snippet("Institut capabil de tratare AVC");
                    googleMap.addMarker(options);
                }

                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            Location mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (Pair<LatLng,String> marker : latlngs) {
                                    builder.include(marker.first);
                                }
                                builder.include(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                                LatLngBounds bounds = builder.build();
                                int padding = 80; // offset from edges of the map in pixels


                                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude())).zoom(14).build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,padding));

                                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
//                                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+marker.getPosition().latitude+","+marker.getPosition().longitude);
                                        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+marker.getPosition().latitude+","+marker.getPosition().longitude+"("+ marker.getTitle()+")");
                                        Intent intent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
                                        intent.setPackage("com.google.android.apps.maps");
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });




        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
