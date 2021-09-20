package ch.so.agi;

import java.util.List;

public abstract class SearchResult {
    private String dataproductId;
    
    private String display;

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
}
