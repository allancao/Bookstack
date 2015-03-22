package bookstack;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bookstack.R;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class WeekFragment extends Fragment {
    public static final String ARG_PLANET_NUMBER = "planet_number";

    public WeekFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.graph, container, false);
        return rootView;
    }

    public String sign() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        SignedRequestsHelper signedRequestsHelper = new SignedRequestsHelper();
        String signed = signedRequestsHelper.sign(UrlParameterHandler.getInstance().buildMapForItemSearch());

        return signed;
    }
}