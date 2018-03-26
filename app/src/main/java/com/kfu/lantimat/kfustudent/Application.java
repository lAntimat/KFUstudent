package com.kfu.lantimat.kfustudent;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kfu.lantimat.kfustudent.Feeds.Repository;
import com.loopj.android.http.PersistentCookieStore;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by User on 008 08.09.17.
 */

public class Application extends MultiDexApplication {

    private FirebaseAnalytics mFirebaseAnalytics;

        @Override
        public void onCreate() {
            super.onCreate();

            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            JodaTimeAndroid.init(this);
            PersistentCookieStore myCookieStore = new PersistentCookieStore(this); /**Обязательно один раз нужно задать CookieStore*/
            KFURestClient.setCookieStore(myCookieStore);
            //FirebaseCrash.report(new Exception("My first Android non-fatal error"));
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.setFirestoreSettings(settings);

            Repository.setContext(getApplicationContext());
        }
}
