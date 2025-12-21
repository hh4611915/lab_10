import java.util.List;
import java.util.ArrayList;
public class ConcreteSudokuAggregation implements SudokuAggregation {
    private List<int[]> allCombinations;

    public ConcreteSudokuAggregation() {
        this.allCombinations = new ArrayList<>();
    }

    @Override
    public void addCombination(int[] combination) {
        allCombinations.add(combination);
    }

    @Override
    public List<int[]> getCombinations() {
        return allCombinations;
    }
}
