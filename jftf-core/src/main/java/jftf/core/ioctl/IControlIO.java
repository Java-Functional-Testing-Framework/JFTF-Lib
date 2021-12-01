package jftf.core.ioctl;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface IControlIO {
    Path getJftfHomeDirectoryPath();
    Path getJftfLogDirectoryPath();
    Path getJftfTestCasesDirectoryPath();
    Path getJftfConfigDirectoryPath();
    Map<String, List<String>> checkJftfEnvironmentIntegrity();
    ConfigurationManager getConfigurationManager();
}
