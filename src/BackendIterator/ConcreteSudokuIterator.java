package src.BackendIterator;

public class ConcreteSudokuIterator implements SudokuIterator {
    private long currentIndex;
    private final long endIndex;
    private final int numCells;
    private final boolean[] stopFlag;

    public ConcreteSudokuIterator(long startIndex, long endIndex, int numCells, boolean[] stopFlag) {
        this.currentIndex = startIndex;
        this.endIndex = endIndex;
        this.numCells = numCells;
        this.stopFlag = stopFlag;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < endIndex && !stopFlag[0];
    }

    @Override
    public int[] next() {
        int[] perm = new int[numCells];
        long temp = currentIndex;

        for (int i = 0; i < numCells; i++) {
            perm[i] = (int)(temp % 9) + 1;
            temp /= 9;
        }

        currentIndex++;
        return perm;
    }
}