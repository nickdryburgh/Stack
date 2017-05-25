package com.example.org.gvrfapplication;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRCollider;
import org.gearvrf.GVRComponent;
import org.gearvrf.GVRDirectLight;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.GVRPointLight;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTransform;
import org.gearvrf.physics.GVRCollisionMatrix;
import org.gearvrf.physics.GVRWorld;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.utility.Log;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import org.gearvrf.physics.GVRRigidBody;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.Gravity;
import android.view.MotionEvent;
import org.gearvrf.scene_objects.GVRCubeSceneObject;

import java.io.IOException;
import java.util.Random;


public class StackMain extends GVRMain { //implements GVRSceneObject.ComponentVisitor{
    //-------------------------------------------------------------------------
    // constants
    //-------------------------------------------------------------------------

    private enum State {
        NONE,
        INTRO,
        PLAYING,
        GAME_OVER;
    }

    public static final float BLOCK_HEIGHT = 0.2f;
    public static final float START_WIDTH = 0.5f;
    public static final float START_DEPTH = 0.5f;
    public static final float MOVE_LIMIT = 2.0f;
    public static final float SPEED_PROGRESSION = 0.05f;
    public static final int BLOCK_COLLISION_GROUP = 1;


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
    private Block mRootBlock = null;
    private Block mCurrentBlock = null;
    private Block mPreviousBlock = null;
    private GVRSceneObject mPhysicsBlockObject = null;
    private GVRRigidBody mPhysicsBlockRigidBody = null;
    private boolean mMoveAlongX = false;
    private boolean mButtonPressed = false;
    private float mGameSpeed = 1.0f;
    private int mCombo = 0;
    private GVRTextViewSceneObject mScoreBoard;
    private SoundPool   mAudioEngine;
    private SoundEffect[] mStackSound = new SoundEffect[3];
    private SoundEffect[] mCheerSound = new SoundEffect[3];
    private SoundEffect mGameOverSound = null;
    private SoundEffect mTitleSound = null;

    //-------------------------------------------------------------------------
    // inhereted funcs
    //-------------------------------------------------------------------------

    @Override
    public void onInit(GVRContext context) {
        mContext = context;
        mScene = mContext.getMainScene();

        initCamera();
        initReticle();
        initAudio();
        initScene();

        createUI();

        startIntro();
    }


    @Override
    public void onStep() {
        mStateElapsedTime = System.currentTimeMillis() - mStateStartTime;

        switch (mState) {
            default:
            case NONE:
                break;
            case INTRO:
                updateIntro();
                break;
            case PLAYING:
                //mScene.getRoot().forAllComponents(this, GVRRigidBody.getComponentType());
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

    public void onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mButtonPressed = true;
                break;

            default:
                break;
        }
    }


    //-------------------------------------------------------------------------
    // private funcs
    //-------------------------------------------------------------------------

    private void setState(State state) {
        if (state != mState) {
            mState = state;
            mStateStartTime = System.currentTimeMillis();
            mStateElapsedTime = 0;
            Log.d("Stack", "StackMain.setState(" + mState + ")");
        }
    }


    private void initCamera() {
        mScene.getMainCameraRig().getLeftCamera().setBackgroundColor(0.5f, 0.5f, 1.0f, 1.0f);
        mScene.getMainCameraRig().getRightCamera().setBackgroundColor(0.5f, 0.5f, 1.0f, 1.0f);
        mScene.getMainCameraRig().getTransform().setPosition(2.0f, 1.0f, 1.0f);
        mScene.getMainCameraRig().getTransform().setRotationByAxis(60f, 0.0f, 1.0f, 0.0f);
    }


    private void initReticle() {
        //GVRSceneObject headTracker = new GVRSceneObject(context,
        //        context.createQuad(0.1f, 0.1f),
        //        context.loadTexture(new GVRAndroidResource(context, R.drawable.headtrackingpointer)));
        //headTracker.getTransform().setPosition(0.0f, 0.0f, -1.0f);
        //headTracker.getRenderData().setDepthTest(false);
        //headTracker.getRenderData().setRenderingOrder(100000);
        //mScene.getMainCameraRig().addChildObject(headTracker);
    }

