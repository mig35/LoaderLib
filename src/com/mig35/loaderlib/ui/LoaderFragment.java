package com.mig35.loaderlib.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.mig35.injectorlib.utils.inject.InjectSavedState;
import com.mig35.injectorlib.utils.inject.Injector;
import com.mig35.loaderlib.utils.FragmentLoaderHelper;
import com.mig35.loaderlib.utils.FragmentLoaderTaskListener;
import com.mig35.loaderlib.utils.LoaderHelper;

/**
 * Date: 2/4/14
 * Time: 2:47 PM
 *
 * @author MiG35
 */
public class LoaderFragment extends Fragment implements FragmentLoaderTaskListener {

	private Injector mInjector;

	@InjectSavedState
	private FragmentLoaderHelper mFragmentLoaderHelper;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		mInjector = Injector.init(this);

		super.onCreate(savedInstanceState);

		mInjector.applyOnFragmentCreate(this, savedInstanceState);

		if (null == mFragmentLoaderHelper) {
			mFragmentLoaderHelper = new FragmentLoaderHelper();
		}
		if (getId() == 0) {
			// if fragment has no container view Fragment.onViewCreated will not be called, so we will don't add loader listener
			mFragmentLoaderHelper.addLoaderListener(this);
		}
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

		if (getRetainInstance()) {
			if (null == mFragmentLoaderHelper) {
				mFragmentLoaderHelper = new FragmentLoaderHelper();
			}
			mFragmentLoaderHelper.addLoaderListener(this);
		}
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mInjector.applyOnFragmentViewCreated(this);
		mFragmentLoaderHelper.addLoaderListener(this);
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);

		mInjector.applyOnFragmentSaveInstanceState(this, outState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		mFragmentLoaderHelper.removeLoaderListener(this);
		mInjector.applyOnFragmentDestroyView(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();

		if (getRetainInstance() || getId() == 0) {
			mFragmentLoaderHelper.removeLoaderListener(this);
		}
	}

	@Override
	public LoaderHelper getLoaderHelper() {
		return mFragmentLoaderHelper;
	}

	@Override
	public void onLoaderResult(final int id, final Object result) {
	}

	@Override
	public void onLoaderError(final int id, final Exception exception) {
	}
}