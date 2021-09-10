# suche-4.0-searchservice

## Database

```
mkdir -m 0777 ~/pgdata_search
docker run -p 54322:5432 -v ~/pgdata:/var/lib/postgresql/data:delegated -e POSTGRES_DB=pub -e POSTGRES_PASSWORD=mysecretpassword edigonzales/postgis:13-3.1
```

```
git clone https://github.com/edigonzales/suche-4.0.git
gradle createTables importData
```
Use Gradle < 7.



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
