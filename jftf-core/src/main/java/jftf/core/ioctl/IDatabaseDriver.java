package jftf.core.ioctl;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public interface IDatabaseDriver {
    Boolean testConnection() throws SQLException;
    void connectDatabase(String databaseIp, String databaseUsername, String databasePassword, String databaseName);
    void checkoutDatabase();
    void checkoutDatabase(String databaseName);
    void connectDatabase();
    void closeConnection();
    Boolean getConnectionStatus();
    Connection getDatabaseConnection();
    void insertTestCase(String testName, String featureGroup, String testGroup, Path testPath, String testVersion);
    Boolean deleteTestCase(int testId);
    Boolean updateTestCase(int testId, String updateMode, Timestamp firstExecution, Timestamp lastExecution, Boolean executed);
    List<String> getTestCase(int testId);
    List<String> getTestCaseMetadata(int metadataId);
    int lookupTestCaseMetadata(String testName, String featureGroup, String testGroup, Path testPath, String testVersion);
    int getTestCaseMetadataId(int testId);
    Boolean updateTestCaseMetadata(int testId, String testName, String featureGroup, String testGroup, Path testPath, String testVersion);
    List<Integer> getTestCaseIds();
    void insertTestReport(int testId, Timestamp startupTimestamp, Timestamp endTimestamp, Time testDuration, String errorMessages, String loggerOutput, String executionResult);
    List<String> getTestReportInformation(int testReportInformationId);
    Boolean deleteTestReport(int testReportInformationId);
    List<Integer> getTestReportInformationIds();
    List<List<String>> getTestReportInformationForTestId(int testId);
}