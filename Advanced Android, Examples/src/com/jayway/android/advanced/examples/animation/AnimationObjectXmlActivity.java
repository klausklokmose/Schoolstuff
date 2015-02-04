package com.jayway.android.advanced.examples.animation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.view.View;

import com.jayway.android.advanced.examples.R;

public class AnimationObjectXmlActivity extends AnimationActivity {

	private class ObjectAnimatorController extends AnimationController {
		private Animator animator;

		public ObjectAnimatorController(View view) {
			animator = AnimatorInflater.loadAnimator(view.getContext(), R.animator.bounce_animator);
			animator.setTarget(view);
		}

		/**
		 * Scale animation using a ObjectAnimation. Animates a property of the
		 * view using reflection
		 * 
		 * @param view
		 */

		@Override
		protected void startAnimation() {
			animator.start();
		}

		@Override
		protected void stopAnimation() {
			animator.cancel();
		}

	}

	@Override
	protected AnimationController createAnimationController(View view) {
		return animationController = new ObjectAnimatorController(view);
	}
}
