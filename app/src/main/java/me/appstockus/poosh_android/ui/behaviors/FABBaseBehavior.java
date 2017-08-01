package me.appstockus.poosh_android.ui.behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class FABBaseBehavior extends FloatingActionButton.Behavior
{
    public FABBaseBehavior(Context context, AttributeSet attributeSet){
        super();
    }


    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, final FloatingActionButton child, MotionEvent ev) {
        child.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setInterpolator(new LinearOutSlowInInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        child.animate().scaleX(1).scaleY(1).setDuration(500);
                    }
                })
                .setDuration(300);

        return super.onInterceptTouchEvent(parent, child, ev);
    }
}
