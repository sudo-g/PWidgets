package com.tronacademy.phantom.utils;

/**
 * <p>
 * PWidget.java
 * </p>
 * 
 * <p>
 * Methods common to all Phantom ControlInputs.
 * By having these methods as interfaces, 
 * the widgets can still inherit View types.
 * </p>
 * 
 * <p>
 * All ControlInputs have a number of sub-channels can be bound to 
 * input channels of a Mixer.
 * </p>
 * 
 * @author George Xian
 * @since 2015-03-22
 *
 */
public interface ControlInput {
	
	/**
	 * Sets a sub-channel to a particular value.
	 * 
	 * @param subchannel The sub-channel whose value is to be altered.
	 * @param value      The value to set the sub-channel to.
	 */
	public void setSubChanVal(int subchannel, byte value);
	
	/**
	 * Gets the number sub-channels in this particular Phantom widgets.
	 * 
	 * @return The number of sub-channels this Phantom widget has.
	 */
	public int getNumOfSubChans();
	
	/**
	 * Attach listener to control input.
	 * 
	 * @param listener   Listener with user overridden callbacks to use.
	 */
	public void setControlInputListener(ControlInputListener listener);
	
	/**
	 * Sets whether touch input is allowed
	 * 
	 * @param touchable  Set true to enable touch or false to disable
	 */
	public void setTouchable(boolean touchable);

}
