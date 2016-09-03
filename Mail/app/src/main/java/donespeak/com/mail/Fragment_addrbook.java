package donespeak.com.mail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by glorior on 2016/6/18.
 */
public class Fragment_addrbook extends Fragment {
    private View layoutView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        layoutView = inflater.inflate(R.layout.fragment_addrbook, null);
        return layoutView;
    }
}
