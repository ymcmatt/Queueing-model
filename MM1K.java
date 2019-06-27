import java.util.LinkedList;


public class MM1K extends MM1{

	private final int K;
	private LinkedList<Request> droppedRequests;
	private LinkedList<Request> totalRequests;
	
	public MM1K(String name, LinkedList<Event> schedule, double lambda, double ts,int k) {
		super(name, schedule, lambda, ts);

		this.K = k;
		droppedRequests = new LinkedList<>();
		totalRequests = new LinkedList<>();
	}
	
	@Override
	public void onBirth(Event ev, double timestamp){
		
		Request request = new Request(timestamp);
		totalRequests.add(request);
		
		if(getQueue().size() < K){
			queue.add(request);
		} else {
			droppedRequests.add(request);
		}
		
		/**
		 * if the queue is empty then start executing directly there is no waiting time.
		 */
		if(queue.size() == 1){
			
			request.setStartedProcessing(timestamp);
			Event event = new Event(timestamp + getTimeOfNextDeath(), EventType.DEATH, this);
			schedule.add(event);
		}
		
		if(ev.getTag()){
			/**
			 * schedule the next arrival
			 */
			double time = getTimeOfNextBirth();
			Event event = new Event(timestamp + time, EventType.BIRTH, this);
			event.setTag(true);
			schedule.add(event);
		}
	}
	
	@Override
	public void onMonitor(double timestamp,  double startTime) {
		super.onMonitor(timestamp, startTime);
		
		//clear all the logs before the specified time to start logging.
		if(timestamp < startTime) {
			totalRequests.clear();
			droppedRequests.clear();
			return;
		}
	}
	
	@Override
	public void printStats() {
		super.printStats();
		System.out.println("Total Requests: "+ totalRequests.size());
		System.out.println("Dropped Requests: "+ droppedRequests.size());
		int acceptedReq = totalRequests.size() - droppedRequests.size();
		System.out.println("Accpeted Requests: "+ acceptedReq);
		
		if(totalRequests.size() != 0) System.out.println("Rejection rate: " + droppedRequests.size()*1.0/(totalRequests.size()));
	}

}
