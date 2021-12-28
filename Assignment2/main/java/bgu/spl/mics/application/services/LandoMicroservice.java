package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;
    private Diary diary = Diary.getInstance();
    public LandoMicroservice(long duration) {

        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast terminationBroadcast) -> {
            diary.LandoTerminate= System.currentTimeMillis();
            terminate();
        });

        subscribeEvent(BombDestroyerEvent.class, (BombDestroyerEvent bombDestroyerEvent) -> {
            Thread.sleep(duration);
            sendBroadcast(new TerminationBroadcast());
            complete(bombDestroyerEvent, true);
        });
    }
}
