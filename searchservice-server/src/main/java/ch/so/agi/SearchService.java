package ch.so.agi;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SearchService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    NamedParameterJdbcTemplate jdbcParamTemplate; 

    @Autowired
    private ObjectMapper objectMapper;

    public List<SearchResult> search(String queryString, String filterString) {
        log.info(queryString);
        log.info(filterString);
        
        Stream<String> stream = Arrays.stream(filterString.split( "," ));      
        String facets = stream.map(filter -> {
            return "'"+filter+"'";
        }).collect(Collectors.joining(","));
        
//        String facets = String.join(",", facetsList);
        log.info(facets);
        
        String stmtv2 = ""
                + "SELECT "
                + "t_id, id, display, dset_children, dset_info, facet, bbox, idfield_meta "
                + "FROM "
                + "suche.solr_views "
                + "WHERE facet IN (" + facets + ") ";
                
        String trigramFilter = "(search_1_stem ILIKE '%" + queryString.trim().replace(" ", "%' AND search_1_stem ILIKE '%") + "%')";
        trigramFilter += " OR ";
        trigramFilter += "(search_2_stem ILIKE '%" + queryString.trim().replace(" ", "%' AND search_2_stem ILIKE '%") + "%')";
        trigramFilter += " OR ";
        trigramFilter += "(search_3_stem ILIKE '%" + queryString.trim().replace(" ", "%' AND search_3_stem ILIKE '%") + "%')";
        stmtv2 += " AND " + trigramFilter + " LIMIT 50";
        
        // TODO stem suche
        log.info(trigramFilter);
        
        
//        log.info(stmtv2);

        
        String stmt = ""
                + "SELECT "
                + "t_id, id, display, dset_children, dset_info, facet, bbox, idfield_meta "
                + "FROM "
                + "suche.solr_views "
                + "WHERE facet IN (" + facets + ") "
                + "LIMIT 20";
        
        
        List<SearchResult> foo = jdbcTemplate.query(stmtv2, new RowMapper<SearchResult>() {
            @Override
            public SearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    String[] id = objectMapper.readValue(rs.getString("id"), String[].class);
                    String facet = rs.getString("facet");
                    
                    if (facet.equalsIgnoreCase("foreground")) {
                        DataproductResult result = new DataproductResult();
                        result.setStacktype(facet);
                        result.setDisplay(rs.getString("display"));
                        result.setType(id[0]);
                        result.setDataproductId(id[1]);
                                                
                        String children = rs.getString("dset_children");
                        if (children != null) {
                            List<Map> childrenList = objectMapper.readValue(children, ArrayList.class);
                            for (Map child : childrenList) {
                                DataproductResult childResult = new DataproductResult();
                                childResult.setDataproductId((String)child.get("ident"));
                                childResult.setDisplay((String)child.get("display"));
                                childResult.setDestInfo((Boolean)child.get("dset_info"));
                                childResult.setType((String)child.get("subclass"));
                                result.addSubLayer(childResult);
                            }
                        }
                        return result;
                    } else {
                        FeatureResult result = new FeatureResult();
                        result.setDataproductId(id[0]);
                        result.setFeatureId(id[1]);
                        result.setDisplay(rs.getString("display"));
                        String bbox = rs.getString("bbox");
                        List<Integer> coords = Arrays.stream(bbox.substring(1, bbox.length() - 1).split(","))
                                .map(Integer::parseInt).collect(Collectors.toList());
                        result.setBbox(coords);
                        String[] idFieldMeta = objectMapper.readValue(rs.getString("idfield_meta"), String[].class);
                        result.setIdFieldName(idFieldMeta[0]);
                        return result;
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                                
                
//                String display = rs.getString("display");
//                //String facet = rs.getString("facet");
//                String bbox = rs.getString("bbox");                
//                List<Integer> coords = Arrays.stream(bbox.substring(1, bbox.length()-1).split(","))
//                        .map(Integer::parseInt)
//                        .collect(Collectors.toList());
//                
//                FeatureResult featureResult = new FeatureResult();
//                featureResult.setDisplay(display);
//                featureResult.setDataproductId(facet);
//                featureResult.setBbox(coords);
                
                
//                return featureResult;
            }
            
        });
        
        return foo;
    }
}
