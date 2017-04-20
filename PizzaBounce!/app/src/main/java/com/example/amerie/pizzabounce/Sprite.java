/*
 * CS 193A, Winter 2015, Marty Stepp
 * This class is a helper that can be used to represent sprites,
 * which are in-game objects that can move around.
 * This class is fairly simple and is just meant to gather together
 * several variables that would be associated with in-game sprite objects
 * such as x/y location, width/height size, dx/dy velocity, and Paint color.
 */

package com.example.amerie.pizzabounce;

import android.content.Context;
import android.graphics.*;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Random;

public class Sprite implements Parcelable {
    // state (fields)
    private static int SPRITE_SIZE = 300;
    public RectF rect = new RectF();
    public float x = 0;
    public float y = 0;
    public float dx = 0;
    public float dy = 0;
    public float da = 1;
    public float a = 0;
    public boolean inMotion = true;
    public Paint paint = new Paint();
    int picnum = 0;
    public boolean touched = false;

    /* Constructs a default empty sprite. */
    public Sprite() {
        setLocation(x,y);
        setSize(SPRITE_SIZE,SPRITE_SIZE);
    }

    /* Constructs a sprite of the given location and size. */
    public Sprite(float init_x, float init_y) {
        setLocation(init_x, init_y);
        setSize(SPRITE_SIZE, SPRITE_SIZE);
        setVelocity(0,0);
        Random rn = new Random();
        picnum = rn.nextInt(6);
    }

    /* Tells the sprite to move itself by its current velocity dx,dy. */
    public void move() {
        rect.offset(dx, dy);
    }

    /* Sets the sprite's x,y location on screen to be the given values. */
    public void setLocation(float new_x, float new_y) {
        x = new_x;
        y = new_y;
        rect.offsetTo(x, y);
    }

    /* Sets the sprite's size to be the given values. */
    public void setSize(float width, float height) {
        rect.right = rect.left + width;
        rect.bottom = rect.top + height;
    }

    /* Sets the sprites dx,dy velocity to be the given values. */
    public void setVelocity(float new_dx, float new_dy) {
        dx = new_dx;
        dy = new_dy;
    }

    public void pause() {
        dy = 0;
        dx = 0;
        da = 0;
        inMotion = false;
    }

    // Parcelling functions for saving state of Canvas View list
    public Sprite(Parcel in){
        super();
        try {
            x = in.readFloat();
            y = in.readFloat();
            dx = in.readFloat();
            dy = in.readFloat();
            da = in.readFloat();
            a = in.readFloat();
            picnum = in.readInt();
        } catch (Exception e) {
            //empty
        }
        setLocation(x,y);
        setVelocity(dx,dy);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.x);
        dest.writeFloat(this.y);
        dest.writeFloat(this.dx);
        dest.writeFloat(this.dy);
        dest.writeFloat(this.da);
        dest.writeFloat(this.a);
        dest.writeInt(this.picnum);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Sprite createFromParcel(Parcel in) {
            return new Sprite(in);
        }
        public Sprite[] newArray(int size) {
            return new Sprite[size];
        }
    };

}
