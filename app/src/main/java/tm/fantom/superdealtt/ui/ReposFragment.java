package tm.fantom.superdealtt.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import tm.fantom.superdealtt.R;
import tm.fantom.superdealtt.SuperDealTTApp;
import tm.fantom.superdealtt.api.ApiService;

/**
 * Created by fantom on 27-Sep-17.
 */

public class ReposFragment extends Fragment {
    private static final String KEY_REPOS_NAME = "repos_name";

    @Inject ApiService apiService;
    @BindView(R.id.tvLabel) TextView tvLabel;
    @BindView(R.id.imageView) ImageView imageView;
    @OnClick(R.id.imageView) void clicked(){
        getActivity().onBackPressed();
    };
    @BindView(R.id.rvRepositories) RecyclerView recyclerView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private ReposAdapter adapter;
    private Disposable disposable;
    private LinearLayoutManager layoutManager;
    private long totalCount;

    static ReposFragment newInstance(String name) {
        Bundle arguments = new Bundle();
        arguments.putString(KEY_REPOS_NAME, name);
        ReposFragment fragment = new ReposFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    private String getName() {
        return getArguments().getString(KEY_REPOS_NAME);
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        SuperDealTTApp.getComponent(context).inject(this);
        adapter = new ReposAdapter();
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repos, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        tvLabel.setText(getName());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();

                boolean endHasBeenReached = lastVisible +1 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    if(totalItemCount<totalCount){
                        getNextPage(lastVisible);
                        Snackbar.make(recyclerView,getString(R.string.footer_pagination),Snackbar.LENGTH_SHORT).show();
                    } else
                        Snackbar.make(recyclerView,getString(R.string.footer),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        adapter.setItemClickedListener(position -> Toast.makeText(getContext(), "position: " + position,Toast.LENGTH_SHORT).show());
    }

    @Override public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        disposable = apiService.getPublicRepos("org:" + getName(), 1, 50)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reposResponse -> {
                    this.totalCount = reposResponse.getTotalCount();
                    tvLabel.setText(getString(R.string.repos_label, getName(), reposResponse.getTotalCount()));
                    progressBar.setVisibility(View.GONE);
                    adapter.setRepoItems(reposResponse.getItems());

                }, e -> showError(e.getMessage()));
    }

    private void getNextPage(int lastPosition){
        if(!disposable.isDisposed()) disposable.dispose();
        progressBar.setVisibility(View.VISIBLE);
        disposable = apiService.getPublicRepos("org:" + getName(), 1 + (lastPosition+1)/50, 50)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reposResponse -> {
                    progressBar.setVisibility(View.GONE);
                    adapter.appendRepoItems(reposResponse.getItems());

                }, e -> showError(e.getMessage()));
    }

    @Override public void onPause() {
        super.onPause();
        disposable.dispose();
    }

    private void showError(String errorText){
        Timber.e(errorText);
        Snackbar.make(recyclerView,errorText,Snackbar.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }
}
