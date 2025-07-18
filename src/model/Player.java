package model;


public enum Player {
    X, O, NONE;

    public Player opposite() {
        return this == X ? O : (this == O ? X : NONE);
    }
}