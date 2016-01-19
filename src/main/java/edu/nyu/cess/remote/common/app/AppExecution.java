package edu.nyu.cess.remote.common.app;

import java.io.Serializable;

public class AppExecution implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final String path;
	private final String args;

	private AppState appState;

	public AppExecution(String name, String path, String args, AppState appState) {
		this.name = name;
		this.path = path;
		this.args = args;

		this.appState = appState;
	}

	public AppState getState() {
		return appState;
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

	public AppExecution clone(AppState appState) {
		return new AppExecution(name, path, args, appState);
	}
}
