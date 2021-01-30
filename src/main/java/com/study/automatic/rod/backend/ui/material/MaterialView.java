package com.study.automatic.rod.backend.ui.material;

import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.service.MaterialService;
import com.study.automatic.rod.backend.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@CssImport("./styles/material-styles.css")
@PageTitle("Material | Automatic")
public class MaterialView extends VerticalLayout {

    private MaterialService materialService;
    private Grid<Material> grid = new Grid<>(Material.class);
    private TextField filterText = new TextField();
    private MaterialForm form;

    public MaterialView(MaterialService service){
        this.materialService = service;
        addClassName("material-view");
        setSizeFull();
        configureGrid();

        form = new MaterialForm(service.findAll());
        form.addListener(MaterialForm.SaveEvent.class, this::saveMaterial);
        form.addListener(MaterialForm.DeleteEvent.class, this::deleteMaterial);
        form.addListener(MaterialForm.CloseEvent.class, e->closeEditor());

        Div content = new Div (grid, form);
        content.addClassName("material-content");
        content.setSizeFull();
        closeEditor();

        add(getToolbar(), content);
        updateList();
    }

    private Component getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e->updateList());

        Button addMaterialButton = new Button("Add material");
        addMaterialButton.getElement().setProperty("title", "This button allow to add new material to list.");
        addMaterialButton.addClickListener(click->addMaterial());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addMaterialButton);
        toolbar.addClassName("material-toolbar");
        return toolbar;
    }//getToolbar()

    private void addMaterial(){
        grid.asSingleSelect().clear();
        editMaterial(new Material());
    }//addMaterial()

    private void configureGrid() {
        grid.addClassName("material-grid");
        grid.setSizeFull();
        grid.setColumns("name", "alpha", "temp_0", "length_0");
        //grid.getColumnByKey("alpha").setHeader("\u03b1[mm/\u2103]");
        //grid.getColumnByKey("temp_0").setHeader("T_0[\u2103]");
        //grid.getColumnByKey("length_0").setHeader("l_0[\u2103]");
        grid.getColumns().forEach(col->col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editMaterial(event.getValue()));

        //Description
        Label nameLabel = new Label("name");
        nameLabel.getElement().setProperty("title", "name of material");
        grid.getHeaderRows().get(0).getCell(grid.getColumnByKey("name")).setComponent(nameLabel);

        Label alphaLabel = new Label("\u03b1[mm/\u2103]");
        alphaLabel.getElement().setProperty("title", "linear expansion coefficient");
        grid.getHeaderRows().get(0).getCell(grid.getColumnByKey("alpha")).setComponent(alphaLabel);

        Label temp_0Label = new Label("T_0[\u2103]");
        temp_0Label.getElement().setProperty("title", "temperature during first length measurement");
        grid.getHeaderRows().get(0).getCell(grid.getColumnByKey("temp_0")).setComponent(temp_0Label);

        Label length_0Label = new Label("l_0[\u2103]");
        length_0Label.getElement().setProperty("title", "first length measurement with known temperature");
        grid.getHeaderRows().get(0).getCell(grid.getColumnByKey("length_0")).setComponent(length_0Label);
    }

    public void editMaterial(Material material){
        if(material==null){
            closeEditor();
        } else {
            form.setMaterial(material);
            form.setVisible(true);
            addClassName("material-editing");
        }//else
    }

    private void closeEditor(){
        form.setMaterial(null);
        form.setVisible(false);
        removeClassName("material-editing");
    }

    private void updateList() {
        grid.setItems(materialService.findAll(filterText.getValue()));
    }

    private void saveMaterial(MaterialForm.SaveEvent event){
        materialService.save(event.getMaterial());
        updateList();
        closeEditor();
    }

    private void deleteMaterial(MaterialForm.DeleteEvent event){
        materialService.delete(event.getMaterial());
        updateList();
        closeEditor();
    }
}