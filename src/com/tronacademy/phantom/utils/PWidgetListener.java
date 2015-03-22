package com.tronacademy.phantom.utils;

import android.view.View;

/**
 * <p>
 * PWidgetListener.java
 * </p>
 * 
 * <p>
 * Callbacks for events on Phantom widgets
 * </p>
 * 
 * @author George Xian
 * @since 2015-03-22
 *
 */
public interface PWidgetListener {
	
	/**
	 * Called when a sub-channel value of a Phantom widget changes.
	 * 
	 * @param view       The widget instance being affected.         
	 * @param subchannel The sub-channel being changed.
	 * @param value      The value the sub-channel is being changed to.
	 */
	public void onSubChanValChanged(View view, int subchannel, byte value);
	
	/**
	 * For trackable type widgets, called when user starts holding the tracker.
	 * 
	 * @param view       The widget instance being affected.
	 */
	public void onStartTracking(View view);
	
	/**
	 * For trackable type widgets, called when user releases the tracker.
	 * 
	 * @param view       The widget instance being affected.
	 */
	public void onReleaseTracking(View view);
	
	/**
	 * For trackable type widgets, called when tracker hits its defined. 
	 * boundary. 
	 * 
	 * @param view       The widget instance being affected.
	 */
	public void onHitBoundary(View view);
	
	/**
	 * Called when a sub-channel value hits to its control limit value.
	 * 
	 * @param view       The widget instances being affected.
	 * @param subchannel The sub-channel whose's value is at its limit.
	 */
	public void onSubChanLimit(View view, int subchannel);

}
