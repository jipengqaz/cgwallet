package cgtz.com.cgwallet.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.net.URL;
import java.util.List;

import cgtz.com.cgwallet.R;

public class BannerViewPagerAdapter extends PagerAdapter {
	private Context context;
	private List ilist;
	private List ulist;
	public ImageView icdifault;//默认的图片

//出现错误 private DisplayImageOptions options;
//	options=new DisplayImageOptions.Builder().showImageLoading(R.mipmap.cs).showImageForEmptyUri().
//	showImageOnFail(R.mipmap.cs).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).
//	displayer(new FadeInBitmapDisplayer(3000)).build();


	public BannerViewPagerAdapter(Context context) {
		this.context = context;
	}

//	2015年12月29日11:16:19  测试
	public BannerViewPagerAdapter(Context context,List iilist,List uulist){
		this.context = context;
		ilist = iilist;
		ulist = uulist;
}

	@Override
	public int getCount() {
		return ilist.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		final View view = View.inflate(context, R.layout.layout_adapter, null);
		container.addView(view);
		icdifault = (ImageView) view.findViewById(R.id.ic_default);//默认的图片
		ImageLoader.getInstance().displayImage(ilist.get(position).toString(), icdifault);
		icdifault.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(ulist.get(position).toString());
				intent.setData(content_url);
				context.startActivity(intent);
			}
		});
		return view;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
}
