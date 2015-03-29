package bookstack;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import bookstack.R;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ProgressFragment extends Fragment {
    public static final String ARG_PLANET_NUMBER = "planet_number";
    public Context context = null;

    public ProgressFragment(Context context) {
        this.context = context;
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // http://www.appsrox.com/android/tutorials/showcase/8/

        // do not call super. it is for activity.
//        http://stackoverflow.com/questions/19214620/android-imageadapter-with-gridview-in-fragment

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.progress, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.progress);
        gridview.setAdapter(new ImageAdapter(context));

        return rootView;
    }

}