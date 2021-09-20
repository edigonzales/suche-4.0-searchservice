package ch.so.agi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

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

    public List<SearchResult> search(String queryString) {
        log.info(queryString);
        
        String stmt = "SELECT t_id, id, display, dset_children, dset_info, facet, bbox FROM suche.solr_views LIMIT 10";
        
        List<SearchResult> foo = jdbcTemplate.query(stmt, new RowMapper<SearchResult>() {
            @Override
            public SearchResult mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                
                String facet = rs.getString("facet");
                if (facet.equalsIgnoreCase("foreground")) {
                    
                    try {
                        String[] id = objectMapper.readValue(rs.getString("id"), String[].class);
                        
                        DataproductResult result = new DataproductResult();
                        result.setStacktype(facet);
                        result.setDisplay(rs.getString("display"));
                        result.setType(id[0]);
                        result.setDataproductId(id[1]);

                        
                        return result;
                        
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }

                    
                    
                }                    
//                } else {
//                    
//                }
                return null;
                
                
                
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
