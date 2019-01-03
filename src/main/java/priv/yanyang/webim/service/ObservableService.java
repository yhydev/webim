package priv.yanyang.webim.service;

import java.util.Observable;
import java.util.Observer;

public interface ObservableService<T> {

    void removeThenNotifyObservable(String channel,T t);

    void addObserver(String t, Observer observer);

    void deleteObserver(String t, Observer observer);


}
