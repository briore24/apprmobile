package com.ibm.maximo.technician.listener;

import org.testng.ITestResult;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.SkipException;
import com.ibm.maximo.technician.framework.TechnicianTest;

public class FastFailClass implements IInvokedMethodListener {

    private boolean hasFailures = false;

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        synchronized (this) {
            if (hasFailures) {
                throw new SkipException("Skipping this test as preceding test got failed");
            }
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod() && !testResult.isSuccess()) {
            synchronized (this) {
                hasFailures = true;
                TechnicianTest.woStatusToCompleted();
            }
        }
    }
}
