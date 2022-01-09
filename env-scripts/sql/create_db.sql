use jftf_cmdb;
drop table if exists TestReports;
drop table if exists TestReportInformation;
drop table if exists TestCases;
drop table if exists TestCaseMetadata;
create table `TestCaseMetadata` (
                                    metadataId int not null auto_increment,
                                    testName varchar(255) not null,
                                    featureGroup varchar(255) default null,
                                    testGroup varchar(255) not null,
                                    testPath varchar(512) not null,
                                    testVersion varchar(255) not null,
                                    primary key(metaDataId)
);
create table `TestCases` (
                             testId int not null auto_increment,
                             metaDataId int not null,
                             firstExecution timestamp null default null,
                             lastExecution timestamp null default null,
                             executed boolean not null default false,
                             primary key (testId),
                             constraint fk_test_metadata
                                foreign key (metaDataId) references TestCaseMetadata(metaDataId)
                                on delete cascade
                                on update restrict
);
create table `TestReportInformation` (
                                         testReportInformationId int not null auto_increment,
                                         testId int not null,
                                         startupTimestamp  timestamp not null,
                                         endTimestamp timestamp not null,
                                         testDuration time not null,
                                         errorMessages mediumtext default null,
                                         loggerOutput mediumtext default null,
                                         executionResult varchar(255) not null,
                                         primary key(testReportInformationId),
                                         constraint fk_test_report_id
                                            foreign key(testId) references TestCases(testId)
                                            on delete cascade
                                            on update restrict
);
create table `TestReports` (
                               reportId int not null auto_increment,
                               testId int not null,
                               testReportInformationId int not null,
                               primary key(reportId),
                               constraint fk_test_id
                                    foreign key(testId) references TestCases(testID)
                                    on delete cascade
                                    on update restrict,
                               foreign key(testReportInformationId) references TestReportInformation(testReportInformationId)
);