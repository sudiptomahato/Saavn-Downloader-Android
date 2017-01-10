package com.arunkr.saavn.downloader.activity_frag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arunkr.saavn.downloader.R;
import com.arunkr.saavn.downloader.TouchImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arun Kumar Shreevastava on 25/10/16.
 */

public class HelpActivity extends AppCompatActivity
{
    private TouchImageView iv = null;
    List<Integer> thumbs;
    public int index=0;
    Target target;
    ImageButton leftBtn,rightBtn;
    String[] caption;
    TextView txtCaption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_help);


        ActionBar actionBar = getSupportActionBar();
        try {
            if (actionBar.isShowing())
                actionBar.hide();
        } catch (Exception e) {
            Log.e("actionbar", e.getMessage());
        }

        iv = (TouchImageView)findViewById(R.id.large_image_iv);
        txtCaption = (TextView)findViewById(R.id.caption);
        leftBtn = (ImageButton)findViewById(R.id.imgBtn_left);
        rightBtn = (ImageButton)findViewById(R.id.imgBtn_right);

        leftBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSwipeRight();
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onSwipeLeft();
            }
        });

        iv.setOnTouchListener(new OnSwipeTouchListener(this));

        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                iv.setImageBitmap(bitmap);
                fadeInImage(iv);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable)
            {
                iv.setImageDrawable(errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {
                fadeOutImage(iv);
                //iv.setImageDrawable(placeHolderDrawable);
            }
        };

        caption = getResources().getStringArray(R.array.instructions_caption);
        setAdapter();
        setImage(thumbs.get(index));

        setIcon(R.id.imgBtn_left);
        setIcon(R.id.imgBtn_right);

    }

    private void setIcon(int id)
    {
        ImageButton button = (ImageButton)findViewById(id);
        button.setBackgroundColor(Color.argb(63, 63, 63, 63));
    }

    private void fadeOutImage(final TouchImageView img)
    {
        Animation fadeOut = new AlphaAnimation(1.0f, 0.4f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);

        img.startAnimation(fadeOut);
    }

    private void fadeInImage(final TouchImageView img)
    {
        Animation fadeIn = new AlphaAnimation(0.4f,1.0f);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(500);

        img.startAnimation(fadeIn);
    }

    void setAdapter()
    {
        thumbs = new ArrayList<>(4);

        thumbs.add(R.drawable.instruction_01);
        thumbs.add(R.drawable.instruction_02);
        thumbs.add(R.drawable.instruction_03);
        thumbs.add(R.drawable.instruction_04);

    }

    private void setImage(int resId)
    {
        Picasso.with(this).load(resId).into(target);
        txtCaption.setText(caption[index]);
        if(index==0) //enable right
        {
            leftBtn.setVisibility(View.GONE);
            rightBtn.setVisibility(View.VISIBLE);
        }
        else if(index == thumbs.size()-1) //enable only left
        {
            leftBtn.setVisibility(View.VISIBLE);
            rightBtn.setVisibility(View.GONE);
        }
        else
        {
            leftBtn.setVisibility(View.VISIBLE);
            rightBtn.setVisibility(View.VISIBLE);
        }
    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;
        Context context;

        public OnSwipeTouchListener(Context ctx) {
            context = ctx;
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener
        {
            private static final int SWIPE_THRESHOLD = 40;
            private static final int SWIPE_VELOCITY_THRESHOLD = 40;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                        result = true;
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                    result = true;

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
    }

    public void onSwipeTop() {

    }

    public void onSwipeRight()
    {
        index = index - 1;
        if (index < 0)
        {
            index = index + 1;
            return;
        }
        setImage(thumbs.get(index));
    }

    public void onSwipeLeft()
    {
        index = index + 1;
        if (index >= thumbs.size())
        {
            index = index - 1;
            return;
        }
        setImage(thumbs.get(index));
    }

    public void onSwipeBottom() {

    }
}
