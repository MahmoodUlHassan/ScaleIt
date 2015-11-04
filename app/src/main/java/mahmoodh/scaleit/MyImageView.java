package mahmoodh.scaleit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by ivy4619 on 3/14/15.
 */


public class MyImageView extends View
{
    static private final String TAG = "MyImageView:";
    float refPoints[] = new float[4];
    float targetPoints[] = new float[4];


    public static final int STATE_ALL_CLEAR = 0;
    public static final int STATE_FIRST_DRAW = 1;
    public static final int STATE_REFERENCE_SET = 2;
    public static final int STATE_SECOND_DRAW = 3;
    public static final int STATE_TARGET_SET = 4;
    public static final int STATE_THIRD_DRAW = 5;

    int currentStage = 0;

    private float mX ,mY;
    Path    mPath;
    Paint   mPaint;
    Path    mReferencePath;
    Paint   mReferencePaint;
    Path    mTargetPath;
    Paint   mTargetPaint;

    private float mStartX, mStartY, mEndX,mEndY;
    private static final float TOUCH_TOLERANCE = 7;
    private static final float DISTANCE_FROM_TOUCH_POINT = 40;
    private boolean saveToGallery;
    private int counter = 0;

    public MyImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);

        mPath = new Path();
        mReferencePath = new Path();
        mTargetPath = new Path();

        mReferencePaint = new Paint();
        mReferencePaint = mPaint;
        mReferencePaint.setStrokeWidth(6);
        mReferencePaint.setColor(Color.GREEN);

        mTargetPaint = new Paint();
        mTargetPaint = mPaint;
        mTargetPaint.setStrokeWidth(6);
        mTargetPaint.setColor(Color.BLACK);

    }

    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);
        Log.i(TAG, "inDraw : currentStage : "+currentStage);
//        Log.i(TAG, "currentStage : "+currentStage);
        canvas.drawPath(mTargetPath, mTargetPaint);
        canvas.drawPath(mReferencePath, mReferencePaint);
        canvas.drawPath(mPath, mPaint);
        if (currentStage == STATE_FIRST_DRAW) {
            canvas.drawPath(mPath, mPaint);
        } else if(currentStage == STATE_REFERENCE_SET) {
            canvas.drawPath(mReferencePath, mReferencePaint);
        } else if (currentStage == STATE_SECOND_DRAW) {
            canvas.drawPath(mReferencePath, mReferencePaint);
            canvas.drawPath(mPath, mPaint);
        } else if (currentStage == STATE_TARGET_SET) {
            canvas.drawPath(mTargetPath, mTargetPaint);
            canvas.drawPath(mReferencePath, mReferencePaint);
        } else {
            canvas.drawPath(mTargetPath, mTargetPaint);
            canvas.drawPath(mReferencePath, mReferencePaint);
            canvas.drawPath(mPath, mPaint);
        }

        if(saveToGallery == true) {
            // Attempt to make the bitmap and write it to a file.
            Bitmap toDisk = null;
            try {
//                String path = //Environment.getExternalStorageDirectory().toString();

                String path = Environment.getExternalStoragePublicDirectory("Pictures/AndroidExampleFolder").getPath();
                OutputStream fOut = null;
                File file = new File(path, "scaleIt_"+counter+".jpg");
                fOut = new FileOutputStream(file);

                toDisk = Bitmap.createBitmap(canvas.getWidth(),canvas.getHeight(), Bitmap.Config.ARGB_8888);
                canvas.setBitmap(toDisk);
                toDisk.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                saveToGallery = false;
                counter++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mStartX = x;
        mStartY = y;
        if(currentStage == STATE_ALL_CLEAR || currentStage == STATE_REFERENCE_SET || currentStage == STATE_TARGET_SET) {
            currentStage++;
            Log.i(TAG, "touch_start : currentStageInc to : " + currentStage);
        }// else its just clearing the previous line and drawing another so no new state.
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.reset();
            mPath.moveTo(mStartX, mStartY);
            mPath.lineTo(x, y);
            mX = x; mY = y;
            invalidate();
        }
    }
    private void touch_up(float x, float y) {
        mEndX = x;
        mEndY = y;
        mPath.reset();
        mPath.moveTo(mStartX, mStartY);
        mPath.lineTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y - DISTANCE_FROM_TOUCH_POINT);
                break;
            case MotionEvent.ACTION_UP:
                touch_up(x,y - DISTANCE_FROM_TOUCH_POINT);
                invalidate();
                break;
        }
        return true;
    }

    /**
     * Clear the most recently drawn
     * line and then more in that order
     * */
    public void clearBtnAction() {
        mPath.reset();
        mStartX = 0; mStartY = 0; mEndX = 0; mEndY = 0;
        mPath= new Path();

        if(currentStage == STATE_REFERENCE_SET) {
            mReferencePath.reset();
            Arrays.fill(refPoints,0);
            if (mTargetPath.isEmpty())
                currentStage = STATE_ALL_CLEAR;
            else
                currentStage = STATE_TARGET_SET;
        } else if (currentStage == STATE_TARGET_SET) {
            mTargetPath.reset();
            Arrays.fill(targetPoints, 0);
            if (mReferencePath.isEmpty())
                currentStage = STATE_ALL_CLEAR;
            else
                currentStage = STATE_REFERENCE_SET;
        } else if(currentStage == STATE_FIRST_DRAW || currentStage == STATE_SECOND_DRAW || currentStage == STATE_THIRD_DRAW) {
            mPath.reset();currentStage--;
        }
        Log.i(TAG, "clearBtnAction::currentStage to : " + currentStage);
        invalidate();
    }

    public int SaveAsReference() {
        if (mStartX+mStartY+mEndY+mEndX == 0) {
            return 1;
        }
        refPoints[0] = mStartX;
        refPoints[1] = mStartY;
        refPoints[2] = mEndX;
        refPoints[3] = mEndY;
        mReferencePath.reset();
        mReferencePath.moveTo(mStartX, mStartY);
        mReferencePath.lineTo(mEndX, mEndY);
        currentStage = STATE_REFERENCE_SET;//1;
        Log.i(TAG, "SaveAsReference::currentStage : "+currentStage);
        invalidate();
        mStartX = 0; mStartY = 0; mEndX = 0; mEndY = 0;
        return 0;
    }

    public int SaveAsTarget() {

        if (mStartX+mStartY+mEndY+mEndX == 0) {
            return 1;
        }
        targetPoints[0] = mStartX;
        targetPoints[1] = mStartY;
        targetPoints[2] = mEndX;
        targetPoints[3] = mEndY;
        mTargetPath.reset();
        mTargetPath.moveTo(mStartX, mStartY);
        mTargetPath.lineTo(mEndX, mEndY);
        currentStage = STATE_TARGET_SET;//3;
        Log.i(TAG, "SaveAsTarget::currentStage : "+currentStage);
        invalidate();
        mStartX = 0; mStartY = 0; mEndX = 0; mEndY = 0;
        return 0;
    }

    public float Measure() {
        float refL = findLenght(refPoints);
        float tarL = findLenght(targetPoints);
        if (refL!=0) return tarL/refL;
        return 0;
    }

    private float findLenght(float[] refPoints) {
        if(refPoints != null && refPoints.length == 4)
            return  (float)Math.sqrt( (refPoints[0] - refPoints[2])*(refPoints[0] - refPoints[2]) + (refPoints[1]-refPoints[3])*(refPoints[1]-refPoints[3])  );
        return  0;
    }

    public void saveToGallery() {
//        saveToGallery = true;
//        invalidate();
    }
}


