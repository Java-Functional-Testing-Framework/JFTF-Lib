package jftf.core.daemon;

import jftf.core.JftfModule;
import jftf.core.ioctl.ConfigurationManager;

public class InitDaemon {
    public static void main(String[] args) {
        JftfModule.startupSequence(ConfigurationManager.groupLoggerDaemonContextInformation);
    }
}
