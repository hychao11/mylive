package com.example.myliveapp.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.myliveapp.R;
import com.example.myliveapp.util.BezierEvaluator;
import com.example.myliveapp.util.BezierListenr;

import java.util.Random;

/**
 * Created by 郝颖超 on 2016/9/28.
 */

public class FavorLayout extends RelativeLayout {
    private Random random = new Random();//用于实现随机功能

    private int dHeight;//爱心的高度
    private int dWidth;//爱心的宽度
    private int mHeight;//FavorLayout的高度
    private int mWidth;//FavorLayout的宽度
    private LayoutParams lp;
    // 我为了实现 变速效果 挑选了几种插补器
    private Interpolator line = new LinearInterpolator();//线性
    private Interpolator acc = new AccelerateInterpolator();//加速
    private Interpolator dce = new DecelerateInterpolator();//减速
    private Interpolator accdec = new AccelerateDecelerateInterpolator();//先加速后减速
    // 在init中初始化
    private Interpolator[] interpolators ;
    private Drawable[] drawables;

    public FavorLayout(Context context) {
        super(context);
    }

    public FavorLayout(Context context, AttributeSet attrs) {

        super(context, attrs);
        //为了显示区域,我设置了一个背景颜色,随意
        setBackgroundColor(Color.parseColor("#00000000"));
        //init里做一些初始化变量的操作
        init();
    }
    // 最终 init方法长这样:
    private void init() {

        //初始化显示的图片
        drawables = new Drawable[8];
        Drawable d1 = getResources().getDrawable(R.drawable.li_room_praise_0);
        Drawable d2 = getResources().getDrawable(R.drawable.li_room_praise_1);
        Drawable d3 = getResources().getDrawable(R.drawable.li_room_praise_2);
        Drawable d4 = getResources().getDrawable(R.drawable.li_room_praise_3);
        Drawable d5 = getResources().getDrawable(R.drawable.li_room_praise_4);
        Drawable d6 = getResources().getDrawable(R.drawable.li_room_praise_5);
        Drawable d7 = getResources().getDrawable(R.drawable.li_room_praise_6);
        Drawable d8 = getResources().getDrawable(R.drawable.li_room_praise_7);

        drawables[0]=d1;
        drawables[1]=d2;
        drawables[2]=d3;
        drawables[3]=d4;
        drawables[4]=d5;
        drawables[5]=d6;
        drawables[6]=d7;
        drawables[7]=d8;
        //获取图的宽高 用于后面的计算
        //注意 我这里3张图片的大小都是一样的,所以我只取了一个
        dHeight = d1.getIntrinsicHeight();
        dWidth = d1.getIntrinsicWidth();

        //底部 并且 水平居中
        lp = new LayoutParams(dWidth, dHeight);
        lp.addRule(CENTER_HORIZONTAL, TRUE);//这里的TRUE 要注意 不是true
        lp.addRule(ALIGN_PARENT_BOTTOM, TRUE);

        // 初始化插补器
        interpolators = new Interpolator[4];
        interpolators[0] = line;
        interpolators[1] = acc;
        interpolators[2] = dce;
        interpolators[3] = accdec;

    }

    //重写onMeasure 获取控件宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //注意!! 获取本身的宽高 需要在测量之后才有宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }
    //获取贝塞尔动画的方法
    private ValueAnimator getBezierValueAnimator(View target) {

        //初始化一个BezierEvaluator
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(2),getPointF(1));

        //这里最好画个图 理解一下 传入了起点 和 终点
        ValueAnimator animator = ValueAnimator.ofObject(evaluator,new PointF((mWidth-dWidth)/2,mHeight-dHeight),new PointF(random.nextInt(getWidth()),0));//随机
        animator.addUpdateListener(new BezierListenr(target));
        animator.setTarget(target);
        animator.setDuration(3000);
        return animator;
    }

//这里涉及到另外一个方法:getPointF(),这个是我用来获取途径的两个点
// 这里的取值可以随意调整,调整到你希望的样子就好
    /**
     * 获取中间的两个 点
     * @param scale
     */
    private PointF getPointF(int scale) {

        PointF pointF = new PointF();
        pointF.x = random.nextInt((mWidth - 100));//减去100 是为了控制 x轴活动范围,看效果 随意~~
        //再Y轴上 为了确保第二个点 在第一个点之上,我把Y分成了上下两半 这样动画效果好一些 也可以用其他方法
        pointF.y = random.nextInt((mHeight - 100))/scale;
        return pointF;
    }
    //我封装了一个方法 利用ObjectAnimator AnimatorSet来实现 alpha以及x,y轴的缩放功能
//target就是爱心
    private AnimatorSet getEnterAnimtor(final View target) {

        ObjectAnimator alpha = ObjectAnimator.ofFloat(target,View.ALPHA, 0.2f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target,View.SCALE_X, 0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target,View.SCALE_Y, 0.2f, 1f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(500);
        enter.setInterpolator(new LinearInterpolator());
        enter.playTogether(alpha,scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }

    // 合并前俩种动画,做成最终动画
    private Animator getAnimator(View target){
        AnimatorSet set = getEnterAnimtor(target);

        ValueAnimator bezierValueAnimator = getBezierValueAnimator(target);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(set);
        finalSet.playSequentially(set, bezierValueAnimator);
        finalSet.setInterpolator(interpolators[random.nextInt(4)]);//实现随机变速
        finalSet.setTarget(target);
        return finalSet;
    }
    //
    public void addFavor() {

        ImageView imageView = new ImageView(getContext());
        //随机选一个
        imageView.setImageDrawable(drawables[random.nextInt(8)]);
        imageView.setLayoutParams(lp);

        addView(imageView);
        Log.v("FavorLayout", "add后子view数:"+getChildCount());

        Animator set = getAnimator(imageView);
        set.addListener(new AnimEndListener(imageView));
        set.start();

    }
    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
            Log.v("FavorLayout", "removeView后子view数:"+getChildCount());
        }
    }

}



