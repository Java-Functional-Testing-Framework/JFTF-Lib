package jftf.lib.core.meta;

import java.nio.file.Path;

public class JftfTestCaseMetadata {
    private String testName;
    private String featureGroup;
    private String testGroup;
    private Path testPath;
    private String testVersion;

    public JftfTestCaseMetadata(String testName, String featureGroup, String testGroup, Path testPath, String testVersion) {
        this.testName = testName;
        this.featureGroup = featureGroup;
        this.testGroup = testGroup;
        this.testPath = testPath;
        this.testVersion = testVersion;
    }

    @Override
    public String toString() {
        return "JftfTestCaseMetadata{" +
                "testName='" + testName + '\'' +
                ", featureGroup='" + featureGroup + '\'' +
                ", testGroup='" + testGroup + '\'' +
                ", testPath=" + testPath +
                ", testVersion='" + testVersion + '\'' +
                '}';
    }

    public String getTestName() {
        return this.testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getFeatureGroup() {
        return this.featureGroup;
    }

    public void setFeatureGroup(String featureGroup) {
        this.featureGroup = featureGroup;
    }

    public String getTestGroup() {
        return this.testGroup;
    }

    public void setTestGroup(String testGroup) {
        this.testGroup = testGroup;
    }

    public Path getTestPath() {
        return this.testPath;
    }

    public void setTestPath(Path testPath) {
        this.testPath = testPath;
    }

    public String getTestVersion() {
        return this.testVersion;
    }

    public void setTestVersion(String testVersion) {
        this.testVersion = testVersion;
    }
}
