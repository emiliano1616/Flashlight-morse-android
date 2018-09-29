package tpandroid.flashlight;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
//import example.camera.imgproc.R;

public class TopView extends View {

    public Bitmap mBitmap;
    private Paint mPaintGray;
    private Paint mPaintWhite;
    private Paint mPaintBlue;

    public byte[] mYUVData;
    public int mImageWidth, mImageHeight;

    private int canvasWidth, canvasHeight;
    private int centerW, centerH;

    public String msg;
    //private Rect rectangle;


    public TopView(Context ctx){
        super(ctx);

        mPaintWhite = new Paint();
        mPaintWhite.setStyle(Paint.Style.FILL);
        mPaintWhite.setColor(Color.WHITE);
        mPaintWhite.setTextSize(50);

        mPaintBlue = new Paint();
        //mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.BLUE);
        mPaintBlue.setTextSize(50);
        mPaintBlue.setAlpha(100);

        mPaintGray = new Paint();
        mPaintGray.setStyle(Paint.Style.STROKE);
        mPaintGray.setColor(Color.GRAY);
        //mPaintGray.setAlpha(65);
        mPaintGray.setStrokeWidth(7);

        mBitmap = null;
        mYUVData = null;
        msg = new String("");
        //rectangle = new Rect(500,500,50,100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        //Log.i("pepe", "1");
        //Log.i("despues", "width: " + String.valueOf(mImageWidth) + " height: " + String.valueOf(mImageHeight));

        //canvas.drawRect(rectangle, mPaintGray);
        //canvas.drawRect(100,600,1100,700, mPaintGray);
        canvas.drawRect((float) (mImageHeight * 0.1), (float) (mImageWidth * 0.85), (float) (mImageHeight * 0.9), (float) (mImageWidth * 0.95), mPaintBlue);
        canvas.drawCircle(mImageHeight / 2, mImageWidth / 2, 50, mPaintGray);
        if(!msg.equals(""))
            canvas.drawText(msg, (float)(mImageHeight*0.15), (float)(mImageWidth*0.92), mPaintWhite);
        if(mBitmap ==null){

            canvasWidth = canvas.getWidth();
            canvasHeight = canvas.getHeight();
            centerW = canvasWidth>>1;
            centerH = canvasHeight>>1;

            //image processing functions goes here
            //image data is in the mYUVData byte array
            //this is a yuv formated image(you can get a grayscale image by extracting the first wxh bytes from this array
            //or use image proccesing libraries such as OpenCV

            //optionally, draw the processed image
            //fill the mBitmap variable with processed array data and display it with canvas.drawBitmap
            //canvas.drawCircle(0,0,100,mPaintYellow);
        }
        super.onDraw(canvas);
    }

}
