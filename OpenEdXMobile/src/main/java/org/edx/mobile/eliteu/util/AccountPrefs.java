package org.edx.mobile.eliteu.util;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.user.Account;

import javax.inject.Singleton;

@Singleton
public class AccountPrefs {

    public static final String ACCOUNT = "account";

    @NonNull
    private final PrefManager pref;

    @NonNull
    private final Gson gson = new GsonBuilder().create();

    @Inject
    public AccountPrefs(@NonNull Context context) {
        pref = new PrefManager(context, PrefManager.Pref.LOGIN);
    }

    public void storeAccount(Account account) {
        pref.put(ACCOUNT, gson.toJson(account));
    }

    @Nullable
    public Account getAccount() {
        final String json = pref.getString(ACCOUNT);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, Account.class);
    }

}