    private void initAudio() {
        mAudioEngine = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        try
        {
            mStackSound[0] = new SoundEffect(mContext, mAudioEngine, "stack1.wav", false);
            mStackSound[0].setVolume(1.0f);
            mStackSound[1] = new SoundEffect(mContext, mAudioEngine, "stack2.wav", false);
            mStackSound[1].setVolume(1.0f);
            mStackSound[2] = new SoundEffect(mContext, mAudioEngine, "stack3.wav", false);
            mStackSound[2].setVolume(1.0f);
            mCheerSound[0] = new SoundEffect(mContext, mAudioEngine, "cheer1.wav", false);
            mCheerSound[0].setVolume(1.0f);
            mCheerSound[1] = new SoundEffect(mContext, mAudioEngine, "cheer2.wav", false);
            mCheerSound[1].setVolume(1.0f);
            mCheerSound[2] = new SoundEffect(mContext, mAudioEngine, "cheer3.wav", false);
            mCheerSound[2].setVolume(1.0f);
            //mCheerSound[3] = new SoundEffect(mContext, mAudioEngine, "cheer4.wav", false);
            //mCheerSound[3].setVolume(1.0f);
            //mCheerSound[4] = new SoundEffect(mContext, mAudioEngine, "cheer5.mp3", false);
            //mCheerSound[4].setVolume(1.0f);
            mGameOverSound = new SoundEffect(mContext, mAudioEngine, "gameOver.wav", false);
            mGameOverSound.setVolume(1.0f);
            mTitleSound = new SoundEffect(mContext, mAudioEngine, "title.wav", false);
            mTitleSound.setVolume(1.0f);
        }
        catch (IOException ex)
        {
            Log.e("Audio", "Cannot load sound");
        }
    }

    private void initScene() {


        GVRCollisionMatrix collisionMatrix;
        collisionMatrix = new GVRCollisionMatrix();
        //collisionMatrix.setCollisionFilterMask(COLLISION_GROUP_INFINITY_GROUND, (short) 0x0);
        collisionMatrix.enableCollision(BLOCK_COLLISION_GROUP, BLOCK_COLLISION_GROUP);

        mScene.getRoot().attachComponent(new GVRWorld(mContext, new GVRCollisionMatrix()));
        mScene.getEventReceiver().addListener(this);
        
        createLights();

        mBlockMesh = new GVRCubeSceneObject(mContext, true).getRenderData().getMesh();
    }


    private void createUI() {
        mScoreBoard = new GVRTextViewSceneObject(mContext, 3.0f, 1.5f, "Welcome to StackMojo Tap to start");
        mScoreBoard.getTransform().setPosition(-1.0f, 1.0f, -2.0f);
        mScoreBoard.getTransform().setRotationByAxis(30f, 0.0f, 1.0f, 0.0f);
        mScene.addSceneObject(mScoreBoard);
     }


    private void createLights() {
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


    private Block createBlock(Vector2f dimensions) {
        Log.d("Stack", "StackMain.createBlock() dimensions:" + dimensions + "");
        GVRSceneObject blockObject = new GVRSceneObject(mContext, mBlockMesh);
        GVRRenderData rdata = blockObject.getRenderData();
        blockObject.setName("block " + mStackHeight);
        rdata.setShaderTemplate(GVRPhongShader.class);
        rdata.setAlphaBlend(true);

        GVRMaterial material = new GVRMaterial(mContext);
        Random rand = new Random();
        float red = rand.nextFloat();
        float green = rand.nextFloat();
        float blue = rand.nextFloat();
        material.setDiffuseColor(red, green, blue, 1.0f);
        rdata.setMaterial(material);
        rdata.setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);

        Block block = new Block(mContext, mMoveAlongX, BLOCK_HEIGHT, MOVE_LIMIT, mGameSpeed);
        blockObject.attachComponent(block);

        return block;
    }


