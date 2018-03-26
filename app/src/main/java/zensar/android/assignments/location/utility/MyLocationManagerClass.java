package zensar.android.assignments.location.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import zensar.android.assignments.location.services.AddressResultReciever;

import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import zensar.android.assignments.location.interactors.ILocationAddressClassInteractor;
import zensar.android.assignments.location.interactors.ILocationClassInteractor;
import zensar.android.assignments.location.interactors.ILocationInteractor;
import zensar.android.assignments.location.interactors.ILocationManagerToActivity;
import zensar.android.assignments.location.services.FetchAddress;
import zensar.android.assignments.location.services.FetchLatitudeLongitude;
import zensar.android.assignments.location.services.LatLngReciever;
import zensar.android.assignments.R;


/**
 * Created by RK51670 on 29-01-2018.
 */
public class MyLocationManagerClass extends LocationCallback
        implements OnSuccessListener<LocationSettingsResponse>,
        OnFailureListener,
        ILocationClassInteractor
{
    LocationRequest mLocationRequest;
    LocationSettingsRequest.Builder mBuilder;
    Context mActivityContext;
    FusedLocationProviderClient mFusedLocationApiClient;
    ILocationManagerToActivity mLocationActivity;
    Location mCurrentLocation;
    Intent mAddressServiceIntent;
    Intent mLocationServiceIntent;


    public MyLocationManagerClass(Context context, ILocationManagerToActivity mLocationActivityInteractor) {
        mActivityContext = context;
        mLocationActivity = mLocationActivityInteractor;
    }

    @Override
    public void checkGooglePlayService() {
        if (LocationUtils.checkPlayServicesAvailbility(mActivityContext) == Constants.TRUE) {
            if (LocationUtils.checkApiLevelForRuntimePermission(mActivityContext)) {
                createLocationRequest();
            }
        } else if (LocationUtils.checkPlayServicesAvailbility(mActivityContext) == Constants.FALSE) {
            ((Activity) mActivityContext).finish();
        }
    }

    @Override
    public void getLastKnownLocation() {
        try {
            if (mFusedLocationApiClient != null) {
                int checkPermission = ContextCompat.checkSelfPermission(mActivityContext, Manifest.permission.ACCESS_FINE_LOCATION);
                if (checkPermission == PackageManager.PERMISSION_GRANTED) {
                    Task<Location> lastKnownLocation = mFusedLocationApiClient.getLastLocation();
                    mCurrentLocation = lastKnownLocation.getResult();

                }
            }
        } catch (SecurityException e) {
            Log.i(Constants.ERROR_MESSAGE, e.getMessage());
        }

    }

    @Override
    public void onLocationResult(LocationResult locationReq) {
        if (locationReq != null) {
            for (Location location : locationReq.getLocations()) {
                mCurrentLocation = location;
            }
        } else {
            getLastKnownLocation();
        }
        if (mCurrentLocation != null) {
            mLocationActivity.getLocation(mCurrentLocation);
            stopLocationUpdates();
        }
    }

    @Override
    public void checkLocationSettings() {
        if (mBuilder == null) {
            mBuilder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest).
                    setAlwaysShow(true);
        }
        SettingsClient settingClient = LocationServices.getSettingsClient(mActivityContext);
        Task<LocationSettingsResponse> task = settingClient.checkLocationSettings(mBuilder.build());
        task.addOnSuccessListener(this);
        task.addOnFailureListener(this);
    }

    @Override
    public void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
        }
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        checkLocationSettings();
    }

    @Override
    public void onFailure(Exception e) {
        if (e instanceof ResolvableApiException) {
            try {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                resolvable.startResolutionForResult(((Activity) mActivityContext), Constants.REQUEST_CHECK_SETTINGS);
            } catch (Exception exception) {
                Log.i(Constants.ERROR_MESSAGE, exception.getMessage());
            }
        }
    }

    @Override
    public void onSuccess(LocationSettingsResponse p0) {
        startLocationUpdates();
    }

    @Override
    public void startLocationUpdates() {
        try {
            setUpFusedApiClient();
            mFusedLocationApiClient.requestLocationUpdates(mLocationRequest, this, Looper.myLooper());
        } catch (SecurityException e) {
            Log.i(Constants.ERROR_MESSAGE, e.getMessage());
        }
    }

    @Override
    public void stopLocationUpdates() {
        if (mFusedLocationApiClient != null) {
            mFusedLocationApiClient.removeLocationUpdates(this);
            if (mAddressServiceIntent != null) {
                mActivityContext.stopService(mAddressServiceIntent);
            }
        }
    }

    @Override
    public void setUpFusedApiClient() {
        if (mFusedLocationApiClient == null) {
            mFusedLocationApiClient = LocationServices.getFusedLocationProviderClient(mActivityContext);
        }
    }

    @Override
    public void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mActivityContext.getPackageName(), null);
        intent.setData(uri);
        ((Activity) mActivityContext).startActivityForResult(intent, Constants.OPEN_MOBILE_APP_SETTINGS);
    }

}