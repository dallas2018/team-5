package me.najmsheikh.charitymarket.ui;

import android.arch.lifecycle.ViewModel;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity<V extends ViewModel> extends AppCompatActivity {

    protected final String TAG = getClass().getSimpleName();
}
