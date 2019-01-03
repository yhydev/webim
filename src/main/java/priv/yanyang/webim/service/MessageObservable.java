package priv.yanyang.webim.service;


import java.util.Observable;

public class MessageObservable extends Observable {
    @Override
    public synchronized void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
        deleteObservers();
    }

}
