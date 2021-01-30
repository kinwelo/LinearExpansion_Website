package com.study.automatic.rod.backend.ui.workstation;

import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.entity.Record;
import com.study.automatic.rod.backend.entity.Sample;
import com.study.automatic.rod.backend.repository.RecordRepository;
import com.study.automatic.rod.backend.service.MaterialService;
import com.study.automatic.rod.backend.service.RecordService;
import com.study.automatic.rod.backend.service.SampleService;
import com.study.automatic.rod.backend.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Route(value = "workstation", layout = MainLayout.class)
//@CssImport("./styles/workstation-styles.css")
@PageTitle("Workstation | Automatic")
public class WorkstationView extends VerticalLayout {
    private MaterialService materialService;
    private SampleService sampleService;
    private RecordService recordService;

    private ChoosenMaterialForm firstMaterial;
    private Button startButton;
    private Button stopButton;
    private Sample sample;
    private boolean simulationRun = false;

    Runnable probkowanie;
    ScheduledExecutorService executor;

    public WorkstationView(MaterialService materialService, SampleService sampleService, RecordService recordService) {
        this.materialService = materialService;
        this.sampleService = sampleService;
        this.recordService = recordService;

        addClassName("workstation-view");
        setSizeFull();
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
        add(image);
        add(createMaterialLayout(), createSimulationButtonLayout());
        //add(getRecordsChart());

        //---------------------- RUNABLE
        probkowanie = new Runnable(){
            public void run() {
                addNewRecord();
            }
        };
    }

    private void addNewRecord(){
        if(sample != null && simulationRun){
            recordService.save(new Record(sample, 10));
            System.out.println("Add 10.0 to records");
            //refreshPlot();
        }//if
    }

    private void refreshPlot() {
    }

    private HorizontalLayout createSimulationButtonLayout() {
        HorizontalLayout hl = new HorizontalLayout();

        startButton = new Button("Start simulation");
        stopButton = new Button("Stop simulation");

        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        stopButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        hl.add(startButton, stopButton);

        setStartEnable();
        startButton.setEnabled(false);
        startButton.getElement().setProperty("title", "choose material first");


        firstMaterial.getMaterialBox().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                startButton.setEnabled(false);
                startButton.getElement().setProperty("title", "choose material first");
            } else {
                startButton.setEnabled(true);
                startButton.getElement().setProperty("title", "");
            }
        });

        startButton.addClickListener(event -> startSimulation());
        stopButton.addClickListener(event -> stopSimulation());

        return hl;
    }

    private void setStartEnable(){
        startButton.setVisible(true);
        startButton.setEnabled(true);
        stopButton.setVisible(false);
        stopButton.setEnabled(false);
    }
    private void setStopEnable(){
        startButton.setVisible(false);
        startButton.setEnabled(false);
        stopButton.setVisible(true);
        stopButton.setEnabled(true);
    }

    private void startSimulation() {
        sample = new Sample();
        simulationRun = true;
        sampleService.save(sample);
        setStopEnable();

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(probkowanie, 0, 1, TimeUnit.SECONDS);
    }

    private void stopSimulation() {
        simulationRun = false;
        setStartEnable();

        executor.shutdown();
    }

    private HorizontalLayout createMaterialLayout() {
        firstMaterial = new ChoosenMaterialForm(materialService);
        return new HorizontalLayout(firstMaterial);
    }

    private Chart getRecordsChart(){
        Chart chart = new Chart(ChartType.PIE);

        DataSeries dataSeries = new DataSeries();

        Map<String, Integer> records = new HashMap<>();
        records.put("Darek", 10);
        records.put("Stefan", 20);
        records.forEach((imie, ilosc) ->
                dataSeries.add(new DataSeriesItem(imie, ilosc))
        );
        chart.getConfiguration().setSeries(dataSeries);
        return chart;
    }
}
