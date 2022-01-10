package jftf.lib.core.computer;

import jftf.lib.core.computer.JftfComputer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public final class JftfSequentialComputer extends JftfComputer {

    @Override
    public void run() {
        Boolean haltExecution = Boolean.FALSE;
        if(super.beforeTest.size() != 0 ){
            this.executeTestScenario(super.beforeTest);
            if(this.errorMessages.size() != 0){
                haltExecution = Boolean.TRUE;
            }
        }
        if(haltExecution != Boolean.TRUE) {
            this.executeTestScenario(super.testFixtures);
        }
        if(super.afterTest.size() !=0){
            this.executeTestScenario(super.afterTest);
        }
    }

    @Override
    public void executeTestScenario(List<Method> testMethodList){
        for(Method method : testMethodList) {
            logger.LogInfo(String.format("Executing test method '%s'", method.getName()));
            try {
                List<Object> parameterList = new ArrayList<Object>();
                for (Parameter ignored : method.getParameters()) {
                    System.out.println(ignored.getType());
                    parameterList.add(null);
                }
                if (parameterList.size() != 0) {
                    method.invoke(this.testObject, parameterList.toArray());
                } else {
                    method.invoke(this.testObject);
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                logger.LogError(String.format("Failed to execute test method '%s'!", method.getName()));
                System.err.printf("Failed to execute test method '%s'!%n", method.getName());
                e.printStackTrace();
                System.exit(1);
            }
            catch (InvocationTargetException e){
                StringBuilder stackTrace = new StringBuilder();
                for (StackTraceElement traceLine : e.getTargetException().getStackTrace()){
                    stackTrace.append(traceLine+"\n");
                }
                String errorMessage = String.format("~~~~~Error occurred in test method: '%s'\n\n~~~~~Error cause is: '%s'\n\n~~~~~Stack trace:\n\n%s\n\n\n",method.getName(),e.getCause().getMessage(),stackTrace);
                super.errorMessages.add(errorMessage);
            }
        }
    }
}
