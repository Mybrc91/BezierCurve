package com.wbq.beziercurve;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 作者：${wbq} on 2017/1/6 11:11
 * 邮箱：wangbaiqiang@heigo.com.cn
 */

public class BourceView extends SurfaceView implements SurfaceHolder.Callback{
    private static final int STATE_DOWN=1;
    private static final int STATE_UP=2;

    private Paint mPaint;
    private Path mPath;
    private int mLineColor;
    private int mPointColor;
    private int mLineWidth;
    private int mLineHeight;
    private float mDownDistance;
    private float mUpDistance;
    private float freeBallDistance;

    private ValueAnimator downControl;
    private ValueAnimator upControl;
    private ValueAnimator freeControl;
    private AnimatorSet animatorSet;
    private int state;

    private boolean isBound=false;
    private boolean isBallFreeUp=false;
    private boolean isUpControlDied=false;
    private boolean isAnimationShowing=false;
    private static final String TAG="wbq";

    public BourceView(Context context) {
        this(context,null);
    }

    public BourceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BourceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        initAttributes(context,attrs);
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mLineHeight);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPath=new Path();
        getHolder().addCallback(this);
        initControl();
    }

    /**
     * 三个属性动画 并且添加监听
     */
    private void initControl() {
        downControl=ValueAnimator.ofInt(0,1);
        downControl.setDuration(500);
        downControl.setInterpolator(new DecelerateInterpolator());
        downControl.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDownDistance=50*animation.getAnimatedFraction();
            }
        });
        downControl.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                state=STATE_DOWN;
                postInvalidate();
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        upControl=ValueAnimator.ofFloat(0,1);
        upControl.setDuration(900);
        upControl.setInterpolator(new BounceInterplator());
        upControl.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mUpDistance=50*animation.getAnimatedFraction();
                if(mUpDistance>=50) {
                    isBound=true;
                    if(!freeControl.isRunning()&&!freeControl.isStarted()&&!isBallFreeUp) {
                        freeControl.start();
                    }
                }
                postInvalidate();
            }
        });
        upControl.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                state=STATE_UP;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isUpControlDied=true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        freeControl=ValueAnimator.ofFloat(0,6.8f);
        freeControl.setDuration(600);
        freeControl.setInterpolator(new DecelerateInterpolator());
        freeControl.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t=animation.getAnimatedFraction();
                freeBallDistance=34*t-5*t*t;
                if(isUpControlDied) {
                    postInvalidate();
                }
            }
        });
        freeControl.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isBallFreeUp=true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimationShowing=false;
                startTotalAnimator();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet=new AnimatorSet();
        animatorSet.play(downControl).before(upControl);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public  void startTotalAnimator() {
        if(isAnimationShowing) {
            return;
        }
        if(animatorSet.isRunning()) {
            animatorSet.end();
            animatorSet.cancel();
        }
        isBound=false;
        isBallFreeUp=false;
        isUpControlDied=false;
        animatorSet.start();
    }

    /**
     * 初始化自定义的属性值
     * @param context
     * @param attrs
     */
    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.myBourceView);
        mLineColor=a.getColor(R.styleable.myBourceView_line_color, Color.GREEN);
        mLineWidth=a.getDimensionPixelOffset(R.styleable.myBourceView_line_width,200);
        mLineHeight=a.getDimensionPixelOffset(R.styleable.myBourceView_line_height,3);
        mPointColor=a.getColor(R.styleable.myBourceView_point_color,Color.BLACK);
        a.recycle();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas=holder.lockCanvas();
        draw(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mLineColor);
        mPath.reset();
        mPath.moveTo(getWidth()/2-mLineWidth/2,getHeight()/2);

        if(state==STATE_DOWN) {
            //绳子向下  小球一直连着绳子
            mPath.quadTo((float) (getWidth()/2-mLineHeight/2+0.375*mLineWidth),getHeight()/2,getWidth()/2,getHeight()/2+mDownDistance);
            mPath.quadTo((float) (getWidth()/2+mLineWidth/2-0.375*mLineWidth),getHeight()/2+mDownDistance,getWidth()/2+mLineWidth/2,getHeight()/2);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mPath,mPaint);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mPointColor);
            canvas.drawCircle(getWidth()/2,getHeight()/2+mDownDistance-10,10,mPaint);

        }else if(state==STATE_UP){
            //绳子向上  分为两种状态 小球挨着绳子和不挨着绳子
            mPath.quadTo((float) (getWidth()/2-mLineWidth/2+0.375*mLineWidth),getHeight()/2+(50-mUpDistance),getWidth()/2,getHeight()/2+(50-mUpDistance));
            mPath.quadTo((float) (getWidth()/2+mLineWidth/2-0.375*mLineWidth),getHeight()/2+(50-mUpDistance),getWidth()/2+mLineWidth/2,getHeight()/2);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mPath,mPaint);

            //绘制原点
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mPointColor);

            //小球未脱离绳子
            if(!isBound) {
                canvas.drawCircle(getWidth()/2,getHeight()/2+(50-mUpDistance)-10,10,mPaint);
            }else{
            //小球脱离绳子 运动到水平点做向上抛的运动
                canvas.drawCircle(getWidth()/2,getHeight()/2-freeBallDistance-10,10,mPaint);
            }

        }
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth()/2-mLineWidth/2,getHeight()/2,10,mPaint);
        canvas.drawCircle(getWidth()/2+mLineWidth/2,getHeight()/2,10,mPaint);
    }
    class BounceInterplator implements Interpolator{

        @Override
        public float getInterpolation(float input) {
            return (float)(1-Math.exp(-3*input)*Math.cos(10*input));
        }
    }
}
