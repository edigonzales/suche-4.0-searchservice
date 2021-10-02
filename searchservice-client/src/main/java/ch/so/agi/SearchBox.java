package ch.so.agi;

import static elemental2.dom.DomGlobal.console;
import static org.jboss.elemento.Elements.body;
import static org.jboss.elemento.Elements.div;
import static org.jboss.elemento.Elements.img;
import static org.jboss.elemento.Elements.span;
import static org.dominokit.domino.ui.style.Unit.px;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.dominokit.domino.ui.button.Button;
import org.dominokit.domino.ui.button.ButtonSize;
import org.dominokit.domino.ui.dropdown.DropDownMenu;
import org.dominokit.domino.ui.forms.SuggestBox;
import org.dominokit.domino.ui.forms.SuggestBoxStore;
import org.dominokit.domino.ui.forms.SuggestBoxStore.SuggestionsHandler;
import org.dominokit.domino.ui.forms.SuggestItem;
import org.dominokit.domino.ui.forms.CheckBox;
import org.dominokit.domino.ui.grid.Row;
import org.dominokit.domino.ui.grid.Column.Span;
import org.dominokit.domino.ui.grid.Column;
import org.dominokit.domino.ui.forms.AbstractSuggestBox.DropDownPositionDown;
import org.dominokit.domino.ui.icons.Icon;
import org.dominokit.domino.ui.icons.Icons;
import org.dominokit.domino.ui.icons.MdiIcon;
import org.dominokit.domino.ui.style.Color;
import org.dominokit.domino.ui.utils.HasSelectionHandler.SelectionHandler;
import org.jboss.elemento.IsElement;

import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import ol.layer.Base;
import ol.layer.Image;
import ol.Coordinate;
import ol.Extent;
import ol.OLFactory;
import ol.View;
import ol.layer.LayerOptions;
import ol.source.ImageWms;
import ol.source.ImageWmsOptions;
import ol.source.ImageWmsParams;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dev.javac.testing.Source;

public class SearchBox implements IsElement<HTMLElement> {

    private final HTMLElement root;
    private ol.Map map;
    private SuggestBox suggestBox;
    private List<String> wmsLayers = new ArrayList<>();
    
    private String ID_ATTR_NAME = "id";
    private int MAX_SEARCH_RESULTS = 15;
    
