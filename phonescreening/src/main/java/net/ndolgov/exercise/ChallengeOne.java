package net.ndolgov.exercise;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ChallengeOne {
    private static final int LETTER_COUNT = 26;

    public static void main(String[] args) {
        final BlockingQueue<Character> letters = new ArrayBlockingQueue<>(LETTER_COUNT);
        for (int i = 0; i < LETTER_COUNT; i++) {
            letters.add((char) ('A' + i));
        }

        final Object sync = new Object();
        final AtomicInteger count = new AtomicInteger(0);

        try {
           new Thread(new PrinterJob("T1", sync, letters, count, 0)).start();
           new Thread(new PrinterJob("T2", sync, letters, count, 1)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final class PrinterJob implements Runnable {
        private final String prefix;
        private final Object sync;
        private final BlockingQueue<Character> queue;
        private final AtomicInteger count;
        private int nextIndex;

        private PrinterJob(String prefix, Object sync, BlockingQueue<Character> queue, AtomicInteger count, int nextIndex) {
            this.prefix = prefix;
            this.sync = sync;
            this.queue = queue;
            this.count = count;
            this.nextIndex = nextIndex;
        }

        public void run() {
            try {
                while (doRun()) {
                }
            }
            catch (Exception e) {
                System.err.println("Unexpected error: " + e + " in " + prefix);
            }
        }

        private boolean doRun() throws InterruptedException {
            synchronized (sync) {
                while ((count.get() != nextIndex) && !queue.isEmpty()) {
                    sync.wait(60_000);
                }

                if (queue.isEmpty()) {
                    sync.notifyAll();
                    return false;
                }

                System.out.println(prefix + " -> " + queue.take());
                count.incrementAndGet();
                nextIndex += 2;

                sync.notifyAll();
            }

            return true;
        }
    }
}
