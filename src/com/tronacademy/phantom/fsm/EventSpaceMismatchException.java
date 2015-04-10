package com.tronacademy.phantom.fsm;

public class EventSpaceMismatchException extends Exception {
	
	private static final long serialVersionUID = 7130913380099985139L;
	
	private static String st2stErr = "State '%s' cannot transition to state '%s' because they do not listen to the same event space";
	private static String st2evErr = "State '%s' cannot listen to event [%s] because their event spaces do not match";

	public EventSpaceMismatchException(final FsmState srcSt, final FsmState dstSt) {
		super(String.format(st2stErr, srcSt.getName(), dstSt.getName()));
	}
	
	public EventSpaceMismatchException(final FsmState srcSt, final FsmEvent transEv) {
		super(String.format(st2evErr, srcSt.getName(), transEv.getName()));
	}
}
