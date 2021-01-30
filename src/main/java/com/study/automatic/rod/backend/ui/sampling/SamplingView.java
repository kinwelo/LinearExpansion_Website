package com.study.automatic.rod.backend.ui.sampling;


import com.study.automatic.rod.backend.calculation.Calculation;
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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;






@Route(value = "test", layout = MainLayout.class)
//@CssImport("./styles/workstation-styles.css")
@PageTitle("Test | Automatic")
public class SamplingView extends VerticalLayout {
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
    boolean czyPierwszypoguziku=true;
    private final Calculation calculation;
//    private ArrayList<Calculation> wyniki;

    public SamplingView(SampleService sampleService, RecordService recordService){

        double x=200,y=200,z=10,t0=20;// potem brane z inputow albo cos Z i T najlepiej
        double xAlpha=0.00017,yAlpha=0.00012;



        this.calculation = new Calculation(x,y,z,t0,xAlpha,yAlpha);// po kolei: to tylko dane startowe z inputow: X poczatkowa dlugosc x w milimetrach
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

        sliderValueLabel = new Label("elo");
        this.sampleService = sampleService;
        this.recordService = recordService;
        addClassName("test-view");
        setSizeFull();
        add(createSimulationButtonLayout(), createSlider(), sliderValueLabel, createChars());

    }

    private HorizontalLayout createChars(){
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.add(chart);
        hl.add(chart2);
        hl.add(chart3);
        return hl;
    }

    private void addNewRecord(){
        if(sample != null){
            Record newRecord = new Record (sample, paperSlider.getValue());
            recordService.save(newRecord);
            System.out.println("Sample " + sample.getId() + " record " + newRecord.getId() + " value " + paperSlider.getValue());

            //notification
            this.ui.access(() -> {
                Notification.show("Sample " + sample.getId() + " record " + newRecord.getId() + " value " + paperSlider.getValue());
            });
            //refreshPlot();
        }//if
    }

    private HorizontalLayout createSimulationButtonLayout() {
        HorizontalLayout hl = new HorizontalLayout();

        startButton = new Button("Start simulation");
        stopButton = new Button("Stop simulation");

        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        stopButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        hl.add(startButton, stopButton);

        setStartEnable();

        startButton.addClickListener(event -> {
            startSampling();
            chart.setRunThread(true);
        });//start event
        stopButton.addClickListener(event -> {
            stopSampling();
            chart.setRunThread(false);
        });//stop event

        return hl;
    }

    private PaperSlider createSlider(){


        this.paperSlider = new PaperSlider();
        paperSlider.setMin(-100);
        paperSlider.setMax(200);
        paperSlider.setPin(true);

        connectSliderWithCharts();
        return paperSlider;
    }

    private void connectSliderWithCharts(){//listener-----------------------------------------------------------------TU USTAWIAMY ZALEZNOSCI SLIDER WYKRESY
        paperSlider.addValueChangeListener(e -> {
            sliderValueLabel.setText(e.getValue().toString());




            calculation.settActual(e.getValue());
            chart.setNextValueDouble(calculation.getxPrev());//Niech nazwa cie nie myli to aktualna wielkosc tylko pod koniec change value ustawia pod tą zmienną wielkosc aktualną
            chart2.setNextValueDouble(calculation.getPsi());
            chart3.setNextValueDouble(calculation.getyPrev());
            //chart3.setNextValueDouble(calculation.getPsi());


        });//e
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
                if(isThreadRunning){
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
}
