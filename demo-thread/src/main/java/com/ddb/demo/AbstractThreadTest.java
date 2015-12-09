package com.ddb.demo;

/**
 * @author evan
 * @Date 2015年12月09日T21:39
 */
public abstract class AbstractThreadTest implements ThreadTest{

    private boolean init;

    protected void init(){

        if (this.init){
            return;
        }
        this.init = true;

        doInit();

    }

    protected abstract void doInit();
}
