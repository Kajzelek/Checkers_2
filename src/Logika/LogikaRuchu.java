package Logika;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import Model.Gra;
import Model.Plansza;
public class LogikaRuchu {

    /**
     * Determines if the specified move is valid based on the rules of checkers.
     *
     * @param gra        the game to check against.
     * @param startIndex the start index of the move.
     * @param endIndex   the end index of the move.
     * @return true if the move is legal according to the rules of checkers.
     */
    public static boolean isValidMove(Gra gra,
                                      int startIndex, int endIndex) {
        return gra == null? false : isValidMove(gra.getBoard(),
                gra.isP1Turn(), startIndex, endIndex, gra.getSkipIndex());
    }

    /**
     * Determines if the specified move is valid based on the rules of checkers.
     *
     * @param plansza    the current board to check against.
     * @param isP1Turn   the flag indicating if it is player 1's turn.
     * @param startIndex the start index of the move.
     * @param endIndex   the end index of the move.
     * @param skipIndex  the index of the last skip this turn.
     * @return true if the move is legal according to the rules of checkers.
     * @see {@link #isValidMove(Gra, int, int)}
     */
    public static boolean isValidMove(Plansza plansza, boolean isP1Turn,
                                      int startIndex, int endIndex, int skipIndex) {

        // Basic checks
        if (plansza == null || !Plansza.isValidIndex(startIndex) ||
                !Plansza.isValidIndex(endIndex)) {
            return false;
        } else if (startIndex == endIndex) {
            return false;
        } else if (Plansza.isValidIndex(skipIndex) && skipIndex != startIndex) {
            return false;
        }

        // Perform the tests to validate the move
        if (!validateIDs(plansza, isP1Turn, startIndex, endIndex)) {
            return false;
        } else if (!validateDistance(plansza, isP1Turn, startIndex, endIndex)) {
            return false;
        }

        // Passed all tests
        return true;
    }

    /**
     * Validates all ID related values for the start, end, and middle (if the
     * move is a skip).
     *
     * @param plansza    the current board to check against.
     * @param isP1Turn   the flag indicating if it is player 1's turn.
     * @param startIndex the start index of the move.
     * @param endIndex   the end index of the move.
     * @return true if and only if all IDs are valid.
     */
    private static boolean validateIDs(Plansza plansza, boolean isP1Turn,
                                       int startIndex, int endIndex) {

        // Check if end is clear
        if (plansza.get(endIndex) != Plansza.EMPTY) {
            return false;
        }

        // Check if proper ID
        int id = plansza.get(startIndex);
        if ((isP1Turn && !Plansza.isBlackChecker(id))
                || (!isP1Turn && !Plansza.isWhiteChecker(id))) {
            return false;
        }

        // Check the middle
        Point middle = Plansza.middle(startIndex, endIndex);
        int midID = plansza.get(Plansza.toIndex(middle));
        if (midID != Plansza.INVALID && ((!isP1Turn &&
                !Plansza.isBlackChecker(midID)) ||
                (isP1Turn && !Plansza.isWhiteChecker(midID)))) {
            return false;
        }

        // Passed all tests
        return true;
    }

    /**
     * Checks that the move is diagonal and magnitude 1 or 2 in the correct
     * direction. If the magnitude is not 2 (i.e. not a skip), it checks that
     * no skips are available by other checkers of the same player.
     *
     * @param plansza    the current board to check against.
     * @param isP1Turn   the flag indicating if it is player 1's turn.
     * @param startIndex the start index of the move.
     * @param endIndex   the end index of the move.
     * @return true if and only if the move distance is valid.
     */
    private static boolean validateDistance(Plansza plansza, boolean isP1Turn,
                                            int startIndex, int endIndex) {

        // Check that it was a diagonal move
        Point start = Plansza.toPoint(startIndex);
        Point end = Plansza.toPoint(endIndex);
        int dx = end.x - start.x;
        int dy = end.y - start.y;
        if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) > 2 || dx == 0) {
            return false;
        }

        // Check that it was in the right direction
        int id = plansza.get(startIndex);
        if ((id == Plansza.WHITE_CHECKER && dy > 0) ||
                (id == Plansza.BLACK_CHECKER && dy < 0)) {
            return false;
        }

        // Check that if this is not a skip, there are none available
        Point middle = Plansza.middle(startIndex, endIndex);
        int midID = plansza.get(Plansza.toIndex(middle));
        if (midID < 0) {

            // Get the correct checkers
            List<Point> checkers;
            if (isP1Turn) {
                checkers = plansza.find(Plansza.BLACK_CHECKER);
                checkers.addAll(plansza.find(Plansza.BLACK_KING));
            } else {
                checkers = plansza.find(Plansza.WHITE_CHECKER);
                checkers.addAll(plansza.find(Plansza.WHITE_KING));
            }

            // Check if any of them have a skip available
            for (Point p : checkers) {
                int index = Plansza.toIndex(p);
                if (!GenerujRuch.getSkips(plansza, index).isEmpty()) {
                    return false;
                }
            }
        }

        // Passed all tests
        return true;
    }

    /**
     * Checks if the specified checker is safe (i.e. the opponent cannot skip
     * the checker).
     *
     * @param plansza the current board state.
     * @param checker the point where the test checker is located at.
     * @return true if and only if the checker at the point is safe.
     */
    public static boolean isSafe(Plansza plansza, Point checker) {

        // Trivial cases
        if (plansza == null || checker == null) {
            return true;
        }
        int index = Plansza.toIndex(checker);
        if (index < 0) {
            return true;
        }
        int id = plansza.get(index);
        if (id == Plansza.EMPTY) {
            return true;
        }

        // Determine if it can be skipped
        boolean isBlack = Plansza.isBlackChecker(id);
        List<Point> check = new ArrayList<>();
        GenerujRuch.addPoints(check, checker, Plansza.BLACK_KING, 1);
        for (Point p : check) {
            int start = Plansza.toIndex(p);
            int tid = plansza.get(start);

            // Nothing here
            if (tid == Plansza.EMPTY || tid == Plansza.INVALID) {
                continue;
            }

            // Check ID
            boolean isWhite = Plansza.isWhiteChecker(tid);
            if (isBlack && !isWhite) {
                continue;
            }

            // Determine if valid skip direction
            int dx = (checker.x - p.x) * 2;
            int dy = (checker.y - p.y) * 2;
            if (!Plansza.isKingChecker(tid) && (isWhite ^ (dy < 0))) {
                continue;
            }
            int endIndex = Plansza.toIndex(new Point(p.x + dx, p.y + dy));
            if (GenerujRuch.isValidSkip(plansza, start, endIndex)) {
                return false;
            }
        }

        return true;
    }
}
