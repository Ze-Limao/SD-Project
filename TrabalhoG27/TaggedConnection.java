package SD.TrabalhoG27;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {

    private final Socket s;
    private final Lock readLock = new ReentrantLock();
    private final Lock writeLock = new ReentrantLock();
    private final DataInputStream in;
    private final DataOutputStream out;

    public TaggedConnection(Socket socket) throws IOException {
        s = socket;
        this.out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
        this.in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
    }

    public void send(int tag, int ask, Account acc) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(ask);
            acc.serialize(this.out);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }
    public void send(int tag, int ask, Quest quest) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(ask);
            quest.serialize(this.out);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void send(int tag, int ask, boolean bool) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(ask);
            this.out.writeBoolean(bool);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void send(int tag, int ask, String str) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(ask);
            this.out.writeUTF(str);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void send(int tag, int ask, boolean bool, byte[] b) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(ask);
            this.out.writeBoolean(bool);
            this.out.writeInt(b.length);
            this.out.write(b);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }
    public void send(boolean bool, byte[] b) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeBoolean(bool);
            this.out.writeInt(b.length);
            this.out.write(b);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void send(int tag) throws IOException{
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.flush();

        }
        finally {
        this.writeLock.unlock();
        }
    }

    public void send(String string) throws IOException{
        this.writeLock.lock();
        try{
            this.out.writeUTF(string);
            this.out.flush();

        }
        finally {
            this.writeLock.unlock();
        }
    }
    public void send(boolean bool, String string) throws IOException{
        this.writeLock.lock();
        try{
            this.out.writeBoolean(bool);
            this.out.writeUTF(string);
            this.out.flush();

        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void send(int tag, int ask, boolean bool, int err, String msg) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(ask);
            this.out.writeBoolean(bool);
            this.out.writeInt(err);
            this.out.writeUTF(msg);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }
    public void send(int tag, boolean bool, int err, String msg) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeBoolean(bool);
            this.out.writeInt(err);
            this.out.writeUTF(msg);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void send(int tag,  int ask, int activeTasks, int availableMemory) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(ask);
            this.out.writeInt(activeTasks);
            this.out.writeInt(availableMemory);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }
    //receive client<-server
    public Frame receiveFromServer() throws IOException {
        this.readLock.lock();

        try{
            int tag = in.readInt();
            int ask = in.readInt();
            switch (tag) {
                case 1 -> {
                    return new Frame(tag, ask, in.readBoolean());
                }
                case 2 -> {
                    return new Frame(tag,ask,in.readUTF());
                }
                case 3 -> {
                    return new Frame(tag, ask, "There is " + in.readInt() + " bytes available and " + in.readInt() + " tasks waiting");
                }
                default -> {
                    System.out.println("not implemented yet //" + tag + "//");
                    return null;
                }
            }
        } finally {
            this.readLock.unlock();
        }
    }
    //receive server<-client
    public Frame receiveFromClient() throws IOException {
        this.readLock.lock();
        try {
            int tag = in.readInt();

            if (tag == 253 || tag == 254) {

                return new Frame(tag, 0, null);
            }
            int ask = in.readInt();

            switch (tag) {
                case 1, 0 -> {
                    Account acc = Account.deserialize(in);
                    return new Frame(tag, ask, acc);
                }
                case 2 -> {
                    return new Frame(tag, ask, Quest.deserialize(in));
                }
                case 3 -> {
                    in.readUTF();
                    return new Frame(tag, ask, null);
                }
                default -> {
                    System.out.println("not implemented yet 2 //" + tag + "//");
                    return null;
                }
            }
        } finally {
            this.readLock.unlock();
        }
    }
    //Receiving the task on the worker
    public String receiveQuest() throws IOException {
        this.readLock.lock();
        try{
            return (in.readUTF());
        } finally {
            this.readLock.unlock();
        }
    }
    //Receiving the result from the worker
    public String receiveResult()throws IOException{
        this.readLock.lock();
        try{
            if (in.readBoolean()) {
                byte[] b = new byte[in.readInt()];
                in.readFully(b);

                return Arrays.toString(b);
            }
            return (in.readUTF());
        }finally {
            this.readLock.unlock();
        }
    }

    public void close() throws IOException {
        this.readLock.lock();
        this.writeLock.lock();
        this.s.close();
        this.writeLock.unlock();
        this.readLock.unlock();
    }
}
