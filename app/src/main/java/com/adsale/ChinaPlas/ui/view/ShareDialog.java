package com.adsale.ChinaPlas.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.databinding.DialoShareBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 分享界面
 *
 * @author Carrie
 * @version 创建时间：2016年6月16日 下午5:51:16
 */
public class ShareDialog extends Dialog {

    private GridView gridView;
    private SimpleAdapter saImageItems;
    private int[] image = {R.drawable.share_wechat, R.drawable.share_wechatmoments, R.drawable.share_sinaweibo, R.drawable.share_qq, R.drawable.share_qzone,
            R.drawable.share_email, R.drawable.share_facebook, R.drawable.share_whatsapp};//,R.drawable.logo_twitter
    private View mView;

    private Context mContext;

    public ShareDialog(Context context) {
        super(context, R.style.transparentDialog);
        mContext = context;
        initView();
        initData();
    }

    private void initView() {
        DialoShareBinding binding = DialoShareBinding.inflate(LayoutInflater.from(mContext), null, false);
        binding.setView(this);
        binding.executePendingBindings();
        gridView = binding.shareGridView;
        mView = binding.getRoot();
    }

    private void initData() {
        String[] name = {mContext.getString(R.string.share_wechat_friends), mContext.getString(R.string.share_wechat_moments), mContext.getString(R.string.share_sinna_weibo),
                mContext.getString(R.string.share_qq), mContext.getString(R.string.share_qq_zone),
                mContext.getString(R.string.share_email), mContext.getString(R.string.share_facebook), mContext.getString(R.string.share_whatsApp)};//, "Twitter"
        List<HashMap<String, Object>> shareList = new ArrayList<>();
        HashMap<String, Object> map;
        for (int i = 0; i < image.length; i++) {
            map = new HashMap<>();
            map.put("ItemImage", image[i]);// 添加图像资源的ID
            map.put("ItemText", name[i]);// 按序号做ItemText
            shareList.add(map);
        }
        saImageItems = new SimpleAdapter(mContext, shareList, R.layout.view_share_item, new String[]{"ItemImage", "ItemText"}, new int[]{
                R.id.imageView1, R.id.textView1});
        gridView.setAdapter(saImageItems);
    }

    public void onCancel() {
        dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
        window.setContentView(mView);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        gridView.setOnItemClickListener(listener);
    }

    public boolean isDialogShowing() {
        return isShowing();
    }
}
