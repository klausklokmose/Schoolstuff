package com.jayway.android.advanced.examples.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class AnimationValueActivity extends AnimationActivity {

	private class ValueAnimatorController extends AnimationController {
		private Animator animator;

		/**
		 * We use a value animator to animate the color of the circle gradient
		 * for a pulsating animation
		 * 
		 * @param circle
		 */
		public ValueAnimatorController(final View view) {
			ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 500).setDuration(3000);
			valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
			valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
			valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

			valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float f = (Float) animation.getAnimatedValue();
					view.setTranslationY(f);

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
