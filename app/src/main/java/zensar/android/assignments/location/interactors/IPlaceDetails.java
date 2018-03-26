package zensar.android.assignments.location.interactors;

/**
 * Created by RK51670 on 20-02-2018.
 */

public interface IPlaceDetails {
    void initViews();
    void hideLoading();
    void showLoading();
    void fetchPlaceDetails();
}
