
package com.android.imagecarousel;

import android.content.Context;

import android.os.Handler;
import android.os.Message;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager实现的轮播图广告自定义视图，既支持自动轮播页面也支持手势滑动切换页面
 */

public class SlideShowView extends FrameLayout {

    //自定义轮播图的资源ID
    private int[] imagesResIds;

    //存放轮播图片的ImageView的list
    private List<ImageView> imageViewsList;

    //放圆点的View的list
    private List<View> dotViewsList;

    //轮播图片的viewpager
    private ViewPager viewPager;

    //当前轮播页
    private int currentItem ;

    private ImageHandler handler = new ImageHandler(new WeakReference<SlideShowView>(this));

    public SlideShowView(Context context) {
        this(context, null);
    }

    public SlideShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initData();
		
        initUI(context);
    }


    /**
     * 初始化相关Data
     */
    private void initData() {

        //本地的四张广告图片
        imagesResIds = new int[]{
                R.drawable.ad1,
                R.drawable.ad2,
                R.drawable.ad3,
                R.drawable.ad4,
        };

        imageViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<View>();
    }

    /**
     * 初始化Views等UI
     */
    private void initUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);

        for (int imageID : imagesResIds) {
            ImageView view = new ImageView(context);
            view.setImageResource(imageID);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViewsList.add(view);
        }
        //轮播图片视图右下角的四个点
        dotViewsList.add(findViewById(R.id.v_dot1));
        dotViewsList.add(findViewById(R.id.v_dot2));
        dotViewsList.add(findViewById(R.id.v_dot3));
        dotViewsList.add(findViewById(R.id.v_dot4));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setFocusable(true);

        //设置适配器
        viewPager.setAdapter(new SlideShowPagerAdapter(imageViewsList));

        //设置页面改变监听事件
        viewPager.setOnPageChangeListener(new MyPageChangeListener());

        viewPager.setCurrentItem(0);

        //开始轮播效果
        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
    }

    /**
     * ViewPager的监听器 ，当ViewPager中页面的状态发生改变时调用
     */
    private class MyPageChangeListener implements OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {

            switch (arg0) {
                case ViewPager.SCROLL_STATE_DRAGGING:

                    isAutoPlay = false;
                    handler.sendEmptyMessage(ImageHandler.MSG_PAUSE_CAROUSEL);
                    break;

                case ViewPager.SCROLL_STATE_IDLE:

                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }

                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1 );
                    }

                    handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
                    break;

                default:

                    isAutoPlay = true;
                    break;

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int pos) {

            handler.sendMessage(Message.obtain(handler, ImageHandler.MSG_PAGE_CHANGED, pos, 0));

            currentItem = pos;
            for (int i = 0; i < dotViewsList.size(); i++) {
                if (i == pos) {
                    ((View) dotViewsList.get(pos)).setBackgroundResource(R.drawable.dotshape);
                } else {
                    ((View) dotViewsList.get(i)).setBackgroundResource(R.drawable.dotshape2);
                }
            }
        }

    }


    //Handler
    private static class ImageHandler extends Handler{

        //请求更新显示的View
        protected static final int MSG_UPDATE_IMAGE  = 1 ;

        //暂停轮播
        protected static final int MSG_PAUSE_CAROUSEL = 2 ;

        //恢复轮播
        protected static final int MSG_REGAIN_CAROUSEL = 3 ;

        //当手动滑动时,记录新页号
        protected static final int MSG_PAGE_CHANGED  = 4;

        //轮播间隔时间4秒
        protected static final long MSG_DELAY = 4000;

        //使用弱引用避免Handler泄露.
        private WeakReference<SlideShowView> weakReference;

        private int currentItem = 0;

        protected ImageHandler(WeakReference<SlideShowView> wk){
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SlideShowView slideShowView = weakReference.get();
            if (slideShowView == null){
                return ;
            }

            //检查消息队列并移除未发送的消息，避免消息出现重复
            if (slideShowView.handler.hasMessages(MSG_UPDATE_IMAGE)){
                slideShowView.handler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {

                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    slideShowView.viewPager.setCurrentItem(currentItem);

                    //准备下次播放
                    slideShowView.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;

                case MSG_REGAIN_CAROUSEL:
                    slideShowView.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;

                case MSG_PAGE_CHANGED:

                    //记录当前的页号，避免播放的时候页面显示不正确。
                    currentItem = msg.arg1;

                    //若为最后一张，则从头开始
                    if(currentItem == 3){
                        currentItem = -1 ;
                    }
                    break;

                case MSG_PAUSE_CAROUSEL:
                    slideShowView.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;

                default:
                    break;
            }
        }
    }
}
