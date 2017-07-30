package event_control;

class EventSet {
	private Event[] events = new Event[100];
	private int index = 0;
	private int next = 0;
	public boolean add(Event e){
		if (index >= events.length)
			return false;
		else
			events[index++]=e;
			return true;
	}
	public Event getNext(){
		if (next==index)
			return null;
		else
			return events[next];
	}
	public void removeCurrent(){
		events[next]=null;
		next++;
	}
}

public class Controller {
	private EventSet es = new EventSet();
	public void addEvent(Event c){
		es.add(c);
	}
	public void run(){
		Event e;
		while((e=es.getNext())!=null){
			if (e.ready())
				e.action();
				System.out.println(e.description());
				es.removeCurrent();
		}
	}
}