package org.jeecg.modules.test;

import java.util.Date;

/**
 * create by Ernest on 2020/5/1.
 */
public class Main implements Runnable {

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (this) {
                try {
                    wait();
                }catch (InterruptedException e) {
                    System.out.println("InterruptedException");
                }
            }
        }
        System.out.println("Final");
    }

    public static void main(String[] args) throws InterruptedException {
//        Thread thread = new Thread(new Main());
//        thread.start();
//        new Thread(() -> {
//            try {
//                Thread.sleep(1000);
//            }catch (InterruptedException e) {
//
//            }
//            thread.interrupt();
//            System.out.println("interrupt");
//        }).start();
//        thread.join();
//        System.out.println("exit");

        System.out.println("ONR2020052916385828".substring(3));
    }

    //InterruptedException interrupt
}
