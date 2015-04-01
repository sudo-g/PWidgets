package com.tronacademy.phantom.controls;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tronacademy.phantom.fsm.FsmEvent;
import com.tronacademy.phantom.fsm.FsmState;
import com.tronacademy.phantom.utils.ControlInput;
import com.tronacademy.phantom.utils.ControlInputListener;
import com.tronacademy.phantom.controls.R;

/**
 * <p>
 * A ControlInput UI widget that is operated like a joy-stick.
 * </p>
 * 
 * @author George Xian
 * @since 2015-03-22
 *
 */
public class Joystick extends View implements ControlInput {
	
	/* -- Static properties of this ControlInput type -- */
	// Joysticks are 2D control inputs
	private static final int NUM_OF_SUBCHANS = 2;
	
	// FSM properties
	private static final int EV_SPACE_ID = 1;
	private static final int EV_SPACE_SIZE = 4;
	protected static final FsmEvent EVENT_GRIP = new FsmEvent("Grip", EV_SPACE_ID, 0);
	protected static final FsmEvent EVENT_RELEASE = new FsmEvent("Release", EV_SPACE_ID, 1);
	protected static final FsmEvent EVENT_INBOUNDARY = new FsmEvent("OnBoundary", EV_SPACE_ID, 2);
	protected static final FsmEvent EVENT_ONBOUNDARY = new FsmEvent("InBoundary", EV_SPACE_ID, 3);
	
	/* -- Fields -- */
	protected ControlInputListener mControlInputListener;
	
	// graphics
	protected Bitmap mJoystickBaseBmp;
	protected Bitmap mJoystickTrkrBmp;
	protected Paint mJoystickBaseStyle = new Paint();
	protected Paint mJoystickTrkrStyle = new Paint();
	
	// size and position for display in the View's reference frame
	// this reference has (0, 0) at the top left of the view space allocation
	private int mTrkrRad;
	private int mBaseRad;
	protected int mTrkrPosX;
	protected int mTrkrPosY;
	protected int mCenPosX;
	protected int mCenPosY;
	
	// operational limits as a percentage of graphic radius
	protected int BASE_RAD_BOUNDARY_PERC = 70;
	protected int TRKR_RAD_TOUCHABLE_PERC = 100;
	
	// number of pixels extra to the base diameter to allocate for the View
	protected int PADDING = 10;
	
	// state trackers
	private boolean mInitialCentered = false;
	protected JoystickFsm mJoystickFsm = new JoystickFsm();	
	

	/* -- Constructor -- */
	public Joystick(Context context, AttributeSet attrs) {
		super(context, attrs);
		loadGraphics();
	}


	/* -- ControlInput methods -- */
	@Override
	public synchronized void setSubChanVal(int subchannel, byte value) {
		if (subchannel > 0 && subchannel < NUM_OF_SUBCHANS) {
			// TODO:
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
		setClickable(touchable);
	}
	
	@Override
	public void configure() {
		// TODO:
	}
	
	/* -- Sizing methods -- */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// get the co-ordinates of the center of the View
		mCenPosX = getWidth() / 2;
		mCenPosY = getHeight() / 2;
		
		// get size of the bitmap elements
		mTrkrRad = mJoystickTrkrBmp.getHeight() / 2;	
		mBaseRad = mJoystickBaseBmp.getHeight() / 2;
		
		// allocate room for the view and the overhang from the tracker off the base
		int viewSpaceSideLen = 2*mBaseRad + mTrkrRad + 2*PADDING;
		setMeasuredDimension(viewSpaceSideLen, viewSpaceSideLen);
	}
	
