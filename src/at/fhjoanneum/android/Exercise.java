package at.fhjoanneum.android;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

public class Exercise implements Serializable {
	private String name;
	private Integer priority;
	private long deadline;
	private String addInput;
	private Integer status;
	private Integer sessions;
	private Integer duration;
	

	public Exercise(String name, int priority, long deadline, Integer sessions, Integer duration) {
		this.name = name;
		this.deadline = deadline;
		this.priority = priority;
		this.sessions = sessions;
		this.duration = duration;
		this.status = 0;
		}
	
	public Exercise(String name, int priority, long deadline, Integer sessions, Integer duration, String addInput) {
		this.name = name;
		this.deadline = deadline;
		this.priority = priority;
		this.addInput = addInput;
		this.sessions = sessions;
		this.duration = duration;
		this.status = 0;
		}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public String getAddInput() {
		return addInput;
	}

	public void setAddInput(String addInput) {
		this.addInput = addInput;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSessions() {
		return sessions;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
}