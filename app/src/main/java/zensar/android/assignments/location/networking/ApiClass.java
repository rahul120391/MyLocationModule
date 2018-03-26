package zensar.android.assignments.location.networking;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zensar.android.assignments.location.interactors.ITransferApiResult;
import zensar.android.assignments.location.model.Place;
import zensar.android.assignments.location.model.PlaceDetails;
import zensar.android.assignments.location.utility.Constants;

/**
 * Created by RK51670 on 07-02-2018.
 */
public class ApiClass {


    ITransferApiResult mTransferResult;
    RetrofitService retrofitService = null;


    public ApiClass(ITransferApiResult mITransferApiResult) {
        retrofitService = RetrofitAdapter.create();
        mTransferResult=mITransferApiResult;
    }

    public void getPlace(Map<String, String> values) {
        Call<Place> placeRequest = retrofitService.getNearbyPlaces(values);
        placeRequest.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                mTransferResult.success(response.body());
            }

            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                String error = Constants.ERROR_FETCHING_DATA;
                if (t != null && t.getMessage() != null) {
                    error = t.getMessage();
                }
                mTransferResult.failure(error);
            }
        });
    }

    public void getPlaceDetails(String placeid,String key){
        Call<PlaceDetails> placeRequest=retrofitService.getPlaceDetails(placeid,key);
        placeRequest.enqueue(new Callback<PlaceDetails>() {
            @Override
            public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                mTransferResult.success(response.body());
            }

            @Override
            public void onFailure(Call<PlaceDetails> call, Throwable t) {
                String error = Constants.ERROR_FETCHING_DATA;
                if (t != null && t.getMessage() != null) {
                    error = t.getMessage();
                }
                mTransferResult.failure(error);
            }
        });
    }

    /**
     * Cancel running repoRequest
     */
    public void cancelRequest() {
        RetrofitAdapter.cancelRetrofitRequest();
    }


}