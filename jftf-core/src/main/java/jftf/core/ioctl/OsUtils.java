package jftf.core.ioctl;

public final class OsUtils
{
    private static String OS = null;
    public static String getOsName()
    {
        try {
            if (OS == null) {
                OS = System.getProperty("os.name");
            }
        }
        catch(SecurityException e){
            System.err.println("Failed to get the name of the current operating system!");
            e.printStackTrace();
        }
        return OS;
    }
    public static boolean isWindows()
    {
        return getOsName().startsWith("Windows");
    }

    public static boolean isLinux(){
        return getOsName().startsWith("Linux");
    }
}