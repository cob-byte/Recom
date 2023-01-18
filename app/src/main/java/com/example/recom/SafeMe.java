package com.example.recom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SafeMe extends AppCompatActivity {

    Spinner spType;
    Button btFind;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat=0,currentLng=0;
    ImageButton SafebackBtn;
    TextView loc,safe, safeHealth, safeTyphoon, safeFire, safeEarthquake;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_me);

        getSupportActionBar().hide();

        //spType = findViewById(R.id.sp_type);
        safeHealth=findViewById(R.id.safeHealth);
        safeTyphoon=findViewById(R.id.safeTyphoon);
        safeEarthquake=findViewById(R.id.safeEarthquake);
        safeFire=findViewById(R.id.safeFire);

        safe = findViewById( R.id.SafeCLocation);
        loc=findViewById(R.id.safeLocationview);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        String[] placeTypeList = {"hospital", "fire_station", "fire_station", "fire_station"};
        String[] placeNameList = {"Health Services", "Fire", "Earthquake", "Typhoon"};

//        spType.setAdapter(new ArrayAdapter<>(SafeMe.this
//                , android.R.layout.simple_spinner_dropdown_item, placeNameList));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();


        }else{
            ActivityCompat.requestPermissions(SafeMe.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},44);
        }

//        safe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                int i =spType.getSelectedItemPosition();
//                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
//                        "?location=" + currentLat + "," + currentLng +
//                        "&radius=5000" +
//                        "&types=" + placeTypeList[i] +
//                        "&sensor=true" +
//                        "&key=" + getResources().getString(R.string.app_key);
//
//                new PlaceTask().execute(url);
//            }
//        });

        safeHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent info= new Intent(SafeMe.this, hotline_allnum.class);
                startActivity(info);
            }
        });
        safeTyphoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent info= new Intent(SafeMe.this, typhoon_allnum.class);
                startActivity(info);
            }
        });
        safeFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent info= new Intent(SafeMe.this, fire_allnum.class);
                startActivity(info);
            }
        });
        safeEarthquake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent info= new Intent(SafeMe.this, quake_allnum.class);
                startActivity(info);
            }
        });

        SafebackBtn = findViewById(R.id.SafebackBtn);
        SafebackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if(location!=null){
                    currentLat=location.getLatitude();
                    currentLng=location.getLongitude();

//                    LatLng latlng=new LatLng(currentLat,currentLng);
                    try {
                        Geocoder geocoder = new Geocoder(SafeMe.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        String address = addresses.get(0).getAddressLine(0);

                        loc.setText(address);

                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            map=googleMap;
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat,currentLng), 16
                            ));

                            MarkerOptions options=new MarkerOptions();
                            LatLng latlng = new LatLng(currentLat, currentLng);
                            // Position of Marker on Map
                           //options.title(name);
                            options.position(latlng);
                            //loc.setText();
                            Marker m= map.addMarker(options);
                            if (m != null) {
                                m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.usermarker));
                            }
                        }
                    });
                }
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 44) {

            if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){

                getCurrentLocation();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data=null;
            try {

                data=downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            new ParserTask().execute(s);

        }
    }

    private String downloadUrl(String string) throws IOException {
        URL url=new URL(string);

        HttpURLConnection connection=(HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream stream=connection.getInputStream();
        BufferedReader reader=new BufferedReader(new InputStreamReader(stream));

        StringBuilder builder=new StringBuilder();

        String line="";
        while((line=reader.readLine())!=null){

            builder.append(line);

        }

        String data=builder.toString();
        reader.close();
        return data;
    }

    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser=new JsonParser();

            List <HashMap<String,String>> mapList=null;
            JSONObject object=null;

            try {
                object= new JSONObject(strings[0]);
                mapList=jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {

            map.clear();

            for(int i=0;i<hashMaps.size();i++){
                HashMap<String,String> hashMapList= hashMaps.get(i);

                double lat=Double.parseDouble(hashMapList.get("lat"));
                double lng=Double.parseDouble(hashMapList.get("lng"));

                String name=hashMapList.get("name");
                LatLng latlng=new LatLng(lat,lng);
                MarkerOptions options=new MarkerOptions();
                options.position(latlng);
                options.title(name);
                Marker m=map.addMarker(options);
                if (m != null) {
                    m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
                }


            }
        }
    }
}