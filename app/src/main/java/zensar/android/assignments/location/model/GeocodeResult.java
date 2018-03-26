package zensar.android.assignments.location.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeocodeResult {

    @SerializedName("results")
    @Expose
    private List<GeoAddressResult> results = null;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    public List<GeoAddressResult> getResults() {
        return results;
    }

    public void setResults(List<GeoAddressResult> results) {
        this.results = results;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}