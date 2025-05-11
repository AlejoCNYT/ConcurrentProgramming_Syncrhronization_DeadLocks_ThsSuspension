package edu.eci.arst.concprg.prodcons;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartProduction {

    public static void main(String[] args) {
        // Usamos LinkedBlockingQueue en lugar de Queue normal
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

        new Producer(queue, Long.MAX_VALUE).start();

        // Espera inicial para que el productor cree stock
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(StartProduction.class.getName()).log(Level.SEVERE, null, ex);
        }

        new Consumer(queue).start();
    }
}