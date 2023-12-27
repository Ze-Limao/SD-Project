package SD.TrabalhoG27;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
public class Account {

    private final String name;
    private final String password;
    private boolean active;

    public Account(String name, String password){
        this.name = name;
        this.password = password;
        this.active = true;
    }
    public Account(Account acc){
        this.name = acc.name;
        this.password = acc.password;
        this.active = true;
    }

    public void setActive(Boolean bool){
        active = bool;
    }

    public boolean isActive() {
        return active;
    }

    public String getName() {
        return name;
    }

    public boolean verifyPassword(Account acc){
        return this.password.equals(acc.password);
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(password);
    }
    // @TODO
    public static Account deserialize(DataInputStream in) throws IOException {
        String name = in.readUTF();
        String password = in.readUTF();
        return new Account(name,password);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account acc = (Account) o;
        return this.name.equals(acc.name) && this.password.equals(acc.password);
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active +
                '}';
    }
}
