package com.wt2024.points.restful.backend;

import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/9 16:43
 * @project points3.0:com.wt2024.points.restful.backend
 */
public abstract class BaseTest {

    private static AtomicInteger count = new AtomicInteger(0);

    @BeforeEach
    public void before() {
        count.set(0);
        this.setStep("main");
    }

    protected void setStep(String message) {
        int index = count.getAndAdd(1);

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement father = stackTrace[1];
        StackTraceElement log = stackTrace[2];
        String tag = null;
        if (log != null) {
            tag = log.getClassName() + "#" + log.getMethodName();
        }
        if (tag == null) {
            tag = father.getClassName() + "#" + father.getMethodName();
        }
        Thread.currentThread().setName(tag + "-" + index + "-" + message);
    }
}
