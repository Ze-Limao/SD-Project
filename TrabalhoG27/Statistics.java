package SD.TrabalhoG27;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Statistics {
    private int availableMemory;
    private int activeTasks;

    private ReentrantLock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();


    public Statistics(int memory) {
        this.availableMemory = memory;
        this.activeTasks = 0;

    }

    public int getAvailableMemory() {
        return availableMemory;
    }

    public int getActiveTasks() {
        return activeTasks;
    }
    public void newTask(int memory){
        try {
            this.lock.lock();
            activeTasks += 1;
            while (this.availableMemory<memory) {
                cond.await();
            }
            this.availableMemory-= memory;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally{
            this.lock.unlock();
        }
    }

    public void endTask(int memory){
        try {
            this.lock.lock();
            activeTasks -= 1;
            this.availableMemory += memory;
            cond.signalAll();

        }finally{
            this.lock.unlock();
        }
    }

}

