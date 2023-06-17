package jftf.lib.core;


import jftf.lib.core.computer.JftfComputer;
import jftf.lib.core.meta.JftfMetaPackager;
import jftf.lib.core.runner.JftfDetachedRunner;
import jftf.lib.core.runner.JftfRunner;

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
                else if(cliExitCode == 9){
                    JftfMetaPackager jftfMetaPackager = JftfMetaPackager.JftfMetaPackagerFactory();
                    System.exit(jftfMetaPackager.registerTestCaseJFTFCore(testClasses));
                }
                else if(cliExitCode == 10){
                    JftfMetaPackager jftfMetaPackager = JftfMetaPackager.JftfMetaPackagerFactory();
                    System.exit(jftfMetaPackager.registerTestCase(testClasses));
                }
                else{
                    System.exit(cliExitCode);
                }
            }
            System.exit(0);
        }
    }
}
