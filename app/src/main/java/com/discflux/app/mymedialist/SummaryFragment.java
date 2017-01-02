package com.discflux.app.mymedialist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Phil on 28/12/2016.
 */
public class SummaryFragment extends Fragment {

    TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.content_summary, container, false);

        mTextView = (TextView) rootView.findViewById(R.id.hello_tv);
        mTextView.setText("I know where you were last summer... PHil");

        return rootView;
    }
}
