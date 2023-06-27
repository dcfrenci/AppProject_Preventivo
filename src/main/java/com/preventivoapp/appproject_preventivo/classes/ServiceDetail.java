package com.preventivoapp.appproject_preventivo.classes;

import java.util.List;

public class ServiceDetail {
    private Service chosenService;
    private List<Integer> chosenTeeth;
    private int timeSelected;

    public ServiceDetail() {
        this.chosenService = null;
        this.timeSelected = 0;
    }

    public ServiceDetail(Service chosenService) {
        this.chosenService = chosenService;
        this.timeSelected = 1;
    }
    public ServiceDetail(Service chosenService, List<Integer> chosenTeeth, int timeSelected){
        this.chosenService = chosenService;
        this.chosenTeeth = chosenTeeth;
        this.timeSelected = timeSelected;
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
            if (!string.isEmpty()) string.append(", ");
            string.append(elem);
        }
        return string.toString();
    }
    @Override
    public String toString() {
        return "\t\tService: " + getChosenService().getServiceName() + " (name), " + getChosenService().getServicePrice() + " (price), " + getChosenService().getServicePriceForTooth() + " (toothPrice)\n" +
                "\t\t\t" + getTimeSelected() + " (time selected)\n" +
                "\t\t\t" + getChosenTeeth() + " (chosen teeth)\n";
    }

    @Override
    public Object clone(){
        ServiceDetail serviceDetail;
        try {
            serviceDetail = (ServiceDetail) super.clone();
        } catch (CloneNotSupportedException e){
            serviceDetail = new ServiceDetail(this.getChosenService(), this.getChosenTeeth(), this.getTimeSelected());
        }
        serviceDetail.chosenService = (Service) this.chosenService.clone();
        return serviceDetail;
    }
}
