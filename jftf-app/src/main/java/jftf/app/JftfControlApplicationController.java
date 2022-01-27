package jftf.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import jftf.core.JftfModule;
import jftf.core.ioctl.ConfigurationManager;
import jftf.core.ioctl.DatabaseDriver;
import jftf.lib.core.meta.JftfTestCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class JftfControlApplicationController extends JftfModule implements Initializable  {
    private final List<JftfTestCase> jftfTestCaseList = new ArrayList<>();
    private List<Integer> testCaseIds = new ArrayList<>();
    @FXML
    private GridPane mainPane;
    @FXML
    private TableView<JftfTestCase> testTableView;
    @FXML
    private Text testNotSelectedText;
    @FXML
    private VBox testInformationVBox;
    @FXML
    private Button reloadTestCaseListButton;
    @FXML
    private TextArea testNameTextArea;
    @FXML
    private TextArea featureGroupTextArea;
    @FXML
    private TextArea testGroupTextArea;
    @FXML
    private TextArea testVersionTextArea;
    @FXML
    private Button executeTestCaseButton;
    @FXML
    private TextArea testOutputTextArea;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        JftfModule.startupSequence(ConfigurationManager.groupLoggerControlAppContextInformation);
        DatabaseDriver.DatabaseDriverFactory();
        logger.LogInfo("Started JFTF control application!");
        this.populateTestTableView();
    }

    @FXML
    private void reloadTestTableView(){
        if(JftfControlUiFlags.reloadingTestCaseListFlag == Boolean.FALSE) {
            JftfControlUiFlags.reloadingTestCaseList();
            this.reloadTestCaseListButton.setDisable(Boolean.TRUE);
            this.testTableView.getItems().clear();
            this.jftfTestCaseList.clear();
            this.testCaseIds.clear();
            this.populateTestTableView();
        }
    }

    @FXML
    private void tableOnClickEvent(){
        if(JftfControlUiFlags.settingTestCaseMetadataFlag == Boolean.FALSE){
            this.updateTestCaseMetadata();
        }
        this.changeVisibilityExecuteButton();
        this.changeVisibilityTestOutputArea();
    }

    @FXML
    private void executeTestCaseOnClickEvent(){
        if(JftfControlUiFlags.executingTestCaseFlag == Boolean.FALSE) {
            this.executeTestCaseButton.setDisable(Boolean.TRUE);
            this.executeTestCase();
            this.executeTestCaseButton.setDisable(Boolean.FALSE);
        }
    }

    private void executeTestCase(){
        JftfControlUiFlags.executingTestCase();
        Integer selectedTestMetadataId = this.testTableView.getSelectionModel().getSelectedItem().getMetaDataId();
        List<String> packagedTestCaseMetadata = this.retrievePackagedTestCaseMetadata(selectedTestMetadataId);
        Path testJarPath = Path.of(packagedTestCaseMetadata.get(3));
        if(Files.exists(testJarPath)){
            Path testExecutablePath = Path.of(testJarPath.getParent().getParent().toString(),"bin",FilenameUtils.getBaseName(testJarPath.toString()));
            if(Files.exists(testExecutablePath)) {
                Alert executingAlert = new Alert(Alert.AlertType.INFORMATION);
                logger.LogInfo(String.format("Executing test case with metadata Id '%s'!", selectedTestMetadataId));
                executingAlert.setContentText(String.format("Executing test case with metadata Id '%s'", selectedTestMetadataId));
                executingAlert.show();
                ProcessBuilder jftfTestCaseProcessBuilder = new ProcessBuilder(testExecutablePath.toString(), "-d", "JftfDetachedRunner");
                try {
                    String testCaseOutput = IOUtils.toString(jftfTestCaseProcessBuilder.start().getInputStream(), StandardCharsets.UTF_8);
                    logger.LogInfo(String.format("Executed test case with metadata Id '%s'!", selectedTestMetadataId));
                    this.testOutputTextArea.setVisible(Boolean.TRUE);
                    this.testOutputTextArea.setText(testCaseOutput);
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert testExecutionFailureAlert = new Alert(Alert.AlertType.ERROR);
                    testExecutionFailureAlert.setContentText("Test case failed to execute!");
                    testExecutionFailureAlert.show();
                }
            }
            else{
                Alert testNotFoundAlert = new Alert(Alert.AlertType.ERROR);
                testNotFoundAlert.setContentText("Test executable not found! Aborting!");
                testNotFoundAlert.show();
            }
        }
        else{
            Alert testNotFoundAlert = new Alert(Alert.AlertType.ERROR);
            testNotFoundAlert.setContentText("Test executable not found! Aborting!");
            testNotFoundAlert.show();
        }
        JftfControlUiFlags.executedTestCase();
    }

    private List<String> retrievePackagedTestCaseMetadata(Integer metadataId){
        logger.LogDebug(String.format("Retrieving packaged test case metadata with Id '%s'", metadataId));
        JftfControlUiFlags.selectedTestCase();
        this.testNotSelectedText.setVisible(Boolean.FALSE);
        List<String> packagedTestCaseMetadata = databaseDriver.getTestCaseMetadata(metadataId);
        logger.LogDebug("Retrieved packaged test case metadata!");
        return packagedTestCaseMetadata;
    }

    private void updateTestCaseMetadata(){
        if(testTableView.getSelectionModel().getSelectedItem() != null) {
            JftfControlUiFlags.settingTestCaseMetadata();
            Integer selectedTestMetadataId = testTableView.getSelectionModel().getSelectedItem().getMetaDataId();
            List<String> packagedTestCaseMetadata = this.retrievePackagedTestCaseMetadata(selectedTestMetadataId);
            this.testNameTextArea.setText(packagedTestCaseMetadata.get(0));
            this.testGroupTextArea.setText(packagedTestCaseMetadata.get(1));
            this.featureGroupTextArea.setText(packagedTestCaseMetadata.get(2));
            this.testVersionTextArea.setText(packagedTestCaseMetadata.get(4));
            this.testInformationVBox.setVisible(Boolean.TRUE);
            JftfControlUiFlags.setTestCaseMetadata();
        }
    }

    private void changeVisibilityExecuteButton(){
        if(JftfControlUiFlags.testCaseSelectedFlag == Boolean.TRUE){
            this.executeTestCaseButton.setVisible(Boolean.TRUE);
        }
        else{
            this.executeTestCaseButton.setVisible(Boolean.FALSE);
        }
    }

    private void changeVisibilityTestOutputArea(){
        this.testOutputTextArea.setVisible(Boolean.FALSE);
    }

    private void populateTestTableView() {
        logger.LogInfo("Populating test table in the JFTF control application");
        this.testCaseIds = databaseDriver.getTestCaseIds();
        logger.LogDebug(String.format("Found '%s' test cases registered in the JFTF CMDB!",testCaseIds.size()));
        for (Integer testId : this.testCaseIds){
            logger.LogDebug(String.format("Retrieving info for test case '%s'",testId));
            List<String> packagedTestCaseInformation = databaseDriver.getTestCase(testId);
            this.jftfTestCaseList.add(new JftfTestCase((packagedTestCaseInformation)));
            logger.LogDebug(String.format("Retrieved info for test case '%s'!",testId));
        }
        ObservableList<JftfTestCase> tableData = FXCollections.observableArrayList();
        tableData.addAll(this.jftfTestCaseList);
        ObservableList<TableColumn<JftfTestCase, ?>> testTableColumns = this.testTableView.getColumns();
        testTableColumns.get(0).setCellValueFactory(new PropertyValueFactory<>("metaDataId"));
        testTableColumns.get(1).setCellValueFactory(new PropertyValueFactory<>("firstExecution"));
        testTableColumns.get(2).setCellValueFactory(new PropertyValueFactory<>("lastExecution"));
        testTableColumns.get(3).setCellValueFactory(new PropertyValueFactory<>("executed"));
        this.testTableView.setItems(tableData);
        logger.LogInfo("Successfully populated test table in the JFTF control application!");
        JftfControlUiFlags.reloadedTestCaseList();
        JftfControlUiFlags.unselectedTestCase();
        this.testInformationVBox.setVisible(Boolean.FALSE);
        this.testNotSelectedText.setVisible(Boolean.TRUE);
        this.changeVisibilityExecuteButton();
        this.changeVisibilityTestOutputArea();
        reloadTestCaseListButton.setDisable(Boolean.FALSE);
    }
}