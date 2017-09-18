package concurrent;

import com.fan.threads.LinkedBlockingQueue;

public class Test {

    LinkedBlockingQueue queue = new LinkedBlockingQueue(10);

    public static void main(String args[]) {
        Test test = new Test();
        new Thread(test.new Producer()).start();
        new Thread(test.new Consumer()).start();
    }

    public class Producer implements Runnable {
        @Override
        public void run() {
            try {
                for (int i = 0; i < 20; i++) {
                    System.out.println("enter: " + i);
                    queue.put("val = " + i);
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                String result;
                Thread.sleep(1000);
                while ((result = queue.take().toString()) != null) {
                    System.out.println("take: " + result);
                    System.out.println("++++++++++++++++++++++++++++");
                }
                System.out.println("exit consumer!!!!");
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
