package jftf.core.ioctl;

public class ControIOWindows extends ControlIO{
    private static final String applicationDirectoryNameUnix = "jftf";
    public ControIOWindows() {
        super.applicationDirectoryName = applicationDirectoryNameUnix;
        super.generateBaseDirectory();
    }
}
