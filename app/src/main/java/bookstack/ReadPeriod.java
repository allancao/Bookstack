package bookstack;

import java.util.Date;

/**
 * Created by davintwong on 3/18/15.
 */
public class ReadPeriod {
    private int id;
    private int bookId;
    private int start;
    private int end;
    private int percent;
    private int startForce;
    private int endForce;

    public ReadPeriod(){}

    public ReadPeriod(int bookId,
            int start,
            int end,
            int percent,
            int startForce,
            int endForce) {
        super();
        this.bookId = bookId;
        this.start = start;
        this.end = end;
        this.percent = percent;
        this.startForce = startForce;
        this.endForce = endForce;

    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getBookId() {
        return bookId;
    }

    public int getPercent() {
        return percent;
    }

    public int getStartForce() {
        return startForce;
    }

    public int getEndForce() {
        return endForce;
    }
}
