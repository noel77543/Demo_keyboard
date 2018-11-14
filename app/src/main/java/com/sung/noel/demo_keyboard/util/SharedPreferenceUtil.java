package com.sung.noel.demo_keyboard.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SharedPreferenceUtil {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({_NAME_USER_DEFAULT})
    public @interface SharedPreferenceName {
    }

    public final static String _NAME_USER_DEFAULT = "UserDefault";
    private final String _KEY_IS_SPELL = "Spell";


    public SharedPreferenceUtil(Context context, String name) {
        sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //----------

    /***
     *  是否開啟拼字發音
     */
    public boolean isSpell() {
        return sharedPreferences.getBoolean(_KEY_IS_SPELL, false);
    }

    //--------

    /***
     * 設定 開啟拚字發音
     */
    public void setSpell(boolean isSpell) {
        editor.putBoolean(_KEY_IS_SPELL, isSpell).commit();
    }

}
