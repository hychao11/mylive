package com.example.myliveapp.util;

import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.view.View;

/**
 * Created by 郝颖超 on 2016/9/28.
 */

public class BezierListenr implements ValueAnimator.AnimatorUpdateListener {
    private View target;

    public BezierListenr(View target) {
        this.target = target;
    }
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
        PointF pointF = (PointF) animation.getAnimatedValue();
        target.setX(pointF.x);
        target.setY(pointF.y);
        // 这里偷个懒,顺便做一个alpha动画,这样alpha渐变也完成啦
        target.setAlpha(1-animation.getAnimatedFraction());
    }
}
