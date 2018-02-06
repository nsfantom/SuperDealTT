package tm.fantom.superdealtt.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import tm.fantom.superdealtt.R;
import tm.fantom.superdealtt.SuperDealTTApp;
import tm.fantom.superdealtt.api.ApiService;
import tm.fantom.superdealtt.databinding.FragmentReposBinding;

/**
 * Created by fantom on 27-Sep-17.
 */

public final class ReposFragment extends Fragment {
    private static final String KEY_REPOS_NAME = "repos_name";


    @Inject ApiService apiService;
    private ReposAdapter adapter;
    private Disposable disposable;
    private LinearLayoutManager layoutManager;
    private long totalCount;
    private FragmentReposBinding binding;
    private GestureListener gestureListener;

    static ReposFragment newInstance(String name) {
        Bundle arguments = new Bundle();
        arguments.putString(KEY_REPOS_NAME, name);
        ReposFragment fragment = new ReposFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    interface GestureListener{
        void onAttachForGesture(View view);
    }

    private String getName() {
        return getArguments().getString(KEY_REPOS_NAME);
    }

    @Override public void onAttach(Context context) {
        if (!(getActivity() instanceof GestureListener)) {
            throw new IllegalStateException("Activity must implement fragment Listener.");
        }
        super.onAttach(context);
        SuperDealTTApp.getComponent(context).inject(this);
        adapter = new ReposAdapter();
        gestureListener = (GestureListener)getActivity();
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_repos, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ButterKnife.bind(this, view);
        binding.tvLabel.setText(getName());
        gestureListener.onAttachForGesture(binding.getRoot());
        binding.rvRepositories.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRepositories.setAdapter(adapter);
        layoutManager = LinearLayoutManager.class.cast(binding.rvRepositories.getLayoutManager());
        binding.rvRepositories.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        binding.imageView.setEnabled(true);
        binding.imageView.setClickable(true);
        binding.imageView.setOnClickListener(v->getActivity().onBackPressed());
    }

    @Override public void onResume() {
        super.onResume();
        binding.progressBar.setVisibility(View.VISIBLE);
        disposable = apiService.getPublicRepos("org:" + getName(), 1, 50)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reposResponse -> {
                    this.totalCount = reposResponse.getTotalCount();
                    binding.tvLabel.setText(getString(R.string.repos_label, getName(), reposResponse.getTotalCount()));
                    binding.progressBar.setVisibility(View.GONE);
                    adapter.setRepoItems(reposResponse.getItems());

                }, e -> showError(e.getMessage()));
    }

    private void getNextPage(int lastPosition){
        if(!disposable.isDisposed()) disposable.dispose();
        binding.progressBar.setVisibility(View.VISIBLE);
        disposable = apiService.getPublicRepos("org:" + getName(), 1 + (lastPosition+1)/50, 50)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(reposResponse -> {
                    binding.progressBar.setVisibility(View.GONE);
                    adapter.appendRepoItems(reposResponse.getItems());

                }, e -> showError(e.getMessage()));
    }

    @Override public void onPause() {
        super.onPause();
        disposable.dispose();
    }

    private void showError(String errorText){
        Timber.e(errorText);
        Snackbar.make(binding.rvRepositories,errorText,Snackbar.LENGTH_SHORT).show();
        binding.progressBar.setVisibility(View.GONE);
    }
}
