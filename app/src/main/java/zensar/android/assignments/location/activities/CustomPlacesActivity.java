package zensar.android.assignments.location.activities;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import zensar.android.assignments.R;
import zensar.android.assignments.location.interactors.ICustomPlacesView;
import zensar.android.assignments.location.interactors.ITransferApiResult;
import zensar.android.assignments.location.model.CustomPlacesData;
import zensar.android.assignments.location.model.Prediction;
import zensar.android.assignments.location.model.Result;
import zensar.android.assignments.location.networking.ApiClass;
import zensar.android.assignments.location.utility.Constants;
import zensar.android.assignments.location.utility.Utils;

import static zensar.android.assignments.location.utility.Constants.OPEN_SETTINGS;
import static zensar.android.assignments.location.utility.Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE;

public class CustomPlacesActivity extends BaseActivity implements ICustomPlacesView, OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, ITransferApiResult, View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolBar;
    private AppCompatImageView mImgSettings;
    private AppCompatImageView mImgSearch;
    private FrameLayout mFlProgressBar;
    private SupportMapFragment mMapFragment;
    private NavigationView mNavigationView;
    private int mRadius = 10000;
    private String mPlaceType;
    private Marker mCurrentMarker;
    private ApiClass mApiClass;
    private List<Marker> mMarkerList = new ArrayList<>();
    private String mPreviousPlaceType;
    private int REQUEST = 0;
    private MarkerOptions currentLocationMarker;
    private GoogleMap mGoogleMap;
    private Location mCurrentLocation;
    private AppCompatTextView mTvDrawerHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_places);
        initViews();
    }

    @Override
    public void initViews() {
        mFlProgressBar = findViewById(R.id.flProgressBar);
        mNavigationView = findViewById(R.id.mNavigationView);
        View view= mNavigationView.getHeaderView(0);
        mTvDrawerHeader=view.findViewById(R.id.tvDrawerHeader);
        mTvDrawerHeader.setText(getString(R.string.mybranches));
        mImgSettings = findViewById(R.id.imgSettings);
        mImgSearch = findViewById(R.id.imgSearch);
        //  mImgSettings.setOnClickListener(this);
        // mImgSearch.setOnClickListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getChildAt(0).setVerticalScrollBarEnabled(false);
        mDrawerLayout = findViewById(R.id.mDrawerLayout);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mMapFragment.getMapAsync(this);
        mToolBar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void hideLoading() {
        mFlProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        mFlProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(Gravity.START);
        loadJson(item.getTitle().toString());
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgSettings) {
            int radiusInKm = mRadius / 1000;
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.RADIUS, radiusInKm);
            Utils.startAcivityForResultWithData(this, SettingsActivity.class, bundle, Constants.OPEN_SETTINGS);

        } else if (view.getId() == R.id.imgSearch) {
            try {
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(CustomPlacesActivity.this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void success(Object object) {

    }

    @Override
    public void failure(String message) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.OPEN_SETTINGS && resultCode == RESULT_OK) {

        } else if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {

        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        moveToCurrentLocation();
    }

    @Override
    public void moveToCurrentLocation() {
        if (mGoogleMap != null && mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(Constants.MAP_ZOOM_VALUE)
                    .build();
            currentLocationMarker = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location));
            if (mCurrentMarker != null) {
                mCurrentMarker.remove();
            }
            mCurrentMarker = mGoogleMap.addMarker(currentLocationMarker);
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        }
    }

    @Override
    public void loadJson(String title) {
        InputStream raw = getResources().openRawResource(R.raw.jsonfile);
        Reader rd = new BufferedReader(new InputStreamReader(raw));
        Gson gson = new Gson();
        CustomPlacesData data = gson.fromJson(rd, CustomPlacesData.class);
        clearMarkers();
        for (Prediction prediction : data.getPredictions()) {
            String desc[]=prediction.getDescription().split("\\|");
            String name=desc[0].trim();
            double latitude = Double.valueOf(prediction.getLat());
            double longitude = Double.valueOf(prediction.getLng());
            if(title.equalsIgnoreCase(name)){
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude))
                        .snippet(prediction.getDescription()+"@"+prediction.getRating()+"")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location));
                Marker marker = mGoogleMap.addMarker(markerOptions);
                mMarkerList.add(marker);
            }


        }

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!(mCurrentLocation.getLatitude() == marker.getPosition().latitude && mCurrentLocation.getLongitude() == marker.getPosition().longitude)) {
                    Bundle bundle = new Bundle();
                    String snippet=marker.getSnippet();
                    String data[]=snippet.split("@");
                    bundle.putString(Constants.RATING,data[1]);
                    String name_desc[]=data[0].split("\\|");
                    String name=name_desc[0];
                    String desc=name_desc[1];
                    bundle.putString(Constants.NAME,name);
                    bundle.putString(Constants.DESC,desc);
                    bundle.putString(Constants.FROM, Constants.CUSTOM_MAPS);
                    Utils.startAcivityWithData(CustomPlacesActivity.this, PlaceDetailsActivity.class, bundle);
                }
                return false;
            }
        });

    }

    @Override
    public void clearMarkers() {
        if (mMarkerList != null) {
            for (Marker marker : mMarkerList) {
                marker.remove();
            }
            mMarkerList.clear();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Get Current location and display on map
     *
     * @param location
     */
    @Override
    public void getLocation(Location location) {
        if (REQUEST != PLACE_AUTOCOMPLETE_REQUEST_CODE || REQUEST != OPEN_SETTINGS) {
            mCurrentLocation = location;
            moveToCurrentLocation();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }
}
