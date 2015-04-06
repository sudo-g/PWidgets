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
	private static final String evAddErrMsg = "Event space size exceeded: Failed adding event '%s', size is %d.";
	
	/**
	 * @param size Size of the event space.
	 * @throws IllegalArgumentException Negative number specified for event space size.
	 */
	public EventSpace(int size) throws IllegalArgumentException {
		if (size >= 0) {
			mEventSet = new HashSet<FsmEvent>(size);
			mSize = size;
		} else {
			throw new IllegalArgumentException(evSizeErrMsg);
		}
	}
	
	/**
	 * Create an event that is a member of this event space.
	 * 
	 * @param name String name of this event.
	 * @return Reference to event instance created.
	 * @throws IndexOutOfBoundsException event space is full.
	 */
	public FsmEvent newEvent(String name) throws IndexOutOfBoundsException {
		if (mEvIndx < mSize) {
			FsmEvent newEvent = new FsmEvent(name, mEvIndx++);
			mEventSet.add(newEvent);
			
			return newEvent;
		} else {
			System.out.println(String.format("mEvIndx = %d", mEvIndx));
			throw new IndexOutOfBoundsException(String.format(evAddErrMsg, this, name, mSize));
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
