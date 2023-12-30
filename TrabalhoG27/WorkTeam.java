package SD.TrabalhoG27;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WorkTeam {

    private final List<WorkerInfo> workers = new ArrayList<>();
    private final Lock readLock = new ReentrantLock();
    private final Lock writeLock = new ReentrantLock();
    private final ArrayDeque<Integer> availableWorkers= new ArrayDeque<>();
    private final Lock l = new ReentrantLock();
    //private final Condition cond = l.newCondition();
    private final List<Condition> conds = new ArrayList<>();
    private Integer worker_counter = -1; // Workers next job
    private Integer queue_counter = -1; // Lugar da fila de espera


    public WorkTeam() {}

    /**função que adiciona e disponibliza um novo worker*/
    public void setNewWorker(TaggedConnection c,int memory){
        this.readLock.lock();
        this.writeLock.lock();
        WorkerInfo wi = new WorkerInfo(c, memory);
        workers.add(wi);
        this.writeLock.unlock();
        this.readLock.unlock();
        makeAvailable(wi);
    }
    /**função usada para manualmente remover um worker da lista de "disponiveis"(apenas usada quando o worker deixa de existir)*/
    public void removeWorker(TaggedConnection c, int memory){
        this.readLock.lock();
        WorkerInfo wi = new WorkerInfo(c, memory);
        int id = workers.indexOf(wi);
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
        worker_counter--;
        this.l.unlock();
    }

    /**função usada para atualizar o estado de um worker para "disponivel"*/
    public void makeAvailable(WorkerInfo wi){
        this.readLock.lock();
        int id = workers.indexOf(wi);
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

    /**função que devolve o taggedConnection de um worker disponivel*/
    public WorkerInfo getWorker(int memo) throws InterruptedException {
        this.l.lock();
        Integer q = ++queue_counter;
        Condition cond = l.newCondition();
        conds.add(cond);
        while (q > worker_counter) {
            cond.await();
        }

        Integer best = availableWorkers.peek();
        for(Integer o : availableWorkers.toArray(new Integer[0])) {
            if (workers.get(o).memory >= memo && workers.get(o).memory < workers.get(best).memory)
                best = o;
        }
        availableWorkers.remove(best);
        this.l.unlock();
        if (best == null) {
            System.out.println("o has null value.");
            return null;
        }
        this.readLock.lock();
        try {
            return workers.get(best);
        } finally {
            this.readLock.unlock();
        }
    }


}
