package com.example.avc.ui.othertesting;

import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.avc.OtherTesting;
import com.example.avc.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
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
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OtherTestingTimeFragment extends Fragment {

    private OtherTestingTimeViewModel mViewModel;
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

    public static OtherTestingTimeFragment newInstance() {
        return new OtherTestingTimeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if(PackageManager.PERMISSION_GRANTED!= ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)){
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }

        View rootView = inflater.inflate(R.layout.other_testing_time_fragment, container, false);
        mMapView = rootView.findViewById(R.id.timeMapView);
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
        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;
            // For showing a move to my location button
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setScrollGesturesEnabled(false);

            for (Pair<LatLng,String> pair : latlngs) {
                options.position(pair.first);
                options.title(pair.second);
                options.snippet("Institut capabil de tratare AVC");
                googleMap.addMarker(options);
            }

            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    Location mLastKnownLocation = task.getResult();
                    if (mLastKnownLocation != null) {

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (Pair<LatLng,String> marker : latlngs) {
                            builder.include(marker.first);
                        }
                        builder.include(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                        LatLngBounds bounds = builder.build();
                        int padding = 100; // offset from edges of the map in pixels


                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude())).zoom(14).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,padding));

                        googleMap.setOnMapClickListener(latLng -> {
                            Collections.sort(latlngs, (a, b) -> {
                                Location locationA = new Location("point A");
                                locationA.setLatitude(a.first.latitude);
                                locationA.setLongitude(a.first.longitude);
                                Location locationB = new Location("point B");
                                locationB.setLatitude(b.first.latitude);
                                locationB.setLongitude(b.first.longitude);
                                float distanceOne = mLastKnownLocation.distanceTo(locationA);
                                float distanceTwo = mLastKnownLocation.distanceTo(locationB);
                                return Float.compare(distanceOne, distanceTwo);
                            });
                            Pair<LatLng,String> closest = latlngs.get(0);
                            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+closest.first.latitude+","+closest.first.longitude+"("+ closest.second+")");
                            Intent intent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);
                        });
                    }
                }
            });
        });

        OtherTesting testingActivity = (OtherTesting) getActivity();
        short otherSymptoms = 0;
        if(testingActivity.faceNumbness)
            otherSymptoms++;
        if(testingActivity.headache)
            otherSymptoms++;
        if(testingActivity.puking)
            otherSymptoms++;
        if(testingActivity.balance)
            otherSymptoms++;
        if(testingActivity.vision)
            otherSymptoms++;
        if(testingActivity.confusion)
            otherSymptoms++;

        short unknown = 0;
        if(testingActivity.faceResult == 0)
            unknown++;
        if(testingActivity.armsResult== 0)
            unknown++;
        if(testingActivity.speechResult== 0)
            unknown++;

        //Severe cases
        if(testingActivity.faceResult == 1 || testingActivity.armsResult == 1 || testingActivity.speechResult == 1 || unknown>=2 || otherSymptoms>=4 || (unknown>=1 && otherSymptoms>2))
        {
            ((TextView)rootView.findViewById(R.id.timeTitleResult)).setText("Risc AVC ridicat!");
            ((TextView)rootView.findViewById(R.id.timeSubtitle)).setText("Sună IMEDIAT la 112!");
            ((ImageView)rootView.findViewById(R.id.timeRisk)).setImageDrawable(getContext().getDrawable(R.drawable.risk));
            ((ImageView)rootView.findViewById(R.id.timeBackgroundGradient)).setImageDrawable(getContext().getDrawable(R.drawable.time_critical));
            ((TextView)rootView.findViewById(R.id.lowRiskMessage)).setVisibility(View.INVISIBLE);
            ((Button)rootView.findViewById(R.id.emergencyButton)).setBackground(getContext().getDrawable(R.drawable.gradient_high_risk_button));
        }//Medium cases
        else if(unknown==1 || otherSymptoms>=2){
            ((TextView)rootView.findViewById(R.id.timeTitleResult)).setText("Risc AVC mediu!");
            ((TextView)rootView.findViewById(R.id.timeSubtitle)).setText("Considerati să sunați la 112");
            ((ImageView)rootView.findViewById(R.id.timeRisk)).setImageDrawable(getContext().getDrawable(R.drawable.risk));
            ((ImageView)rootView.findViewById(R.id.timeBackgroundGradient)).setImageDrawable(getContext().getDrawable(R.drawable.time_medium));
            ((TextView)rootView.findViewById(R.id.lowRiskMessage)).setVisibility(View.INVISIBLE);
            ((Button)rootView.findViewById(R.id.emergencyButton)).setBackground(getContext().getDrawable(R.drawable.gradient_medium_risk_button));
        }//Low cases
        else{
            ((TextView)rootView.findViewById(R.id.timeTitleResult)).setText("Risc AVC scazut!");
            ((TextView)rootView.findViewById(R.id.timeSubtitle)).setText("Considerați un control medical");
            ((ImageView)rootView.findViewById(R.id.timeRisk)).setImageDrawable(getContext().getDrawable(R.drawable.right));
            ((ImageView)rootView.findViewById(R.id.timeRisk)).setScaleX(1.0f);
            ((ImageView)rootView.findViewById(R.id.timeBackgroundGradient)).setImageDrawable(getContext().getDrawable(R.drawable.time_low));
            ((TextView)rootView.findViewById(R.id.lowRiskMessage)).setVisibility(View.VISIBLE);
            Button btn = ((Button)rootView.findViewById(R.id.emergencyButton));
            ((ViewManager)btn.getParent()).removeView(btn);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(OtherTestingTimeViewModel.class);
        // TODO: Use the ViewModel
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
