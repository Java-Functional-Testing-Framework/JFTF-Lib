package jftf.lib.core.meta;

import java.sql.Timestamp;

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