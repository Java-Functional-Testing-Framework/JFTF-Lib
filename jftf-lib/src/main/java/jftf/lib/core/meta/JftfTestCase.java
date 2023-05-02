package jftf.lib.core.meta;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class JftfTestCase {
    private int metaDataId;
    private Timestamp firstExecution;
    private Timestamp lastExecution;
    Boolean executed;

    public JftfTestCase(int metaDataId, Timestamp firstExecution, Timestamp lastExecution, Boolean executed) {
        this.metaDataId = metaDataId;
        this.firstExecution = firstExecution;
        this.lastExecution = lastExecution;
        this.executed = executed;
    }

    public JftfTestCase(List<String> packagedTestCaseInformation){
        this.metaDataId = Integer.parseInt(packagedTestCaseInformation.get(0));
        try {
            this.firstExecution = Timestamp.valueOf(packagedTestCaseInformation.get(1));
        }
        catch (IllegalArgumentException e){
            this.firstExecution = null;
        }
        try {
            this.lastExecution = Timestamp.valueOf(packagedTestCaseInformation.get(2));
        }
        catch (IllegalArgumentException e){
            this.lastExecution = null;
        }
        if(Objects.equals(packagedTestCaseInformation.get(3), "1")){
            this.executed = Boolean.TRUE;
        }
        else{
            this.executed = Boolean.FALSE;
        }
    }

    public int getMetaDataId() {
        return this.metaDataId;
    }

    public void setMetaDataId(int metaDataId) {
        this.metaDataId = metaDataId;
    }

    public Timestamp getFirstExecution() {
        return this.firstExecution;
    }

    public void setFirstExecution(Timestamp firstExecution) {
        this.firstExecution = firstExecution;
    }

    public Timestamp getLastExecution() {
        return this.lastExecution;
    }

    public void setLastExecution(Timestamp lastExecution) {
        this.lastExecution = lastExecution;
    }

    public Boolean getExecuted() {
        return this.executed;
    }

    public void setExecuted(Boolean executed) {
        this.executed = executed;
    }
}