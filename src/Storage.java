package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Storage {

    private static final String[] FOLDERS = {"easy", "medium", "hard", "incomplete"};

    public Storage() {
        for (int i = 0; i < FOLDERS.length; i++) {
            createFolder(FOLDERS[i]);
        }
    }

    private void createFolder(String name) {
        File f = new File(name);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public Catalog getCatalogInfo() {
        Catalog c = new Catalog();

        File unfinished = new File("incomplete/game.csv");
        c.current = unfinished.exists();

        boolean easy = new File("easy/game.csv").exists();
        boolean med = new File("medium/game.csv").exists();
        boolean hard = new File("hard/game.csv").exists();

        c.allModesExist = easy && med && hard;

        return c;
    }

    public void saveGame(String folder, int[][] board) throws IOException {
        File file = new File(folder + "/game.csv");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    writer.print(board[i][j]);
                    if (j < 8) {
                        writer.print(",");
                    }
                }
                writer.println();
            }
        }
    }

    public int[][] loadGame(String folder) throws NotFoundException {
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
        } catch (FileNotFoundException e) {
            throw new NotFoundException("Could not find FILE: " + folder);
        } catch (IOException | NumberFormatException e) {
            throw new NotFoundException("Error reading file: " + folder);
        }
        return board;
    }

    public void deleteGame(String folder) {
        File file = new File(folder + "/game.csv");
        if (file.exists()) {
            file.delete();
        }

        if (folder.equals("incomplete")) {
            File log = new File("incomplete/log.csv");
            if (log.exists()) {
                log.delete();
            }
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
