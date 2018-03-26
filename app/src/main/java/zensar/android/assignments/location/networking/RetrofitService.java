package zensar.android.assignments.location.networking;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import zensar.android.assignments.location.model.GeocodeResult;
import zensar.android.assignments.location.model.Place;
import zensar.android.assignments.location.model.PlaceDetails;
import zensar.android.assignments.location.utility.Constants;


/**
 * Created by RK51670 on 05-02-2018.
 */
public interface RetrofitService {

    @GET(Constants.GOOGLE_PLACES_URL)
    Call<Place> getNearbyPlaces(@QueryMap Map<String,String> values);

    @GET(Constants.GOOGLE_GEOCODE_URL)
    Call<GeocodeResult> getGeocodeResult(@QueryMap Map<String,String> values);

    @GET(Constants.GOOGLE_PLACE_DETAIL_URL)
    Call<PlaceDetails> getPlaceDetails(@Query("placeid") String placeid, @Query("key") String key);
}