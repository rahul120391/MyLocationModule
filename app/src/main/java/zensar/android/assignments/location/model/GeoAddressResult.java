package zensar.android.assignments.location.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by RK51670 on 16-02-2018.
 */

public class GeoAddressResult {

    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }
}
