package tk.wenop.XiangYu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import tk.wenop.XiangYu.R;

public class FirstRunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        GifImageView gif_iv = (GifImageView) findViewById(R.id.gif_first_bg);
        GifDrawable gif_drawable = (GifDrawable) gif_iv.getDrawable();

        final RelativeLayout logo_group = (RelativeLayout) findViewById(R.id.group_logo);

        final Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new LinearInterpolator()); //add this
        fadeIn.setDuration(2000);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // after logo shows
                Intent intent = new Intent(FirstRunActivity.this, SplashActivity.class);
                startActivity(intent);
                FirstRunActivity.this.finish();
                Log.d("wenop-debug", "animation end");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        gif_drawable.setLoopCount(1);
        gif_drawable.addAnimationListener(new AnimationListener() {

            /**
             * Called when a single loop of the animation is completed.
             *
             * @param loopNumber 0-based number of the completed loop, 0 for infinite animations
             */
            @Override
            public void onAnimationCompleted(int loopNumber) {
                // show the logo
                logo_group.setAlpha(1f);
                logo_group.startAnimation(fadeIn);
            }

        });
        gif_drawable.start();
    }

}
