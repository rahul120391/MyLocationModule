package zensar.android.assignments.location.interactors;

import android.view.MenuItem;

/**
 * Created by RK51670 on 12-02-2018.
 */
public interface IMapsAndPlacesView {

    void showLoading();
    void hideLoading();
    void initViews();
    void moveToCurrentLocation();
    void fetchPlaceData(MenuItem item);
    void clearMarkers();
}