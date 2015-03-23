package bookstack;

/**
 * Created by davintwong on 3/18/15.
 */
public class ReadPeriod {
    private int id;
    private long start;
    private long end;
    private int percent;
    private int startForce;
    private int endForce;
    private int bookId;

    public ReadPeriod(){}

    public ReadPeriod(
            long start,
            long end,
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

    public long getStart() {
        return start;
    }

    public long getEnd() {
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

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
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
