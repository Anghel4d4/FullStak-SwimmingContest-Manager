package ro.mpp2025.Observer.observer;

import java.util.ArrayList;
import java.util.List;

public class ObservableImplementat implements Observable{

    List<Observer> lst = new ArrayList<>();

    @Override
    public void addObserver(Observer o) {
        lst.add(o);
    }

    @Override
    public void notifyObservers() {
        for(Observer o:lst){
            o.update();
        }
    }
}