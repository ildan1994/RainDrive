package com.example.raindriveiter1_10.ui.widget;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
//import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;


/**
 * Created by gengzhibo on 17/8/3.
 */

/**
 * The dashboard class to show the indicator
 */
public class DashBoard extends View {



    private Paint paint , tmpPaint , textPaint ,  strokePain;
    private RectF rect;
    private int backGroundColor;    //background color
    private float pointLength;      //cursor length
    private float per ;             //cursor percentage
    private float perPoint ;        //cache cursor percentage
    private float perOld ;          //cursor before changed
    private float length ;          //radius of the dashboard
    private float r ;

    public DashBoard(Context context) {
        super(context);
        init();
    }



    public DashBoard(Context context,  AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width / 2 / 4 * 5;
        initIndex(width / 2);
        setMeasuredDimension(width, height);
    }



    private void initIndex(int specSize) {
        backGroundColor = Color.TRANSPARENT;
        r = specSize;
        length = r  / 4 * 3;
        pointLength =  - (float) (r *  0.6);
        per = 0;
        perOld = 0;
    }


    private void init() {
        paint = new Paint();
        rect = new RectF();
        textPaint = new Paint();
        tmpPaint = new Paint();
        strokePain = new Paint();
    }

    public DashBoard(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void setR(float r) {
        this.r = r;
        this.length = r  / 4 * 3;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initRing(canvas);
        initScale(canvas);
        initPointer(canvas);
        initText(canvas);
    }

    private void initText(Canvas canvas) {

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth()/2, r);

        float rIndex = length ;

        paint.setColor(Color.parseColor("#eeeeee"));
        paint.setShader(null);
        paint.setShadowLayer(5, 0, 0, 0x54000000);
        rect = new RectF( - (rIndex/ 3 ), - (rIndex / 3), rIndex / 3, rIndex / 3);
        canvas.drawArc(rect, 0, 360, true, paint);

        paint.clearShadowLayer();

        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth()/2f , r);


        textPaint.setStrokeWidth(0);
        textPaint.setAntiAlias(true);

        textPaint.setTextSize(60);
        textPaint.setColor(Color.parseColor("#fc6555"));
        textPaint.setTextAlign(Paint.Align.RIGHT);



        int _per = (int) (per * 120);

        if (_per == 7){
            textPaint.setColor(Color.parseColor("#66f011"));
            canvas.drawText("Low", 55, 0, textPaint);
        }else if (_per == 36) {
            textPaint.setColor(Color.parseColor("#dbdb40"));
            textPaint.setTextSize(55);
            canvas.drawText("Medium", 90, 0, textPaint);
        }else if(_per == 88){
            textPaint.setColor(Color.parseColor("#f07d18"));
            //textPaint.setTextSize(55);
            canvas.drawText("High", 65, 0, textPaint);
        }
        else if(_per == 116){
            textPaint.setColor(Color.parseColor("#f01111"));
            textPaint.setTextSize(45);
            canvas.drawText(" Extra High", 95, 0, textPaint);
        }



        float swidth = textPaint.measureText(String.valueOf(_per));
        swidth =   (swidth - (swidth + 22) / 2);


        canvas.translate( swidth , 0);
        //canvas.drawText("" + (_per), 0, 0, textPaint);

        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.LEFT);

        //canvas.drawText("%" , 0, 0, textPaint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.parseColor("#999999"));


        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth()/2  , r + length / 3 /2 );
        canvas.drawText("Risk" , 0, 0, textPaint);

    }


    public void setBackGroundColor(int color){
        this.backGroundColor = color;
    }

    public void setPointLength1(float pointLength1){
        this.pointLength = -length * pointLength1 ;
    }

    private void initScale(Canvas canvas) {
        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth()/2, r);
        paint.setColor(Color.parseColor("#999999"));

        tmpPaint = new Paint(paint);
        tmpPaint.setStrokeWidth(1);
        tmpPaint.setTextSize(35);
        tmpPaint.setTextAlign(Paint.Align.CENTER);



        canvas.rotate(-90,0f,0f);

        float  y = length;
        y = - y;
        int count = 8;
        paint.setColor(backGroundColor);

        float tempRou = 180 / 8f;

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);

