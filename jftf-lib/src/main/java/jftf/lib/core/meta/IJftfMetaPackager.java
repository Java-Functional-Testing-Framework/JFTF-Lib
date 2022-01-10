package jftf.lib.core.meta;

import java.sql.Timestamp;

public interface IJftfMetaPackager {
    int registerTestCase(Class<?> testClasses);
    int lookupTestCase(JftfTestCaseMetadata jftfTestCaseMetadata);
    JftfTestCaseMetadata generateTestCaseMetadata(Class<?> testClasses);
    void insertTestReportInformation(JftfTestReportInformation jftfTestReportInformation);
    void updateTestCaseInformation(String updateMode, Timestamp firstExecution, Timestamp lastExecution, Boolean executed);
}
