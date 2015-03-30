package com.tronacademy.phantom.utils;

import android.view.View;

/**
 * <p>
 * PWidgetListener.java
 * </p>
 * 
 * <p>
 * Callbacks for events on ControlInputs.
 * </p>
 * 
 * @author George Xian
 * @since 2015-03-22
 *
 */
public interface ControlInputListener {
	
	/**
	 * Called when a sub-channel value of a Phantom widget changes.
	 * 
	 * @param view       The widget instance broadcasting this event.         
	 * @param subchannel The sub-channel being changed.
	 * @param value      The value the sub-channel is being changed to.
	 */
	public void onSubChanValChanged(View view, int subchannel, byte value);
	
	/**
	 * For trackable type widgets, called when user starts holding the tracker.
	 * 
	 * @param view       The widget instance broadcasting this event.
	 */
	public void onStartTracking(View view);
	
	/**
	 * For trackable type widgets, called when user releases the tracker.
	 * 
	 * @param view       The widget instance broadcasting this event.
	 */
	public void onReleaseTracking(View view);
	
	/**
	 * For trackable type widgets, called upon the tracker reaching its 
	 * allowed travel boundary.
	 * 
	 * @param view       The widget instance broadcasting this event.
	 */
	public void onTrackerHitBoundary(View view);
	
	/**
	 * For trackable type widgets, called upon the tracker moving away 
	 * from its allowed travel boundary.
	 * 
	 * @param view       The widget instance broadcasting this event.
	 */
	public void onTrackerLeaveBoundary(View view);
	
	/**
	 * Called upon a sub-channel value reaching its control limit value.
	 * 
	 * @param view       The widget instance broadcasting this event.
	 * @param subchannel The sub-channel whose's value is at its limit.
	 * @param dir        False for lower limit, true for upper limit.
	 */
	public void onSubChanHitLimit(View view, int subchannel, boolean dir);
	
	/**
	 * Called upon a sub-channel value moving away from its control limit value.
	 * 
	 * @param view       The widget instance broadcasting this event.
	 * @param subchannel The sub-channel whose's value.
	 * @param dir        False for lower limit, true for upper limit.
	 */
	public void onSubChanLeaveLimit(View view, int subchannel, boolean dir);

}
