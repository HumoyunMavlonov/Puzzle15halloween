package uz.gita.puzzle15halloween.app;

import android.app.Application;
import uz.gita.puzzle15halloween.Preference.Pref;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Pref.init(this);
    }
}
