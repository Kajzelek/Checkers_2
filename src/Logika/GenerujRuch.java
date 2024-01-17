package Logika;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import Model.Plansza;
public class GenerujRuch{

    /**
     * Gets a list of move end-points for a given start index.
     *
     * @param plansza the board to look for available moves.
     * @param start   the center index to look for moves around.
     * @return the list of points such that the start to a given point
     * represents a move available.
     */
    public static List<Point> getMoves(Plansza plansza, Point start) {
        return getMoves(plansza, Plansza.toIndex(start));
    }

    /**
     * Gets a list of move end-points for a given start index.
     *
     * @param board			the board to look for available moves.
     * @param startIndex	the center index to look for moves around.
     * @return the list of points such that the start to a given point
     * represents a move available.
     * @see {@link #getMoves(Plansza, Point)}
     */
    public static List<Point> getMoves(Plansza board, int startIndex) {

        // Trivial cases
        List<Point> endPoints = new ArrayList<>();
        if (board == null || !Plansza.isValidIndex(startIndex)) {
            return endPoints;
        }

        // Determine possible points
        int id = board.get(startIndex);
        Point p = Plansza.toPoint(startIndex);
        addPoints(endPoints, p, id, 1);

        // Remove invalid points
        for (int i = 0; i < endPoints.size(); i ++) {
            Point end = endPoints.get(i);
            if (board.get(end.x, end.y) != Plansza.EMPTY) {
                endPoints.remove(i --);
            }
        }

        return endPoints;
    }

    /**
     * Gets a list of skip end-points for a given starting point.
     *
     * @param plansza the board to look for available skips.
     * @param start   the center index to look for skips around.
     * @return the list of points such that the start to a given point
     * represents a skip available.
     * @see {@link #getSkips(Plansza, int)}
     */
    public static List<Point> getSkips(Plansza plansza, Point start) {
        return getSkips(plansza, Plansza.toIndex(start));
    }

    /**
     * Gets a list of skip end-points for a given start index.
     *
     * @param plansza    the board to look for available skips.
     * @param startIndex the center index to look for skips around.
     * @return the list of points such that the start to a given point
     * represents a skip available.
     * @see {@link #getSkips(Plansza, Point)}
     */
    public static List<Point> getSkips(Plansza plansza, int startIndex) {

        // Trivial cases
        List<Point> endPoints = new ArrayList<>();
        if (plansza == null || !Plansza.isValidIndex(startIndex)) {
            return endPoints;
        }

        // Determine possible points
        int id = plansza.get(startIndex);
        Point p = Plansza.toPoint(startIndex);
        addPoints(endPoints, p, id, 2);

        // Remove invalid points
        for (int i = 0; i < endPoints.size(); i ++) {

            // Check that the skip is valid
            Point end = endPoints.get(i);
            if (!isValidSkip(plansza, startIndex, Plansza.toIndex(end))) {
                endPoints.remove(i --);
            }
        }

        return endPoints;
    }

    /**
     * Checks if a skip is valid.
     *
     * @param plansza    the board to check against.
     * @param startIndex the start index of the skip.
     * @param endIndex   the end index of the skip.
     * @return true if and only if the skip can be performed.
     */
    public static boolean isValidSkip(Plansza plansza,
                                      int startIndex, int endIndex) {

        if (plansza == null) {
            return false;
        }

        // Check that end is empty
        if (plansza.get(endIndex) != Plansza.EMPTY) {
            return false;
        }

        // Check that middle is enemy
        int id = plansza.get(startIndex);
        int midID = plansza.get(Plansza.toIndex(Plansza.middle(startIndex, endIndex)));
        if (id == Plansza.INVALID || id == Plansza.EMPTY) {
            return false;
        } else if (midID == Plansza.INVALID || midID == Plansza.EMPTY) {
            return false;
        } else if (Plansza.isBlackChecker(midID) ^ Plansza.isWhiteChecker(id)) {
            return false;
        }

        return true;
    }

    /**
     * Adds points that could potentially result in moves/skips.
     *
     * @param points	the list of points to add to.
     * @param p			the center point.
     * @param id		the ID at the center point.
     * @param delta		the amount to add/subtract.
     */
    public static void addPoints(List<Point> points, Point p, int id, int delta) {

        // Add points moving down
        boolean isKing = Plansza.isKingChecker(id);
        if (isKing || id == Plansza.BLACK_CHECKER) {
            points.add(new Point(p.x + delta, p.y + delta));
            points.add(new Point(p.x - delta, p.y + delta));
        }

        // Add points moving up
        if (isKing || id == Plansza.WHITE_CHECKER) {
            points.add(new Point(p.x + delta, p.y - delta));
            points.add(new Point(p.x - delta, p.y - delta));
        }
    }

}