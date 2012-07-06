package edu.nyu.cess.remote.common.app;


public interface ApplicationObservable {

	public void addObserver(ApplicationObserver observer);

	public void deleteObserver(ApplicationObserver observer);

	public void notifyObservers();

}
