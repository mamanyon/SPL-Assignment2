package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    //Mapping MicroService's Messages queues
    private Map<MicroService, LinkedList<Message>> registeredMicro = new ConcurrentHashMap<>();
    //Mapping Message's MicroServices queues
    private Map<Class<? extends Message>, LinkedBlockingDeque<MicroService>> messageTypeMicro = new ConcurrentHashMap<>();
    //Mapping All microService's references in the other maps
    private Map<MicroService, LinkedList<Class<? extends Message>>> microReferences = new ConcurrentHashMap<>();
    //Mapping each future ot it's event
    private Map<Message, Future> eventsFuture = new ConcurrentHashMap<>();

    protected int activeReaders = 0;
    protected int activeWriters = 0;
    protected int waitingWriters = 0;

    private static class MessageBusHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private MessageBusImpl() {
    }

    public static MessageBusImpl getInstance() {
        return MessageBusHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) throws InterruptedException {
        beforeWrite();

        if (registeredMicro.containsKey(m)) {
            if (!messageTypeMicro.containsKey(type))
                messageTypeMicro.put(type, new LinkedBlockingDeque<>());

            messageTypeMicro.computeIfPresent(type, (key, microQueue) -> {
                microQueue.add(m);
                return microQueue;
            });

            if (!microReferences.containsKey(m))
                microReferences.put(m, new LinkedList<>());
            microReferences.get(m).add(type);

        } else {
            System.out.println("You didn't register '" + m.getName() + "' yet");
        }

        afterWrite();
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) throws InterruptedException {
        beforeWrite();

        if (registeredMicro.containsKey(m)) {
            if (!messageTypeMicro.containsKey(type))
                messageTypeMicro.put(type, new LinkedBlockingDeque<>());

            messageTypeMicro.computeIfPresent(type, (key, microQueue) -> {
                microQueue.add(m);
                return microQueue;
            });
            if (!microReferences.containsKey(m))
                microReferences.put(m, new LinkedList<>());
            microReferences.get(m).add(type);

        } else {
            System.out.println("You didn't register '" + m.getName() + "' yet");
        }

        afterWrite();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void complete(Event<T> e, T result) throws InterruptedException {    //it's change the future to isdone // that how Lia could know when all the attacks is done and she could say to r2d2 to take off the shields
        beforeWrite();
        eventsFuture.get(e).resolve(result);

        afterWrite();
    }

    @Override
    public void sendBroadcast(Broadcast b) throws InterruptedException {
        beforeWrite();

        if (messageTypeMicro.containsKey(b.getClass())) {
            for (MicroService micro : messageTypeMicro.get(b.getClass()))
                registeredMicro.get(micro).add(b);

        } else
            System.out.println("Broadcast type: " + b + " has no registered MicroServices");

        afterWrite();
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) throws InterruptedException {
        beforeWrite();

        if (messageTypeMicro.containsKey(e.getClass()) && !messageTypeMicro.get(e.getClass()).isEmpty()) {

            LinkedBlockingDeque<MicroService> microsQueue = messageTypeMicro.get(e.getClass());
            MicroService micro = microsQueue.poll();
            registeredMicro.get(micro).add(e);
            microsQueue.add(micro);

            eventsFuture.put(e, new Future<T>());
        }

        afterWrite();
        return eventsFuture.get(e);                    //				<-----------------------
    }


    @Override
    public void register(MicroService m) throws InterruptedException {
        beforeWrite();

        if (!registeredMicro.containsKey(m))
            registeredMicro.put(m, new LinkedList<Message>());

        afterWrite();
    }

    @Override
    public void unregister(MicroService m) throws InterruptedException {
        beforeWrite();

        try {
            for (Class<? extends Message> ref : microReferences.get(m)) {

                messageTypeMicro.get(ref).remove(m);
            }
            registeredMicro.remove(m);

        } catch (Exception e) {
            System.out.println(e);
        }

        afterWrite();
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {    //take method of blocking queue
        beforeRead();

        Message output = null;
        try {
            output = registeredMicro.get(m).poll();
        } catch (NullPointerException e) {
            System.out.println(m.getName() + " doesn't exist in the Map ");
        }

        afterRead();
        return output;
    }

    protected synchronized void beforeRead() throws InterruptedException {
        while (!(waitingWriters == 0 && activeWriters == 0))
            wait();
        activeReaders++;
    }

    protected synchronized void afterRead() {
        activeReaders--;
        notifyAll();
    }

    protected synchronized void beforeWrite() throws InterruptedException {
        waitingWriters++;
        while (!(activeReaders == 0 && activeWriters == 0)) {
            wait();
        }
        waitingWriters--;
        activeWriters++;
    }

    protected synchronized void afterWrite() {
        activeWriters--;
        notifyAll();
    }
}
