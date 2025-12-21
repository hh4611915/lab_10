import java.util.List;

public class ConcreteSudokuIterator implements SudokuIterator {
    private List<int[]> combinations;
    private int currentIndex = 0;

    public ConcreteSudokuIterator(List<int[]> combinations) {
        this.combinations = combinations;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < combinations.size();
    }

    @Override
    public int[] next() {
        if (hasNext()) {
            return combinations.get(currentIndex++);
        }
        return null;
    }
}
