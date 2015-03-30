package com.tronacademy.phantom.fsm;

import java.util.concurrent.Semaphore;

/**
 * <p>
 * Framework for a finite state machine (FSM) with nesting functionality.
 * FSMs created from this framework are thread safe.
 * </p>
 * 
 * <p>
 * Each state is itself can host another FSM, this is what gives it 
 * its nesting functionality. To create an FSM, extend from this class
 * to create a host state class. Then declare all the worker state 
 * classes inside the host state. Apply the same patterns for sub-states 
 * further down the hierarchy by declaring them inside their parent 
 * state.
 * </p>
 * 
 * <p>
 * Add custom actions to states by overriding the 
 * {@code performAction()} method. Ensure the {@code super} method is
 * called before custom actions are performed to retain nesting 
 * function. Note that all sub-states effectively inherit the actions
 * of the parent.
 * </p>
 * 
 * <p>
 * Add event hooks by overriding the {@code evaluateTransition()} method.
 * Ensure that the return value of the {@code super} method is still 
 * returned so transition can still occur. A transition hook is 
 * selectively executing actions based on incoming event or the state 
 * being transitioned on the overridden method.
 * </p>
 * 
 * <p>
 * All states in the same FSM (an child FSM is considered a separate 
 * FSM hosted inside one of the states) must share the same event 
 * space. Sub-states cannot share the same event space as its parent. 
 * That way, when an event is evaluated for transition, the state knows 
 * when to delegate the evaluation to a sub-state.
 * </p>
 * 
 * @author George Xian
 * @since 2015-03-26
 *
 */
public abstract class FsmState {
	private String mName;
	private int mEvSpaceId;
	private FsmState[] mTransitionStates;
	private FsmState mEntrySubstate = null;
	
	private FsmState mCurrentState = null;
	
	private Semaphore mEntrySubstateAccessLock = new Semaphore(1);
	
	private final String evIdErrMsg = "Invalid event ID, '%s' allows event IDs within [0, %d), [%s] had ID %d";
	private final String evSpaceErrMsg = "Wrong event space, '%s' operates in event space ID %d, [%s] has event space ID %d";
	private final String sameEvSpaceErrMsg = "Sub-state cannot share same event space as parent";
	
	/**
	 * @param name          String name for this state.
	 * @param evSpaceId     ID for the event space this FSM operates in.
	 * @param evSpaceSize   Size of the event space this FSM operates in.
	 */
	public FsmState(String name, int evSpaceId, int evSpaceSize) {
		mName = name;
		mEvSpaceId = evSpaceId;
		
		// pre-allocate transition matrix based on event space size
		mTransitionStates = new FsmState[evSpaceSize];
	}
	
	/**
	 * Sets the inner state that becomes active immediately upon entry of this state.
	 * 
	 * @param state   State select as entry point.
	 * @throws IllegalArgumentException if state is the same event space as parent.
	 * @throws InterruptedException if changing mEntrySubstate is attempted after it is set. 
	 */
	public synchronized void setEntrySubstate(final FsmState state) throws 
	IllegalArgumentException, InterruptedException {
		if (state.getEvSpaceId() != mEvSpaceId) {
			// prevents mEntrySubstate from changing once it is set
			// so it is always safe to call methods of mCurrentState 
			// after mEntrySubstate found to not be null
			mEntrySubstateAccessLock.acquire(); 
			
			mEntrySubstate = state;
			mCurrentState = mEntrySubstate;
		} else {
			throw new IllegalArgumentException(sameEvSpaceErrMsg);
		}
	}
	
	/**
	 * Bind a state transition to an event.
	 * 
	 * @param state State to transition to.
	 * @param event Event that will trigger the transition.
	 * @throws IndexOutOfBoundsException if event ID is negative or larger than event space.
	 * @throws IllegalArgumentException if event space ID does not match this state.
	 */
	public synchronized void bindEventToTransition(final FsmState state, final FsmEvent event) throws 
	IndexOutOfBoundsException, IllegalArgumentException {
		if (event.getSpaceId() == mEvSpaceId) {
			final int evId = event.getId();
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
	 * Evaluates the state to transition to based on event.
	 * 
	 * @param event Event that occurred.
	 * @return State to transition to or null if no transition.
	 * @throws IndexOutOfBoundsException is event ID is invalid
	 */
	public FsmState signalEvent(final FsmEvent event) throws IndexOutOfBoundsException {
		if (event == null) {
			// no meaningful event occurred, so no transition
			return null;
		}
		else {
			if (event.getSpaceId() == mEvSpaceId) {
				// event space ID matched, evaluate transition
				int evId = event.getId();
				if (evId >= 0 && evId < mTransitionStates.length) {
					// find the new state to transition to
					final FsmState newState = mTransitionStates[evId];
					
					// if transition is to occur, erase internal states from this hierarchy onwards
					if (newState != null) {
						resetInternalState();
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
				
				// signal event to sub-state if it exists
				if (hasInnerState()) {
					final FsmState newState = mCurrentState.signalEvent(event);
					if (newState != null) {
						mCurrentState = newState;
					}
				}
				
				// if control is passed to a sub-state, no transition will occur
				return null;
			}
		}
	}
	
	/**
	 * Perform the action of this state
	 * 
	 * @param context  Information for the state action.
	 */
	public void performAction(Object... context) {
		// perform action of child state if it exists
		if (hasInnerState()) {
			mCurrentState.performAction(context);
		}
	}
	
	/**
	 * @return String name of this state.
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * @return The ID of the event space this state operates in.
	 */
	public int getEvSpaceId() {
		return mEvSpaceId;
	}
	
	/**
	 * @return Current sub-state or null if no internal state.
	 */
	protected FsmState getInnerCurrentState() {
		return mCurrentState;
	}
	
	private boolean hasInnerState() {
		return (mEntrySubstate != null);
	}
	
	private void resetInternalState() {
		// recursively traverse sub-states
		if (hasInnerState()) {
			mCurrentState.resetInternalState();
		}
		
		// action of recursion is to reset internal state
		mCurrentState = mEntrySubstate;
	}
}