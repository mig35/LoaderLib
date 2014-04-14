package com.mig35.loaderlib.utils;

import android.app.Activity;

/**
 * Date: 2/4/14
 * Time: 3:40 PM
 *
 * @author MiG35
 */
public interface FragmentLoaderTaskListener extends LoaderTaskListener {

	Activity getActivity();

	LoaderHelper getLoaderHelper();
}