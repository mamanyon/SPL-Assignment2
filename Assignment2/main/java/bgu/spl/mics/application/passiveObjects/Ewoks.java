package bgu.spl.mics.application.passiveObjects;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private Ewok[] ewoks;

    public void setEwoks(int size) {
        ewoks = new Ewok[size];
        for (int i = 0; i < size; i++) {
            Ewok tmp = new Ewok(i+1);
            ewoks[i] = tmp;
        }
    }

    public void release(Integer num) {
        ewoks[num-1].release();
    }

    private static class SingletonHolder {
        private static Ewoks instance = new Ewoks();

    }
    private Ewoks() {
    }
    public Ewok[] getEwoks(){return ewoks;}
    public static Ewoks getInstance() {
        return SingletonHolder.instance;
    }
    public boolean acquire(int num) {
        if (ewoks[num-1].available) {
            ewoks[num-1].acquire();
            return true;
        }
        return false;
    }
}