	/* -- View methods -- */
	public boolean onTouchEvent(MotionEvent event) {
		final FsmEvent fsmEvent = evaluateEvent(event);
		
		// update state machine
		mJoystickFsm.signalEvent(fsmEvent);
		mJoystickFsm.performAction(event);
		
		// touched but missed tracker, ignore gesture
		if (event.getAction() == MotionEvent.ACTION_DOWN && fsmEvent != EVENT_GRIP) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// on first draw event, center the tracker graphic
		if (!mInitialCentered) {
			mTrkrPosX = mCenPosX;
			mTrkrPosY = mCenPosY;
			
			mInitialCentered = true;
		}
		
		// stored co-ordinates is for centroid, draw co-ordinates from from top left
		final float baseDrawX = (float) (mCenPosX - mBaseRad);
		final float baseDrawY = (float) (mCenPosY - mBaseRad);
		
		final float trkrDrawX = (float) (mTrkrPosX - mTrkrRad);
		final float trkrDrawY = (float) (mTrkrPosY - mTrkrRad);
		
		canvas.drawBitmap(mJoystickBaseBmp, baseDrawX, baseDrawY, mJoystickBaseStyle);
		canvas.drawBitmap(mJoystickTrkrBmp, trkrDrawX, trkrDrawY, mJoystickTrkrStyle);
	}
	
	protected void loadGraphics() {
		Resources res = getContext().getResources();
		mJoystickBaseBmp = (Bitmap) BitmapFactory.decodeResource(res, R.drawable.joystick_base);
		mJoystickTrkrBmp = (Bitmap) BitmapFactory.decodeResource(res, R.drawable.joystick_tracker);
	}
	
	protected int getBaseRad() {
		return mBaseRad;
	}
	
	protected int getTrkrRad() {
		return mTrkrRad;
	}
	
	protected void releaseTracker() {
		mTrkrPosX = mCenPosX;
		mTrkrPosY = mCenPosY;
		
		invalidate();
	}
	
	/* -- FSM helper functions -- */
	private void inBoundaryTrack(MotionEvent event) {
		// track the finger precisely
		mTrkrPosX = (int) event.getX();
		mTrkrPosY = (int) event.getY();
		invalidate();
		
		byte[] chanVal = toSubChanRefFrame(mTrkrPosX, mTrkrPosY);
		if (mControlInputListener != null) {
			mControlInputListener.onSubChanValChanged(getRootView(), 0, chanVal[0]);
			mControlInputListener.onSubChanValChanged(getRootView(), 1, chanVal[1]);
		}
	}
	
	private void onBoundaryTrack(MotionEvent event) {
		// represent vector as if origin was the view center
		final int dx = ((int) event.getX()) - mCenPosX;
		final int dy = ((int) event.getY()) - mCenPosY;
		
		// unit vector multiplied by the boundary radius
		final int touchPolMag = (int) Math.sqrt(dx*dx + dy*dy);
		mTrkrPosX = dx * getTravelRad()/touchPolMag + mCenPosX;
		mTrkrPosY = dy * getTravelRad()/touchPolMag + mCenPosY;
		
		invalidate();
	}
	
