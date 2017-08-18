package com.adsale.ChinaPlas.ui;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.News;
import com.adsale.ChinaPlas.dao.NewsLink;
import com.adsale.ChinaPlas.databinding.ActivityNewsDtlBinding;
import com.adsale.ChinaPlas.utils.Constant;
import com.adsale.ChinaPlas.utils.DisplayUtil;
import com.adsale.ChinaPlas.utils.LogUtil;
import com.adsale.ChinaPlas.viewmodel.NewsModel;

import java.util.ArrayList;

/**
 * todo track photo显示和点击
 */
public class NewsDtlActivity extends BaseActivity implements View.OnClickListener {
    public final ObservableField<String> newsTitle = new ObservableField<>();
    public final ObservableField<String> content = new ObservableField<>();
    public final ObservableField<String> logoUrl = new ObservableField<>();
    public final ObservableArrayList<String> links = new ObservableArrayList<>();

    private News news;
    private ActivityNewsDtlBinding binding;

    @Override
    protected void initView() {
        binding = ActivityNewsDtlBinding.inflate(getLayoutInflater(), mBaseFrameLayout, true);
        binding.setView(this);

        Bundle bundle = getIntent().getExtras();
        news = bundle.getParcelable("News");
        LogUtil.i(TAG, "NEWS= " + news.toString());

        barTitle.set(bundle.getString(Constant.TITLE));

    }

    @Override
    protected void initData() {



        newsTitle.set(news.getTitle());
        content.set(news.getDescription());

        NewsModel model = new NewsModel();
        ArrayList<NewsLink> links = model.getLinks(news.getNewsID());

        LinearLayout linkLayout = binding.lyLink;
        int size = links.size();
        LayoutInflater inflater = getLayoutInflater();
        if (!links.isEmpty()) {
            for (NewsLink oNewsLink : links) {
//                strPhoto = oNewsLink.getPhoto();
//                if (!TextUtils.isEmpty(strPhoto)) {
//                    photoUrl = mDownLoadURL + strPhoto;
//                    LogUtil.i(TAG, "photoUrl=" + photoUrl);
//                    imgPath = App.rootDir + "News/" + strPhoto;
//                    LogUtil.i(TAG, "strPhoto=" + strPhoto);
//                }

                String strLink = oNewsLink.getLink();
                if (!TextUtils.isEmpty(strLink)) {
                    View linkView = inflater.inflate(R.layout.view_link, linkLayout, false);
                    TextView txtLink = (TextView) linkView.findViewById(R.id.textView1);
                    txtLink.setText(oNewsLink.getTitle());
                    linkView.setTag(oNewsLink.getLink());
                    linkView.setOnClickListener(this);
                    linkLayout.addView(linkView, getParams());
                }
            }
        }
    }

    public LinearLayout.LayoutParams getParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, DisplayUtil.dip2px(getApplicationContext(), 10));
        return params;
    }



    public void share() {

    }

    @Override
    public void onClick(View v) {
        String url = v.getTag().toString();
//        SystemMethod.trackViewLog(mContext, 190, "Page", newsID, "NewsLink");



        LogUtil.i(TAG,"url="+url);
    }
}
