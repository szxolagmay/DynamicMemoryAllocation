public class MemoryBlock {
    public int start;
    public int end;
    public boolean allocated;
    public String processId;

    public MemoryBlock(int start, int end) {
        this.start = start;
        this.end = end;
        this.allocated = false;
        this.processId = "";
    }

    public int size() {
        return end - start + 1;
    }
}
