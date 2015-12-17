package edu.nyu.cess.remote.common.app;

import java.io.Serializable;

public class ExeRequestMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final String path;
	private final String args;

	private State applicationState;

	public ExeRequestMessage(String name, String path, String args, State applicationState) {
		this.name = name;
		this.path = path;
		this.args = args;

		this.applicationState = applicationState;
	}

	public State getApplicationState() {
		return applicationState;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getArgs() {
		return args;
	}

	public void setApplicationState(State applicationState) {
		this.applicationState = applicationState;
	}

}
