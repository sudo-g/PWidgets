package com.tronacademy.phantom.fsm;

public class EventSpaceConflictException extends Exception {

	private static final long serialVersionUID = -7574395290980140762L;
	
	private static String tmplt = "Internal state (%s) cannot listen to same event space as parent state (%s)";
	
	public EventSpaceConflictException(final FsmState srcSt, final FsmState intSt) {
		super(String.format(tmplt, intSt, srcSt));
	}
}
