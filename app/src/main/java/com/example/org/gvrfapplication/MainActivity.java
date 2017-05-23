package com.example.org.gvrfapplication;

import android.os.Bundle;
import android.view.MotionEvent;
import org.gearvrf.GVRActivity;
import org.gearvrf.GVRContext;


public class MainActivity extends GVRActivity {

    StackMain main = new StackMain();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setMain(main, "gvr.xml");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        main.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

}
