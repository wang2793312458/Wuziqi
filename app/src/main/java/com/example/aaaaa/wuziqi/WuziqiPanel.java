package com.example.aaaaa.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AAAAA on 2016/5/27.
 */
public class WuziqiPanel extends View {
    private int mPanelWidth;
    //不能写int类型（xiam的float），有精度丢失
    private float mLineHeight;
    //行数
    private int MAX_LINE = 10;
    private int MAX_COUNT_IN_LINE=5;

    private Paint mPaint = new Paint();
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    //设置棋子大小
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;
    //声明一个集合，用来存储用户点击的坐标
    private ArrayList<Point> mWhiteArry = new ArrayList<>();
    private ArrayList<Point> mBlackArry = new ArrayList<>();
    //判断黑子白子谁先下,为true白子先下
    private boolean mIsWhite = true;


    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;

    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        //透明色
       // setBackgroundColor(0x44ff0000);
        //添加方法对paint进行初始化
        init();
    }

    private void init() {
        //颜色
        mPaint.setColor(0x88000000);
        //抗锯齿
        mPaint.setAntiAlias(true);
        //绘制棋子
        mPaint.setDither(true);
        //画线
        mPaint.setStyle(Paint.Style.STROKE);
        //读取图片
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }

    //考虑具体使用情况，编写测量代码逻辑
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);
        //判断棋盘编剧不能为零
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        //正方形
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
        //棋子的尺寸跟随棋盘的变化
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);

    }

    //触屏点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) return false;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            //存储用户点击的坐标
            int x = (int) event.getX();
            int y = (int) event.getY();
            //每个point代表一个棋子位置
            Point p = getValidPoint(x, y);
            if (mWhiteArry.contains(p) || mBlackArry.contains(p)) {
                return false;
            }

            if (mIsWhite) {
                mWhiteArry.add(p);
            } else {
                mBlackArry.add(p);
            }
            invalidate();
            mIsWhite = !mIsWhite;

        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    /*
    * huipang
    * */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        //绘制棋子
        drawPieces(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
        boolean whitewin = checkFiveInLine(mWhiteArry);
        boolean blackWin = checkFiveInLine(mBlackArry);
        if (whitewin||blackWin){
            mIsGameOver=true;
            mIsWhiteWinner=whitewin;
            String text=mIsWhiteWinner?"白子胜":"黑子胜";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();

        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points){
            int x=p.x;
            int y=p.y;
            boolean win=checkHorizontal(x,y,points);
            if (win)return true;
            win=checkVetical(x,y,points);
            if (win)return true;
            win=checkLeftDiagonl(x,y,points);
            if (win)return true;
            win=checkRightDigonal(x,y,points);
            if (win)return true;
        }
        return false;
    }
//判断xy位置的棋子是否有横向的5个相邻一致
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count=1;
        //左
        for (int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x-i,y))){
                count++;
            }else {
                break;
            }
        }
        //右
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x+i,y))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }


        return false;
    }
    //判断竖直方向上是否 5子连
    private boolean checkVetical(int x, int y, List<Point> points) {
        int count=1;
        //上
        for (int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x,y+i))){
                count++;
            }else {
                break;
            }
        }
        //下
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }


        return false;
    }
    //左斜
    private boolean checkLeftDiagonl(int x, int y, List<Point> points) {
        int count=1;
        //上
        for (int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x-i,y+i))){
                count++;
            }else {
                break;
            }
        }
        //下
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x+i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }
    //右斜
    private boolean checkRightDigonal(int x, int y, List<Point> points) {
        int count=1;
        //上
        for (int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x+i,y+i))){
                count++;
            }else {
                break;
            }
        }
        //下
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }
        for (int i=1;i<MAX_COUNT_IN_LINE;i++){
            if (points.contains(new Point(x-i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAX_COUNT_IN_LINE){
            return true;
        }


        return false;
    }

    //绘制棋子
    private void drawPieces(Canvas canvas) {
        //白子
        for (int i = 0, n = mWhiteArry.size(); i < n; i++) {

            Point whitePoint = mWhiteArry.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
        //黑子
        for (int i = 0, n = mBlackArry.size(); i < n; i++) {

            Point blackPoint = mBlackArry.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);

        }
    }

    private void drawBoard(Canvas canvas) {
        //棋盘宽度
        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);

            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }
    //下一局
    public void start(){
        mWhiteArry.clear();
        mBlackArry.clear();
        mIsGameOver=false;
        mIsWhiteWinner=false;
        invalidate();
    }




    //View的存储与恢复、
    private static final String INSTANCE="instance";
    private static final String INSTANCE_GAME_OVER="instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY="instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY="instance_black_array";
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle=new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArry);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArry);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle= (Bundle) state;
            mIsGameOver=bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArry=bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArry=bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}