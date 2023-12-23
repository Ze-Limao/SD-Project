package SD.TrabalhoG27;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WorkTeam {
    private List<TaggedConnection> workers = new ArrayList<>();
    private final Lock readLock = new ReentrantLock();
    private final Lock writeLock = new ReentrantLock();

    private PriorityQueue<Integer> availableWorkers= new PriorityQueue<>();
    private final Lock l = new ReentrantLock();

    public WorkTeam() {
    }

    public void setNewWorker(TaggedConnection c){
        this.readLock.lock();
        this.writeLock.lock();
        workers.add(c);
        this.writeLock.unlock();
        this.readLock.unlock();
        makeAvailable(c);
    }
    public void removeWorker(TaggedConnection c){
        this.readLock.lock();
        int id = workers.indexOf(c);
        try {
            if (id == -1){
                System.out.println(id + "doesnt exist in workers");
                return;
            }
        }finally {
            this.readLock.unlock();
        }
        this.l.lock();
        availableWorkers.remove(id);
        this.l.unlock();
    }

    public void makeAvailable(TaggedConnection c){
        this.readLock.lock();
        int id = workers.indexOf(c);
        try {
            if (id == -1){
                System.out.println(id + "doesnt exist in workers");
                return;
            }
        }finally {
            this.readLock.unlock();
        }
        this.l.lock();
        availableWorkers.add(id);
        this.l.unlock();
    }

    public TaggedConnection getWorker(){
        this.l.lock();
        Integer o = null;
        while (o == null)
            o = availableWorkers.poll();
        this.l.unlock();
        this.readLock.lock();
        try {
            return workers.get(o);
        } finally {
            this.readLock.unlock();
        }
    }


}
