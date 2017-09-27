package tm.fantom.superdealtt;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tm.fantom.superdealtt.db.DbModule;

/**
 * Created by fantom on 26-Sep-17.
 */
@Module(
        includes = {
                DbModule.class,
        }
)
public final class SDModule {
    private final Application application;

    public SDModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}
