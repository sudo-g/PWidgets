package com.tronacademy.phantom.fsm;


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
 * classes inside the host state. Apply the same patterns for inner 
 * states deeper in the hierarchy by declaring them inside their 
 * parent state.
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
 * All states in the same FSM (an internal FSM is considered a separate 
 * FSM hosted inside one of the states) must listen to same event space.
 * Internal states cannot listen to same event space as its parent. 
 * That way, when an event is evaluated for transition, the state knows 
 * when to delegate the evaluation to a internal state.
 * </p>
 * 
 * @author George Xian
 * @since 2015-03-26
 *
 */
public abstract class FsmState {
	// Not mutable after FSM runs
	private String mName;
	EventSpace mListenEvSp;   // not private, can be modified by FsmBuilder
	private FsmState[] mTransitionStates;
	private FsmState[] mInnerStates = null;
	private int mInnerStateIndex = 0;
	
	// Mutable when FSM runs, access carefully
	private FsmState mCurrentState = null;    
	
	// Error messages
	private static final String stEvSpErrMsg = "Event space mismatch: '%s' state does not listen to same event space as '%s'.";
	private static final String evEvSpErrMsg = "Event space mismatch: '%s' will not be able to listen to event [%s].";
	private static final String sameEvSpErrMsg = "Internal states cannot listen to same event space as parent.";
	private static final String stSpSizeExErMsg = "Internal state space size exceeded: Adding state '%s' failed, max size of '%s' is %d.";
	
	/**
	 * @param name      String name for this state.
	 * @param evSp      Handle to the event space this state listens to.
	 * @param intSpSize Size of inner FSM.
	 */
	public FsmState(String name, final EventSpace evSp, int intSpSize) {
		mName = name;
		mListenEvSp = evSp;
		
		// pre-allocate transition matrix based on event space size
		mTransitionStates = new FsmState[evSp.getSize()];
		
		// pre-allocate array of internal states
		mInnerStates = new FsmState[intSpSize];
	}
	
	/**
	 * Bind a state transition to an event.
	 * 
	 * @param state State to transition to.
	 * @param event Event that will trigger the transition.
	 * @throws IllegalArgumentException if event space mismatch.
	 */
	protected void bindEventToTransition(final FsmState state, final FsmEvent event) throws 
	IllegalArgumentException {
		if (state.getListenEventSpace() == mListenEvSp) {
			if (event.isMemberOf(mListenEvSp)) {	
		 		mTransitionStates[event.getId()] = state;
			} else {
				String erMsg = String.format(evEvSpErrMsg, mName, event.getName());
				// event not in event space this state listens to
				throw new IllegalArgumentException(erMsg);
			}
		} else {
			// new state does not listen to the same event space as this state
			String erMsg = String.format(stEvSpErrMsg, state.getName(), mName);
			throw new IllegalArgumentException(erMsg);
		}
	}
	
	/**
	 * Add state to the internal FSM.
	 * 
	 * @param state State to add to internal FSM
	 * @throws IllegalArgumentException if inserted state listens to same event space as parent.
	 * @throws IndexOutOfBoundsException if inserting state exceeds state space size.
	 */
	protected void addStateToInternalFsm(final FsmState state) throws 
	IllegalArgumentException, IndexOutOfBoundsException {
		
		if (state.getListenEventSpace() != mListenEvSp) {
			if (mInnerStateIndex < mInnerStates.length) {
				mInnerStates[mInnerStateIndex++] = state;
				
				// entry inner state is the first added internal state
				mCurrentState = mInnerStates[0];
			} else {
				// state space of internal FSM exceeded
				String erMsg = String.format(stSpSizeExErMsg, state.getName(), mName, mInnerStates.length);
				throw new IndexOutOfBoundsException(erMsg);
			}
		} else {
			// internal states cannot listen to same event space as parent
			throw new IllegalArgumentException(sameEvSpErrMsg);
		}
	}
	
	/**
	 * Evaluates the state to transition to based on event.
	 * 
	 * @param event Event that occurred.
	 * @return State to transition to or null if no transition.
	 */
	public FsmState signalEvent(final FsmEvent event, Object... context) {
		if (event == null) {
			// no meaningful event occurred, so no transition
			return null;
		}
		else {
			if (event.isMemberOf(mListenEvSp)) {
				// event space match, evaluate transition

				// find the new state to transition to
				final FsmState newState = mTransitionStates[event.getId()];
				
				// if transition is to occur, erase internal states from this hierarchy onwards
				if (newState != null) {
					resetInternalState();
				}
				
				return newState;
			} else {
				// event space mismatch, evaluate internal state transition
				
				// signal event to sub-state if it exists
				final FsmState newState = signalEventToInternalState(event, context);
				if (newState != null) {
					mCurrentState = newState;
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
		performActionOfInternalState(context);
	}
	
	/**
	 * @return String name of this state.
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * @return The event space this state listens for.
	 */
	public EventSpace getListenEventSpace() {
		return mListenEvSp;
	}
	
	/**
	 * @return Current sub-state or null if no internal state.
	 */
	protected FsmState getCurrentInternalState() {
		return mCurrentState;
	}
	
	private FsmState signalEventToInternalState(FsmEvent event, Object... context) {
		if (hasInternalState()) {
			// get a reference copy as mCurrentState can change
			FsmState currentStateCache = mCurrentState;
			
			if (currentStateCache == null) {
				currentStateCache = mInnerStates[0];
			}
			return currentStateCache.signalEvent(event, context);
		} else {
			return null;
		}
	}
	
	private void performActionOfInternalState(Object... context) {
		if (hasInternalState()) {
			// get a reference copy as mCurrentState can change
			FsmState currentStateCache = mCurrentState;
			
			if (currentStateCache == null) {
				currentStateCache = mInnerStates[0];
			}
			currentStateCache.performAction(context);
		}
	}
	
	private boolean hasInternalState() {
		return (mInnerStates.length > 0);
	}
	
	private void resetInternalState() {
		if (hasInternalState()) {
			// action of recursion is to reset internal state
			mCurrentState = mInnerStates[0];
			
			// traverse internal states
			for (FsmState state : mInnerStates) {
				state.resetInternalState();
			}
		}
	}
}