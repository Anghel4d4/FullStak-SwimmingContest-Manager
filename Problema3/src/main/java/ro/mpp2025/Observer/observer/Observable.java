package ro.mpp2025.Observer.observer;

public interface Observable{
    void addObserver(Observer e);

    void notifyObservers();
}
