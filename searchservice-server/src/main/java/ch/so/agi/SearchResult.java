package ch.so.agi;

import java.util.List;

public class SearchResult {
    List<FeatureResult> features;
    List<DataproductResult> dataproducts;
    
    public List<FeatureResult> getFeatures() {
        return features;
    }
    public void setFeatures(List<FeatureResult> features) {
        this.features = features;
    }
    public List<DataproductResult> getDataproducts() {
        return dataproducts;
    }
    public void setDataproducts(List<DataproductResult> dataproducts) {
        this.dataproducts = dataproducts;
    }
}
