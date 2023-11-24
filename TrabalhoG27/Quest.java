package SD.TrabalhoG27;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Quest {
    private final int memory;
    private final String code;

    public Quest(int memory, String code) {
        this.memory = memory;
        this.code = code;
    }

    public int getMemory() {
        return memory;
    }

    public String getCode() {
        return code;
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(memory);
        out.writeUTF(code);
    }
    // @TODO
    public static Quest deserialize(DataInputStream in) throws IOException {
        int memory = in.readInt();
        String code = in.readUTF();
        return new Quest(memory,code);
    }
}
