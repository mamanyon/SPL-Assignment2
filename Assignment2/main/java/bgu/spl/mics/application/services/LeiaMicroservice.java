package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.List;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private List<Future> futures;
	private Diary diary = Diary.getInstance();

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		futures = new LinkedList<>();
    }

    @Override
    protected void initialize() {
    	subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast terminationBroadcast) -> {
            diary.LeiaTerminate=System.currentTimeMillis();
    	    terminate();
        });

        try{
            Thread.sleep(100);

        }catch(Exception e){
        }
        for(Attack attack : attacks){
            futures.add(sendEvent(new AttackEvent(attack)));
        }

        for(Future future: futures){
            future.get();
        }

        Future future = sendEvent(new DeactivationEvent());
        future.get();

        sendEvent(new BombDestroyerEvent());
    }
}