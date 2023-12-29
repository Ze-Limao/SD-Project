package SD.TrabalhoG27;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Statistics {
    private int availableMemory;
    private int activeTasks;

    private final ReentrantLock lock = new ReentrantLock();
    private final Queue<Condition> conds = new ArrayDeque<>();

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

    /**Função responsavel por atualizar as estatisticas antes de uma task começar
     * Se não tem memória disponivel espera até ter
     * */
    public void newTask(int memory){
        try {
            this.lock.lock();
            activeTasks ++;
            if (this.availableMemory < memory) {
                Condition cond = lock.newCondition();
                conds.add(cond);
                while (this.availableMemory<memory)
                    cond.await();
            }

            this.availableMemory-= memory;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally{
            this.lock.unlock();
        }
    }

    /**Função responsavel por atualizar as estatisticas depois de uma task acabar*/
    public void endTask(int memory){
        try {
            this.lock.lock();
            activeTasks -= 1;
            this.availableMemory += memory;

            if (!conds.isEmpty()) {
                conds.poll().signal();
            }
        }finally{
            this.lock.unlock();
        }
    }

}

