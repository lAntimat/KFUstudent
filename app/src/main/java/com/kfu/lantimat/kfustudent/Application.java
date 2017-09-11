package com.kfu.lantimat.kfustudent;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.loopj.android.http.PersistentCookieStore;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by User on 008 08.09.17.
 */

public class Application extends android.app.Application {

    private FirebaseAnalytics mFirebaseAnalytics;

        @Override
        public void onCreate() {
            super.onCreate();

            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            JodaTimeAndroid.init(this);
            PersistentCookieStore myCookieStore = new PersistentCookieStore(this); /**Обязательно один раз нужно задать CookieStore*/
            KFURestClient.setCookieStore(myCookieStore);
        }
}
