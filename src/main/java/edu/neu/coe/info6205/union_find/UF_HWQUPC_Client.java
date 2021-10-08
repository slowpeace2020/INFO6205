package edu.neu.coe.info6205.union_find;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class UF_HWQUPC_Client {
    public static void main(String[] args) {

        try {
            FileWriter dataCsv = new FileWriter("union_find_data.csv");
            String header = "nodes,connect\r\n";
            dataCsv.write(header);
            for(int n=10;n<=10000;n=n+10){
                StringBuffer str = new StringBuffer();
                int operationCount = count(n);
                str.append(n+","+operationCount+"\r\n");
                dataCsv.write(str.toString());
                dataCsv.flush();
            }
            dataCsv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * @description experiment 100 times for n nodes
     * @param n
     * @return average connect numbers
     */

    public static int count(int n){
        Random random = new Random();
        int operationCount =0;
        for(int test=0;test<100;test++){
            UF h = new UF_HWQUPC(n);
            while (h.components()>1){
                int left = random.nextInt(n);
                int right = random.nextInt(n);
                operationCount++;
                if(!h.isConnected(left,right)){
                    h.union(left,right);
                }

            }
        }

        return operationCount/100;
    }
}
