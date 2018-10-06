package me.najmsheikh.charitymarket.ui.sellerfragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.ImageEngine;
import me.najmsheikh.charitymarket.R;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProductInfoFragment extends Fragment {

    static final int IMAGE_CODE = 10;
    static final int PERMISSION_REQUEST_READ_EXTERNAL = 50;

    @BindView(R.id.tv_image_notif)
    TextView imageNotif;
    @BindView(R.id.iv_image)
    ImageView imageView;

    private View rootView;
    private List<Uri> images;

    public ProductInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_product_info, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK) {
            images = Matisse.obtainResult(data);

            imageNotif.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(images.get(0))
                    .into(imageView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_EXTERNAL: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPhotosClicked();
                } else {
                    Snackbar.make(rootView, "Unable to view images without permission!",
                            Snackbar.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @OnClick(R.id.bt_photos)
    public void onPhotosClicked() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    PERMISSION_REQUEST_READ_EXTERNAL);
        } else {
            Matisse.from(this)
                    .choose(MimeType.ofImage())
                    .maxSelectable(1)
                    .imageEngine(new EdgePicassoEngine())
                    .forResult(IMAGE_CODE);
        }
    }

    public class EdgePicassoEngine implements ImageEngine {
        public void loadThumbnail(Context context, int resize, Drawable placeholder,
                ImageView imageView, Uri uri) {
            Picasso.get().load(uri).placeholder(placeholder)
                    .resize(resize, resize)
                    .centerCrop()
                    .into(imageView);
        }

        @Override
        public void loadGifThumbnail(Context context, int resize, Drawable placeholder,
                ImageView imageView,
                Uri uri) {
            loadThumbnail(context, resize, placeholder, imageView, uri);
        }

        @Override
        public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView,
                Uri uri) {
            Picasso.get().load(uri).resize(resizeX, resizeY).priority(Picasso.Priority.HIGH)
                    .centerInside().into(imageView);
        }

        @Override
        public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView,
                Uri uri) {
            loadImage(context, resizeX, resizeY, imageView, uri);
        }

        @Override
        public boolean supportAnimatedGif() {
            return false;
        }
    }
}
