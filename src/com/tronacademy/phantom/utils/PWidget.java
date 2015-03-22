package com.tronacademy.phantom.utils;

/**
 * <p>
 * PWidget.java
 * </p>
 * 
 * <p>
 * Methods common to all Phantom widgets.
 * By having these methods as interfaces, 
 * the widgets can still inherit View types.
 * </p>
 * 
 * @author George Xian
 * @since 2015-03-22
 *
 */
public interface PWidget {
	
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

}
