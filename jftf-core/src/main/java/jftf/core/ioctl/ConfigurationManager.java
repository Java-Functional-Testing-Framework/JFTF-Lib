package jftf.core.ioctl;

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
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfigurationManager  {
    private CombinedConfiguration jftfModuleConfiguration;
    private Path jftfDaemonConfigurationFilePath;
    private Path jftfLoggerConfigurationFilePath;
    private List<Path> jftfConfigurationFilesPathList;
    private static ConfigurationManager configurationManagerInstance = null;
    private final static String daemonConfigurationName = "DAEMON_CONFIG";
    private final static String loggerConfigurationName = "LOGGER_CONFIG";
    private final static List<String> configurationNameList = List.of(daemonConfigurationName,loggerConfigurationName);
    public final static String groupLoggerBehaviour = "behaviour";
    public final static String groupLoggerIp = "ip";
    public final static String keyLoggerEnableDebug = "enable_debug";
    private final static Boolean defaultValueLoggerEnableDebug = Boolean.FALSE;
    public final static String keyLoggerSyslogServerIp = "syslog_server_ip";
    public final static String defaultValueLoggerSyslogServerIp = "localhost";

    private ConfigurationManager(Path jftfDaemonConfigurationFilePath, Path jftfLoggerConfigurationFilePath){
        this.jftfDaemonConfigurationFilePath = jftfDaemonConfigurationFilePath;
        this.jftfLoggerConfigurationFilePath = jftfLoggerConfigurationFilePath;
        jftfConfigurationFilesPathList = List.of(this.jftfDaemonConfigurationFilePath,this.jftfLoggerConfigurationFilePath);
        this.jftfModuleConfiguration = new CombinedConfiguration();
        this.loadConfigurationFiles();
    }

    public Map<Path,Boolean> checkConfigurationFilesIntegrity(){
        Map<Path,Boolean> configurationFilesIntegrityMap = new HashMap<>();
        this.jftfConfigurationFilesPathList.forEach(file -> configurationFilesIntegrityMap.put(file, Files.exists(file)));
        return configurationFilesIntegrityMap;
    }

    public void loadConfigurationFiles(){
        Map<Path,Boolean> configurationFilesIntegrityMap = this.checkConfigurationFilesIntegrity();
        for (Path ConfigFilePath : this.jftfConfigurationFilesPathList){
            if(configurationFilesIntegrityMap.get(ConfigFilePath) == Boolean.FALSE){
                if(ConfigFilePath == this.jftfLoggerConfigurationFilePath){
                    this.generateLoggerConfigurationFile();
                }
            }
        }
        Parameters parameters = new Parameters();
        FileBasedConfigurationBuilder<XMLConfiguration> fileBasedConfigurationBuilder = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class).configure(parameters.xml().setFile(this.jftfLoggerConfigurationFilePath.toFile()));
        try {
            XMLConfiguration loggerXmlConfiguration = fileBasedConfigurationBuilder.getConfiguration();
            System.out.println(loggerXmlConfiguration.getProperty("ip.syslog_server_ip"));
        }
        catch (ConfigurationException e){
            System.err.println("(CRITICAL) Failed to load the logger configuration XML file!");
            e.printStackTrace();
            System.exit(3);
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
            groupBehaviour.appendChild(keyEnableDebug);

            Element groupIp = document.createElement(groupLoggerIp);
            rootElement.appendChild(groupIp);
            Element keySyslogServerIp = document.createElement(keyLoggerSyslogServerIp);
            keySyslogServerIp.appendChild(document.createTextNode(defaultValueLoggerSyslogServerIp));
            groupIp.appendChild(keySyslogServerIp);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult streamResult = new StreamResult(this.jftfLoggerConfigurationFilePath.toFile());
            transformer.transform(source,streamResult);
        }
        catch (ParserConfigurationException e){
            System.err.println("(CRITICAL) XML Builder configuration error!");
            e.printStackTrace();
            System.exit(3);
        }
        catch (TransformerConfigurationException e){
            System.err.println("(CRITICAL) XML Transformer configuration error!");
            e.printStackTrace();
            System.exit(3);
        }
        catch (TransformerException e){
            System.err.println("(CRITICAL) Error while transforming DOM to XML file!");
            e.printStackTrace();
            System.exit(3);
        }
    }

    public static ConfigurationManager ConfigurationManagerFactory(Path jftfDaemonConfigurationFilePath, Path jftfLoggerConfigurationFilePath){
        if(configurationManagerInstance == null){
            configurationManagerInstance = new ConfigurationManager(jftfDaemonConfigurationFilePath,jftfLoggerConfigurationFilePath);
        }
        return configurationManagerInstance;
    }
}
