package com.dparseh.samiee.shaderecognition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.LogPrinter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import boofcv.alg.color.ColorHsv;
import boofcv.android.ConvertBitmap;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import georegression.metric.UtilAngle;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainActivity extends Activity implements BottomNavigation.OnMenuItemSelectionListener{

    private TextView mTextMessage;
    private ImageView smilePicView;
    private BottomNavigation mBottomNavigation;
    private TextView textView;
    private Button detectButton;

    //For marker motion
    private ViewGroup markerLayout;

    private ImageView marker;

    private int xDelta;
    private int yDelta;
    private int receiverVisibleToInstantApps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().hide();
        setContentView(R.layout.activity_main);

        mBottomNavigation = (BottomNavigation) findViewById(R.id.BottomNavigation);
        if (null != mBottomNavigation) {
//            Typeface typeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
            mBottomNavigation.setOnMenuItemClickListener(this);
//            mBottomNavigation.setDefaultTypeface(typeface);
        }


//        final BadgeProvider provider = mBottomNavigation.getBadgeProvider();
//        provider.show(R.id.navigation_home);
//        bottomNavigationView.getBadgeProvider().remove(R.id.navigation_home);


        mTextMessage = findViewById(R.id.message);
        textView = findViewById(R.id.textView);
        markerLayout =  (RelativeLayout) findViewById(R.id.markerLayout);
        marker = (ImageView) findViewById(R.id.marker);
        smilePicView = findViewById(R.id.SmilePic);
        final Bitmap bitmap = ((BitmapDrawable)smilePicView.getDrawable()).getBitmap();
//        smilePicView.setOnTouchListener(new View.OnTouchListener(){
//            @Override
//            public boolean onTouch(View v, MotionEvent event){
//                int x = (int)event.getX();
//                int y = (int)event.getY();
//                int pixel = bitmap.getPixel(x,y);
//
//                //then do what you want with the pixel data, e.g
//                int redValue = Color.red(pixel);
//                int blueValue = Color.blue(pixel);
//                int greenValue = Color.green(pixel);
//
//
//                float[] hslColor = new float[3];
//
//                Color.RGBToHSV(redValue, greenValue, blueValue, hslColor);
//
//
//
//                showSelectedColor("Selected",bitmap,hslColor[0],hslColor[1]);
//
//                Log.e("redValue", redValue+"");
//                Log.e("greenValue", greenValue+"");
//                Log.e("blueValue", blueValue+"");
//
//\
//
//
//
//                return false;
//            }
//        });


        marker.setOnTouchListener(onMarkerTouchListener());

        detectButton = findViewById(R.id.detectButton);
        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int[] posXY = new int[2];
                markerLayout.getLocationOnScreen(posXY);
                int[] markerPosXY = new int[2];
                marker.getLocationOnScreen(markerPosXY);

                int xPos = markerPosXY[0] - posXY[0];
                int yPos = markerPosXY[1] - posXY[1];



//                ViewGroup frame = findViewById(R.id.frameLayout);
//                FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams( frame.getLayoutParams());
//                xPos = frameParams.leftMargin;
//                yPos = frameParams.topMargin;

                Log.e("markerLayoutPosx: ", posXY[0]+"");
                Log.e("markerLayoutPosy: ", posXY[1]+"");
                Log.e("markerPosx: ", markerPosXY[0]+"");
                Log.e("markerPosy: ", markerPosXY[1]+"");

                printLab(xPos, yPos);

            }
        });


    }

    @Override
    public void onMenuItemSelect(int i, int i1, boolean b) {
        mTextMessage = findViewById(R.id.message);
        switch (i) {
            case R.id.navigation_home :
                mTextMessage.setText(R.string.title_home);
                break;
            case R.id.navigation_dashboard:
                mTextMessage.setText(R.string.title_dashboard);
                break;
            case R.id.navigation_notifications:
                mTextMessage.setText(R.string.title_notifications);
                break;
            case R.id.shade_recognition:
                mTextMessage.setText(R.string.title_shade);
                break;
        }
    }

    @Override
    public void onMenuItemReselect(int i, int i1, boolean b) {
       //TODO
    }


    private View.OnTouchListener onMarkerTouchListener() {
        return new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();

                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:
//                        Toast.makeText(MainActivity.this,
//                                "thanks for new location!", Toast.LENGTH_SHORT)
//                                .show();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        ViewGroup frame = findViewById(R.id.frameLayout);
                        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams( frame.getLayoutParams());
                        int leftM = x - xDelta;
                        int topM = y - yDelta;
                        if (leftM < frameParams.leftMargin) leftM = frameParams.leftMargin;
                        if (topM < frameParams.topMargin) topM = frameParams.topMargin;
                        layoutParams.leftMargin = leftM;
                        layoutParams.topMargin = topM;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;

                }
                markerLayout.invalidate();
                return true;

            }
        };
    }
    private void printLab(int xPos, int yPos){

        Bitmap bitmap = ((BitmapDrawable)smilePicView.getDrawable()).getBitmap();
//        smilePicView.setImageBitmap(BitmapFactory.decodeFile("input/smile_pic.bmp")); another choice
//        mTextMessage = findViewById(R.id.message);
//                        mTextMessage.setText("Touch coordinates : " +
//                                String.valueOf(event.getX()) + "x" + String.valueOf(event.getY())+"\n");
//                        ImageView imageView = ((ImageView)v);
//                        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//                        int x = (int)event.getX();
//                        int y = (int)event.getY();
//                        if (x<0) x *= (-1);
//                        if (y<0) y *= (-1);

//        int pixel = bitmap.getPixel(100,100);
//
//        int redValue = Color.red(pixel);
//        int blueValue = Color.blue(pixel);
//        int greenValue = Color.green(pixel);

        int redValue, blueValue, greenValue;
        int color = calculateDominantColor(bitmap, xPos, yPos, 0);
        redValue = Color.red(color);
        blueValue = Color.blue(color);
        greenValue = Color.green(color);

        Log.e("redValue", redValue+"");
        Log.e("greenValue", greenValue+"");
        Log.e("blueValue", blueValue+"");

        Log.e("width", bitmap.getWidth()+"");
        Log.e("height", bitmap.getHeight()+"");

        int[] lab = {0, 0, 0};
        rgb2lab(redValue, greenValue, blueValue, lab);

        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        marker.startAnimation(animFadeIn);
        textView.setText("L: "+lab[0] +" a:"+lab[1]+" b:"+lab[2]);
    }

    public int calculateDominantColor(Bitmap bitmap, int xPos, int yPos, int pixelSpacing) {

        Rect rect = marker.getDrawable().getBounds();

        int width = rect.width();
        int height = rect.height();

        int[] pixels = new int[width * height];
        Log.e("location", "Location is: " + xPos + " , "+ yPos);
        Log.e("width & height", width+" & " + height);
        Log.e("bitmap width", bitmap.getWidth()+"");
        Log.e("bitmap height", bitmap.getHeight()+"");
        bitmap.getPixels(pixels, 0, width, xPos, yPos, width, height);
//        Log.e("ColorSpace", "CS: "+bitmap.getColorSpace()+"");
        if (null == bitmap) return Color.TRANSPARENT;

        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int alphaBucket = 0;

        boolean hasAlpha = bitmap.hasAlpha();
        Log.e("alpha", "has Alpha: "+hasAlpha+"");
        int pixelCount = width * height;

        for (int y = 0, h = width ; y < h; y++)
        {
            for (int x = 0, w = height; x < w; x++)
            {
                int color = pixels[x + y * w]; // x + y * width
                redBucket += (color >> 16) & 0xFF; // Color.red
                greenBucket += (color >> 8) & 0xFF; // Color.green
                blueBucket += (color & 0xFF); // Color.blue
                if (hasAlpha) alphaBucket += (color >>> 24); // Color.alpha
            }
        }

        return Color.argb(
                (hasAlpha) ? (alphaBucket / pixelCount) : 255,
                redBucket / pixelCount,
                greenBucket / pixelCount,
                blueBucket / pixelCount);

    }
    private void rgb2lab(int R, int G, int B, int[] lab) {

        float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
        float Ls, as, bs;
        float eps = 216.f/24389.f;
        float k = 24389.f/27.f;

        float Xr = 0.964221f;  // reference white D50
        float Yr = 1.0f;
        float Zr = 0.825211f;

        // RGB to XYZ

        r = R/255.f; //R 0..1
        g = G/255.f; //G 0..1
        b = B/255.f; //B 0..1

        // assuming sRGB (D65)
        if (r <= 0.04045)
            r = r/12;
        else
            r = (float) Math.pow((r+0.055)/1.055,2.4);

        if (g <= 0.04045)
            g = g/12;
        else
            g = (float) Math.pow((g+0.055)/1.055,2.4);

        if (b <= 0.04045)
            b = b/12;
        else
            b = (float) Math.pow((b+0.055)/1.055,2.4);


        X =  0.436052025f*r     + 0.385081593f*g + 0.143087414f *b;
        Y =  0.222491598f*r     + 0.71688606f *g + 0.060621486f *b;
        Z =  0.013929122f*r     + 0.097097002f*g + 0.71418547f  *b;

        // XYZ to Lab
        xr = X/Xr;
        yr = Y/Yr;
        zr = Z/Zr;

        if ( xr > eps )
            fx =  (float) Math.pow(xr, 1/3.);
        else
            fx = (float) ((k * xr + 16.) / 116.);

        if ( yr > eps )
            fy =  (float) Math.pow(yr, 1/3.);
        else
            fy = (float) ((k * yr + 16.) / 116.);

        if ( zr > eps )
            fz =  (float) Math.pow(zr, 1/3.);
        else
            fz = (float) ((k * zr + 16.) / 116);

        Ls = ( 116 * fy ) - 16;
        as = 500*(fx-fy);
        bs = 200*(fy-fz);

        lab[0] = (int) (2.55*Ls + .5);
        lab[1] = (int) (as + .5);
        lab[2] = (int) (bs + .5);
    }

    /**
     * Selectively displays only pixels which have a similar hue and saturation values to what is provided.
     * This is intended to be a simple example of color based segmentation.  Color based segmentation can be done
     * in RGB color, but is more problematic due to it not being intensity invariant.  More robust techniques
     * can use Gaussian models instead of a uniform distribution, as is done below.
     */
    public void showSelectedColor( String name , Bitmap bitmap , float hue , float saturation ) {
        Planar<GrayF32> input = ConvertBitmap.bitmapToMS(bitmap, null, GrayF32.class, null);
        Planar<GrayF32> hsv = input.createSameShape();

        // Convert into HSV
        ColorHsv.rgbToHsv_F32(input,hsv);

        // Euclidean distance squared threshold for deciding which pixels are members of the selected set
        float  maxDist2 = 0.4f*0.4f;


        // Extract hue and saturation bands which are independent of intensity
        GrayF32 H = hsv.getBand(0);
        GrayF32 S = hsv.getBand(1);

        // Adjust the relative importance of Hue and Saturation.
        // Hue has a range of 0 to 2*PI and Saturation from 0 to 1.
        float adjustUnits = (float)(Math.PI/2.0);

        // step through each pixel and mark how close it is to the selected color
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());



//        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                : Bitmap.Config.RGB_565;
//        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            int counter=0;
//        BufferedImage output = new BufferedImage(input.width,input.height,BufferedImage.TYPE_INT_RGB);
        for( int y = 0; y < hsv.height; y++ ) {
            for( int x = 0; x < hsv.width; x++ ) {
                // Hue is an angle in radians, so simple subtraction doesn't work
                float dh = UtilAngle.dist(H.unsafe_get(x,y),hue);
                float ds = (S.unsafe_get(x,y)- saturation)*adjustUnits;

                // this distance measure is a bit naive, but good enough for to demonstrate the concept
                float dist2 = dh*dh + ds*ds;
                if( dist2 <= maxDist2 ) {
                    outputBitmap.setPixel(x,y,bitmap.getPixel(x, y));
                    counter++;
                }
            }
        }

        Log.e("set pixel", counter+"");

        smilePicView.setImageBitmap(outputBitmap);


    }
}

