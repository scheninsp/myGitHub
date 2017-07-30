package event_control;

public abstract class Event {
	private long evtTime;
	public void Event (long eventTime){
		evtTime = eventTime;
	}
	public boolean ready(){
		return System.currentTimeMillis()>evtTime;
	}
	public abstract void action();
	public abstract String description();
}
