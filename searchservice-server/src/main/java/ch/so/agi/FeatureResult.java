package ch.so.agi;

import java.util.List;

public class FeatureResult extends SearchResult {
    private String featureId; 
        
    private String idFieldName;
    
    private boolean idFieldType = false;
    
    List<Integer> bbox;

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
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