//        for (int i = 0 ; i <= count ; i++){
//
////            if (i % 2 == 0 ) {
////                canvas.drawText(String.valueOf((i)*2), 0, y - 20f, tmpPaint);
////            }
//            canvas.drawText(String.valueOf((i)*2), 0, y - 20f, tmpPaint);
//
//            canvas.drawLine(0f, y , 0, y + length / 15, paint);
//
//            canvas.rotate(tempRou,0f,0f);
//        }

    }


    private void initPointer(Canvas canvas) {
        paint.setColor(Color.BLACK);


        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth()/2, r);
        float change;

        if (perPoint < 1 ){
            change = perPoint * 180;
        }else {
            change = 180;
        }

        canvas.rotate(-90 + change,0f,0f);

        Path path = new Path();
        path.moveTo(0 , pointLength);
        path.lineTo(-10 , 0);
        path.lineTo(10,0);
        path.lineTo(0 , pointLength);
        path.close();

        canvas.drawPath(path, paint);

    }

    private void initRing(Canvas canvas) {
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        canvas.save();
        canvas.translate(canvas.getWidth()/2, r);


        paint.setStyle(Paint.Style.FILL);
//        int[] colors = {Color.parseColor("#79D062"), Color.parseColor("#f9cf45"), Color.parseColor("#00ff00")};
        //float[] positions = {0.5f - 10f/180f * 0.5f, 0.5f + 0.5f * 5f / 6f, 1.0f};
        int[] colors = {Color.parseColor("#f01111"), Color.parseColor("#66f011"),Color.parseColor("#ffff00"),Color.parseColor("#ffcc00"), Color.parseColor("#f07d18"), Color.parseColor("#f01111")};
        float[] positions = {1/36f, 0.5f - 1/36f, 0.5f + 1/9f, 0.75f, 0.75f + 5/36f, 1};
        SweepGradient sweepGradient = new SweepGradient(0, 0, colors, positions);
        paint.setShader(sweepGradient);
        rect = new RectF( -length, -length, length, length);
//        canvas.drawArc(rect, 170, 10f + 180f / 6f * 5f, true, paint);
        canvas.drawArc(rect, 170, 200, true, paint);


//        paint.setStyle(Paint.Style.FILL);
//        canvas.rotate(10,0f,0f);//F95A37 f9cf45
//        int[] colors2 = {Color.parseColor("#f9cf45"),  Color.parseColor("#F95A37")};
//        float[] positions2 = {0.5f + 0.5f * ( 144f / 180f), 1.0f};
//        sweepGradient = new SweepGradient(0, 0, colors2, positions2);
//        paint.setShader(sweepGradient);
//        rect = new RectF( -length, -length, length, length);
//        canvas.drawArc(rect, 180f + 180f * (140f / 180f), 180f / 6 + 10, true, paint);



        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth()/2, r);

        strokePain = new Paint(paint);

        strokePain.setColor(0x3f979797);
        strokePain.setStrokeWidth(10);
        strokePain.setShader(null);
        strokePain.setStyle(Paint.Style.STROKE);
        canvas.drawArc(rect, 170, 200, true, strokePain);



        canvas.restore();
        canvas.save();
        canvas.translate(canvas.getWidth()/2, r);


        paint.setShader(null);
        paint.setColor(backGroundColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(-length  , (float) (Math.sin(Math.toRadians(10) ) * length /3f * 2f), length  ,  (float) (Math.sin(Math.toRadians(10)) * length  + 100) , paint);
        canvas.drawRect(-length  , (float) (Math.sin(Math.toRadians(10) ) * length /3f * 2f), length  ,  (float) (Math.sin(Math.toRadians(10) ) * length /3f * 2f) , strokePain);



        paint.setColor(backGroundColor);
        paint.setShader(null);
        rect = new RectF( - (length - length / 3f  - 2), -(length / 3f * 2f - 2), length - length / 3f -2 , length / 3f * 2f - 2);
        canvas.drawArc(rect, 170, 200, true, strokePain);
        canvas.drawArc(rect, 0, 360, true, paint);

    }



    public void cgangePer(float per ){
        this.perOld = this.per;
        this.per = per;
        ValueAnimator va =  ValueAnimator.ofFloat(perOld,per);
        va.setDuration(1000);
        va.setInterpolator(new OvershootInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                perPoint = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        va.start();
    }
}
