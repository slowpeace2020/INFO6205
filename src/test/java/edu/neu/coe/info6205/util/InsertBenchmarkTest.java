package edu.neu.coe.info6205.util;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.HelperFactory;
import edu.neu.coe.info6205.sort.SortWithHelper;
import edu.neu.coe.info6205.sort.elementary.InsertionSort;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class InsertBenchmarkTest {
    @Test
    public void randomSort(){
        int nRuns = 100;
        final Config config = ConfigTest.setupConfig("true", "0", "1", "", "");
        try {
            FileWriter dataCsv = new FileWriter("random_data.csv");
            String header = "length,runtime\r\n";
            dataCsv.write(header);
            for(int i=1;i<=100000;i=i*2) {
                int n = i;
                Helper<Integer> helper = HelperFactory.create("InsertionSort", n, config);
                helper.init(n);
                Integer[] xs = helper.random(Integer.class, r -> r.nextInt(n));
                SortWithHelper<Integer> sorter = new InsertionSort<Integer>(helper);
                Benchmark<Integer[]> bm = new Benchmark_Timer<Integer[]>(
                        "testRandomInsertionSort", b -> {
                    sorter.sort(xs);
                });
                double x = bm.run(xs, nRuns);
                System.out.println("random sort " + n + " numbers need time: " + x);
                StringBuffer str = new StringBuffer();
                str.append(n + "," + x + "\r\n");
                dataCsv.write(str.toString());
                dataCsv.flush();
            }
            dataCsv.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    @Test
    public void orderedSort(){
        int nRuns = 100;
        final Config config = ConfigTest.setupConfig("true", "0", "1", "", "");
        try {
            FileWriter dataCsv = new FileWriter("order_data.csv");
            String header = "length,runtime\r\n";
            dataCsv.write(header);
            for(int i=1;i<=100000;i=i*2) {
            int n = i;
            Helper<Integer> helper = HelperFactory.create("InsertionSort", n, config);
            helper.init(n);
            final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
            final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
            Integer[] xs = helper.random(Integer.class, r -> r.nextInt(n));
            SortWithHelper<Integer> sorter = new InsertionSort<Integer>(helper);
            sorter.preProcess(xs);
            Integer[] ys = sorter.sort(xs);
            assertTrue(helper.sorted(ys));
            Benchmark<Integer[]> bm = new Benchmark_Timer<Integer[]>(
                    "testOrderedInsertionSort", b -> {
                sorter.sort(ys);
            });
            double x = bm.run(ys, nRuns);
            System.out.println("ordered sort "+n+" numbers need time: "+x);
            StringBuffer str = new StringBuffer();
            str.append(n + "," + x + "\r\n");
            dataCsv.write(str.toString());
            dataCsv.flush();
        }
            dataCsv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void partOrderSort(){
        int nRuns = 100;
        final Config config = ConfigTest.setupConfig("true", "0", "1", "", "");
        try {
            FileWriter dataCsv = new FileWriter("part_data.csv");
            String header = "length,runtime\r\n";
            dataCsv.write(header);
            for(int i=2;i<=100000;i=i*2) {
            int n = i;
            Helper<Integer> helper = HelperFactory.create("InsertionSort", n, config);
            helper.init(n);
            final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
            final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
            Integer[] xs = helper.random(Integer.class, r -> r.nextInt(n));
            SortWithHelper<Integer> sorter = new InsertionSort<Integer>(helper);
            sorter.preProcess(xs);
            Random random = new Random();
            int from = random.nextInt(n-1);
            int to = from + random.nextInt(n-from);
            sorter.sort(xs,from,to);
//            Integer[] ys = sorter.sort(xs,from,to);
//            assertTrue(helper.sorted(xs));
            Benchmark<Integer[]> bm = new Benchmark_Timer<Integer[]>(
                    "testPartOrderedInsertionSort", b -> {
                sorter.sort(xs);
            });
            double x = bm.run(xs, nRuns);
            System.out.println("part ordered sort "+n+" numbers need time: "+x);
            StringBuffer str = new StringBuffer();
            str.append(n + "," + x + "\r\n");
            dataCsv.write(str.toString());
            dataCsv.flush();
        }
        dataCsv.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    @Test
    public void reverseOrderSort(){
        int nRuns = 100;
        final Config config = ConfigTest.setupConfig("true", "0", "1", "", "");
        try {
            FileWriter dataCsv = new FileWriter("reverse_data.csv");
            String header = "length,runtime\r\n";
            dataCsv.write(header);
            for(int i=1;i<=100000;i=i*2) {
            int n = i;
            Helper<Integer> helper = HelperFactory.create("InsertionSort", n, config);
            helper.init(n);
            final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
            final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
            Integer[] xs = helper.random(Integer.class, r -> r.nextInt(n));
            SortWithHelper<Integer> sorter = new InsertionSort<Integer>(helper);
            sorter.preProcess(xs);
            Integer[] ys = sorter.sort(xs);
            assertTrue(helper.sorted(ys));

            for(int j=0;j<n/2;j++){
                helper.swap(ys,j,n-1-j);
            }
            Benchmark<Integer[]> bm = new Benchmark_Timer<Integer[]>(
                    "testReverseOrderedInsertionSort", b -> {
                sorter.sort(ys);
            });
            double x = bm.run(ys, nRuns);
            System.out.println("reverse ordered sort "+n+" numbers need time: "+x);
            StringBuffer str = new StringBuffer();
            str.append(n + "," + x + "\r\n");
            dataCsv.write(str.toString());
            dataCsv.flush();
        }
        dataCsv.close();
    } catch (IOException e) {
        e.printStackTrace();
        }
    }
}
