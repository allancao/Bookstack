package bookstack;

import java.util.Date;

/**
 * Created by davintwong on 3/18/15.
 */
public class ReadPeriod {
    private int id;
    private Integer bookId;
    private Date start;
    private Date end;
    private Integer percent;
    private Integer startForce;
    private Integer endForce;

    public ReadPeriod(){}

    public ReadPeriod(Integer bookId,
            Date start,
            Date end,
            Integer percent,
            Integer startForce,
            Integer endForce) {
        super();
        this.bookId = bookId;
        this.start = start;
        this.end = end;
        this.percent = percent;
        this.startForce = startForce;
        this.endForce = endForce;

    }

}
