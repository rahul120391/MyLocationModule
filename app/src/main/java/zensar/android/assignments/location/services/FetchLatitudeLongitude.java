package zensar.android.assignments.location.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import zensar.android.assignments.location.utility.Constants;
import zensar.android.assignments.R;

import java.util.*;

/**
 * Created by RK51670 on 30-01-2018.
 */
public class FetchLatitudeLongitude extends IntentService {

    String mClassName = FetchAddress.class.getName();
    String exceptionMessage;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchLatitudeLongitude() {
        super("FetchLatitudeLongitude");
    }


    /**
     * Deliver  location recieved from geocoder api to LatLngReciever class
     */
    private void deliverSuccessResultToReciever(Location location, ResultReceiver resultReceiver) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.LOCATION, location);
        resultReceiver.send(Constants.SUCCESS_RESULT, bundle);
    }

    /**
     * Deliver  failure message  recieved from geocoder api to LatLngReciever class
     */
    private void deliverFailureResultToReciever(String errorMessage, ResultReceiver resultReceiver) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MESSAGE, errorMessage);
        resultReceiver.send(Constants.FAILURE_RESULT, bundle);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String mAddress = intent.getStringExtra(Constants.LOCATION_ADDRESS);
        ResultReceiver mReciever = intent.getParcelableExtra(Constants.RECIEVER);
        if (mReciever == null) {
            Log.i(mClassName, "No reciever recieved");
            return;
        }
        if (mAddress == null) {
            Log.i(mClassName, "Not a Proper Address");
            return;
        }
        Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
        List<Address> mAddressList = new ArrayList<>();
        try {
            mAddressList = mGeocoder.getFromLocationName(mAddress, 1);
        } catch (Exception e) {
            if (e != null && e.getMessage() != null) {
                Log.e(mClassName, e.getMessage());
                exceptionMessage = e.getMessage();
            } else {
                exceptionMessage = getResources().getString(R.string.address_not_found);
            }
            deliverFailureResultToReciever(exceptionMessage, mReciever);
        }

        if (mAddressList != null && !mAddressList.isEmpty()) {
            Location location = new Location("");
            location.setLatitude(mAddressList.get(0).getLatitude());
            location.setLongitude(mAddressList.get(0).getLongitude());
            deliverSuccessResultToReciever(location, mReciever);
        } else {
            exceptionMessage = getResources().getString(R.string.address_not_found);
            deliverFailureResultToReciever(exceptionMessage, mReciever);
        }
    }
}