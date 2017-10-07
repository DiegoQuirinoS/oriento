package br.com.hackaton.oriento;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATIONS = 0;
    private static final long LOCATION_REFRESH_TIME = 2000;
    private static final float LOCATION_REFRESH_DISTANCE = 0.0f;
    private LocationManager mLocationManager;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    public boolean canGetLocation = true;
    private Location location;
    private double latitude;
    private double longitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, this);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATIONS);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
//
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
                    intent.putExtra("enabled", true);
                    sendBroadcast(intent);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.s
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient hhtpClient = new OkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.104.3:8080")
                .client(hhtpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Client client = retrofit.create(Client.class);
        Call<String> linhas = client.linhas();

        linhas.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Toast.makeText(MainActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"Não foi possíve encontrar nenhum ponto próximo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        System.out.println(status);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getLocation() {
        try {
            mLocationManager = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);

            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    } else {
                        if (isGPSEnabled) {
                            if (location == null) {
                                mLocationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        LOCATION_REFRESH_TIME,
                                        LOCATION_REFRESH_DISTANCE, this);
                                if (mLocationManager != null) {
                                    location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
}
