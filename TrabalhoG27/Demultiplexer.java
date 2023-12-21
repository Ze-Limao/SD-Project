package SD.TrabalhoG27;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer {
    private TaggedConnection c;
    private ReentrantLock lock = new ReentrantLock();
    //tag e Pedidos
    private Map<Integer, FramesTag> map = new HashMap<>();
    private IOException exception = null;
    private class FramesTag {
        int waitingThreads = 0;
        Queue<Frame> queue = new ArrayDeque<>();
        Condition cond = lock.newCondition();

        public FramesTag(){}
    }

    public Demultiplexer(TaggedConnection c){
        this.c = c;
    }

    // Inicia uma nova thread para receber frames continuamente da conexão e processá-los.
    public void start(){
        Thread newThread = new Thread(() -> {
            try{
                while(true) {
                    Frame frame = c.receive();
                    lock.lock();
                    try{
                        FramesTag list = map.get(frame.tag);
                        if(list == null){
                            list = new FramesTag();
                            map.put(frame.tag, list);
                        }
                        list.queue.add(frame);
                        list.cond.signal();
                    }
                    finally {
                        lock.unlock();
                    }
                }
            }
            catch(IOException e){
                exception = e;
            }
        });
        newThread.start();
    }


    public void send(int tag, int src, int ask, Account acc) throws IOException{
        c.send(tag, src, ask, acc);
    }

    public void send(int tag, int src, int ask, Quest quest) throws IOException {
        c.send(tag, src, ask, quest);
    }

    public void send(int tag, int src, int ask, boolean bool) throws IOException{
        c.send(tag, src, ask, bool);
    }
    public void send(int tag, int src, int ask, String str) throws IOException{
        c.send(tag, src, ask, str);
    }
    public void send(int tag, int src, int ask, boolean bool, byte[] b) throws IOException{
        c.send(tag, src, ask, bool, b);
    }

    public void send(int tag, int src, int ask, boolean bool, int err, String msg) throws IOException{
        c.send(tag, src, ask, bool, err, msg);
    }

    public void send(int tag, int src, int ask, int activeTasks, int availableMemory) throws IOException{
        c.send(tag, src, ask, activeTasks, availableMemory);
    }

    public Frame receive(int tag) throws IOException, InterruptedException{
        lock.lock();
        FramesTag list;
        try{
            list = map.get(tag);
            if(list == null){
                list = new FramesTag();
                map.put(tag, list);
            }
            list.waitingThreads++;
            while(true){
                if(list.queue.isEmpty() != true){
                    list.waitingThreads--;
                    Frame r = list.queue.poll();
                    if((list.waitingThreads == 0) && (list.queue.isEmpty() == true)){
                        map.remove(tag);
                    }
                    return r;
                }
                if(exception != null){
                    throw exception;
                }
                list.cond.await();
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void close() throws IOException{
        this.c.close();
    }
}
