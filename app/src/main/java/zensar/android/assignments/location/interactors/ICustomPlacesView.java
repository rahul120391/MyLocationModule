package zensar.android.assignments.location.interactors;

/**
 * Created by RK51670 on 14-03-2018.
 */

public interface ICustomPlacesView {
    void initViews();
    void hideLoading();
    void showLoading();
    void moveToCurrentLocation();
    void loadJson(String title);
    void clearMarkers();
}
