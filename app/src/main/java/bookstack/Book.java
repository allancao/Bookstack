package bookstack;

import android.util.Log;

/**
 * Created by davintwong on 3/18/15.
 */
public class Book {
    private int id;
    private String detailPageUrl;
    private String smallImage;
    private String asin;
    private String isbn;
    private String title;
    private String author;
    private int reco;
    private int percent;

    public Book(){}

    public Book(String title, String author, String smallImage, int reco) {
        super();
        this.title = title;
        this.author = author;
        this.smallImage = smallImage;
        this.reco = reco;
//        Log.d(smallImage2, "smallImage2");
//        Log.d(smallImage, "smallImage");
        // total force
    }

    //getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDetailPageUrl() {
        return detailPageUrl;
    }

    public void setDetailPageUrl(String detailPageUrl) {
        this.detailPageUrl = detailPageUrl;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        String ret = "Book [" +
                "id=" + this.id + "," +
                "detailPageUrl=" + this.detailPageUrl + "," +
                "smallImage=" + this.smallImage + "," +
                "asin=" + this.asin + "," +
                "isbn=" + this.isbn + "," +
                "title=" + this.title + "," +
                "author=" + this.author + "]";
        return ret;
    }

    public int getPercent() {
        return percent;
    }

    public int getReco() {
        return reco;
    }


    public void setPercent(int percent) {
        this.percent = percent;
    }

    public void setReco(int reco) {
        this.reco = reco;
    }
}
