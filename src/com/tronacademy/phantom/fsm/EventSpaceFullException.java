package com.tronacademy.phantom.fsm;

public class EventSpaceFullException extends Exception {

	private static final long serialVersionUID = 8816562993558663086L;
	
	private static final String evAddErrMsg = "Event space size exceeded: Failed adding event [%s], event space size was %d.";
	
	public EventSpaceFullException(String evName, int evSpSize) {
		super(String.format(evAddErrMsg, evName, evSpSize));
	}
	
}