package com.mig35.loaderlib.data;

/**
 * Date: 6/24/13
 * Time: 10:51 AM
 *
 * @author MiG35
 */
public class LoaderResult<Result> {

	private Exception mError;
	private Result mResult;

	public LoaderResult(final Result result) {
		mResult = result;
	}

	/**
	 * @return exception which happened during background work. or null if work
	 * was successful.
	 */
	public Exception getException() {
		return mError;
	}

	public void setException(final Exception error) {
		mError = error;
	}

	public Result getResult() {
		return mResult;
	}

	public void setResult(final Result result) {
		mResult = result;
	}
}