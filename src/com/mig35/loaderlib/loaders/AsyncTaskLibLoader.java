package com.mig35.loaderlib.loaders;

import android.content.Context;
import android.database.SQLException;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import com.mig35.loaderlib.data.LoaderResult;
import com.mig35.loaderlib.exceptions.LoadException;

/**
 * Date: 6/24/13
 * Time: 10:51 AM
 *
 * @author MiG35
 */
public abstract class AsyncTaskLibLoader<Result> extends AsyncTaskLoader<LoaderResult<Result>> {

	private static final String TAG = "AsyncTaskLibLoader";

	/**
	 * If data was retrieved and still valid.
	 * <p/>
	 * Commonly called on main thread.
	 *
	 * @return true if has data, false otherwise
	 */
	protected abstract boolean hasData();

	/**
	 * Replace current data with new one.
	 * <p/>
	 * Commonly called on main thread.
	 *
	 * @param data new data.
	 * @return old data
	 */
	protected abstract Result setData(Result data);

	/**
	 * Return current data.
	 * <p/>
	 * Commonly called on main thread.
	 *
	 * @return current data
	 */
	protected abstract Result getData();

	/**
	 * Dispose passed data.
	 * <p/>
	 * Commonly called on main thread.
	 *
	 * @param data Data to release
	 */
	protected abstract void releaseData(Result data);

	/**
	 * Check if data equals. By default makes == comparison.
	 *
	 * @param newData New data
	 * @param oldData Old data
	 * @return true if passed datas are equals, false otherwise.
	 */
	protected boolean isDataEqual(final Result newData, final Result oldData) {
		return newData == oldData;
	}

	/**
	 * Do background work here.<br>
	 * <p>
	 * <b>WARNING: Do not use or replace existing data. Create new.</b>
	 * <p/>
	 * <p>
	 * Try to reduce and concrete thrown Exceptions. <br>
	 * Example:
	 * </p>
	 * <p/>
	 * <pre>
	 * protected Result doWork() throws NetworkException, IOException
	 * </pre>
	 *
	 * @return result of your work
	 * @throws Exception on any exception
	 */
	protected abstract Result doWork() throws Exception;

	/**
	 * Creates loader
	 *
	 * @param context Context of your application
	 */
	protected AsyncTaskLibLoader(final Context context) {
		super(context);
	}

	@Override
	public final LoaderResult<Result> loadInBackground() {
		final LoaderResult<Result> loaderResult = new LoaderResult<Result>(null);
		Result result = null;

		try {
			result = doWork();

			if (null == result) {
				Log.e(TAG, "Data is null!");

				loaderResult.setException(new LoadException());
			}
		}
		catch (final SQLException e) {
			Log.e(TAG, "SQLException catched", e);

			loaderResult.setException(e);
		}
		catch (final RuntimeException e) {
			Log.e(TAG, "RuntimeException catched", e);

			throw e;
		}
		catch (final Exception e) {
			Log.e(TAG, "Exception catched", e);

			loaderResult.setException(e);
		}
		catch (final Throwable e) {
			Log.e(TAG, "Throwable catched", e);

			throw new RuntimeException(e);
		}

		loaderResult.setResult(result);

		return loaderResult;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		final boolean hasData = hasData();

		if (hasData) {
			deliverResult(new LoaderResult<Result>(getData()));
		}

		final boolean contentChanged = takeContentChanged();

		if (!hasData || contentChanged) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		super.onStopLoading();

		cancelLoad();
	}

	@Override
	public void onCanceled(final LoaderResult<Result> data) {
		super.onCanceled(data);

		if (data == null) {
			releaseData(null);
		}
		else {
			releaseData(data.getResult());
		}
	}

	@Override
	protected void onReset() {
		super.onReset();

		onStopLoading();

		releaseData(getData());
	}

	@Override
	public void deliverResult(final LoaderResult<Result> data) {
		if (isReset()) {
			if (data == null) {
				releaseData(null);
			}
			else {
				releaseData(data.getResult());
			}

			return;
		}

		if (isAbandoned()) {
			return;
		}

		if (data == null) {
			return;
		}

		final Result oldData = setData(data.getResult());

		if (isStarted()) {
			super.deliverResult(data);
		}

		if (!isDataEqual(data.getResult(), oldData)) {
			releaseData(oldData);
		}
	}
}