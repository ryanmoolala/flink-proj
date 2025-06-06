package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        // Run Flink_one asynchronously in a separate thread
        Thread flinkThread = new Thread(() -> {
            try {
                new Flink_one().test_flink_one();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        flinkThread.start();        
        // Run Reader separately (this will run concurrently with the Flink job)
        Reader.run();
    }
}