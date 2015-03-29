package com.tronacademy.phantom.fsm;

/** 
 * <p>
 * Container for all attributes associated with 
 * finite state machine events.
 * </p>
 * 
 * @author George Xian
 * @since 2015-03-27
 *
 */
public class FsmEvent {
	public final String mName;
	public final int mSpace;
	public final int mId;
	
	/**
	 * Creates a generic finite state machine event
	 * 
	 * @param name  String name for this event (debugging usage).
	 * @param space ID of the event space this event belongs to.
	 * @param id    Unique identifier within its event space.
	 */
	public FsmEvent(String name, int space, int id) {
		mName = name;
		mSpace = space;
		mId = id;
	}
	
	/**
	 * @return String name of the event.
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * @return ID of this event within its event space.
	 */
	public int getId() {
		return mId;
	}
	
	/**
	 * @return ID of the event space containing this event.
	 */
	public int getSpaceId() {
		return mSpace;
	}
}
