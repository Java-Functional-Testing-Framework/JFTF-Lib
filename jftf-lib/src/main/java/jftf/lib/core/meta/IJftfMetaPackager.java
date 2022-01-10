package jftf.lib.core.meta;

public interface IJftfMetaPackager {
    int registerTestCase(Class<?> testClasses);
    int lookupTestCase(JftfTestCaseMetadata jftfTestCaseMetadata);
    JftfTestCaseMetadata generateTestCaseMetadata(Class<?> testClasses);
    void insertTestReportInformation(JftfTestReportInformation jftfTestReportInformation);
}
