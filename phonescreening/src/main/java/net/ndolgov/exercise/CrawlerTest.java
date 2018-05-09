package net.ndolgov.exercise;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CrawlerTest {
    private static final String PRODUCT = "Product";
    private static final String BLOG = "Blog";
    private final int nCPUs = Runtime.getRuntime().availableProcessors();
    private final AtomicInteger hpc = new AtomicInteger();
    private final AtomicInteger bc = new AtomicInteger();
    private final CountDownLatch latch = new CountDownLatch(2);
    private final BlockingQueue<String> urls = new ArrayBlockingQueue<>(65535);
    private final ConcurrentHashMap<String, Boolean> visited = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            new CrawlerTest().crawl(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crawl(String url) throws Exception {
        urls.add(url);

        final ExecutorService e = Executors.newFixedThreadPool(nCPUs);
        for (int i=0; i< nCPUs; i++) {
            e.execute(new Crawler());
        }

        if (!latch.await(1, TimeUnit.DAYS)) {
            throw new IllegalStateException("Never finished domain crawling");
        }

        System.out.println("Found 20 products and 10 blogs");
    }


    private final class Crawler implements Runnable {
        public void run() {
            try {
                while (true) {
                    final String url = urls.take();
                    doCrawl(url);
                }
            }
            catch (Exception e) {
                System.err.println("Unexpected crawling error: " + e);
            }
        }

        private void doCrawl(String url) throws InterruptedException {
            if (isNotVisited(url)) {
                incCounters(getPageType(url));
            }

            for (String nested : getPageLinks(url)) {
                urls.put(nested);
            }
        }

        private void incCounters(String type) {
            if (type.equals(PRODUCT)) { // todo collect to print when finished
                final int c = hpc.incrementAndGet();
                if (c == 20) {
                    latch.countDown();
                }

            } else if (type.equals(BLOG)) { // todo collect to print when finished
                final int c = bc.incrementAndGet();
                if (c == 10) {
                    latch.countDown();
                }
            }
        }

        private boolean isNotVisited(String url) {
            return visited.putIfAbsent(url, true) == null;
        }
    }

    private List<String> getPageLinks(String url) {
        return null;
    }

    private String getPageType(String url) {
        return null;
    }
}
