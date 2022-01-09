package jftf.lib.core;


import java.util.Objects;

public class JftfVirtualMachineBuilder {
    private static JftfComputer jftfComputer = null;

    public void VirtualMachineFactory(String[] args, Class<?> testClasses) {
        if (jftfComputer == null) {
            JftfRunner jftfRunner;
            int cliExitCode = JftfCliParser.parseCli(args);
            if (cliExitCode != 0) {
                if (cliExitCode == 11) {
                    if (Objects.equals(JftfCliParser.getSelectedJftfRunner(), JftfCliParser.jftfDetachedRunner)) {
                        jftfRunner = new JftfDetachedRunner(testClasses);
                        jftfRunner.run();
                    }
                }
                else if(cliExitCode == 10){
                    System.out.println("Register");
                    System.exit(0);
                }
                else{
                    System.exit(cliExitCode);
                }
            }
            System.exit(0);
        }
    }
}
