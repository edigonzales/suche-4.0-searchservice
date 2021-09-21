package ch.so.agi;

import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeatureResult extends SearchResult {
    private BigInteger featureId; 
        
    private String idFieldName;
    
    private boolean idFieldType = false;
    
    List<Integer> bbox;

    public BigInteger getFeatureId() {
        return featureId;
    }

    public void setFeatureId(BigInteger featureId) {
        this.featureId = featureId;
    }
 
    public String getIdFieldName() {
        return idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }

    public boolean isIdFieldType() {
        return idFieldType;
    }

    public void setIdFieldType(boolean idFieldType) {
        this.idFieldType = idFieldType;
    }

    public List<Integer> getBbox() {
        return bbox;
    }

    public void setBbox(List<Integer> bbox) {
        this.bbox = bbox;
    }
}
