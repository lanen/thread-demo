package com.ddb.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author evan
 * @Date 2015年12月09日T21:36
 */
public class ThreadAndQueue extends AbstractThreadTest {

    protected int threadCount;

    private static final Logger logger = LoggerFactory.getLogger(ThreadAndQueue.class);

    private List<Thread> threadList ;

    private CountDownLatch latch;

    /**
     * 保险值
     */
    public static final int LATCH_VALUE = (int)(Integer.MAX_VALUE * 0.75);

    private boolean goingStop=false;

    @Override
    public void start(int threadCount) {
        this.threadCount = threadCount;
        init();
    }

    @Override
    protected void doInit() {

        if (null!=threadList){
            for (Thread thread : threadList) {
                thread.interrupt();
            }
        }

        threadList = new ArrayList<Thread>(threadCount+1);

        latch = new CountDownLatch(threadCount);

        for(int i=0;i<threadCount;i++){
            threadList.add(produce());
        }

        MetricHelper.getInstance().forQueue(this.queue,ThreadAndQueue.class);

        try {
            MetricHelper.getInstance().cpu();
        } catch (Exception e) {
            logger.error("",e);
        }

        MetricHelper.getInstance().start();

        counsume().start();

        for (Thread thread : threadList) {
            thread.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("",e);
        }

        logger.info("finish test");
    }

    private Queue<String> queue = new LinkedBlockingQueue<String>();

    private Thread counsume(){

        Thread t = new Thread(new Runnable() {
            @Override
            public synchronized void run() {

                while(true){

                    String peek = queue.poll();

                    if(null == peek){
                        try {
                            wait(50);
                        } catch (InterruptedException e) {
                            logger.error("",e);
                        }
                    }

                    if(queue.size()>LATCH_VALUE){
                        goingStop=true;
                    }
                }


            }
        });

        return t;
    }

    private Thread produce(){


        Thread t = new Thread(new Runnable() {
            @Override
            public synchronized void run() {

                while(!goingStop){

                    queue.add("word");

                }
                latch.countDown();
            }
        });

        return t;

    }
}
