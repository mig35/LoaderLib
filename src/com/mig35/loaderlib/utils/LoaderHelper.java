package com.mig35.loaderlib.utils;

import com.mig35.loaderlib.loaders.AsyncTaskLibLoader;

/**
 * Date: 2/4/14
 * Time: 2:14 PM
 *
 * @author MiG35
 */
public interface LoaderHelper {

	public void removeLoaderFromRunningLoaders(final int loaderId);

	public void removeAllLoadersFromRunningLoaders();

	public <Result> AsyncTaskLibLoader<Result> initAsyncLoader(final int id, final AsyncTaskLibLoader<Result> loader);

	public <Result> AsyncTaskLibLoader<Result> restartAsyncLoader(final int id, final AsyncTaskLibLoader<Result> loader);

	public void destroyAsyncLoader(final int id);

	public void destroyAllAsyncLoaders();

	public boolean hasLoader(final int loaderId);

	public <Result> AsyncTaskLibLoader<Result> getLoader(final int loaderId);

	public int getLoaderId(final String loaderName);

	public boolean hasRunningLoaders();
}