package com.adsale.ChinaPlas.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.adsale.ChinaPlas.R;
import com.adsale.ChinaPlas.ui.view.ShareDialog;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.email.Email;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.whatsapp.WhatsApp;

/**
 * 分享对话框，分享功能
 * @author new
 *
 */
public class ShareSDKDialog {
	private static final String TAG = "ShareSDKDialog";
	private Context mContext;
	
	private ShareDialog shareDialog;
	
	private static final int ERROR=0;
	private static final int SUCCESS=1;
	private static final int CANCEL=2;
	protected static final int START_SHARE = 3;

	public boolean isDialogShowing(){
		return shareDialog.isDialogShowing();
	}

	public void dismiss(){
		shareDialog.dismiss();
	}
	

	/**
	 * 
	 * @param context
	 * @param text   分享内容
	 * @param image
	 * @param url
	 */
	public void showDialog(Context context,final String text,final String image,final String url,final String iamgePath) {
		shareDialog = new ShareDialog(context);
		shareDialog.show();
		mContext=context;
			
		shareDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);// 微博，朋友圈，空间，Facebook，twitter, whatsApp

				LogUtil.i(TAG, "item.get(ItemText)=" + item.get("ItemText"));

				//微博：分享图文：text\imagePath
				if (item.get("ItemText").equals(mContext.getString(R.string.share_sinna_weibo))) {
					ShareParams sp = new ShareParams();
					sp.setText(text+" \n"+url); // 分享文本
					if(!TextUtils.isEmpty(iamgePath)){
						sp.setImagePath(iamgePath);
					}
					
					startShare(SinaWeibo.NAME, sp);
				} 
				
				//朋友圈：标题title，内容text(朋友圈不显示此字段)，图片地址imageUrl，网页地址url
				else if (item.get("ItemText").equals(mContext.getString(R.string.share_wechat_moments))) {
					// 2、设置分享内容
					ShareParams sp = new ShareParams();
					sp.setShareType(Platform.SHARE_WEBPAGE); // 非常重要：一定要设置分享属性
					sp.setTitle(text);
					sp.setTitleUrl(url);
					sp.setText(text);
					if(!TextUtils.isEmpty(image)){
						sp.setImageUrl(image);
					}
					if(!TextUtils.isEmpty(url)){
						if(url.startsWith("http")){
							sp.setUrl(url);
						}
					}
					startShare(WechatMoments.NAME, sp);
				}
				
				//微信好友
				else if (item.get("ItemText").equals(mContext.getString(R.string.share_wechat_friends))) {//title+text+imageurl+url
					// 2、设置分享内容
					ShareParams sp = new ShareParams();
					sp.setShareType(Platform.SHARE_WEBPAGE);
					sp.setText(text); // 分享文本
					sp.setTitle(mContext.getString(R.string.share_title));
					sp.setUrl(url);
					if(!TextUtils.isEmpty(image)){
						sp.setImageUrl(image);
					}
					
					startShare(Wechat.NAME, sp);
				}
				
				//QQ: title	titleUrl	text	imageUrl
				else if (item.get("ItemText").equals(mContext.getString(R.string.share_qq))) {//
					// 2、设置分享内容
					ShareParams sp = new ShareParams();
					if(!TextUtils.isEmpty(image)){
						sp.setImageUrl(image);
					}
					sp.setTitle(mContext.getString(R.string.share_title));
					sp.setTitleUrl(url);
					sp.setText(text);
					
					startShare(QQ.NAME, sp);
					
				}
				
				//QZONE: title	titleUrl	text	imagePath	site	siteUrl
				else if (item.get("ItemText").equals(mContext.getString(R.string.share_qq_zone))) {//不支持分享本地图片,title+titleUrl+text+imagePath+site+siteUrl+imageUrl
					// 2、设置分享内容
					ShareParams sp = new ShareParams();
					if(!TextUtils.isEmpty(url)){
						sp.setTitle(mContext.getString(R.string.share_title));
						sp.setTitleUrl(url);
					}
					LogUtil.i(TAG, "text="+text);
					sp.setText(text);
					sp.setSite(mContext.getString(R.string.app_name));
					sp.setSiteUrl(mContext.getString(R.string.share_setting_url));
					
					if(!TextUtils.isEmpty(image)){
						sp.setImageUrl(image);
					}
					
					startShare(QZone.NAME, sp);
				}
				
				//Facebook
				else if (item.get("ItemText").equals("Facebook")) {
					// 2、设置分享内容
					ShareParams sp = new ShareParams();
					sp.setText(text+" \n"+url); // 分享文本
					
					//分享展商不加图片，新闻加图片
					
					if(!TextUtils.isEmpty(image)){
						sp.setImageUrl(image);
					}
					
					startShare(Facebook.NAME, sp);
					
				}
				
				//Twitter
//				else if (item.get("ItemText").equals("Twitter")) {//text+imageurl
//					// 2、设置分享内容
//					ShareParams sp = new ShareParams();
//					sp.setText(text+" \n"+url); // 分享文本
//					if(!TextUtils.isEmpty(image)){
//						sp.setImageUrl(image);
//					}
//
//					startShare(Twitter.NAME, sp);
//
//				}

				//WhatsApp
				else if (item.get("ItemText").equals("WhatsApp")) {//文本：text 图片：imagePath	 imageUrl
					ShareParams sp = new ShareParams();
					sp.setText(text+" \n"+url); // 分享文本
					startShare(WhatsApp.NAME, sp);
				}
				
				else if (item.get("ItemText").equals(mContext.getString(R.string.share_email))) {//address	title	text	imagePath  imageUrl
					// 2、设置分享内容
					ShareParams sp = new ShareParams();
					if(!TextUtils.isEmpty(image)){
						sp.setImageUrl(image);
					}
					sp.setTitle(mContext.getString(R.string.share_title));
					sp.setText(text+" \n"+url);
			//		sp.setAddress(address);
					
					startShare(Email.NAME, sp);
					

				}

				shareDialog.dismiss();

			}

			private void startShare(String platform, ShareParams sp){
				Platform pf = ShareSDK.getPlatform(platform);
				pf.setPlatformActionListener(new OnPlatformActionListener()); // 设置分享事件回调
				pf.share(sp);
				LogUtil.i(TAG, pf.getName()+":url="+url+",image="+image);
				handler.sendEmptyMessage(START_SHARE);
			}
		});
		
		
	}	
	
	public class OnPlatformActionListener implements PlatformActionListener {

		@Override
		public void onCancel(Platform arg0, int arg1) {
			handler.sendEmptyMessage(CANCEL);
		}

		@Override
		public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
			handler.sendEmptyMessage(SUCCESS);
		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			LogUtil.e(TAG, "error:"+arg2.getLocalizedMessage());
			handler.obtainMessage(ERROR, arg2.getLocalizedMessage());
		}
		
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				Toast.makeText(mContext,"Share Success", Toast.LENGTH_LONG).show();
				break;
			case ERROR:
				Toast.makeText(mContext, " Share Error", Toast.LENGTH_LONG).show();
				break;
			case CANCEL:
				Toast.makeText(mContext, " Share Cancel", Toast.LENGTH_LONG).show();
				break;
			case START_SHARE:
		//		Toast.makeText(mContext, "正在后台分享...", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};
	
	

}
