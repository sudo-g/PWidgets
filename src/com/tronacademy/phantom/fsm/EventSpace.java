package com.tronacademy.phantom.fsm;


import java.util.HashSet;

/**
 * 
 * @author George Xian
 * @since 2015-04-05
 *
 */
public class EventSpace {
	private HashSet<FsmEvent> mEventSet;
	private int mSize;
	private int mEvIndx = 0;
	
	private static final String evSizeErrMsg = "Event space size cannot be negative.";
	
	
	/**
	 * @param size Size of the event space.
	 * @throws NegativeArraySizeException if negative number specified for event space size.
	 */
	public EventSpace(int size) throws NegativeArraySizeException {
		if (size >= 0) {
			mEventSet = new HashSet<FsmEvent>(size);
			mSize = size;
		} else {
			throw new NegativeArraySizeException(evSizeErrMsg);
		}
	}
	
	/**
	 * Create an event that is a member of this event space.
	 * 
	 * @param name String name of this event.
	 * @return Reference to event instance created.
	 * @throws EventSpaceFullException if event space is full.
	 */
	public FsmEvent newEvent(String name) throws EventSpaceFullException {
		if (mEvIndx < mSize) {
			FsmEvent newEvent = new FsmEvent(name, mEvIndx++);
			mEventSet.add(newEvent);
			
			return newEvent;
		} else {
			throw new EventSpaceFullException(name, mSize);
		}
	}
	
	/**
	 * @return Size of event space.
	 */
	public int getSize() {
		return mSize;
	}
	
	/**
	 * Test whether event is a member of this event space.
	 * 
	 * @param event Event to test.
	 * @return Flag representing whether event is a member.
	 */
	public boolean hasEvent(FsmEvent event) {
		return mEventSet.contains(event);
	}
}

class EventSpaceFullException extends Exception {

	private static final long serialVersionUID = 8816562993558663086L;
	
	private static final String evAddErrMsg = "Event space size exceeded: Failed adding event [%s], event space size was %d.";
	
	public EventSpaceFullException(String evName, int evSpSize) {
		super(String.format(evAddErrMsg, evName, evSpSize));
	}
	
}
