package tm.fantom.superdealtt.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import tm.fantom.superdealtt.R;
import tm.fantom.superdealtt.SuperDealTTApp;
import tm.fantom.superdealtt.api.ApiService;
import tm.fantom.superdealtt.api.response.OrgResponse;
import tm.fantom.superdealtt.db.OrgItem;
import tm.fantom.superdealtt.util.Connectivity;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

/**
 * Created by fantom on 26-Sep-17.
 */

public class MainFragment extends Fragment {
    private static final String ORG_QUERY = "SELECT * FROM " + OrgItem.TABLE +
            " ORDER BY " + OrgItem.ID + " DESC";

    private Disposable disposable;
    private CompositeDisposable disposables;

    interface Listener{
        void onOrgClicked(String name);
    }

    static MainFragment newInstance() {
        return new MainFragment();
    }

    @Inject BriteDatabase db;
    @Inject ApiService apiService;
    @BindView(R.id.etRepo) EditText etSearch;
    @BindView(R.id.errorHolder) TextView errorHolder;
    @BindView(R.id.rvOrganizations) RecyclerView rvOrgs;
    @BindView(R.id.rvLL) LinearLayout ll;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvBlog) TextView tvBlog;
    @BindView(R.id.tvLocation) TextView tvLocation;
    @BindView(R.id.avatar) SquareImageView avatar;
    @BindView(R.id.org_item) ConstraintLayout orgItem;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private ResultsAdapter adapter;

    private Listener listener;

    @Override public void onAttach(Context context) {
        if (!(getActivity() instanceof Listener)) {
            throw new IllegalStateException("Activity must implement fragment Listener.");
        }
        super.onAttach(context);
        SuperDealTTApp.getComponent(context).inject(this);
        adapter = new ResultsAdapter();
        listener = (Listener) getActivity();
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        rvOrgs.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrgs.setAdapter(adapter);
        adapter.setItemClickedListener(name -> {
            if (isConnected()) listener.onOrgClicked(name);
        });
        errorHolder.setVisibility(View.GONE);
        orgItem.setVisibility(View.GONE);
        RxTextView.textChanges(etSearch)
                .filter(e -> e.length() >= 3)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getOrg);
        RxTextView.textChanges(etSearch)
                .filter(e -> e.length() < 3)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(txt -> ll.setVisibility(View.VISIBLE));
    }

    @Override public void onResume() {
        super.onResume();
        disposables = new CompositeDisposable();
        disposables.add(db.createQuery(OrgItem.TABLE, ORG_QUERY)
                .mapToList(OrgItem.MAPPER)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter));
    }

    @Override public void onPause() {
        super.onPause();
        disposables.dispose();
    }

    private void getOrg(CharSequence text){
        if (!isConnected()){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = apiService
                .getOrg(text.toString())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showResult, e -> showError(getString(R.string.error_404)));
    }

    private void showError(String errorText){
        Timber.e(errorText);
        progressBar.setVisibility(View.GONE);
        orgItem.setVisibility(View.GONE);
        ll.setVisibility(View.VISIBLE);
        errorHolder.setVisibility(View.VISIBLE);
    }

    private void showResult(OrgResponse orgResponse){
        Glide.with(getActivity()).load(orgResponse.getAvatarUrl())
                .crossFade()
                .into(avatar);
        tvName.setText(orgResponse.getName());
        tvLocation.setText(orgResponse.getLocation());
        tvBlog.setText(orgResponse.getBlog());
        progressBar.setVisibility(View.GONE);
        errorHolder.setVisibility(View.GONE);
        ll.setVisibility(View.GONE);
        orgItem.setVisibility(View.VISIBLE);
        orgItem.setClickable(true);
        orgItem.setOnClickListener(v->{
            if (!isConnected()){
                return;
            }
            if(orgResponse.getName() == null) {
                Snackbar.make(etSearch,R.string.error_null, Snackbar.LENGTH_LONG).show();
                return;
            }
            db.insert(OrgItem.TABLE, new OrgItem.Builder()
                            .name(orgResponse.getName())
                            .blog(orgResponse.getBlog())
                            .location(orgResponse.getLocation())
                            .avatarUrl(orgResponse.getAvatarUrl())
                            .build(), CONFLICT_REPLACE);
            listener.onOrgClicked(orgResponse.getName());
        });
    }

    private boolean isConnected() {
        if (!Connectivity.isConnected(getContext())) {
            hideKeyboard();
            Snackbar.make(etSearch, R.string.no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_go_online, view -> {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                        startActivity(intent);
                    })
                    .show();
            return false;
        }
        return true;
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }
}
