package src.BackendIterator;

public class ConcreteSudokuAggregation implements SudokuAggregation {
    private final long startIndex;
    private final long endIndex;
    private final int numCells;
    private final boolean[] stopFlag;

    public ConcreteSudokuAggregation(long startIndex, long endIndex, int numCells, boolean[] stopFlag) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.numCells = numCells;
        this.stopFlag = stopFlag;
    }

    @Override
    public SudokuIterator createIterator() {
        return new ConcreteSudokuIterator(startIndex, endIndex, numCells, stopFlag);
    }
}
