public class SudokuSolver {

    public int[] solve(int[][] board, int[] positions) {
        SudokuAggregation aggregation = new ConcreteSudokuAggregation();
        generateCombinations(board, positions, aggregation);

        SudokuIterator iterator = new ConcreteSudokuIterator(aggregation.getCombinations());
        SudokuFlyweight flyweight = new SudokuFlyweight(board);

        while (iterator.hasNext()) {
            int[] combination = iterator.next();
            if (flyweight.checkCombination(combination, positions)) {
                return combination;
            }
        }
        return null;
    }

    private void generateCombinations(int[][] board, int[] positions, SudokuAggregation aggregation) {
        generateCombinationsHelper(board, positions, 0, new int[positions.length], aggregation);
    }

    private void generateCombinationsHelper(int[][] board, int[] positions, int index, int[] currentCombination, SudokuAggregation aggregation) {
        if (index == positions.length) {
            aggregation.addCombination(currentCombination.clone());
            return;
        }

        for (int i = 1; i <= 9; i++) {
            currentCombination[index] = i;
            generateCombinationsHelper(board, positions, index + 1, currentCombination, aggregation);
        }
    }
}
