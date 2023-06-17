package jftf.core.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jftf.core.JftfModule;
import jftf.core.ioctl.ConfigurationManager;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


public class JftfCoreApiHandler extends JftfModule implements IJftfCoreApiHandler {
    private static JftfCoreApiHandler jftfCoreApiHandlerInstance = null;
    private static final String TRANSPORT = "http://";
    private static final String LOGIN_ENDPOINT = "/api/rest-auth/login/";
    private final Map<String, String> tokens;
    private String apiHostname;
    private String apiPort;

    private JftfCoreApiHandler() {
        String username = controlIO.getConfigurationManager().getProperty(ConfigurationManager.daemonConfigurationName, ConfigurationManager.groupJftfCoreConfig, ConfigurationManager.keyApiAuthUsername);
        String password = controlIO.getConfigurationManager().getProperty(ConfigurationManager.daemonConfigurationName, ConfigurationManager.groupJftfCoreConfig, ConfigurationManager.keyApiAuthPassword);
        this.apiHostname = controlIO.getConfigurationManager().getProperty(ConfigurationManager.daemonConfigurationName, ConfigurationManager.groupJftfCoreConfig, ConfigurationManager.keyApiHostname);
        this.apiPort = controlIO.getConfigurationManager().getProperty(ConfigurationManager.daemonConfigurationName, ConfigurationManager.groupJftfCoreConfig, ConfigurationManager.keyApiPort);
        this.tokens = authenticate(username, password);
        super.attachJftfCoreApiHandler(this);
    }

    public static JftfCoreApiHandler JftfCoreApiHandlerFactory() {
        if (jftfCoreApiHandlerInstance == null) {
            jftfCoreApiHandlerInstance = new JftfCoreApiHandler();
        }
        return jftfCoreApiHandlerInstance;
    }

    @Override
    public int lookupTestCaseMetadata(String testName, String featureGroup, String testGroup, Path testPath, String testVersion) {
        try {
            logger.LogInfo("Looking up test case metadata in the JFTF CMDB!");

            // Build the URL with the query parameters
            String apiUrl = String.format("/api/test-case-metadata/?testName=%s&testGroup=%s&featureGroup=%s&testPath=%s&testVersion=%s",
                    testName, testGroup, featureGroup, testPath.toString(), testVersion);

            // Execute the API request and parse the response
            JsonNode response = executeRequest(apiUrl);
            if (response.isArray() && response.size() > 0) {
                JsonNode metadata = response.get(0);
                int metadataId = metadata.get("metadataId").asInt();
                logger.LogDebug(String.format("Metadata id is '%d'", metadataId));
                return metadataId;
            }
        } catch (Exception e) {
            logger.LogError(String.format("API request for looking up TestCaseMetadata failed! ('%s')", e.getMessage()));
            System.err.printf("API request for looking up TestCaseMetadata failed! ('%s')", e.getMessage());
            e.printStackTrace();
            System.exit(5);
        }
        logger.LogDebug("Test case metadata was not found in the JFTF CMDB!");
        return -1;
    }

    @Override
    public int lookupTestCase(int metadataId) {
        try {
            logger.LogInfo("Looking up test case in the JFTF CMDB!");

            // Build the URL with the query parameter
            String apiUrl = String.format("/api/test-case/?metaDataId=%d", metadataId);

            // Execute the API request and parse the response
            JsonNode response = executeRequest(apiUrl);
            if (response.isArray() && response.size() > 0) {
                JsonNode testCase = response.get(0);
                int testCaseId = testCase.get("testId").asInt();
                logger.LogDebug(String.format("Test id is '%d'", testCaseId));
                return testCaseId;
            }
        } catch (Exception e) {
            logger.LogError(String.format("API request for looking up TestCase failed! ('%s')", e.getMessage()));
            System.err.printf("API request for looking up TestCase failed! ('%s')", e.getMessage());
            e.printStackTrace();
            System.exit(5);
        }

        logger.LogDebug("Test case was not found in the JFTF CMDB!");
        return -1;
    }

    @Override
    public void insertTestCaseMetadata(String testName, String featureGroup, String testGroup, Path testPath, String testVersion) {
        try {
            // Build the request body for /api/test-case-metadata
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("testName", testName);
            requestBody.put("featureGroup", featureGroup);
            requestBody.put("testGroup", testGroup);
            requestBody.put("testPath", testPath.toString());
            requestBody.put("testVersion", testVersion);

            // Make a POST request to /api/test-case-metadata
            JsonNode metadataResponse = executePostRequest("/api/test-case-metadata/", requestBody.toString());

            // Check if the request was successful and retrieve the metadataId
            if (metadataResponse != null && metadataResponse.has("metadataId")) {
                int metadataId = metadataResponse.get("metadataId").asInt();

                // Build the request body for /api/test-case
                ObjectNode testCaseRequestBody = objectMapper.createObjectNode();
                testCaseRequestBody.put("metaDataId", metadataId);

                // Make a POST request to /api/test-case
                JsonNode testCaseResponse = executePostRequest("/api/test-case/", testCaseRequestBody.toString());

                // Check if the request was successful and retrieve the testId
                if (testCaseResponse != null && testCaseResponse.isArray() && testCaseResponse.size() > 0) {
                    JsonNode testCase = testCaseResponse.get(0);
                    if (!testCase.has("testId")) {
                        logger.LogError(String.format("POST /api/test-case/ response is: '%s'", testCaseResponse));
                        System.err.println("(CRITICAL) Failed to insert test case!");
                        System.exit(5);
                    }
                }
            } else {
                logger.LogError(String.format("POST /api/test-case-metadata/ response is: '%s'", metadataResponse));
                System.err.println("(CRITICAL) Failed to insert test case!");
                System.exit(5);
            }
        } catch (Exception e) {
            logger.LogError(String.format("API request for looking up TestCaseMetadata failed! ('%s')", e.getMessage()));
            System.err.printf("API request for looking up TestCaseMetadata failed! ('%s')", e.getMessage());
            e.printStackTrace();
            System.exit(5);
        }
    }

