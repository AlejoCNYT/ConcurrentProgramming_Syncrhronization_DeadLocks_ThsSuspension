package edu.eci.arst.concprg.prodcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BlackListDiscoverer {

    private static final int BLACK_LIST_ALARM_COUNT = 5;
    private static final int SERVER_SEARCHER_THREADS = 10;

    private final AtomicInteger occurrenceCount = new AtomicInteger(0);
    private final AtomicBoolean searchStopped = new AtomicBoolean(false);
    private final List<String> checkedServers = Collections.synchronizedList(new ArrayList<>());

    public boolean checkHost(String ipaddress, int serverRange) {
        occurrenceCount.set(0);
        searchStopped.set(false);
        checkedServers.clear();

        Thread[] searchers = new Thread[SERVER_SEARCHER_THREADS];
        int segmentSize = serverRange / SERVER_SEARCHER_THREADS;

        for (int i = 0; i < SERVER_SEARCHER_THREADS; i++) {
            int start = i * segmentSize;
            int end = (i == SERVER_SEARCHER_THREADS - 1) ? serverRange : start + segmentSize;
            searchers[i] = new Thread(new SearcherTask(ipaddress, start, end));
            searchers[i].start();
        }

        for (Thread searcher : searchers) {
            try {
                searcher.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return occurrenceCount.get() >= BLACK_LIST_ALARM_COUNT;
    }

    private class SearcherTask implements Runnable {
        private final String ipaddress;
        private final int start, end;

        public SearcherTask(String ipaddress, int start, int end) {
            this.ipaddress = ipaddress;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = start; i < end && !searchStopped.get(); i++) {
                if (searchStopped.get()) break;

                String server = "server" + i + ".com";
                if (checkedServers.contains(server)) continue;

                if (checkServer(server, ipaddress)) {
                    checkedServers.add(server);
                    int currentCount = occurrenceCount.incrementAndGet();

                    if (currentCount >= BLACK_LIST_ALARM_COUNT) {
                        searchStopped.set(true);
                        break;
                    }
                }
            }
        }

        private boolean checkServer(String server, String ipaddress) {
            // Simulación de verificación en servidor remoto
            try {
                Thread.sleep(new Random().nextInt(100));
                return new Random().nextBoolean();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }
}
