# suche-4.0-searchservice

## Database

```
mkdir -m 0777 ~/pgdata_search
docker run -p 54322:5432 -v ~/pgdata_search:/var/lib/postgresql/data:delegated -e POSTGRES_DB=pub -e POSTGRES_PASSWORD=mysecretpassword postgis/postgis:13-3.1
```
Mein auf Apple Silicon erzeugtes `edigonzales/postgis:13-3.1` Image scheint nicht mehr zu funktioneren. Keine Ahnung warum, es ist nur ein Layer zusätzlich. Nun denn: 

```
    CREATE USER gretl LOGIN ENCRYPTED PASSWORD 'gretl';
    GRANT ALL PRIVILEGES ON DATABASE pub TO gretl;
```

```
git clone https://github.com/edigonzales/suche-4.0.git
gradle createTables importData
```
Use Gradle < 7.0 (wegen GRETL).



## Develop
First terminal:
```
mvn spring-boot:run -Penv-dev -pl *-server -am (-Dspring-boot.run.profiles=XXXX)
```

Second terminal:
```
mvn gwt:codeserver -pl *-client -am
```

Or without downloading all the snapshots again:

```
mvn gwt:codeserver -pl *-client -am -nsu
```

## Run
```
java -jar sodata-server/target/sodata.jar --spring.profiles.active=XXXX
```

## Vergleiche Suche 3.0
- 'Wasser':
  * Warum erscheinen Drainagen (nicht)? Wird in Solr "unaccent" verwendet?
  * Mit Originalanfragen ("dev.xxx") schauen aus welchem Index die Drainagen stammen?
  * Sowieso unaccent verwenden? In Solr funktioniert "rotistrasse".


## Todo
- Mehrere Tabellen? Ortssuche, Layersuche und Featuresuche. Bei der Ortssuche ist wohl praktisch immer trigram entscheidend. Bei beiden anderen kann es beides sein. Bei exakten ID eher "stem" oder dann noch zusätzlich normaler Index (oder kommt das auf selbe raus?). Vorteil könnte sein, dass man so besser balancieren kann, z.B. je 50 requesten und falls Orte nur 20 liefert, werden dann (falls vorhanden) 30 Karten verwendet.