package jftf.lib.core;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public final class JftfCliParser implements Callable<Integer> {
    private static String selectedJftfRunner = null;
    public final static String jftfDetachedRunner = "JftfDetachedRunner";
    private final static List<String> availableDaemonRunners = List.of();
    private final static List<String > availableDetachedRunners = List.of(jftfDetachedRunner);

    @Parameters(index = "0", description = "test runner used for executing test case", arity = "0")
    private String jftfRunner;

    @Option(names = {"-d", "--detached"}, description = "run the test case detached from the JFTF daemon")
    private boolean detachedRun = false;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display the command line argument help")
    private boolean helpRequested = false;

    @Option(names = {"-l", "--list-runner"}, description = "display the available runners for selected run options")
    private boolean listRunner = false;

    @Option(names = {"-r", "--register"}, description = "register test case in the jftf cmdb")
    private boolean registerTest = false;

    private JftfCliParser(){}

    public static int parseCli(String[] args){
        return new CommandLine(new JftfCliParser()).execute(args);
    }

    @Override
    public Integer call(){
        if(helpRequested){
            return 0;
        }
        if(registerTest){
            return 10;
        }
        if(listRunner){
            if(detachedRun){
                System.out.println(String.format("Available detached test runners are '%s'",availableDetachedRunners));
            }
            else{
                System.out.println(String.format("Available daemon test runners are '%s'",availableDaemonRunners));
            }
            return 0;
        }
        if(jftfRunner == null){
            System.out.println("Select test runner to execute the test case!");
            return 0;
        }
        if(detachedRun){
            if(availableDetachedRunners.contains(jftfRunner)){
                if(Objects.equals(jftfRunner, jftfDetachedRunner)){
                    selectedJftfRunner = jftfDetachedRunner;
                }
            }
            else{
                System.out.println(String.format("Runner '%s' not supported! Please refer to the available detached test runners list!",jftfRunner));
                return 0;
            }
            return 11;
        }
        else{
            System.out.println("Daemon runners are not yet implemented!");
            return 0; //12
        }
    }

    public static String getSelectedJftfRunner() {
        return selectedJftfRunner;
    }
}
