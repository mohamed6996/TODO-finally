package com.example.android.todolist;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by lenovo on 3/20/2017.
 */

public class AnimationUtils {
    public static void animate(RecyclerView.ViewHolder holder ,boolean goesDown){

        slidingAnimation(holder, goesDown);

       // slidingWithBounce(holder, goesDown);

    }

    private static void slidingAnimation(RecyclerView.ViewHolder holder, boolean goesDown){

        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translationY", goesDown ? 200 : -200, 0);

        animatorTranslateY.setDuration(1000);

        animatorTranslateY.start();

    }
}
