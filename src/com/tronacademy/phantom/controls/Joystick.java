package com.tronacademy.phantom.controls;

import com.tronacademy.phantom.utils.ControlInput;
import com.tronacademy.phantom.utils.ControlInputListener;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;

/**
 * <p>
 * Joystick.java
 * </p>
 * 
 * <p>
 * A ControlInput UI widget that is operated like a joystick.
 * It operates two sub-channels.
 * 
 * @author George Xian
 * @since 2015-03-22
 *
 */
public class Joystick extends View implements ControlInput {
	
	/* -- Static properties of this ControlInput type -- */
	// Joysticks are 2D control inputs
	private static final int NUM_OF_SUBCHANS = 2;
	
	/* -- Fields -- */
	private ControlInputListener mControlInputListener;
	private byte[] mSubChanVals = new byte[NUM_OF_SUBCHANS];
	
	// graphics
	private Bitmap mJoystickBase;
	private Bitmap mJoystickTracker;

	/* -- Constructor -- */
	public Joystick(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// load graphics for this control input
		Resources res = getContext().getResources();
		mJoystickBase = (Bitmap) BitmapFactory.decodeResource(res, R.drawable.joystick_base);
		mJoystickTracker = (Bitmap) BitmapFactory.decodeResource(res, R.drawable.joystick_tracker);
	}

	/* -- ControlInput methods -- */
	@Override
	public void setSubChanVal(int subchannel, byte value) {
		if (subchannel > 0 && subchannel < NUM_OF_SUBCHANS) {
			mSubChanVals[subchannel] = value;
		}
	}

	@Override
	public int getNumOfSubChans() {
		return NUM_OF_SUBCHANS;
	}

	@Override
	public void setControlInputListener(ControlInputListener listener) {
		mControlInputListener = listener;
	}

	@Override
	public void setTouchable(boolean touchable) {
		// TODO Auto-generated method stub
		
	}

}
