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
	private int mEvIndx = 0;
	private static final String evSizeErrMsg = "Event space size cannot be negative.";
	
	/**
	 * Create an event space of specified size.
	 * The size does not actually limit the addition of events.
	 * However, setting the size to only as large is necessary 
	 * helps conserve memory.
	 * 
	 * @param size Size of the event space.
	 * @throws NegativeArraySizeException if negative number specified for event space size.
	 */
	public EventSpace(int size) throws NegativeArraySizeException {
		if (size >= 0) {
			mEventSet = new HashSet<FsmEvent>(size);
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
	public FsmEvent newEvent(String name) {
		FsmEvent newEvent = new FsmEvent(name, mEvIndx++);
		mEventSet.add(newEvent);
			
		return newEvent;
	}
	
	/**
	 * @return Size of event space.
	 */
	public int getSize() {
		return mEventSet.size();
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
