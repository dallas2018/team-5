package me.najmsheikh.charitymarket.ui;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import me.najmsheikh.charitymarket.data.Listing;

import java.util.List;

public class MarketViewModel extends ViewModel {

    private MutableLiveData<List<Listing>> listings;

    public MutableLiveData<List<Listing>> getListings() {
        if (listings == null) {
            listings = new MutableLiveData<>();
            loadListings();
        }
        return listings;
    }

    private void loadListings() {
        Query query = FirebaseFirestore.getInstance().collection("listings");
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                listings.postValue(queryDocumentSnapshots.toObjects(Listing.class));
            }
        });
    }
}
