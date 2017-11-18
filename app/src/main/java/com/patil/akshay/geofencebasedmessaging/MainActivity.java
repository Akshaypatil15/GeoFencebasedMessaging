package com.patil.akshay.geofencebasedmessaging;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {
    EditText editText, editText2;
    Button button;
    LocationManager locationManager;
    double latitudeA,longitudeA;
    float dist;
    int key;
    TextView txt;
    private static final int MY_PERMISSION_REQ_AC = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndRequestPermission();

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        button = (Button) findViewById(R.id.button);
        txt = (TextView) findViewById(R.id.txt);

        try {
            Thread.sleep(2000);
            Toast.makeText(MainActivity.this, "Checking Current Location!!!", Toast.LENGTH_SHORT).show();
            getLocation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = editText.getText().toString();
                String sms = editText2.getText().toString();

                float[] results = new float[1];
                // Set Geo-Fence     Latitude -> 18.554498  Longitude -> 73.825732
                Location.distanceBetween(18.554498, 73.825732, latitudeA, longitudeA, results);
                float distMtr = results[0];
                txt.setText("Current position \nLatitude: "+18.554498+ "  Longitude: "+ 73.825732 +"\nDistance: "+(int)distMtr+" Meters");

                if (distMtr < 5000){
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(number, null, sms, null, null);
                        Toast.makeText(MainActivity.this, "You are in Geo-Fence!!! \n SMS Sent!", Toast.LENGTH_LONG).show();
                    } catch (Exception e){
                        Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "You are not in Geo-Fence!!! \nSMS Sent Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void getLocation() {
        try {
            this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    private boolean checkAndRequestPermission() {
        int smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int gpsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionNeeded = new ArrayList<>();
        if (smsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (gpsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), MY_PERMISSION_REQ_AC);
            return false;
        }
        else {}
        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        // Get current position
        latitudeA = location.getLatitude();
        longitudeA = location.getLongitude();
        float[] results = new float[1];
        Location.distanceBetween(18.554498, 73.825732, latitudeA, longitudeA, results);
        float distMtr = results[0];
        txt.setText("Current position \nLatitude: "+18.554498+ "  Longitude: "+ 73.825732 +"\nDistance: "+(int)distMtr+" Meters");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void onRequestPermissionResult(int requestCode, String permission[], int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQ_AC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } break;
        }
    }

    @Override
    public void onBackPressed() {
        if(key == 1){
            key =0;
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "press back Button again to exit", Toast.LENGTH_SHORT).show();
            key++;
        }
    }
}