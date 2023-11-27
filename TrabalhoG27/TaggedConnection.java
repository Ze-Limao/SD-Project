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

    public void send(int tag, int src, Account acc) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(src);
            acc.serialize(this.out);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }
    public void send(int tag, int src, Quest quest) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(src);
            quest.serialize(this.out);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void send(int tag, int src, boolean bool) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(src);
            this.out.writeBoolean(bool);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void send(int tag, int src, String str) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(src);
            this.out.writeUTF(str);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void send(int tag, int src, boolean bool, byte[] b) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(src);
            this.out.writeBoolean(bool);
            this.out.writeInt(b.length);
            this.out.write(b);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }
    public void send(int tag, int src, boolean bool, int err, String msg) throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(src);
            this.out.writeBoolean(bool);
            this.out.writeInt(err);
            this.out.writeUTF(msg);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void send(int tag, int src, int activeTasks, int availableMemory)throws IOException {
        this.writeLock.lock();
        try{
            this.out.writeInt(tag);
            this.out.writeInt(src);
            this.out.writeInt(activeTasks);
            this.out.writeInt(availableMemory);
            this.out.flush();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public Frame receive() throws IOException {
        this.readLock.lock();
        try{
            int tag = in.readInt();
            int src = in.readInt();

            if (src == 1) {
                switch (tag) {
                    case 1, 0 -> {
                        Account acc = Account.deserialize(in);
                        return new Frame(tag, src, acc);
                    }
                    case 2 -> {
                        return new Frame(tag, src, Quest.deserialize(in));
                    }
                    case 3 -> {
                        in.readUTF();
                        return new Frame(tag, src, null);
                    }
                    default -> System.out.println("not implemented tag on receive");
                }
            }
            else if (src == 0) {
                switch (tag) {
                    case 1 -> {
                        return new Frame(tag, src, in.readBoolean());
                    }
                    case 2 -> {
                        if (in.readBoolean()) {
                            byte[] b = new byte[in.readInt()];
                            in.readFully(b);
                            return new Frame(tag, src, Arrays.toString(b));
                        }
                        return new Frame(tag, src, ("Error " + in.readInt() + ": " + in.readUTF()));
                    }
                    case 3 -> {
                        return new Frame(tag, src, "There is " + in.readInt() + " bytes available and " + in.readInt() + " tasks waiting");
                    }
                    default -> System.out.println("not implemented tag on receive");
                }
            }
            System.out.println("not implemented yet //" + tag + "//" + src + "//");
            return null;
        } finally {
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
