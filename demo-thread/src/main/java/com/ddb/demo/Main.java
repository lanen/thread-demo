package com.ddb.demo;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author evan
 * @Date 2015年12月09日T21:14
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        initOptions();

        if ( ! parseArgs(args)){
            logger.debug("命令解析错误");
            HelpFormatter hf = new HelpFormatter();
            String formatstr = " -t 设置线程数 ";

            hf.printHelp(formatstr, "", options, "");
            System.exit(-1);
        }

        int threadCount = Runtime.getRuntime().availableProcessors()*2 + 1;

        if (commandLine.hasOption("thread")){
            try{
                threadCount = Integer.valueOf(commandLine.getOptionValue("thread"));
                if(threadCount<=0){
                    threadCount = Runtime.getRuntime().availableProcessors()*2 + 1;
                }
            }catch (Throwable e){
                logger.error("",e);
            }
        }
        logger.info("create thread {}", threadCount);

        ThreadTest threadTest = new ThreadAndQueue();
        threadTest.start(threadCount);

    }

    public static Options options = new Options();


    public static CommandLine commandLine = null;

    public static void initOptions(){

        options.addOption("h","help",false,"打印帮助信息");

        options.addOption(Option.builder("t")
                .longOpt("thread")
                .hasArg()
                .desc("启动多少线程")
                .type(Integer.class)
                .build());

    }

    public static boolean parseArgs(String[] args){

        try {
            CommandLineParser parser = new DefaultParser();

            // parse the command line arguments
            commandLine = parser.parse( options, args );

        } catch( ParseException exp ) {
            logger.error("",exp);
            return false;
        }
        return true;
    }
}
