package com.example.een;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker markerMe;
    Handler handler=new Handler();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        handler.postDelayed(update, 500);
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
        SharedPreferences settings;
        settings = getSharedPreferences("GPS", 0);
        String str_longitude = settings.getString("longitude", "");
        String str_latitude = settings.getString("latitude", "");
        Double longitude = Double.parseDouble(str_longitude);    //取得經度
        Double latitude = Double.parseDouble(str_latitude);    //取得緯度
        // Add a marker in Sydney and move the camera
        LatLng mapCenter = new LatLng(latitude, longitude);
        markRecord();
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 15));
        markerMe = mMap.addMarker(new MarkerOptions()
                .position(mapCenter)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                .flat(true)
                .rotation(0));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mapCenter)
                .zoom(15)
                .bearing(0)
                .build();
        // Animate the change in camera view over 2 seconds
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                500, null);
    }

    //標記發生意外的地點
    private void markRecord()
    {
        // SELECT DISTINCT map_location, map_longitude, map_latitude FROM android_accident_record　WHERE visible = 1;
        String sql = "queryStr=SELECT%20DISTINCT%20map_location,%20map_longitude,%20map_latitude%20FROM%20android_accident_record%20WHERE%20visible%20=%201;";
        String response = DBHandler.query(sql);
        Log.e("response",response);
        if( response.equals("") || response.equals("\uFEFFnull")){
            Toast.makeText(MapActivity.this, getString(R.string.txt_NoRecord), Toast.LENGTH_SHORT).show();
        }
        else{
            try {
                JSONArray jsonArray = new JSONArray(response);
                for( int i = 0 ; i < jsonArray.length() ; i++){
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    String str_longitude = jsonData.getString("map_longitude");
                    String str_latitude = jsonData.getString("map_latitude");
                    Double longitude = Double.parseDouble(str_longitude);    //取得經度
                    Double latitude = Double.parseDouble(str_latitude);    //取得緯度
                    LatLng myLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions()
                            .position(myLocation)
                            .title("發生意外")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_error_black))
                    );
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void updateMyLocation()
    {
        if( markerMe != null )
            markerMe.remove();
        SharedPreferences settings;
        settings = getSharedPreferences("GPS", 0);
        String str_longitude = settings.getString("longitude", "");
        String str_latitude = settings.getString("latitude", "");
        Double longitude = Double.parseDouble(str_longitude);    //取得經度
        Double latitude = Double.parseDouble(str_latitude);    //取得緯度
        LatLng myLocation = new LatLng(latitude, longitude);

        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.position(myLocation);
        markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
        markerOpt.title("現在位置");
        markerOpt.flat(true);
        markerOpt.rotation(0);

        markerMe = mMap.addMarker(markerOpt);
    }
    private Runnable update = new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
            updateMyLocation();
            handler.postDelayed(this, 500);
        }
    };
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Map Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.een/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Map Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.een/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
