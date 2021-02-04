package com.study.automatic.rod.backend.ui.sampling;


import com.study.automatic.rod.backend.calculation.CalculationKrzychu;
import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.entity.Record;
import com.study.automatic.rod.backend.entity.Sample;
import com.study.automatic.rod.backend.service.MaterialService;
import com.study.automatic.rod.backend.service.RecordService;
import com.study.automatic.rod.backend.service.SampleService;
import com.study.automatic.rod.backend.ui.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Route(value = "symulacja", layout = MainLayout.class)
//@CssImport("./styles/workstation-styles.css")
@PageTitle("Symulacja | Automatyka")
public class SamplingView extends VerticalLayout {
    private final UI ui;
    private Thread thread;
    private boolean isThreadRunning = false;

    SampleService sampleService;
    RecordService recordService;
    MaterialService materialService;
    private int sampleRecordCounter = 0;

    private Sample sample;
    private int sliderValueChanged = 0; //pozwala na update wykresów w kolejnej iteracji po zmianie wartości suwaka.
    private ChartLive chartX;
    //private ChartLive chartXDelta;       ;
    private ChartLive chartY;
    //private ChartLive chartYDelta;
    private ChartLive chartTotal;
    private ChartLive chartTotalDelta;
    private ChartLive chartTemp;
    private ChartLive chartTempDelta;

    private PaperSlider paperSlider;
    private final Label sliderValueLabel;

    private Button startButton;
    private Button stopButton;
    boolean czyPierwszypoguziku=true;
    //private final Calculation calculation;
    private CalculationKrzychu calculation = new CalculationKrzychu();
//    private ArrayList<Calculation> wyniki;

    //connections calculation - control layout
    private Material materialA = new Material();
    private Material materialB = new Material();
    private double beginX =200, beginY=200, beginZ=10, beginTemp=30, minTemp=0, maxTemp=100;

    private VerticalLayout materialAndLengthLayout;
    private VerticalLayout temperatureLayout;

    //Random input
    boolean isRandomTemperatureInput = false;


    public SamplingView(SampleService sampleService, RecordService recordService, MaterialService materialService){
        this.paperSlider = new PaperSlider();
        sliderValueLabel = new Label("Suwak temperatury układu [°C]:");
        Label descriptionLabel=new Label("Okno symulacyjne regulatora PID, którego zadaniem jest utrzymanie zadanej odległości pomiędzy dwoma materiałami w zmiennej temperaturze. Zależnie od temperatury dystans pomiędzy uchwytami materiałów zmienia się. Temperaturę możemy zadawać własnoręcznie przy pomocy suwaka (przycisk \"Manualnie\" aktywny na zielono) lub po wyborze przycisku \"Losowo\" system będzie generował kolejne wartości. Opcja rozpoczęcia symulacji uaktywni się po wybraniu materiałów A i B.\n" +
                "\n" +
                "Wykres \"Zmiana długości całkowitej układu\" jest kluczowy i obrazuje działanie regulatora prowadzącego bierzące korekty dystansu pomiędzy uchwytami materiałów. Pozostałe wykresy przedstawiają wartości całkowite oraz zmiany w czasie dla poszczególnych składowych układu. W przypadku uciętych opisów dopasować przy pomocy Ctrl +-");

        paperSlider.setMin((int)minTemp);
        paperSlider.setMax((int)maxTemp);
        paperSlider.setValue((int)beginTemp);

        ui = UI.getCurrent();

        this.sampleService = sampleService;
        this.recordService = recordService;
        this.materialService = materialService;
        addClassName("test-view");
        setSizeFull();
        add(descriptionLabel,createSimulationControlLayout(), sliderValueLabel, createSlider(), createChartsLayout());

    }

