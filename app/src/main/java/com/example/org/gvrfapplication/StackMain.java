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
        INTRO,
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
    private long mStateStartTime = 0;
    private long mStateElapsedTime = 0;
    private int mStackHeight = 0;
    private Vector2f mCurrentDimensions = new Vector2f(START_WIDTH, START_DEPTH);
    private GVRMesh mBlockMesh;
    private Block mCurrentBlock = null;
    private Block mRootBlock = null;
    private boolean mBlockDirToggle = false;


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

        startIntro();
    }


    @Override
    public void onStep()
    {
        mStateElapsedTime = System.currentTimeMillis() - mStateStartTime;

        switch(mState) {
            default:
            case NONE:
                break;
            case INTRO:
                updateIntro();
                break;
            case PLAYING:
                updatePlaying();
                break;
            case GAME_OVER:
                updateGameOver();
                break;
        }
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
            mStateStartTime = System.currentTimeMillis();
            mStateElapsedTime = 0;
            Log.d("Stack", "StackMain.setState("+mState+")");
        }
    }


    private void initCamera()
    {
        mScene.getMainCameraRig().getLeftCamera().setBackgroundColor(0.5f, 0.5f, 1.0f, 1.0f);
        mScene.getMainCameraRig().getRightCamera().setBackgroundColor(0.5f, 0.5f, 1.0f, 1.0f);
        mScene.getMainCameraRig().getTransform().setPosition(2.0f, 2.0f, 0.0f);
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

    private Block createBlock(Vector2f dimensions)
    {
        Log.d("Stack", "StackMain.createBlock() dimensions:"+dimensions+"");
        GVRSceneObject blockObject = new GVRSceneObject(mContext, mBlockMesh);
        GVRRenderData rdata = blockObject.getRenderData();
        blockObject.setName("block "+mStackHeight);
        rdata.setShaderTemplate(GVRPhongShader.class);
        rdata.setAlphaBlend(true);

        GVRMaterial material = new GVRMaterial(mContext);
        if (mStackHeight == 0) {
            material.setDiffuseColor(1.0f, 0.0f, 0.0f, 1.0f);
        }
        else if (mStackHeight == 1) {
            material.setDiffuseColor(0.0f, 1.0f, 0.0f, 1.0f);
            blockObject.getTransform().setPositionX(1.5f);
        }
        else {
            material.setDiffuseColor(0.0f, 0.0f, 1.0f, 1.0f);
        }
 //       Random rand = new Random();
//        float red = rand.nextFloat();
//        float green = rand.nextFloat();
//        float blue = rand.nextFloat();
//        //material.setAmbientColor(0.2f, 1.0f, 0.2f, 1.0f);
//        //material.setDiffuseColor(1.0f, 0.0f, 0.0f, 0.5f);
//        material.setDiffuseColor(red, green, blue, 1.0f);
        rdata.setMaterial(material);
        rdata.setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);
        //blockObject.getTransform().setScale(dimensions.x, 0.2f, dimensions.y);

        Block block = new Block(mContext, mBlockDirToggle);
        blockObject.attachComponent(block);

        return block;
    }

    private void startIntro()
    {
        mStackHeight = 0;
        mCurrentDimensions = new Vector2f(START_WIDTH, START_DEPTH);
        mRootBlock = createBlock(mCurrentDimensions);
        mScene.addSceneObject(mRootBlock.getOwnerObject());

        setState(State.INTRO);
    }

    private void updateIntro()
    {
        // TODO: wait for button press
        startPlaying();
    }

    private void startPlaying()
    {
        setState(State.PLAYING);
    }

    private void updatePlaying()
    {
        if (mCurrentBlock == null) {
            mStackHeight += 1;
            mBlockDirToggle = !mBlockDirToggle;
            mCurrentBlock = createBlock(mCurrentDimensions);
            mRootBlock.getOwnerObject().addChildObject(mCurrentBlock.getOwnerObject());
            mScene.bindShaders(mRootBlock.getOwnerObject());
            mCurrentBlock.setAnimating(true);
        }
    }

    private void startGameOver()
    {
        setState(State.GAME_OVER);
    }

    private void updateGameOver()
    {
        // TODO: wait for button press
        startIntro();
    }

}


