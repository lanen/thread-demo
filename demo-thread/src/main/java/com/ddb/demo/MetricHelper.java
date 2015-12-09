package com.ddb.demo;

import com.codahale.metrics.*;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * @author evan
 * @Date 2015年12月09日T22:11
 */
public class MetricHelper {

    private static class Singleton {
        public static final MetricHelper INSTSNCE = new MetricHelper();
    }
    public static MetricHelper getInstance() {
      return Singleton.INSTSNCE;
    }
    private MetricHelper(){
    }

    private ScheduledReporter reporter;


    MetricRegistry registry = new MetricRegistry();

    public void start(){


        reporter  = ConsoleReporter.forRegistry(registry)
                //.outputTo(LoggerFactory.getLogger("metrics"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
              .build();

        reporter.start(5,TimeUnit.SECONDS);

    }

    public void cpu() throws Exception{

        Sigar sigar = new Sigar();
        final CpuPerc cpuPerc = sigar.getCpuPerc();
        registry.register(MetricRegistry.name("用户使用率"),
                new Gauge<Double>() {
                    @Override
                    public Double getValue() {
                        return cpuPerc.getUser();
                    }
                });

        registry.register(MetricRegistry.name("系统使用率"),
                new Gauge<Double>() {
                    @Override
                    public Double getValue() {
                        return cpuPerc.getSys();
                    }
                });
        registry.register(MetricRegistry.name("当前等待率"),
                new Gauge<Double>() {
                    @Override
                    public Double getValue() {
                        return cpuPerc.getWait();
                    }
                });
        registry.register(MetricRegistry.name("当前空闲率"),
                new Gauge<Double>() {
                    @Override
                    public Double getValue() {
                        return cpuPerc.getIdle();
                    }
                });
        registry.register(MetricRegistry.name("总的使用率"),
                new Gauge<Double>() {
                    @Override
                    public Double getValue() {
                        return cpuPerc.getCombined();
                    }
                });

    }

    public void forQueue(final Queue queue,Class clazz){

        registry.register(MetricRegistry.name( "QueueSize"),
                new Gauge<Integer>() {
                    @Override
                    public Integer getValue() {
                        return queue.size();
                    }
                });


    }
}
