package src;

public class ConcreteSudokuIterator implements SudokuIterator {
    private int[] currentCombination;
    private boolean hasMore;

    public ConcreteSudokuIterator() {
        this.currentCombination = new int[]{1, 1, 1, 1, 1};
        this.hasMore = true;
    }

    @Override
    public boolean hasNext() {
        return hasMore;
    }

    @Override
    public int[] next() {
        if (!hasMore) return null;

        int[] result = currentCombination.clone();

        increment(4);
        return result;
    }

    private void increment(int index) {
        if (index < 0) {
            hasMore = false;
            return;
        }

        currentCombination[index]++;

        if (currentCombination[index] > 9) {
            currentCombination[index] = 1;
            increment(index - 1);
        }
    }
}
