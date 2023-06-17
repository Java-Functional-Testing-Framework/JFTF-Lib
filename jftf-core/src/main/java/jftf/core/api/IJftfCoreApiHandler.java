package jftf.core.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.nio.file.Path;

public interface IJftfCoreApiHandler {
    JsonNode executeRequest(String apiUrl);

    int lookupTestCaseMetadata(String testName, String featureGroup, String testGroup, Path testPath, String testVersion);

}
