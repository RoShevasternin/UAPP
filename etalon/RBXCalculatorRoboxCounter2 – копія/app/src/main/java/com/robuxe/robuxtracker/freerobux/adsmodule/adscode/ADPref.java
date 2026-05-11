package com.robuxe.robuxtracker.freerobux.adsmodule.adscode;

import android.content.Context;
import android.content.SharedPreferences;

import com.robuxe.robuxtracker.freerobux.adsmodule.model.RemoteConfigModel;
import com.google.gson.Gson;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public final class ADPref {
    public static final Companion INSTANCE = new Companion(null);
    private static final String CONFIG_KEY = "AdsDATAConfig";
    private static volatile ADPref instance;
    private static SharedPreferences sharedPreferences;

    public ADPref(Context context, DefaultConstructorMarker defaultConstructorMarker) {
        this(context);
    }

    private ADPref(Context context) {
        SharedPreferences sharedPreferences2 = context.getSharedPreferences("ff_maxskintool", 0);
        Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "getSharedPreferences(...)");
        sharedPreferences = sharedPreferences2;
    }

    public final void saveRemoteConfig(RemoteConfigModel remoteConfigModel) {
        Intrinsics.checkNotNullParameter(remoteConfigModel, "remoteConfigModel");
        SharedPreferences sharedPreferences2 = sharedPreferences;
        if (sharedPreferences2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sharedPreferences");
            sharedPreferences2 = null;
        }
        SharedPreferences.Editor editor = sharedPreferences2.edit();
        Gson gson = new Gson();
        String json = gson.toJson(remoteConfigModel);
        editor.putString(CONFIG_KEY, json);
        editor.apply();
    }

    public final RemoteConfigModel getRemoteConfig() {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences2 = sharedPreferences;
        if (sharedPreferences2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sharedPreferences");
            sharedPreferences2 = null;
        }
        String json = sharedPreferences2.getString(CONFIG_KEY, null);
        return (RemoteConfigModel) gson.fromJson(json, RemoteConfigModel.class);
    }

    public static final class Companion {
        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final ADPref getInstance(Context context) {
            ADPref it;
            Intrinsics.checkNotNullParameter(context, "context");
            ADPref aDPref = ADPref.instance;
            if (aDPref != null) {
                return aDPref;
            }
            synchronized (this) {
                it = ADPref.instance;
                if (it == null) {
                    it = new ADPref(context, null);
                    Companion companion = ADPref.INSTANCE;
                    ADPref.instance = it;
                }
            }
            return it;
        }
    }

}