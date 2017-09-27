package tm.fantom.superdealtt;

import javax.inject.Singleton;

import dagger.Component;
import tm.fantom.superdealtt.ui.MainActivity;
import tm.fantom.superdealtt.ui.MainFragment;
import tm.fantom.superdealtt.ui.ReposFragment;

/**
 * Created by fantom on 26-Sep-17.
 */

@Singleton
@Component(modules = {SDModule.class, ApiModule.class})
public interface SDComponent {

    void inject(MainFragment fragment);
    void inject(ReposFragment fragment);
}
