package ch.so.agi;

import java.util.List;

public class DataproductResult extends SearchResult {
//    private String dataproductId;
//    private String display;
    private boolean destInfo;
    private String stacktype;
    private String type;
    private List<DataproductResult> sublayers;
    
//    public String getDataproductId() {
//        return dataproductId;
//    }
//    public void setDataproductId(String dataproductId) {
//        this.dataproductId = dataproductId;
//    }
//    public String getDisplay() {
//        return display;
//    }
//    public void setDisplay(String display) {
//        this.display = display;
//    }
    public boolean isDestInfo() {
        return destInfo;
    }
    public void setDestInfo(boolean destInfo) {
        this.destInfo = destInfo;
    }
    public String getStacktype() {
        return stacktype;
    }
    public void setStacktype(String stacktype) {
        this.stacktype = stacktype;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public List<DataproductResult> getSublayers() {
        return sublayers;
    }
    public void setSublayers(List<DataproductResult> sublayers) {
        this.sublayers = sublayers;
    }
}
