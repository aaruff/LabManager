package edu.nyu.cess.remote.common.app;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class Application implements ApplicationObservable, Serializable {

	private static final long serialVersionUID = 1L;

	ArrayList<ApplicationObserver> observers = new ArrayList<ApplicationObserver>();

	private String name;
	private String path;
	private String args;

	private State currentState;

	private final State startedState, stopedState;

	private Process process;

	private ProcessIOStreamGobbler errorGobbler;
	private ProcessIOStreamGobbler outputGobbler;

	public Thread processMonitor;

	public Application(String name, String path, String args) {
		this.name = name;
		this.path = path;
		this.args = args;

		currentState = new StopedState();
		startedState = new StartedState();
		stopedState = new StopedState();

		process = null;
	}

	public boolean start() {
		boolean execResult = false;

		if (currentState instanceof StopedState) {

			if (process == null) {

				try {
					System.out.println("Attempting to start " + path + name + " " + args);
					process = Runtime.getRuntime().exec(path + name + " " + args);

					if (process != null) {
						currentState = startedState;

						errorGobbler = new ProcessIOStreamGobbler(process.getErrorStream(), "ERROR");
						outputGobbler = new ProcessIOStreamGobbler(process.getInputStream(), "OUTPUT");

						errorGobbler.start();
						outputGobbler.start();

						startProcessMonitor();

						execResult = true;
						System.out.println(name + " has been executed");
					}
					else {
						currentState = stopedState;
					}
				} catch (SecurityException ex) {
					System.out.println("Security Exception Occured");
				} catch (IOException e) {
					System.out.println("Process execution failed for: " + path + name + " " + args);
					currentState = stopedState;
				}
			}
		}
		else if (currentState instanceof StartedState) {
			execResult = true;
		}

		return execResult;
	}

	public synchronized boolean stop() {

		if (process != null) {
			processMonitor.interrupt();
			process.destroy();
		}

		currentState = stopedState;

		process = null;
		args = null;
		name = null;
		path = null;
		outputGobbler = null;
		errorGobbler = null;

		System.out.println("application has terminated.");
		return true;
	}

	public void changeState(State appState) {

		if (appState instanceof StartedState) {
			start();
		}
		else if (appState instanceof StopedState) {
			stop();
		}

		notifyObservers();
	}

	public boolean isStarted() {
		return (currentState instanceof StartedState);
	}

	public boolean isStopped() {
		return (currentState instanceof StopedState);
	}

	public void addObserver(ApplicationObserver observer) {
		observers.add(observer);
	}

	public void deleteObserver(ApplicationObserver observer) {
		observers.remove(observer);
	}

	public void startProcessMonitor() {
		processMonitor = new Thread(new ProcessMonitor());
		processMonitor.start();

	}

	public synchronized void notifyObservers() {
		State state;
		if (currentState instanceof StartedState) {
			state = new StartedState();
		}
		else {
			state = new StopedState();
		}

		for (ApplicationObserver observer : observers) {
			observer.applicationUpdate(state);
		}
	}

	private class ProcessMonitor implements Runnable {

		public void run() {

			try {
				if (process != null) {
					System.out.println("Monitoring process...");
					process.waitFor();

					//getErrorGobbler().join(); // handle condition where the
					//getOutputGobbler().join(); // process ends before the threads finish

					if (currentState instanceof StartedState) {
						stop();
						notifyObservers();
					}
				}
				else {
					System.out.println("******************************");
					System.out.println("process null, what the hell!");
					System.out.println("******************************");
				}
			} catch (InterruptedException e) {
				System.out.println("Process execution interrupted.");
			}

			System.out.println("Process destroyed on client.");
			// send message here to server
		}

	}

}
