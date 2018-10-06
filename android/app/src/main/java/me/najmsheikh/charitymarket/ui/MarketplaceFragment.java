package me.najmsheikh.charitymarket.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.najmsheikh.charitymarket.R;
import me.najmsheikh.charitymarket.Tools;
import me.najmsheikh.charitymarket.data.AdapterGridShopProductCard;
import me.najmsheikh.charitymarket.data.Listing;
import me.najmsheikh.charitymarket.data.ListingsAdapter;

import java.util.List;

public class MarketplaceFragment extends Fragment
        implements ListingsAdapter.OnListingClickListener {

    private static MarketplaceFragment INSTANCE;

    private View rootView;
    private ListingsAdapter adapter;
    private AdapterGridShopProductCard mAdapter;
    private MarketViewModel viewModel;

    @BindView(R.id.fab_sell)
    FloatingActionButton sellFab;
    @BindView(R.id.rv_listings)
    RecyclerView listingRecycler;

    public MarketplaceFragment() {
        // required constructor
    }

    public static MarketplaceFragment newInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MarketplaceFragment();
        }

        return INSTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_market, container, false);
        ButterKnife.bind(this, rootView);

        viewModel = ViewModelProviders.of(getActivity()).get(MarketViewModel.class);

        mAdapter = new AdapterGridShopProductCard();
        adapter = new ListingsAdapter(this);
        listingRecycler.setAdapter(mAdapter);
        listingRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        listingRecycler.addItemDecoration(
                new SpacingItemDecoration(2, Tools.dpToPx(getContext(), 8), true));
        listingRecycler.setHasFixedSize(true);
        listingRecycler.setNestedScrollingEnabled(false);

        viewModel.getListings().observe(this, new Observer<List<Listing>>() {
            @Override
            public void onChanged(@Nullable List<Listing> listings) {
                mAdapter.setList(listings);
            }
        });

        return rootView;
    }

    @OnClick(R.id.fab_sell)
    public void onNewSaleClick() {
        Intent intent = new Intent(getActivity(), SellActivity.class);
        startActivity(intent);
    }

    @Override
    public void onListingClicked(Listing listing) {

    }

    public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacingPx;
        private boolean includeEdge;

        public SpacingItemDecoration(int spanCount, int spacingPx, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacingPx = spacingPx;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacingPx - column * spacingPx / spanCount;
                outRect.right = (column + 1) * spacingPx / spanCount;

                if (position < spanCount) { // top edge
                    outRect.top = spacingPx;
                }
                outRect.bottom = spacingPx; // item bottom
            } else {
                outRect.left = column * spacingPx / spanCount;
                outRect.right = spacingPx - (column + 1) * spacingPx / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacingPx; // item top
                }
            }
        }
    }
}
