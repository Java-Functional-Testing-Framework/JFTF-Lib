package jftf.core.ioctl;

import jftf.core.JftfModule;

import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DatabaseDriver extends JftfModule implements IDatabaseDriver{
    private static DatabaseDriver databaseDriverInstance = null;
    private Connection databaseConnection;
    private String databaseIp;
    private String databaseUsername;
    private String databasePassword;
    private String databaseName;
    private String currentDatabaseUrl;
    private Boolean connectionStatus;
    private final static String testConnectionString = "ConnectionTest";
    private final static String testConnectionQuery = String.format("SELECT '%s'",testConnectionString);
    private final static String sqlInsertTestCaseMetadata = "insert into TestCaseMetadata(testName, featureGroup, testGroup ,  testPath, testVersion) values (?,?,?,?,?);";
    private final static String sqlUpdateTestCaseMetadata = "update TestCaseMetadata SET testName = ?, featureGroup = ?, testGroup = ?, testPath = ?, testVersion = ? where metadataId = ?;";
    private final static String sqlInsertTestCase = "insert into TestCases(metaDataID) values (?);";
    private final static String sqlDeleteTestCase = "delete from TestCaseMetadata where metadataId = ?;";
    public final static String modeUpdateTestCaseFirstExecution = "firstExecution";
    public final static String modeUpdateTestCaseLastExecution = "lastExecution";
    private final static List<String> modesUpdateTestCase = List.of(modeUpdateTestCaseFirstExecution,modeUpdateTestCaseLastExecution);
    private final static String sqlInsertTestReportInformation = "insert into TestReportInformation(testId, startupTimestamp, endTimestamp, testDuration, errorMessages, loggerOutput, executionResult) values(?,?,?,?,?,?,?);";
    private final static String sqlInsertTestReport = "insert into TestReports(testId, testReportInformationId) values(?,?);";
    private final static String sqlDeleteTestReportP1 = "delete from TestReports where testReportInformationId = ?;";
    private final static String sqlDeleteTestReportP2 = "delete from TestReportInformation where testReportInformationId = ?;";
    private final static String sqlViewGetLastMetadataId = "select metadataID from getLastMetadataId;";
    private final static String sqlViewGetLastReportInformationId = "select testReportInformationId from getLastTestReportInformationId;";
    private final static String sqlSetViewMetadataId = "set @mId = ?;";
    private final static String sqlViewGetTestCaseMetadata = "select testName,featureGroup,testGroup,testPath,testVersion from getTestCaseMetadata;";
    private final static String sqlLookupTestCaseMetadata = "select metadataId from TestCaseMetadata where testName = ? and featureGroup = ? and testGroup  = ? and testPath = ? and testVersion = ?;";
    private final static String sqlSetViewTestcaseId = "set @tId = ?;";
    private final static String sqlViewGetTestCase = "select metaDataId,firstExecution,lastExecution,executed from getTestCase;";
    private final static String sqlLookupTestCaseId = "select testId from TestCases where metaDataId = ?;";
    private final static String sqlViewGetTestCaseIds = "select testId from getTestCaseIds;";
    private final static String sqlSetViewTestReportId = "set @trId = ?;";
    private final static String sqlViewGetTestReportInformation = "select testReportInformationId, testId, startupTimestamp, endTimestamp, testDuration, errorMessages, loggerOutput, executionResult from getTestReportInformation;";
    private final static String sqlViewGetTestReportInformationIds = "select testReportInformationId from getTestReportInformationIds;";
    private final static String sqlSetViewTestcaseId1 = "set @t1Id = ?;";
    private final static String sqlViewGetTestReportInformationForTestId = "select testReportInformationId, testId, startupTimestamp, endTimestamp, testDuration, errorMessages, loggerOutput, executionResult from getTestReportInformationForTestId";

    private DatabaseDriver(String databaseIp, String databaseUsername, String databasePassword, String databaseName) {
        this.connectDatabase(databaseIp, databaseUsername, databasePassword, databaseName);
        this.checkoutDatabase(databaseName);
        super.attachDatabaseDriver(this);
    }

    private DatabaseDriver(){
        this.setDatabaseIp(controlIO.getConfigurationManager().getProperty(ConfigurationManager.cmdbConfigurationName,ConfigurationManager.groupCmdbCredentials,ConfigurationManager.keyCmdbIp));
        this.setDatabaseUsername(controlIO.getConfigurationManager().getProperty(ConfigurationManager.cmdbConfigurationName,ConfigurationManager.groupCmdbCredentials,ConfigurationManager.keyCmdbUsername));
        this.setDatabasePassword(controlIO.getConfigurationManager().getProperty(ConfigurationManager.cmdbConfigurationName,ConfigurationManager.groupCmdbCredentials,ConfigurationManager.keyCmdbPassword));
        this.setDatabaseName(controlIO.getConfigurationManager().getProperty(ConfigurationManager.cmdbConfigurationName,ConfigurationManager.groupCmdbCredentials,ConfigurationManager.keyCmdbName));
        this.connectDatabase();
        this.checkoutDatabase();
        super.attachDatabaseDriver(this);
    }

    public static DatabaseDriver DatabaseDriverFactory(){
        if(databaseDriverInstance == null){
            databaseDriverInstance = new DatabaseDriver();
        }
        return databaseDriverInstance;
    }

    public static DatabaseDriver DatabaseDriverFactory(String databaseIp, String databaseUsername, String databasePassword, String databaseName) {
        if(databaseDriverInstance == null){
            databaseDriverInstance = new DatabaseDriver(databaseIp,databaseUsername,databasePassword,databaseName);
        }
        return databaseDriverInstance;
    }

    public void setDatabaseIp(String databaseIp) {
        this.databaseIp = databaseIp;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " +
                        ((SQLException) e).getSQLState());
                System.err.println("Error Code: " +
                        ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.err.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    @Override
    public Boolean getConnectionStatus() {
        return this.connectionStatus;
    }

    @Override
    public Connection getDatabaseConnection() {
        return this.databaseConnection;
    }

    @Override
    public Boolean testConnection() throws SQLException {
        Statement testConnectionStatement = this.databaseConnection.createStatement();
        ResultSet resultSet = testConnectionStatement.executeQuery(testConnectionQuery);
        resultSet.first();
        if (Objects.equals(resultSet.getString(1), testConnectionString)) {
            this.connectionStatus = Boolean.TRUE;
            return Boolean.TRUE;
        } else {
            this.connectionStatus = Boolean.FALSE;
            return Boolean.FALSE;
        }
    }

    @Override
    public void connectDatabase(String databaseIp, String databaseUsername, String databasePassword, String databaseName) {
        this.databaseIp = databaseIp;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        logger.LogDebug(String.format("Creating connection to database '%s' with user '%s'@'%s'!",this.databaseName,this.databaseUsername,this.databaseIp));
        try {
            this.currentDatabaseUrl = String.format("jdbc:mariadb://%s/",this.databaseIp);
            this.databaseConnection = DriverManager.getConnection(this.currentDatabaseUrl, this.databaseUsername, this.databasePassword);
            this.databaseConnection.setAutoCommit(false);
            if(this.testConnection() != Boolean.TRUE){
                System.err.println("(CRITICAL) Failed to open database connection!");
                System.exit(4);
            }
            else{
                logger.LogDebug("Database connection is successful!");
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to open database connection!");
            e.printStackTrace();
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
    }

    @Override
    public void checkoutDatabase() {
        logger.LogDebug(String.format("Checking out database '%s'",this.databaseName));
        try{
            Statement checkoutDatabaseStatement = this.databaseConnection.createStatement();
            checkoutDatabaseStatement.executeUpdate(String.format("use %s",this.databaseName));
            this.databaseConnection.commit();
            logger.LogDebug("Database checkout successful!");
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to checkout database!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
    }

    @Override
    public void checkoutDatabase(String databaseName) {
        this.databaseName = databaseName;
        logger.LogDebug(String.format("Checking out database '%s'",this.databaseName));
        try{
            Statement checkoutDatabaseStatement = this.databaseConnection.createStatement();
            checkoutDatabaseStatement.executeUpdate(String.format("use %s",databaseName));
            this.databaseConnection.commit();
            logger.LogDebug("Database checkout successful!");
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to checkout database!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
    }

    @Override
    public void connectDatabase() {
        logger.LogDebug(String.format("Creating connection to database '%s' with user '%s'@'%s'!",this.databaseName,this.databaseUsername,this.databaseIp));
        try {
            this.currentDatabaseUrl = String.format("jdbc:mariadb://%s/",this.databaseIp);
            this.databaseConnection = DriverManager.getConnection(this.currentDatabaseUrl, this.databaseUsername, this.databasePassword);
            this.databaseConnection.setAutoCommit(false);
            if(this.testConnection() != Boolean.TRUE){
                System.err.println("(CRITICAL) Failed to open database connection!");
                System.exit(4);
            }
            else {
                logger.LogDebug("Database connection is successful!");
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to open database connection!");
            e.printStackTrace();
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
    }

    @Override
    public void closeConnection() {
        logger.LogDebug("Closing database connection");
        if(this.getConnectionStatus() == Boolean.TRUE) {
            try {
                this.databaseConnection.close();
                logger.LogDebug("Database connection closed successfully!");
                this.connectionStatus = Boolean.FALSE;
            } catch (SQLException e) {
                logger.LogError("(CRITICAL) Failed to close database connection!");
                System.err.println("(CRITICAL) Failed to close database connection!");
                e.printStackTrace();
                DatabaseDriver.printSQLException(e);
                System.exit(4);
            }
        }
    }

    @Override
    public void insertTestCase(String testName, String featureGroup, String testGroup, Path testPath, String testVersion) {
        try {
            PreparedStatement insertMetadataPs = this.databaseConnection.prepareStatement(sqlInsertTestCaseMetadata);
            insertMetadataPs.setString(1,testName);
            insertMetadataPs.setString(2,featureGroup);
            insertMetadataPs.setString(3,testGroup);
            insertMetadataPs.setString(4,testPath.toString());
            insertMetadataPs.setString(5,testVersion);
            insertMetadataPs.executeUpdate();
            Statement getLastMetadataIdStatement = this.databaseConnection.createStatement();
            ResultSet resultSet = getLastMetadataIdStatement.executeQuery(sqlViewGetLastMetadataId);
            if(resultSet.first()){
                PreparedStatement insertTestCasePs = this.databaseConnection.prepareStatement(sqlInsertTestCase);
                insertTestCasePs.setInt(1,resultSet.getInt(1));
                insertTestCasePs.executeUpdate();
                this.databaseConnection.commit();
            }
            else{
                System.err.println("(CRITICAL) Failed to insert test case!");
                System.exit(4);
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to insert test case!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
    }

    @Override
    public Boolean deleteTestCase(int testId) {
        try{
            int metadataId = this.getTestCaseMetadataId(testId);
            if(metadataId != -1) {
                PreparedStatement deleteTestCasePs = this.databaseConnection.prepareStatement(sqlDeleteTestCase);
                deleteTestCasePs.setInt(1, metadataId);
                deleteTestCasePs.executeUpdate();
                this.databaseConnection.commit();
                return Boolean.TRUE;
            }
            else{
                return Boolean.FALSE;
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to delete test case!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
        return Boolean.FALSE;
    }


    @Override
    public Boolean updateTestCase(int testId, String updateMode, Timestamp firstExecution, Timestamp lastExecution, Boolean executed) {
        if(modesUpdateTestCase.contains(updateMode)) {
            if(Objects.equals(updateMode, modeUpdateTestCaseFirstExecution)){
                if(executed!=null){
                    if(executed == Boolean.FALSE && firstExecution!=null){
                        return Boolean.FALSE;
                    }
                    if(executed == Boolean.TRUE && firstExecution == null){
                        return Boolean.FALSE;
                    }
                    try{
                        String sqlUpdateTestCase = "update TestCases SET firstExecution = ?, executed = ? where testId = ?;";
                        PreparedStatement updateTestCasePs = this.databaseConnection.prepareStatement(sqlUpdateTestCase);
                        updateTestCasePs.setTimestamp(1, firstExecution);
                        updateTestCasePs.setBoolean(2, executed);
                        updateTestCasePs.setInt(3, testId);
                        if (updateTestCasePs.executeUpdate() != 0) {
                            this.databaseConnection.commit();
                            return Boolean.TRUE;
                        } else {
                            return Boolean.FALSE;
                        }
                    }
                    catch (SQLException e){
                        System.err.println("(CRITICAL) Failed to update test case information!");
                        DatabaseDriver.printSQLException(e);
                        System.exit(4);
                    }
                }
                else{
                    return Boolean.FALSE;
                }
            }
            else if(Objects.equals(updateMode, modeUpdateTestCaseLastExecution)) {
                try {
                    String sqlUpdateTestCase = "update TestCases SET lastExecution = ? where testId = ?;";
                    PreparedStatement updateTestCasePs = this.databaseConnection.prepareStatement(sqlUpdateTestCase);
                    updateTestCasePs.setTimestamp(1, lastExecution);
                    updateTestCasePs.setInt(2, testId);
                    if (updateTestCasePs.executeUpdate() != 0) {
                        this.databaseConnection.commit();
                        return Boolean.TRUE;
                    } else {
                        return Boolean.FALSE;
                    }
                } catch (SQLException e) {
                    System.err.println("(CRITICAL) Failed to update test case information!");
                    DatabaseDriver.printSQLException(e);
                    System.exit(4);
                }
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public List<String> getTestCase(int testId) {
        try{
            List<String> packagedTestCase = new ArrayList<>();
            PreparedStatement setTestCaseMetadataIdPs = this.databaseConnection.prepareStatement(sqlSetViewTestcaseId);
            setTestCaseMetadataIdPs.setInt(1,testId);
            setTestCaseMetadataIdPs.executeUpdate();
            Statement getTestCaseStatement = this.databaseConnection.createStatement();
            ResultSet resultSet = getTestCaseStatement.executeQuery(sqlViewGetTestCase);
            if(resultSet.first()){
                for (int i=1; i<5; i++){
                    packagedTestCase.add(resultSet.getString(i));
                }
                return packagedTestCase;
            }
            else{
                return null;
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to retrieve test case information!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
        return null;
    }

    @Override
    public int lookupTestCase(int metadataId) {
        try {
            PreparedStatement lookupTestCaseIdPs = this.databaseConnection.prepareStatement(sqlLookupTestCaseId);
            lookupTestCaseIdPs.setInt(1,metadataId);
            ResultSet resultSet = lookupTestCaseIdPs.executeQuery();
            if(resultSet.first()){
                return resultSet.getInt(1);
            }
            else{
                return -1;
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to retrieve test case Id!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
        return -1;
    }

    @Override
    public List<String> getTestCaseMetadata(int metadataId) {
        try{
            List<String> packagedTestcaseMetadata = new ArrayList<>();
            PreparedStatement setTestCaseMetadataIdPs = this.databaseConnection.prepareStatement(sqlSetViewMetadataId);
            setTestCaseMetadataIdPs.setInt(1,metadataId);
            setTestCaseMetadataIdPs.executeUpdate();
            Statement getTestCaseMetadataStatement = this.databaseConnection.createStatement();
            ResultSet resultSet = getTestCaseMetadataStatement.executeQuery(sqlViewGetTestCaseMetadata);
            if(resultSet.first()){
                for (int i=1; i<6; i++){
                    packagedTestcaseMetadata.add(resultSet.getString(i));
                }
                return packagedTestcaseMetadata;
            }
            else{
                return null;
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to retrieve test case metadata!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
        return null;
    }

    @Override
    public int lookupTestCaseMetadata(String testName, String featureGroup, String testGroup, Path testPath, String testVersion) {
        try{
            PreparedStatement lookupTestCaseMetadataPs = this.databaseConnection.prepareStatement(sqlLookupTestCaseMetadata);
            lookupTestCaseMetadataPs.setString(1,testName);
            lookupTestCaseMetadataPs.setString(2,featureGroup);
            lookupTestCaseMetadataPs.setString(3,testGroup);
            lookupTestCaseMetadataPs.setString(4,testPath.toString());
            lookupTestCaseMetadataPs.setString(5,testVersion);
            ResultSet resultSet = lookupTestCaseMetadataPs.executeQuery();
            if(resultSet.first()){
                return resultSet.getInt(1);
            }
            else{
                return -1;
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to lookup test case metadata!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
        return -1;
    }

    @Override
    public int getTestCaseMetadataId(int testId) {
        List<String> packagedTestCaseInformation = this.getTestCase(testId);
        if(packagedTestCaseInformation != null) {
            return Integer.parseInt(packagedTestCaseInformation.get(0));
        }
        else{
            return -1;
        }
    }

    @Override
    public Boolean updateTestCaseMetadata(int testId, String testName, String featureGroup, String testGroup, Path testPath, String testVersion) {
        int metadataId = this.getTestCaseMetadataId(testId);
        if(metadataId != -1) {
            if (testName != null && featureGroup != null && testGroup != null && testPath != null && testVersion != null) {
                try {
                    PreparedStatement updateTestCaseMetadataPs = this.databaseConnection.prepareStatement(sqlUpdateTestCaseMetadata);
                    updateTestCaseMetadataPs.setString(1, testName);
                    updateTestCaseMetadataPs.setString(2, featureGroup);
                    updateTestCaseMetadataPs.setString(3, testGroup);
                    updateTestCaseMetadataPs.setString(4, testPath.toString());
                    updateTestCaseMetadataPs.setString(5, testVersion);
                    updateTestCaseMetadataPs.setInt(6, metadataId);
                    if(updateTestCaseMetadataPs.executeUpdate() != 0){
                        this.databaseConnection.commit();
                        return Boolean.TRUE;
                    }
                    else{
                        return Boolean.FALSE;
                    }
                } catch (SQLException e) {
                    System.err.println("(CRITICAL) Failed to update test case metadata!");
                    DatabaseDriver.printSQLException(e);
                    System.exit(4);
                }
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public List<Integer> getTestCaseIds() {
        List<Integer> testCaseIds = new ArrayList<>();
        try{
            Statement getTestCaseIdsStatement = this.databaseConnection.createStatement();
            ResultSet resultSet = getTestCaseIdsStatement.executeQuery(sqlViewGetTestCaseIds);
            if(resultSet.first()){
                resultSet.beforeFirst();
                while(resultSet.next()){
                    testCaseIds.add(resultSet.getInt(1));
                }
                return testCaseIds;
            }
            else{
                return null;
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to retrieve list of test case Ids!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
        return null;
    }

    @Override
    public void insertTestReport(int testId, Timestamp startupTimestamp, Timestamp endTimestamp, Time testDuration, String errorMessages, String loggerOutput, String executionResult) {
        try{
            PreparedStatement insertTestReportInformationPs = this.databaseConnection.prepareStatement(sqlInsertTestReportInformation);
            insertTestReportInformationPs.setInt(1,testId);
            insertTestReportInformationPs.setTimestamp(2,startupTimestamp);
            insertTestReportInformationPs.setTimestamp(3,endTimestamp);
            insertTestReportInformationPs.setTime(4,testDuration);
            insertTestReportInformationPs.setString(5,errorMessages);
            insertTestReportInformationPs.setString(6,loggerOutput);
            insertTestReportInformationPs.setString(7,executionResult);
            insertTestReportInformationPs.executeUpdate();
            Statement getLastReportInformationIdStatement = this.databaseConnection.createStatement();
            ResultSet resultSet = getLastReportInformationIdStatement.executeQuery(sqlViewGetLastReportInformationId);
            if(resultSet.first()){
                PreparedStatement insertTestReportPs = this.databaseConnection.prepareStatement(sqlInsertTestReport);
                insertTestReportPs.setInt(1,testId);
                insertTestReportPs.setInt(2,resultSet.getInt(1));
                insertTestReportPs.executeUpdate();
                this.databaseConnection.commit();
            }
            else{
                System.err.println("(CRITICAL) Failed to insert test report!");
                System.exit(4);
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to insert test report!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
    }

    @Override
    public List<String> getTestReportInformation(int testReportInformationId) {
        try{
            List<String> packagedTestReportInformation = new ArrayList<>();
            PreparedStatement setTestReportInformationIdPs = this.databaseConnection.prepareStatement(sqlSetViewTestReportId);
            setTestReportInformationIdPs.setInt(1,testReportInformationId);
            setTestReportInformationIdPs.executeUpdate();
            Statement getTestReportInformationStatement = this.databaseConnection.createStatement();
            ResultSet resultSet = getTestReportInformationStatement.executeQuery(sqlViewGetTestReportInformation);
            if(resultSet.first()){
                for (int i=1; i<9; i++){
                    packagedTestReportInformation.add(resultSet.getString(i));
                }
                return packagedTestReportInformation;
            }
            else{
                return null;
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to retrieve test report information!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
        return null;
    }

    @Override
    public Boolean deleteTestReport(int testReportInformationId) {
        try{
            if(this.getTestReportInformation(testReportInformationId) != null) {
                PreparedStatement deleteTestReportP1Ps = this.databaseConnection.prepareStatement(sqlDeleteTestReportP1);
                deleteTestReportP1Ps.setInt(1, testReportInformationId);
                deleteTestReportP1Ps.executeUpdate();
                PreparedStatement deleteTestReportP2Ps = this.databaseConnection.prepareStatement(sqlDeleteTestReportP2);
                deleteTestReportP2Ps.setInt(1,testReportInformationId);
                deleteTestReportP2Ps.executeUpdate();
                this.databaseConnection.commit();
                return Boolean.TRUE;
            }
            else{
                return Boolean.FALSE;
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to delete test report!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
        return Boolean.FALSE;
    }

    @Override
    public List<Integer> getTestReportInformationIds() {
        List<Integer> testReportInformationIds = new ArrayList<>();
        try{
            Statement getTestReportInformationIdsStatement = this.databaseConnection.createStatement();
            ResultSet resultSet = getTestReportInformationIdsStatement.executeQuery(sqlViewGetTestReportInformationIds);
            if(resultSet.first()){
                resultSet.beforeFirst();
                while(resultSet.next()){
                    testReportInformationIds.add(resultSet.getInt(1));
                }
                return testReportInformationIds;
            }
            else{
                return null;
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to retrieve list of test report information Ids!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
        return null;
    }

    @Override
    public List<List<String>> getTestReportInformationForTestId(int testId) {
        List<List<String>> packagedQuery = new ArrayList<>();
        try{
            PreparedStatement setTestIdPs = this.databaseConnection.prepareStatement(sqlSetViewTestcaseId1);
            setTestIdPs.setInt(1,testId);
            setTestIdPs.executeUpdate();
            Statement getTestReportInformationForTestIdStatement = this.databaseConnection.createStatement();
            ResultSet resultSet = getTestReportInformationForTestIdStatement.executeQuery(sqlViewGetTestReportInformationForTestId);
            if(resultSet.first()){
                resultSet.beforeFirst();
                while(resultSet.next()){
                    packagedQuery.add(this.getTestReportInformation(resultSet.getInt(1)));
                }
                return packagedQuery;
            }
            else{
                return null;
            }
        }
        catch (SQLException e){
            System.err.println("(CRITICAL) Failed to retrieve list of test report information for set test Id!");
            DatabaseDriver.printSQLException(e);
            System.exit(4);
        }
        return null;
    }
}