    private VerticalLayout createChartsLayout() {
        //initialize charts
        chartX = new ChartLive("Długość materiału A [mm]", "X", "#1E90FF");
        chartTotal = new ChartLive("Długość całkowita układu [mm]", "Dł. całkowita", "#1E90FF");
        chartY = new ChartLive("Długość materiału B [mm]", "Y", "#1E90FF");

        //chartXDelta = new ChartLive("Zmiana długości materiału A [mm]", "ΔX", "#1E90FF");
        chartTotalDelta = new ChartLive("Zmiana długości całkowitej układu [mm]", "ΔCałkowita", "#1E90FF");
        //chartYDelta = new ChartLive(" Zmiana długości materiału B [mm]", "ΔX", "#1E90FF");

        chartTemp = new ChartLive("Temperatura [◦C]", "T", "#E91E63");
        chartTempDelta = new ChartLive("Zmiana temperatury [◦C]", "ΔT", "#E91E63");

        String chartWidth = "500px";
        chartX.setWidth(chartWidth);
        chartTotal.setWidth(chartWidth);
        chartY.setWidth(chartWidth);
        //chartXDelta.setWidth(chartWidth);
        chartTotalDelta.setWidth("500px");
        //chartYDelta.setWidth(chartWidth);
        chartTemp.setWidth(chartWidth);
        chartTempDelta.setWidth(chartWidth);

        //initialize layout array
        HorizontalLayout[] chartsLevels = new HorizontalLayout[3];
        for (int i=0; i<chartsLevels.length; i++) {
            chartsLevels[i] = new HorizontalLayout();
            chartsLevels[i].setSizeFull();
        }

        chartsLevels[0].add(chartTemp, chartTotalDelta, chartTempDelta);
        chartsLevels[1].add(chartX, chartTotal, chartY);
        //chartsLevels[2].add(chartTemp, chartTempDelta);

        VerticalLayout chartsLayout = new VerticalLayout();
        for (HorizontalLayout level: chartsLevels){
            chartsLayout.add(level);
        }//for
        //chartsLayout.setWidth("1000px");
        //chartsLayout.setHeight("1000px");
        return chartsLayout;
    }

    private void addNewRecord(){
        if(sample != null){
            Record newRecord = new Record (sample, paperSlider.getValue());
            recordService.save(newRecord);
            System.out.println("Sample " + sample.getId() + " record " + newRecord.getId() + " sampleCounter " + sampleRecordCounter + " value " + paperSlider.getValue());

            //notification
            //this.ui.access(() -> {
            //    Notification.show("Sample " + sample.getId() + " record " + newRecord.getId() + " sampleCounter " + sampleRecordCounter + " value " + paperSlider.getValue());
            //});
            //refreshPlot();
        }//if
    }

    private HorizontalLayout createSimulationButtonLayout() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();

        startButton = new Button("Rozpocznij symulację");
        stopButton = new Button("Zakończ symulację");

        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        stopButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        startButton.setSizeFull();
        stopButton.setSizeFull();

        hl.add(startButton, stopButton);

        setStartEnable();

        startButton.addClickListener(event -> {
            startSampling();

        });//start event
        stopButton.addClickListener(event -> {
            stopSampling();
        });//stop event

