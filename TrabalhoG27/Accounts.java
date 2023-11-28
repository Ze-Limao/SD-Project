package TrabalhoG27;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Accounts {
    private final Map<String, Account> accounts;

    private final ReentrantLock lock;

    public Accounts() {
        this.accounts = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public boolean isActive(String key){
        return accounts.get(key).isActive();
    }
    public void setActive(String key, boolean b){
        lock.lock();
        accounts.get(key).setActive(b);
        lock.unlock();
    }
    public boolean loginAttempt(Account acc){
        lock.lock();
        try {
            boolean b = false;
            if (!accounts.containsKey(acc.getName())) {
                accounts.put(acc.getName(), new Account(acc));
                b = true;

            } else if (accounts.get(acc.getName()).verifyPassword(acc) && !accounts.get(acc.getName()).isActive()) {
                isActive(acc.getName());
                setActive(acc.getName(), true);
                isActive(acc.getName());
                b = true;
            }
            return b;
        }
        finally {
            lock.unlock();
        }
    }
}
