package me.najmsheikh.charitymarket.data;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import me.najmsheikh.charitymarket.R;

import java.util.ArrayList;
import java.util.List;

public class ListingsAdapter extends RecyclerView.Adapter<ListingsAdapter.ListingViewHolder> {

    private OnListingClickListener listener;
    private List<Listing> listings;

    public interface OnListingClickListener {
        void onListingClicked(Listing listing);
    }

    public ListingsAdapter(OnListingClickListener listingClickListener) {
        this.listener = listingClickListener;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View listItem = inflater.inflate(R.layout.item_listing, viewGroup, false);
        return new ListingViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int i) {
        holder.bind(listings.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return listings != null ? listings.size() : 0;
    }

    public void addListing(@NonNull Listing listing) {
        if (listings == null) {
            listings = new ArrayList<>();
        }

        listings.add(listing);
        notifyDataSetChanged();
    }

    public void addListings(@NonNull List<Listing> listings) {
        this.listings = listings;
        notifyDataSetChanged();
    }

    static class ListingViewHolder extends RecyclerView.ViewHolder {

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(final Listing listing, final OnListingClickListener listener) {
            ImageView imageView = itemView.findViewById(R.id.iv_image);
            TextView titleView = itemView.findViewById(R.id.tv_title);

            titleView.setText(listing.getTitle());
            Picasso.get()
                    .load(listing.getImage())
                    .into(imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onListingClicked(listing);
                }
            });
        }
    }
}
