package jftf.lib.core.meta;

import java.sql.Timestamp;

public interface IJftfMetaPackager {
    int registerTestCase(Class<?> testClasses);
    int lookupTestCase(JftfTestCaseMetadata jftfTestCaseMetadata);
    JftfTestCaseMetadata generateTestCaseMetadata(Class<?> testClasses);
    void insertTestReportInformation(JftfTestReportInformation jftfTestReportInformation);
    void updateTestCaseInformation(Timestamp lastExecution, Boolean executed);
}