    @Override
    public JsonNode executeRequest(String apiUrl) {
        try {
            String fullUrl = TRANSPORT + String.format("%s:%s", apiHostname, apiPort) + apiUrl;

            logger.LogDebug(String.format("Executing request 'GET %s'", fullUrl));

            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Set the Authorization header with the token
            connection.setRequestProperty("Authorization", "Token " + tokens.get("authtoken"));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            logger.LogDebug(String.format("Response code for request 'GET %s' is: '%s'", fullUrl, responseCode));

            // Handle the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            logger.LogDebug(String.format("Response for request 'GET %s' is: '%s'", fullUrl, response));

            // Parse the response JSON
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response.toString());
        } catch (IOException e) {
            logger.LogError(String.format("An error ('%s') occurred during the API request: 'GET %s'", e.getMessage(), apiUrl));
            System.err.printf("An error ('%s') occurred during the API request: '%s'", e.getMessage(), apiUrl);
            e.printStackTrace();
            System.exit(5);
        }
        return null;
    }

    @Override
    public JsonNode executePostRequest(String apiUrl, String requestBody) {
        try {
            String fullUrl = TRANSPORT + String.format("%s:%s", apiHostname, apiPort) + apiUrl;

            logger.LogDebug(String.format("Executing request 'POST %s'", fullUrl));

            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            // Set the Authorization header with the token
            connection.setRequestProperty("Authorization", "Token " + tokens.get("authtoken"));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-CSRFToken", tokens.get("csrftoken"));

            // Enable input and output streams
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Write the request body to the connection's output stream
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            logger.LogDebug(String.format("Response code for request 'POST %s' is: '%s'", fullUrl, responseCode));

            // Handle the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            logger.LogDebug(String.format("Response for request 'POST %s' is: '%s'", fullUrl, response));

            // Parse the response JSON
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response.toString());
        } catch (IOException e) {
            logger.LogError(String.format("An error ('%s') occurred during the API request: 'POST %s'", e.getMessage(), apiUrl));
            System.err.printf("An error ('%s') occurred during the API request: 'POST %s'", e.getMessage(), apiUrl);
            e.printStackTrace();
            System.exit(5);
        }
        return null;
    }

    private Map<String, String> authenticate(String username, String password) {
        try {
            logger.LogInfo("Attempting JFTF-Core API token retrieval with the configured API authorization parameters!");

            // Build the auth URL
            String authUrl = TRANSPORT + String.format("%s:%s", apiHostname, apiPort) + LOGIN_ENDPOINT;

            URL tempUrl = new URL(authUrl);
            HttpURLConnection tempConnection = (HttpURLConnection) tempUrl.openConnection();
            tempConnection.setRequestMethod("GET");
            tempConnection.setRequestProperty("Accept", "application/json");
            tempConnection.connect();

            // Retrieve the csrftoken cookie value from the response headers
            String csrfToken = extractCsrfToken(tempConnection);

            tempConnection.disconnect();

            URL url = new URL(authUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set X-CSRFToken header with the csrfToken value
            connection.setRequestProperty("X-CSRFToken", csrfToken);

            // Set the request body
            String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

            // Set Content-Type header
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Write the request body
            connection.getOutputStream().write(jsonInputString.getBytes());

            int responseCode = connection.getResponseCode();
            logger.LogDebug(String.format("API Authentication Response Code: '%s'", responseCode));

            // Handle the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            logger.LogDebug(String.format("API Authentication Response: '%s'", response));

            // Parse the response JSON and retrieve the token
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.toString());

            String authToken = jsonResponse.get("key").asText();

            // Create a map to hold the tokens
            Map<String, String> tokens = new HashMap<>();
            tokens.put("csrftoken", csrfToken);
            tokens.put("authtoken", authToken);

            logger.LogInfo("Successfully retrieved API and CSRF tokens!");

            return tokens;
        } catch (IOException e) {
            logger.LogError(String.format("Failed JFTF-Core API token retrieval! ('%s')", e.getMessage()));
            System.err.printf("Failed JFTF-Core API token retrieval! ('%s')%n", e.getMessage());
            e.printStackTrace();
            System.exit(5);
        }
        return new HashMap<>();
    }

    private String extractCsrfToken(HttpURLConnection connection) {
        String cookieHeader = connection.getHeaderField("Set-Cookie");
        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                String[] parts = cookie.trim().split("=");
                if (parts.length == 2 && parts[0].equals("csrftoken")) {
                    return parts[1];
                }
            }
        }
        return null;
    }
}
