package jftf.lib.jtest;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public class JftfRunner implements Callable<Integer> {
    private final static String jftfDefaultRunner = "JftfDefaultRunner";
    private final static List<String> availableRunners = List.of(jftfDefaultRunner);

    @Option(names = {"-r","--runner"}, description = "JftfDefaultRunner, ...")
    private String jftfRunner = "JftfDefaultRunner";

    @Option(names = {"-d","--detached"}, description = "run the test case detached from the JFTF daemon")
    private boolean detachedRun = false;

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "display the command line argument help")
    private boolean helpRequested = false;

    @Override
    public Integer call() {
        if(!helpRequested) {
            if (!availableRunners.contains(this.jftfRunner)) {
                System.err.println(String.format("Runner '%s' is not available!", this.jftfRunner));
                System.err.println(String.format("Available test runners are %s", availableRunners));
                return 1;
            }
            else{
                if(Objects.equals(this.jftfRunner, jftfDefaultRunner)){
                    JftfDefaultRunner jftfDefaultRunner = new JftfDefaultRunner();
                    jftfDefaultRunner.run();
                }
            }
        }
        return 0;
    }

    public static void main(String[] args, Class<?> testClass, Object testInstance) throws InvocationTargetException, IllegalAccessException {
        int exitCode =  new CommandLine(new JftfRunner()).execute(args);
        System.exit(exitCode);
    }

}
