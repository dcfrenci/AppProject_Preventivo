package com.preventivoapp.appproject_preventivo.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class teethSelectionController {
    @FXML private CheckBox tooth11;
    @FXML private CheckBox tooth12;
    @FXML private CheckBox tooth13;
    @FXML private CheckBox tooth14;
    @FXML private CheckBox tooth15;
    @FXML private CheckBox tooth16;
    @FXML private CheckBox tooth17;
    @FXML private CheckBox tooth18;
    @FXML private CheckBox tooth21;
    @FXML private CheckBox tooth22;
    @FXML private CheckBox tooth23;
    @FXML private CheckBox tooth24;
    @FXML private CheckBox tooth25;
    @FXML private CheckBox tooth26;
    @FXML private CheckBox tooth27;
    @FXML private CheckBox tooth28;
    @FXML private CheckBox tooth31;
    @FXML private CheckBox tooth32;
    @FXML private CheckBox tooth33;
    @FXML private CheckBox tooth34;
    @FXML private CheckBox tooth35;
    @FXML private CheckBox tooth36;
    @FXML private CheckBox tooth37;
    @FXML private CheckBox tooth38;
    @FXML private CheckBox tooth41;
    @FXML private CheckBox tooth42;
    @FXML private CheckBox tooth43;
    @FXML private CheckBox tooth44;
    @FXML private CheckBox tooth45;
    @FXML private CheckBox tooth46;
    @FXML private CheckBox tooth47;
    @FXML private CheckBox tooth48;
    private Map<String, Boolean> teeth;
    private boolean toSave;
    @FXML
    public void initialize(){

    }

    public void setTeethSelectionController(List<Integer> teethSelected){
        toSave = false;
        if (teethSelected != null) {
            this.teeth = listToMap(teethSelected);
        } else {
            this.teeth = voidMap();
        }
    }
    public List<Integer> mapToList(){
        List<Integer> list = new ArrayList<>();
        for(int i = 11; i < 49; i++){
            if (i % 10 == 9){
                i++;
                continue;
            }
            if (teeth.get("tooth" + i)){
                list.add(i);
            }
        }
        return list;
    }
    private Map<String, Boolean> listToMap(List<Integer> list){
        Map<String, Boolean> map = voidMap();
        for(Integer nTooth: list){
            map.replace("tooth" + nTooth, true);
            setSelectedCheckBox("tooth" + nTooth, true);
        }
        return map;
    }
    private Map<String, Boolean> voidMap(){
        Map<String, Boolean> map = new HashMap<>();
        for(int i = 11; i < 49; i++){
            if (i % 10 == 9){
                i++;
                continue;
            }
            map.replace("tooth" + i, false);
            setSelectedCheckBox("tooth" + i, false);
        }
        return map;
    }
    private void setSelectedCheckBox(String string, boolean state){
        switch (string){
            case "tooth11":
                tooth11.setSelected(state);
            case "tooth12":
                tooth12.setSelected(state);
            case "tooth13":
                tooth13.setSelected(state);
            case "tooth14":
                tooth14.setSelected(state);
            case "tooth15":
                tooth15.setSelected(state);
            case "tooth16":
                tooth16.setSelected(state);
            case "tooth17":
                tooth17.setSelected(state);
            case "tooth18":
                tooth18.setSelected(state);
            case "tooth21":
                tooth21.setSelected(state);
            case "tooth22":
                tooth22.setSelected(state);
            case "tooth23":
                tooth23.setSelected(state);
            case "tooth24":
                tooth24.setSelected(state);
            case "tooth25":
                tooth25.setSelected(state);
            case "tooth26":
                tooth26.setSelected(state);
            case "tooth27":
                tooth27.setSelected(state);
            case "tooth28":
                tooth28.setSelected(state);
            case "tooth31":
                tooth31.setSelected(state);
            case "tooth32":
                tooth32.setSelected(state);
            case "tooth33":
                tooth33.setSelected(state);
            case "tooth34":
                tooth34.setSelected(state);
            case "tooth35":
                tooth35.setSelected(state);
            case "tooth36":
                tooth36.setSelected(state);
            case "tooth37":
                tooth37.setSelected(state);
            case "tooth38":
                tooth38.setSelected(state);
            case "tooth41":
                tooth41.setSelected(state);
            case "tooth42":
                tooth42.setSelected(state);
            case "tooth43":
                tooth43.setSelected(state);
            case "tooth44":
                tooth44.setSelected(state);
            case "tooth45":
                tooth45.setSelected(state);
            case "tooth46":
                tooth46.setSelected(state);
            case "tooth47":
                tooth47.setSelected(state);
            case "tooth48":
                tooth48.setSelected(state);
        }
    }

    /*
     * HANDLER of BUTTONS
     */
    private void handleSaveButton(ActionEvent actionEvent){
        toSave = true;
        Stage thisWindow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        thisWindow.close();
    }
    private void handleResetButton(){
        this.teeth = voidMap();
    }
    private void handleCancelButton(ActionEvent actionEvent){
        Stage thisWindow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        thisWindow.close();
    }
}
