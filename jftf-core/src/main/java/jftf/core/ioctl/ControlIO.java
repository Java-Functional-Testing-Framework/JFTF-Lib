package jftf.core.ioctl;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ControlIO implements IControlIO{
    private static String homeDirectoryURI = null;
    protected String applicationDirectoryName = null;
    protected static String osGenericJavaLogDirectory = System.getProperty("java.io.tmpdir");
    private static final SimpleDateFormat javaLogFileTimestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
    private static Path applicationHomeDirectory = null;
    protected static String getVarHomeDirectory() {
        if(homeDirectoryURI == null){
            try{
                homeDirectoryURI = System.getProperty("user.home");
            }
            catch(SecurityException e){
                System.err.println("Failed to get the home directory URI!");
                e.printStackTrace();
            }
        }
        return homeDirectoryURI;
    }

    protected final Path getVarApplicationHomeDirectory(){
        if(applicationHomeDirectory == null){
            try{
                applicationHomeDirectory = Paths.get(getVarHomeDirectory(),this.applicationDirectoryName);
            }
            catch(InvalidPathException e){
                System.err.println("Invalid system path for the JFTF directory!");
                e.printStackTrace();
            }
        }
        return applicationHomeDirectory;
    }

    protected final void generateBaseDirectory() {
        try {
            Files.createDirectory(this.getVarApplicationHomeDirectory());
        }
        catch(IOException e){
            if(!(e instanceof FileAlreadyExistsException)) {
                System.err.println("Failed to generate the JFTF directory!");
                e.printStackTrace();
            }
        }
        catch(SecurityException e){
            System.err.println("Failed to generate the JFTF directory!");
            e.printStackTrace();
        }
    }

    @Override
    public Path getBaseDirectoryPath() {
        if(this.applicationDirectoryName != null){
            try {
                if(Files.exists(this.getVarApplicationHomeDirectory())){
                    return this.getVarApplicationHomeDirectory();
                }
            }
            catch (SecurityException e){
                System.err.println("Failed to get the path for the JFTF directory!");
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Path generateJavaLogFile(String currentContextApplicationID){
        String javaLogFileTimestamp = javaLogFileTimestampFormat.format(new Date())+".log";
        Path javaLogFilePath = Paths.get(osGenericJavaLogDirectory, String.format("jftf_%s_%s", currentContextApplicationID, javaLogFileTimestamp));
        try {
            Files.createFile(javaLogFilePath);
        }
        catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        return javaLogFilePath;
    }
}
