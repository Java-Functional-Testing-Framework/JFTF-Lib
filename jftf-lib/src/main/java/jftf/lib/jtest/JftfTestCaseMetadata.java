package jftf.lib.jtest;

import java.nio.file.Path;

public class JftfTestCaseMetadata implements java.io.Serializable{
    private static JftfTestCaseMetadata jftfTestCaseMetadata = null;
    private final String testCaseId;
    private final String testCaseFeatureGroup;
    private final String testCaseExecutableName;
    private final Path testCasePath;

    private JftfTestCaseMetadata(String testCaseId, String testCaseFeatureGroup, String testCaseExecutableName, Path testCasePath) {
        this.testCaseId = testCaseId;
        this.testCaseFeatureGroup = testCaseFeatureGroup;
        this.testCaseExecutableName = testCaseExecutableName;
        this.testCasePath = testCasePath;
    }

    public static JftfTestCaseMetadata generateTestCaseMetadata(String testCaseId, String testCaseFeatureGroup, String testCaseExecutableName, Path testCasePath){
        if(jftfTestCaseMetadata == null){
            jftfTestCaseMetadata = new JftfTestCaseMetadata(testCaseId,testCaseFeatureGroup,testCaseExecutableName,testCasePath);
        }
        return jftfTestCaseMetadata;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public String getTestCaseFeatureGroup() {
        return testCaseFeatureGroup;
    }

    public String getTestCaseExecutableName() {
        return testCaseExecutableName;
    }

    public Path getTestCasePath() {
        return testCasePath;
    }
}
