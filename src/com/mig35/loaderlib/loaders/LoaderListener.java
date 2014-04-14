package com.mig35.loaderlib.loaders;

/**
 * Date: 2/4/14
 * Time: 2:06 PM
 *
 * @author MiG35
 */
public interface LoaderListener {

	/**
	 * Task started.
	 *
	 * @param id Id of loader
	 */
	void onTaskStart(int id);

	/**
	 * Task completed
	 *
	 * @param id     Id of loader
	 * @param result Result of loader
	 */
	void onTaskComplete(int id, Object result);

	/**
	 * Task failed
	 *
	 * @param id    Id of failed loader
	 * @param error Exception with which loader failed
	 */
	void onTaskFail(int id, Exception error);

	/**
	 * Task reseted
	 *
	 * @param id Id of reseted loader
	 */
	void onTaskDestroy(int id);
}