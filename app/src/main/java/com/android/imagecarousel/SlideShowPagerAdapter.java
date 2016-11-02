
package com.android.imagecarousel;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

/**
 * 显示广告图片的viewPager适配器
 */
public class SlideShowPagerAdapter extends PagerAdapter {

    //放轮播图片的ImageView 的list
    public List<ImageView> imageViewsList;

    public SlideShowPagerAdapter(List<ImageView> imageViewsList) {
        this.imageViewsList = imageViewsList;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {

        ((ViewPager)container).removeView(imageViewsList.get(position));
    }

    @Override
    public Object instantiateItem(View container, int position) {

        ((ViewPager)container).addView(imageViewsList.get(position));
        return imageViewsList.get(position);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageViewsList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }
    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public Parcelable saveState() {
        // TODO Auto-generated method stub
        return null;
    }

}