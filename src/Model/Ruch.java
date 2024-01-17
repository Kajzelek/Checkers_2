package Model;

import java.awt.Point;
public class Ruch {

    /** The weight corresponding to an invalid move. */
    public static final double WEIGHT_INVALID = Double.NEGATIVE_INFINITY;

    /** The start index of the move. */
    private byte startIndex;

    /** The end index of the move. */
    private byte endIndex;

    /** The weight associated with the move. */
    private double weight;

    public Ruch(int startIndex, int endIndex) {
        setStartIndex(startIndex);
        setEndIndex(endIndex);
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = (byte) startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = (byte) endIndex;
    }

    public Point getStart() {
        return Plansza.toPoint(startIndex);
    }

    public void setStart(Point start) {
        setStartIndex(Plansza.toIndex(start));
    }

    public Point getEnd() {
        return Plansza.toPoint(endIndex);
    }

    public void setEnd(Point end) {
        setEndIndex(Plansza.toIndex(end));
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void changeWeight(double delta) {
        this.weight += delta;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[startIndex=" + startIndex + ", "
                + "endIndex=" + endIndex + ", weight=" + weight + "]";
    }
}
