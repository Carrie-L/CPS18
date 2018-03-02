package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.ApplicationAdapter;
import com.adsale.ChinaPlas.adapter.MessageCenterAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.ApplicationIndustry;
import com.adsale.ChinaPlas.dao.HistoryExhibitor;
import com.adsale.ChinaPlas.data.model.ExhibitorFilter;
import com.adsale.ChinaPlas.data.model.MessageCenter;
import com.adsale.ChinaPlas.databinding.ActivityCommonListBinding;
import com.adsale.ChinaPlas.helper.IntentHelper;
import com.adsale.ChinaPlas.ui.view.CpsRecyclerView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by new on 2016/10/13.
 * <p>title|baiduTJ</p>
 */
public class CommonListActivity extends BaseActivity {
    private static final String TAG = "CommonListActivity";
    public static final int TYPE_MSG_CENTER = 1; /* 消息中心 */
    public static final int TYPE_NEW_TEC_INDUSTRY = 2; /* 新技术产品 - 产品类别 */
    public static final int TYPE_NEW_TEC_APPLICATION = 3; /* 新技术产品 - 应用分类 */

    private ArrayList<HistoryExhibitor> histories = new ArrayList<>();
    private ArrayList<MessageCenter.Message> messages = new ArrayList<>();

    private CpsRecyclerView recyclerView;

    private int type;
    private Context mContext;
    private Intent gIntent;
    private String mBaiduTJ;
    private ArrayList<ExhibitorFilter> productFilters;
    public final ObservableBoolean nodata = new ObservableBoolean();

    public void initView() {
        gIntent = getIntent();
        barTitle.set(gIntent.getStringExtra("title"));
        mBaiduTJ = gIntent.getStringExtra("baiduTJ");
        mContext = getApplicationContext();

        ActivityCommonListBinding binding = ActivityCommonListBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);
        binding.executePendingBindings();
        recyclerView = binding.commomRecyclerView;
    }

    public void initData() {
        type = gIntent.getIntExtra(Constant.INTENT_COMMON_TYPE, 0);
        setAdapter(type);
        recyclerView.setOnItemClickListener(itemClickListener);
    }

    private void setAdapter(int type) {
        switch (type) {
            case TYPE_MSG_CENTER:// 通知中心
                getMessages();
                break;
            case TYPE_NEW_TEC_INDUSTRY: // new tec - 产品类别
                getNewTecProducts();
                break;
            default:
                break;
        }
    }


    RecyclerItemClickListener.OnItemClickListener itemClickListener = new RecyclerItemClickListener.OnItemClickListener() {

        @Override
        public void onItemLongClick(View view, int position) {
        }

        @Override
        public void onItemClick(View view, int position) {
            switch (type) {
                case TYPE_MSG_CENTER:// 通知中心
                    messageCenter(position);
                    break;

                default:
                    break;
            }

        }
    };


    /**
     * ---------------------- 新技术产品 - 产品 ----------------------
     * NewProductAndCategory.csv
     * 字段 Category: A- 机械及仪器；B- 化工及原料；C- 2017年首发技术
     */
    private void getNewTecProducts() {
//        NewTecRepository newTecRepository = NewTecRepository.newInstance();
//        newTecRepository.initDao();
//        newTecRepository.getCategories();
        ArrayList<ApplicationIndustry> list = new ArrayList<>();
        productFilters = new ArrayList<>();
        list.add(new ApplicationIndustry("0", getString(R.string.new_tec_Product_A)));
        list.add(new ApplicationIndustry("1", getString(R.string.new_tec_Product_B)));
        recyclerView.setCpsAdapter(new ApplicationAdapter(list, productFilters));
        nodata.set(productFilters.isEmpty());
    }

    /**
     * ---------------------- 通知中心 ----------------------
     */
    private void getMessages() {
        messages.clear();
        MessageCenter msgCenter = Parser.parseJsonFilesDirFile(MessageCenter.class, Constant.TXT_NOTIFICATION);
        ArrayList<MessageCenter.Message> notifications = msgCenter.getNotifications();
        if (notifications != null && notifications.size() > 0) {
            String currTime = AppUtil.getCurrentTime();
            size = notifications.size();
            for (int i = 0; i < size; i++) {
                message = notifications.get(i);
                LogUtil.i(TAG, "currTime=" + currTime + ",date=" + message.date);
                if (message.date.compareTo(currTime) <= 0) {
                    messages.add(message);
                }
            }
            LogUtil.i(TAG, "messages before=" + messages.toString());

            Collections.sort(messages, new Comparator<MessageCenter.Message>() {
                @Override
                public int compare(MessageCenter.Message lhs, MessageCenter.Message rhs) {
                    if (lhs.date.compareTo(rhs.date) < 0) {
                        return 1;
                    }
                    return -1;
                }
            });
            LogUtil.i(TAG, "messages after+++ " + messages.size());

            recyclerView.setCpsAdapter(new MessageCenterAdapter(messages));
            nodata.set(messages.isEmpty());
        }
    }

    private void messageCenter(int position) {
        message = messages.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message.date).setMessage(message.Message).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        if (!TextUtils.isEmpty(message.function)) {
            builder.setPositiveButton(getString(R.string.dialog_go), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IntentHelper intentHelper = new IntentHelper(CommonListActivity.this);
                    intentHelper.messageIntent(message, false);
                }
            });
        }
        builder.show();
    }


    private MessageCenter.Message message;
    private int size;


}
