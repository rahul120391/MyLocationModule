package zensar.android.assignments.location.interactors;

import android.location.Location;

/**
 * Created by RK51670 on 30-01-2018.
 */
public interface ILocationManagerToActivity {

    void getLocation(Location location);
    void fetchLatLngFromAddress(String address);
    void startAddressFetch(Location location);
}