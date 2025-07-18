package model;

//A single field in a 3×3 subboard.
public class Cell {
    private Player owner = Player.NONE;

    public Player getOwner() {
        return owner;
    }
    public void setOwner(Player owner) {
        this.owner = owner;
    }
    public boolean isEmpty() {
        return owner == Player.NONE;
    }

}
