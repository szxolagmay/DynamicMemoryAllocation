public class BlockSplittingAndMerging {
    public static void splitBlock(java.util.List<MemoryBlock> blocks, int idx, int requestSize) {
        MemoryBlock b = blocks.get(idx);
        if (!b.allocated && b.size() > requestSize) {
            MemoryBlock newBlock = new MemoryBlock(b.start + requestSize, b.end);
            b.end = b.start + requestSize - 1;
            blocks.add(idx + 1, newBlock);
        }
    }
    public static void coalesceBlocks(java.util.List<MemoryBlock> blocks) {
        for (int i = 0; i < blocks.size() - 1;) {
            if (!blocks.get(i).allocated && !blocks.get(i + 1).allocated) {
                blocks.get(i).end = blocks.get(i + 1).end;
                blocks.remove(i + 1);
            } else {
                i++;
            }
        }
    }
}
