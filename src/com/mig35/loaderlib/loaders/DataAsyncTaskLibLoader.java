package com.mig35.loaderlib.loaders;

import android.content.Context;
import com.mig35.loaderlib.utils.Utils;

/**
 * Date: 6/24/13
 * Time: 10:51 AM
 *
 * @author MiG35
 */
public abstract class DataAsyncTaskLibLoader<Result> extends AsyncTaskLibLoader<Result> {

	private Result mData;

	private final boolean mIsLocal;

	/**
	 * will call DataAsyncTaskLibLoader(context, false);
	 */
	protected DataAsyncTaskLibLoader(final Context context) {
		this(context, false);
	}

	/**
	 * @param isLocal if true will not check internet connection before call. default is false
	 */
	protected DataAsyncTaskLibLoader(final Context context, final boolean isLocal) {
		super(context);

		mIsLocal = isLocal;
	}

	@Override
	protected boolean hasData() {
		return mData != null;
	}

	@Override
	protected Result setData(final Result data) {
		final Result oldData = mData;
		mData = data;

		return oldData;
	}

	@Override
	protected Result getData() {
		return mData;
	}

	@Override
	protected void releaseData(final Result data) {
	}

	@Override
	protected final Result doWork() throws Exception {
		if (!mIsLocal) {
			Utils.checkInternet(getContext());
		}
		return performLoad();
	}

	protected abstract Result performLoad() throws Exception;
}