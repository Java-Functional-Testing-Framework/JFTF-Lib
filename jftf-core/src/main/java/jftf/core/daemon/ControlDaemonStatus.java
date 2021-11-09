package jftf.core.daemon;

public class ControlDaemonStatus {
    public static ControlDaemonStatus controlDaemonStatus = null;
    public static final String RUNNING_STATUS = "RUNNING";
    public static final String FAULT_STATUS = "FAULT";
    public static final String WAITING_STATUS = "WAITING";
    private String currentRunStatus;
    private ControlDaemonStatus(){
        ;
    }
    public static ControlDaemonStatus getControlDaemonStatus(){
        if(controlDaemonStatus == null)
            controlDaemonStatus = new ControlDaemonStatus();

            return controlDaemonStatus;
    }
}
