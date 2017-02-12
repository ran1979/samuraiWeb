package com.samuriex.analyze;

import org.springframework.stereotype.Service;
import samurai.core.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by ran on 2/9/17.
 */
@Service
public class SamuraiService {
   /* @Autowired
    FileRepository fileRepository;


    List<DumpFile> getAllDumpFiles(){
        List<DumpFile> list = new LinkedList();
        fileRepository.findAll().forEach(i->list.add(i));
        return list;
    }*/

    public void examine(File in ) throws IOException {
        File out  = new File(in.getAbsolutePath() + ".expected");
        if (!out.exists()) {
            dumpAnalyzed(in);
        }

        ThreadStatistic statistic = new ThreadStatistic();
        ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
        dumpExtractor.analyze(in);
        Properties props = new Properties();
        props.load(new FileInputStream(out));

        for (int i = 0; i < statistic.getFullThreadDumpCount(); i++) {
            String examining = in.getAbsolutePath() + ":";
            FullThreadDump ftd = statistic.getFullThreadDump(i);
            System.out.println(examining + "ftd" + i + ".deadLockSize"+ props.getProperty("ftd." + i + ".deadLockSize") +"   "+ String.valueOf(ftd.getDeadLockSize()));
            System.out.println(examining + "ftd" + i + ".threadCount"+"   "+ props.getProperty("ftd." + i + ".threadCount")+"   "+ String.valueOf(ftd.getThreadCount()));
            for (int j = 0; j < ftd.getThreadCount(); j++) {
                ThreadDump td = ftd.getThreadDump(j);
                System.out.println(examining + "td." + i + "." + j + ".blockedObjectId"+"   "+ props.getProperty("td." + i + "." + j + ".blockedObjectId")+"   "+ String.valueOf(td.getBlockedObjectId()));
                System.out.println(examining + "td." + i + "." + j + ".blockerId"+"   "+props.getProperty("td." + i + "." + j + ".blockerId")+"   "+ String.valueOf(td.getBlockerId()));
                System.out.println(examining + "td." + i + "." + j + ".condition"+"   "+ props.getProperty("td." + i + "." + j + ".condition")+"   "+ String.valueOf(td.getCondition()));
                System.out.println(examining + "td." + i + "." + j + ".header"+"   "+ props.getProperty("td." + i + "." + j + ".header")+"   "+ String.valueOf(td.getHeader()));
                System.out.println(examining + "td." + i + "." + j + ".id"+"   "+ props.getProperty("td." + i + "." + j + ".id")+"   "+ String.valueOf(td.getId()));
                System.out.println(examining + "td." + i + "." + j + ".name"+"   "+ props.getProperty("td." + i + "." + j + ".name")+"   "+ String.valueOf(td.getName()));
                System.out.println(examining + "td." + i + "." + j + ".isBlocked"+"   "+ props.getProperty("td." + i + "." + j + ".isBlocked")+"   "+String.valueOf(td.isBlocked()));
                System.out.println(examining + "td." + i + "." + j + ".isBlocking"+"   "+ props.getProperty("td." + i + "." + j + ".isBlocking")+"   "+ String.valueOf(td.isBlocking()));
                System.out.println(examining + "td." + i + "." + j + ".isDaemon"+"   "+ props.getProperty("td." + i + "." + j + ".isDaemon")+"   "+ String.valueOf(td.isDaemon()));
                System.out.println(examining + "td." + i + "." + j + ".isDeadLocked"+"   "+ props.getProperty("td." + i + "." + j + ".isDeadLocked")+"   "+ String.valueOf(td.isDeadLocked()));
                System.out.println(examining + "td." + i + "." + j + ".isIdle"+"   "+ props.getProperty("td." + i + "." + j + ".isIdle")+"   "+ String.valueOf(td.isIdle()));
            }
        }
    }

    /*package*/
    static void dumpAnalyzed(File in) throws IOException {
        dumpAnalyzed(in, new File(in.getAbsolutePath() + ".expected"));
    }

    /*package*/
    static void dumpAnalyzed(File in, File out) throws IOException {
        System.out.println("Analyzing: " + in.getAbsolutePath());
        if (out.exists()) {
            System.out.println("Output exists, skipping: " + out.getAbsolutePath());
        } else {
            ThreadStatistic statistic = new ThreadStatistic();
            ThreadDumpExtractor dumpExtractor = new ThreadDumpExtractor(statistic);
            dumpExtractor.analyze(in);
            PrintWriter pw = new PrintWriter(new FileOutputStream(out));

            for (int i = 0; i < statistic.getFullThreadDumpCount(); i++) {
                FullThreadDump ftd = statistic.getFullThreadDump(i);
                pw.println("ftd." + i + ".deadLockSize=" + ftd.getDeadLockSize());
                pw.println("ftd." + i + ".threadCount=" + ftd.getThreadCount());
                for (int j = 0; j < ftd.getThreadCount(); j++) {
                    ThreadDump td = ftd.getThreadDump(j);
                    pw.println("#" + td.getHeader());
                    for(StackLine line: td.getStackLines()){
                        pw.println("#" + line.toString());
                    }
                    pw.println("td." + i + "." + j + ".blockedObjectId=" + td.getBlockedObjectId());
                    pw.println("td." + i + "." + j + ".blockerId=" + td.getBlockerId());
                    pw.println("td." + i + "." + j + ".condition=" + td.getCondition());
                    pw.println("td." + i + "." + j + ".header=" + td.getHeader());
                    pw.println("td." + i + "." + j + ".id=" + td.getId());
                    pw.println("td." + i + "." + j + ".name=" + td.getName());
                    pw.println("td." + i + "." + j + ".isBlocked=" + td.isBlocked());
                    pw.println("td." + i + "." + j + ".isBlocking=" + td.isBlocking());
                    pw.println("td." + i + "." + j + ".isDaemon=" + td.isDaemon());
                    pw.println("td." + i + "." + j + ".isDeadLocked=" + td.isDeadLocked());
                    pw.println("td." + i + "." + j + ".isIdle=" + td.isIdle());
                    pw.println();
                }
            }
            pw.close();
            System.out.println("Done analyzing: " + out.getAbsolutePath());
            System.out.println("Review and edit it if needed.");
        }
    }
}
