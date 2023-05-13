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
    public boolean getToSave(){
        return toSave;
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
            map.put("tooth" + i, false);
            setSelectedCheckBox("tooth" + i, false);
        }
        return map;
    }
    private String getCheckBoxName(String string){
        StringBuilder toothName = new StringBuilder();
        boolean copy = false;
        for (char elem: string.toCharArray()){
            if (elem == ',')    break;
            if (copy)           toothName.append(elem);
            if (elem == '=')    copy = true;
        }
        return toothName.toString();
    }
    private void setSelectedCheckBox(String string, boolean state){
        switch (string) {
            case "tooth11" -> tooth11.setSelected(state);
            case "tooth12" -> tooth12.setSelected(state);
            case "tooth13" -> tooth13.setSelected(state);
            case "tooth14" -> tooth14.setSelected(state);
            case "tooth15" -> tooth15.setSelected(state);
            case "tooth16" -> tooth16.setSelected(state);
            case "tooth17" -> tooth17.setSelected(state);
            case "tooth18" -> tooth18.setSelected(state);
            case "tooth21" -> tooth21.setSelected(state);
            case "tooth22" -> tooth22.setSelected(state);
            case "tooth23" -> tooth23.setSelected(state);
            case "tooth24" -> tooth24.setSelected(state);
            case "tooth25" -> tooth25.setSelected(state);
            case "tooth26" -> tooth26.setSelected(state);
            case "tooth27" -> tooth27.setSelected(state);
            case "tooth28" -> tooth28.setSelected(state);
            case "tooth31" -> tooth31.setSelected(state);
            case "tooth32" -> tooth32.setSelected(state);
            case "tooth33" -> tooth33.setSelected(state);
            case "tooth34" -> tooth34.setSelected(state);
            case "tooth35" -> tooth35.setSelected(state);
            case "tooth36" -> tooth36.setSelected(state);
            case "tooth37" -> tooth37.setSelected(state);
            case "tooth38" -> tooth38.setSelected(state);
            case "tooth41" -> tooth41.setSelected(state);
            case "tooth42" -> tooth42.setSelected(state);
            case "tooth43" -> tooth43.setSelected(state);
            case "tooth44" -> tooth44.setSelected(state);
            case "tooth45" -> tooth45.setSelected(state);
            case "tooth46" -> tooth46.setSelected(state);
            case "tooth47" -> tooth47.setSelected(state);
            case "tooth48" -> tooth48.setSelected(state);
        }
    }
    private boolean getSelectedCheckBox(String string){
        return switch (string) {
            case "tooth11" -> tooth11.isSelected();
            case "tooth12" -> tooth12.isSelected();
            case "tooth13" -> tooth13.isSelected();
            case "tooth14" -> tooth14.isSelected();
            case "tooth15" -> tooth15.isSelected();
            case "tooth16" -> tooth16.isSelected();
            case "tooth17" -> tooth17.isSelected();
            case "tooth18" -> tooth18.isSelected();
            case "tooth21" -> tooth21.isSelected();
            case "tooth22" -> tooth22.isSelected();
            case "tooth23" -> tooth23.isSelected();
            case "tooth24" -> tooth24.isSelected();
            case "tooth25" -> tooth25.isSelected();
            case "tooth26" -> tooth26.isSelected();
            case "tooth27" -> tooth27.isSelected();
            case "tooth28" -> tooth28.isSelected();
            case "tooth31" -> tooth31.isSelected();
            case "tooth32" -> tooth32.isSelected();
            case "tooth33" -> tooth33.isSelected();
            case "tooth34" -> tooth34.isSelected();
            case "tooth35" -> tooth35.isSelected();
            case "tooth36" -> tooth36.isSelected();
            case "tooth37" -> tooth37.isSelected();
            case "tooth38" -> tooth38.isSelected();
            case "tooth41" -> tooth41.isSelected();
            case "tooth42" -> tooth42.isSelected();
            case "tooth43" -> tooth43.isSelected();
            case "tooth44" -> tooth44.isSelected();
            case "tooth45" -> tooth45.isSelected();
            case "tooth46" -> tooth46.isSelected();
            case "tooth47" -> tooth47.isSelected();
            case "tooth48" -> tooth48.isSelected();
            default -> false;
        };
    }

    /*
     * HANDLER of BUTTONS
     */
    public void handleSaveButton(ActionEvent actionEvent){
        toSave = true;
        Stage thisWindow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        thisWindow.close();
    }
    public void handleResetButton(){
        this.teeth = voidMap();
    }
    public void handleCancelButton(ActionEvent actionEvent){
        Stage thisWindow = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        thisWindow.close();
    }
    public void handleChangeState(ActionEvent actionEvent){
        teeth.put(getCheckBoxName(actionEvent.getSource().toString()), getSelectedCheckBox(getCheckBoxName(actionEvent.getSource().toString())));
    }
}
