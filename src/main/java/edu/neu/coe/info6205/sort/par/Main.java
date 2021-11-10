package edu.neu.coe.info6205.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
public class Main {

    private static final int CHUNK = 250000;

    //thread number
    //1,2,4,8,16,32,64,128,256,512
    private static final int NUM_PROCS = 8;
    private static ForkJoinPool FORKJOIN_POOL = new ForkJoinPool(NUM_PROCS);

    public static void main(String[] args) {
        processArgs(args);
//        System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        Random random = new Random();

        // type=1,cutoff; type=2, thread number, type=3, combine
        int type = configuration.getOrDefault("-T",1);
        if(type==1){
            for(int j = 0;j<100;j++){//fix thread number
                String res ="";
                for (int arrSize = 1; arrSize <= 8; arrSize += arrSize) {
                    int[] array = new int[arrSize*CHUNK*8];
                    ParSort.cutoff = (j + 1) * 10000;
                    long time;
                    long startTime = System.currentTimeMillis();
                    for (int t = 0; t < 10; t++) {
                        for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                        ParSort.sort(array, 0, array.length,FORKJOIN_POOL);
                    }
                    long endTime = System.currentTimeMillis();
                    time = (endTime - startTime);
                    res+=(time/10)+",";
                    System.out.println("Degree of parallelism: " + FORKJOIN_POOL.getParallelism() + ",cutoff:" + ParSort.cutoff +
                        ",size:" + arrSize + ",time:" + time + "ms");
                }
                System.out.println(res.substring(0,res.length()-1));
                writeToCsv(res.substring(0,res.length()-1),"cutoff");
            }
        }else if(type==2){//experiment for different thread number, fix cutoff
            ParSort.cutoff = 40000;

            String res ="";
            for (int arrSize = 1; arrSize <= 256; arrSize += arrSize) {
                int[] array = new int[arrSize*CHUNK];
                long time;
                long startTime = System.currentTimeMillis();
                for (int t = 0; t < 10; t++) {
                    for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                    ParSort.sort(array, 0, array.length,FORKJOIN_POOL);
                }
                long endTime = System.currentTimeMillis();
                time = (endTime - startTime);
                res+=(time/10)+",";
                System.out.println("Degree of parallelism: " + FORKJOIN_POOL.getParallelism() + ",cutoff:" + ParSort.cutoff +
                    ",size:" + arrSize + ",time:" + (time/10) + "ms");
            }

            System.out.println(res.substring(0,res.length()-1));
            writeToCsv(res.substring(0,res.length()-1),"threadnumber");
        }else {
            for(int j = 1;j<16;j+=j){//change thread number, combine with different cutoff
                String res ="";
                for (int arrSize = 1; arrSize <= 256; arrSize += arrSize) {
                    int[] array = new int[arrSize*CHUNK];
                    ParSort.cutoff = j*10000;
                    long time;
                    long startTime = System.currentTimeMillis();
                    for (int t = 0; t < 10; t++) {
                        for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                        ParSort.sort(array, 0, array.length,FORKJOIN_POOL);
                    }
                    long endTime = System.currentTimeMillis();
                    time = (endTime - startTime);
                    res+=(time/10)+",";
                }
                System.out.println(res.substring(0,res.length()-1));
                writeToCsv(res.substring(0,res.length()-1),"combine_"+j);
            }
        }
    }

    public static void writeToCsv(String content, String fileName) {
        try {
            FileOutputStream fis = new FileOutputStream("./src/" + fileName + ".csv", true);
            OutputStreamWriter isr = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(isr);
            bw.write(content + "\n");
            bw.flush();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("-N")) setConfig(x, Integer.parseInt(y));
        else
            // TODO sort this out
            if (x.equalsIgnoreCase("-P")) //noinspection ResultOfMethodCallIgnored
                ForkJoinPool.getCommonPoolParallelism();
            if (x.equalsIgnoreCase("-T")) setConfig(x, Integer.parseInt(y));
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();


}
