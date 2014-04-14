package com.mig35.loaderlib.loaders;

import android.content.Context;
import android.net.Uri;

/**
 * Date: 6/24/13
 * Time: 10:51 AM
 *
 * @author MiG35
 */
public abstract class UriDataAsyncTaskLibLoader<Result> extends DataAsyncTaskLibLoader<Result> {

	private final Uri mUri;
	private final ForceLoadContentObserver mObserver;

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Will throw an exception if uri is null
	 */
	@SuppressWarnings("UnusedDeclaration")
	protected UriDataAsyncTaskLibLoader(final Context context, final Uri uri) {
		super(context);

		if (null == uri) {
			throw new IllegalArgumentException("uri can't be null");
		}
		mUri = uri;

		mObserver = new ForceLoadContentObserver();
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Will throw an exception if uri is null
	 */
	protected UriDataAsyncTaskLibLoader(final Context context, final Uri uri, final boolean isLocal) {
		super(context, isLocal);

		if (null == uri) {
			throw new IllegalArgumentException("uri can't be null");
		}
		mUri = uri;

		mObserver = new ForceLoadContentObserver();
	}

	@Override
	protected final Result performLoad() throws Exception {
		final Result result = getResult();

		applyUri(result, mUri);

		return result;
	}

	protected abstract Result getResult() throws Exception;

	/**
	 * Register uri somewhere.
	 * Default implementation will register on content observer
	 *
	 * @param result result of loader.
	 * @param uri    Uri to register
	 */
	protected void applyUri(final Result result, final Uri uri) {
		getContext().getContentResolver().registerContentObserver(uri, true, getObserver());
	}

	/**
	 * Unregister uri somewhere.
	 *
	 * @param uri Uri to register
	 */
	protected void deapplyUri(final Uri uri) {
		getContext().getContentResolver().unregisterContentObserver(getObserver());
	}


	@Override
	protected void onReset() {
		super.onReset();

		deapplyUri(mUri);
	}

	protected ForceLoadContentObserver getObserver() {
		return mObserver;
	}
}