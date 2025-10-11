public class FragmentationMetrics {
    public static int calculateExternalFragmentation(java.util.List<MemoryBlock> blocks) {
        int maxFreeBlock = blocks.stream().filter(b -> !b.allocated).mapToInt(MemoryBlock::size).max().orElse(0);
        int totalFreeMemory = blocks.stream().filter(b -> !b.allocated).mapToInt(MemoryBlock::size).sum();
        return totalFreeMemory - maxFreeBlock;
    }
    public static int calculateInternalFragmentation(java.util.List<MemoryBlock> blocks) {
        return blocks.stream()
                .filter(b -> b.allocated)
                .mapToInt(b -> b.size() - Math.max(1, b.size() * 2 / 3)) // Dummy formula
                .sum();
    }
}
