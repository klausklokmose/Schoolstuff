package com.jayway.android.advanced.examples.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.jayway.android.advanced.examples.R;
import com.jayway.android.advanced.examples.customview.Circle;

public class CircleAnimationValueActivity extends AnimationActivity {
	private Circle circle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		circle = findTypedViewById(R.id.custom_view_circle);
		circle.setText("Animate me!");
		circle.setTextSize(70);
	}

	@Override
	protected int getLayout() {
		return R.layout.custom_view_animation;
	}

	@Override
	protected int getIdOfViewToAnimate() {
		return R.id.custom_view_circle;
	}

	private class ValueAnimatorController extends AnimationController {
		private Animator animator;

		/**
		 * We use a value animator to animate the color of the circle gradient
		 * for a pulsating animation
		 * 
		 * @param circle
		 */
		public ValueAnimatorController(View view) {
			ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 500).setDuration(3000);
			valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
			valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
			valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

			valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float f = (Float) animation.getAnimatedValue();
					circle.setAnimationValue(f);

				}
			});
			animator = valueAnimator;

		}

		@Override
		protected void startAnimation() {
			animator.start();
		}

		@Override
		protected void stopAnimation() {
			animator.end();
		}

	}

	@Override
	protected AnimationController createAnimationController(View view) {
		return new ValueAnimatorController(view);
	}
}
