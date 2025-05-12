package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;

    private final Object lock = new Object();

    private int defaultDamageValue;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private final Object pauseLock = new Object();

    private int health;
    private final Object healthLock = new Object();
    private final List<Immortal> immortalsPopulation;

    private volatile boolean paused = false;
    private volatile boolean stopped = false;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {
        while (true) {
            synchronized (pauseLock) {
                while (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            Immortal im;
            int myIndex = immortalsPopulation.indexOf(this);
            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            if (nextFighterIndex == myIndex) {
                nextFighterIndex = (nextFighterIndex + 1) % immortalsPopulation.size();
            }

            im = immortalsPopulation.get(nextFighterIndex);
            this.fight(im);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void fight(Immortal i2) {
        Immortal i1 = this;

        Immortal first, second;
        // Ordenar por nombre para evitar deadlock
        if (i1.name.compareTo(i2.name) < 0) {
            first = i1;
            second = i2;
        } else {
            first = i2;
            second = i1;
        }

        synchronized (first.getLock()) {
            synchronized (second.getLock()) {
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                } else {
                    updateCallback.processReport(this + " says: " + i2 + " is already dead!\n");
                }
            }
        }
    }

    public void changeHealth(int v) {
        synchronized (healthLock) {
            health = v;
        }
    }

    public int getHealth() {
        synchronized (healthLock) {
            return health;
        }
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

    public void pauseThread() {
        paused = true;
    }

    public void resumeThread() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notify();
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public Object getLock() {
        return lock;
    }

    public void addHealth(int value) {
        synchronized (healthLock) {
            health += value;
        }
    }

    public void reduceHealth(int value) {
        synchronized (healthLock) {
            health -= value;
        }
    }

    public void stopThread() {
        stopped = true;
        resumeThread(); // Despierta si está pausado
    }

}
