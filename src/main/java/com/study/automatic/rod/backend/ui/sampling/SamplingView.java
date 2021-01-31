package com.study.automatic.rod.backend.ui.sampling;


import com.study.automatic.rod.backend.calculation.CalculationKrzychu;
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
    private int sampleRecordCounter = 0;

    private Sample sample;
    private int sliderValueChanged = 0; //pozwala na update wykresów w kolejnej iteracji po zmianie wartości suwaka.
    private ChartLive chartX;
    private ChartLive chartXDelta;       ;
    private ChartLive chartY;
    private ChartLive chartYDelta;
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
    private CalculationKrzychu calculation;
//    private ArrayList<Calculation> wyniki;

    double x=200, y=200, z=10, temp=20, xAlpha=0.00017, yAlpha =0.00012;

    public SamplingView(SampleService sampleService, RecordService recordService){
        this.paperSlider = new PaperSlider();
        paperSlider.setValue((int)temp);
        sliderValueLabel = new Label(Integer.toString((int)temp));

        ui = UI.getCurrent();

        this.sampleService = sampleService;
        this.recordService = recordService;
        addClassName("test-view");
        setSizeFull();
        add(createSimulationButtonLayout(), createSlider(), sliderValueLabel, createChartsLayout());

    }

    private VerticalLayout createChartsLayout() {
        //initialize charts
        chartX = new ChartLive("x");
        chartTotal = new ChartLive("totalLength");
        chartY = new ChartLive("y");

        chartXDelta = new ChartLive("xDelta");
        chartTotalDelta = new ChartLive("totalDelta");
        chartYDelta = new ChartLive("yDelta");

        chartTemp = new ChartLive("temp");
        chartTempDelta = new ChartLive("tempDelta");

        //initialize layout array
        HorizontalLayout[] chartsLevels = new HorizontalLayout[3];
        for (int i=0; i<chartsLevels.length; i++) {
            chartsLevels[i] = new HorizontalLayout();
        }

        chartsLevels[0].add(chartX, chartTotal, chartY);
        chartsLevels[1].add(chartXDelta, chartTotalDelta, chartYDelta);
        chartsLevels[2].add(chartTemp, chartTempDelta);

        VerticalLayout chartsLayout = new VerticalLayout();
        for (HorizontalLayout level: chartsLevels){
            chartsLayout.add(level);
        }//for

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

        startButton = new Button("Start simulation");
        stopButton = new Button("Stop simulation");

        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        stopButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

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
        paperSlider.setMin(-100);
        paperSlider.setMax(200);
        paperSlider.setPin(true);

        connectSliderWithCharts();
        return paperSlider;
    }

    private void connectSliderWithCharts(){//listener-----------------------------------------------------------------TU USTAWIAMY ZALEZNOSCI SLIDER WYKRESY
        paperSlider.addValueChangeListener(e -> {
            sliderValueLabel.setText(e.getValue().toString());

            sliderValueChanged = 3;//mogłoby być 2 odświeżenia, gdyby nie calculations
        });//e
    }

    private void updateCharts(){
        calculation.setTemp(paperSlider.getValue());
        calculation.recalculateAll();
        //calculation.settActual(paperSlider.getValue());

        chartX.setNextValueDouble(calculation.getX());
        chartXDelta.setNextValueDouble(calculation.getxDelta());
        chartY.setNextValueDouble(calculation.getY());
        chartYDelta.setNextValueDouble(calculation.getyDelta());
        chartTotal.setNextValueDouble(calculation.getTotal());
        chartTotalDelta.setNextValueDouble(calculation.getTotalDelta());
        chartTemp.setNextValueDouble(calculation.getTemp());
        chartTempDelta.setNextValueDouble(calculation.getTempDelta());
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
        chartXDelta.setRunThread(isRunning);
        chartY.setRunThread(isRunning);
        chartYDelta.setRunThread(isRunning);
        chartTotal.setRunThread(isRunning);
        chartTotalDelta.setRunThread(isRunning);
        chartTemp.setRunThread(isRunning);
        chartTempDelta.setRunThread(isRunning);
    }

    private void startSampling() {
        sample = new Sample();
        isThreadRunning = true;

        setChartTreadRunning(isThreadRunning);

        sampleService.save(sample);
        setStopEnable();
        this.calculation = new CalculationKrzychu(x,y,z,xAlpha,yAlpha,temp);
        updateCharts();
    }

    private void stopSampling() {
        isThreadRunning = false;

        setChartTreadRunning(isThreadRunning);

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
}