    CheckBox cbBienen = CheckBox.create("Bienen").setColor(Color.RED_DARKEN_3);
    CheckBox cbQuellen = CheckBox.create("Quellen").setColor(Color.RED_DARKEN_3);
    CheckBox cbSondierung = CheckBox.create("Sondierung").setColor(Color.RED_DARKEN_3);
    CheckBox cbVollzugsmeldung = CheckBox.create("Vollzugsmeldung").setColor(Color.RED_DARKEN_3);
    CheckBox cbMutation = CheckBox.create("Mutation").setColor(Color.RED_DARKEN_3);
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public SearchBox(ol.Map map) {
        this.map = map;
        
        root = div().id("searchBox").element();
        
        HTMLElement logoDiv = div().id("logoDiv")
                .add(img()
                        .attr("src", GWT.getHostPageBaseURL() + "logo.png")
                        .attr("alt", "Logo Kanton Solothurn").attr("width", "50%"))
                .element();
        root.appendChild(logoDiv);
        
        SuggestBoxStore dynamicStore = new SuggestBoxStore() {
            @Override
            public void filter(String value, SuggestionsHandler suggestionsHandler) {
                if (value.trim().length() == 0) {
                    return;
                }

                String searchString = "search?searchtext="+value.trim().toLowerCase() + "&filter=foreground,ch.so.agi.av.bodenbedeckung,ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge,ch.so.agi.av.grundstuecke.rechtskraeftig,ch.so.agi.av.nomenklatur.flurnamen";
                
                if (cbBienen.isChecked()) {
                    searchString += "ch.so.alw.bienenstandorte_und_sperrgebiete.bienenstandorte,";
                } 
                if (cbQuellen.isChecked()) {
                    searchString += "ch.so.afu.gewaesserschutz.quellen,";
                } 
                if (cbSondierung.isChecked()) {
                    searchString += "ch.so.afu.wasserbewirtschaftung.sondierung,";
                }
                if (cbVollzugsmeldung.isChecked()) {
                    searchString += "ch.so.agi.av.gb2av.controlling_vollzugsmeldungen,";
                }
                if (cbMutation.isChecked()) {
                    searchString += "ch.so.agi.av.gb2av.controlling_mutationen,";
                } 
                
                if (searchString.charAt(searchString.length()-1) == ',') {
                    searchString = searchString.substring(0, searchString.length() - 1);
                }
                
                //console.log(searchString);
                
                DomGlobal.fetch(searchString)
                .then(response -> {
                    if (!response.ok) {
                        return null;
                    }
                    return response.text();
                })
                .then(json -> {
//                    console.log(json);
                    List<SuggestItem<SearchResult>> featureResults = new ArrayList<SuggestItem<SearchResult>>();
                    List<SuggestItem<SearchResult>> dataproductResults = new ArrayList<SuggestItem<SearchResult>>();                    
                    List<SuggestItem<SearchResult>> suggestItems = new ArrayList<>();
                    JsArray<String> results = Js.cast(Global.JSON.parse(json));
                    for (int i=0; i<results.length; i++) {
                        //console.log(results.getAt(i));
                        JsPropertyMap<?> resultObj = Js.cast(results.getAt(i));
                        console.log(resultObj);
                        if (resultObj.has("featureId")) {
                            String display = Js.asString(resultObj.get("display"));
                            String dataproductId = Js.asString(resultObj.get("dataproductId"));
                            String idFieldName = Js.asString(resultObj.get("idFieldName"));
                            // TODO BigInt problem?
                            // int featureId = Js.asInt(feature.get("featureId"));
                            String featureId = Js.asString(resultObj.get("featureId"));
                            List<Double> bbox = ((JsArray) resultObj.get("bbox")).asList();

                            SearchResult searchResult = new SearchResult();
                            searchResult.setLabel(display);
                            searchResult.setDataproductId(dataproductId);
                            searchResult.setIdFieldName(idFieldName);
                            searchResult.setFeatureId(featureId);
                            searchResult.setBbox(bbox);
                            searchResult.setType("feature");

                            Icon icon;
                            if (dataproductId.contains("gebaeudeadressen")) {
                                icon = Icons.ALL.mail();
                            } else if (dataproductId.contains("grundstueck")) {
                                icon = Icons.ALL.home();
                            } else if (dataproductId.contains("flurname")) {
                                icon = Icons.ALL.terrain();
                            } else {
                                icon = Icons.ALL.place();
                            }

                            SuggestItem<SearchResult> suggestItem = SuggestItem.create(searchResult,
                                    searchResult.getLabel(), icon);
                            featureResults.add(suggestItem);
                            // suggestItems.add(suggestItem);
                        } else if (resultObj.has("dataproductId")) {
                            String display = Js.asString(resultObj.get("display"));
                            String dataproductId = Js.asString(resultObj.get("dataproductId"));
                        
                            SearchResult searchResult = new SearchResult();
                            searchResult.setLabel(display);
                            searchResult.setDataproductId(dataproductId);
                            searchResult.setType("dataproduct");
                        
                            MdiIcon icon;
                            if (resultObj.has("sublayers")) {
                                icon = Icons.ALL.layers_plus_mdi();  
                            } else {
                                icon = Icons.ALL.layers_mdi();
                            } 
                        
                            SuggestItem<SearchResult> suggestItem = SuggestItem.create(searchResult, searchResult.getLabel(), icon);                            
                            dataproductResults.add(suggestItem);
//                          suggestItems.add(suggestItem);
                        }
                    }
                    
                    SearchResult featureHeader = new SearchResult();
                    featureHeader.setLabel("<span class='suggestion-group'>Orte</span>");
                    SuggestItem<SearchResult> featureHeaderItem = SuggestItem.create(featureHeader, featureHeader.getLabel(), null);                            
                    suggestItems.add(featureHeaderItem);
                    List<SuggestItem<SearchResult>> limitedFeatureResults = featureResults.stream().limit(MAX_SEARCH_RESULTS).collect(Collectors.toList());
                    suggestItems.addAll(limitedFeatureResults);
                    
                    SearchResult dataproductHeader = new SearchResult();
                    dataproductHeader.setLabel("<span class='suggestion-group'>Karten</span>");
                    SuggestItem<SearchResult> dataproductHeaderItem = SuggestItem.create(dataproductHeader, dataproductHeader.getLabel(), null);
                    suggestItems.add(dataproductHeaderItem);
                    List<SuggestItem<SearchResult>> limitedDataproductResults = dataproductResults.stream().limit(MAX_SEARCH_RESULTS).collect(Collectors.toList());
                    suggestItems.addAll(limitedDataproductResults);
                    
                    suggestionsHandler.onSuggestionsReady(suggestItems);

                    return null;
                }).catch_(error -> {
                    console.log(error);
                    return null;
                });
            }

            @Override
            public void find(Object searchValue, Consumer handler) {
                if (searchValue == null) {
                    return;
                }
                HTMLInputElement el =(HTMLInputElement) suggestBox.getInputElement().element();
                SearchResult searchResult = (SearchResult) searchValue;
                SuggestItem<SearchResult> suggestItem = SuggestItem.create(searchResult, el.value);
                handler.accept(suggestItem);
//                String searchResult = (String) searchValue;
//                SuggestItem<String> suggestItem = SuggestItem.create(searchResult, el.value);
//                handler.accept(suggestItem);
            }
        };

        suggestBox = SuggestBox.create("Suche: Adressen, Orte und Karten", dynamicStore);
        suggestBox.setId("SuggestBox");
        suggestBox.addLeftAddOn(Icons.ALL.search());
        suggestBox.setAutoSelect(false);
        suggestBox.setFocusColor(Color.RED_DARKEN_3);
        suggestBox.setFocusOnClose(false);
        
        HTMLElement resetIcon = Icons.ALL.close().setId("SearchResetIcon").element();
        resetIcon.style.cursor = "pointer";
        resetIcon.addEventListener("click", new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                HTMLInputElement el =(HTMLInputElement) suggestBox.getInputElement().element();
                el.value = "";
                suggestBox.unfocus();
            }
        });
        
        suggestBox.addRightAddOn(resetIcon);
        
        suggestBox.getInputElement().setAttribute("autocomplete", "off");
        suggestBox.getInputElement().setAttribute("spellcheck", "false");
        DropDownMenu suggestionsMenu = suggestBox.getSuggestionsMenu();
        suggestionsMenu.setId("suggestions");
        suggestionsMenu.setPosition(new DropDownPositionDown());
        suggestionsMenu.setSearchable(false);
       // suggestionsMenu.element().style.maxHeight = CSSProperties.MaxHeightUnionType.of("500px");
        
        suggestBox.addSelectionHandler(new SelectionHandler() {
            @Override
            public void onSelection(Object value) {
                
                SuggestItem<SearchResult> item = (SuggestItem<SearchResult>) value;
                SearchResult result = (SearchResult) item.getValue();
                //console.log(result);
                if (result.getType().equalsIgnoreCase("dataproduct")) {
                    String dataproductId = result.getDataproductId();
                    Image wmsLayer = createWmsLayer(dataproductId);
                    map.addLayer(wmsLayer);
                    wmsLayers.add(dataproductId);
                } else {
                    List<Double> bbox = result.getBbox();                 
                    Extent extent = new Extent(bbox.get(0), bbox.get(1), bbox.get(2), bbox.get(3));
                    View view = map.getView();
                    double resolution = view.getResolutionForExtent(extent);
                    view.setZoom(Math.floor(view.getZoomForResolution(resolution)) - 0);
                    double x = extent.getLowerLeftX() + extent.getWidth() / 2;
                    double y = extent.getLowerLeftY() + extent.getHeight() / 2;
                    view.setCenter(new Coordinate(x,y));
                } 
            }
        });
        
        HTMLElement suggestBoxDiv = div().id("suggestBoxDiv").add(suggestBox).element();
        suggestBoxDiv.style.paddingBottom = CSSProperties.PaddingBottomUnionType.of("0px");
        root.appendChild(suggestBoxDiv);

        Row facetRow1 = Row.create()
                .addColumn(Column.span(4).appendChild(cbBienen))
                .addColumn(Column.span(4).appendChild(cbQuellen))
                .addColumn(Column.span(4).appendChild(cbSondierung));
        root.appendChild(facetRow1.element());
        
        Row facetRow2 = Row.create()
                .addColumn(Column.span(4).appendChild(cbVollzugsmeldung))
                .addColumn(Column.span(4).appendChild(cbMutation));
        root.appendChild(facetRow2.element());
        
        Button resetBtn = Button.create(Icons.ALL.layers_remove_mdi())
                .setSize(ButtonSize.SMALL)
                .setContent("Reset")
                .setBackground(Color.WHITE)
                .elevate(0)
                .style()
                .setColor("#c62828")
                .setBorder("1px #c62828 solid")
                .setMinWidth(px.of(100)).get();

        Row resetRow = Row.create().style().setPaddingLeft("10px").setPaddingTop("10px").get().appendChild(resetBtn);
        root.appendChild(resetRow.element());

        resetRow.addClickListener(evt -> {
            removeWmsLayers();
        });
    }
    
    @Override
    public HTMLElement element() {
        return root;
    }
    
    private void removeWmsLayers() {
        for (String layerId : wmsLayers) {
            Image rlayer = (Image) getMapLayerById(layerId);
            map.removeLayer(rlayer);
        }
        wmsLayers.clear();
    }
    
    private Image createWmsLayer(String layerName) {
        ImageWmsParams imageWMSParams = OLFactory.createOptions();
        imageWMSParams.setLayers(layerName);

        ImageWmsOptions imageWMSOptions = OLFactory.createOptions();

        String baseUrl = "https://geo.so.ch/api/wms?";

        imageWMSOptions.setUrl(baseUrl);
        imageWMSOptions.setParams(imageWMSParams);
        imageWMSOptions.setRatio(1.2f);

        ImageWms imageWMSSource = new ImageWms(imageWMSOptions);

        LayerOptions layerOptions = OLFactory.createOptions();
        layerOptions.setSource(imageWMSSource);

        Image wmsLayer = new Image(layerOptions);
        wmsLayer.set(ID_ATTR_NAME, layerName);
        wmsLayer.setVisible(true);
        //wmsLayer.setOpacity(referenceWms.getLayerOpacity());
 
        return wmsLayer;
    }
 
    private Base getMapLayerById(String id) {
        ol.Collection<Base> layers = map.getLayers();
        for (int i = 0; i < layers.getLength(); i++) {
            Base item = layers.item(i);
            try {
                String layerId = item.get(ID_ATTR_NAME);
                if (layerId == null) {
                    continue;
                }
                if (layerId.equalsIgnoreCase(id)) {
                    return item;
                }
            } catch (Exception e) {
                //console.log(e.getMessage());
                //console.log("should not reach here");
            }
        }
        return null;
    }

}
