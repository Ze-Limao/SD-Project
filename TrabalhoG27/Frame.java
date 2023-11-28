package TrabalhoG27;

public class Frame {
    public final int tag;
    public final int src;
    public final int ask;

    public Object obj;

    public Frame(int tag,int src, int ask, Object obj) {
        this.tag = tag;
        this.src = src;
        this.ask = ask;
        this.obj = obj;
    }

}
