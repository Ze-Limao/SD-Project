package SD.TrabalhoG27;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Statistics {
    private int availableMemory;
    private int activeTasks;

    private class TaskMemory {
        public Condition cond;
        public int memory;
        public int i;

        public TaskMemory(Condition cond, int memory){
            this.cond = cond;
            this.memory = memory;
            this.i = 0;

        }
    }

    private final ReentrantLock lock = new ReentrantLock();
    private final Queue<TaskMemory> conds = new ArrayDeque<>();

    public Statistics(int memory) {
        this.availableMemory = memory;
        this.activeTasks = 0;

    }

    public int getAvailableMemory() {
        this.lock.lock();
        try {
            return availableMemory;
        }finally{
            this.lock.unlock();
        }
    }

    public int getActiveTasks() {
        this.lock.lock();
        try{
            return activeTasks;
        }finally {
            this.lock.unlock();
        }
    }

    /**Função responsavel por atualizar as estatisticas antes de uma task começar
     * Se não tem memória disponivel espera até ter
     * */
    public void newTask(int memory){
        try {
            this.lock.lock();
            activeTasks ++;


            if (!conds.isEmpty()|| this.availableMemory < memory) {
                Condition cond = lock.newCondition();
                TaskMemory tm = new TaskMemory(cond, memory);
                conds.add(tm);
                while (this.availableMemory < memory){
                    cond.await();
                    tm.i++;
                }
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
            int memoriaDisponivel= this.availableMemory;
            int vistos = 0;
            while (!conds.isEmpty() && vistos < conds.size()  && memoriaDisponivel > 0){
                TaskMemory tm =conds.peek();
                //verificar se terá memória soficiente
                if (memoriaDisponivel < tm.memory){
                    //se não tem: verifica se i < 5
                    if (tm.i < 5){
                        // verifica-se:passa para o fim da fila e incrementa o i
                        tm.i++;
                        conds.add(conds.poll());
                        vistos++;
                    }
                    else{
                        // nao se verifica: não sinaliza e espera até ter
                        break;
                    }
                }
                //se tem: incrementa memória a ser indisponbilizada, sinaliza, e verifica se a próxima tm pode ser sinalizada ou não
                else{
                    memoriaDisponivel -= tm.memory;
                    conds.poll().cond.signal();
                }
            }
        }finally{
            this.lock.unlock();
        }
    }

}

