package app.shome.ir.shome.utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import app.shome.ir.shome.Const;
import app.shome.ir.shome.R;
import app.shome.ir.shome.SHomeActivity;
import app.shome.ir.shome.SHomeApplication;
import app.shome.ir.shome.ui.SenarioActivity;
import app.shome.ir.shome.Const;

/**
 * Created by Mahdi on 29/01/2017.
 */
public class Utils {
    public static void expand(final View v) {
        float targetWidth = v.getWidth();
        v.setX(-targetWidth);
        v.setVisibility(View.VISIBLE);
        v.animate().translationX(0).setDuration(Const.AnimDuration);
    }

    public static void collapse(final View v, Activity activity) {
        float sw = app.shome.ir.shome.Utils.getScreenWidth(activity);
        v.animate().translationX(-sw).setDuration(Const.AnimDuration);
    }

    public static void zoom_in(final View v) {
        Animation aa = AnimationUtils.loadAnimation(SHomeApplication.context, R.anim.zoom_in);
        aa.setDuration(Const.AnimDuration);
        v.startAnimation(aa);
    }

    public static void zoom_out(final View v) {
        Animation aa = AnimationUtils.loadAnimation(SHomeApplication.context, R.anim.zoom_out);
        aa.setDuration(Const.AnimDuration);
        v.startAnimation(aa);
    }

    public static void change_color(final View v, int colorFrom, int colorTo) {
//        int colorFrom = getResources().getColor(R.color.transparent);
//        int colorTo = getResources().getColor(R.color.white);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(Const.AnimDuration);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                v.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }

}
