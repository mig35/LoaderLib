package com.mig35.loaderlib.utils;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.mig35.loaderlib.exceptions.UnhandalableByBaseActivityException;
import com.mig35.loaderlib.loaders.AsyncTaskLibLoader;
import com.mig35.loaderlib.loaders.CallbackWrapper;
import com.mig35.loaderlib.loaders.LoaderListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Date: 2/4/14
 * Time: 2:18 PM
 *
 * @author MiG35
 */
public class ActivityLoaderHelper implements Serializable, LoaderListener, LoaderHelper {

	private static final long serialVersionUID = -8404711791184797687L;

	private transient boolean mActivityStateSaved;

	private transient Set<LoaderTaskListener> mListeners = new HashSet<LoaderTaskListener>();
	private transient Set<LoaderTaskListener> mWaitingAddListeners = new HashSet<LoaderTaskListener>();
	private transient Set<LoaderTaskListener> mWaitingRemoveListeners = new HashSet<LoaderTaskListener>();
	private transient boolean mInNotificationCompleteLoop;
	private transient boolean mInNotificationFailLoop;

	// this loaders are running at this moment (or was running before save state happen). we should reinit them to take its data.
	private final HashSet<Integer> mRunningLoaders = new HashSet<Integer>();
	// this loaders bring there data while we was on save state. so we should reinit them to take its data (or if activity recreates,
	// we will get it automatically).
	private final HashSet<Integer> mMissedContentChangedLoaders = new HashSet<Integer>();
	// all loaders and there ids. needed to manage loader's ids and names
	private final HashMap<String, Integer> mLoaderIds = new HashMap<String, Integer>();

	private transient ActivityLoaderListener mLoaderTaskListener;
	private transient LoaderManager mLoaderManager;


	public void onCreate(final FragmentActivity fragmentActivity) {
		if (!(fragmentActivity instanceof ActivityLoaderListener)) {
			throw new IllegalArgumentException("fragmentActivity should implement ActivityLoaderListener");
		}
		mActivityStateSaved = false;

		mLoaderTaskListener = (ActivityLoaderListener) fragmentActivity;
		mLoaderManager = fragmentActivity.getSupportLoaderManager();
	}

	public void onStart() {
		mActivityStateSaved = false;
	}

	public void onResumeFragments() {
		mActivityStateSaved = false;

		final Set<Integer> tmpLoadersToStart = new HashSet<Integer>(mRunningLoaders);
		tmpLoadersToStart.addAll(mMissedContentChangedLoaders);
		mMissedContentChangedLoaders.clear();

		final Iterator<Integer> iterator = tmpLoadersToStart.iterator();
		while (iterator.hasNext()) {
			final int loaderId = iterator.next();
			if (!hasLoader(loaderId)) {
				iterator.remove();
				mRunningLoaders.remove(loaderId);
			}
		}
		for (final Integer loaderId : tmpLoadersToStart) {
			initAsyncLoader(loaderId, null);
		}

		updateProgress();
	}

	public void onSaveInstanceState() {
		mActivityStateSaved = true;
	}

	public void onPause() {
		mActivityStateSaved = true;
	}

	public void onStop() {
		mActivityStateSaved = true;
	}

	public void onDestroy() {
		mLoaderTaskListener = null;
		mLoaderManager = null;
	}

	@Override
	public void removeLoaderFromRunningLoaders(final int loaderId) {
		mRunningLoaders.remove(loaderId);
		updateProgress();
	}

	@Override
	public void removeAllLoadersFromRunningLoaders() {
		final List<Integer> tmp = new ArrayList<Integer>(mRunningLoaders);
		for (final Integer loaderId : tmp) {
			removeLoaderFromRunningLoaders(loaderId);
		}
	}

	@Override
	public <Result> AsyncTaskLibLoader<Result> initAsyncLoader(final int id, final AsyncTaskLibLoader<Result> loader) {
		mRunningLoaders.add(id);
		updateProgress();
		return (AsyncTaskLibLoader<Result>) mLoaderManager.initLoader(id, null, new CallbackWrapper<Result>(this, loader));
	}

	@Override
	public <Result> AsyncTaskLibLoader<Result> restartAsyncLoader(final int id, final AsyncTaskLibLoader<Result> loader) {
		mRunningLoaders.add(id);
		updateProgress();
		return (AsyncTaskLibLoader<Result>) mLoaderManager.restartLoader(id, null, new CallbackWrapper<Result>(this, loader));
	}

	@Override
	public void destroyAsyncLoader(final int id) {
		if (mRunningLoaders.remove(id)) {
			updateProgress();
		}
		mLoaderManager.destroyLoader(id);
	}

