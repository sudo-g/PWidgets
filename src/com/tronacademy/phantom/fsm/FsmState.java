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
	EventSpace mListenEvSp;
	private FsmState[] mTransitionStates;
	private FsmState mInitInternalState = null;
	
	// Mutable when FSM runs, access carefully
	private FsmState mCurrentState = null;    
	
	/**
	 * @param name      String name for this state.
	 * @param evSp      Handle to the event space this state listens to.
	 * @param intSpSize Size of inner FSM.
	 */
	public FsmState(String name, final EventSpace evSp) {
		mName = name;
		mListenEvSp = evSp;
		
		// pre-allocate transition matrix based on event space size
		mTransitionStates = new FsmState[evSp.getSize()];
	}
	
	/**
	 * Bind a state transition to an event.
	 * 
	 * @param state State to transition to.
	 * @param event Event that will trigger the transition.
	 * @throws EventSpaceMismatchException if transition state or event has wrong event space.
	 */
	protected void bindEventToTransition(final FsmState state, final FsmEvent event) throws 
	EventSpaceMismatchException {
		if (state.getListenEventSpace() == mListenEvSp) {
			if (event.isMemberOf(mListenEvSp)) {	
		 		mTransitionStates[event.getId()] = state;
			} else {
				// event not in event space this state listens to
				throw new EventSpaceMismatchException(this, state);
			}
		} else {
			// new state does not listen to the same event space as this state
			throw new EventSpaceMismatchException(this, state);
		}
	}
	
	/**
	 * Entry hook, override to add action.
	 * 
	 * @param context Mealy machine input. 
	 */
	protected void entryAction(Object... context) {
		
	}
	
	/**
	 * Exit hook, override to add action.
	 * 
	 * @param context Mealy machine input.
	 */
	protected void exitAction(Object... context) {
		
	}
	
	/**
	 * Creates an internal FSM and setups the initial state of it.
	 * 
	 * @param entryState The entry point state of the internal FSM.
	 * @throws EventSpaceConflictException if entry point state listens to same event space.
	 */
	protected void configInternalFsm(final FsmState initState) throws 
	EventSpaceConflictException {
		
		if (initState.getListenEventSpace() != mListenEvSp) {
			mInitInternalState = initState;
			mCurrentState = mInitInternalState;
		} else {
			// internal states cannot listen to same event space as parent
			throw new EventSpaceConflictException(this, initState);
		}
	}
	
	/**
	 * Evaluates the state to transition to based on event.
	 * 
	 * @param event   Event that occurred.
	 * @param context Mealy machine input.
	 * @return State to transition to or null if no transition.
	 */
	public FsmState signalEvent(final FsmEvent event, Object... context) {
		if (event == null) {
			// no meaningful event occurred, so no transition
			return null;
		}
		else {
			// atomic section for run to completion
			synchronized (this) {
				if (event.isMemberOf(mListenEvSp)) {
					// event space match, evaluate transition
	
					// find the new state to transition to
					final FsmState newState = mTransitionStates[event.getId()];
					
					// if transition is to occur
					if (newState != null) {
						exitAction(context);           // perform exit action of this state
						resetInternalState();          // reset internal state from this hierarchy onwards
						newState.entryAction(context); // perform entry action of new state
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
	}
	
	/**
	 * Perform the action of this state
	 * 
	 * @param context Mealy machine input.
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
	
	/** 
	 * Tests whether an internal FSM exists in this state. 
	 * 
	 * @return Flag indicating whether an internal FSM exists. 
	 */
	protected boolean hasInternalState() {
		return (mInitInternalState != null);
	}
	
	/**
	 * Signal event to the current internal state.
	 * A null checking and thread safe wrapper for signalEvent().
	 * 
	 * @param event   Event to signal.
	 * @param context Mealy machine input.
	 * @return The state to transition to.
	 */
	FsmState signalEventToInternalState(FsmEvent event, Object... context) {
		if (hasInternalState()) {
			// called in atomic section, already thread safe
			if (mCurrentState == null) {
				mCurrentState = mInitInternalState;
			}
			return mCurrentState.signalEvent(event, context);
		} else {
			return null;
		}
	}
	
	/**
	 * Execute the internal state action.
	 * A null checking and thread safe wrapper for performAction().
	 * 
	 * @param context Mealy machine input.
	 */
	void performActionOfInternalState(Object... context) {
		if (hasInternalState()) {
			// get a reference copy for thread safety of mCurrentState
			FsmState currentStateCache = mCurrentState;
			
			if (currentStateCache == null) {
				currentStateCache = mInitInternalState;
			}
			currentStateCache.performAction(context);
		}
	}
	
	/**
	 * Set all internal states from this hierarchy onwards, to their initial state.
	 */
	void resetInternalState() {
		if (hasInternalState()) {
			// called in atomic section, already thread safe.
			mCurrentState.resetInternalState();
		}
		mCurrentState = mInitInternalState;
	}
}
