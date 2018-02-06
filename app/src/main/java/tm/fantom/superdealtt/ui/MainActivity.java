package tm.fantom.superdealtt.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import tm.fantom.superdealtt.R;

public final class MainActivity extends AppCompatActivity implements MainFragment.Listener,ReposFragment.GestureListener {
    private float mScale = 1f;
    private ScaleGestureDetector mScaleDetector;
    GestureDetector gestureDetector;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, MainFragment.newInstance())
                    .commit();
        }
        gestureDetector = new GestureDetector(new GestureListener());
    }

    @Override public void onOrgClicked(String name) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left,
                        R.anim.slide_out_right)
                .replace(android.R.id.content, ReposFragment.newInstance(name))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        if(mScaleDetector!=null)
        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onAttachForGesture(View view) {
        // animation for scalling
        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener()
        {
            @Override
            public boolean onScale(ScaleGestureDetector detector)
            {
                float scale = 1 - detector.getScaleFactor();

                float prevScale = mScale;
                mScale += scale;

                if (mScale < 0.5f) // Minimum scale condition:
                    mScale = 0.5f;

                if (mScale > 5f) // Maximum scale condition:
                    mScale = 5f;

                PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f/prevScale, 1f/mScale);
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f/prevScale, 1f/mScale);
                ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY);
                AnimatorSet setAnimation = new AnimatorSet();
                setAnimation.play(scaleAnimator);
                setAnimation.start();
                return true;
            }
        });

        gestureDetector = new GestureDetector(this, new GestureListener()){float dX,dY;
            @Override
            public boolean onTouchEvent(MotionEvent ev) {

                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - ev.getRawX();
                        dY = view.getY() - ev.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float movex = ev.getRawX()+dX;
                        float movey = ev.getRawY()+dY;
                        // TODO: 06-Feb-18 hit bounds
                        ObjectAnimator moveX = ObjectAnimator.ofFloat(view,"x", movex);
                        ObjectAnimator moveY = ObjectAnimator.ofFloat(view,"y", movey);
                        moveX.setDuration(0);
                        moveY.setDuration(0);

                        AnimatorSet scaleDown = new AnimatorSet();
                        scaleDown.play(moveX).with(moveY);
                        scaleDown.start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // double tap fired.
            return true;
        }
    }
}
