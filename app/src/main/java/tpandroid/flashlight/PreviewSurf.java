package tpandroid.flashlight;


import java.security.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
//import android.hardware.Camera;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

//import example.camera.imgproc.R;
@SuppressWarnings("deprecation")
public class PreviewSurf extends SurfaceView implements Callback {

    //private Camera mCamera;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private TopView mTopView;
    private boolean mFinished;
    private int[] pixels;
    private Camera.Size previewSize;

    private long timeWhite;
    private long timeBlack;
    private long acumulatedTimeWhite;
    private long acumulatedTimeBlack;
    private boolean lastFrameWasWhite;
    private int lightAccurate = 60;

    Map<String, String> letterTable;
    String tempMsg;

    public PreviewSurf(Context ctx, TopView topView) {
        super(ctx);

        mTopView = topView;
        mFinished = false;

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        letterTable = new HashMap<String, String>();
        letterTable.put(".-", "a");
        letterTable.put("-...", "b");
        letterTable.put("-.-.", "c");
        letterTable.put("-..", "d");
        letterTable.put(".", "e");
        letterTable.put("..-.", "f");
        letterTable.put("--.", "g");
        letterTable.put("....", "h");
        letterTable.put("..", "i");
        letterTable.put(".---", "j");
        letterTable.put("-.-", "k");
        letterTable.put(".-..", "l");
        letterTable.put("--", "m");
        letterTable.put("-.", "n");
        letterTable.put("---", "o");
        letterTable.put(".--.", "p");
        letterTable.put("--.-", "q");
        letterTable.put(".-.", "r");
        letterTable.put("...", "s");
        letterTable.put("-", "t");
        letterTable.put("..-", "u");
        letterTable.put("...-", "v");
        letterTable.put(".--", "x");
        letterTable.put("-..-", "y");
        letterTable.put("-.--", "z");
        letterTable.put("--..", " ");
        tempMsg = "";

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // TODO Auto-generated method stub
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        int frameWidth = w;
        int frameHeight = h;

        // selecting optimal camera preview size
        {
            double minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    frameWidth = size.width;
                    frameHeight = size.height;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        Log.i("MIN RES", String.valueOf(frameWidth) + " x " + String.valueOf(frameHeight));
        parameters.setPreviewSize(frameWidth, frameHeight);
        previewSize = parameters.getPreviewSize();
        //***************

        int max = 15;
        List<Integer> fps = parameters.getSupportedPreviewFrameRates();
        {
            int maxFps = Integer.MIN_VALUE;
            for (Integer i : fps) {
                if (i.intValue() > maxFps) {
                    max = i.intValue();
                    maxFps = max;
                }
            }
        }
        Log.i("MAX FPS", String.valueOf(max));
        parameters.setPreviewFrameRate(max);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_SHADE);
        parameters.setPreviewFormat(ImageFormat.YV12);

        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mCamera = Camera.open();
        try {
            mCamera.cancelAutoFocus();
            mCamera.setPreviewDisplay(holder);
            Camera.Parameters parameters = mCamera.getParameters();
            previewSize = parameters.getPreviewSize();
            pixels = new int[previewSize.width * previewSize.height];

            long time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            timeWhite = time;
            timeBlack = time;
            acumulatedTimeWhite = 0;
            acumulatedTimeBlack = 0;
            lastFrameWasWhite = false;
            //set callback for camera frames
            SharedPreferences sp = getContext().getSharedPreferences("CamReader", 0);
            lightAccurate = sp.getInt("lightAccurate", 60);

            mCamera.setPreviewCallback(new Camera.PreviewCallback() {

                public void onPreviewFrame(byte[] data, Camera camera) {
                    // TODO Auto-generated method stub
                    //Log.i("antes", "width: " + String.valueOf(previewSize.width) + " height: " + String.valueOf(previewSize.height));


                    if (mTopView == null || mFinished) {
                        return;
                    }

                    if (mTopView.mBitmap == null) {
                        Camera.Parameters params = mCamera.getParameters();
                        mTopView.mImageHeight = params.getPreviewSize().width;
                        mTopView.mImageWidth = params.getPreviewSize().height;
                        //mTopView.mBitmap = Bitmap.createBitmap(mTopView.mImageWidth, mTopView.mImageHeight, Bitmap.Config.ARGB_8888);
                        //mTopView.mYUVData = new byte[data.length];
                    }
                    //System.arraycopy(data, 0, mTopView.mYUVData, 0, data.length);


                    decodeYUV420SP(pixels, data, previewSize.width, previewSize.height);

                    String dato = Integer.toHexString(getMiddlePixel());
                    String red = dato.substring(2, 4);
                    String green = dato.substring(4, 6);
                    String blue = dato.substring(6, 8);

                    int ired = Integer.parseInt(red, 16);
                    int igreen = Integer.parseInt(green, 16);
                    int iblue = Integer.parseInt(blue, 16);

                    String sred = String.valueOf(ired * 100 / 255) + "%";
                    String sgreen = String.valueOf(igreen * 100 / 255) + "%";
                    String sblue = String.valueOf(iblue * 100 / 255) + "%";

                    int iwhite = (ired + igreen + iblue) * 100 / 765;
                    String swhite = String.valueOf(iwhite);

                    long time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
                    if (iwhite > lightAccurate) {
                        //Log.i("white", String.valueOf(iwhite));
                        if (!lastFrameWasWhite) {
                            //mTopView.msg = String.valueOf(acumulatedTimeWhite);
                            //Log.i("note", "duration of black: "  + String.valueOf(acumulatedTimeBlack));
                            if(acumulatedTimeBlack  > 3500)
                                tempMsg = "";

                            if (isBreak(acumulatedTimeBlack)) {
                                translateMorse();
                            } else if(isWhiteSpace(acumulatedTimeBlack)){
                                translateMorse();
                                mTopView.msg += " ";
                            }
                        }
                        timeWhite = lastFrameWasWhite ? timeWhite : time;
                        acumulatedTimeWhite = time - timeWhite;
                        acumulatedTimeBlack = 0;
                        lastFrameWasWhite = true;
                    } else {
                        if (lastFrameWasWhite) {
                            //mTopView.msg = String.valueOf(acumulatedTimeWhite);
                            //Log.i("note", "duration of white: " + String.valueOf(acumulatedTimeWhite));

                            tempMsg += examineWhiteDuration(acumulatedTimeWhite);
                        }
                        if(tempMsg != "" && acumulatedTimeBlack > 2500) {
                            translateMorse();
                        }
                        timeBlack = !lastFrameWasWhite ? timeBlack : time;
                        acumulatedTimeBlack = time - timeBlack;
                        acumulatedTimeWhite = 0;
                        lastFrameWasWhite = false;
                    }
                    mTopView.invalidate();

/*
                    Log.i(String.valueOf(previewSize.width) + " - " + String.valueOf(previewSize.height), "Mid pixel: " + dato
                            + " red: " + red
                            + " green: " + green
                            + " blue: " + blue
                            + " sred: " + sred
                            + " sgreen: " + sgreen
                            + " sblue: " + sblue
                            + " swhite: " + swhite);
                            */
                }
            });
        } catch (Exception e) {
            mCamera.release();
            mCamera = null;
        }

    }

    private void translateMorse(){
        if (letterTable.containsKey(tempMsg)) {
            mTopView.msg += letterTable.get(tempMsg);
        } else {
            mTopView.msg += "@";
        }
        tempMsg="";
    }

    private boolean isBreak(long duration) {
        return duration > 400 && duration < 850;
    }

    private boolean isWhiteSpace(long duration) {
        return duration > 1300 && duration < 2000;
    }

    private String examineWhiteDuration(long duration) {
        if (duration < 250) {
            return ".";
        }

        if (duration > 300) {
            return "-";
        }

        return "@";

    }

    void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {

        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143)
                    b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

    public int getMiddlePixel() {
        return getPixel(previewSize.height / 2, previewSize.width / 2);
    }

    public int getPixel(int x, int y) {
        return pixels[previewSize.width * x + y];
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mFinished = true;
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

    }

}
