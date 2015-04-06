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
	public final int mId;
	
	/**
	 * @param name  String name for this event (debugging usage).
	 * @param id    Unique identifier within its event space.
	 */
	public FsmEvent(String name, int id) {
		mName = name;
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
	 * Finds whether this event is a member of an event space.
	 * 
	 * @param evSp Event space to query for membership
	 * @return Flag representing whether event is a member.
	 */
	public boolean isMemberOf(EventSpace evSp) {
		return evSp.hasEvent(this);
	}
}
