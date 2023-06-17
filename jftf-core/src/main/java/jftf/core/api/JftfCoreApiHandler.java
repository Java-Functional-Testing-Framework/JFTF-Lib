package jftf.core.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jftf.core.JftfModule;
import jftf.core.ioctl.ConfigurationManager;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


public class JftfCoreApiHandler extends JftfModule implements IJftfCoreApiHandler {
    private static JftfCoreApiHandler jftfCoreApiHandlerInstance = null;
    private static final String LOGIN_ENDPOINT = "http://localhost:8000/api/rest-auth/login/";
    private final Map<String, String> tokens;

    private JftfCoreApiHandler() {
        String username = controlIO.getConfigurationManager().getProperty(ConfigurationManager.cmdbConfigurationName, ConfigurationManager.groupCmdbCredentials, ConfigurationManager.keyApiAuthUsername);
        String password = controlIO.getConfigurationManager().getProperty(ConfigurationManager.cmdbConfigurationName, ConfigurationManager.groupCmdbCredentials, ConfigurationManager.keyApiAuthPassword);
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
            // Build the URL with the query parameters
            String apiUrl = String.format("/api/test-case-metadata/?testName=%s&testGroup=%s&featureGroup=%s&testPath=%s&testVersion=%s",
                    testName, testGroup, featureGroup, testPath.toString(), testVersion);

            // Execute the API request and parse the response
            JsonNode response = executeRequest(apiUrl);
            if (response.isArray() && response.size() > 0) {
                JsonNode metadata = response.get(0);
                return metadata.get("metadataId").asInt();
            }
        } catch (Exception e) {
            logger.LogError(String.format("API request for looking-up TestCaseMetadata failed! ('%s')", e.getMessage()));
            System.err.printf("API request for looking-up TestCaseMetadata failed! ('%s')", e.getMessage());
            e.printStackTrace();
            System.exit(5);
        }
        return -1;
    }

    @Override
    public JsonNode executeRequest(String apiUrl) {
        try {
            String fullUrl = "http://localhost:8000" + apiUrl;

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


    private Map<String, String> authenticate(String username, String password) {
        try {
            logger.LogInfo("Attempting JFTF-Core API token retrieval with the configured API authorization parameters!");

            URL tempUrl = new URL(LOGIN_ENDPOINT);
            HttpURLConnection tempConnection = (HttpURLConnection) tempUrl.openConnection();
            tempConnection.setRequestMethod("GET");
            tempConnection.setRequestProperty("Accept", "application/json");
            tempConnection.connect();

            // Retrieve the csrftoken cookie value from the response headers
            String csrfToken = extractCsrfToken(tempConnection);

            tempConnection.disconnect();

            URL url = new URL(LOGIN_ENDPOINT);
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
