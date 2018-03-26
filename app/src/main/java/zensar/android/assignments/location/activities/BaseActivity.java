package zensar.android.assignments.location.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import zensar.android.assignments.location.interactors.ILocationAddressClassInteractor;
import zensar.android.assignments.location.interactors.ILocationInteractor;
import zensar.android.assignments.location.interactors.ILocationManagerToActivity;
import zensar.android.assignments.location.interactors.IPermissionResultTransfer;
import zensar.android.assignments.location.services.AddressResultReciever;
import zensar.android.assignments.location.services.FetchAddress;
import zensar.android.assignments.location.services.FetchLatitudeLongitude;
import zensar.android.assignments.location.services.LatLngReciever;
import zensar.android.assignments.location.utility.*;
import zensar.android.assignments.R;


public class BaseActivity extends AppCompatActivity implements
        ILocationManagerToActivity, IPermissionResultTransfer,ILocationAddressClassInteractor,ILocationInteractor {


    MyLocationManagerClass mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mLocationManager == null) {
            mLocationManager = new MyLocationManagerClass(this, this);
        }
        mLocationManager.checkGooglePlayService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && mLocationManager != null) {
            mLocationManager.createLocationRequest();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && mLocationManager != null) {
            mLocationManager.stopLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_ACCESS_LOCATION && grantResults != null) {
            if (grantResults.length <= 0) {
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Utils.showAlertDialog(this, this);
                } else {
                    if (mLocationManager != null) {
                        Utils.showToast(this, getString(R.string.enable_loctaion));
                        mLocationManager.openAppSettings();
                    }
                }
            }
        } else {
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            mLocationManager.startLocationUpdates();
        } else if (requestCode == Constants.OPEN_MOBILE_APP_SETTINGS && resultCode == RESULT_CANCELED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Utils.showAlertDialog(this, this);
        }
    }

    /**
     * //This method will be called on receiving location from MyLocationManager class
     *
     * @param location
     */
    @Override
    public void getLocation(Location location) {
        if (location != null) {
            Log.i("location", location.toString());
        }
    }

    @Override
    public void fetchLatLngFromAddress(String address) {
        if (mLocationManager != null) {
            Intent mLocationServiceIntent = new Intent(this, FetchLatitudeLongitude.class);
            mLocationServiceIntent.putExtra(Constants.RECIEVER, new LatLngReciever(new Handler(),this));
            mLocationServiceIntent.putExtra(Constants.LOCATION_ADDRESS, address);
            startService(mLocationServiceIntent);
        }
    }

    @Override
    public void startAddressFetch(Location location) {
        if (mLocationManager != null) {
            Intent mAddressServiceIntent = new Intent(this, FetchAddress.class);
            mAddressServiceIntent.putExtra(Constants.RECIEVER, new AddressResultReciever(new Handler(), this));
            mAddressServiceIntent.putExtra(Constants.LATITUDE, location.getLatitude());
            mAddressServiceIntent.putExtra(Constants.LONGITUDE,location.getLongitude());
            startService(mAddressServiceIntent);
        }
    }



    @Override
    public void setResultCode(int code, Context context) {
        if (code == Constants.SUCCESS_RESULT) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_ACCESS_LOCATION);
        } else if (code == Constants.FAILURE_RESULT) {
            finish();
        }
    }

    @Override
    public void getAddressFromLocation(String address) {
        Log.i("address",address);
    }

    @Override
    public void onAddressFetchError(String errorMessage) {
        Log.i("error",errorMessage);
    }

    @Override
    public void getLatLng(Location location) {
        Log.i("locationfromaddress",location.toString());
    }

    @Override
    public void onLocationFetchError(String message) {
        Utils.showToast(this,getString(R.string.location_error));
    }
}
