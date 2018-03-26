package zensar.android.assignments.location.interactors;

/**
 * Created by RK51670 on 07-02-2018.
 */
public interface ITransferApiResult {
    void success(Object object);
    void failure(String message);
}