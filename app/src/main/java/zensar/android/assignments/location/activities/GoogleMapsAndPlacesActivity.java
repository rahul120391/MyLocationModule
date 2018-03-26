package zensar.android.assignments.location.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zensar.android.assignments.location.interactors.IMapsAndPlacesView;
import zensar.android.assignments.location.interactors.ITransferApiResult;
import zensar.android.assignments.location.model.Place;
import zensar.android.assignments.location.model.Result;
import zensar.android.assignments.location.networking.ApiClass;
import zensar.android.assignments.location.utility.Constants;
import zensar.android.assignments.location.utility.Utils;
import zensar.android.assignments.R;

import static zensar.android.assignments.location.utility.Constants.OPEN_SETTINGS;
import static zensar.android.assignments.location.utility.Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE;


public class GoogleMapsAndPlacesActivity extends BaseActivity implements IMapsAndPlacesView, OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, ITransferApiResult, View.OnClickListener {

    private AppCompatImageView mImgSettings, mImgSearch;
    private SupportMapFragment mMapFragment;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private GoogleMap mGoogleMap;
    private Toolbar mToolBar;
    private Location mCurrentLocation;
    private int mRadius = 10000;
    private String mPlaceType;
    private Marker mCurrentMarker;
    private ApiClass mApiClass;
    private FrameLayout mFlProgressBar;
    private List<Marker> mMarkerList = new ArrayList<>();
    private String mPreviousPlaceType;
    private int REQUEST=0;
    private MarkerOptions currentLocationMarker;
    private AppCompatTextView mTvDrawerHeader;
    private MenuItem item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps_and_places);
        initViews();
    }

    /**
     * Intialize Views Here
     */
    @Override
    public void initViews() {
        mFlProgressBar = findViewById(R.id.flProgressBar);
        mNavigationView = findViewById(R.id.mNavigationView);
        View view= mNavigationView.getHeaderView(0);
        mTvDrawerHeader=view.findViewById(R.id.tvDrawerHeader);
        mTvDrawerHeader.setText(getString(R.string.find_places_nearby));
        mImgSettings = findViewById(R.id.imgSettings);
        mImgSearch = findViewById(R.id.imgSearch);
        mImgSettings.setOnClickListener(this);
        mImgSearch.setOnClickListener(this);

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getChildAt(0).setVerticalScrollBarEnabled(false);
        mDrawerLayout = findViewById(R.id.mDrawerLayout);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mMapFragment.getMapAsync(this);
        mToolBar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
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
    public void showLoading() {
        mFlProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mFlProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        moveToCurrentLocation();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }

    @Override
    protected void onDestroy() {
        if (mApiClass != null) {
            mApiClass.cancelRequest();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Get Current location and display on map
     *
     * @param location
     */
    @Override
    public void getLocation(Location location) {
        if(!(REQUEST==PLACE_AUTOCOMPLETE_REQUEST_CODE || REQUEST==OPEN_SETTINGS)){
            mCurrentLocation = location;
            moveToCurrentLocation();
        }
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

    /**
     * Fetch nearby place selected data
     */
    @Override
    public void fetchPlaceData(MenuItem item) {
        if (mGoogleMap != null && mCurrentLocation != null) {
            if (mApiClass == null) {
                mApiClass = new ApiClass(this);
            }
            if (Utils.checkConnectivity(this) && mPlaceType != null) {
                showLoading();
                clearMarkers();
                mPreviousPlaceType = mPlaceType;
                Map map = new HashMap<String, String>();
                map.put(Constants.KEY, getString(R.string.google_api_key));
                map.put(Constants.TYPE, mPlaceType);
                map.put(Constants.LOCATION, mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                map.put(Constants.RADIUS, String.valueOf(mRadius));
                mApiClass.getPlace(map);
            } else {
                mPreviousPlaceType = "";
                if (mPlaceType == null) {
                    Utils.showToast(this, "Please select place from drawer");
                    return;
                }
                if(item!=null){
                    item.setChecked(false);
                }
                Utils.showToast(this, getString(R.string.network_error));
            }
        }
        else{
            if(item!=null){
                item.setChecked(false);
            }

        }
    }

    /**
     * Clears markers that are already displayed on map
     */
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.OPEN_SETTINGS && resultCode == RESULT_OK) {
            int newRadius = data.getIntExtra(Constants.RADIUS, 0) * 1000;
            if (mRadius != newRadius) {
                mRadius = newRadius;
                fetchPlaceData(item);
            }
        }
        else if(requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK){
            com.google.android.gms.location.places.Place place = PlaceAutocomplete.getPlace(this, data);
            if(item!=null){
                mPreviousPlaceType="";
                item.setChecked(false);
            }
            if(mGoogleMap!=null){
                mGoogleMap.clear();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(place.getLatLng())
                        .zoom(Constants.MAP_ZOOM_VALUE)
                        .build();
                Double latitude=place.getLatLng().latitude;
                Double longitude=place.getLatLng().longitude;
                System.out.println(latitude+","+longitude);
                currentLocationMarker = new MarkerOptions().position(place.getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current_location));
                mCurrentMarker = mGoogleMap.addMarker(currentLocationMarker);
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
                REQUEST=PLACE_AUTOCOMPLETE_REQUEST_CODE;
                Location location=new Location("");
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                mCurrentLocation=location;
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(mCurrentLocation!=null){
            mPlaceType = item.getTitle().toString().toLowerCase();
            mDrawerLayout.closeDrawer(Gravity.START);
            this.item=item;
            if (!mPlaceType.equalsIgnoreCase(mPreviousPlaceType)) {
                fetchPlaceData(item);
            }
            else{
                item.setChecked(false);
            }
        }
        else{
            item.setChecked(false);
        }
        return true;
    }


    @Override
    public void success(Object t) {
        hideLoading();
        if (t != null) {
            clearMarkers();
            Place place = (Place) t;
            if (place != null && place.getResults() != null && place.getResults().size() > 0) {

                for (Result result : place.getResults()) {
                    double latitude = result.getGeometry().getLocation().getLat();
                    double longitude = result.getGeometry().getLocation().getLng();
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location));
                    markerOptions.snippet(result.getPlaceId());
                    Marker marker = mGoogleMap.addMarker(markerOptions);
                    Log.i("place id", result.getPlaceId());
                    mMarkerList.add(marker);
                }

                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (Utils.checkConnectivity(GoogleMapsAndPlacesActivity.this)) {
                            if (!(mCurrentLocation.getLatitude() == marker.getPosition().latitude && mCurrentLocation.getLongitude() == marker.getPosition().longitude)) {
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.PLACE_ID, marker.getSnippet());
                                bundle.putString(Constants.TYPE, mPlaceType);
                                bundle.putString(Constants.FROM,Constants.GOOGLE_MAPS);
                                Utils.startAcivityWithData(GoogleMapsAndPlacesActivity.this, PlaceDetailsActivity.class, bundle);
                            }
                        } else {
                            Utils.showToast(GoogleMapsAndPlacesActivity.this, getString(R.string.network_error));
                        }
                        return false;
                    }
                });

            } else {
                mPreviousPlaceType = "";
                Utils.showToast(this, getString(R.string.no_result_found));
            }

        }
    }

    @Override
    public void failure(String message) {
        mPreviousPlaceType = "";
        Utils.showToast(this, message);
        hideLoading();
    }


    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.imgSettings) {
            REQUEST=OPEN_SETTINGS;
            int radiusInKm = mRadius / 1000;
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.RADIUS, radiusInKm);
            Utils.startAcivityForResultWithData(this, SettingsActivity.class, bundle, Constants.OPEN_SETTINGS);

        }
        else if (view.getId() == R.id.imgSearch) {
            try {
                REQUEST=PLACE_AUTOCOMPLETE_REQUEST_CODE;
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(GoogleMapsAndPlacesActivity.this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }
}
