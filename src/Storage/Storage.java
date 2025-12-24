package src.Storage;

import src.Model.Catalog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    private static Storage instance;
    private static final String[] FOLDERS = {"easy", "medium", "hard", "incomplete"};

    private Storage() {
        for (int i = 0; i < FOLDERS.length; i++) {
            createFolder(FOLDERS[i]);
        }
    }

    public static synchronized Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    private void createFolder(String name) {
        File f = new File(name);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public Catalog getCatalogInfo() {
        Catalog c = new Catalog();
        c.current = new File("incomplete/game.csv").exists();
        c.allModesExist = new File("easy/game.csv").exists() &&
                new File("medium/game.csv").exists() &&
                new File("hard/game.csv").exists();
        return c;
    }

    public void saveGame(String folder, int[][] board) throws IOException {
        File file = new File(folder + "/game.csv");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    writer.print(board[i][j]);
                    if (j < 8) writer.print(",");
                }
                writer.println();
            }
        }
    }

    public int[][] loadRawBoard(String folder) throws NotFoundException {
        File file = new File(folder + "/game.csv");
        if (!file.exists()) {
            throw new NotFoundException("No game found in folder: " + folder);
        }

        int[][] board = new int[9][9];
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < 9) {
                String[] parts = line.split(",");
                if (parts.length == 9) {
                    for (int col = 0; col < 9; col++) {
                        board[row][col] = Integer.parseInt(parts[col].trim());
                    }
                }
                row++;
            }
        } catch (IOException | NumberFormatException e) {
            throw new NotFoundException("Error reading file: " + folder);
        }
        return board;
    }

    public int[][] loadGame(String folder) throws NotFoundException {
        int[][] board = loadRawBoard(folder);

        if (folder.equals("incomplete")) {
            File logFile = new File("incomplete/log.csv");
            if (logFile.exists()) {
                try (BufferedReader logReader = new BufferedReader(new FileReader(logFile))) {
                    String logLine;
                    while ((logLine = logReader.readLine()) != null) {
                        String[] parts = logLine.split(",");
                        if (parts.length >= 3) {
                            int r = Integer.parseInt(parts[0]);
                            int c = Integer.parseInt(parts[1]);
                            int val = Integer.parseInt(parts[2]);
                            board[r][c] = val;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Warning: Could not replay log.");
                }
            }
        }
        return board;
    }

    public void deleteGame(String folder) {
        File file = new File(folder + "/game.csv");
        if (file.exists()) file.delete();

        if (folder.equals("incomplete")) {
            File log = new File("incomplete/log.csv");
            if (log.exists()) log.delete();
        }
    }

    public void logMove(int x, int y, int val, int prev) throws IOException {
        File logFile = new File("incomplete/log.csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile, true))) {
            pw.println(x + "," + y + "," + val + "," + prev);
        }
    }

    public String undoLastLog() throws IOException {
        File logFile = new File("incomplete/log.csv");
        if (!logFile.exists()) return null;

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        if (lines.isEmpty()) return null;
        String lastAction = lines.remove(lines.size() - 1);

        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile))) {
            for (int i = 0; i < lines.size(); i++) {
                pw.println(lines.get(i));
            }
        }
        return lastAction;
    }
}