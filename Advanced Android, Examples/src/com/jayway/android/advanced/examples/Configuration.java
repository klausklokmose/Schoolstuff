/*
 * (C)opyright 2013 : Jayway ApS
 */

package com.jayway.android.advanced.examples;

import android.content.Context;

import com.jayway.android.advanced.examples.adapters.ArrayAdapterActivity;
import com.jayway.android.advanced.examples.adapters.BaseAdapterActivity;
import com.jayway.android.advanced.examples.animation.AnimationObjectActivity;
import com.jayway.android.advanced.examples.animation.AnimationObjectXmlActivity;
import com.jayway.android.advanced.examples.animation.AnimationTweenXmlActivity;
import com.jayway.android.advanced.examples.animation.AnimationValueActivity;
import com.jayway.android.advanced.examples.animation.CircleAnimationValueActivity;
import com.jayway.android.advanced.examples.asynctasks.baduiupdate.BadUiUpdateActivity;
import com.jayway.android.advanced.examples.asynctasks.cancellation.AsyncTaskCancellationActivity;
import com.jayway.android.advanced.examples.asynctasks.progressupdate.ProgressReportingActivity;
import com.jayway.android.advanced.examples.asynctasks.uiblocking.UiBlockingActivity;
import com.jayway.android.advanced.examples.customview.CustomViewActivity;
import com.jayway.android.advanced.examples.fragments.component.ComponentFragmentActivity;
import com.jayway.android.advanced.examples.fragments.xml.ResourceDeterminedFragmentActivity;
import com.jayway.android.advanced.examples.network.rssfeed.RssFeedActivity;
import com.jayway.android.advanced.examples.services.intervalservice.RepeatedServiceActivity;
import com.jayway.android.advanced.examples.services.simpleworkqueue.IntentServiceActivity;

public final class Configuration {

	public static SampleConfiguration getSampleConfiguration(Context context) {
		return new SampleConfigurationBuilder()
			.beginCollection("Adapter Views")
				.addSample("ArrayAdapter", ArrayAdapterActivity.class)
				.addSample("BaseAdapter", BaseAdapterActivity.class)
			.endCollection()
			.beginCollection("Custom Views")
				.addSample(context.getString(R.string.custom_view), CustomViewActivity.class)
			.endCollection()
			.beginCollection("Animations")
				.addSample(context.getString(R.string.animation_tween_xml), AnimationTweenXmlActivity.class)
				.addSample(context.getString(R.string.animation_property_java), AnimationObjectActivity.class)
				.addSample(context.getString(R.string.animation_property_xml), AnimationObjectXmlActivity.class)
				.addSample(context.getString(R.string.animation_value_java), AnimationValueActivity.class)
				.addSample(context.getString(R.string.animation_value_java_on_circle), CircleAnimationValueActivity.class)
			.endCollection()
			.beginCollection("Fragments")
				.addSample(context.getString(R.string.fragments_component_main_title), ComponentFragmentActivity.class)
				.addSample(context.getString(R.string.fragments_xml_main_title), ResourceDeterminedFragmentActivity.class)
			.endCollection()
			.beginCollection("Async Tasks/Loaders")
				.addSample(context.getString(R.string.asynctasks_uiblocking_main_title), UiBlockingActivity.class)
				.addSample(context.getString(R.string.asynctasks_baduiupdate_main_title), BadUiUpdateActivity.class)
				.addSample(context.getString(R.string.asynctasks_cancellation_main_title), AsyncTaskCancellationActivity.class)
				.addSample(context.getString(R.string.asynctasks_progressupdate_main_title), ProgressReportingActivity.class)
			.endCollection()
			.beginCollection("Services")
				.addSample(context.getString(R.string.services_simpleworkqueue_main_title), IntentServiceActivity.class)
				.addSample(context.getString(R.string.services_intervalservice_main_title), RepeatedServiceActivity.class)
			.endCollection()
			.beginCollection("Network")
				.addSample(context.getString(R.string.network_rssfeed_main_title), RssFeedActivity.class)
			.endCollection()
			.beginCollection("Content Providers")
			.endCollection()
		.build();
	}
}
