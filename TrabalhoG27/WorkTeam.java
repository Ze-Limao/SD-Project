package SD.TrabalhoG27;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WorkTeam {
    private final List<TaggedConnection> workers = new ArrayList<>();
    private final Lock readLock = new ReentrantLock();
    private final Lock writeLock = new ReentrantLock();
    private final PriorityQueue<Integer> availableWorkers= new PriorityQueue<>();
    private final Lock l = new ReentrantLock();
    //private final Condition cond = l.newCondition();
    private final List<Condition> conds = new ArrayList<>();
    private Integer worker_counter = -1; // Workers next job
    private Integer queue_counter = -1; // Lugar da fila de espera


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
                System.out.println(id + "doesn`t exist in workers");
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
        worker_counter++;
        if(queue_counter >= worker_counter){
            conds.get(worker_counter).signal();
        }
        this.l.unlock();
    }

    public TaggedConnection getWorker() throws InterruptedException {
        this.l.lock();
        Integer q = ++queue_counter;
        Condition cond = l.newCondition();
        conds.add(cond);
        while (q > worker_counter) {
            cond.await();
        }
        Integer o = availableWorkers.poll();
        this.l.unlock();
        if (o == null) {
            System.out.println("o has null value.");
            return null;
        }
        this.readLock.lock();
        try {
            return workers.get(o);
        } finally {
            this.readLock.unlock();
        }
    }


}
