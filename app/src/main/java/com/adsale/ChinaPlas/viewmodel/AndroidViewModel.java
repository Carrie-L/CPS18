package com.adsale.ChinaPlas.viewmodel;

import android.app.Application;

/**
 * Application context aware {@link ViewModel}.
 * <p>
 * Subclasses must have a constructor which accepts {@link Application} as the only parameter.
 * <p>
 */

public class AndroidViewModel extends ViewModel {

    private Application mApplication;

    public AndroidViewModel(Application mApplication) {
        this.mApplication = mApplication;
    }

    @SuppressWarnings("TypeParameterUnusedInFormals")
    public <T extends Application> T getApplication(){
        return (T) mApplication;
    }
}
