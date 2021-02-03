package com.study.automatic.rod.backend.samplingMarek;

import com.study.automatic.rod.backend.ui.sampling.PaperSlider;
import com.study.automatic.rod.backend.calculation.Calculation;
import com.study.automatic.rod.backend.service.MaterialService;
import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.entity.Record;
import com.study.automatic.rod.backend.entity.Sample;
import com.study.automatic.rod.backend.service.RecordService;
import com.study.automatic.rod.backend.service.SampleService;
import com.study.automatic.rod.backend.ui.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Route(value = "mario", layout = MainLayout.class)
//@CssImport("./styles/workstation-styles.css")
@PageTitle("Obliczanie | Automatic")
@CssImport("./styles/sampling-styles.css")
public class SamplingViewMarek extends VerticalLayout {
    private final UI ui;
    private Thread thread;
    private boolean isThreadRunning = false;

    SampleService sampleService;
    RecordService recordService;

    private Sample sample;
    private final StreamingDataExampleView chart;
    private final StreamingDataExampleView chart2;
    private final StreamingDataExampleView chart3;

    private PaperSlider paperSlider;
    private final Label sliderValueLabel;

    private Button startButton;
    private Button stopButton;
    boolean czyPierwszypoguziku = true;
    private final Calculation calculation;
//    private ArrayList<Calculation> wyniki;

    public SamplingViewMarek(SampleService sampleService, RecordService recordService, MaterialService materialService) {

        double x = 200, y = 200, z = 10, t0 = 20;// potem brane z inputow albo cos Z i T najlepiej
        double xAlpha = 0.00017, yAlpha = 0.00012;


        this.calculation = new Calculation(x, y, z, t0, xAlpha, yAlpha);// po kolei: to tylko dane startowe z inputow: X poczatkowa dlugosc x w milimetrach
        //y - pocz dl. mat. y w milimetrach, Z odleglosc ktora ma byc zachowana miedzy nimi w milimetrach, t - poczatkowa temperatura
        //xAlpha i yAlpha stale wspolczynniki materialow.
        chart = new StreamingDataExampleView();
        chart2 = new StreamingDataExampleView();
        chart3 = new StreamingDataExampleView();
        chart.setNextValueDouble(t0);   //ustawia poczatkowa wartosc
        /*addAttachListener(event -> {
            this.ui = event.getUI();
        });*/
        ui = UI.getCurrent();

        //Ustaw wartości początkowe
        calculation.settPrev(t0);
        chart.setNextValueDouble(calculation.getxPrev());
        chart2.setNextValueDouble(calculation.getPsi());
        chart3.setNextValueDouble(calculation.getyPrev());

        sliderValueLabel = new Label("0 °C");
        this.sampleService = sampleService;
        this.recordService = recordService;
        addClassName("test-view");
        setSizeFull();
        add(createSimulationControlLayout(materialService), createSlider(), sliderValueLabel, createChars());

    }

    private HorizontalLayout createChars() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.add(chart);
        hl.add(chart2);
        hl.add(chart3);
        return hl;
    }

    private void addNewRecord() {
        if (sample != null) {
            Record newRecord = new Record(sample, paperSlider.getValue());
            recordService.save(newRecord);
            System.out.println("Sample " + sample.getId() + " record " + newRecord.getId() + " value " + paperSlider.getValue());

            //notification
            this.ui.access(() -> {
                Notification.show("Sample " + sample.getId() + " record " + newRecord.getId() + " value " + paperSlider.getValue());
            });
            //refreshPlot();
        }//if
    }

