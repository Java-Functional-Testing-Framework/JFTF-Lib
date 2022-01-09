package jftf.core.ioctl;

import jftf.core.logging.LoggingContextInformation;
import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class ConfigurationManager  {
    private CombinedConfiguration jftfModuleConfiguration;
    private final Path jftfCmdbConfigurationFilePath;
    private final Path jftfDaemonConfigurationFilePath;
    private final Path jftfLoggerConfigurationFilePath;
    private final List<Path> jftfConfigurationFilesPathList;
    private static ConfigurationManager configurationManagerInstance = null;
    public final static String daemonConfigurationName = "DAEMON_CONFIG";
    public final static String loggerConfigurationName = "LOGGER_CONFIG";
    public final static String cmdbConfigurationName = "CMDB_CONFIG";
    private final static List<String> validConfigurationNames = List.of(daemonConfigurationName,loggerConfigurationName,cmdbConfigurationName);
    // LOGGER CONFIG
    public final static String groupLoggerBehaviour = "behaviour";
    public final static String groupLoggerIp = "ip";
    public final static String groupLoggerDaemonContextInformation = "daemon_ctx_info";
    public final static String groupLoggerTestAppContextInformation = "jftf_test_ctx_info";
    public final static String keyLoggerEnableDebug = "enable_debug";
    private final static Boolean defaultValueLoggerEnableDebug = Boolean.FALSE;
    public final static String keyLoggerSyslogServerIp = "syslog_server_ip";
    private final static String defaultValueLoggerSyslogServerIp = "localhost";
    public final static String keyLoggerEnableLogging = "enable_logging";
    private final static Boolean defaultValueEnableLogging = Boolean.TRUE;
    public final static String keyLoggerAppId = "app_id";
    public final static String keyLoggerLogLevel = "log_level";
    private final static String defaultValueLoggerLogLevel = LoggingContextInformation.infoLogLevel.toString();
    public final static String keyLoggerAppender = "appender";
    private final static String defaultValueLoggerAppender = LoggingContextInformation.multiAppender;
    private final static String defaultValueLoggerDaemonAppId = "jftfDaemon";
    private final static String defaultValueLoggerTestAppId = "jftfTestApp";
    // CMDB_CONFIG
    public final static String groupCmdbCredentials = "credentials";
    public final static String keyCmdbIp = "ip";
    private final static String defaultValueCmdbIp = "localhost";
    public final static String keyCmdbUsername = "username";
    private final static String defaultValueCmdbUsername = "jftf";
    public final static String keyCmdbPassword = "password";
    private final static String defaultValueCmdbPassword = "";
    public final static String keyCmdbName = "db_name";
    private final static String defaultValueCmdbName = "jftf_cmdb";


    private final static Map<String,List<String>> loggerConfigurationMap = new HashMap<>();
    private final static Map<String,List<String>> daemonConfigurationMap = new HashMap<>();
    private final static Map<String,List<String>> cmdbConfigurationMap = new HashMap<>();
    public final static String valueNotFound = "N/A";

    private ConfigurationManager(Path jftfCmdbConfigurationFilePath, Path jftfDaemonConfigurationFilePath, Path jftfLoggerConfigurationFilePath){
        this.jftfCmdbConfigurationFilePath = jftfCmdbConfigurationFilePath;
        this.jftfDaemonConfigurationFilePath = jftfDaemonConfigurationFilePath;
        this.jftfLoggerConfigurationFilePath = jftfLoggerConfigurationFilePath;
        jftfConfigurationFilesPathList = List.of(this.jftfCmdbConfigurationFilePath,this.jftfDaemonConfigurationFilePath,this.jftfLoggerConfigurationFilePath);
        this.initConfigurationManager();
    }

    public static ConfigurationManager ConfigurationManagerFactory(Path jftfCmdbConfigurationFilePath, Path jftfDaemonConfigurationFilePath, Path jftfLoggerConfigurationFilePath){
        if(configurationManagerInstance == null){
            configurationManagerInstance = new ConfigurationManager(jftfCmdbConfigurationFilePath,jftfDaemonConfigurationFilePath,jftfLoggerConfigurationFilePath);
        }
        return configurationManagerInstance;
    }

    public void initConfigurationManager(){
        this.jftfModuleConfiguration = new CombinedConfiguration();
        this.populateConfigurationMaps();
        this.loadConfigurationFiles();
    }

    public Map<Path,Boolean> checkConfigurationFilesIntegrity(){
        Map<Path,Boolean> configurationFilesIntegrityMap = new HashMap<>();
        this.jftfConfigurationFilesPathList.forEach(file -> configurationFilesIntegrityMap.put(file, Files.exists(file)));
        return configurationFilesIntegrityMap;
    }

    public String getProperty(String configurationName, String configurationGroup, String configurationKey){
        if(validConfigurationNames.contains(configurationName)){
            if(Objects.equals(configurationName, loggerConfigurationName)){
                return this.getPropertyMapCheck(configurationName, configurationGroup, configurationKey, loggerConfigurationMap);
            }
            else if(Objects.equals(configurationName, daemonConfigurationName)){
                return this.getPropertyMapCheck(configurationName, configurationGroup, configurationKey, daemonConfigurationMap);
            }
            else if(Objects.equals(configurationName, cmdbConfigurationName)){
                return this.getPropertyMapCheck(configurationName, configurationGroup, configurationKey, cmdbConfigurationMap);
            }
        }
        return valueNotFound;
    }

    private String getPropertyMapCheck(String configurationName, String configurationGroup, String configurationKey, Map<String, List<String>> ConfigurationMap) {
        if(ConfigurationMap.containsKey(configurationGroup)){
            if(ConfigurationMap.get(configurationGroup).contains(configurationKey)){
                return this.jftfModuleConfiguration.getConfiguration(configurationName).getProperty(String.format("%s.%s", configurationGroup, configurationKey)).toString();
            }
            else{
                return valueNotFound;
            }
        }
        else{
            return valueNotFound;
        }
    }

    public boolean setProperty(String configurationName, String configurationGroup, String configurationKey, String configurationValue){
        if(validConfigurationNames.contains(configurationName)){
            if(Objects.equals(configurationName, loggerConfigurationName)){
                return setPropertyMapCheck(configurationName, configurationGroup, configurationKey, configurationValue, loggerConfigurationMap);
            }
            else if(Objects.equals(configurationName, daemonConfigurationName)){
                return setPropertyMapCheck(configurationName, configurationGroup, configurationKey, configurationValue, daemonConfigurationMap);
            }
            else if(Objects.equals(configurationName, daemonConfigurationName)){
                return setPropertyMapCheck(configurationName, configurationGroup, configurationKey, configurationValue, cmdbConfigurationMap);
            }
        }
        return Boolean.FALSE;
    }

    private boolean setPropertyMapCheck(String configurationName, String configurationGroup, String configurationKey, String configurationValue, Map<String, List<String>> ConfigurationMap) {
        if(ConfigurationMap.containsKey(configurationGroup)){
            if(ConfigurationMap.get(configurationGroup).contains(configurationKey)){
                this.jftfModuleConfiguration.getConfiguration(configurationName).setProperty(String.format("%s.%s", configurationGroup, configurationKey), configurationValue);
                return Boolean.TRUE;
            }
            else{
                return Boolean.FALSE;
            }
        }
        else{
            return Boolean.FALSE;
        }
    }

    private void loadConfigurationFiles(){
        this.generateConfigurationFiles();
        Parameters loggerParameters = new Parameters();
        Parameters daemonParameters = new Parameters();
        Parameters cmdbParameters = new Parameters();
        FileBasedConfigurationBuilder<XMLConfiguration> loggerFileBasedConfigurationBuilder = new FileBasedConfigurationBuilder<>(XMLConfiguration.class).configure(loggerParameters.xml().setFile(this.jftfLoggerConfigurationFilePath.toFile()));
        FileBasedConfigurationBuilder<XMLConfiguration> daemonFileBasedConfigurationBuilder = new FileBasedConfigurationBuilder<>(XMLConfiguration.class).configure(daemonParameters.xml().setFile(this.jftfDaemonConfigurationFilePath.toFile()));
        FileBasedConfigurationBuilder<XMLConfiguration> cmdbFileBasedConfigurationBuilder = new FileBasedConfigurationBuilder<>(XMLConfiguration.class).configure(cmdbParameters.xml().setFile(this.jftfCmdbConfigurationFilePath.toFile()));
        loggerFileBasedConfigurationBuilder.setAutoSave(Boolean.TRUE);
        daemonFileBasedConfigurationBuilder.setAutoSave(Boolean.TRUE);
        cmdbFileBasedConfigurationBuilder.setAutoSave(Boolean.TRUE);
        try {
            this.jftfModuleConfiguration.addConfiguration(loggerFileBasedConfigurationBuilder.getConfiguration(),loggerConfigurationName);
        }
        catch (ConfigurationException e){
            System.err.println("(CRITICAL) Failed to load the logger configuration XML file!");
            e.printStackTrace();
            System.exit(3);
        }
        try {
            this.jftfModuleConfiguration.addConfiguration(daemonFileBasedConfigurationBuilder.getConfiguration(),daemonConfigurationName);
        }
        catch (ConfigurationException e){
            System.err.println("(CRITICAL) Failed to load the jftfDaemon configuration XML file!");
            e.printStackTrace();
            System.exit(3);
        }
        try {
            this.jftfModuleConfiguration.addConfiguration(cmdbFileBasedConfigurationBuilder.getConfiguration(),cmdbConfigurationName);
        }
        catch (ConfigurationException e){
            System.err.println("(CRITICAL) Failed to load the CMDB configuration XML file!");
            e.printStackTrace();
            System.exit(3);
        }
    }

    private void populateConfigurationMaps(){
        loggerConfigurationMap.put(groupLoggerBehaviour,List.of(keyLoggerEnableDebug,keyLoggerEnableLogging));
        loggerConfigurationMap.put(groupLoggerIp,List.of(keyLoggerSyslogServerIp));
        loggerConfigurationMap.put(groupLoggerDaemonContextInformation,List.of(keyLoggerAppId,keyLoggerLogLevel,keyLoggerAppender));
        loggerConfigurationMap.put(groupLoggerTestAppContextInformation,List.of(keyLoggerAppId,keyLoggerLogLevel,keyLoggerAppender));
        cmdbConfigurationMap.put(groupCmdbCredentials,List.of(keyCmdbName,keyCmdbIp,keyCmdbUsername,keyCmdbPassword));
    }

    private void generateConfigurationFiles(){
        Map<Path,Boolean> configurationFilesIntegrityMap = this.checkConfigurationFilesIntegrity();
        for (Path ConfigFilePath : this.jftfConfigurationFilesPathList){
            if(configurationFilesIntegrityMap.get(ConfigFilePath) == Boolean.FALSE){
                if(ConfigFilePath == this.jftfLoggerConfigurationFilePath){
                    this.generateLoggerConfigurationFile();
                }
                else if(ConfigFilePath == this.jftfDaemonConfigurationFilePath){
                    this.generateDaemonConfigurationFile();
                }
                else if(ConfigFilePath == this.jftfCmdbConfigurationFilePath){
                    this.generateCmdbConfigurationFile();
                }
            }
        }
    }

    private void generateLoggerConfigurationFile(){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement(loggerConfigurationName.toLowerCase(Locale.ROOT));
            document.appendChild(rootElement);

            Element groupBehaviour = document.createElement(groupLoggerBehaviour);
            rootElement.appendChild(groupBehaviour);
            Element keyEnableDebug = document.createElement(keyLoggerEnableDebug);
            keyEnableDebug.appendChild(document.createTextNode(String.valueOf(defaultValueLoggerEnableDebug)));
            Element keyEnableLogging = document.createElement(keyLoggerEnableLogging);
            keyEnableLogging.appendChild(document.createTextNode(String.valueOf(defaultValueEnableLogging)));
            groupBehaviour.appendChild(keyEnableDebug);
            groupBehaviour.appendChild(keyEnableLogging);

            Element groupIp = document.createElement(groupLoggerIp);
            rootElement.appendChild(groupIp);
            Element keySyslogServerIp = document.createElement(keyLoggerSyslogServerIp);
            keySyslogServerIp.appendChild(document.createTextNode(defaultValueLoggerSyslogServerIp));
            groupIp.appendChild(keySyslogServerIp);

            Element groupDaemonContextInformation = document.createElement(groupLoggerDaemonContextInformation);
            rootElement.appendChild(groupDaemonContextInformation);
            Element keyDaemonAppId = document.createElement(keyLoggerAppId);
            keyDaemonAppId.appendChild(document.createTextNode(defaultValueLoggerDaemonAppId));
            Element keyDaemonLogLevel = document.createElement(keyLoggerLogLevel);
            keyDaemonLogLevel.appendChild(document.createTextNode(defaultValueLoggerLogLevel));
            Element keyDaemonAppender = document.createElement(keyLoggerAppender);
            keyDaemonAppender.appendChild(document.createTextNode(defaultValueLoggerAppender));
            groupDaemonContextInformation.appendChild(keyDaemonAppId);
            groupDaemonContextInformation.appendChild(keyDaemonLogLevel);
            groupDaemonContextInformation.appendChild(keyDaemonAppender);

            Element groupTestAppContextInformation = document.createElement(groupLoggerTestAppContextInformation);
            rootElement.appendChild(groupTestAppContextInformation);
            Element keyTestAppAppId = document.createElement(keyLoggerAppId);
            keyTestAppAppId.appendChild(document.createTextNode(defaultValueLoggerTestAppId));
            Element keyTestAppLogLevel = document.createElement(keyLoggerLogLevel);
            keyTestAppLogLevel.appendChild(document.createTextNode(defaultValueLoggerLogLevel));
            Element keyTestAppAppender = document.createElement(keyLoggerAppender);
            keyTestAppAppender.appendChild(document.createTextNode(defaultValueLoggerAppender));
            groupTestAppContextInformation.appendChild(keyTestAppAppId);
            groupTestAppContextInformation.appendChild(keyTestAppLogLevel);
            groupTestAppContextInformation.appendChild(keyTestAppAppender);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult streamResult = new StreamResult(this.jftfLoggerConfigurationFilePath.toFile());
            transformer.transform(source,streamResult);
        }
        catch (ParserConfigurationException e){
            System.err.println("(CRITICAL) XML Builder configuration error! (Logging configuration XML)");
            e.printStackTrace();
            System.exit(3);
        }
        catch (TransformerConfigurationException e){
            System.err.println("(CRITICAL) XML Transformer configuration error! (Logging configuration XML)");
            e.printStackTrace();
            System.exit(3);
        }
        catch (TransformerException e){
            System.err.println("(CRITICAL) Error while transforming DOM to XML file! (Logging configuration XML)");
            e.printStackTrace();
            System.exit(3);
        }
    }

    private void generateDaemonConfigurationFile(){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement(daemonConfigurationName.toLowerCase(Locale.ROOT));
            document.appendChild(rootElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult streamResult = new StreamResult(this.jftfDaemonConfigurationFilePath.toFile());
            transformer.transform(source,streamResult);
        }
        catch (ParserConfigurationException e){
            System.err.println("(CRITICAL) XML Builder configuration error! (jftfDaemon configuration XML)");
            e.printStackTrace();
            System.exit(3);
        }
        catch (TransformerConfigurationException e){
            System.err.println("(CRITICAL) XML Transformer configuration error! (jftfDaemon configuration XML)");
            e.printStackTrace();
            System.exit(3);
        }
        catch (TransformerException e){
            System.err.println("(CRITICAL) Error while transforming DOM to XML file! (jftfDaemon configuration XML)");
            e.printStackTrace();
            System.exit(3);
        }
    }

    private void generateCmdbConfigurationFile(){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement(daemonConfigurationName.toLowerCase(Locale.ROOT));
            document.appendChild(rootElement);

            Element groupCredentials = document.createElement(groupCmdbCredentials);
            rootElement.appendChild(groupCredentials);
            Element keyIp = document.createElement(keyCmdbIp);
            keyIp.appendChild(document.createTextNode(defaultValueCmdbIp));
            Element keyName = document.createElement(keyCmdbName);
            keyName.appendChild(document.createTextNode(defaultValueCmdbName));
            Element keyUsername = document.createElement(keyCmdbUsername);
            keyUsername.appendChild(document.createTextNode(defaultValueCmdbUsername));
            Element keyPassword = document.createElement(keyCmdbPassword);
            keyPassword.appendChild(document.createTextNode(defaultValueCmdbPassword));
            groupCredentials.appendChild(keyIp);
            groupCredentials.appendChild(keyName);
            groupCredentials.appendChild(keyUsername);
            groupCredentials.appendChild(keyPassword);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult streamResult = new StreamResult(this.jftfCmdbConfigurationFilePath.toFile());
            transformer.transform(source,streamResult);
        }
        catch (ParserConfigurationException e){
            System.err.println("(CRITICAL) XML Builder configuration error! (CMDB configuration XML)");
            e.printStackTrace();
            System.exit(3);
        }
        catch (TransformerConfigurationException e){
            System.err.println("(CRITICAL) XML Transformer configuration error! (CMDB configuration XML)");
            e.printStackTrace();
            System.exit(3);
        }
        catch (TransformerException e){
            System.err.println("(CRITICAL) Error while transforming DOM to XML file! (CMDB configuration XML)");
            e.printStackTrace();
            System.exit(3);
        }
    }
}
