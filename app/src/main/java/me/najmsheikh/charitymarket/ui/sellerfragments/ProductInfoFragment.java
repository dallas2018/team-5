package me.najmsheikh.charitymarket.ui.sellerfragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.picasso.Picasso;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.ImageEngine;
import me.najmsheikh.charitymarket.R;
import me.najmsheikh.charitymarket.Tools;
import me.najmsheikh.charitymarket.data.InfoRequest;
import me.najmsheikh.charitymarket.data.InfoResponse;
import me.najmsheikh.charitymarket.data.ListingInfoClient;
import me.najmsheikh.charitymarket.ui.SellActivity;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

public class ProductInfoFragment extends Fragment {

    static final int IMAGE_CODE = 10;
    static final int PERMISSION_REQUEST_READ_EXTERNAL = 50;

    private String finalLink;

    @BindView(R.id.et_title)
    EditText titleView;
    @BindView(R.id.et_brand)
    EditText brandView;
    @BindView(R.id.et_price)
    EditText priceView;
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

            new ImageCompressionTask().execute(images.get(0));
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

    public class ImageCompressionTask extends AsyncTask<Uri, Integer, byte[]> {
        Bitmap bitmap;

        @Override
        protected byte[] doInBackground(Uri... uris) {
            if (bitmap == null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                            uris[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return Tools.getBytesFromBitmap(bitmap, 75);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            new ImageUploadTask().execute(bytes);
        }
    }

    public class ImageUploadTask extends AsyncTask<byte[], Integer, Void> {

        @Override
        protected Void doInBackground(byte[]... bytes) {
            StorageReference ref = FirebaseStorage.getInstance().getReference();
            ref = ref.child("product/" +
                    UUID.randomUUID() + ".jpg");

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();

            ref.putBytes(bytes[0], metadata).addOnSuccessListener(taskSnapshot -> {
                String url = taskSnapshot.getMetadata().getReference().getPath();
                setLink(url);
                getInfo();
                //Toast.makeText(getActivity(), "this" + url, Toast.LENGTH_LONG).show();

            }).addOnFailureListener(e -> {
                Log.e("TAG", e.getLocalizedMessage());
            });

            return null;
        }
    }

    private void setLink(String link) {
        finalLink = link;
        ((SellActivity) getActivity()).listing.setImage(link);
        //Toast.makeText(getActivity(), "this" + link, Toast.LENGTH_LONG).show();
    }

    private void getInfo() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://0e86d1c6.ngrok.io/")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
                .build();

        InfoRequest request = new InfoRequest();
        request.setFileName(finalLink);

        retrofit.create(ListingInfoClient.class).getInfo(request).enqueue(
                new Callback<InfoResponse>() {
                    @Override
                    public void onResponse(Call<InfoResponse> call,
                            Response<InfoResponse> response) {
                        if (response != null && response.body() != null) {
                            Log.d("NAJM", "reached");
                            setData(response.body());
                        } else {
                            Log.d("NAJM", "not reached");
                        }
                    }

                    @Override
                    public void onFailure(Call<InfoResponse> call, Throwable t) {

                    }
                });
    }

    public void setData(InfoResponse response) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                titleView.setText(response.getTitle());
                brandView.setText(response.getBrand());
                priceView.setText(String.valueOf(response.getPrice()));
            }
        });
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
