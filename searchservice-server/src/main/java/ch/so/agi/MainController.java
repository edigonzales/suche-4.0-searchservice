package ch.so.agi;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Settings settings;
    
    @Autowired
    SearchService searchService;
    
    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        return ResponseEntity.ok().body(settings);
    }
    
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok().body("suche-4.0");
    }
    
    @GetMapping("/search")
    public List<String> search(@RequestParam(value="searchtext", required=true) String queryString) {
        log.info(queryString);
        
        if (queryString == null) {

        }

        List<String> result = searchService.search(queryString);
        
        
        return result;
    }
    
}
