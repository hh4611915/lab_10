package src;

public class ConcreteSudokuAggregation implements SudokuAggregation {
    @Override
    public SudokuIterator createIterator() {
        return new ConcreteSudokuIterator();
    }
}
