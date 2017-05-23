package com.example.org.gvrfapplication;

import org.gearvrf.GVRContext;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.gearvrf.GVRBehavior;


public class Block extends GVRBehavior {

    Vector3f mDimensions;


    Block(GVRContext context, Vector3f dimensions) {
        super(context);
        mDimensions = dimensions;
    }

}


