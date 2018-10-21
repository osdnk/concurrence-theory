package osdnk.tw.hw1;

import java.util.Random;

class Philosophers {
    public static void main(String[] args) {
       p3();
    }

    private static void p1() {
        Waiter waiter = new Waiter();
        Philosopher1[] philosophers = new Philosopher1[5];
        for (int i = 0; i < 5; i++) philosophers[i] = new Philosopher1(i, waiter);

        for (int i = 0; i < 5; i++) philosophers[i].start();
        try {
            for (int i = 0; i < 5; i++) philosophers[i].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void p3() {
        BinarySemaphore[] forks = new BinarySemaphore[5];
        for(int i=0; i < 5; i++) forks[i] = new BinarySemaphore();

        Philosopher3[] philosophers = new Philosopher3[5];
        for(int i=0; i < 5; i++) philosophers[i] = new Philosopher3(forks, i);

        for(int i=0; i < 5; i++) philosophers[i].start();
        try {for(int i=0; i < 5; i++) philosophers[i].join();}
        catch (InterruptedException e) {e.printStackTrace();}
    }

    private static void p2() {
        BinarySemaphore[] philosophersSemaphore = new BinarySemaphore[5];
        for(int i=0; i < 5; i++) philosophersSemaphore[i] = new BinarySemaphore();

        Philosopher2[] philosophers = new Philosopher2[5];
        for(int i=0; i < 5; i++) philosophers[i] = new Philosopher2(philosophersSemaphore, i);

        for(int i=0; i < 5; i++) philosophers[i].start();
        try {for(int i=0; i < 5; i++) philosophers[i].join();}
        catch (InterruptedException e) {e.printStackTrace();}
    }
}

// rozwiązane z użyciem kelnera, który obsługuje filozofów - podaje i zabiera widelce parami (taki centralny zarządca zasobów), dzięki czemu zapobiega zakleszczeniom.
/* package */ class Philosopher1 extends Thread {
    private int n;
    private Waiter waiter;

    /* package */ Philosopher1(int n, Waiter waiter) {
        this.n = n;
        this.waiter = waiter;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                waiter.distribute(n);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(n + " is dining");
            Random rand = new Random();
            try {
                Thread.sleep(rand.nextInt(1000) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(n + " ended eating");
            waiter.collect(n);

            System.out.println(n + " is thinking...");
            try {
                Thread.sleep(rand.nextInt(1000) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


/* package */ class Waiter {
    private boolean[] mForks = new boolean[5];

    /* package */ Waiter() {
        for (int i = 0; i < 5; i++) mForks[i] = true;
    }

    /* package */ synchronized void collect(int i) {
        mForks[i] = mForks[(i + 1) % 5] = true;
        this.notify();
    }

    /* package */ synchronized void distribute(int i) throws InterruptedException {
        while (!mForks[i] || !mForks[(i + 1) % 5]) this.wait();
        mForks[i] = mForks[(i + 1) % 5] = false;
    }
}

//  każdy z filozofów stara się wziąć oba widelce, próbuje zająć najpierw jeden, a potem drugi widelec. Może tu powstać sytuacja zakleszczeń. Random czas pomaga
/* package */ class Philosopher2 extends Thread {
    static BinarySemaphore[] sPhilosophers;
    int n;

    /* package */ Philosopher2(BinarySemaphore[] philosophers1, int n) {
        sPhilosophers = philosophers1;
        this.n = n;
    }

    /* package */ synchronized void take() throws InterruptedException {
        sPhilosophers[((this.n - 1) % 5 + 5) % 5].semWait();
        sPhilosophers[this.n].semWait();
    }

    /* package */ synchronized void put() {
        sPhilosophers[((this.n - 1) % 5 + 5) % 5].semSignal();
        sPhilosophers[this.n].semSignal();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            try {
                take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(n + " is dining");
            Random rand = new Random();
            try {
                Thread.sleep(rand.nextInt(1000) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(n + " ended");
            put();
            try {
                Thread.sleep(rand.nextInt(1000) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// rozwiązanie z użyciem hierarchii zasobów
/* package */ class Philosopher3 extends Thread {
    private final BinarySemaphore[] mForks;
    private int n;


    /* package */ Philosopher3(BinarySemaphore[] forks, int n) {
        this.mForks = forks;
        this.n = n;
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            Random rand = new Random();

            try {
                mForks[this.n == 4 ? (this.n + 1) % 5 : this.n].semWait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                mForks[this.n == 4 ? this.n : (this.n + 1) % 5].semWait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(n + " is dining");
            try {
                Thread.sleep(rand.nextInt(1000) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mForks[(this.n + 1) % 5].semSignal();
            mForks[this.n].semSignal();
            System.out.println(n + " finished eating");


            System.out.println(n + " is thinking...");
            try {
                Thread.sleep(rand.nextInt(1000) + 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
