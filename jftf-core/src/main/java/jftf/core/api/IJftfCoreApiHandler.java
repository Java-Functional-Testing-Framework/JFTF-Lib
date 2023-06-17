package jftf.core.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.nio.file.Path;

public interface IJftfCoreApiHandler {
    JsonNode executeRequest(String apiUrl);

    JsonNode executePostRequest(String apiUrl, String requestBody);

    void insertTestCaseMetadata(String testName, String featureGroup, String testGroup, Path testPath, String testVersion);

    int lookupTestCaseMetadata(String testName, String featureGroup, String testGroup, Path testPath, String testVersion);

    int lookupTestCase(int metadataId);

}
