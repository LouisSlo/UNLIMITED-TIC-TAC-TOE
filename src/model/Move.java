package model;

public class Move {
    private final int subBoardIdx;
    private final int cellIdx;
    private final Player player;
    private final long timestamp;

    public Move(int subBoardIdx, int cellIdx, Player player) {
        this.subBoardIdx = subBoardIdx;
        this.cellIdx = cellIdx;
        this.player = player;
        this.timestamp = System.currentTimeMillis();
    }

    public int getSubBoardIdx() {
        return subBoardIdx;
    }

    public int getCellIdx() {
        return cellIdx;
    }

    public Player getPlayer() {
        return player;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s -> sub %d, cell %d",
                player, subBoardIdx, cellIdx);
    }
}
