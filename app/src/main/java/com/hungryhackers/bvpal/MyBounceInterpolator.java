package com.hungryhackers.bvpal;

import android.view.animation.Interpolator;

/**
 * Created by YourFather on 26-02-2017.
 */

public class MyBounceInterpolator implements Interpolator {
    double mAmplitude = 1;
    double mFrequency = 10;

    MyBounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    @Override
    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}
