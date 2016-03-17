package eu.depa.flang;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScreenSlidePageFragment extends Fragment {

    public int pos;

    public ScreenSlidePageFragment(int pPos) {
        this.pos = pPos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int[] fragments = {R.layout.intro_1,
                R.layout.intro_2,
                R.layout.intro_3};
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                fragments[pos], container, false);

        return rootView;
    }
}