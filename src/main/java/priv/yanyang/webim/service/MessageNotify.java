package priv.yanyang.webim.service;


import java.util.Observable;

public class MessageNotify extends Observable {

    public void notifyAll(Object o){
        setChanged();
        notifyObservers(o);
    }

}
