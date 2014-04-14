package com.mig35.loaderlib.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.mig35.injectorlib.utils.inject.InjectSavedState;
import com.mig35.injectorlib.utils.inject.Injector;
import com.mig35.loaderlib.utils.ActivityLoaderHelper;
import com.mig35.loaderlib.utils.ActivityLoaderListener;
import com.mig35.loaderlib.utils.FragmentToActivityLoaderTaskListener;
import com.mig35.loaderlib.utils.LoaderHelper;

/**
 * Date: 2/4/14
 * Time: 2:18 PM
 *
 * @author MiG35
 */
public class LoaderActivity extends FragmentActivity implements ActivityLoaderListener {

	private Injector mInjector;

	@InjectSavedState
	private ActivityLoaderHelper mActivityLoaderHelper;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		mInjector = Injector.init(this);

		mInjector.applyOnActivityCreate(this, savedInstanceState);

		if (null == mActivityLoaderHelper) {
			mActivityLoaderHelper = new ActivityLoaderHelper();
		}

		super.onCreate(savedInstanceState);

		mActivityLoaderHelper.onCreate(this);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();

		mInjector.applyOnActivityContentChange(this);
	}

	@Override
	protected void onStart() {
		mActivityLoaderHelper.onStart();

		super.onStart();
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();

		mActivityLoaderHelper.onResumeFragments();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mActivityLoaderHelper.onDestroy();
		mInjector.applyOnActivityDestroy(this);
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		mActivityLoaderHelper.onSaveInstanceState();
		mInjector.applyOnActivitySaveInstanceState(this, outState);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mActivityLoaderHelper.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();

		mActivityLoaderHelper.onStop();
	}

	@Override
	public void showProgress(final boolean hasRunningLoaders) {
		// nothing to see here
	}

	@Override
	public void onLoaderResult(final int id, final Object result) {
		// nothing to see here
	}

	@Override
	public void onLoaderError(final int id, final Exception exception) {
		// nothing to see here
	}

	@Override
	public LoaderHelper getLoaderHelper() {
		return mActivityLoaderHelper;
	}

	@Override
	public void addLoaderListener(final FragmentToActivityLoaderTaskListener loaderTaskListener) {
		mActivityLoaderHelper.addLoaderListener(loaderTaskListener);
	}

	@Override
	public void removeLoaderListener(final FragmentToActivityLoaderTaskListener loaderFragment) {
		mActivityLoaderHelper.removeLoaderListener(loaderFragment);
	}
}