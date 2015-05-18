package com.xkeshi.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * code编号生成
 *
 */
public class CodeUtil {

    private static AtomicInteger counter = new AtomicInteger(0);

    public static long getAtomicCounter() {
        if (counter.get() > 999999) {
            counter.set(1);
        }
        long time = System.currentTimeMillis();
        long returnValue = time * 180 + counter.incrementAndGet();
        return returnValue;
    }
    public static String getNewCode() {
        return String.valueOf(getAtomicCounter());
    }

    public static String getNewCode(String pre) {
        if (counter.get() > 999999) {
            counter.set(1);
        }
        long time = System.currentTimeMillis();
        long returnValue = time * 180 + counter.incrementAndGet();
        return StringUtils.join(pre, String.valueOf(returnValue));
    }

    private static long incrementAndGet() {
        return counter.incrementAndGet();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {

            System.out.println(CodeUtil.getAtomicCounter());
        }
    }
    
     
}