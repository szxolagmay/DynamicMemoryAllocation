import java.util.ArrayList;
import java.util.List;

public class memoryManager {
    private int totalSize;
    private List<Block> blocks;

    public memoryManager(int totalSize) {
        this.totalSize = totalSize;
        this.blocks = new ArrayList<>();
    }

    public boolean allocate(int size) {
        int startAddress = 0;

        // Find first free space big enough
        for (Block b : blocks) {
            if (startAddress + size <= b.address) {
                break;
            }
            startAddress = b.address + b.size;
        }

        if (startAddress + size > totalSize) {
            return false; // Not enough space
        }

        blocks.add(new Block(startAddress, size, true));
        blocks.sort((a, b) -> a.address - b.address);
        return true;
    }

    public boolean free(int address) {
        return blocks.removeIf(b -> b.address == address);
    }

    public boolean resize(int address, int newSize) {
        for (Block b : blocks) {
            if (b.address == address) {
                if (address + newSize <= totalSize) {
                    b.size = newSize;
                    return true;
                }
            }
        }
        return false;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    // Inner class representing a memory block
    static class Block {
        int address;
        int size;
        boolean allocated;

        Block(int address, int size, boolean allocated) {
            this.address = address;
            this.size = size;
            this.allocated = allocated;
        }
    }
}