	private FsmEvent evaluateEvent(MotionEvent event) {
		// represent vector as if origin was the view center
		final int x = ((int) event.getX()) - mCenPosX;
		final int y = ((int) event.getY()) - mCenPosY;
		final int action = event.getAction();
		
		if (action == MotionEvent.ACTION_DOWN) {			
			// actual region of tracker that is touchable 
			final int touchTrkrRad = TRKR_RAD_TOUCHABLE_PERC*mTrkrRad/100;
			
			if ((x*x + y*y) <= (touchTrkrRad*touchTrkrRad)) {
				return EVENT_GRIP;
			} else {
				// counting missing the tracker as a release is defensive programming
				return EVENT_RELEASE;
			}
		} else if (action == MotionEvent.ACTION_MOVE) {
			if ((x*x + y*y) <= (getTravelRad()*getTravelRad())) {
				return EVENT_INBOUNDARY;
			} else {
				return EVENT_ONBOUNDARY;
			}
			
		} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
			return EVENT_RELEASE;
		} else {
			// counting unexpected occurrences as a release is defensive programming
			return EVENT_RELEASE;
		}
	}
	
	private int[] toAbstractRefFrame(int viewRefX, int viewRefY) {
		// view's y axis has 0 at top, want 0 at bottom
		int[] vect = {viewRefX - mCenPosX, viewRefY - mCenPosY};
		return vect;
	}
	
	private byte[] toSubChanRefFrame(int viewRefX, int viewRefY) {
		int[] abstrRefFrameVect = toAbstractRefFrame(viewRefX, viewRefY);
		byte subChanRefFrameX = (byte) (128 * abstrRefFrameVect[0] / getTravelRad());
		byte subChanRefFrameY = (byte) (128 * abstrRefFrameVect[1] / getTravelRad());
		
		if (subChanRefFrameX >= 128) {
			// asymmetry of byte [-128, 127]
			subChanRefFrameX -= 1;
		}
		
		byte[] subChanRefFrame = {subChanRefFrameX, subChanRefFrameY};
		return subChanRefFrame;
	}
	
	private int getTravelRad() {
		return BASE_RAD_BOUNDARY_PERC*mBaseRad / 100; 
	}
	
	/* -- FSM declarations -- */
	private class JoystickFsm extends FsmState {
		
		// worker states for joy-stick
		private IdleState mIdleState = new IdleState(EV_SPACE_ID, EV_SPACE_SIZE);
		private InBoundaryState mInBoundaryState = new InBoundaryState(EV_SPACE_ID, EV_SPACE_SIZE);
		private OnBoundaryState mOnBoundaryState = new OnBoundaryState(EV_SPACE_ID, EV_SPACE_SIZE);
		
		public JoystickFsm() {
			// this is the hosting state and thus it has a 0 event space because it
			// always defers control to sub-states
			super("Joystick", 0, 0);
			
			try {
				setEntrySubstate(mIdleState);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// couple states
			mIdleState.bindEventToTransition(mInBoundaryState, EVENT_GRIP);
			
			mInBoundaryState.bindEventToTransition(mIdleState, EVENT_RELEASE);
			mInBoundaryState.bindEventToTransition(mOnBoundaryState, EVENT_ONBOUNDARY);
			
			mOnBoundaryState.bindEventToTransition(mIdleState, EVENT_RELEASE);
			mOnBoundaryState.bindEventToTransition(mInBoundaryState, EVENT_INBOUNDARY);
		}
		
		/* -- Worker states -- */
		private class IdleState extends FsmState {
			public IdleState(int evSpaceId, int evSpaceSize) {
				super("Idle", evSpaceId, evSpaceSize);
			}
			
			public void performAction(Object... context) {
				super.performAction(context);
				releaseTracker();
			}
			
			public FsmState signalEvent(final FsmEvent event) {
				FsmState newState = super.signalEvent(event);
				
				if (mControlInputListener != null && newState == mInBoundaryState) {
					mControlInputListener.onStartTracking(getRootView());
				}
				
				return newState;
			}
		}
		
		private class InBoundaryState extends FsmState {
			public InBoundaryState(int evSpaceId, int evSpaceSize) {
				super("In Boundary", evSpaceId, evSpaceSize);
			}
			
			public void performAction(Object... context) {
				super.performAction(context);
				inBoundaryTrack((MotionEvent) context[0]);
			}
			
			public FsmState signalEvent(final FsmEvent event) {
				FsmState newState = super.signalEvent(event);
				
				if (mControlInputListener != null) {
					if (newState == mOnBoundaryState) {
						mControlInputListener.onTrackerHitBoundary(getRootView());
						
						// TODO: evaluate whether a sub-channel also hit its limit
	
					} else if (newState == mIdleState) {
						mControlInputListener.onReleaseTracking(getRootView());
					}
				}
				
				return newState;
			}
		}
		
		private class OnBoundaryState extends FsmState {
			public OnBoundaryState(int evSpaceId, int evSpaceSize) {
				super("On Boundary", evSpaceId, evSpaceSize);
			}
			
			public void performAction(Object... context) {
				super.performAction(context);
				onBoundaryTrack((MotionEvent) context[0]);
			}
			
			public FsmState signalEvent(final FsmEvent event) {
				FsmState newState = super.signalEvent(event);
				
				if (mControlInputListener != null && newState == mIdleState) {
					mControlInputListener.onReleaseTracking(getRootView());
				}
				
				return newState;
			}
		}
	}
}
