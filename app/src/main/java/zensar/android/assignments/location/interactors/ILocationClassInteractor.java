package zensar.android.assignments.location.interactors;

/**
 * Created by RK51670 on 30-01-2018.
 */
public interface ILocationClassInteractor {

    void checkLocationSettings();
    void createLocationRequest();
    void startLocationUpdates();
    void stopLocationUpdates();
    void setUpFusedApiClient();
    void openAppSettings();
    void getLastKnownLocation();
    void checkGooglePlayService();
}