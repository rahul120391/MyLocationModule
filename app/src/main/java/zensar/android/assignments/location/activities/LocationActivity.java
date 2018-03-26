package zensar.android.assignments.location.activities;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import zensar.android.assignments.R;
import zensar.android.assignments.location.utility.Constants;
import zensar.android.assignments.location.utility.Utils;

import static zensar.android.assignments.location.utility.Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE;

public class LocationActivity extends BaseActivity {

    /**
     * Variable declaration
     */
    AppCompatTextView mTvCurrentLatitude;
    AppCompatTextView mTvCurrentLongitude;
    AppCompatTextView mTvCurrentAddress;
    AppCompatEditText mEtLatitude;
    AppCompatEditText mEtLongitude;
    AppCompatEditText mEtLocationAddress;
    AppCompatTextView mTvAddress;
    AppCompatTextView mTvLatitude;
    AppCompatTextView mTvLongitude;
    AppCompatButton mBtnFetchLocation;
    AppCompatButton mBtnFetchAddress;
    AppCompatButton mBtnFetchLatLng;
    Toolbar mToolBar;

    int position = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initViews();
        fetchAddressFromLatLng();
    }

    /**
     * Intialize views here
     */
    private void initViews() {
        mTvCurrentLatitude = findViewById(R.id.tvCurrentLatitude);
        mTvCurrentLongitude = findViewById(R.id.tvCurrentLongitude);
        mTvCurrentAddress = findViewById(R.id.tvCurrentAddress);
        mEtLatitude = findViewById(R.id.etLatitude);
        mEtLongitude = findViewById(R.id.etLongitude);
        mEtLocationAddress=findViewById(R.id.etAddress);
        mTvLatitude = findViewById(R.id.tvLatitude);
        mTvLongitude = findViewById(R.id.tvLongitude);
        mTvAddress = findViewById(R.id.tvAddress);
        mBtnFetchAddress = findViewById(R.id.btnFetchAddress);
        mBtnFetchLocation = findViewById(R.id.btnFetchLocation);
        mBtnFetchLatLng=findViewById(R.id.btnFetchLatLng);
        mToolBar = findViewById(R.id.toolBar);
        mToolBar.setTitle(getString(R.string.location));
        mToolBar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBtnFetchLatLng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(LocationActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                    e.printStackTrace();
                }
            }

        });
    }


    /**
     * Fetch Location from Latitude & Longitude
     */
    private void fetchAddressFromLatLng() {
        mBtnFetchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latitude;
                double longitude;
                if (mEtLatitude.getText().toString().trim().isEmpty()) {
                    Utils.showToast(LocationActivity.this, getString(R.string.invalid_input));
                    return;
                } else {
                    double num = Double.parseDouble(mEtLatitude.getText().toString().trim());
                    if (num == Math.floor(num)) {
                        Utils.showToast(LocationActivity.this, getString(R.string.invalid_input));
                        return;
                    } else {
                        latitude = num;
                    }
                }
                if (mEtLongitude.getText().toString().trim().isEmpty()) {
                    Utils.showToast(LocationActivity.this, getString(R.string.invalid_input));
                    return;
                } else {
                    double num = Double.parseDouble(mEtLongitude.getText().toString().trim());
                    if (num == Math.floor(num)) {
                        Utils.showToast(LocationActivity.this, getString(R.string.invalid_input));
                        return;
                    } else {
                        longitude = num;
                    }
                }
                position = 2;
                Location location = new Location("");
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                startAddressFetch(location);

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            String address = place.getAddress().toString();
            mEtLocationAddress.setText(address);
            mTvLatitude.setText(getString(R.string.fetch_latitude) + "  " + place.getLatLng().latitude);
            mTvLongitude.setText(getString(R.string.fetch_longitude) + "  " + place.getLatLng().longitude);
        }
    }

    /**
     * This function is called on current location recieved from base activity
     */
    @Override
    public void getLocation(Location location) {
        super.getLocation(location);
        super.startAddressFetch(location);
        position = 1;
        mTvCurrentLatitude.setText(getString(R.string.latitude) + "  " + " : " + "  " + location.getLatitude());
        mTvCurrentLongitude.setText(getString(R.string.longitude) + " " + " : " + "  " + location.getLongitude());
    }

    @Override
    public void getAddressFromLocation(String address) {
        super.getAddressFromLocation(address);
        if (position == 1) {
            mTvCurrentAddress.setText(getString(R.string.address) + "  " + " : " + "  " + address);
        } else if (position == 2) {
            position = 1;
            mTvAddress.setText(getString(R.string.addr) + "  " + " : " + "  " + address);
        }
    }

    @Override
    public void onAddressFetchError(String errorMessage) {
        super.onAddressFetchError(errorMessage);
    }

    /**
     * This function gets called on location recieved from address eneterd in the location field
     */
    @Override
    public void getLatLng(Location location) {
        super.getLatLng(location);
        if (location != null) {
            mTvLatitude.setText(getString(R.string.fetch_latitude) + "  " + location.getLatitude());
            mTvLongitude.setText(getString(R.string.fetch_longitude) + "  " + location.getLongitude());
        }
    }
}
