package com.mig35.loaderlib.utils;

/**
 * Date: 2/4/14
 * Time: 2:33 PM
 *
 * @author MiG35
 */
public interface LoaderTaskListener {

	void onLoaderResult(int id, Object result);

	void onLoaderError(int id, Exception exception);
}