package edu.eci.arst.concprg.prodcons;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Producer extends Thread {
    private final BlockingQueue<Integer> queue;
    private int dataSeed = 0;
    private final Random rand;
    private final long stockLimit;

    public Producer(BlockingQueue<Integer> queue, long stockLimit) {
        this.queue = queue;
        this.rand = new Random(System.currentTimeMillis());
        this.stockLimit = stockLimit;
    }

    @Override
    public void run() {
        while (true) {
            dataSeed = dataSeed + rand.nextInt(100);
            System.out.println("Producer added " + dataSeed);
            try {
                queue.put(dataSeed); // Método bloqueante si la cola está llena
                Thread.sleep(1000); // Simula producción lenta
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}