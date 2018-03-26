package zensar.android.assignments.location.interactors;

/**
 * Created by RK51670 on 30-01-2018.
 */
public interface ILocationAddressClassInteractor {
    void getAddressFromLocation(String address);
    void onAddressFetchError(String errorMessage);
}