package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import java.util.List;
/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private Diary diary = Diary.getInstance();
    private Ewoks ewok;
    private Callback<AttackEvent> callAttack = (AttackEvent att) -> {
        Attack attack = att.getAttack();
        int size = attack.getSerialsSize();
        List<Integer> serials = attack.getSerials();
        for (int i = 0; i < size; i++) {
            ewok.acquire(serials.get(i));
        }
        //sleep for the duration of the attack
        try {
            Thread.sleep(attack.getDuration());
        } catch (Exception e) {
        }
        for (int i = 0; i < size; i++) {//release the ewoks when you done sleeping
            ewok.release(serials.get(i));
        }
        diary.HanSoloFinish = System.currentTimeMillis();
        diary.totalAttacks.incrementAndGet();
        complete(att, true);
    };


    public HanSoloMicroservice(Ewoks ewoks) {
        super("Han");
        ewok = ewoks;
    }

    @Override
    protected void initialize() {

        subscribeBroadcast(TerminationBroadcast.class, (TerminationBroadcast terminationBroadcast) -> {
            terminate();
            diary.HanSoloTerminate= System.currentTimeMillis();
        });

        subscribeEvent(AttackEvent.class, callAttack);
    }
}



