package tk.wenop.XiangYu.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import tk.wenop.XiangYu.R;
import tk.wenop.XiangYu.util.ImageLoadOptions;
import tk.wenop.XiangYu.view.CustomViewPager;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**图片浏览
  * @ClassName: ImageBrowserActivity
  * @Description: TODO
  * @author smile
  * @date 2014-6-19 下午8:22:49
  */


// wenop-add
interface OnPictureClickCallback {
    void onPictureClick();
}

public class ImageBrowserActivity extends BaseActivity
        implements OnPageChangeListener, OnPictureClickCallback {

	private CustomViewPager mSvpPager;
	private ImageBrowserAdapter mAdapter;
	LinearLayout layout_image;
	private int mPosition;
	
	private ArrayList<String> mPhotos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showpicture);
		init();
		initViews();
	}

	private void init() {
		mPhotos = getIntent().getStringArrayListExtra("photos");
		mPosition = getIntent().getIntExtra("position", 0);
	}
	
	protected void initViews() {
		mSvpPager = (CustomViewPager) findViewById(R.id.pagerview);
		mAdapter = new ImageBrowserAdapter(this);
		mSvpPager.setAdapter(mAdapter);
		mSvpPager.setCurrentItem(mPosition, false);
		mSvpPager.setOnPageChangeListener(this);

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		mPosition = arg0;
	}

    @Override
    public void onPictureClick() {
        // wenop-add
        // 点击图片返回
        finish();
    }

    private class ImageBrowserAdapter extends PagerAdapter{

        OnPictureClickCallback mPictureClickCallback;
		
		private LayoutInflater inflater;
        Context mContext;
		
		public ImageBrowserAdapter (Context context){
			this.inflater = LayoutInflater.from(context);
            mContext = context;
            mPictureClickCallback = (OnPictureClickCallback) context;
		}
		
		@Override
		public int getCount() {
			
			return mPhotos.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			
			return view == object;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {


			
			View imageLayout = inflater.inflate(R.layout.item_show_picture,
	                container, false);
	        final PhotoView photoView = (PhotoView) imageLayout
	                .findViewById(R.id.photoview);
	        final ProgressBar progress = (ProgressBar)imageLayout.findViewById(R.id.progress);

            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float v, float v1) {
                    mPictureClickCallback.onPictureClick();
                }
            });

            /*photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPictureClickCallback.onPictureClick();
                }
            });*/
	        
	        final String imgUrl = mPhotos.get(position);
	        ImageLoader.getInstance().displayImage(imgUrl, photoView, ImageLoadOptions.getOptions(),new SimpleImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					
					progress.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					
					progress.setVisibility(View.GONE);
					
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					
					progress.setVisibility(View.GONE);
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					
					progress.setVisibility(View.GONE);
					
				}
			});
	        
	        container.addView(imageLayout, 0);
	        return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		
	}

	
}
