import java.util.LinkedList;

public class MMN extends MM1 {
	
	private final int N;
	// keep track of how many servers are busy
	private int n=0;
	
	public MMN(String name,LinkedList<Event> schedule, double lambda, double ts,int N)
	{
		super(name,schedule,lambda,ts);
		this.N=N;
	    
	}
	
	@Override
	public void onBirth(Event ev, double timestamp)
	{
		Request request = new Request(timestamp);
		queue.add(request);
		
		/**
		 * if N-n servers are empty then start executing N-n requests directly there is no waiting time.
		 */
			for(Request requests:queue){
				if( n == N){
					break;
				}
				
				if(requests.isWaiting()){
				requests.setStartedProcessing(timestamp);
				Event event = new Event(timestamp + getTimeOfNextDeath(), EventType.DEATH, this);
				schedule.add(event);
				n++;
				}
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
	public void onDeath(double timestamp){
		
		/**
		 * request has finished and left the mm1
		 */
		
		if(queue.size()!=0){
		Request req = queue.remove();
		req.setEndedProcessing(timestamp);
		log.add(req);
		n--;
		
		}
		
		/**
		 * look for another blocked event in the queue that wants to execute and schedule it's death.
		 * at this time the waiting request enters processing time.
		 */
		
			for(Request nextdeath:queue){
				if(nextdeath.isWaiting() && n<N){
					double timeOfNextDeath = timestamp + getTimeOfNextDeath();
					nextdeath.setStartedProcessing(timestamp);
					Event event = new Event(timeOfNextDeath, EventType.DEATH, this);
					schedule.add(event);
					n++;
					//break;
				}
				
			}	
	}
	

}
