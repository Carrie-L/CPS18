package com.adsale.ChinaPlas.viewmodel;

/**
 * Created by Carrie on 2018/4/12.
 */

public abstract class ViewModel {

   /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     * <p>
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    @SuppressWarnings("WeakerAccess")
    protected void onCleared() {
    }
}
