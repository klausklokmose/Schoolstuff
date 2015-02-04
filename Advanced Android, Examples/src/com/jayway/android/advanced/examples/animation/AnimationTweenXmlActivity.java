package com.jayway.android.advanced.examples.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jayway.android.advanced.examples.R;

public class AnimationTweenXmlActivity extends AnimationActivity {

	private class XmlAnimatorController extends AnimationController {
		private Animation animation;
		private View view;

		public XmlAnimatorController(View view) {
			this.view = view;
			animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce);
		}

		@Override
		protected void startAnimation() {
			view.startAnimation(animation);
		}

		@Override
		protected void stopAnimation() {
			animation.cancel();
			animation.reset();
		}

	}

	@Override
	protected AnimationController createAnimationController(View view) {
		return animationController = new XmlAnimatorController(view);
	}
}
