public class MMRealloc {
    public static boolean realloc(java.util.List<MemoryBlock> blocks, String processId, int newSize) {
        for (int i = 0; i < blocks.size(); i++) {
            MemoryBlock b = blocks.get(i);
            if (b.allocated && b.processId.equals(processId)) {
                int currentSize = b.size();
                if (newSize < currentSize) {
                    BlockSplittingAndMerging.splitBlock(blocks, i, newSize);
                    return true;
                } else if (newSize > currentSize) {
                    if (i + 1 < blocks.size()) {
                        MemoryBlock next = blocks.get(i + 1);
                        if (!next.allocated && (next.size() >= (newSize - currentSize))) {
                            b.end += newSize - currentSize;
                            next.start = b.end + 1;
                            if (next.start > next.end) blocks.remove(i + 1);
                            return true;
                        }
                    }
                    return false; // Can't expand in place
                }
                return true;
            }
        }
        return false;
    }
}
