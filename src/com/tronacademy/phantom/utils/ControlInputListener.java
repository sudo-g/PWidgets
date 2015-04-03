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
	 * Called when a sub-channel value of a Phantom control input changes.
	 * 
	 * @param view       The control input instance broadcasting this event.         
	 * @param subchannel The sub-channel being changed.
	 * @param value      The value the sub-channel is being changed to.
	 */
	public void onSubChanValChanged(View view, int subchannel, byte value);
	
	/**
	 * For trackable type control inputs, called when user starts holding the tracker.
	 * 
	 * @param view       The control input instance broadcasting this event.
	 */
	public void onStartTracking(View view);
	
	/**
	 * For trackable type control inputs, called when user releases the tracker.
	 * 
	 * @param view       The control input instance broadcasting this event.
	 */
	public void onReleaseTracking(View view);
	
	/**
	 * <p>
	 * For button type control inputs, called upon pressing the button.
	 * </p>
	 * 
	 * <p>
	 * For switch type control inputs, called upon transitioning from
	 * off to on state
	 * </p>
	 * 
	 * <p>
	 * For trackable type control inputs, called upon the tracker reaching its 
	 * allowed travel boundary.
	 * </p>
	 * 
	 * @param view       The control input instance broadcasting this event.
	 */
	public void onTrackerHitBoundary(View view);
	
	/**
	 * <p>
	 * For button type control inputs, called upon pressing the button.
	 * </p>
	 * 
	 * <p>
	 * For switch type control inputs, called upon transitioning from
	 * off to on state
	 * </p>
	 * 
	 * <p>
	 * For trackable type control inputs, called upon the tracker moving away
	 * from its allowed travel boundary.
	 * </p>
	 *  
	 * @param view       The control input instance broadcasting this event.
	 */
	public void onTrackerLeaveBoundary(View view);
}
