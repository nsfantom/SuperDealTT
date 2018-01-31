package tm.fantom.superdealtt.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import tm.fantom.superdealtt.R;

public final class MainActivity extends AppCompatActivity implements MainFragment.Listener {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, MainFragment.newInstance())
                    .commit();
        }
    }

    @Override public void onOrgClicked(String name) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                        R.anim.slide_out_right)
                .replace(android.R.id.content, ReposFragment.newInstance(name))
                .addToBackStack(null)
                .commit();
    }
}
