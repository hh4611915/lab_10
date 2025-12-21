package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.Scanner;

public class SudokuGUI extends JFrame {
    private SudokuController controller;
    private JTextField[][] cells = new JTextField[9][9];
    private JButton btnVerify, btnSolve, btnUndo;
    private int[][] previousState = new int[9][9];
    private boolean isProgrammaticUpdate = false;
    private String currentDifficulty = "";

    public SudokuGUI() {
        controller = new SudokuController();
        initUI();
        startFlow();
    }

    private void initUI() {
        setTitle("Lab 10 Sudoku");
        setSize(600, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel boardPanel = new JPanel(new GridLayout(9, 9));
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j] = new JTextField();
                cells[i][j].setHorizontalAlignment(JTextField.CENTER);
                cells[i][j].setFont(new Font("SansSerif", Font.BOLD, 20));

                final int r = i;
                final int c = j;

                cells[i][j].addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        try {
                            String txt = cells[r][c].getText();
                            previousState[r][c] = txt.isEmpty() ? 0 : Integer.parseInt(txt);
                        } catch (Exception ex) {
                            previousState[r][c] = 0;
                        }
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        handleInputChange(r, c);
                    }
                });

                boardPanel.add(cells[i][j]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        btnVerify = new JButton("Verify");
        btnSolve = new JButton("Solve");
        btnUndo = new JButton("Undo");

        btnSolve.setEnabled(false);

        btnVerify.addActionListener(e -> verifyAction());
        btnSolve.addActionListener(e -> solveAction());
        btnUndo.addActionListener(e -> undoAction());

        controlPanel.add(btnVerify);
        controlPanel.add(btnSolve);
        controlPanel.add(btnUndo);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void startFlow() {
        Catalog cat = controller.getCatalog();
        try {
            if (cat.current) {
                currentDifficulty = "incomplete";
                int[][] currentBoard = controller.getGame("incomplete");
                int[][] initialBoard = controller.getInitialGame("incomplete");

                loadGameToBoard(currentBoard, initialBoard);
                JOptionPane.showMessageDialog(this, "Resumed unfinished game.");
            }
            else if (cat.allModesExist) {
                askDifficultyAndLoad();
            }
            else {
                String path = JOptionPane.showInputDialog(this,
                        "No games found.\nPlease enter path to solved Sudoku file (e.g. data/solved.txt):");

                if (path != null && !path.trim().isEmpty()) {
                    int[][] source = loadSourceFile(path.trim());
                    controller.generateGamesFromSource(source);
                    JOptionPane.showMessageDialog(this, "Games generated successfully!");
                    askDifficultyAndLoad();
                } else {
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void askDifficultyAndLoad() {
        String[] options = {"EASY", "MEDIUM", "HARD"};
        int choice = JOptionPane.showOptionDialog(this, "Select Difficulty", "New Game",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice != -1) {
            currentDifficulty = options[choice];
            int[][] board = controller.getGame(options[choice]);
            controller.createIncompleteGame(board);
            loadGameToBoard(board, board);
        } else {
            System.exit(0);
        }
    }


    private void loadGameToBoard(int[][] currentBoard, int[][] initialBoard) {
        isProgrammaticUpdate = true;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int val = currentBoard[i][j];

                if (val != 0) {
                    cells[i][j].setText(String.valueOf(val));
                } else {
                    cells[i][j].setText("");
                }

                if (initialBoard[i][j] != 0) {
                    cells[i][j].setEditable(false);
                    cells[i][j].setBackground(Color.LIGHT_GRAY);
                } else {
                    cells[i][j].setEditable(true);
                    cells[i][j].setBackground(Color.WHITE);
                }
            }
        }
        checkSolveButton();
        isProgrammaticUpdate = false;
    }


    private void loadGameToBoard(int[][] board) {
        loadGameToBoard(board, board);
    }

    private void handleInputChange(int r, int c) {
        if (isProgrammaticUpdate) return;
        checkSolveButton();
        try {
            String valTxt = cells[r][c].getText();
            int val = valTxt.isEmpty() ? 0 : Integer.parseInt(valTxt);
            int prev = previousState[r][c];
            if (val != prev) {
                controller.logMove(r, c, val, prev);
            }
        } catch (NumberFormatException e) { }
    }

    private void undoAction() {
        int[] move = controller.undo();
        if (move != null) {
            isProgrammaticUpdate = true;
            int r = move[0];
            int c = move[1];
            int prevVal = move[2];
            cells[r][c].setText(prevVal == 0 ? "" : String.valueOf(prevVal));
            checkSolveButton();
            isProgrammaticUpdate = false;
        } else {
            JOptionPane.showMessageDialog(this, "Nothing to undo!");
        }
    }

    private void checkSolveButton() {
        int emptyCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (cells[i][j].getText().trim().isEmpty()) {
                    emptyCount++;
                }
            }
        }
        btnSolve.setEnabled(emptyCount == 5);
    }

    private void verifyAction() {
        int[][] currentBoard = parseBoard();
        String result = controller.verifyGame(currentBoard);
        JOptionPane.showMessageDialog(this, "Verification Result: " + result);

        if (result.equals("VALID")) {
            controller.onGameWon(currentDifficulty);
            JOptionPane.showMessageDialog(this, "Game Completed! File deleted.\nExiting...");
            System.exit(0);
        }
    }

    private void solveAction() {
        try {
            int[][] currentBoard = parseBoard();
            int[][] solved = controller.solveGame(currentBoard);
            loadGameToBoard(solved); // Show solved state
            JOptionPane.showMessageDialog(this, "Puzzle Solved!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Solver failed: " + e.getMessage());
        }
    }

    private int[][] parseBoard() {
        int[][] temp = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String txt = cells[i][j].getText();
                try {
                    temp[i][j] = txt.isEmpty() ? 0 : Integer.parseInt(txt);
                } catch (NumberFormatException e) {
                    temp[i][j] = 0;
                }
            }
        }
        return temp;
    }

    private int[][] loadSourceFile(String path) throws Exception {
        Scanner sc = new Scanner(new File(path));
        int[][] board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sc.hasNextInt()) board[i][j] = sc.nextInt();
            }
        }
        sc.close();
        return board;
    }
}