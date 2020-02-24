package tk.twilightlemon.lemonapp.Fragments;
/*
 *
 *                    此页面尚未开启
 * */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import tk.twilightlemon.lemonapp.R;

//layout/third_fragment_layout.xml的交互逻辑
public class ThirdFragment extends Fragment {
    public static Fragment newInstance() {
        ThirdFragment fragment = new ThirdFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.third_fragment_layout, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
