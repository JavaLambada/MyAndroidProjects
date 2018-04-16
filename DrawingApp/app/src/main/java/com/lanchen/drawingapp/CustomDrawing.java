package com.lanchen.drawingapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.os.Handler;
import java.util.ArrayList;
import java.util.Random;



import static java.lang.System.currentTimeMillis;

/**
 * Created by chenlan on 13/03/18.
 */

public class CustomDrawing extends View implements View.OnTouchListener, View.OnLongClickListener {

    public CustomDrawing(Context context) {
        super(context);
        initApp();
    }

    public CustomDrawing(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initApp();
    }

    public CustomDrawing(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initApp();
    }

    public void initApp() {
        setOnLongClickListener(this);
        setOnTouchListener(this);
        handler = new Handler();
        handler.post(handlerProc);

    }


    ArrayList<Position> positionArrayList = new ArrayList<Position>();
    ArrayList<Position> longPressArrayList = new ArrayList<Position>();
    ArrayList<Position> tempArrayList = new ArrayList<Position>();
    final Paint paint = new Paint();
    final Random random = new Random();
    float brushSize = 10;   //move seekbar to change fone size
    float pressX;   //for press down x
    float pressY;
    float moveDisX; //for judge long press or not
    float moveDisY;
    final int MAX_BACK_STEPS = 10;  //for undo or redo
    int undoSteps = 0;
    int redoSteps = 0;
    long pressDown; //for judge long press or not
    long pressUp;
    long pressTime;

    boolean isLongPress = false;
    Handler handler;


    class Position {
        float x;
        float y;
        int color;
        float size;

        private Position(float x, float y, int color, float size) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = size;
        }
    }

    protected Runnable handlerProc = new Runnable() {
        @Override
        public void run() {

            if (isLongPress) {

                doLongPress();

            }

            // Repeat this the same runnable code block again longPress
            handler.postDelayed(handlerProc, 150);
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int count = event.getPointerCount();
        // Log.i("drawArea", String.format("onTouch: befor mask %d(%x), count = %d", actID, actID, count));

        float secondX = 0;
        float secondY = 0;
        if (count > 1) {

            // get second position
            secondX = event.getX(1);
            secondY = event.getY(1);
            positionArrayList.add(new Position(secondX,secondY, random.nextInt(), brushSize));
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressX = event.getX();
                pressY = event.getY();
                pressDown = currentTimeMillis();

                return false;


            case MotionEvent.ACTION_MOVE:

                Log.i("INFO", "onTouch up");
                float tempX = event.getX();
                float tempY = event.getY();

                pressUp = currentTimeMillis();

                pressTime = pressUp - pressDown;
                moveDisX = Math.abs(tempX - pressX);
                moveDisY =  Math.abs(tempY - pressY);

                    Log.i("Info", "moveX = " + moveDisX + " moveY = " + moveDisY);
                if (pressTime > 300 && moveDisX < 2 && moveDisY < 2){
                    isLongPress = true;
                    positionArrayList.removeAll(positionArrayList);
                    doLongPress();

                }else {
                    isLongPress = false;
                    longPressArrayList.removeAll(longPressArrayList);
                }

                if(!isLongPress){
                    positionArrayList.add(new Position(tempX,tempY, random.nextInt(), brushSize));
                }
                invalidate();
                pressX = event.getX();
                pressY = event.getY();


                return false;

            case MotionEvent.ACTION_UP:
                isLongPress = false;
//                longPressArrayList.removeAll(longPressArrayList);
                longPressArrayList.clear();
                undoSteps=0;
                redoSteps=0;
                invalidate();
                return true;
            default:
                return false;
        }
    

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //TODO set random color use handle

//        if(isLongPress){

            for (Position pt : longPressArrayList) {
                paint.setColor(pt.color);

                canvas.drawCircle(pt.x, pt.y, pt.size, paint);
            }

//        }else{

            for (Position pt : positionArrayList) {
                paint.setColor(pt.color);

                canvas.drawCircle(pt.x, pt.y, pt.size, paint);
            }

//        }
    }

    public void doLongPress(){
        Log.i("INFO", "do long press");

        longPressArrayList.add(new Position(pressX, pressY, random.nextInt(), 170));
        invalidate();
    }

    @Override
    public boolean onLongClick(View v) {
        isLongPress = true;
        return true;
    }


    /**
     * chagne circle size
     *
     * @param i
     */
    public void setBrushSize(int i) {
        if (i > 10) {
            brushSize = i;
        }

        System.out.println("current number is " + brushSize);
    }


    /**
     * undo click action
     */
    public void undoClick() {

        if (positionArrayList.size() < 1){

            showTips("You can only undo after you drawing something");
            return;
        }
        if (undoSteps < MAX_BACK_STEPS) {
            Position temp = positionArrayList.get(positionArrayList.size()-1);
            tempArrayList.add(new Position(temp.x, temp.y, temp.color, temp.size));
            positionArrayList.remove(positionArrayList.size() - 1);
            Log.i("INFO", "from undo event, positionArray's size: " + positionArrayList.size() + " temp's size: "+ tempArrayList.size());
            undoSteps++;
            invalidate();
        } else {

            showTips("You can only undo up to 10 steps");
        }

    }

    public void redoCLick(){

        //no drawing cannot redo
        if (positionArrayList.size() < 1){
            showTips("You can only redo after you undo something");
            return;
        }

        //redo have to less than undo or less than 10 steps
        if (redoSteps < MAX_BACK_STEPS && redoSteps < undoSteps) {


            Log.i("INFO", "from undo event, positionArray's size: " + positionArrayList.size() + " temp's size: "+ tempArrayList.size() + " redo "+redoSteps + "  undo: "+undoSteps);
            Position temp = tempArrayList.get(tempArrayList.size()-1);  //get the last delete position
            positionArrayList.add(new Position(temp.x, temp.y, temp.color, temp.size)); //add to draw the last delete position
            tempArrayList.remove(tempArrayList.size()-1); 

            redoSteps++;
            invalidate();
        } else {

            showTips("You can only redo your undo steps or redo up to 10 steps");
        }

    }

    public void clearClick(){
//        positionArrayList.removeAll(positionArrayList);
        positionArrayList.clear();
//        longPressArrayList.removeAll(longPressArrayList);
        longPressArrayList.clear();
        invalidate();

    }

    public  void showTips(String str){
        Toast.makeText(getContext(), str, Toast.LENGTH_LONG).show();
    }


}
