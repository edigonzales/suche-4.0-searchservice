package ch.so.agi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class SearchResult {
    private String dataproductId;
    
    private String display;
    
    private String weight;

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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
