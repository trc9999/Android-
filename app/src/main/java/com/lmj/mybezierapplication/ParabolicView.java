package com.lmj.mybezierapplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.lmj.mybezierapplication.view.ViewPath;
import com.lmj.mybezierapplication.view.ViewPathEvaluator;
import com.lmj.mybezierapplication.view.ViewPoint;

/**
 * Created by trc on 17/4/27.
 * 抛物线
 */

public class ParabolicView extends View {
    /**
     * 红色空心球画笔
     */
    private Paint paint = new Paint();
    /**
     * 轨迹 画笔
     */
    private Paint linePaint = new Paint();
    /**
     * 贝塞尔 偏移量
     */
    private int radus = 300;
    /**
     * 动画时间
     */
    private int time = 500;
    /**
     * 动画集合按特定的顺序播放
     */
    private AnimatorSet animatorSet;
    /**
     * 控制点
     */
    private ViewPoint control = new ViewPoint();
    /**
     * 起始点  终点
     */
    private ViewPoint start, end;
    /**
     * 圆动态坐标
     */
    private ViewPoint circlePoint = new ViewPoint();
    /**
     * 轨迹路径
     */
    private Path linePath;
    /**
     * 圆 的路径
     */
    private ViewPath viewPath;
    /**
     * 动画的状态
     */
    private boolean isAnimationing = false;
    /**
     * 轨迹是否初始化
     */
    private boolean lineFlag = false;

    public ParabolicView(Context context) {
        super(context);
        init();
    }


    public ParabolicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public ParabolicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public ParabolicView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    private void init() {
        start = new ViewPoint();
        end = new ViewPoint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(30);

        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.GRAY);

        end.x = 100;
        end.y = 990;
    }


    private void initPath() {
        // 轨迹路径
        linePath = new Path();
        linePath.moveTo(start.x, start.y);
        //view 的路径
        viewPath = new ViewPath();
        viewPath.moveTo(start.x, start.y);
        viewPath.quadTo(control.x, control.y, end.x, end.y);
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new ViewPathEvaluator(), viewPath.getPoints().toArray());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                circlePoint = (ViewPoint) valueAnimator.getAnimatedValue();
                linePath.lineTo(circlePoint.x, circlePoint.y);
                postInvalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimationing = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnimationing = false;
                //动画 完成 可以写个接口
//                if(null!=bezierPathListener){
//                    bezierPathListener.onAnimationEnd();
//                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                isAnimationing = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.setDuration(time);
        animatorSet = new AnimatorSet();
        animatorSet.playTogether(valueAnimator);
        animatorSet.setDuration(time);
        if (!isAnimationing) {
            animatorSet.start();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 根据触摸位置更新控制点，并提示重绘
        control.x = 400;
        control.y = event.getY() - radus;
        start.x = event.getX();
        start.y = event.getY();
        initPath();
        lineFlag = true;
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lineFlag) {
            canvas.drawPath(linePath, linePaint);
        }
        canvas.drawCircle(circlePoint.x - 20, circlePoint.y - 20, 25, paint);
    }
}
