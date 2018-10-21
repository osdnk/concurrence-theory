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
        //BinarySemaphore binarySemaphore = new BinarySemaphore();
        GeneralSemaphore binarySemaphore = new GeneralSemaphore(1);
        int n = 1410;
        Racer t1 = new Racer(binarySemaphore, n, true);
        Racer t2 = new Racer(binarySemaphore, n, false);
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
    private final GeneralSemaphore mBinarySemaphore;
    private final int mAmount;
    private final boolean mIsInc;

    /* package */ Racer(GeneralSemaphore binarySemaphore, int amount, boolean inc) {
        mBinarySemaphore = binarySemaphore;
        mRace = new Race();
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
            try {
                mBinarySemaphore.semSignal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/* package */ class BinarySemaphore {
    private boolean mFree = true;

    /* package */ synchronized void semSignal() {
        this.mFree = true;
        this.notify();
    }

    /* package */  synchronized void semWait() throws InterruptedException {
        while (!this.mFree) {
            this.wait();
        }
        this.mFree = false;
    }
}


//  Zaimplementowac semafor licznikowy (ogolny) za pomoca semaforow binarnych. Czy semafor binarny jest szczegolnym przypadkiem semafora ogolnego ?


/* package */ class GeneralSemaphore {

    private int n;
    private final BinarySemaphore mBlockSem = new BinarySemaphore();

    /* package */ GeneralSemaphore(int n) {
        this.n = n;
    }

    /* package */ void semWait() throws InterruptedException {
        if (n > 0) {
            if ((--n) == 0) {
                mBlockSem.semWait();
            }
        }
    }

    /* package */ void semSignal() throws InterruptedException {
        if((n++)==0){
            mBlockSem.semSignal();
        }
    }
}