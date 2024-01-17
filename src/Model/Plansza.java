/*
 * Opis: Ta klasa implementuje szachownicę o wymiarach 8x8 pól. Zgodnie z
 * standardowymi zasadami, pionek może poruszać się tylko po czarnych polach,
 * co oznacza, że jest dostępnych tylko 32 pola. Wykorzystuje trzy liczby
 * całkowite do reprezentowania szachownicy, przydzielając 3 bity dla każdego
 * czarnego pola.
 */

package Model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


public class Plansza {

    public static final int INVALID = -1; /** ID wskazujące, że punkt nie znajduje się na szachownicy. */

    public static final int EMPTY = 0; /** ID pustego pola na szachownicy. */

    public static final int BLACK_CHECKER = 4 * 1 + 2 * 1 + 1 * 0; /** ID białego pionka na szachownicy. */


    public static final int WHITE_CHECKER = 4 * 1 + 2 * 0 + 1 * 0; /** ID czarnego pionka na szachownicy. */


    public static final int BLACK_KING = 4 * 1 + 2 * 1 + 1 * 1; /** ID czarnego pionka będącego królem. */


    public static final int WHITE_KING = 4 * 1 + 2 * 0 + 1 * 1; /** ID białego pionka będącego królem. */


    private int[] state; /** Aktualny stan planszy, reprezentowany przez trzy liczby całkowite. */


    /**
     * Konstruuje nową planszę do gry w warcaby, wypełnioną nowym stanem gry.
     */
    public Plansza() {
        reset();
    }

    /**
     * Tworzy dokładną kopię planszy. Zmiany dokonywane na kopii nie wpływają na
     * bieżący obiekt.
     */
    public Plansza copy() {
        Plansza copy = new Plansza();
        copy.state = state.clone();
        return copy;
    }

    /**
     * Resetuje planszę do gry w warcaby do pierwotnego stanu gry z czarnymi
     * pionkami u góry i białymi na dole. Jest łącznie 12 czarnych pionków i
     * 12 białych pionków.
     */
    public void reset() {

        // Reset the state
        this.state = new int[3];
        for (int i = 0; i < 12; i ++) {
            set(i, BLACK_CHECKER);
            set(31 - i, WHITE_CHECKER);
        }
    }

    /**
     * Przeszukuje planszę do gry w warcaby i znajduje czarne pola pasujące
     * do określonego ID.
     *
     * @param id	ID, które ma zostać znalezione.
     * @return lista punktów na planszy z określonym ID. Jeśli żadne nie
     * istnieją, zwracana jest pusta lista.
     */
    public List<Point> find(int id) {

        // Find all black tiles with matching IDs
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < 32; i ++) {
            if (get(i) == id) {
                points.add(toPoint(i));
            }
        }

