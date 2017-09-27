package tm.fantom.superdealtt;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

/**
 * Created by fantom on 26-Sep-17.
 */

public class SuperDealTTApp extends Application {

    private SDComponent sdComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        sdComponent = DaggerSDComponent.builder()
                .sDModule(new SDModule(this))
                .apiModule(new ApiModule())
                .build();

    }

    public static SDComponent getComponent(Context context) {
        return ((SuperDealTTApp) context.getApplicationContext()).sdComponent;
    }
}
