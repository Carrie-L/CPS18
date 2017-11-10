package com.adsale.ChinaPlas.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.adsale.ChinaPlas.App;
import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.adapter.ExhibitorHistoryAdapter;
import com.adsale.ChinaPlas.adapter.MessageCenterAdapter;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.HistoryExhibitor;
import com.adsale.ChinaPlas.data.ExhibitorRepository;
import com.adsale.ChinaPlas.data.model.MessageCenter;
import com.adsale.ChinaPlas.helper.IntentHelper;
import com.adsale.ChinaPlas.ui.view.CpsRecyclerView;
import com.adsale.ChinaPlas.utils.AppUtil;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.utils.Parser;
import com.adsale.ChinaPlas.utils.RecyclerItemClickListener;
import com.adsale.ChinaPlas.viewmodel.NavViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by new on 2016/10/13.
 * <p>title|baiduTJ</p>
 */
public class CommonListActivity extends BaseActivity {
    private static final String TAG = "CommomListFragment";

    private ArrayList<HistoryExhibitor> histories = new ArrayList<>();
    private ArrayList<MessageCenter.Message> messages = new ArrayList<>();

    private CpsRecyclerView recyclerView;

    private String type;
    private Context mContext;
    private Intent gIntent;
    private String mBaiduTJ;


    public void initView() {
        gIntent = getIntent();
        setTitle(gIntent.getStringExtra("title"));
        mBaiduTJ = gIntent.getStringExtra("baiduTJ");
        getLayoutInflater().inflate(R.layout.activity_common_list, mBaseFrameLayout);

        mContext = getApplicationContext();
        recyclerView = (CpsRecyclerView) findViewById(R.id.commom_recycler_view);
    }

    public void initData() {
        type = gIntent.getStringExtra(Constant.INTENT_COMMON_TYPE);
        setAdapter(type);
        recyclerView.setOnItemClickListener(itemClickListener);
    }

    private void setAdapter(String type) {
        switch (type) {
            case Constant.COM_HISTORY_EXHI:// 历史记录
                histories.clear();
                ExhibitorRepository repository = ExhibitorRepository.getInstance();
                repository.initHistoryDao();
                histories = repository.getAllHistoryExhibitors(0);
                recyclerView.setCpsAdapter(new ExhibitorHistoryAdapter(mContext, histories));
                break;

            case Constant.COM_MSG_CENTER:// 通知中心
                messages.clear();
                MessageCenter msgCenter = Parser.parseJsonFilesDirFile(MessageCenter.class, Constant.TXT_NOTIFICATION);
                ArrayList<MessageCenter.Message> notifications = msgCenter.notifications;

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
                case Constant.COM_HISTORY_EXHI:// 历史记录
                    historyExhibitor(position);
                    break;

                case Constant.COM_MSG_CENTER:// 通知中心
                    messageCenter(position);
                    break;

                default:
                    break;
            }

        }
    };

    // ----------------------------歷史記錄HistoryExhibitor-----------------------------------------
    private void historyExhibitor(int position) {
//        Intent intent = new Intent(mContext, ExhibitorDetailActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("CompanyID", histories.get(position).getCompanyID());
//        startActivity(intent);
//        if (oDeviceType.equals("Pad")) {
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        }
    }

    /**
     * 通知中心
     */
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
