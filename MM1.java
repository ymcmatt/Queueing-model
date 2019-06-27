import java.util.LinkedList;
import java.util.Queue;


public class MM1 extends Device {
	private double slowdown = 0;
	
	//private double LAMBDA;
	private final double TS;
	public double getSlowdown(){
		return this.slowdown;
	}
	
	protected Queue<Request> queue;
	protected LinkedList<Event> schedule;
	
	//saves all the previous requests
	protected LinkedList<Request> log;
	
	//saves the q and w of the queue, at the time the monitor event occurs
	LinkedList<double[]>QandW;
		
	
	public MM1(String name, LinkedList<Event> schedule, double lambda, double ts){
		super(name);
		
		this.queue = new LinkedList<>();
		this.QandW = new LinkedList<>();
		this.log = new LinkedList<>();
		
		this.schedule = schedule;
		this.LAMBDA = lambda;
		this.TS = ts;

	}
	
	public double getTS() {
		return TS;
	}
	public LinkedList<Request> getLog() {
		return log;
	}
	
	public Queue<Request> getQueue() {
		return queue;
	}
	public int getServiceNumber(){
		return log.size();
	}
	
	
	@Override
	 public void SetOutputDevice(Device[] devices, double[] probabilities, double time )
	 {
	  Device device1= devices[0];
	  Device device2= devices[1];
	  double prob= Math.random();
	  Event birth;
	  if(prob<=probabilities[0])
	  {
	    birth=new Event(time+getTimeOfNextBirth(),EventType.BIRTH,device1);
	   birth.setTag(false);
	   schedule.add(birth);
	  }
	  else if(probabilities[0]<prob && prob<=probabilities[1])
	  {
	   birth=new Event(time+getTimeOfNextBirth(),EventType.BIRTH,device2);
	   birth.setTag(false);
	   schedule.add(birth);
	  }
	 }
	
	
	/**
	 * called when a death event happens
	 */
	public void onDeath(double timestamp){
		/**
		 * request has finished and left the mm1
		 */
		Request req = queue.remove();
		req.setEndedProcessing(timestamp);
		log.add(req);
		
		/**
		 * look for another blocked event in the queue that wants to execute and schedule it's death.
		 * at this time the waiting request enters processing time.
		 */
		if(queue.size() > 0){
			double timeOfNextDeath = timestamp + getTimeOfNextDeath();
			queue.peek().setStartedProcessing(timestamp);
			Event event = new Event(timeOfNextDeath, EventType.DEATH, this);
			schedule.add(event);
		}
	}
	
	/**
	 * called when a birth event happens.
	 */
	public void onBirth(Event ev, double timestamp){
		Request request = new Request(timestamp);
		queue.add(request);
		
		
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
	
	/**
	 * called when a monitor event happens
	 */
	public void onMonitor(double timestamp, double startTime){
	
		if(timestamp < startTime) {
			//don't start lagging before the start time
			//clear the logs
			log.clear(); 
			return;
		}

		//count the number of waiting requests
		double w = 0;
		for(Request r: queue){
			if(r.isWaiting()) w++;
		}
		
		System.out.println("Monitor Event at time:" + timestamp);
		System.out.println("---------------------");
		double[] qAndW = new double[2];
		qAndW[0] = queue.size();
		qAndW[1] = w;
		
		QandW.add(qAndW);
				
	}
	
	/**
	 * 
	 * @return time for the next birth event
	 */
	public double getTimeOfNextBirth(){
		return Event.exp(LAMBDA);
	}
	
	/**
	 * 
	 * @return time for the next death event
	 */
	public double getTimeOfNextDeath(){
		return Event.exp(1.0/TS);
	}
	public double getTq(){
		return Tq;
	}
	
	/**
	 * initializes the device with an event
	 */
	@Override
	public void initializeScehduleWithOneEvent() {
		double time = getTimeOfNextBirth();
		Event birthEvent = new Event(time, EventType.BIRTH, this);
		birthEvent.setTag(true);
		schedule.add(birthEvent);	
	}

	@Override
	public void printStats() {
		double Tw = 0;
		double Tq = 0;
		double Tqsum = 0;

		for(Request r: log){
			Tw += r.getTw();
			Tq += r.getTq();
			Tqsum += Tq;
		}
		Tq = Tq/log.size();
		Tw = Tw/log.size();
		
		
		double finalQ = 0;
		double finalW = 0;
		
		for(double[] qw: QandW){
			finalQ += qw[0];
			finalW += qw[1];
		}
		
		finalQ = finalQ/QandW.size();
		finalW = finalW/QandW.size();
		
		double slowdown = Tq/TS;
		
		System.out.println("************************************");
		System.out.println("************************************");
		System.out.println("************************************");
		
		System.out.println("Device name: " + getName());
		System.out.println("Tw: "+ Tw);
		System.out.println("Response time: "+ Tq);
		System.out.println("average q over the system is: " + finalQ);
		System.out.println("average w over the system is: " + finalW);
		System.out.println("Slowdown of the system is: " + slowdown);
		System.out.println("Tqsums: " + Tqsum);
		
	}	
	
	double Tw = 0;
	double Tq = 0;
	double Tqsum = 0;
	double finalQ = 0;
	double finalW = 0;
	
	public void printStatsSystem() {

		for(Request r: log){
			Tw += r.getTw();
			Tq += r.getTq();
			Tqsum += Tq;
		}
		Tq = Tq/log.size();
		Tw = Tw/log.size();
		
	
		
		for(double[] qw: QandW){
			finalQ += qw[0];
			finalW += qw[1];
		}
		
		finalQ = finalQ/QandW.size();
		finalW = finalW/QandW.size();
		double TQ = getTq();
		slowdown += Tq/TS;
		
		System.out.println("************************************");
		System.out.println("************************************");
		System.out.println("************************************");
		
		System.out.println("Device name: " + getName());
		System.out.println("Tw: "+ Tw);
		System.out.println("Response time: "+ Tq);
		System.out.println("average q over the system is: " + finalQ);
		System.out.println("average w over the system is: " + finalW);
		System.out.println("Slowdown of the system is: " + slowdown);		
	}
}
