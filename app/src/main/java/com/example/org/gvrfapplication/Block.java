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

    public static final float MOVE_SPEED = 1.0f;
    public static final float MOVE_DOWN_SPEED = 1.0f;


    //-------------------------------------------------------------------------
    // variables
    //-------------------------------------------------------------------------

    private boolean mAnimating = false;
    private boolean mMoveAlongX = true;
    private boolean mMoveForward= true;
    private float mMoveLimit = 2.0f;
    private float mBlockHeight = 0f;
    private float mTargetHeight = 0f;
    private float mGameSpeed = 0f;


    //-------------------------------------------------------------------------
    // public funcs
    //-------------------------------------------------------------------------

    Block(GVRContext context, boolean moveAlongX, float blockHeight, float moveLimit, float gameSpeed) {
        super(context);
        mMoveAlongX = moveAlongX;
        mBlockHeight = blockHeight;
        mMoveLimit = moveLimit;
        mGameSpeed = gameSpeed;
    }

    public void onDrawFrame(float frameTime)
    {
        if (mAnimating) {
            animate(frameTime);
        }
        if (mTargetHeight != 0f) {
            animateDown(frameTime);
        }
    }

    public void setAnimating(boolean animating)
    {
        mAnimating = animating;
    }

    public void moveDown()
    {
        mTargetHeight = getOwnerObject().getTransform().getPositionY() - mBlockHeight;
    }


    //-------------------------------------------------------------------------
    // private funcs
    //-------------------------------------------------------------------------

    private void animateDown(float frameTime) {
        float y = getOwnerObject().getTransform().getPositionY();
        y -= MOVE_DOWN_SPEED * frameTime;
        if (y < mTargetHeight) {
            y = mTargetHeight;
            mTargetHeight = 0f;
        }
        getOwnerObject().getTransform().setPositionY(y);
    }

    private void animate(float frameTime)
    {
        if (mMoveAlongX) {
            float x = getOwnerObject().getTransform().getPositionX();
            if (mMoveForward) {
                if (x > mMoveLimit) {
                    mMoveForward = false;
                } else {
                    x += mGameSpeed * frameTime;
                }
            }
            else {
                if (x < -mMoveLimit) {
                    mMoveForward = true;
                } else {
                    x -= mGameSpeed * frameTime;
                }
            }
           getOwnerObject().getTransform().setPositionX(x);
        }
        else {
            float z = getOwnerObject().getTransform().getPositionZ();
            if (mMoveForward) {
                if (z > mMoveLimit) {
                    mMoveForward = false;
                } else {
                    z += mGameSpeed * frameTime;
                }
            }
            else {
                if (z < -mMoveLimit) {
                    mMoveForward = true;
                } else {
                    z -= mGameSpeed * frameTime;
                }
            }
            getOwnerObject().getTransform().setPositionZ(z);
         }
    }

}


