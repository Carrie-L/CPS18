package com.adsale.ChinaPlas.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.MyExhibitorAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.data.OnIntentListener;
import com.adsale.ChinaPlas.databinding.ActivityMyExhibitorBinding;
import com.adsale.ChinaPlas.ui.view.HelpView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.NetWorkHelper;
import com.adsale.ChinaPlas.utils.RecyclerItemDecoration;
import com.adsale.ChinaPlas.utils.RecyclerViewScrollTo;
import com.adsale.ChinaPlas.viewmodel.MyExhibitorViewModel;

public class MyExhibitorActivity extends BaseActivity implements OnIntentListener {

    private MyExhibitorViewModel viewModel;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ActivityMyExhibitorBinding binding;
    private MyExhibitorAdapter adapter;
    private HelpView helpDialog;

    @Override
    protected void initView() {
        binding = ActivityMyExhibitorBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setAty(this);
        viewModel = new MyExhibitorViewModel(getApplicationContext());
        binding.setModel(viewModel);
        binding.executePendingBindings();
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new RecyclerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));
    }

    @Override
    protected void initData() {
        recyclerView = binding.myExhibitorRv;
        initRecyclerView();
        RecyclerViewScrollTo mRVScrollTo = new RecyclerViewScrollTo(layoutManager, recyclerView);
        viewModel.start(binding.sideLetter, mRVScrollTo);

        adapter = new MyExhibitorAdapter(viewModel.mExhibitors, this);
        recyclerView.setAdapter(adapter);

        if (HelpView.isFirstShow(HelpView.HELP_PAGE_MY_EXHIBITOR)) {
            showHelpPage();
            App.mSP_HP.edit().putInt("HELP_PAGE_" + HelpView.HELP_PAGE_MY_EXHIBITOR, HelpView.HELP_PAGE_MY_EXHIBITOR).apply();
        }
    }

    private void showHelpPage() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag("Dialog");
        if (fragment != null) {
            ft.remove(fragment);
        }
        ft.addToBackStack(null);

        helpDialog = HelpView.newInstance(HelpView.HELP_PAGE_MY_EXHIBITOR);
        helpDialog.show(ft, "Dialog");
    }

    public void onSync() {
        if (AppUtil.isLogin()) {
            viewModel.setAdapter(adapter);
            viewModel.sync();
        } else {
            AppUtil.showAlertDialog(this, R.string.login_first_sync, R.string.login_text4, R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
//                    Intent intent = new Intent(MyExhibitorActivity.this, LoginActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("from", "MyExhibitor");
//                    startActivity(intent);
//                    overridePendingTransPad();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra(Constant.WEB_URL, String.format(NetWorkHelper.MY_CHINAPLAS_URL,AppUtil.getLanguageUrlType()));
                    intent.putExtra(Constant.TITLE,getString(R.string.title_login));
                    startActivity(intent);
                    overridePendingTransPad();

                }
            });
        }
    }

    public void onHelpPage() {
        showHelpPage();
    }

    public void onHallMap() {
        intent(FloorDistributeActivity.class, getString(R.string.my_exhibt_on_map));
    }

    @Override
    public <T> void onIntent(T entity, Class toCls) {
        Intent intent = new Intent(this, ExhibitorDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constant.COMPANY_ID, ((Exhibitor) entity).getCompanyID());
        intent.putExtra("from", "myExhibitor");
        startActivity(intent);
        overridePendingTransPad();
    }
}
