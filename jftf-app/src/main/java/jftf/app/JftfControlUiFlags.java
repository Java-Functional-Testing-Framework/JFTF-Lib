package jftf.app;

public class JftfControlUiFlags {
    public static Boolean reloadingTestCaseListFlag = Boolean.FALSE;
    public static Boolean testCaseSelectedFlag = Boolean.FALSE;
    public static Boolean settingTestCaseMetadataFlag = Boolean.FALSE;
    public static Boolean executingTestCaseFlag = Boolean.FALSE;
    private JftfControlUiFlags() {}

    public static void reloadingTestCaseList(){
        reloadingTestCaseListFlag = Boolean.TRUE;
    }

    public static void reloadedTestCaseList(){
        reloadingTestCaseListFlag = Boolean.FALSE;
    }

    public static void selectedTestCase(){
        testCaseSelectedFlag = Boolean.TRUE;
    }

    public static void unselectedTestCase(){
        testCaseSelectedFlag = Boolean.FALSE;
    }

    public static void settingTestCaseMetadata(){
        settingTestCaseMetadataFlag = Boolean.TRUE;
    }

    public static void setTestCaseMetadata(){
        settingTestCaseMetadataFlag = Boolean.FALSE;
    }

    public static void executingTestCase(){
        executingTestCaseFlag = Boolean.TRUE;
    }

    public static void executedTestCase(){
        executingTestCaseFlag = Boolean.FALSE;
    }
}
