package jftf.lib.jtest;

public class JftfDefaultRunner implements Runnable {
    private JftfTestCase runnableJftfTestCase = null;

    @Override
    public void run() {
        runnableJftfTestCase = new JftfTestCase();
        System.out.println("In test run!");
    }
}
