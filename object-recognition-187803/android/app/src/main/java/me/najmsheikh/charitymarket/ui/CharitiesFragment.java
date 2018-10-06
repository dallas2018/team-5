package me.najmsheikh.charitymarket.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import me.najmsheikh.charitymarket.R;

public class CharitiesFragment extends Fragment {

    private View rootView;

    @BindView(R.id.iv_charity1) ImageView charity1;
    @BindView(R.id.iv_charity2) ImageView charity2;
    @BindView(R.id.iv_charity3) ImageView charity3;
    @BindView(R.id.iv_charity4) ImageView charity4;

    public CharitiesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_charities, container, false);
        ButterKnife.bind(this, rootView);

        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/jpmc4g-18.appspot.com/o/redcross.jpg?alt=media&token=9b598a03-ffe6-4489-b3f8-cc1df9fad039")
                .into(charity1);

        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/jpmc4g-18.appspot.com/o/1200px-Feeding_America_logo.svg.png?alt=media&token=529520bd-7aca-497f-9ca6-f9efb8892de0")
                .into(charity2);

        Picasso.get()
                .load("https://firebasestorage.googleapis.com/v0/b/jpmc4g-18.appspot.com/o/boysgirlclub.jpg?alt=media&token=adf877ac-d21c-4a85-9f84-d7764ec4daed")
                .into(charity3);

        Picasso.get()
                .load("https://d11sa1anfvm2xk.cloudfront.net/media/downloads/logos/cw_vertical_black.jpg")
                .into(charity4);

        return rootView;
    }

    @OnClick({ R.id.lyt_parent, R.id.lyt_parent2, R.id.lyt_parent3, R.id.lyt_parent4 })
    public void onClick(View v) {
        v.setBackgroundColor(Color.RED);
    }
}
