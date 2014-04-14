package com.mig35.loaderlib.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import com.mig35.loaderlib.loaders.AsyncTaskLibLoader;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Date: 2/4/14
 * Time: 3:21 PM
 *
 * @author MiG35
 */
public class FragmentLoaderHelper implements LoaderHelper, LoaderTaskListener, Serializable, FragmentToActivityLoaderTaskListener {

	private static final long serialVersionUID = 2244770691733936219L;

	private transient LoaderHelper mLoaderHelper;
	private transient FragmentLoaderTaskListener mLoaderTaskListener;

	private final Set<Integer> mFragmentLoaders = new HashSet<Integer>();

	public void addLoaderListener(final Fragment fragment) {
		if (!(fragment instanceof FragmentLoaderTaskListener)) {
			throw new IllegalArgumentException("Fragment should implement FragmentLoaderTaskListener");
		}
		final Activity activity = fragment.getActivity();
		if (!(activity instanceof ActivityLoaderListener)) {
			throw new IllegalArgumentException("Activity should implement ActivityLoaderListener");
		}

		final Fragment parentFragment = fragment.getParentFragment();
		if (null == parentFragment) {
			mLoaderHelper = ((ActivityLoaderListener) activity).getLoaderHelper();
		}
		else {
			mLoaderHelper = ((FragmentLoaderTaskListener) parentFragment).getLoaderHelper();
		}

		mLoaderTaskListener = (FragmentLoaderTaskListener) fragment;
		((ActivityLoaderListener) activity).addLoaderListener(this);
	}

	public void removeLoaderListener(final Fragment fragment) {
		if (!(fragment instanceof LoaderTaskListener)) {
			throw new IllegalArgumentException("Fragment should implement LoaderTaskListener");
		}
		final Activity activity = fragment.getActivity();
		if (!(activity instanceof ActivityLoaderListener)) {
			throw new IllegalArgumentException("Activity should implement ActivityLoaderListener");
		}
		mLoaderHelper = null;
		mLoaderTaskListener = null;
		((ActivityLoaderListener) activity).removeLoaderListener(this);
	}

	@Override
	public void removeLoaderFromRunningLoaders(final int loaderId) {
		checkStatus();
		mLoaderHelper.removeLoaderFromRunningLoaders(loaderId);
	}

	@Override
	public void removeAllLoadersFromRunningLoaders() {
		checkStatus();
		for (final Integer loaderId : mFragmentLoaders) {
			mLoaderHelper.removeLoaderFromRunningLoaders(loaderId);
		}
		mFragmentLoaders.clear();
	}

	@Override
	public <Result> AsyncTaskLibLoader<Result> initAsyncLoader(final int id, final AsyncTaskLibLoader<Result> loader) {
		checkStatus();
		mFragmentLoaders.add(id);
		return mLoaderHelper.initAsyncLoader(id, loader);
	}

	@Override
	public <Result> AsyncTaskLibLoader<Result> restartAsyncLoader(final int id, final AsyncTaskLibLoader<Result> loader) {
		checkStatus();
		mFragmentLoaders.add(id);
		return mLoaderHelper.restartAsyncLoader(id, loader);
	}

	@Override
	public void destroyAsyncLoader(final int id) {
		checkStatus();
		mFragmentLoaders.remove(id);
		mLoaderHelper.destroyAsyncLoader(id);
	}

	@Override
	public void destroyAllAsyncLoaders() {
		checkStatus();
		mLoaderHelper.destroyAllAsyncLoaders();
	}

	@Override
	public boolean hasLoader(final int loaderId) {
		checkStatus();
		return mLoaderHelper.hasLoader(loaderId);
	}

	@Override
	public <Result> AsyncTaskLibLoader<Result> getLoader(final int loaderId) {
		checkStatus();
		return mLoaderHelper.getLoader(loaderId);
	}

	@Override
	public int getLoaderId(final String loaderName) {
		checkStatus();
		return mLoaderHelper.getLoaderId(loaderName);
	}

	@Override
	public boolean hasRunningLoaders() {
		checkStatus();
		return mLoaderHelper.hasRunningLoaders();
	}

	private void checkStatus() {
		if (null == mLoaderHelper) {
			throw new IllegalStateException("Fragment is not attached to Activity");
		}
	}

	@Override
	public void onLoaderResult(final int id, final Object result) {
		if (null != mLoaderTaskListener && null != mLoaderTaskListener.getActivity()) {
			mLoaderTaskListener.onLoaderResult(id, result);
		}
	}

	@Override
	public void onLoaderError(final int id, final Exception exception) {
		if (null != mLoaderTaskListener && null != mLoaderTaskListener.getActivity()) {
			mLoaderTaskListener.onLoaderError(id, exception);
		}
	}
}