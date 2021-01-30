package com.study.automatic.rod.backend.ui.material;

import com.study.automatic.rod.backend.entity.Material;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class MaterialForm extends FormLayout {
    private Material material;

    TextField name = new TextField("name");
    NumberField alpha = new NumberField("\u03b1[mm/\u2103]");
    NumberField temp_0 = new NumberField("T_0[\u2103]");
    NumberField length_0 = new NumberField("l_0[\u2103]");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Material> binder = new BeanValidationBinder<>(Material.class);

    public MaterialForm(List<Material> materials){
        addClassName("material-form");
        binder.bindInstanceFields(this);
        createFieldDescriptions();
        add(name, alpha, temp_0, length_0, createButtonsLayout());
    }//MaterialForm()

    private void createFieldDescriptions() {
        name.getElement().setProperty("title", "name of material");
        alpha.getElement().setProperty("title", "linear expansion coefficient");
        temp_0.getElement().setProperty("title", "temperature during first length measurement");
        length_0.getElement().setProperty("title", "first length measurement with known temperature");
    }

    public void setMaterial(Material material){
        this.material = material;
        binder.readBean(material);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, material)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }//createButtonsLayout

    private void validateAndSave() {
        try {
            binder.writeBean(material);
            fireEvent(new SaveEvent(this, material));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }//validateAndSave()

    // Events
    public static abstract class MaterialFormEvent extends ComponentEvent<MaterialForm> {
        private Material material;

        protected MaterialFormEvent(MaterialForm source, Material material) {
            super(source, false);
            this.material = material;
        }

        public Material getMaterial() {
            return material;
        }
    }//MaterialFormEvent()

    public static class SaveEvent extends MaterialFormEvent {
        SaveEvent(MaterialForm source, Material material) {
            super(source, material);
        }
    }

    public static class DeleteEvent extends MaterialFormEvent {
        DeleteEvent(MaterialForm source, Material material) {
            super(source, material);
        }

    }

    public static class CloseEvent extends MaterialFormEvent {
        CloseEvent(MaterialForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}