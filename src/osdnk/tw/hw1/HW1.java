package osdnk.tw.hw1;

public class HW1 {
    public static void main(String[] args) {
        Race race = new Race();
        race.startRace();
    }
}

// zaimplementowac semafor binarny za pomoca metod wait i notify, uzyc go do synchronizacji programu Wyscig (1 pkt.)
/* package */ class Race {
    /* package */ private int mCounter = 0;

    /* package */ void incrementCounter() {
        mCounter++;
    }

    /* package */ void decrementCounter() {
        mCounter--;
    }

    /* package */ void startRace() {
        BinarySemaphore binarySemaphore = new BinarySemaphore();
        //GeneralSemaphore binarySemaphore = new GeneralSemaphore(1);
        int n = 1410;
        Racer t1 = new Racer(binarySemaphore, n, true, this);
        Racer t2 = new Racer(binarySemaphore, n, false, this);
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(mCounter);
    }
}

/* package */ class Racer extends Thread {
    private final Race mRace;
    private final BinarySemaphore mBinarySemaphore;
    private final int mAmount;
    private final boolean mIsInc;

    /* package */ Racer(BinarySemaphore binarySemaphore, int amount, boolean inc, Race race) {
        mBinarySemaphore = binarySemaphore;
        mRace = race;
        mAmount = amount;
        mIsInc = inc;
    }

    @Override
    public void run() {
        for (int i = 0; i < mAmount; i++) {
            try {
                mBinarySemaphore.semWait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mIsInc) {
                mRace.incrementCounter();
            } else {
                mRace.decrementCounter();
            }
            mBinarySemaphore.semSignal();
        }
    }
}

/* package */ class BinarySemaphore {
    private boolean mFree = true;

    /* package */ synchronized void semSignal() {
        this.mFree = true;
        this.notifyAll();
    }

    /* package */  synchronized void semWait() throws InterruptedException {
        while (!this.mFree) {
            this.wait();
        }
        this.mFree = false;
    }
}

// Zad. 2 -- nie wystarczy, ponieważ wtedy dwa procesy czekające naraz ruszą

//  Zaimplementowac semafor licznikowy (ogolny) za pomoca semaforow binarnych. Czy semafor binarny jest szczegolnym przypadkiem semafora ogolnego ?


/* package */ class Semaphore{
    private int mResource;
    private BinarySemaphore mSem = new BinarySemaphore();

    /* package */ Semaphore (int resource) {
        mResource = resource;
    }

    /* package */ synchronized void increment(){
        while(isAvailable()){
            try {
                mSem.semWait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mResource++;

        report();

        mSem.semSignal();
    }

    /* package */  synchronized void decrement(){
        while(!isAvailable()){
            try {
                mSem.semWait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mResource--;

        report();

        mSem.semSignal();
    }

    /* package */  synchronized boolean isAvailable(){
        return mResource > 0;
    }

    /* package */  synchronized void report(){
        System.out.println("Resource value: " + mResource);
    }
}