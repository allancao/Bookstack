// Code From:
// http://hmkcode.com/android-simple-sqlite-database-tutorial/

package bookstack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 22;

    // Database Name
    private static final String DATABASE_NAME = "BookDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_BOOK_TABLE = "CREATE TABLE books ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, "+
                "author TEXT, "+
                "smallImage TEXT, "+
                "reco INT, "+
                "percent INT ) ";

        String CREATE_READPERIOD_TABLE = "CREATE TABLE ReadPeriod ( " +
                "rp_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "start INTEGER, "+
                "end INTEGER, "+
                "percent INTEGER, "+
                "startForce INTEGER, "+
                "endForce INTEGER,"+
                "fk_bookId INTEGER," +
                    " FOREIGN KEY(fk_bookId) REFERENCES books(id) ) ";

        // create books table
        db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_READPERIOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS books");
        db.execSQL("DROP TABLE IF EXISTS ReadPeriod");

        // create fresh books table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------

    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
     */

    // Books table name
    private static final String TABLE_BOOKS = "books";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";

    private static final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_AUTHOR, "smallImage", "reco", "percent"};

    public void addBook(Book book){
        Log.d("addBook", book.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, book.getTitle()); // get title
        values.put(KEY_AUTHOR, book.getAuthor()); // get author
        values.put("smallImage", book.getSmallImage());
        values.put("reco", book.getReco());
        values.put("percent", book.getPercent());

        // 3. insert
        db.insert(TABLE_BOOKS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Book getBook(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_BOOKS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        Book book = new Book();
        book.setId(Integer.parseInt(cursor.getString(0)));
        book.setTitle(cursor.getString(1));
        book.setAuthor(cursor.getString(2));
        book.setSmallImage(cursor.getString(3));
        book.setReco(cursor.getInt(4));
        book.setPercent(cursor.getInt(5));

        Log.d("getBook("+id+")", book.toString());

        // 5. return book
        return book;
    }

    // Get All Books
    public List<Book> getAllBooks(int reco) {
        List<Book> books = new LinkedList<Book>();

        // 1. build the query
        String query = "";

        if (reco == 0) {
            query = "SELECT  * FROM " + TABLE_BOOKS + " WHERE reco = 0";
        }
        else if (reco == 1) {
            query = "SELECT  * FROM " + TABLE_BOOKS + " WHERE reco = 1";
        }

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Book book = null;
        if (cursor.moveToFirst()) {
            do {
                book = new Book();
                book.setId(Integer.parseInt(cursor.getString(0)));
                book.setTitle(cursor.getString(1));
                book.setAuthor(cursor.getString(2));
                book.setSmallImage(cursor.getString(3));
                book.setReco(cursor.getInt(4));
                book.setPercent(cursor.getInt(5));

                // Add book to books
                books.add(book);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", books.toString());

        // return books
        return books;
    }

    // Updating single book
    public int updateBook(Book book) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("title", book.getTitle()); // get title
        values.put("author", book.getAuthor()); // get author

        // 3. updating row
        int i = db.update(TABLE_BOOKS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(book.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single book
    public void deleteBook(Book book) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_BOOKS,
                KEY_ID+" = ?",
                new String[] { String.valueOf(book.getId()) });

        // 3. close
        db.close();

        Log.d("deleteBook", book.toString());

    }

    // READPERIODS

    // readPeriods table name
//    private static final String TABLE_READPERIODS = "readPeriods";

    // Books Table Columns names
//    private static final String RP_KEY_ID = "id";
//    private static final String RP_KEY_BOOKID = "bookId";
//    private static final String RP_KEY_START = "start";

//    private static final String[] RP_COLUMNS = {KEY_ID,KEY_TITLE,KEY_AUTHOR};

    public void addReadPeriod(ReadPeriod rp){
        Log.d("addRP0", "addRP0");
        Log.d("addRP1", Long.toString(rp.getStart()));
        Log.d("addRP2", Long.toString(rp.getEnd()));

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("fk_bookId", rp.getBookId());
        values.put("start", rp.getStart());
        values.put("end", rp.getEnd());
        values.put("percent", rp.getPercent());
        values.put("startForce", rp.getStartForce());
        values.put("endForce", rp.getEndForce());

        // 3. insert
        db.insert("ReadPeriod", // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public List<ReadPeriod> getAllReadPeriod() {
        return getAllReadPeriod(0);
    }

    public List<ReadPeriod> getAllReadPeriod(int fk_bookId) {
        // bookId of 0 means get all books

        List<ReadPeriod> rps = new LinkedList<ReadPeriod>();

        // 1. build the query
        String query = "";

        if (fk_bookId == 0) {
            query = "SELECT  * FROM " + "ReadPeriod";
        }
        else {
            query = "SELECT  * FROM " + "ReadPeriod" + " WHERE fk_bookId = " + Integer.toString(fk_bookId);
        }
        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        ReadPeriod rp = null;
        if (cursor.moveToFirst()) {
            do {
                rp = new ReadPeriod();
                rp.setId(Integer.parseInt(cursor.getString(0)));
                rp.setStart(Long.parseLong(cursor.getString(1)));
                rp.setEnd(Long.parseLong(cursor.getString(2)));
                rp.setPercent(Integer.parseInt(cursor.getString(3)));
                rp.setStartForce(Integer.parseInt(cursor.getString(4)));
                rp.setEndForce(Integer.parseInt(cursor.getString(5)));
                rp.setBookId(Integer.parseInt(cursor.getString(6)));

                // Add book to books
                rps.add(rp);
            } while (cursor.moveToNext());
        }

        Log.d("getAllReadPeriod()", rps.toString());
        Log.d("getAllReadPeriod bookid", Integer.toString(fk_bookId));

        // return books
        return rps;
    }
}