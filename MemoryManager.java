import java.util.*;

public class MemoryManager {
    public int memorySize;
    public List<MemoryBlock> memoryBlocks = new ArrayList<>();

    public MemoryManager(int memorySize) {
        this.memorySize = memorySize;
        reset();
    }

    public void reset() {
        memoryBlocks.clear();
        memoryBlocks.add(new MemoryBlock(0, memorySize - 1));
    }

    // Basic First Fit allocation
    public boolean allocateFirstFit(Process process) {
        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (!block.allocated && block.size() >= process.size) {
                int originalEnd = block.end;
                block.end = block.start + process.size - 1;
                block.allocated = true;
                block.processId = process.id;
                if (block.end < originalEnd)
                    memoryBlocks.add(i + 1, new MemoryBlock(block.end + 1, originalEnd));
                return true;
            }
        }
        return false;
    }

    // Best Fit
    public boolean allocateBestFit(Process process) {
        int bestIdx = -1, bestSize = Integer.MAX_VALUE;
        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (!block.allocated && block.size() >= process.size && block.size() < bestSize) {
                bestSize = block.size();
                bestIdx = i;
            }
        }
        if (bestIdx == -1) return false;
        MemoryBlock block = memoryBlocks.get(bestIdx);
        int originalEnd = block.end;
        block.end = block.start + process.size - 1;
        block.allocated = true;
        block.processId = process.id;
        if (block.end < originalEnd)
            memoryBlocks.add(bestIdx + 1, new MemoryBlock(block.end + 1, originalEnd));
        return true;
    }

    // Worst Fit
    public boolean allocateWorstFit(Process process) {
        int worstIdx = -1, worstSize = -1;
        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (!block.allocated && block.size() >= process.size && block.size() > worstSize) {
                worstSize = block.size();
                worstIdx = i;
            }
        }
        if (worstIdx == -1) return false;
        MemoryBlock block = memoryBlocks.get(worstIdx);
        int originalEnd = block.end;
        block.end = block.start + process.size - 1;
        block.allocated = true;
        block.processId = process.id;
        if (block.end < originalEnd)
            memoryBlocks.add(worstIdx + 1, new MemoryBlock(block.end + 1, originalEnd));
        return true;
    }

    public boolean deallocate(String pid) {
        boolean found = false;
        for (MemoryBlock b : memoryBlocks) {
            if (b.allocated && b.processId.equals(pid)) {
                b.allocated = false; b.processId="";
                found = true;
            }
        }
        // Merge adjacent free blocks
        for(int i=0; i<memoryBlocks.size()-1;)
            if(!memoryBlocks.get(i).allocated && !memoryBlocks.get(i+1).allocated) {
                memoryBlocks.get(i).end = memoryBlocks.get(i+1).end;
                memoryBlocks.remove(i+1);
            } else i++;
        return found;
    }
}
