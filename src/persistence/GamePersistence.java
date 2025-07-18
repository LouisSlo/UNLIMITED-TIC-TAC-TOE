package persistence;

import model.GameState;
import model.Player;

import java.io.*;

/**
 * GamePersistence handles saving and loading the GameState to and from a text file.
 * It writes the current player, the active sub-board index, each sub-board's winner,
 * the cell contents, and the move history in a simple line-based format.
 */
public class GamePersistence {

    /**
     * Saves the provided GameState to a text file.
     * Format:
     * 1st line: current player name
     * 2nd line: next active sub-board index
     * For each of 9 sub-boards:
     *   - one line for sub-board winner
     *   - three lines for each row of cell owners as comma-separated names
     * Next line: MOVES=<number of moves>
     * Followed by each move on its own line
     *
     * state the GameState to save
     * file  the file to write to
     * IOException if an I/O error occurs
     */
    public static void saveAsText(GameState state, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write current player
            writer.write(state.getCurrentPlayer().name());
            writer.newLine();
            // Write next active sub-board index
            writer.write(String.valueOf(state.getNextActiveSubBoard()));
            writer.newLine();

            // Save each sub-board's winner and cells
            for (int sb = 0; sb < 9; sb++) {
                writer.write(state.getSubBoardWinner(sb).name());
                writer.newLine();
                for (int r = 0; r < 3; r++) {
                    writer.write(
                            state.getCell(sb, r, 0).name() + "," +
                                    state.getCell(sb, r, 1).name() + "," +
                                    state.getCell(sb, r, 2).name()
                    );
                    writer.newLine();
                }
            }

            // Save move history count and entries
            writer.write("MOVES=" + state.getMoveHistory().size());
            writer.newLine();
            for (String move : state.getMoveHistory()) {
                writer.write(move);
                writer.newLine();
            }
        }
    }

    /**
     * Loads a GameState from the specified text file.
     * Expects the same format produced by saveAsText.
     *
     * file the file to read from
     * the reconstructed GameState
     * IOException if an I/O error occurs or the format is invalid
     */
    public static GameState loadFromText(File file) throws IOException {
        GameState state = new GameState();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Read current player
            state.setCurrentPlayer(Player.valueOf(reader.readLine()));
            // Read next active sub-board index
            state.setNextActiveSubBoard(Integer.parseInt(reader.readLine()));

            // Load each sub-board's winner and cells
            for (int sb = 0; sb < 9; sb++) {
                state.setSubBoardWinner(sb, Player.valueOf(reader.readLine()));
                for (int r = 0; r < 3; r++) {
                    String[] tokens = reader.readLine().split(",");
                    for (int c = 0; c < 3; c++) {
                        state.setCell(sb, r, c, Player.valueOf(tokens[c]));
                    }
                }
            }

            // Read move history count and entries
            String line = reader.readLine();
            if (line != null && line.startsWith("MOVES=")) {
                int count = Integer.parseInt(line.substring(6));
                for (int i = 0; i < count; i++) {
                    String move = reader.readLine();
                    state.getMoveHistory().add(move);
                }
            }
        }
        return state;
    }
}