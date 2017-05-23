package com.example.org.gvrfapplication;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.utility.Log;
import org.joml.Vector2f;
import org.joml.Vector3f;
import android.view.MotionEvent;
import org.gearvrf.scene_objects.GVRCubeSceneObject;

import java.util.Random;


public class StackMain extends GVRMain
{
    //-------------------------------------------------------------------------
    // constants
    //-------------------------------------------------------------------------

    private enum State {
        NONE,
        START,
        PLAYING,
        GAME_OVER;
    }

    public static final float BLOCK_HEIGHT = 0.05f;
    public static final float START_WIDTH = 0.5f;
    public static final float START_DEPTH = 0.5f;


    //-------------------------------------------------------------------------
    // variables
    //-------------------------------------------------------------------------

    private GVRContext mContext;
    private GVRScene mScene = null;
    private State mState = State.NONE;
    private int mStackHeight = 0;
    private Vector2f mCurrentDimensions = new Vector2f(START_WIDTH, START_DEPTH);
    private GVRMesh mBlockMesh;


    //-------------------------------------------------------------------------
    // inhereted funcs
    //-------------------------------------------------------------------------

    @Override
    public void onInit(GVRContext context)
    {
        mContext = context;
        mScene = mContext.getMainScene();

        initCamera();
        initReticle();
        initScene();

        startGame();
    }


    @Override
    public void onStep()
    {
    }


    //-------------------------------------------------------------------------
    // public funcs
    //-------------------------------------------------------------------------

    public void onTouchEvent(MotionEvent event)
    {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;

            default:
                break;
        }
    }


    //-------------------------------------------------------------------------
    // private funcs
    //-------------------------------------------------------------------------

    private void setState(State state)
    {
        if (state != mState) {
            mState = state;
            Log.d("Stack", "StackMain.setState("+mState+")");
        }
    }


    private void initCamera()
    {
        mScene.getMainCameraRig().getLeftCamera().setBackgroundColor(0.5f, 0.5f, 1.0f, 1.0f);
        mScene.getMainCameraRig().getRightCamera().setBackgroundColor(0.5f, 0.5f, 1.0f, 1.0f);
        mScene.getMainCameraRig().getTransform().setPositionX(2.0f);
    }


    private void initReticle()
    {
        //GVRSceneObject headTracker = new GVRSceneObject(context,
        //        context.createQuad(0.1f, 0.1f),
        //        context.loadTexture(new GVRAndroidResource(context, R.drawable.headtrackingpointer)));
        //headTracker.getTransform().setPosition(0.0f, 0.0f, -1.0f);
        //headTracker.getRenderData().setDepthTest(false);
        //headTracker.getRenderData().setRenderingOrder(100000);
        //mScene.getMainCameraRig().addChildObject(headTracker);
    }


    private void initScene()
    {
        createLights();

        mBlockMesh = new GVRCubeSceneObject(mContext, true).getRenderData().getMesh();
    }


    private void createLights()
    {
        GVRSceneObject lightObject = new GVRSceneObject(mContext);
        GVRPointLight light = new GVRPointLight(mContext);
        float ambientIntensity = 0.5f;
        float diffuseIntensity = 1.0f;
        light.setAmbientIntensity(1.0f * ambientIntensity, 0.95f * ambientIntensity, 0.83f * ambientIntensity, 0.0f);
        light.setDiffuseIntensity(1.0f * diffuseIntensity, 0.95f * diffuseIntensity, 0.83f * diffuseIntensity, 0.0f);
        light.setSpecularIntensity(0.0f, 0.0f, 0.0f, 0.0f);
        lightObject.getTransform().setPosition(-10.0f, 5.0f, 20.0f);
        lightObject.attachLight(light);
        mScene.addSceneObject(lightObject);

        GVRSceneObject lightObject2 = new GVRSceneObject(mContext);
        GVRPointLight light2 = new GVRPointLight(mContext);
        float ambientIntensity2 = 0.5f;
        float diffuseIntensity2 = 1.0f;
        light2.setAmbientIntensity(1.0f * ambientIntensity2, 0.95f * ambientIntensity2, 0.83f * ambientIntensity2, 0.0f);
        light2.setDiffuseIntensity(1.0f * diffuseIntensity2, 0.95f * diffuseIntensity2, 0.83f * diffuseIntensity2, 0.0f);
        light2.setSpecularIntensity(0.0f, 0.0f, 0.0f, 0.0f);
        lightObject2.getTransform().setPosition(10.0f, 5.0f, 20.0f);
        lightObject2.attachLight(light2);
        mScene.addSceneObject(lightObject2);

        GVRSceneObject directLightObject = new GVRSceneObject(mContext);
        GVRDirectLight directLight = new GVRDirectLight(mContext);
        directLight.setCastShadow(true);
        float ambientIntensity3 = 0.1f;
        float diffuseIntensity3 = 1.0f;
        directLight.setAmbientIntensity(1.0f * ambientIntensity3, 0.95f * ambientIntensity3, 0.83f * ambientIntensity3, 0.0f);
        directLight.setDiffuseIntensity(1.0f * diffuseIntensity3, 0.95f * diffuseIntensity3, 0.83f * diffuseIntensity3, 0.0f);
        directLight.setSpecularIntensity(0.0f, 0.0f, 0.0f, 0.0f);
        directLightObject.getTransform().setPosition(0.0f, 10.0f, 2.0f);
        directLightObject.attachLight(directLight);
    }


    private void startGame()
    {
       float height = mStackHeight * BLOCK_HEIGHT;
        Vector3f dimensions = new Vector3f(mCurrentDimensions.x(), BLOCK_HEIGHT, mCurrentDimensions.y());
        Block startBlock = new Block(mContext, dimensions);
        mStackHeight = 1;

        // TODO: create a block
        GVRSceneObject blockObject = new GVRSceneObject(mContext, mBlockMesh);
        GVRRenderData rdata = blockObject.getRenderData();
        blockObject.setName("block "+mStackHeight);
        rdata.setShaderTemplate(GVRPhongShader.class);
        rdata.setAlphaBlend(true);
        GVRMaterial material = new GVRMaterial(mContext);
        material.setAmbientColor(0.2f, 1.0f, 0.2f, 1.0f);
        material.setDiffuseColor(1.0f, 0.0f, 0.0f, 0.5f);
        rdata.setMaterial(material);
        blockObject.attachComponent(startBlock);
        rdata.setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        blockObject.getTransform().setPosition(0.0f, -2.0f, -2.0f);
        mScene.addSceneObject(blockObject);

        setState(State.START);
    }

}


