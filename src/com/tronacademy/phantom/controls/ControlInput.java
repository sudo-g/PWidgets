package com.tronacademy.phantom.controls;

/**
 * <p>
 * Methods common to all Phantom ControlInputs.
 * By having these methods as interfaces, the widgets can 
 * still inherit View types.
 * </p>
 * 
 * <p>
 * All ControlInputs have a number of sub-channels.
 * Create a {@code ControlInputListener} and override the
 * {@code onSubChanValChanged()} to specify what these 
 * sub-channels affect.  
 * </p>
 * 
 * @author George Xian
 * @since 2015-03-22
 *
 */
public interface ControlInput {
	
	/**
	 * @return The string name of this ControlInput instance.
	 */
	public String getName();
	
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
	 * Gets the current value of the sub-channel requested
	 * 
	 * @param subchannel The sub-channel of interest.
	 * @return The value of the sub-channel of interest.
	 */
	public byte getSubChanVal(int subchannel);
	
	/**
	 * Attach custom hook to ControlInput events.
	 * 
	 * @param listener   User defined event callbacks for this instance.
	 */
	public void setControlInputListener(ControlInputListener listener);
	
	/**
	 * Sets whether touch input is allowed
	 * 
	 * @param touchable  Set true to enable touch or false to disable
	 */
	public void setTouchable(boolean touchable);
}
