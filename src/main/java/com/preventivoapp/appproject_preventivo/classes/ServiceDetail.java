package com.preventivoapp.appproject_preventivo.classes;

import java.util.List;

public class ServiceDetail {
    private Service chosenService;
    private List<Integer> chosenTeeth;

    public ServiceDetail(Service chosenService) {
        this.chosenService = chosenService;
    }

    public Service getChosenService() {
        return chosenService;
    }

    public void setChosenService(Service chosenService) {
        this.chosenService = chosenService;
    }

    public List<Integer> getChosenTeeth() {
        return chosenTeeth;
    }

    public void setChosenTeeth(List<Integer> chosenTeeth) {
        this.chosenTeeth = chosenTeeth;
    }
}
