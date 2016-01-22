package edu.nyu.cess.remote.server.ui;

public interface ViewActionObserver
{
	void notifyViewObserverStartAppInRangeRequested(String app, String start, String end);
	void messageClient(String message, String ipAddress);
	void messageClientInRange(String message, String lowerBoundHostName, String upperBoundHostName);
	void notifyStopAppInRangeRequested(String start, String end);
	void startApplication(String appName, String ipAddress);
	void stopApplication(String ipAddress);
}
