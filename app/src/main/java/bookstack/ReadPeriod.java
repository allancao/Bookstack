package bookstack;

import java.util.Date;

/**
 * Created by davintwong on 3/18/15.
 */
public class ReadPeriod {
    private int id;
    private int start;
    private int end;
    private int percent;
    private int startForce;
    private int endForce;
    private int bookId;

    public ReadPeriod(){}

    public ReadPeriod(
            int start,
            int end,
            int percent,
            int startForce,
            int endForce,
            int bookId) {
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


    public void setId(int id) {
        this.id = id;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public void setStartForce(int startForce) {
        this.startForce = startForce;
    }

    public void setEndForce(int endForce) {
        this.endForce = endForce;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
}
