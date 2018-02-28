package com.kfu.lantimat.kfustudent.Feeds.KfuFeed;

/**
 * Created by GabdrakhmanovII on 04.09.2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kfu.lantimat.kfustudent.Feeds.Feed;
import com.kfu.lantimat.kfustudent.Feeds.FeedPresenter;
import com.kfu.lantimat.kfustudent.Feeds.FeedView;
import com.kfu.lantimat.kfustudent.Feeds.FeedsRecyclerAdapter;
import com.kfu.lantimat.kfustudent.Feeds.FullFeeds.FullFeedsActivity;
import com.kfu.lantimat.kfustudent.ItemClickSupport;
import com.kfu.lantimat.kfustudent.R;

import java.util.ArrayList;

public class KfuFeedFragment extends Fragment implements KfuNewsMVP.View {

    private final String ARG_PARAM1 = "param1";

    RecyclerView recyclerView;
    KfuNewsRecyclerAdapter adapter;
    ArrayList<KfuNews> ar;
    TextView textView;
    ImageView imageView;
    ProgressBar progressBar;
    KfuNewsMVP.Presenter presenter;
    private SwipeRefreshLayout swipeContainer;


    public KfuFeedFragment() {
        // Required empty public constructor
    }

    public KfuFeedFragment newInstance() {

        KfuFeedFragment fragment = new KfuFeedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, 1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            getArguments().getInt(ARG_PARAM1);
        }

        ar = new ArrayList<>();
        adapter = new KfuNewsRecyclerAdapter(getContext(), ar);

        presenter = new KfuNewsPresenter(this);
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false));
        recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                int visibleCount = Math.abs(firstVisible - layoutManager.findLastVisibleItemPosition());
                int itemCount = recyclerView.getAdapter().getItemCount();

                if ((firstVisible + visibleCount + 1) >= itemCount) {
                    presenter.loadData();
                }
            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                startFeedActivity(ar.get(position).getUrl(), ar.get(position).getImage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_feeds, null);

        textView = v.findViewById(R.id.textView);
        imageView = v.findViewById(R.id.imageView);
        progressBar = v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        initSwipeRefreshLayout(v);


        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        initRecyclerView();

        presenter.loadData();

        return v;
    }

    private void initSwipeRefreshLayout(View v) {
        // Lookup the swipe container view
        swipeContainer = v.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                presenter.refreshData();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    private void emptyPic() {
        if (ar.size() == 0) {
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void showData(ArrayList<KfuNews> ar) {
        this.ar.clear();
        this.ar.addAll(ar);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void startFeedActivity(String url, String img) {
        Intent intent = new Intent(getContext(), FullFeedsActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("img", img);
        startActivity(intent);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void showError(String error) {

    }
}
