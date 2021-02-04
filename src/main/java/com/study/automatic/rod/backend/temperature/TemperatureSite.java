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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Comparator;
import java.util.List;


@Route(value = "obliczanie_temperatury", layout = MainLayout.class)
//@CssImport("./styles/workstation-styles.css")
//@NpmPackage(value = "jquery", version = "3.4.1")
//@JavaScript("./src/jquery-test.js")
@PageTitle(" Obliczanie temperatury | Automatyka")
public class TemperatureSite extends VerticalLayout {




    public TemperatureSite(MaterialService materialService) {



        add(mainBox(materialService));


    }

private VerticalLayout mainBox(MaterialService materialService){
    VerticalLayout widok=new VerticalLayout();
    VerticalLayout middleLeft1=new VerticalLayout();
    HorizontalLayout mid=new HorizontalLayout();
    HorizontalLayout guz=new HorizontalLayout();
    VerticalLayout midM=new VerticalLayout();
    VerticalLayout middleRight=new VerticalLayout();


    Label area2=new Label("Proszę wybrać materiał:");
    Label area3=new Label("Nowa długość [mm]");
    Label area4=new Label("Oblicz Temperature");
    Icon logoV = new Icon(VaadinIcon.QUESTION_CIRCLE_O);
    logoV.getStyle().set("question_circle_o", "pointer");

    VerticalLayout layout=new VerticalLayout();
    Label emptyLabel2 = new Label("");
    emptyLabel2.setHeight("2em");
    logoV.getElement().setProperty("title", "Startowa długość materiału to 0,2 [m]. Za pomocą interfejsu wybierz docelową długość. Pamietaj, że im wieksza długość, tym większa temperatura będzie potrzebna.");

    Label area1=new Label("To pierwsze główne zastosowanie projektu. Na tej stronie mozesz sprawdzic jaka temperatura jest wymagana do wydlużenia jednego z dostepnych materialów o poczatkowej dlugosc 200 [mm]. Aby otrzymac temperature potrzebną do wydlużenia proszę wybrać materiał oraz nową długość. Temperatura zalezy glownie od współczynnika alfa materialu. Startowa temperatura to  20◦C  " );
    NumberField numberField = new NumberField();
    numberField.setValue(200d);
    numberField.setHasControls(true);
    numberField.setMinWidth("200px");
    numberField.setMin(200);
    numberField.setMax(500);
    ComboBox<Material> chooseMaterial=new ComboBox();
    List<Material> materials = materialService.findAll();
    materials.sort(Comparator.comparing(Material::getName, String::compareToIgnoreCase));
    chooseMaterial.setItems(materials);
    chooseMaterial.setItemLabelGenerator(Material::getName);
    TextArea wynik=new TextArea("Temperatura potrzebna do wydlużenia wynosi: ");
    wynik.setReadOnly(true);
    wynik.setMinWidth("600px");

    wynik.setValue("Proszę wybrać dowolny materiał");

    Button click= new Button("Zatwierdź zmiany",buttonClickEvent -> {
////
        Double Tstart=20.0;//st C
        Double Lstart= 0.200;//m

        Double Tend;
        Double Lend=numberField.getValue();
        //System.out.println(chooseMaterial.getValue().getAlpha());
        Double a=chooseMaterial.getValue().getAlpha();
        Tend=Tstart+((Lend*0.001-Lstart)/(Lstart*a));
        wynik.setValue(Tend +" [◦C]");



    });
    click.setEnabled(false);
    chooseMaterial.addValueChangeListener(event -> {click.setEnabled(true);});


    //tworzenie widoku
    middleLeft1.add(area2,chooseMaterial);//napis wyboru materialu i wybor z listy
    guz.add(area4,logoV);
    middleRight.add(guz,click);
    midM.add(area3,numberField);
    mid.add(middleLeft1,midM);//srdkowy segment z wyborem materialu, nowa dlugosc guzik startu i icona pomocnicza
     widok.add(area1,mid,middleRight,wynik);   //final look


    return widok;
}



}

