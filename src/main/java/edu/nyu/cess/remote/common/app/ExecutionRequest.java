package edu.nyu.cess.remote.common.app;

import java.io.Serializable;

public class ExecutionRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final String path;
	private final String args;

	private AppState applicationAppState;

	public ExecutionRequest(String name, String path, String args, AppState applicationAppState) {
		this.name = name;
		this.path = path;
		this.args = args;

		this.applicationAppState = applicationAppState;
	}

	public AppState getApplicationAppState() {
		return applicationAppState;
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

}
