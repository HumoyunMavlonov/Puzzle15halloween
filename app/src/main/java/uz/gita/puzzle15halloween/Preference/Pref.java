package uz.gita.puzzle15halloween.Preference;

import android.content.Context;
import android.content.SharedPreferences;

public class Pref {
    static private Pref myPref ;
    public SharedPreferences sharedPreferences ;

    private Pref(Context context) {
        sharedPreferences = context.getSharedPreferences("MyPref" , Context.MODE_PRIVATE) ;
    }

    static public void init(Context context) {
        if (myPref == null) myPref = new Pref(context) ;
    }

    static public SharedPreferences getShared() {
        return myPref.sharedPreferences;
    }

}
