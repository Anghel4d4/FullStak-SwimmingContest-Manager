package main.swimmingservices;


import main.swimmingmodel.Inscriere;
import main.swimmingmodel.User;

public interface ISwimmingObserver {
    void notifyRegister(Inscriere inscriere) throws Exception;
}
