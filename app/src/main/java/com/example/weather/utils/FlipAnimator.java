package com.example.weather.utils;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;
import com.example.weather.R;


public class FlipAnimator {

   static AnimatorSet leftIn, leftOut, rightIn, rightOut;

    public static void flip(Context context, View back, View front, boolean showFront){
        leftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in);
        rightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out);
        rightIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in);
        leftOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out);

        AnimatorSet anim = new AnimatorSet();

        if(showFront){
            leftIn.setTarget(back);
            rightOut.setTarget(front);
            anim.playTogether(leftIn, rightOut);
            anim.start();
        } else {
            leftOut.setTarget(back);
            rightIn.setTarget(front);
            anim.playTogether(leftOut, rightIn);
            anim.start();
        }
    }

}
