package zensar.android.assignments.location.interactors;

import android.location.Location;

/**
 * Created by RK51670 on 31-01-2018.
 */
public interface ILocationInteractor {
    void getLatLng(Location location);
    void onLocationFetchError(String message);
}