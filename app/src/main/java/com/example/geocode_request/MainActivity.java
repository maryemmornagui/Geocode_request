package com.example.geocode_request;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;



import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Use the Mapbox Geocoding API to retrieve various information about a set of coordinates.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private Button chooseCityButton;
    private EditText latEditText;
    private EditText longEditText;
    private TextView geocodeResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, "pk.eyJ1IjoibWFyeWFtbW9ybmFndWkiLCJhIjoiY2s3dW84YThuMTB4YjNvbXJyc2hlNG5hayJ9.at86ELId-GCZVyB9I42X1g");

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                initTextViews();
                initButtons();
            }
        });
    }

    private void initTextViews() {
        latEditText = findViewById(R.id.geocode_latitude_editText);
        longEditText = findViewById(R.id.geocode_longitude_editText);
        geocodeResultTextView = findViewById(R.id.geocode_result_message);
    }

    private void initButtons() {
        Button mapCenterButton = findViewById(R.id.map_center_button);
        Button startGeocodeButton = findViewById(R.id.start_geocode_button);
        chooseCityButton = findViewById(R.id.choose_city_spinner_button);
        startGeocodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make sure the EditTexts aren't empty


                if (TextUtils.isEmpty(latEditText.getText().toString())) {
                    latEditText.setError(getString(R.string.fill_in_a_value));
                } else if (TextUtils.isEmpty(longEditText.getText().toString())) {
                    longEditText.setError(getString(R.string.fill_in_a_value));
                } else {

                    if (latCoordinateIsValid(Double.valueOf(latEditText.getText().toString()))
                            && longCoordinateIsValid(Double.valueOf(longEditText.getText().toString())))

                    {



                        // Make a geocoding search with the values inputted into the EditTexts
                        makeGeocodeSearch(new LatLng(Double.valueOf(latEditText.getText().toString()),
                                Double.valueOf(longEditText.getText().toString())));


                    } else {
                        Toast.makeText(MainActivity.this, R.string.make_valid_lat, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        chooseCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                double  coordonnees[][]=new  double [4][2];

                //focus
                coordonnees [0][0]= 36.899378;
                coordonnees [0][1]= 10.190871;
//habib borguiba
                coordonnees [1][0]= 36.800640;
                coordonnees [1][1]= 10.186609;
//rades
                coordonnees [2][0]= 36.748195;
                coordonnees [2][1]= 10.272628;
//bab bhar
                coordonnees [3][0]= 36.803358;
                coordonnees [3][1]= 10.175673;

                long temps_depart = System.currentTimeMillis();
                long duree = 5000; // en millisecondes
                int i = 0;

                try {
                    while( i<4 )
                    {
                        LatLng cityLatLng = new LatLng();


                        cityLatLng = new LatLng(coordonnees[i][0], coordonnees[i][1]);
                        setCoordinateEditTexts(cityLatLng);
                        animateCameraToNewPosition(cityLatLng);
                        makeGeocodeSearch(cityLatLng);

                        Thread.sleep(5000);
                        i=i+1;
                    }
                } catch (InterruptedException ex) {
                    //SomeFishCatching
                }

//                Timer timer = new Timer();
//                // creating timer task, timer
//                TimerTask tasknew = new TimerTask() {
//                    @Override
//                    // this method performs the task
//                    public void run() {
//
//                        LatLng cityLatLng = new LatLng();
//
//                        for(int i = 0; i <4; i++) {
//                            cityLatLng = new LatLng(coordonnees[i][0], coordonnees[i][1]);
//                            setCoordinateEditTexts(cityLatLng);
//                            animateCameraToNewPosition(cityLatLng);
//                            makeGeocodeSearch(cityLatLng);
//
//                            try {
//                                Thread.sleep(5000);
//                            } catch(InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    };
//
//                };
//                timer.schedule(tasknew, 0, 5000);


            }
        });
        mapCenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the map's target
                LatLng target = mapboxMap.getCameraPosition().target;

                // Fill the coordinate EditTexts with the target's coordinates
                setCoordinateEditTexts(target);

                // Make a geocoding search with the target's coordinates
                makeGeocodeSearch(target);

            }
        });
    }

    private boolean latCoordinateIsValid(double value) {
        return value >= -90 && value <= 90;
    }

    private boolean longCoordinateIsValid(double value) {
        return value >= -180 && value <= 180;
    }

    private void setCoordinateEditTexts(LatLng latLng) {
        latEditText.setText(String.valueOf(latLng.getLatitude()));
        longEditText.setText(String.valueOf(latLng.getLongitude()));
    }

    private void showCityListMenu() {
        List<String> modes = new ArrayList<>();
        modes.add("Focus");
        modes.add("Avenue Habib Borguiba");
        modes.add("Stade Rades");
        modes.add("Bab el Bhar");
        ArrayAdapter<String> profileAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, modes);

        final ListPopupWindow listPopup = new ListPopupWindow(this);
        listPopup.setAdapter(profileAdapter);
        listPopup.setAnchorView(chooseCityButton);
        listPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long longg) {
                LatLng cityLatLng = new LatLng();
                if (position == 0) {
                    // Focus
                    //36.899378, 10.190871
                    cityLatLng = new LatLng(36.899378, 10.190871);

                    setCoordinateEditTexts(cityLatLng);


                } else if (position == 1) {
                    // Avenue Habib Borguiba Tunis
                    //36.800640, 10.186609
                    cityLatLng = new LatLng(36.800640, 10.186609);
                    setCoordinateEditTexts(cityLatLng);
                } else if (position == 2) {
                    // Stade Rades Rades
                    //36.748195, 10.272628
                    cityLatLng = new LatLng(36.748195, 10.272628);
                    setCoordinateEditTexts(cityLatLng);
                } else if (position == 3) {
                    // Bab el Bhar Tunis
                    //36.803358, 10.175673
                    cityLatLng = new LatLng(36.803358, 10.175673);
                    setCoordinateEditTexts(cityLatLng);
                }
                animateCameraToNewPosition(cityLatLng);
                makeGeocodeSearch(cityLatLng);
                listPopup.dismiss();
            }
        });
        listPopup.show();
    }

    private void makeGeocodeSearch(final LatLng latLng) {
        try {
            // Build a Mapbox geocoding request
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken("pk.eyJ1IjoibWFyeWFtbW9ybmFndWkiLCJhIjoiY2s3dW84YThuMTB4YjNvbXJyc2hlNG5hayJ9.at86ELId-GCZVyB9I42X1g")
                    .query(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_PLACE)
                    .mode(GeocodingCriteria.MODE_PLACES)
                    .build();
            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call,
                                       Response<GeocodingResponse> response) {
                    if (response.body() != null) {
                        List<CarmenFeature> results = response.body().features();
                        if (results.size() > 0) {

                            // Get the first Feature from the successful geocoding response
                            CarmenFeature feature = results.get(0);
                            geocodeResultTextView.setText(feature.toString());
                            animateCameraToNewPosition(latLng);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.no_results,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    Timber.e("Geocoding Failure: " + throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            Timber.e("Error geocoding: " + servicesException.toString());
            servicesException.printStackTrace();
        }
    }

    private void animateCameraToNewPosition(LatLng latLng) {
        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(13)
                        .build()), 1500);
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}

