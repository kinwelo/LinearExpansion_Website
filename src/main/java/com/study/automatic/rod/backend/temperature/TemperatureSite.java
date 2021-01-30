package com.study.automatic.rod.backend.temperature;

import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.service.MaterialService;
import com.study.automatic.rod.backend.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Comparator;
import java.util.List;


@Route(value = "calculate_temperature", layout = MainLayout.class)
//@CssImport("./styles/workstation-styles.css")
//@NpmPackage(value = "jquery", version = "3.4.1")
//@JavaScript("./src/jquery-test.js")
@PageTitle("Calculate Temperature \u03b1 | Linear expansion")
public class TemperatureSite extends VerticalLayout {




    public TemperatureSite(MaterialService materialService) {


        Label area1=new Label("This is the first main use of the application. The app will show temperature needed to be maintained in order to obtain a wire from the material available in the database of the selected length. Start at room temperature: 20◦C for the starting length = 200 [mm]. ");//First main use of the application
        Label area2=new Label("Please choose the material:");
        Div message = new Div();
        Icon logoV = new Icon(VaadinIcon.QUESTION_CIRCLE_O);
        logoV.getStyle().set("question_circle_o", "pointer");
        logoV.addClickListener(
                event -> message.setText("Material depends costam!"));
        VerticalLayout layout=new VerticalLayout();

        Label emptyLabel2 = new Label("");
        emptyLabel2.setHeight("2em");

        ComboBox<Material> chooseMaterial=new ComboBox();
        List<Material> materials = materialService.findAll();
        materials.sort(Comparator.comparing(Material::getName, String::compareToIgnoreCase));
        chooseMaterial.setItems(materials);

        chooseMaterial.setItemLabelGenerator(Material::getName);
        NumberField numberField = new NumberField("New length in millimeters");
        numberField.setValue(200d);
        numberField.setHasControls(true);
        numberField.setMinWidth("200px");
        numberField.setMin(200);
        numberField.setMax(500);
        // Create a vertical slider

        logoV.getElement().setProperty("title", "Your material starting length is 0,200m so please choose new length. Remember that for longer material more temperature is needed.");//napisac ladniej
        Double Tstart=20.0;//st C
        Double Lstart= 0.200;//m

        //Double a=0.00013;
        TextArea wynik=new TextArea("Your needed temperature is: ");
        wynik.setReadOnly(true);
        wynik.setMinWidth("600px");

        wynik.setValue("Please choose any material");
        Button click= new Button("Apply changes",buttonClickEvent -> {
////
            Double Tend;
            Double Lend=numberField.getValue();
            //System.out.println(chooseMaterial.getValue().getAlpha());
            Double a=chooseMaterial.getValue().getAlpha();
            Tend=Tstart+((Lend*0.001-Lstart)/(Lstart*a));
            wynik.setValue(Tend +" [◦C]");



        });
        click.setEnabled(false);
        chooseMaterial.addValueChangeListener(event -> {click.setEnabled(true);});







        add(area1);
        layout.addComponentAsFirst(emptyLabel2);
        add(area2);
        add(chooseMaterial,logoV);
        add(numberField);

        add(click);
        add(wynik);

    }




}

