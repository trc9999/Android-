package com.lmj.mybezierapplication.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by limengjie on 2016/12/21.
 */

public class BezierPath extends View {
    Paint paint1 = new Paint();
    Paint paintpath = new Paint();

    int radus = 300;
    int time = 5000;
    int width;
    int height;

    private ValueAnimator redAnim1;
    private ViewPoint cpoint =new ViewPoint();
    private AnimatorSet animatorSet2;
    private BezierPathListener bezierPathListener;
private Path path1 = new Path();

    public BezierPath(Context context) {
        super(context);
        init();
    }

    public BezierPath(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierPath(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        paint1.setStyle(Paint.Style.FILL);
        paint1.setAntiAlias(true);
        paint1.setColor(Color.RED);


        paintpath.setStyle(Paint.Style.FILL);
        paintpath.setAntiAlias(true);
        paintpath.setColor(Color.BLUE);
        paintpath.setStyle(Paint.Style.STROKE);
        paintpath.setAntiAlias(true);
        paintpath.setColor(Color.GRAY);
    }
//    mPath.quadTo(x1, y1, x2, y2) (x1,y1) 为控制点，(x2,y2)为结束点。
    public void initPath() {

        ViewPath viewPath = new ViewPath();
        viewPath.moveTo(width / 2, height / 2);
        cpoint.x  = width/2;
        cpoint.y = height/2;
        path1.moveTo(cpoint.x ,cpoint.y);

        viewPath.quadTo(width / 2 - radus , height / 2 - radus , width / 2, height / 2 - radus);
        viewPath.quadTo(width / 2 + radus , height / 2 - radus , width / 2, height / 2);
        redAnim1 = ValueAnimator.ofObject(new ViewPathEvaluator(), viewPath.getPoints().toArray());
        redAnim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                cpoint = (ViewPoint) valueAnimator.getAnimatedValue();
                path1.lineTo(cpoint.x ,cpoint.y);
                postInvalidate();
            }
        });
        redAnim1.setDuration(time);


        redAnim1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimationing = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnimationing = false;
                if(null!=bezierPathListener){
                    bezierPathListener.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                isAnimationing = false;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });



        animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(redAnim1);
        animatorSet2.setDuration(time);


    }
    public boolean isAnimationing = false;
    boolean isInitPath = false;



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        if (width > 0) {
            if (!isInitPath) {
                isInitPath = true;
                initPath();
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startAnimation();
        return super.onTouchEvent(event);
    }

    @Override
    public void draw(final Canvas canvas) {
        canvas.drawPath(path1, paintpath);
        canvas.drawCircle(cpoint.x-20, cpoint.y-20, 25, paint1);

    }
    public void startAnimation(){
        if (!isAnimationing) {
            animatorSet2.start();
        }
    }
    public void setListener(BezierPathListener bezierPathListener){
        this.bezierPathListener =bezierPathListener;
    }
    public  interface BezierPathListener{
        void onAnimationEnd();
    }


}
