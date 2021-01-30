package com.study.automatic.rod.backend.ui.workstation;

import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.service.MaterialService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Comparator;
import java.util.List;

public class ChoosenMaterialForm extends VerticalLayout {
    private MaterialService materialService;
    private Material choosenMaterial = new Material();
    private ChoosenDescription choosenDescription = new ChoosenDescription(new Material("Nowy", 0, 0, 0));
    private ComboBox<Material> materialBox = new ComboBox<>("Material...");

    public ChoosenMaterialForm(MaterialService materialService) {
        this.materialService = materialService;
        addClassName("choosen_material-form");
        setSizeFull();


        List<Material> materials = materialService.findAll();
        materials.sort(Comparator.comparing(Material::getName, String::compareToIgnoreCase));
        materialBox.setItems(materials);
        materialBox.setItemLabelGenerator(Material::getName);
        materialBox.setClearButtonVisible(true);

        add(materialBox);
        add(choosenDescription);
        choosenDescription.setVisible(false);
        setEventListeners();

    }//ChoosenMaterialForm

    private void setEventListeners() {
        //change event listener
        materialBox.addValueChangeListener(event -> {
            if (event.getValue() == null)  {
                choosenDescription.setVisible(false);
            } else {
                choosenDescription.setChoosenMaterial(event.getValue());
                choosenDescription.setVisible(true);

            }
        });
    }

    public ComboBox<Material> getMaterialBox() {
        return materialBox;
    }

    public void setMaterialBox(ComboBox<Material> materialBox) {
        this.materialBox = materialBox;
    }
}
