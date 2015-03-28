package bookstack;

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

    public Book(){}

    public Book(String title, String author) {
        super();
        this.title = title;
        this.author = author;
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
}