//    private HorizontalLayout createSimulationButtonLayout() {
//        HorizontalLayout hl = new HorizontalLayout();
//
//        startButton = new Button("Start simulation");
//        stopButton = new Button("Stop simulation");
//
//        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        stopButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
//
//        hl.add(startButton, stopButton);
//
//        setStartEnable();
//
//        startButton.addClickListener(event -> {
//            startSampling();
//            chart.setRunThread(true);
//        });//start event
//        stopButton.addClickListener(event -> {
//            stopSampling();
//            chart.setRunThread(false);
//        });//stop event
//
//        return hl;
//    }

    private PaperSlider createSlider() {


        this.paperSlider = new PaperSlider();
        paperSlider.setMin(0);
        paperSlider.setMax(20);
        paperSlider.setPin(true);

        connectSliderWithCharts();
        return paperSlider;
    }

    private void connectSliderWithCharts() {//listener-----------------------------------------------------------------TU USTAWIAMY ZALEZNOSCI SLIDER WYKRESY
        paperSlider.addValueChangeListener(e -> {
            sliderValueLabel.setText(e.getValue().toString() + " °C");


            calculation.settActual(e.getValue());
            chart.setNextValueDouble(calculation.getxPrev());//Niech nazwa cie nie myli to aktualna wielkość tylko pod koniec change value ustawia pod tą zmienną wielkość aktualną
            chart2.setNextValueDouble(calculation.getPsi());
            chart3.setNextValueDouble(calculation.getyPrev());
            //chart3.setNextValueDouble(calculation.getPsi());


        });//e
    }

    private void setStartEnable() {
        startButton.setVisible(true);
        startButton.setEnabled(true);
        stopButton.setVisible(false);
        stopButton.setEnabled(false);
        czyPierwszypoguziku = true;
    }

    private void setStopEnable() {
        startButton.setVisible(false);
        startButton.setEnabled(false);
        stopButton.setVisible(true);
        stopButton.setEnabled(true);
    }

    private void startSampling() {
        sample = new Sample();
        isThreadRunning = true;
        sampleService.save(sample);
        setStopEnable();
    }

    private void stopSampling() {
        isThreadRunning = false;
        setStartEnable();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        thread = new Thread(() -> {
            ArrayList<Double> arrayList = new ArrayList<>();
            arrayList.add(0.0);
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isThreadRunning) {
                    addNewRecord();
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        thread.interrupt();
        isThreadRunning = false;
    }

    private HorizontalLayout createSimulationControlLayout(MaterialService materialService ) {

        HorizontalLayout simulationControl = new HorizontalLayout();

        File file = new File("rys1.png");    //both przeniesione
        //File file = new File("src\\main\\webapp\\images\\rys1.png");    //windows
        //File file = new File("src/main/webapp/images/rys1.png");    //linux
        System.out.println("Expecting to find file from " + file.getAbsolutePath());

        Image image = new Image(new StreamResource("rys1.png", () -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                // file not found
                e.printStackTrace();
            }
            return null;
        }), "alt text");

        image.getStyle().set("border", "3px solid #9E9E9E");

        simulationControl.add(materialsAndLength(materialService), image, temperatureAndControls());
        setStartEnable();
        startButton.setEnabled(false);

//        simulationControl.getStyle().set("border", "3px solid #9E9E9E");

        return simulationControl;
    }

    private VerticalLayout materialsAndLength(MaterialService materialService){
        VerticalLayout ml = new VerticalLayout();
        FormLayout materialX = new FormLayout();
        FormLayout materialY = new FormLayout();
        FormLayout distanceZ = new FormLayout();

        ComboBox<Material> chooseMaterialX=new ComboBox<>();
        ComboBox<Material> chooseMaterialY=new ComboBox<>();

        List<Material> materials = materialService.findAll();
        materials.sort(Comparator.comparing(Material::getName, String::compareToIgnoreCase));

//        startButton.setEnabled(false);
        chooseMaterialX.setItems(materials);
        chooseMaterialX.setItemLabelGenerator(Material::getName);
        chooseMaterialY.setItems(materials);
        chooseMaterialY.setItemLabelGenerator(Material::getName);
        chooseMaterialX.setLabel("Wybierz materiał X:");
        chooseMaterialY.setLabel("Wybierz materiał Y:");
        chooseMaterialX.addValueChangeListener(comboBoxMaterialComponentValueChangeEvent -> {
            startButton.setEnabled(!chooseMaterialX.isEmpty() && !chooseMaterialY.isEmpty());
        });
        chooseMaterialY.addValueChangeListener(comboBoxMaterialComponentValueChangeEvent -> {
            startButton.setEnabled(!chooseMaterialX.isEmpty() && !chooseMaterialY.isEmpty());
        });

        NumberField materialLengthX = new NumberField();
        materialLengthX.setValue(200d);
        materialLengthX.setHasControls(true);
        materialLengthX.setStep(1);
        materialLengthX.setLabel("Długość X: ");

        NumberField materialLengthY = new NumberField();
        materialLengthY.setValue(200d);
        materialLengthY.setHasControls(true);
        materialLengthY.setStep(1);
        materialLengthY.setLabel("Długość Y: ");

        NumberField distanceControlZ = new NumberField();
        distanceControlZ.setValue(10d);
        distanceControlZ.setHasControls(true);
        distanceControlZ.setStep(1);
        distanceControlZ.setLabel("Odległość Z: ");

        materialX.add(chooseMaterialX, materialLengthX);
        materialY.add(chooseMaterialY, materialLengthY);
        distanceZ.add(distanceControlZ);

        ml.getStyle().set("border", "3px solid #9E9E9E");
        ml.add(materialX, materialY, distanceZ);

        return ml;
    }

    private VerticalLayout temperatureAndControls(){
        VerticalLayout tc = new VerticalLayout();

        FormLayout temperature = new FormLayout();
        FormLayout buttonsLayout = new FormLayout();

        NumberField temperatureBegin = new NumberField();
        NumberField temperatureMax = new NumberField();
        temperatureMax.setValue(20d);
        temperatureMax.setHasControls(true);
        temperatureMax.setStep(5);
        temperatureMax.setLabel("Temperature max: ");
        temperatureMax.addValueChangeListener(numberFieldDoubleComponentValueChangeEvent -> {
            paperSlider.setMax((int) Math.round(temperatureMax.getValue()));
            temperatureBegin.setMax(temperatureMax.getValue());
        });

        NumberField temperatureMin = new NumberField();
        temperatureMin.setValue(0d);
        temperatureMin.setHasControls(true);
        temperatureMin.setStep(5);
        temperatureMin.setLabel("Temperature min: ");
        temperatureMin.addValueChangeListener(numberFieldDoubleComponentValueChangeEvent -> {
            paperSlider.setMin((int) Math.round(temperatureMin.getValue()));
            temperatureBegin.setMin(temperatureMin.getValue());
        });


        temperatureBegin.setValue(0d);
        temperatureBegin.setHasControls(true);
        temperatureBegin.setStep(5);
        temperatureBegin.setMax(20);
        temperatureBegin.setMin(0);
        temperatureBegin.setLabel("Temperature begin: ");

        temperature.add(temperatureBegin, temperatureMax, temperatureMin);

        Button random = new Button("Randomly");
        Button manual = new Button("Manually");

        random.addClickListener(buttonClickEvent -> {
            random.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            manual.removeThemeVariants(ButtonVariant.LUMO_SUCCESS);
        });

        manual.addClickListener(buttonClickEvent -> {
            manual.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            random.removeThemeVariants(ButtonVariant.LUMO_SUCCESS);
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(random, manual);

        startButton = new Button("Start simulation");
        stopButton = new Button("Stop simulation");

        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        stopButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        startButton.addClickListener(event -> {
            startSampling();
            chart.setRunThread(true);
        });//start event
        stopButton.addClickListener(event -> {
            stopSampling();
            chart.setRunThread(false);
        });//stop event

        buttonsLayout.add(buttons, startButton, stopButton);

        tc.getStyle().set("border", "3px solid #9E9E9E");

        tc.add(temperature, buttonsLayout);

        return  tc;
    }
}
