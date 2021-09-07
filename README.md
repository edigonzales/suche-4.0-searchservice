# suche-4.0-searchservice

## Database

```
docker run ...
```

```
git clone ...
gretl ...
```


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
