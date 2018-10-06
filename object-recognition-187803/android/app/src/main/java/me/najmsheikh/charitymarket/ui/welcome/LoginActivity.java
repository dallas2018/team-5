package me.najmsheikh.charitymarket.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import me.najmsheikh.charitymarket.BuildConfig;
import me.najmsheikh.charitymarket.data.User;
import me.najmsheikh.charitymarket.ui.BaseActivity;
import me.najmsheikh.charitymarket.ui.marketplace.MarketActivity;

import java.util.Arrays;

public class LoginActivity extends BaseActivity {

    static final int RC_SIGN_IN = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            goToMarketPlace();
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.FacebookBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

                User user = new User();
                FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fireUser == null || fireUser.getMetadata() == null) return;

                if (fireUser.getMetadata().getCreationTimestamp() == fireUser.getMetadata()
                        .getLastSignInTimestamp()) {
                    user.setName(fireUser.getDisplayName());
                    user.setEmail(fireUser.getEmail());
                    if (fireUser.getPhotoUrl() != null) {
                        user.setPhotoURL(fireUser.getPhotoUrl().toString());
                    }

                    FirebaseFirestore.getInstance().collection("users")
                            .document(fireUser.getUid())
                            .set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    goToMarketPlace();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG,
                                            "Unable to set user data: " + e.getLocalizedMessage());
                                }
                            });
                } else {
                    goToMarketPlace();
                }
            } else {
                Toast.makeText(this, "Login cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void goToMarketPlace() {
        finish();
        startActivity(new Intent(this, MarketActivity.class));
    }
}