	@Override
	public void destroyAllAsyncLoaders() {
		final Collection<Integer> allLoaderIds = mLoaderIds.values();
		for (final Integer loaderId : allLoaderIds) {
			destroyAsyncLoader(loaderId);
		}
	}

	@Override
	public boolean hasLoader(final int loaderId) {
		return mLoaderManager.getLoader(loaderId) != null;
	}

	@Override
	public <Result> AsyncTaskLibLoader<Result> getLoader(final int loaderId) {
		final Loader<Result> loader = mLoaderManager.getLoader(loaderId);
		if (loader instanceof AsyncTaskLibLoader) {
			return (AsyncTaskLibLoader<Result>) loader;
		}
		return null;
	}

	@Override
	public int getLoaderId(final String loaderName) {
		Integer loaderId = mLoaderIds.get(loaderName);
		final Collection<Integer> usedIds = mLoaderIds.values();
		int i = 0;
		while (null == loaderId) {
			if (!hasLoader(i) && !usedIds.contains(i)) {
				loaderId = i;
				mLoaderIds.put(loaderName, loaderId);
				break;
			}
			i++;
		}
		return loaderId;
	}

	@Override
	public boolean hasRunningLoaders() {
		return !mRunningLoaders.isEmpty();
	}

	@Override
	public final void onTaskStart(final int id) {
		// pass
	}

	@Override
	public final void onTaskComplete(final int id, final Object result) {
		if (mActivityStateSaved) {
			mMissedContentChangedLoaders.add(id);
			return;
		}
		removeLoaderFromRunningLoaders(id);

		mInNotificationCompleteLoop = true;
		for (final LoaderTaskListener loaderListener : mListeners) {
			loaderListener.onLoaderResult(id, result);
		}
		mInNotificationCompleteLoop = false;
		fireWaitingTasks();

		onLoaderResult(id, result);
	}

	private void onLoaderResult(final int id, final Object result) {
		if (null != mLoaderTaskListener) {
			mLoaderTaskListener.onLoaderResult(id, result);
		}
	}

	@Override
	public final void onTaskFail(final int id, final Exception error) {
		if (mActivityStateSaved) {
			mMissedContentChangedLoaders.add(id);
			return;
		}

		mInNotificationFailLoop = true;
		for (final LoaderTaskListener loaderListener : mListeners) {
			loaderListener.onLoaderError(id, error);
		}
		mInNotificationFailLoop = false;

		if (error instanceof UnhandalableByBaseActivityException) {
			// pass. this is Unhandalable Exception
		}
		else {
			onLoaderError(id, error);
		}

		fireWaitingTasks();

		destroyAsyncLoader(id);
	}

	private void onLoaderError(final int id, final Exception exception) {
		if (null != mLoaderTaskListener) {
			mLoaderTaskListener.onLoaderError(id, exception);
		}
	}

	@Override
	public final void onTaskDestroy(final int id) {
		// pass
	}

	public void addLoaderListener(final LoaderTaskListener loaderListener) {
		// this if needed because this method can be called while we are iterating in mListeners loop in methods onTaskStart, onTaskComplete,
		// onTaskFail or onTaskDestroy (for example we add fragment in onTaskComplete and call executePendingTransaction)
		if (mInNotificationCompleteLoop || mInNotificationFailLoop) {
			mWaitingAddListeners.add(loaderListener);
		}
		else {
			mListeners.add(loaderListener);
		}
	}

	public void removeLoaderListener(final LoaderTaskListener loaderListener) {
		if (mInNotificationCompleteLoop || mInNotificationFailLoop) {
			mWaitingRemoveListeners.add(loaderListener);
		}
		else {
			mListeners.remove(loaderListener);
		}
	}

	public boolean isActivityStateSaved() {
		return mActivityStateSaved;
	}

	private void fireWaitingTasks() {
		mListeners.removeAll(mWaitingRemoveListeners);
		mWaitingRemoveListeners.clear();
		mListeners.addAll(mWaitingAddListeners);
		mWaitingAddListeners.clear();
	}

	private void updateProgress() {
		showProgress(hasRunningLoaders());
	}

	private void showProgress(final boolean hasRunningLoaders) {
		if (null != mLoaderTaskListener) {
			mLoaderTaskListener.showProgress(hasRunningLoaders);
		}
	}

	private void readObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		try {
			inputStream.defaultReadObject();
		}
		finally {
			mListeners = new HashSet<LoaderTaskListener>();
			mWaitingAddListeners = new HashSet<LoaderTaskListener>();
			mWaitingRemoveListeners = new HashSet<LoaderTaskListener>();
		}
	}
}