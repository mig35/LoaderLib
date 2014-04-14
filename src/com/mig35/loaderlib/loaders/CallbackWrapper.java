package com.mig35.loaderlib.loaders;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.mig35.loaderlib.data.LoaderResult;

/**
 * Date: 2/4/14
 * Time: 2:08 PM
 *
 * @author MiG35
 */
public class CallbackWrapper<Result> implements LoaderManager.LoaderCallbacks<LoaderResult<Result>> {

	private final LoaderListener mBaseLoaderCallback;
	private AsyncTaskLibLoader<Result> mLoader;

	public CallbackWrapper(final LoaderListener callback, final AsyncTaskLibLoader<Result> loader) {
		mBaseLoaderCallback = callback;
		mLoader = loader;
	}

	@Override
	public Loader<LoaderResult<Result>> onCreateLoader(final int id, final Bundle args) {
		if (mBaseLoaderCallback != null) {
			mBaseLoaderCallback.onTaskStart(id);
		}

		final Loader<LoaderResult<Result>> loader = mLoader;

		mLoader = null;

		return loader;
	}

	@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
	@Override
	public void onLoadFinished(final Loader<LoaderResult<Result>> loader, final LoaderResult<Result> data) {
		if (mBaseLoaderCallback != null) {
			if (data.getException() == null) {
				mBaseLoaderCallback.onTaskComplete(loader.getId(), data.getResult());
			}
			else {
				mBaseLoaderCallback.onTaskFail(loader.getId(), data.getException());
			}
		}
	}

	@Override
	public void onLoaderReset(final Loader<LoaderResult<Result>> loader) {
		if (mBaseLoaderCallback != null) {
			mBaseLoaderCallback.onTaskDestroy(loader.getId());
		}
	}
}