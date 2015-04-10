package com.tronacademy.phantom.fsm;

/**
 * 
 * 
 * @author George Xian
 * @since 2015-04-08
 * 
 */
public class FsmStateOrtho extends FsmState {

	public FsmStateOrtho(String name, EventSpace evSp, int intSpSize) {
		super(name, evSp, intSpSize);
	}
	
	@Override
	FsmState signalEventToInternalState(FsmEvent event, Object... context) {
		if (hasInternalState()) {
			for (FsmState state : mInnerStates) {
				if (state != null) {
					return state.signalEvent(event, context);
				}
			}
			return null;
		} else {
			return null;
		}
	}
	
	@Override
	void performActionOfInternalState(Object... context) {
		if (hasInternalState()) {
			for (FsmState state : mInnerStates) {
				if (state != null) {
					state.performAction(context);
				}
			}
		}
	}
}
