package com.example.amerie.pizzabounce;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import java.util.ArrayList;

public class CanvasView extends View {

    private static float MAX_SPEED = 50;
    private static int SPRITE_SIZE = 300;

    public ArrayList<Sprite> Sprites = new ArrayList<>(); //Can contain at most 10 sprites
    private DrawingThread dthread;

    // required constructor
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);

        // start a drawing thread to animate screen at 50 frames/sec
        dthread = new DrawingThread(this, 50);
        dthread.start();

        // set up initial ball
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Sprite ball = new Sprite((width/4) + (SPRITE_SIZE/2),(height/4) + SPRITE_SIZE/2);
        Sprites.add(ball);

    }

    private VelocityTracker mVelocityTracker = null;

    //Create a new sprite when the user touches the screen.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        boolean new_sprite = true;
        if(Sprites != null && !Sprites.isEmpty()) {
            for (Sprite ball : Sprites) {
                RectF target_rect = new RectF();
                target_rect.right = ball.rect.right + 5;
                target_rect.left = ball.rect.left - 5;
                target_rect.top = ball.rect.top - 5;
                target_rect.bottom = ball.rect.bottom + 5;
                if (target_rect.contains(x, y)) {
                    new_sprite = false;
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ball.pause();
                        ball.touched = true;
                        if (mVelocityTracker == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            mVelocityTracker = VelocityTracker.obtain();
                        } else {
                            // Reset the velocity tracker back to its initial state.
                            mVelocityTracker.clear();
                        }
                        mVelocityTracker.addMovement(event);
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (mVelocityTracker == null) {
                            // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                            mVelocityTracker = VelocityTracker.obtain();
                        }
                        mVelocityTracker.addMovement(event);
                        // Determine velocity
                        mVelocityTracker.computeCurrentVelocity(100);
                        float vx = mVelocityTracker.getXVelocity();
                        float vy = mVelocityTracker.getYVelocity();
                        if (Math.abs(vx) > 100 | Math.abs(vy) > 100) {
                            ball.inMotion = true;
                            ball.dy = Math.min(vy / 10, MAX_SPEED);
                            ball.dx = Math.min(vx / 10, MAX_SPEED);
                            ball.da = Math.min((vx / Math.max(vy, 100)) * 10, MAX_SPEED / 10);
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        ball.inMotion = true;
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        // Return a VelocityTracker object back to be re-used by others.
                        mVelocityTracker.recycle();
                        ball.inMotion = true;
                    }
                } else {
                    if (ball.touched) {
                        ball.inMotion = true;
                        ball.touched = false;
                    }
                }
            }
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN & new_sprite & Sprites.size() < 10) {
            // set up initial state of ball
            if (x + (SPRITE_SIZE/2) >= getWidth()) {
                x = getWidth() - (SPRITE_SIZE/2) - 1;
            }
            if (x - (SPRITE_SIZE/2) <= 0) {
                x = (SPRITE_SIZE/2) + 1;
            }
            if (y + (SPRITE_SIZE/2) >= getHeight()) {
                y = getHeight() - (SPRITE_SIZE/2) - 1;
            }
            if (y - (SPRITE_SIZE/2) <= 0) {
                y = (SPRITE_SIZE/2) + 1;
            }
            Sprite ball = new Sprite(x - (SPRITE_SIZE/2), y - (SPRITE_SIZE/2));
            Sprites.add(ball);
        }

        return true;
    }

    private Context c = getContext();
    private Bitmap pics[] = {
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.pepperoni), 300, 300, false),
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.cheese), 300, 300, false),
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.veggie), 300, 300, false),
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.gourmet), 300, 300, false),
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.basil), 300, 300, false),
            Bitmap.createScaledBitmap(BitmapFactory.decodeResource(c.getResources(), R.drawable.none), 300, 300, false)
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Sprites != null && !Sprites.isEmpty()) {
            for (Sprite ball : Sprites) {
                Matrix rotator = new Matrix();
                // rotate around x,y
                // NOTE: coords in bitmap-space!
                rotator.postRotate(ball.a % 360, pics[ball.picnum].getWidth() / 2, pics[ball.picnum].getHeight() / 2);

                // to set the position in canvas where the bitmap should be drawn to;
                // NOTE: coords in canvas-space!
                float xTranslate = ball.rect.left;
                float yTranslate = ball.rect.top;
                rotator.postTranslate(xTranslate, yTranslate);

                canvas.drawBitmap(pics[ball.picnum], rotator, ball.paint);
                updateSprite(ball);
            }
        }
    }

    // updates sprites' positions between frames of animation
    private void updateSprite(Sprite ball) {
        // handle ball bouncing off edges
        if (ball.rect.left < 0 || ball.rect.right >= getWidth()) {
            ball.dx = -ball.dx;
        }
        if (ball.rect.top <= 0) {
            ball.dy = Math.abs(ball.dy); //Make velocity positive
        } else if (ball.rect.bottom >= getHeight()) {
            ball.dy = -1 * Math.abs(ball.dy); //Make velocity negative
        } else {
            //update vertical velocity for gravity
            if (ball.inMotion) {
                ball.dy += 0.5;
            }
        }
        ball.a += ball.da;
        ball.move();
    }

    private static final String EXTRA_EVENT_LIST = "event_list";
    private static final String EXTRA_STATE = "instance_state";

    @Override
    public Parcelable onSaveInstanceState() {
        System.out.println("save instance");
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_STATE, super.onSaveInstanceState());
        bundle.putParcelableArrayList(EXTRA_EVENT_LIST, Sprites);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(EXTRA_STATE));
            Sprites = bundle.getParcelableArrayList(EXTRA_EVENT_LIST);
            if (Sprites == null || Sprites.isEmpty()) {
                Sprites = new ArrayList<>();
            }
            return;
        }
        super.onRestoreInstanceState(state);
    }
}