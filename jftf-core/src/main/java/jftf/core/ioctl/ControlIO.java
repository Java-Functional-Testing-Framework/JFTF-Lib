package jftf.core.ioctl;

import jftf.core.JftfModule;
import jftf.core.logging.LoggingContextInformation;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class ControlIO extends JftfModule implements IControlIO {
    protected static String osGenericHomeDirectoryURI = null;
    protected static String osGenericJavaLogDirectory = System.getProperty("java.io.tmpdir");
    private static final SimpleDateFormat javaLogFileTimestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
    protected String jftfHomeDirectoryName = null;
    protected String jftfLogDirectoryName = "logs";
    protected String jftfTestCasesDirectoryName = "test_cases";
    protected String jftfLogDirectorySystemVariableKey = "JFTF_LOGS";
    protected String jftfLogSyslogServerIpSystemVariableKey = "SYSLOG_SERVER_IP";
    private static String jftfLogAppNameSystemVariableKey = "CUR_APP_NAME";
    protected List<String> jftfSystemVariableKeyList = List.of(jftfLogDirectorySystemVariableKey,jftfLogSyslogServerIpSystemVariableKey);
    protected static Path jftfHomeDirectoryPath = null;
    protected static Path jftfLogDirectoryPath = null;
    protected static Path jftfTestCasesDirectoryPath = null;
    protected static List<Path> jftfDirectoriesList = null;

    protected final void setupEnvironment(){
        this.generateJftfHomeDirectory();
        this.generateJftfLogDirectory();
        this.generateJftfTestCasesDirectory();
        this.populateJftfDirectoriesList();
        this.exportSystemVariables();
    }

    protected final void exportSystemVariables(){
        try {
            System.setProperty(jftfLogDirectorySystemVariableKey, this.generateJftfLogDirectoryPath().toString());
            System.setProperty(jftfLogSyslogServerIpSystemVariableKey, "localhost");
        }
        catch (NullPointerException e){
            System.err.println("(CRITICAL) One of the system variable key/value is null!");
            e.printStackTrace();
            System.exit(2);
        }
        catch (IllegalArgumentException e){
            System.err.println("(CRITICAL) One of the system variable key/value is illegal!");
            e.printStackTrace();
            System.exit(2);
        }
    }

    protected static String getOsGenericHomeDirectoryURI() {
        if(osGenericHomeDirectoryURI == null){
            osGenericHomeDirectoryURI = System.getProperty("user.home");
        }
        return osGenericHomeDirectoryURI;
    }

    protected final Path generateJftfHomeDirectoryPath(){
        if(jftfHomeDirectoryPath == null){
            try{
                jftfHomeDirectoryPath = Paths.get(getOsGenericHomeDirectoryURI(),this.jftfHomeDirectoryName);
            }
            catch(InvalidPathException e){
                System.err.println("(CRITICAL) Invalid system path for the JFTF home directory!");
                e.printStackTrace();
                System.exit(2);
            }
        }
        return jftfHomeDirectoryPath;
    }

    protected final Path generateJftfLogDirectoryPath(){
        if(jftfLogDirectoryPath == null){
            try{
                jftfLogDirectoryPath = Paths.get(this.generateJftfHomeDirectoryPath().toString(),this.jftfLogDirectoryName);
            }
            catch(InvalidPathException e){
                System.err.println("(CRITICAL) Invalid system path for the JFTF log directory!");
                e.printStackTrace();
                System.exit(2);
            }
        }
        return jftfLogDirectoryPath;
    }

    protected final Path generateJftfTestCasesDirectoryPath(){
        if(jftfTestCasesDirectoryPath == null){
            try{
                jftfTestCasesDirectoryPath = Paths.get(this.generateJftfHomeDirectoryPath().toString(),this.jftfTestCasesDirectoryName);
            }
            catch(InvalidPathException e){
                System.err.println("(CRITICAL) Invalid system path for the JFTF test cases directory!");
                e.printStackTrace();
                System.exit(2);
            }
        }
        return jftfTestCasesDirectoryPath;
    }

    protected final List<Path> populateJftfDirectoriesList(){
        if(jftfDirectoriesList == null){
            jftfDirectoriesList = List.of(this.generateJftfHomeDirectoryPath(),this.generateJftfLogDirectoryPath(),this.generateJftfTestCasesDirectoryPath());
        }
        return jftfDirectoriesList;
    }

    protected final void generateJftfHomeDirectory() {
        try {
            Files.createDirectory(this.generateJftfHomeDirectoryPath());
        }
        catch(IOException e){
            if(!(e instanceof FileAlreadyExistsException)) {
                System.err.println("(CRITICAL) Failed to generate the JFTF home directory!");
                e.printStackTrace();
                System.exit(2);
            }
        }
    }

    protected final void generateJftfLogDirectory() {
        try {
            Files.createDirectory(this.generateJftfLogDirectoryPath());
        }
        catch(IOException e){
            if(!(e instanceof FileAlreadyExistsException)) {
                System.err.println("(CRITICAL) Failed to generate the JFTF log directory!");
                e.printStackTrace();
                System.exit(2);
            }
        }
    }

    protected final void generateJftfTestCasesDirectory() {
        try {
            Files.createDirectory(this.generateJftfTestCasesDirectoryPath());
        }
        catch(IOException e){
            if(!(e instanceof FileAlreadyExistsException)) {
                System.err.println("(CRITICAL) Failed to generate the JFTF test cases directory!");
                e.printStackTrace();
                System.exit(2);
            }
        }
    }

    protected final List<String> checkDirectoryIntegrity(){
        logger.LogInfo("Checking directory integrity");
        Map<Path, Boolean> directoryIntegrityMap = new HashMap<>();
        List<String> corruptedDirectoriesList = new ArrayList<>();
        this.populateJftfDirectoriesList().forEach(dir -> directoryIntegrityMap.put(dir,Files.exists(dir)));
        if(directoryIntegrityMap.containsValue(Boolean.FALSE)){
            logger.LogError("Directory integrity NOT OK! Attempting repair...");
            this.setupEnvironment();
        }
        else{
            logger.LogInfo("Directory integrity OK!");
            corruptedDirectoriesList.add("DIRECTORY INTEGRITY OK");
            return corruptedDirectoriesList;
        }
        this.populateJftfDirectoriesList().forEach(dir -> directoryIntegrityMap.put(dir,Files.exists(dir)));
        if(directoryIntegrityMap.containsValue(Boolean.FALSE)){
            logger.LogError("Failed to repair directory integrity!");
            for( Map.Entry<Path,Boolean> entry : directoryIntegrityMap.entrySet()){
                if(entry.getValue() == Boolean.FALSE){
                    corruptedDirectoriesList.add(entry.getKey().toString());
                }
            }
        }
        else {
            logger.LogInfo("Directory integrity repaired!");
            corruptedDirectoriesList.add("DIRECTORY INTEGRITY REPAIRED");
        }
        return corruptedDirectoriesList;
    }

    protected final List<String> checkSystemVariablesIntegrity(){
        logger.LogInfo("Checking system variables integrity");
        Map<String, String> systemVariablesIntegrityMap = new HashMap<>();
        List<String> corruptedSysvarList = new ArrayList<>();
        this.jftfSystemVariableKeyList.forEach(key -> systemVariablesIntegrityMap.put(key,System.getProperty(key)));
        if(systemVariablesIntegrityMap.containsValue(null)){
            logger.LogError("System variables integrity NOT OK! Attempting repair...");
            this.exportSystemVariables();
        }
        else{
            logger.LogInfo("System variables  integrity OK!");
            corruptedSysvarList.add("SYS_VAR INTEGRITY OK");
            return corruptedSysvarList;
        }
        this.jftfSystemVariableKeyList.forEach(key -> systemVariablesIntegrityMap.put(key,System.getProperty(key)));
        if(systemVariablesIntegrityMap.containsValue(null)){
            logger.LogError("Failed to repair system variables integrity!");
            for( Map.Entry<String,String> entry : systemVariablesIntegrityMap.entrySet()){
                if(entry.getValue() == null){
                    corruptedSysvarList.add(entry.getKey());
                }
            }
        }
        else {
            logger.LogInfo("System variables integrity repaired!");
            corruptedSysvarList.add("SYS_VAR INTEGRITY REPAIRED");
        }
        return corruptedSysvarList;
    }

    @Override
    public Path getJftfHomeDirectoryPath() {
        return this.generateJftfHomeDirectoryPath();
    }

    @Override
    public Path getJftfLogDirectoryPath(){
        return this.generateJftfLogDirectoryPath();
    }

    @Override
    public Path getJftfTestCasesDirectoryPath(){
        return this.generateJftfTestCasesDirectoryPath();
    }

    @Override
    public Map<String, List<String>> checkJftfEnvironmentIntegrity(){
        logger.LogInfo("Checking JFTF environment integrity");
        Map<String, List<String>> JftfIntegrityMap = new HashMap<>();
        JftfIntegrityMap.put("DIRECTORY",this.checkDirectoryIntegrity());
        JftfIntegrityMap.put("SYS_VAR",this.checkSystemVariablesIntegrity());
        return JftfIntegrityMap;
    }

    public static Path generateJavaLogFile(String currentContextApplicationID){
        String javaLogFileTimestamp = javaLogFileTimestampFormat.format(new Date())+".log";
        Path javaLogFilePath = Paths.get(osGenericJavaLogDirectory, String.format("jftf_%s_%s", currentContextApplicationID, javaLogFileTimestamp));
        try {
            Files.createFile(javaLogFilePath);
        }
        catch (IOException e){
            System.err.println("(CRITICAL) Failed to generate faut logger log file!");
            e.printStackTrace();
            System.exit(1);
        }
        return javaLogFilePath;
    }

    public static void setLogApplicationNameSystemVariable(LoggingContextInformation loggingContextInformation){
        try {
            System.setProperty(jftfLogAppNameSystemVariableKey, loggingContextInformation.getApplicationID());
        }
        catch (NullPointerException e){
            System.err.println("(CRITICAL) Failed to set logger application name!");
            e.printStackTrace();
            System.exit(1);
        }
        catch (IllegalArgumentException e){
            System.err.println("(CRITICAL) Logger application name is illegal!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
