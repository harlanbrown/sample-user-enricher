# sample-user-enricher

### To build

```
git clone git clone git@github.com:harlanbrown/sample-user-enricher.git
cd sample-user-enricher
mvn install
cp target/sample-user-enricher-core-1.0-SNAPSHOT.jar /opt/nuxeo/server/nxserver/bundles
```

### To see results (replace Administrator with your username)

```
curl -X GET http://localhost:8080/nuxeo/api/v1/user/Administrator -H 'X-NXenrichers-user:usernotif' -u Administrator:adminpass
```