    private void createPhysicsBlock(Vector3f position, Vector2f dimensions, float[] color) {
        Log.d("Stack", "StackMain.createPhysicsBlock()  position:" + position + "  dimensions:" + dimensions + "");
        if (mPhysicsBlockObject == null) {
             mPhysicsBlockObject = new GVRSceneObject(mContext, mBlockMesh);
            GVRRenderData rdata = mPhysicsBlockObject.getRenderData();
            mPhysicsBlockObject.setName("physicsblock");
            rdata.setShaderTemplate(GVRPhongShader.class);
            rdata.setAlphaBlend(true);
            GVRMaterial material = new GVRMaterial(mContext);
            rdata.setMaterial(material);
            rdata.setRenderingOrder(GVRRenderData.GVRRenderingOrder.GEOMETRY);

            mPhysicsBlockRigidBody = new GVRRigidBody(mContext, 0.3f, BLOCK_COLLISION_GROUP);
            mPhysicsBlockRigidBody.setRestitution(0.5f);
            mPhysicsBlockRigidBody.setFriction(5.0f);
            mPhysicsBlockRigidBody.setGravity(0f,-9.8f,0f);
            mPhysicsBlockRigidBody.applyCentralForce(0f,-9.8f,0f);
            mPhysicsBlockObject.attachComponent(mPhysicsBlockRigidBody);

            GVRMeshCollider meshCollider = new GVRMeshCollider(mContext, mPhysicsBlockObject.getRenderData().getMesh());
            mPhysicsBlockObject.attachCollider(meshCollider);

            mScene.addSceneObject(mPhysicsBlockObject);


        }

        GVRMaterial material = mPhysicsBlockObject.getRenderData().getMaterial();
        material.setDiffuseColor(color[0], color[1], color[2], 1.0f);
        mPhysicsBlockObject.getTransform().setPosition(position.x, position.y, position.z);
        mPhysicsBlockObject.getTransform().setScale(dimensions.x, BLOCK_HEIGHT, dimensions.y);

        mPhysicsBlockRigidBody.applyCentralForce(0f,-9.8f,0f);

        mPhysicsBlockRigidBody.setCenter(position.x, position.y, position.z);
        mPhysicsBlockRigidBody.setScale(dimensions.x, BLOCK_HEIGHT, dimensions.y);

    }


    private void startIntro() {
        mTitleSound.play();
        mStackHeight = 0;
        mGameSpeed = 1.0f;
        mCurrentDimensions = new Vector2f(START_WIDTH, START_DEPTH);
        mRootBlock = createBlock(mCurrentDimensions);
        mCurrentBlock = mRootBlock;
        mScene.addSceneObject(mRootBlock.getOwnerObject());
        mRootBlock.getTransform().setPositionY(-0.4f);

        setUIText("Welcome to StackMojo\nTap to start");

        setState(State.INTRO);
    }


    private void updateIntro()
    {
        if (mButtonPressed) {
            mButtonPressed = false;
            startPlaying();
        }
    }


    private void startPlaying() {
        setState(State.PLAYING);
        stackBlock();
    }


    private void updatePlaying() {
         if (mButtonPressed) {
            mButtonPressed = false;
            boolean stacked = stackBlock();
            if (!stacked) {
                startGameOver();
            }
        }
    }


    private void startGameOver() {
        setState(State.GAME_OVER);
        setUIText("GAME OVER score:"+mStackHeight);
        mGameOverSound.play();
    }


    private void updateGameOver() {
        if (mButtonPressed) {
            mButtonPressed = false;
            cleanUp();
            startIntro();
        }
    }


