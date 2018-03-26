package zensar.android.assignments.location.services;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import zensar.android.assignments.location.interactors.ILocationInteractor;
import zensar.android.assignments.location.utility.Constants;

/**
 * Created by RK51670 on 30-01-2018.
 */
public class LatLngReciever extends ResultReceiver {


    ILocationInteractor mLocationTransfer;
    String mClassName=AddressResultReciever.class.getName();
    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public LatLngReciever(Handler handler,ILocationInteractor location) {
        super(handler);
        mLocationTransfer=location;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if(resultCode==Constants.SUCCESS_RESULT){
            mLocationTransfer.getLatLng((Location) resultData.getParcelable(Constants.LOCATION));
        }
        else if(resultCode==Constants.FAILURE_RESULT){
            String errorMessage=resultData.getString(Constants.MESSAGE);
            mLocationTransfer.onLocationFetchError(errorMessage);
            Log.e(mClassName,errorMessage);
        }
    }
}