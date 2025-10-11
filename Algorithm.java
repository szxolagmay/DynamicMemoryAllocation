/**
 * Implements Best Fit and First Fit memory allocation algorithms,
 * with a simple FSM for allocation states.
 */
public class Algorithm {

    enum Strategy {
        FIRST_FIT,
        BEST_FIT
    }

    enum State {
        IDLE,
        ALLOCATING,
        DEALLOCATING,
        ERROR
    }

    private static class Block {
        int start;
        int size;
        boolean isFree;
        Block next;

        Block(int start, int size) {
            this.start = start;
            this.size = size;
            this.isFree = true;
            this.next = null;
        }
    }

    private Block head;
    private State state;
    private Strategy strategy;

    public Algorithm(int totalSize, Strategy chosenStrategy) {
        this.head = new Block(0, totalSize);
        this.strategy = chosenStrategy;
        this.state = State.IDLE;
    }

    /**
     * Allocates memory using either First Fit or Best Fit strategy.
     */
    public int allocate(int size) {
        state = State.ALLOCATING;
        Block target = null, prev = null, bestBlock = null, bestPrev = null;
        if (strategy == Strategy.FIRST_FIT) {
            Block current = head;
            while (current != null) {
                if (current.isFree && current.size >= size) {
                    target = current;
                    break;
                }
                prev = current;
                current = current.next;
            }
        } else if (strategy == Strategy.BEST_FIT) {
            Block current = head;
            int minSize = Integer.MAX_VALUE;
            while (current != null) {
                if (current.isFree && current.size >= size && current.size < minSize) {
                    bestBlock = current;
                    bestPrev = prev;
                    minSize = current.size;
                }
                prev = current;
                current = current.next;
            }
            target = bestBlock;
        }

        if (target != null) {
            if (target.size > size) {
                Block newBlock = new Block(target.start + size, target.size - size);
                newBlock.next = target.next;
                target.next = newBlock;
                target.size = size;
            }
            target.isFree = false;
            state = State.IDLE;
            return target.start;
        }
        state = State.ERROR;
        return -1;
    }

    /**
     * Frees memory block, then coalesces.
     */
    public void free(int address) {
        state = State.DEALLOCATING;
        Block current = head;
        while (current != null) {
            if (current.start == address && !current.isFree) {
                current.isFree = true;
                coalesce();
                state = State.IDLE;
                return;
            }
            current = current.next;
        }
        state = State.ERROR;
    }

    /**
     * Combines adjacent free blocks.
     */
    private void coalesce() {
        Block current = head;
        while (current != null && current.next != null) {
            if (current.isFree && current.next.isFree) {
                current.size += current.next.size;
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }
    }

    /**
     * Returns current FSM state.
     */
    public State getState() {
        return state;
    }

    /**
     * Switch allocation strategy (First Fit or Best Fit).
     */
    public void setStrategy(Strategy newStrategy) {
        this.strategy = newStrategy;
    }
}
