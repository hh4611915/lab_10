import java.util.List;
public interface SudokuAggregation {
    void addCombination(int[] combination);
    List<int[]> getCombinations();
}
