package com.example.org.gvrfapplication;

import org.gearvrf.GVRContext;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.gearvrf.GVRBehavior;
import org.gearvrf.utility.Log;


public class Block extends GVRBehavior {

    //-------------------------------------------------------------------------
    // constants
    //-------------------------------------------------------------------------

    public static final float MOVE_LIMIT = 2.0f;
    public static final float MOVE_SPEED = 1.0f;


    //-------------------------------------------------------------------------
    // variables
    //-------------------------------------------------------------------------

    private boolean mAnimating = false;
    private boolean mMoveAlongX = true;
    private boolean mMoveForward= true;


    //-------------------------------------------------------------------------
    // public funcs
    //-------------------------------------------------------------------------

    Block(GVRContext context, boolean moveAlongX) {
        super(context);
        mMoveAlongX = moveAlongX;
    }

    public void onDrawFrame(float frameTime)
    {
        if (mAnimating) {
            animate(frameTime);
        }
    }

    public void setAnimating(boolean animating)
    {
        mAnimating = animating;
    }


    //-------------------------------------------------------------------------
    // private funcs
    //-------------------------------------------------------------------------

    private void animate(float frameTime)
    {
        if (mMoveAlongX) {
            float x = getOwnerObject().getTransform().getPositionX();
            if (mMoveForward) {
                if (x > MOVE_LIMIT) {
                    mMoveForward = false;
                } else {
                    x += MOVE_SPEED * frameTime;
                }
            }
            else {
                if (x < -MOVE_LIMIT) {
                    mMoveForward = true;
                } else {
                    x -= MOVE_SPEED * frameTime;
                }
            }
           getOwnerObject().getTransform().setPositionX(x);
        }
        else {
            float z = getOwnerObject().getTransform().getPositionZ();
            if (mMoveForward) {
                if (z > MOVE_LIMIT) {
                    mMoveForward = false;
                } else {
                    z += MOVE_SPEED * frameTime;
                }
            }
            else {
                if (z < -MOVE_LIMIT) {
                    mMoveForward = true;
                } else {
                    z -= MOVE_SPEED * frameTime;
                }
            }
            getOwnerObject().getTransform().setPositionZ(z);
         }
    }

}


