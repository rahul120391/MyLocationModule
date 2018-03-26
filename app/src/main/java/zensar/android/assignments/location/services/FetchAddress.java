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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zensar.android.assignments.location.model.GeocodeResult;
import zensar.android.assignments.location.networking.RetrofitAdapter;
import zensar.android.assignments.location.utility.Constants;
import zensar.android.assignments.R;
import zensar.android.assignments.location.utility.Utils;

import java.util.*;

/**
 * Created by RK51670 on 30-01-2018.
 */
public class FetchAddress extends IntentService {

    String mClassName = FetchAddress.class.getName();
    String exceptionMessage;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FetchAddress() {
        super("FetchAddress");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("oncreate", "inside oncreate of service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("onstartcommand", "inside onstartcommand of service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("onhandleintent", "inside onhandle intent");
        Double mLatitude = intent.getDoubleExtra(Constants.LATITUDE, 0.0);
        Double mLongitude = intent.getDoubleExtra(Constants.LONGITUDE, 0.0);
        final ResultReceiver mReciever = intent.getParcelableExtra(Constants.RECIEVER);
        if (mReciever == null) {
            Log.i(mClassName, "No reciever recieved");
            return;
        }
        if (mLatitude == null || mLongitude == null) {
            Log.i(mClassName, "location null");
            return;
        }
        if (Geocoder.isPresent()) {
            Log.i("ispresent", "yes");
            Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
            List<Address> mAddressList = new ArrayList<>();
            try {
                mAddressList = mGeocoder.getFromLocation(mLatitude, mLongitude, 1);
            } catch (Exception e) {
                if (e != null && e.getMessage() != null) {
                    Log.e(mClassName, e.getMessage());
                    exceptionMessage = e.getMessage();
                } else {
                    exceptionMessage = getResources().getString(R.string.address_not_found);
                }
                Log.i("exception message", exceptionMessage);
                deliverResultToReciever(Constants.FAILURE_RESULT, exceptionMessage, mReciever);
            }

            String address = "";
            if (mAddressList != null && !mAddressList.isEmpty()) {
                for (int i = 0; i < mAddressList.size(); i++) {
                    if (mAddressList.get(i).getAddressLine(i) != null) {
                        address = mAddressList.get(i).getAddressLine(i);
                        break;
                    }
                }
                Log.i("address", address);
                deliverResultToReciever(Constants.SUCCESS_RESULT, address, mReciever);
            } else {
                exceptionMessage = getResources().getString(R.string.address_not_found);
                Log.i("address", exceptionMessage);
                deliverResultToReciever(Constants.FAILURE_RESULT, exceptionMessage, mReciever);
            }
        } else {
            if (Utils.checkConnectivity(this)) {
                Map<String, String> map = new HashMap<>();
                map.put(Constants.KEY, getResources().getString(R.string.google_api_key));
                map.put(Constants.LAT_LNG, mLatitude + "," + mLongitude);
                RetrofitAdapter.create().getGeocodeResult(map).enqueue(new Callback<GeocodeResult>() {
                    @Override
                    public void onResponse(Call<GeocodeResult> call, Response<GeocodeResult> response) {
                        if (response != null && response.isSuccessful() && response.body() != null
                                && response.body().getResults() != null && response.body().getResults().get(0).getFormattedAddress() != null
                                ) {
                            deliverResultToReciever(Constants.SUCCESS_RESULT, response.body().getResults().get(0).getFormattedAddress(), mReciever);
                        }
                    }

                    @Override
                    public void onFailure(Call<GeocodeResult> call, Throwable t) {
                        String error = Constants.ERROR_FETCHING_DATA;
                        if (t != null && t.getMessage() != null) {
                            error = t.getMessage();
                        }
                        deliverResultToReciever(Constants.FAILURE_RESULT, error, mReciever);
                    }
                });
            } else {
                Utils.showToast(this, getString(R.string.network_error));
            }

        }

    }

    /**
     * Deliver message(address/exception message) recieved from geocoder api to AddressResultReciever class
     * Same function has been used to deliver Success and Failure result
     */
    private void deliverResultToReciever(int resultCode, String message, ResultReceiver mResultReciever) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MESSAGE, message);
        mResultReciever.send(resultCode, bundle);
    }
}


