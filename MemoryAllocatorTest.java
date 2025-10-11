public class MemoryAllocatorTest {
    public static void main(String[] args) {
        MemoryManager mm = new MemoryManager(20); // 20-unit heap

        // Allocate 5 units to P1
        Process p1 = new Process("P1", 5);
        assert mm.allocateFirstFit(p1) : "P1 should allocate";
        System.out.println("After alloc P1: " + mm.memoryBlocks);

        // Allocate 8 units to P2
        Process p2 = new Process("P2", 8);
        assert mm.allocateFirstFit(p2) : "P2 should allocate";
        System.out.println("After alloc P2: " + mm.memoryBlocks);

        // Free P1 and check coalescing is not possible yet
        assert mm.deallocate("P1") : "P1 should free";
        System.out.println("After free P1: " + mm.memoryBlocks);

        // Free P2 and check all is merged free
        assert mm.deallocate("P2") : "P2 should free";
        assert mm.memoryBlocks.size() == 1 && !mm.memoryBlocks.get(0).allocated : "Heap should be all free";
        System.out.println("After free P2: " + mm.memoryBlocks);

        // Try to allocate more than available
        Process p3 = new Process("P3", 25);
        assert !mm.allocateFirstFit(p3) : "P3 should fail (over-allocate)";
        System.out.println("After over-alloc: " + mm.memoryBlocks);

        // Allocate 6 to P4
        Process p4 = new Process("P4", 6);
        assert mm.allocateFirstFit(p4) : "P4 should allocate";
        System.out.println("After alloc P4: " + mm.memoryBlocks);

        // Realloc shrink for P4
        assert MMRealloc.realloc(mm.memoryBlocks, "P4", 3) : "P4 realloc shrink";
        System.out.println("After realloc shrink P4: " + mm.memoryBlocks);

        // Allocate 4 to P5
        Process p5 = new Process("P5", 4);
        assert mm.allocateFirstFit(p5) : "P5 should allocate";
        System.out.println("After alloc P5: " + mm.memoryBlocks);

        // Cleanup
        mm.deallocate("P4");
        mm.deallocate("P5");
        assert mm.memoryBlocks.size() == 1 : "Should be one big free block.";
        System.out.println("Final heap: " + mm.memoryBlocks);

        System.out.println("All tests PASSED.");
    }
}
