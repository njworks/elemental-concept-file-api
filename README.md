# Based on Elemental Concept Technical

### For testing [test-file-upload.html](test-file-upload.html) can be used once the application is running. It will send a POST request to
`http://localhost:8080/upload` with a file named `file`.

All configs are configured
in [application application.properties](application/src/main/resources/application.properties)

Access H2 console at `http://localhost:8080/h2-console` once application is running with the following credentials:
username: `sa`, password: ``, JDBC URL: `jdbc:h2:mem:testdb`
