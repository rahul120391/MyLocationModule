package zensar.android.assignments.location.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import zensar.android.assignments.location.interactors.ILocationAddressClassInteractor;
import zensar.android.assignments.location.utility.Constants;

/**
 * Created by RK51670 on 30-01-2018.
 */
public class AddressResultReciever extends ResultReceiver {

    ILocationAddressClassInteractor mLocationAddress;
    String mClassName=AddressResultReciever.class.getName();
    public AddressResultReciever(Handler handler, ILocationAddressClassInteractor locationAddress){
        super(handler);
        mLocationAddress=locationAddress;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if(resultCode== Constants.SUCCESS_RESULT){
            mLocationAddress.getAddressFromLocation(resultData.getString(Constants.MESSAGE));
        }
        else if(resultCode==Constants.FAILURE_RESULT){
            String errorMessage=resultData.getString(Constants.MESSAGE);
            mLocationAddress.onAddressFetchError(errorMessage);
            Log.e(mClassName,errorMessage);
        }
    }
}