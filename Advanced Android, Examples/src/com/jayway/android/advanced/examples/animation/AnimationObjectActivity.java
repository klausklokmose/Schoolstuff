package com.jayway.android.advanced.examples.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class AnimationObjectActivity extends AnimationActivity {
	
	private static final String PROPERTY = "translationY";

	private class ObjectAnimatorController extends AnimationController {
		private Animator animator;

		/**
		 * Scale animation using a ObjectAnimation. Animates a property of the
		 * view using reflection
		 * 
		 * @param view
		 */
		public ObjectAnimatorController(View view) {
			PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(PROPERTY, 0f, 500f);

			ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX);

			objectAnimator.setDuration(3000);
			objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
			objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
			objectAnimator.setRepeatCount(ValueAnimator.INFINITE);

			animator = objectAnimator;
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
		return new ObjectAnimatorController(view);
	}
}
