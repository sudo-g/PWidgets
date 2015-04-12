package com.tronacademy.phantom.fsm;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A variant of the FSM that allows multiple internal FSMs
 * that run in parallel instead of just one. Each FSM running
 * in parallel are referred to orthogonal regions.
 * </p>
 * 
 * <p>
 * An internal FSM event signal is performed to all internal
 * FSMs. The internal FSMs can listen to the same event space.
 * </p>
 * 
 * <p>
 * For truly parallel state actions, make performAction() perform
 * its actions on another thread and simply start the thread on
 * performAction(). AsyncTask is recommended for Android.
 * </p>
 * 
 * @author George Xian
 * @since 2015-04-08
 * 
 */
public abstract class FsmStateOrtho extends FsmState {
	
	private List<FsmState> mOrthoInternalFsms;
	
	private static final String wrongFsmAddErrMsg = 
			"configInternalFsm() not supported in FsmStateOrtho, please use addParallelFsm().";
	
	private static final String intFsmSizeErrMsg =
			"Cannot have a negative number of orthogonal regions.";

	/**
	 * Create a state that allows multiple internal orthogonal regions.
	 * The size does not actually limit the addition of orthogonal regions.
	 * However, setting the size to only as large as necessary helps
	 * conserve memory.
	 * 
	 * @param name    String name for this state.     
	 * @param evSp    Event space this state listens to.
	 * @param fsmSize Number of parallel FSMs allowed inside this state.
	 */
	public FsmStateOrtho(String name, EventSpace evSp, int fsmSize) throws NegativeArraySizeException{
		super(name, evSp);
		
		if (fsmSize > 0) {
			mOrthoInternalFsms = new ArrayList<FsmState>(fsmSize);
		} else if (fsmSize < 0) {
			throw new NegativeArraySizeException(intFsmSizeErrMsg);
		} else {
			// no internal orthogonal regions (why are you using this class?)
			mOrthoInternalFsms = null;
		}
	}
	
	/**
	 * Creates an orthogonal region inside this state and inserts
	 * an FSM into it.
	 * 
	 * @param fsm FSM contained inside a state.
	 */
	protected void addParallelFsm(final FsmState fsm) {
		mOrthoInternalFsms.add(fsm);
	}
	
	/**
	 * This method originated in the base class and is not supported
	 * in this class.
	 */
	@Override
	protected void configInternalFsm(final FsmState initState) throws
	UnsupportedOperationException {
		throw new UnsupportedOperationException(wrongFsmAddErrMsg);
	}
	
	/**
	 * Signal event to the FSMs in all the orthogonal regions.
	 * 
	 * @param event   Event to signal.
	 * @param context Mealy machine input.
	 */
	@Override
	FsmState signalEventToInternalState(FsmEvent event, Object... context) {
		if (hasInternalState()) {
			for (FsmState state : mOrthoInternalFsms) {
				if (state != null) {
					state.signalEvent(event, context);
				}
			}
			// all internal FSMs are parallel, no transitioning occurs.
			return null;
		} else {
			return null;
		}
	}
	
	/**
	 * Perform the state action of all the FSMs in the orthogonal regions.
	 * 
	 * @param context Mealy machine input.
	 */
	@Override
	void performActionOfInternalState(Object... context) {
		if (hasInternalState()) {
			for (FsmState state : mOrthoInternalFsms) {
				if (state != null) {
					state.performAction(context);
				}
			}
		}
	}
	
	@Override
	protected boolean hasInternalState() {
		return (mOrthoInternalFsms != null && mOrthoInternalFsms.size() > 0);
	}
	
	@Override
	void resetInternalState() {
		if (mOrthoInternalFsms != null) {
			for (FsmState state : mOrthoInternalFsms) {
				if (state != null) {
					state.resetInternalState();
				}
			}
		}
	}
}
