package edu.nyu.cess.remote.server.net;

public interface ClientHostNameObservable
{
    boolean isHostNameSet();
    void notifyHostNameConfirmed();
}
