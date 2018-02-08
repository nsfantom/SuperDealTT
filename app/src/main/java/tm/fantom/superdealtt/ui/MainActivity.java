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

public final class MainActivity extends AppCompatActivity implements MainFragment.Listener, ReposFragment.GestureListener {
    private float mScale = 1f;
    private final float maxScale = 5f;
    private final float minScale = 0.5f;
    private ScaleGestureDetector mScaleDetector;
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, MainFragment.newInstance())
                    .commit();
        }
        gestureDetector = new GestureDetector(new GestureListener());
    }

    @Override
    public void onOrgClicked(String name) {
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
        if (mScaleDetector != null)
            mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onAttachForGesture(View view) {
        // animation for scalling
        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = 1 - detector.getScaleFactor();

                float prevScale = mScale;
                mScale += scale;

                if (mScale < minScale) // Minimum scale condition:
                    mScale = minScale;

                if (mScale > maxScale) // Maximum scale condition:
                    mScale = maxScale;

                PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f / prevScale, 1f / mScale);
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f / prevScale, 1f / mScale);
                ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY);
                AnimatorSet setAnimation = new AnimatorSet();
                setAnimation.play(scaleAnimator);
                setAnimation.start();
                return true;
            }
        });

        gestureDetector = new GestureDetector(this, new GestureListener()) {
            float dX, dY;

            @Override
            public boolean onTouchEvent(MotionEvent ev) {

                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - ev.getRawX();
                        dY = view.getY() - ev.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float moveX = ev.getRawX() + dX;
                        float moveY = ev.getRawY() + dY;
                        float width = view.getWidth();                      // layout Width, can be parent
                        float height = view.getHeight();                    // layout Height, can be parent
                        float scaledWidth = width * 1f / mScale;
                        float scaledHeight = height * 1f / mScale;
                        if (1f / mScale <= 1f) {
                            if (moveX + width / 2 - scaledWidth / 2 <= 0)       // left minimum
                                moveX = scaledWidth / 2 - width / 2;
                            if (moveX - (width / 2 - scaledWidth / 2) >= 0)     // right minimum
                                moveX = width / 2 - scaledWidth / 2;
                            if (moveY + height / 2 - scaledHeight / 2 <= 0)     // top minimum
                                moveY = scaledHeight / 2 - height / 2;
                            if (moveY - (height / 2 - scaledHeight / 2) >= 0)   // bottom minimum
                                moveY = height / 2 - scaledHeight / 2;
                        } else {
                            if (moveX + scaledWidth / 2 - width / 2 <= 0)         // left maximum
                                moveX = width / 2 - scaledWidth / 2;
                            if (moveX - (scaledWidth / 2 - width / 2) >= 0)       // right maximum
                                moveX = scaledWidth / 2 - width / 2;
                            if (moveY + scaledHeight / 2 - height / 2 <= 0)         // top maximum
                                moveY = height / 2 - scaledHeight / 2;
                            if (moveY - (scaledHeight / 2 - height / 2) >= 0)       // bottom maximum
                                moveY = scaledHeight / 2 - height / 2;
                        }
                        ObjectAnimator moveOAX = ObjectAnimator.ofFloat(view, "x", moveX);
                        ObjectAnimator moveOAY = ObjectAnimator.ofFloat(view, "y", moveY);
                        moveOAX.setDuration(0);
                        moveOAY.setDuration(0);

                        AnimatorSet scaleDown = new AnimatorSet();
                        scaleDown.play(moveOAX).with(moveOAY);
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
