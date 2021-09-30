package ch.so.agi;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
        
        String stmt = "\n"
                + "(SELECT\n"
                + "'A' AS weight, CAST(t_id AS TEXT) AS t_id, id, display, dset_children, dset_info, facet, bbox, idfield_meta\n"
                + "FROM\n"
                + "suche.solr_views\n"
                + "WHERE facet IN (" + facets + ")\n"
                + "AND (search_1_stem ILIKE '%" + queryString.trim().replace(" ", "%' AND search_1_stem ILIKE '%") + "%')\n"
                + "LIMIT 50)\n"; 
                
        stmt += "UNION ALL\n";
        
        stmt += ""
                + "(SELECT\n"
                + "'B' AS weight, CAST(t_id AS TEXT) AS t_id, id, display, dset_children, dset_info, facet, bbox, idfield_meta\n"
                + "FROM\n"
                + "suche.solr_views\n"
                + "WHERE facet IN (" + facets + ")\n"
                + "AND (search_2_stem ILIKE '%" + queryString.trim().replace(" ", "%' AND search_2_stem ILIKE '%") + "%')\n"
                + "LIMIT 50)\n";
        
        stmt += "UNION ALL\n";
        
        stmt += ""
                + "(SELECT\n"
                + "'C' AS weight, CAST(t_id AS TEXT) AS t_id, id, display, dset_children, dset_info, facet, bbox, idfield_meta "
                + "FROM\n"
                + "suche.solr_views\n"
                + "WHERE facet IN (" + facets + ")\n"
                + "AND (search_3_stem ILIKE '%" + queryString.trim().replace(" ", "%' AND search_3_stem ILIKE '%") + "%')\n"
                + "LIMIT 50)\n";
       

       
        
        
        String trigramFilter = "";
        trigramFilter += " OR ";
        trigramFilter += "(search_2_stem ILIKE '%" + queryString.trim().replace(" ", "%' AND search_2_stem ILIKE '%") + "%')";
        trigramFilter += " OR ";
        trigramFilter += "(search_3_stem ILIKE '%" + queryString.trim().replace(" ", "%' AND search_3_stem ILIKE '%") + "%')";
        //stmt += " AND " + trigramFilter + " LIMIT 50";
        
        // TODO stem suche
        log.info(stmt);
        
        
//        log.info(stmtv2);

        
//        String stmt = ""
//                + "SELECT "
//                + "t_id, id, display, dset_children, dset_info, facet, bbox, idfield_meta "
//                + "FROM "
//                + "suche.solr_views "
//                + "WHERE facet IN (" + facets + ") "
//                + "LIMIT 20";
        
        Set<String> tids = new HashSet<>();
        List<SearchResult> foo = jdbcTemplate.query(stmt, new RowMapper<SearchResult>() {
            @Override
            public SearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {
                try {
                    String t_id = rs.getString("t_id");
                    if (tids.contains(t_id)) {
                        return null;
                    }
                    tids.add(t_id);
                    
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
        
        foo.removeAll(Collections.singletonList(null)); // TODO: im RowMapper? 
        return foo;
    }
}
