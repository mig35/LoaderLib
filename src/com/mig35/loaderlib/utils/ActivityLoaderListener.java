package com.mig35.loaderlib.utils;

/**
 * Date: 2/4/14
 * Time: 3:24 PM
 *
 * @author MiG35
 */
public interface ActivityLoaderListener extends LoaderTaskListener {

	public LoaderHelper getLoaderHelper();

	public void addLoaderListener(final FragmentToActivityLoaderTaskListener loaderTaskListener);

	public void removeLoaderListener(final FragmentToActivityLoaderTaskListener loaderFragment);

	void showProgress(boolean hasRunningLoaders);
}