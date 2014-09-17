package at.fhjoanneum.android;

public class WorkSession {

	private String name;
	private Long deadLine;
	private Integer priority;
	private String addInput;
	private Integer session;
	private Integer duration;
	private Long end = 0L;
	private Long start = 0L;
	private int status = 0;
	
	public WorkSession(String name, Long date, Integer priority, String addInput, Integer session, Integer duration) {
		this.name     = name;
		this.deadLine  = date;
		this.priority = priority;
		this.addInput = addInput;
		this.session = session;
		this.duration = duration;;
		
	}
	
	public String getNameOfWorkSession() {
		return name;
	}
	
	public Long getDeadLineOfWorkSession() {
		return deadLine;
	}
	
	public Integer getPriorityOfWorkSession() {
		return priority;
	}
	
	public String getAddInput() {
		return addInput;
	}

	public Integer getSession() {
		return session;
	}

	public void setSession(Integer session) {
		this.session = session;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}



	public Long getStart() {
		return start;
	}



	public void setStart(long start) {
		this.start = start;
	}


	public Long getEnd() {
		return end;
	}


	public void setEnd(Long end) {
		this.end = end;
	}




	public int getStatus() {
		return status;
	}




	public void setStatus(int status) {
		this.status = status;
	}

}
