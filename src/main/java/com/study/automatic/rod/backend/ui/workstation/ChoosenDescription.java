package com.study.automatic.rod.backend.ui.workstation;

import com.study.automatic.rod.backend.entity.Material;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import javax.management.Notification;

public class ChoosenDescription extends VerticalLayout {
    Material choosenMaterial;

    Button resetButton = new Button("reset");
    TextField name = new TextField("name");
    NumberField alpha = new NumberField("\u03b1[mm/\u2103]");
    NumberField temp_0 = new NumberField("T[\u2103]");
    NumberField length_0 = new NumberField("l[\u2103]");

    Double alphaDouble = 0.0;
    Double tempDouble = 0.0;
    Double lengthDouble = 0.0;

    public ChoosenDescription(Material choosenMaterial) {
        addClassName("choosen_description-view");
        setSizeFull();
        setChoosenMaterial(choosenMaterial);

        name.setReadOnly(true);
        alpha.setReadOnly(true);

        defineListeners();
        add(resetButton, name, alpha, temp_0, length_0);

        temp_0.getElement().setProperty("title", "Set actual temperature of material.");
        length_0.getElement().setProperty("title", "You can cut material to prefer length for set temperature");

    }

    private void defineListeners() {
        temp_0.addValueChangeListener(event -> {
            if (event.getValue() != null && event.isFromClient())  {
                Double deltaLength = alphaDouble * lengthDouble * (temp_0.getValue()-tempDouble);
                length_0.setValue(lengthDouble + deltaLength);
            }
        });

        length_0.addValueChangeListener(event -> {
            if (event.getValue() != null && event.isFromClient())  {
                Double newAlpha = alphaDouble*length_0.getValue()/lengthDouble;
                alpha.setValue(newAlpha);
            }
        });

        resetButton.addClickListener(event ->
                setPrimaryValues()
        );
    }

    public void setPrimaryValues(){
        length_0.setValue(choosenMaterial.getLength_0());
        alpha.setValue(choosenMaterial.getAlpha());
        name.setValue(choosenMaterial.getName());
        temp_0.setValue(choosenMaterial.getTemp_0());
        setDoubleValues();
    }

    private void setDoubleValues(){
        tempDouble = choosenMaterial.getTemp_0();
        lengthDouble = choosenMaterial.getLength_0();
        alphaDouble = choosenMaterial.getAlpha();
    }

    public Double getAlpha() {
        return alpha.getValue();
    }

    public Double getLength_0() {
        return length_0.getValue();
    }

    public Material getChoosenMaterial() {
        return choosenMaterial;
    }

    public void setChoosenMaterial(Material choosenMaterial) {
        this.choosenMaterial = choosenMaterial;
        setPrimaryValues();
    }
}
