package Model;

import Logika.GenerujRuch;
import Logika.LogikaRuchu;

import java.awt.Point;
import java.util.List;

public class Gra {

    /** The current state of the checker board. */
    private Plansza plansza;

    /** The flag indicating if it is player 1's turn. */
    private boolean isP1Turn;

    /** The index of the last skip, to allow for multiple skips in a turn. */
    private int skipIndex;

    public Gra() {
        restart();
    }

    public Gra(String state) {
        setGameState(state);
    }

    public Gra(Plansza plansza, boolean isP1Turn, int skipIndex) {
        this.plansza = (plansza == null)? new Plansza() : plansza;
        this.isP1Turn = isP1Turn;
        this.skipIndex = skipIndex;
    }

    /**
     * Creates a copy of this game such that any modifications made to one are
     * not made to the other.
     *
     * @return an exact copy of this game.
     */
    public Gra copy() {
        Gra g = new Gra();
        g.plansza = plansza.copy();
        g.isP1Turn = isP1Turn;
        g.skipIndex = skipIndex;
        return g;
    }

    /**
     * Resets the game of checkers to the initial state.
     */
    public void restart() {
        this.plansza = new Plansza();
        this.isP1Turn = true;
        this.skipIndex = -1;
    }

    /**
     * Attempts to make a move from the start point to the end point.
     *
     * @param start	the start point for the move.
     * @param end	the end point for the move.
     * @return true if and only if an update was made to the game state.
     * @see {@link #move(int, int)}
     */
    public boolean move(Point start, Point end) {
        if (start == null || end == null) {
            return false;
        }
        return move(Plansza.toIndex(start), Plansza.toIndex(end));
    }

    /**
     * Attempts to make a move given the start and end index of the move.
     *
     * @param startIndex	the start index of the move.
     * @param endIndex		the end index of the move.
     * @return true if and only if an update was made to the game state.
     * @see {@link #move(Point, Point)}
     */
    public boolean move(int startIndex, int endIndex) {

        // Validate the move
        if (!LogikaRuchu.isValidMove(this, startIndex, endIndex)) {
            return false;
        }

        // Make the move
        Point middle = Plansza.middle(startIndex, endIndex);
        int midIndex = Plansza.toIndex(middle);
        this.plansza.set(endIndex, plansza.get(startIndex));
        this.plansza.set(midIndex, Plansza.EMPTY);
        this.plansza.set(startIndex, Plansza.EMPTY);

        // Make the checker a king if necessary
        Point end = Plansza.toPoint(endIndex);
        int id = plansza.get(endIndex);
        boolean switchTurn = false;
        if (end.y == 0 && id == Plansza.WHITE_CHECKER) {
            this.plansza.set(endIndex, Plansza.WHITE_KING);
            switchTurn = true;
        } else if (end.y == 7 && id == Plansza.BLACK_CHECKER) {
            this.plansza.set(endIndex, Plansza.BLACK_KING);
            switchTurn = true;
        }

        // Check if the turn should switch (i.e. no more skips)
        boolean midValid = Plansza.isValidIndex(midIndex);
        if (midValid) {
            this.skipIndex = endIndex;
        }
        if (!midValid || GenerujRuch.getSkips(
                plansza.copy(), endIndex).isEmpty()) {
            switchTurn = true;
        }
        if (switchTurn) {
            this.isP1Turn = !isP1Turn;
            this.skipIndex = -1;
        }

        return true;
    }

    /**
     * Gets a copy of the current board state.
     *
     * @return a non-reference to the current game board state.
     */
    public Plansza getBoard() {
        return plansza.copy();
    }

    /**
     * Determines if the game is over. The game is over if one or both players
     * cannot make a single move during their turn.
     *
     * @return true if the game is over.
     */
    public boolean isGameOver() {

        // Ensure there is at least one of each checker
        List<Point> black = plansza.find(Plansza.BLACK_CHECKER);
        black.addAll(plansza.find(Plansza.BLACK_KING));
        if (black.isEmpty()) {
            return true;
        }
        List<Point> white = plansza.find(Plansza.WHITE_CHECKER);
        white.addAll(plansza.find(Plansza.WHITE_KING));
        if (white.isEmpty()) {
            return true;
        }

        // Check that the current player can move
        List<Point> test = isP1Turn? black : white;
        for (Point p : test) {
            int i = Plansza.toIndex(p);
            if (!GenerujRuch.getMoves(plansza, i).isEmpty() ||
                    !GenerujRuch.getSkips(plansza, i).isEmpty()) {
                return false;
            }
        }

        // No moves
        return true;
    }

    public boolean isP1Turn() {
        return isP1Turn;
    }

    public void setP1Turn(boolean isP1Turn) {
        this.isP1Turn = isP1Turn;
    }

    public int getSkipIndex() {
        return skipIndex;
    }

    /**
     * Gets the current game state as a string of data that can be parsed by
     * {@link #setGameState(String)}.
     *
     * @return a string representing the current game state.
     * @see {@link #setGameState(String)}
     */
    public String getGameState() {

        // Add the game board
        String state = "";
        for (int i = 0; i < 32; i ++) {
            state += "" + plansza.get(i);
        }

        // Add the other info
        state += (isP1Turn? "1" : "0");
        state += skipIndex;

        return state;
    }

    /**
     * Parses a string representing a game state that was generated from
     * {@link #getGameState()}.
     *
     * @param state	the game state.
     * @see {@link #getGameState()}
     */
    public void setGameState(String state) {

        restart();

        // Trivial cases
        if (state == null || state.isEmpty()) {
            return;
        }

        // Update the board
        int n = state.length();
        for (int i = 0; i < 32 && i < n; i ++) {
            try {
                int id = Integer.parseInt("" + state.charAt(i));
                this.plansza.set(i, id);
            } catch (NumberFormatException e) {}
        }

        // Update the other info
        if (n > 32) {
            this.isP1Turn = (state.charAt(32) == '1');
        }
        if (n > 33) {
            try {
                this.skipIndex = Integer.parseInt(state.substring(33));
            } catch (NumberFormatException e) {
                this.skipIndex = -1;
            }
        }
    }
}