        return points;
    }

    /**
     * Ustawia ID czarnego pola na planszy w określonym miejscu. Jeśli miejsce
     * nie jest czarnym polem, nie zostanie nic zaktualizowane. Jeśli ID jest
     * mniejsze niż 0, plansza w tym miejscu zostanie ustawiona na
     * {@link #EMPTY}.
     *
     * @param x		współrzędna x na planszy (od 0 do 7 włącznie).
     * @param y		współrzędna y na planszy (od 0 do 7 włącznie).
     * @param id	nowe ID, które ma być ustawione na czarnym polu.
     * @see {@link #set(int, int)}, {@link #EMPTY}, {@link #BLACK_CHECKER},
     * {@link #WHITE_CHECKER}, {@link #BLACK_KING}, {@link #WHITE_KING}
     */
    public void set(int x, int y, int id) {
        set(toIndex(x, y), id);
    }

    /**
     * Ustawia ID czarnego pola na planszy w określonym miejscu. Jeśli miejsce
     * nie jest czarnym polem, nie zostanie nic zaktualizowane. Jeśli ID jest
     * mniejsze niż 0, plansza w tym miejscu zostanie ustawiona na
     * {@link #EMPTY}.
     *
     * @param index	indeks czarnego pola (od 0 do 31 włącznie).
     * @param id		nowe ID, które ma być ustawione na czarnym polu.
     * @see {@link #set(int, int, int)}, {@link #EMPTY}, {@link #BLACK_CHECKER},
     * {@link #WHITE_CHECKER}, {@link #BLACK_KING}, {@link #WHITE_KING}
     */
    public void set(int index, int id) {

        // Out of range
        if (!isValidIndex(index)) {
            return;
        }

        // Invalid ID, so just set to EMPTY
        if (id < 0) {
            id = EMPTY;
        }

        // Set the state bits
        for (int i = 0; i < state.length; i ++) {
            boolean set = ((1 << (state.length - i - 1)) & id) != 0;
            this.state[i] = setBit(state[i], index, set);
        }
    }

    /**
     * Pobiera ID odpowiadające określonemu punktowi na planszy do gry w warcaby.
     *
     * @param x	współrzędna x na planszy (od 0 do 7 włącznie).
     * @param y	współrzędna y na planszy (od 0 do 7 włącznie).
     * @return ID na określonym miejscu lub {@link #INVALID}, jeśli miejsce nie
     * znajduje się na planszy lub jest to białe pole.
     * @see {@link #get(int)}, {@link #set(int, int)},
     * {@link #set(int, int, int)}
     */
    public int get(int x, int y) {
        return get(toIndex(x, y));
    }

    /**
     * Pobiera ID odpowiadające określonemu punktowi na planszy do gry w warcaby.
     *
     * @param index	indeks czarnego pola (od 0 do 31 włącznie).
     * @return ID na określonym miejscu lub {@link #INVALID}, jeśli miejsce nie
     * znajduje się na planszy.
     * @see {@link #get(int, int)}, {@link #set(int, int)},
     * {@link #set(int, int, int)}
     */
    public int get(int index) {
        if (!isValidIndex(index)) {
            return INVALID;
        }
        return getBit(state[0], index) * 4 + getBit(state[1], index) * 2
                + getBit(state[2], index);
    }

    /**
     * Konwertuje indeks czarnego pola (0 do 31 włącznie) na punkt (x, y), takie
     * że indeks 0 to (1, 0), indeks 1 to (3, 0), ... indeks 31 to (7, 7).
     *
     * @param index	indeks czarnego pola do konwersji na punkt.
     * @return punkt (x, y) odpowiadający indeksowi czarnego pola lub punkt
     * (-1, -1), jeśli indeks nie mieści się w zakresie 0 - 31 (włącznie).
     * @see {@link #toIndex(int, int)}, {@link #toIndex(Point)}
     */
    public static Point toPoint(int index) {
        int y = index / 4;
        int x = 2 * (index % 4) + (y + 1) % 2;
        return !isValidIndex(index)? new Point(-1, -1) : new Point(x, y);
    }

    /**
     * Konwertuje punkt na indeks czarnego pola na planszy do gry w warcaby, tak
     * że (1, 0) to indeks 0, (3, 0) to indeks 1, ... (7, 7) to indeks 31.
     *
     * @param x	współrzędna x na planszy (od 0 do 7 włącznie).
     * @param y	współrzędna y na planszy (od 0 do 7 włącznie).
     * @return indeks czarnego pola lub -1, jeśli punkt nie jest czarnym polem.
     * @see {@link #toIndex(Point)}, {@link #toPoint(int)}
     */
    public static int toIndex(int x, int y) {

        // Invalid (x, y) (i.e. not in board, or white tile)
        if (!isValidPoint(new Point(x, y))) {
            return -1;
        }

        return y * 4 + x / 2;
    }

    /**
     * Konwertuje punkt na indeks czarnego pola na planszy do gry w warcaby, tak
     * że (1, 0) to indeks 0, (3, 0) to indeks 1, ... (7, 7) to indeks 31.
     *
     * @param p	punkt do konwersji na indeks.
     * @return indeks czarnego pola lub -1, jeśli punkt nie jest czarnym polem.
     * @see {@link #toIndex(int, int)}, {@link #toPoint(int)}
     */
    public static int toIndex(Point p) {
        return (p == null)? -1 : toIndex(p.x, p.y);
    }

    /**
     * Ustawia lub czyści określony bit w wartości docelowej i zwraca zaktualizowaną wartość.
     *
     * @param target	celowa wartość do zaktualizowania.
     * @param bit		bit do zaktualizowania (od 0 do 31 włącznie).
     * @param set		true, aby ustawić bit, false, aby wyczyścić bit.
     * @return zaktualizowana wartość docelowa z ustawionym lub wyczyszczonym bitem.
     * @see {@link #getBit(int, int)}
     */
    public static int setBit(int target, int bit, boolean set) {

        // Nothing to do
        if (bit < 0 || bit > 31) {
            return target;
        }

        // Set the bit
        if (set) {
            target |= (1 << bit);
        }

        // Clear the bit
        else {
            target &= (~(1 << bit));
        }

        return target;
    }

    /**
     * Pobiera stan bitu i określa, czy jest ustawiony (1), czy nie (0).
     *
     * @param target	docelowa wartość do pobrania bitu.
     * @param bit		bit do pobrania (od 0 do 31 włącznie).
     * @return 1, jeśli i tylko jeśli określony bit jest ustawiony, 0 w przeciwnym razie.
     * @see {@link #setBit(int, int, boolean)}
     */
    public static int getBit(int target, int bit) {

        // Out of range
        if (bit < 0 || bit > 31) {
            return 0;
        }

        return (target & (1 << bit)) != 0? 1 : 0;
    }

    /**
     * Gets the middle point on the checker board between two points.
     *
     * @param p1	the first point of a black tile on the checker board.
     * @param p2	the second point of a black tile on the checker board.
     * @return the middle point between two points or (-1, -1) if the points
     * are not on the board, are not distance 2 from each other in x and y,
     * or are on a white tile.
     * @see {@link #middle(int, int)}, {@link #middle(int, int, int, int)}
     */
    public static Point middle(Point p1, Point p2) {

        // A point isn't initialized
        if (p1 == null || p2 == null) {
            return new Point(-1, -1);
        }

        return middle(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * Gets the middle point on the checker board between two points.
     *
     * @param index1	the index of the first point (from 0 to 31 inclusive).
     * @param index2	the index of the second point (from 0 to 31 inclusive).
     * @return the middle point between two points or (-1, -1) if the points
     * are not on the board, are not distance 2 from each other in x and y,
     * or are on a white tile.
     * @see {@link #middle(Point, Point)}, {@link #middle(int, int, int, int)}
     */
    public static Point middle(int index1, int index2) {
        return middle(toPoint(index1), toPoint(index2));
    }

    /**
     * Gets the middle point on the checker board between two points.
     *
     * @param x1	the x-coordinate of the first point.
     * @param y1	the y-coordinate of the first point.
     * @param x2	the x-coordinate of the second point.
     * @param y2	the y-coordinate of the second point.
     * @return the middle point between two points or (-1, -1) if the points
     * are not on the board, are not distance 2 from each other in x and y,
     * or are on a white tile.
     * @see {@link #middle(int, int)}, {@link #middle(Point, Point)}
     */
    public static Point middle(int x1, int y1, int x2, int y2) {

        // Check coordinates
        int dx = x2 - x1, dy = y2 - y1;
        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || // Not in the board
                x1 > 7 || y1 > 7 || x2 > 7 || y2 > 7) {
            return new Point(-1, -1);
        } else if (x1 % 2 == y1 % 2 || x2 % 2 == y2 % 2) { // white tile
            return new Point(-1, -1);
        } else if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) != 2) {
            return new Point(-1, -1);
        }

        return new Point(x1 + dx / 2, y1 + dy / 2);
    }

    /**
     * Checks if an index corresponds to a black tile on the checker board.
     *
     * @param testIndex	the index to check.
     * @return true if and only if the index is between 0 and 31 inclusive.
     */
    public static boolean isValidIndex(int testIndex) {
        return testIndex >= 0 && testIndex < 32;
    }

    /**
     * Checks if a point corresponds to a black tile on the checker board.
     *
     * @param testPoint	the point to check.
     * @return true if and only if the point is on the board, specifically on
     * a black tile.
     */
    public static boolean isValidPoint(Point testPoint) {

        if (testPoint == null) {
            return false;
        }

        // Check that it is on the board
        final int x = testPoint.x, y = testPoint.y;
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            return false;
        }

        // Check that it is on a black tile
        if (x % 2 == y % 2) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the specified ID is for a black checker.
     *
     * @param id	the ID to check.
     * @return true if the ID corresponds to a {@link #BLACK_CHECKER} or
     * a {@link #BLACK_KING} checker.
     */
    public static boolean isBlackChecker(int id) {
        return id == Plansza.BLACK_CHECKER || id == Plansza.BLACK_KING;
    }

    /**
     * Checks if the specified ID is for a white checker.
     *
     * @param id	the ID to check.
     * @return true if the ID corresponds to a {@link #WHITE_CHECKER} or
     * a {@link #WHITE_KING} checker.
     */
    public static boolean isWhiteChecker(int id) {
        return id == Plansza.WHITE_CHECKER || id == Plansza.WHITE_KING;
    }

    /**
     * Checks if the specified ID is for a king checker.
     *
     * @param id	the ID to check.
     * @return true if the ID corresponds to a {@link #BLACK_KING} checker or
     * a {@link #WHITE_KING} checker.
     */
    public static boolean isKingChecker(int id) {
        return id == Plansza.BLACK_KING || id == Plansza.WHITE_KING;
    }

    @Override
    public String toString() {
        String obj = getClass().getName() + "[";
        for (int i = 0; i < 31; i ++) {
            obj += get(i) + ", ";
        }
        obj += get(31);

        return obj + "]";
    }

}
