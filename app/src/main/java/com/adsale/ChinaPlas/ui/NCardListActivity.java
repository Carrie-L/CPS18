package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.databinding.ActivityNcardListBinding;
import com.adsale.ChinaPlas.helper.OnHelpCallback;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.viewmodel.NCardViewModel;

public class NCardListActivity extends BaseActivity implements OnHelpCallback {

    private NCardViewModel viewModel;
    private ActivityNcardListBinding binding;

    @Override
    protected void initView() {
        barTitle.set(getString(R.string.title_all_name_card));
        binding = ActivityNcardListBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        viewModel = new NCardViewModel(getApplicationContext());
        binding.setViewModel(viewModel);
        viewModel.setOnHelpCallback(this);
    }

    @Override
    protected void initData() {
        if (HelpView.isFirstShow(HelpView.HELP_PAGE_NAMECARD_LIST)) {
            show();
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_NAMECARD_LIST, HelpView.HELP_PAGE_NAMECARD_LIST).apply();
        }
        viewModel.onListStart();

        EditText etFilter = binding.etSearch;
        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    viewModel.resetList();
                } else {
                    viewModel.search(s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void show() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        HelpView helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_NAMECARD_LIST);
        helpDialog.show(ft, "Dialog");
    }
}