    private boolean stackBlock() {
        Log.d("Stack", "stackBlock "+mStackHeight);

         if (mCurrentBlock != mRootBlock) {


            mCurrentBlock.setAnimating(false);

            // determine overlap
            Vector2f currentXZ = new Vector2f(mCurrentBlock.getOwnerObject().getTransform().getPositionX(), mCurrentBlock.getOwnerObject().getTransform().getPositionZ());
            Vector2f previousXZ = new Vector2f(mPreviousBlock.getOwnerObject().getTransform().getPositionX(), mPreviousBlock.getOwnerObject().getTransform().getPositionZ());
            Vector2f diffXZ = currentXZ.sub(previousXZ);
            float distance = diffXZ.length();

            boolean overlap = true;


            float newScaleX = mCurrentBlock.getOwnerObject().getTransform().getScaleX();
            float newScaleZ = mCurrentBlock.getOwnerObject().getTransform().getScaleZ();
            float newPositionX = mCurrentBlock.getOwnerObject().getTransform().getPositionX();
            float newPositionZ = mCurrentBlock.getOwnerObject().getTransform().getPositionZ();
             float diffX = 0f;
             float diffZ = 0f;
             if (mMoveAlongX) {
                overlap = distance < mPreviousBlock.getOwnerObject().getTransform().getScaleX();
                newScaleX = mCurrentBlock.getOwnerObject().getTransform().getScaleX() - distance;
                diffX = mCurrentBlock.getOwnerObject().getTransform().getPositionX() - mPreviousBlock.getOwnerObject().getTransform().getPositionX();
                newPositionX = newPositionX - diffX/2.0f;
            } else {
                overlap = distance < mPreviousBlock.getOwnerObject().getTransform().getScaleZ();
                newScaleZ = mCurrentBlock.getOwnerObject().getTransform().getScaleZ() - distance;
                diffZ = mCurrentBlock.getOwnerObject().getTransform().getPositionZ() - mPreviousBlock.getOwnerObject().getTransform().getPositionZ();
                newPositionZ = newPositionZ - diffZ/2.0f;
            }

            Log.d("Stack", "mMoveAlongX:"+mMoveAlongX+"  overlap:"+overlap+"  distance:"+distance);

            // parent block to stack
            float y = mCurrentBlock.getOwnerObject().getTransform().getPositionY() - mRootBlock.getOwnerObject().getTransform().getPositionY();
            mScene.removeSceneObject(mCurrentBlock.getOwnerObject());
            mRootBlock.getOwnerObject().addChildObject(mCurrentBlock.getOwnerObject());
            mScene.bindShaders(mRootBlock.getOwnerObject());
            mCurrentBlock.getOwnerObject().getTransform().setPositionY(y);

             if (overlap) {

                 Random rand = new Random();
                 int index = rand.nextInt(3);
                 mStackSound[index].play();

                 // physics block
                 Vector3f position = new Vector3f(mCurrentBlock.getTransform().getPositionX(), mCurrentBlock.getTransform().getPositionY()+mRootBlock.getTransform().getPositionY(), mCurrentBlock.getTransform().getPositionZ());
                 Vector2f dimensions = new Vector2f(mCurrentBlock.getTransform().getScaleX(), mCurrentBlock.getTransform().getScaleZ());
                 if (mMoveAlongX) {
                     position.x = position.x + diffX/2.0f;
                     dimensions.x = diffX;
                 }
                 else {
                     position.z = position.z + diffZ/2.0f;
                     dimensions.y = diffZ;
                 }
                 createPhysicsBlock(position, dimensions, mCurrentBlock.getOwnerObject().getRenderData().getMaterial().getDiffuseColor());

                 // trim
                mCurrentBlock.getOwnerObject().getTransform().setScaleX(newScaleX);
                mCurrentBlock.getOwnerObject().getTransform().setScaleZ(newScaleZ);
                mCurrentBlock.getOwnerObject().getTransform().setPositionX(newPositionX);
                mCurrentBlock.getOwnerObject().getTransform().setPositionZ(newPositionZ);
             }
            else {
                return false;
            }
        }

        mPreviousBlock = mCurrentBlock;

        mStackHeight += 1;

        mMoveAlongX = !mMoveAlongX;

        // new block
        mCurrentBlock = createBlock(mCurrentDimensions);
        mCurrentBlock.getOwnerObject().getTransform().setScaleX(mPreviousBlock.getOwnerObject().getTransform().getScaleX());
        mCurrentBlock.getOwnerObject().getTransform().setScaleZ(mPreviousBlock.getOwnerObject().getTransform().getScaleZ());
        mCurrentBlock.getOwnerObject().getTransform().setPositionX(mPreviousBlock.getOwnerObject().getTransform().getPositionX());
        mCurrentBlock.getOwnerObject().getTransform().setPositionZ(mPreviousBlock.getOwnerObject().getTransform().getPositionZ());
        if (mMoveAlongX) {
            mCurrentBlock.getOwnerObject().getTransform().setPositionX(mCurrentBlock.getOwnerObject().getTransform().getPositionX() - MOVE_LIMIT);
        }
        else {
            mCurrentBlock.getOwnerObject().getTransform().setPositionZ(mCurrentBlock.getOwnerObject().getTransform().getPositionZ() - MOVE_LIMIT);
        }
        mCurrentBlock.getTransform().setScaleY(BLOCK_HEIGHT);

        mScene.addSceneObject(mCurrentBlock.getOwnerObject());

        mCurrentBlock.setAnimating(true);

        mRootBlock.moveDown();

        mGameSpeed += SPEED_PROGRESSION;

        setUIText("Height "+mStackHeight);

        return true;
    }

    private void setUIText(String text) {
        mScoreBoard.setText(text);
    }

    private void cleanUp()
    {
        mScene.removeSceneObject(mRootBlock.getOwnerObject());
        mRootBlock = null;
        mPreviousBlock = null;
        mCurrentBlock = null;
    }

    /*@Override
    public boolean visit(GVRComponent gvrComponent) {
        if (gvrComponent.getTransform().getPositionY() < SCORE_OFFSET) {
            mScene.removeSceneObject(gvrComponent.getOwnerObject());
            doScore((GVRRigidBody) gvrComponent);
        }

        return false;
    }*/

}


