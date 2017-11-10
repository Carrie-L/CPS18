package com.adsale.ChinaPlas.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.base.BaseActivity;
import com.adsale.ChinaPlas.dao.Exhibitor;
import com.adsale.ChinaPlas.ui.view.IconView;

/**
 * 展商名单
 */
public class ExhibitorActivity extends BaseActivity {

    @Override
    protected void initView() {
        getLayoutInflater().inflate(R.layout.activity_exhibitor, mBaseFrameLayout, true);
    }

    @Override
    protected void initData() {
        IconView ivAllExhibitor = (IconView) findViewById(R.id.iv_all_exhibitor);
        IconView ivClassify = (IconView) findViewById(R.id.iv_classify_search);
        IconView ivKeyword = (IconView) findViewById(R.id.iv_keyword_search);

        ivAllExhibitor.setIconText(R.drawable.all_exhibitors, R.string.item_all_exhibitor);
        ivClassify.setIconText(R.drawable.advanced_search, R.string.item_criteria_search);
        ivKeyword.setIconText(R.drawable.keyword_search, R.string.item_keyword_search);

        ivAllExhibitor.clickListener.set(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExhibitorActivity.this, ExhibitorAllListActivity.class);
                startActivity(intent);
            }
        });
        ivClassify.clickListener.set(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ExhibitorActivity.this,ExhibitorAllListActivity.class);
                startActivity(intent);
            }
        });
        ivKeyword.clickListener.set(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(ExhibitorActivity.this,ExhibitorAllListActivity.class);
//                startActivity(intent);
            }
        });
    }
}
