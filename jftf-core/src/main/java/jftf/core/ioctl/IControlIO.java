package jftf.core.ioctl;

import java.nio.file.Path;

public interface IControlIO {
    Path getBaseDirectoryPath();
    //void generateFatalLogFile(); // underlying generate/getLogDirectory
    //File getFatalLogFile();
}