        return hl;
    }

    private PaperSlider createSlider(){
        paperSlider.setPin(true);

        connectSliderWithCharts();
        return paperSlider;
    }

    private void connectSliderWithCharts(){//listener-----------------------------------------------------------------TU USTAWIAMY ZALEZNOSCI SLIDER WYKRESY
        paperSlider.addValueChangeListener(e -> {
            //sliderValueLabel.setText(e.getValue().toString());

            sliderValueChanged = 3;//mogłoby być 2 odświeżenia, gdyby nie calculations
        });//e
    }

    private void updateCharts(){
        calculation.setTemp(paperSlider.getValue());

        calculation.recalculateAll();
        //calculation.settActual(paperSlider.getValue());

        chartX.setNextValue1(calculation.getX());
        //chartX.setNextValue2(calculation.getTemp());

        //chartXDelta.setNextValue1(calculation.getxDelta());
        //chartXDelta.setNextValue2(calculation.getTempDelta());

        chartY.setNextValue1(calculation.getY());
        //chartY.setNextValue2(calculation.getTemp());

        //chartYDelta.setNextValue1(calculation.getyDelta());
        //chartYDelta.setNextValue2(calculation.getTempDelta());

        chartTotal.setNextValue1(calculation.getTotal());
        //chartTotal.setNextValue2(calculation.getTemp());

        chartTotalDelta.setNextValue1(calculation.getTotalDelta());
        //chartTotalDelta.setNextValue2(calculation.getTempDelta());

        chartTemp.setNextValue1(calculation.getTemp());
        //chartTemp.setNextValue2(calculation.getTemp());
        chartTempDelta.setNextValue1(calculation.getTempDelta());
        //chartTempDelta.setNextValue2(calculation.getTempDelta());
    }

    private void setStartEnable(){
        startButton.setVisible(true);
        startButton.setEnabled(true);
        stopButton.setVisible(false);
        stopButton.setEnabled(false);
        czyPierwszypoguziku=true;
    }
    private void setStopEnable(){
        startButton.setVisible(false);
        startButton.setEnabled(false);
        stopButton.setVisible(true);
        stopButton.setEnabled(true);
    }

    private void setChartTreadRunning(boolean isRunning){
        chartX.setRunThread(isRunning);
        //chartXDelta.setRunThread(isRunning);
        chartY.setRunThread(isRunning);
        //chartYDelta.setRunThread(isRunning);
        chartTotal.setRunThread(isRunning);
        chartTotalDelta.setRunThread(isRunning);
        chartTemp.setRunThread(isRunning);
        chartTempDelta.setRunThread(isRunning);
    }

    private void startSampling() {
        sample = new Sample();
        isThreadRunning = true;

        setChartTreadRunning(isThreadRunning);
        this.materialAndLengthLayout.setEnabled(false);
        this.temperatureLayout.setEnabled(false);

        sampleService.save(sample);
        setStopEnable();
        this.calculation.setBeginValues(beginX, beginY, beginZ, materialA.getAlpha(), materialB.getAlpha(), beginTemp);
        //System.out.println(calculation.toString());
        updateCharts();
    }

    private void stopSampling() {
        isThreadRunning = false;

        setChartTreadRunning(isThreadRunning);
        this.materialAndLengthLayout.setEnabled(true);
        this.temperatureLayout.setEnabled(true);
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
                if(isThreadRunning){
                    sampleRecordCounter++;
                    addNewRecord();
                    if (isRandomTemperatureInput){
                        Integer randomValue = (int) (Math.round((Math.random() * (paperSlider.getMax() - paperSlider.getMin()))) + paperSlider.getMin());
                        System.out.println("Random: " + randomValue);

                        //dostęp przez ui do slidera
                        this.ui.access(() -> {
                            paperSlider.setValue(randomValue);
                        });

                    }

                    if(sliderValueChanged > 0){
                        updateCharts();
                        sliderValueChanged--;
                    }//if
                } else {
                    sampleRecordCounter = 0;
                }
            }//while
        });//thread
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        thread.interrupt();
        isThreadRunning = false;
    }

    private HorizontalLayout createSimulationControlLayout() {

        HorizontalLayout simulationControlLayout = new HorizontalLayout();

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

        simulationControlLayout.add(materialsAndLength(), image, temperatureAndControls());
        setStartEnable();
        startButton.setEnabled(false);

//        simulationControl.getStyle().set("border", "3px solid #9E9E9E");

        return simulationControlLayout;
    }//createSimulationControlLayout()

    private VerticalLayout materialsAndLength(){
        materialAndLengthLayout = new VerticalLayout();

        FormLayout materialX = new FormLayout();
        FormLayout materialY = new FormLayout();
        FormLayout distanceZ = new FormLayout();

        ComboBox<Material> chooseMaterialX=new ComboBox<>();
        ComboBox<Material> chooseMaterialY=new ComboBox<>();

        chooseMaterialX.setClearButtonVisible(true);
        chooseMaterialY.setClearButtonVisible(true);

        List<Material> materials = materialService.findAll();
        materials.sort(Comparator.comparing(Material::getName, String::compareToIgnoreCase));

//        startButton.setEnabled(false);
        chooseMaterialX.setItems(materials);
        chooseMaterialX.setItemLabelGenerator(Material::getName);
        chooseMaterialY.setItems(materials);
        chooseMaterialY.setItemLabelGenerator(Material::getName);
        chooseMaterialX.setLabel("Wybierz materiał A:");
        chooseMaterialY.setLabel("Wybierz materiał B:");
        chooseMaterialX.addValueChangeListener(e -> {
            materialA = e.getValue();
            startButton.setEnabled(!chooseMaterialX.isEmpty() && !chooseMaterialY.isEmpty());
        });
        chooseMaterialY.addValueChangeListener(e -> {
            materialB = e.getValue();
            startButton.setEnabled(!chooseMaterialX.isEmpty() && !chooseMaterialY.isEmpty());
        });

        NumberField materialLengthX = new NumberField();
        materialLengthX.setValue(beginX);
        materialLengthX.setHasControls(true);
        materialLengthX.setStep(1);
        materialLengthX.setLabel("Długość początkowa A (X)[mm]: ");

        materialLengthX.addValueChangeListener(e->{
            this.beginX = e.getValue();
        });

        NumberField materialLengthY = new NumberField();
        materialLengthY.setValue(beginY);
        materialLengthY.setHasControls(true);
        materialLengthY.setStep(1);
        materialLengthY.setLabel("Długość początkowa B (Y)[mm]: ");

        materialLengthY.addValueChangeListener(e->{
            this.beginY = e.getValue();
        });

        NumberField distanceControlZ = new NumberField();
        distanceControlZ.setValue(beginZ);
        distanceControlZ.setHasControls(true);
        distanceControlZ.setStep(1);
        distanceControlZ.setLabel("Zadana odległość pomiędzy A i B (Z)[mm]: ");

        distanceControlZ.addValueChangeListener(e->{
            this.beginZ = e.getValue();
        });

        materialX.add(chooseMaterialX, materialLengthX);
        materialY.add(chooseMaterialY, materialLengthY);
        distanceZ.add(distanceControlZ);

        materialAndLengthLayout.getStyle().set("border", "3px solid #9E9E9E");
        materialAndLengthLayout.add(materialX, materialY, distanceZ);

        return this.materialAndLengthLayout;
    }//materialsAndLength

    private VerticalLayout temperatureAndControls(){
        VerticalLayout tc = new VerticalLayout();
        tc.setSizeFull();

        temperatureLayout = new VerticalLayout();
        temperatureLayout.setSizeFull();
        FormLayout buttonsLayout = new FormLayout();

        NumberField temperatureBegin = new NumberField();
        NumberField temperatureMax = new NumberField();
        temperatureMax.setValue(maxTemp);
        temperatureMax.setHasControls(true);
        temperatureMax.setStep(5);
        temperatureMax.setLabel("Temperatura maksymalna [°C]: ");
        temperatureMax.addValueChangeListener(numberFieldDoubleComponentValueChangeEvent -> {
            paperSlider.setMax((int) Math.round(temperatureMax.getValue()));
            temperatureBegin.setMax(temperatureMax.getValue());
        });

        NumberField temperatureMin = new NumberField();
        temperatureMin.setValue(minTemp);
        temperatureMin.setHasControls(true);
        temperatureMin.setStep(5);
        temperatureMin.setLabel("Temperatura minimalna [°C]: ");
        temperatureMin.addValueChangeListener(numberFieldDoubleComponentValueChangeEvent -> {
            paperSlider.setMin((int) Math.round(temperatureMin.getValue()));
            temperatureBegin.setMin(temperatureMin.getValue());
        });

        temperatureBegin.setSizeFull();
        temperatureMin.setSizeFull();
        temperatureMax.setSizeFull();

        temperatureBegin.setValue(beginTemp);
        temperatureBegin.setHasControls(true);
        temperatureBegin.setStep(5);
        temperatureBegin.setMax(temperatureMax.getValue());
        temperatureBegin.setMin(temperatureMin.getValue());
        temperatureBegin.setLabel("Temperatura poczatkowa [°C]: ");

        temperatureLayout.add(temperatureBegin, temperatureMax, temperatureMin);

        Label randomManual = new Label("Wybór sposobu zadawania temperatury:");
        Button random = new Button("Losowo");
        Button manual = new Button("Manualnie");
        random.setSizeFull();
        manual.setSizeFull();
        manual.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        random.addClickListener(buttonClickEvent -> {
            random.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            manual.removeThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            paperSlider.setEnabled(false);
            this.isRandomTemperatureInput = true;
        });

        manual.addClickListener(buttonClickEvent -> {
            manual.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            random.removeThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
            paperSlider.setEnabled(true);
            this.isRandomTemperatureInput = false;
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(random, manual);

        buttonsLayout.add(randomManual, buttons, createSimulationButtonLayout());

        tc.getStyle().set("border", "3px solid #9E9E9E");

        tc.add(temperatureLayout, buttonsLayout);

        //change listeners temp
        temperatureBegin.addValueChangeListener(e->{
            paperSlider.setValue((int)Math.round(e.getValue()));
        });//temperatureBegin
        temperatureMax.addValueChangeListener(e->{
            paperSlider.setMax((int)Math.round(e.getValue()));
        });//temperatureBegin
        temperatureMin.addValueChangeListener(e->{
            paperSlider.setMin((int)Math.round(e.getValue()));
        });//temperatureBegin

        return  tc;
    }//temperatureAndControls
}
