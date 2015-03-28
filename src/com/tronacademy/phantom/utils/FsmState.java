package com.tronacademy.phantom.utils;

import android.annotation.SuppressLint;

/**
 * <p>
 * Framework for a finite state machine with nesting functionality.
 * </p>
 * 
 * <p>
 * All hierarchically similar events from the same FSM must share 
 * the same event space. FsmState enforces this as its constructor
 * requires an event space ID to be specified. An exception is thrown
 * when adding a transition state if the event specified has the wrong
 * event space ID.
 * </p>
 * 
 * <p>
 * Sub-states must operate on a different event space from its parent.
 * When evaluating an event for state transition, if the ID of the event
 * space is different than the one the current state operates on, the
 * event is passed onto the sub-state for evaluation.
 * </p>
 * 
 * <p>
 * FSM's that are not coupled are allowed to operate on event spaces
 * that share the same event space ID even if they are different.
 * </p>
 * 
 * @author George Xian
 * @since 2015-03-26
 *
 */
@SuppressLint("DefaultLocale")
public class FsmState {
	private String mName;
	private int mEvSpaceId;
	private FsmState[] mTransitionStates;
	protected FsmState mEntrySubstate = null;
	
	private FsmState mCurrentState = null;
	
	private final String evIdErrMsg = "Invalid event ID, '%s' allows event IDs between 0 and %d, [%s] had ID %d";
	private final String evSpaceErrMsg = "Wrong event space, '%s' operates in event space ID %d, [%s] had ID %d";
	
	/**
	 * @param name        String name for this state.
	 * @param evSpaceId   ID for the event space this FSM operates in.
	 * @param evSpaceSize Size of the event space this FSM operates in.
	 */
	public FsmState(String mName, int evSpaceId, int evSpaceSize) {
		mEvSpaceId = evSpaceId;
		
		// pre-allocate transition matrix based on event space size
		mTransitionStates = new FsmState[evSpaceSize];
	}
	
	/**
	 * Sets sub-state that FSM takes upon entering this state.
	 * 
	 * @param state Sub-state to enter upon entering this state.
	 */
	public void setEntrySubstate(FsmState state) {
		mEntrySubstate = state;
	}
	
	/**
	 * Specify state to transition to given an event.
	 * 
	 * @param state State instance to transition to.
	 * @param event Event that will trigger the transition.
	 * @throws IndexOutOfBoundsException if event ID is negative or larger than event space.
	 * @throws IllegalArgumentException if event space ID does not match this state.
	 */
	public void addTransitionState(final FsmState state, final FsmEvent event) throws 
	IndexOutOfBoundsException, IllegalArgumentException {
		if (event.getSpaceId() == mEvSpaceId) {
			int evId = event.getId();
			if (evId >= 0 && evId < mTransitionStates.length) {
	 			mTransitionStates[evId] = state;
			} else {
				// event ID larger event space as specified during state creation
				String erMsg = String.format(evIdErrMsg,
						mName, mTransitionStates.length, event.getName(), evId); 
				throw new IndexOutOfBoundsException(erMsg);
			}
		} else {
			// wrong event space
			String erMsg = String.format(evSpaceErrMsg,
					mName, mEvSpaceId, event.getName(), event.getId());
			throw new IllegalArgumentException(erMsg);
		}
	}
	
	/**
	 * Perform basic FSM state actions such as state transitions.
	 * Call this method from derived classes to get nested state functionality.
	 * 
	 * @param info  Information for state action to use. 
	 * @param event Event that occurred.
	 * @return State to transition to or null if no transition.
	 * @throws IndexOutOfBoundsException is event ID is invalid
	 */
	public FsmState performAction(final Object info, final FsmEvent event) throws IndexOutOfBoundsException {
		if (event == null) {
			// no meaningful event occurred, no transition
			return null;
		}
		else {
			if (event.getSpaceId() == mEvSpaceId) {
				// event space ID matched, evaluate transition state 
				int evId = event.getId();
				if (evId >= 0 && evId < mTransitionStates.length) {
					// find the new state to transition to
					FsmState newState = mTransitionStates[evId];
					
					// if transition is to occur, erase internal states from this hierarchy onwards
					if (newState != null) {
						eraseInternalState();
					}
					
					return newState;
					
				} else {
					// invalid event ID case
					String erMsg = String.format(evIdErrMsg,
							mName, mTransitionStates.length, event.getName(), evId);
					
					throw new IndexOutOfBoundsException(erMsg);
				}
			} else {
				// event space ID mis-match, sub-state may be able to evaluate transition
				
				// give default sub-state entry point if not exist as 
				// internal states are reset upon re-entry
				if ((mCurrentState == null) && (mEntrySubstate != null)) {
					mCurrentState = mEntrySubstate;
				}
				
				// perform action of child state if it exists
				if (mCurrentState != null) {
					mCurrentState = mCurrentState.performAction(info, event);
				}
				
				// if control is passed to a sub-state, no transition will occur
				return null;
			}
		}
	}
	
	/**
	 * @return The ID of the event space this state operates in.
	 */
	public int getEvSpaceId() {
		return mEvSpaceId;
	}
	
	private void eraseInternalState() {
		// recursively traverse sub-states
		if (mCurrentState != null) {
			mCurrentState.eraseInternalState();
		}
		
		// action of recursion is to erase internal state
		mCurrentState = null;
	}
}
