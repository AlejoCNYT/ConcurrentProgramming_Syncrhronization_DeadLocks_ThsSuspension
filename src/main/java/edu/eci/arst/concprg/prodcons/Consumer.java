package edu.eci.arst.concprg.prodcons;

import java.util.concurrent.BlockingQueue;

public class Consumer extends Thread {
    private final BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // take() es bloqueante - espera hasta que haya un elemento disponible
                int elem = queue.take();
                System.out.println("Consumer consumes " + elem);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}