package com.example.innf.newchangtu.Map.widget;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Author: Inno Fang
 * Time: 2016/9/6 17:10
 * Description: 承载百度MapView的布局
 */

public class ContainMapLayout extends RelativeLayout{

    private static final String TAG = "ContainMapLayout";

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    public ContainMapLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ContainMapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ContainMapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContainMapLayout(Context context) {
        super(context);
    }

    public void setCollapsingToolbarLayout(CollapsingToolbarLayout collapsingToolbarLayout) {
        mCollapsingToolbarLayout = collapsingToolbarLayout;
    }


    /*解决CollapsingToolbarLayout与百度MapView的滑动冲突问题*/
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent: is called");
        if (ev.getAction() == MotionEvent.ACTION_UP){
            mCollapsingToolbarLayout.requestDisallowInterceptTouchEvent(false);
        }else {
            mCollapsingToolbarLayout.requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }
}
