package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    long duration;
    private Diary diary = Diary.getInstance();
    public R2D2Microservice(long duration) {

        super("R2D2");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast terminationBroadcast) ->{
            diary.R2D2Terminate = System.currentTimeMillis();
            terminate();

        });

        subscribeEvent(DeactivationEvent.class, (DeactivationEvent deactivationEvent) -> {
            try{
            Thread.sleep(duration);}
            catch (InterruptedException e){}
            diary.R2D2Deactivate = System.currentTimeMillis();
            complete(deactivationEvent, true);
        });

    }
}
