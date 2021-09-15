package ch.so.agi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeatureResult {
    String featureId; // cast to bigint. Momentan wird id nicht verwendet, nur notwendig für dataservice.
    
    //@JsonProperty("dataproduct_id")
    String dataproductId;
    
    String display;
    
    List<Integer> bbox;

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    public String getDataproductId() {
        return dataproductId;
    }

    public void setDataproductId(String dataproductId) {
        this.dataproductId = dataproductId;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public List<Integer> getBbox() {
        return bbox;
    }

    public void setBbox(List<Integer> bbox) {
        this.bbox = bbox;
    }
}
