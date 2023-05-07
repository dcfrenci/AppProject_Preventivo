package com.preventivoapp.appproject_preventivo.classes;

import java.util.List;

public class ServiceDetail {
    private Service chosenService;
    private List<Integer> chosenTeeth;
    private int timeSelected = 0;

    public ServiceDetail(Service chosenService) {
        this.chosenService = chosenService;
        this.timeSelected++;
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

    public int getTimeSelected() {
        return timeSelected;
    }

    public void setTimeSelected(int timeSelected) {
        this.timeSelected = timeSelected;
    }

    public String showTeeth(){
        StringBuilder string = new StringBuilder();
        for(Integer elem: getChosenTeeth()){
            string.append(elem).append(", ");
        }
        return string.toString();
    }

    @Override
    public String toString() {
        return "ServiceDetail{" +
                "chosenService=" + chosenService +
                ", chosenTeeth=" + chosenTeeth +
                '}';
    }
}
