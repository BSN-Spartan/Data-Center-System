package com.spartan.dc.core.exception;


public abstract class AssertBusiness {


    public AssertBusiness() {

    }

    public static void isTrue(boolean expression, String code) {
        if (expression) {
            throw new GlobalException(code);
        }
    }

}
