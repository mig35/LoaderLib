package com.mig35.loaderlib.exceptions;

/**
 * Date: 2/4/14
 * Time: 2:35 PM
 *
 * @author MiG35
 */
public class UnhandalableByBaseActivityException extends Exception {

	private static final long serialVersionUID = 2364260348830760585L;

	public UnhandalableByBaseActivityException() {
	}

	public UnhandalableByBaseActivityException(final String detailMessage) {
		super(detailMessage);
	}

	public UnhandalableByBaseActivityException(final String detailMessage, final Throwable throwable) {
		super(detailMessage, throwable);
	}

	public UnhandalableByBaseActivityException(final Throwable throwable) {
		super(throwable);
	}
}