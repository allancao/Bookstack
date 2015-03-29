package bookstack;

import android.content.ContentProvider;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;

import java.util.List;

/**
 * Created by davintwong on 3/29/15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public List<Book> books;

    public ImageAdapter(Context c) {
        mContext = c;

        // get book from db
        MySQLiteHelper db = new MySQLiteHelper(mContext);
        List<Book> db_books = db.getAllBooks();
        books = db_books;
        Log.d(books.toString(), "imageadapter books");
    }

    public int getCount() {
//        return mThumbIds.length;
        return books.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
//        http://developer.android.com/guide/topics/ui/layout/gridview.html
//        ImageView imageView;
//        if (convertView == null) {
//            // if it's not recycled, initialize some attributes
//            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(8, 8, 8, 8);
//        } else {
//            imageView = (ImageView) convertView;
//        }
//
//        imageView.setImageResource(mThumbIds[position]);
//
//        return imageView;




        // http://www.mkyong.com/android/android-gridview-example/
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

//            gridView = new View(mContext);

            // get layout from grid_item.xml
            gridView = inflater.inflate(R.layout.grid_item, null);

            // set value into textview
            TextView textView = (TextView) gridView
                    .findViewById(R.id.grid_item_label);
            textView.setText(books.get(position).getTitle());
            Log.d(books.get(position).getTitle(), "title book");

            // set image based on selected text
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.grid_item_image);

//            imageView.setImageResource(mThumbIds[position]);

            //http://stackoverflow.com/questions/11595665/saving-resources-path-in-sqlite
            Log.d(books.get(position).getSmallImage(), "small image name");
//            int resId = mContext.getResources().getIdentifier("book_heart_icon","drawable",mContext.getPackageName());
            int resId = mContext.getResources().getIdentifier(books.get(position).getSmallImage(),"drawable",mContext.getPackageName());
            imageView.setImageResource(resId);

        } else {
            gridView = (View) convertView;
        }

        return gridView;

    }

    // references to our images
//    private Integer[] mThumbIds = {
//            R.drawable.book_heart_icon,
//    };
}