package bgu.spl.mics;

import java.util.Queue;

public class TestMicroService extends MicroService{

    int testNum;
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */

    public TestMicroService(String name) {
        super(name);
    }

    @Override
    protected void initialize() {

    }
}
