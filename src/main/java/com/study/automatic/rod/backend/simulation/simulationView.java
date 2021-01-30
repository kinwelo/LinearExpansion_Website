package com.study.automatic.rod.backend.simulation;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.legend.HorizontalAlign;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.subtitle.Align;
import com.github.appreciated.apexcharts.helper.Series;
import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.service.MaterialService;
import com.study.automatic.rod.backend.ui.MainLayout;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Route(value = "Simulator", layout = MainLayout.class)
//@CssImport("./styles/workstation-styles.css")
@PageTitle("Simulation | Automatic")
//@NpmPackage(value = "jquery", version = "3.4.1")


//

public class simulationView extends VerticalLayout {

    private class SimulationEnvironment{
        private float temperature;
        private float minTemperature;
        private float maxTemperature;
        private float delta;

        public float getTemperature() {
            return temperature;
        }

        public void setTemperature(float temperature) {
            this.temperature = temperature;
        }

        public float getMinTemperature() {
            return minTemperature;
        }

        public void setMinTemperature(float minTemperature) {
            this.minTemperature = minTemperature;
        }

        public float getMaxTemperature() {
            return maxTemperature;
        }

        public void setMaxTemperature(float maxTemperature) {
            this.maxTemperature = maxTemperature;
        }

        public SimulationEnvironment(float temperature, float minTemperature, float maxTemperature, float delta) {
            this.temperature = temperature;
            this.minTemperature = minTemperature;
            this.maxTemperature = maxTemperature;
            this.setDelta(delta);
        }

        public float getDelta() {
            return delta;
        }

        public void setDelta(float delta) {
            this.delta = delta;
        }
    }

    private void simulation(SimulationEnvironment environment, Material material, int time){
        double length = 0.0;
        double deltaT = 0.0;
        Double[] submit = new Double[100];
        int pm = 1;
        for (int i = 0; i < time; i++){
            if (environment.getMinTemperature() >= environment.getTemperature() + deltaT)
                pm = 1;
            else if (environment.getMaxTemperature() <= environment.getTemperature() + deltaT)
                pm = -1;
            deltaT += pm * environment.getDelta();
            length = material.getLength_0()*((material.getAlpha() * deltaT) + 1);
            submit[i] = length;
        }
        System.out.println(Arrays.toString(submit));
    }
    public simulationView(MaterialService materialService){
        Material stal = new Material("stal", 0.000013, 20, 20);
        SimulationEnvironment domKrzysia = new SimulationEnvironment(20, -25.6f,33, 0.5f);
        //simulation(domKrzysia, stal, 100);
        NumberField minTemp = new NumberField("Temperatura minimalna: ");
        NumberField maxTemp = new NumberField("Temperatura maksymalna: ");
        minTemp.setHasControls(true);
        maxTemp.setHasControls(true);
        minTemp.setStep(5);
        minTemp.setValue(0d);
        maxTemp.setStep(5);
        maxTemp.setValue(50d);
        String content = "<div id='wykres'>placeholder</div>"; // wrapping <div> tags are required here
        Html html = new Html(content);
        minTemp.setWidth("200px");
        maxTemp.setWidth("200px");


        ComboBox<Material> materialsBoxX = new ComboBox<>();
        List<Material> materials = materialService.findAll();

        materials.sort(Comparator.comparing(Material::getName, String::compareToIgnoreCase));
        materialsBoxX.setItems(materials);
        materialsBoxX.setItemLabelGenerator(Material::getName);
        materialsBoxX.setPlaceholder("Wybierz materiał...");
        materialsBoxX.setLabel("Materiał X: ");

        ComboBox<Material> materialsBoxY = new ComboBox<>();
        materialsBoxY.setItems(materials);
        materialsBoxY.setItemLabelGenerator(Material::getName);
        materialsBoxY.setPlaceholder("Wybierz materiał...");
        materialsBoxY.setLabel("Materiał Y: ");

        AtomicBoolean isChart = new AtomicBoolean(false);
        Button click= new Button("Symulacja", buttonClickEvent -> {

            Double minTempValue = minTemp.getValue();
            Double maxTempValue = maxTemp.getValue();
            double alphaA = materialsBoxY.getValue().getAlpha();
            double alphaB = materialsBoxY.getValue().getAlpha();

            double Z=10;  //wybrane przez użytkownika Z

            int[] temperatureArray = new int[15];
            for (int i =0; i < temperatureArray.length; i++){
                int rand = (int)(Math.random() * (maxTempValue-minTempValue+1));
                temperatureArray[i] = (int) (rand - rand%5 + minTempValue);
            }

            //kolejne temperatury randomowe

            double[] tableLengthX =new double[temperatureArray.length]; //do kolejnego wykresiku: jak sie zmienia długość L stali

            tableLengthX[0]=200;
            double[] tableLengthY = new double[temperatureArray.length]; //kolejny wykres jak sie zmiana długości L miedzi
            tableLengthY[0]=200;
            double[] tablicaPsi = new double[temperatureArray.length]; //Jak w każdym kroku działa suwnica: o ile przybliza przesuwa
            tablicaPsi[0]=0;
            int sampling= temperatureArray.length;


            Double[] tablicaOdl=new Double[sampling]; //najważniejsza do przechowywania jak sie zmienia calkowita odleglosc

            for(int i = 0; i < temperatureArray.length; i++){

                if(i == 0){
                    tablicaOdl[i]=tableLengthY[i]+ tableLengthX[i]+Z;

                }else{
                    double T_i=temperatureArray[i]-temperatureArray[i-1];
                    double stalL_i= tableLengthX[i-1]*(1+alphaA*(T_i));

                    double miedzL_i=tableLengthY[i-1]*(1+alphaB*(T_i));
                    double psi_i=stalL_i+miedzL_i-tableLengthY[i-1]- tableLengthX[i-1];
                    tableLengthX[i]=stalL_i;
                    tableLengthY[i]=miedzL_i;
                    tablicaPsi[i]=psi_i;
                    tablicaOdl[i]=tablicaOdl[i-1]+psi_i;
                }

                //System.out.println(tablicaOdl[i]);
            }
            System.out.println("Pamiec kolejnych calkowitych odleglosci do wykresu:");
            String[] categories = new String[tablicaOdl.length];
            for(int j=0;j<tablicaOdl.length;j++){
                tablicaOdl[j] = Math.round(tablicaOdl[j] * 10000.0) / 10000.0;
                categories[j] = j + " / " + temperatureArray[j] + " °C";
            }
            System.out.println(Arrays.toString(categories));
            System.out.println(Arrays.toString(tablicaOdl));

            Series<Double> series = new Series<Double>();
            series.setData(tablicaOdl);
            series.setName("Długość");

            ApexCharts areaChart = ApexChartsBuilder.get()
                    .withChart(ChartBuilder.get()
                            .withType(Type.area)
                            .withHeight("500px")
                            .withZoom(ZoomBuilder.get()
                                    .withEnabled(false)
                                    .build())
                            .build())
                    .withDataLabels(DataLabelsBuilder.get()
                            .withEnabled(false)
                            .build())
                    .withStroke(StrokeBuilder.get().withCurve(Curve.straight).build())
                    .withSeries(series)
                    .withTitle(TitleSubtitleBuilder.get()
                            .withText("Odległości")
                            .withAlign(Align.left).build())
//                .withLabels(String.valueOf(IntStream.range(1, 10)))
                    .withXaxis(XAxisBuilder.get().withCategories(categories).build())
                    .withYaxis(YAxisBuilder.get()
                            .withOpposite(true).build())
                    .withLegend(LegendBuilder.get().withHorizontalAlign(HorizontalAlign.left).build())
                    .build();

            add(areaChart);







        });

        Tab t1=new Tab("Chart 1");
        Tab t2=new Tab("Chart 2");
        Tab t3=new Tab("Chart 3");
        Tabs tab=new Tabs(t1,t2);
        add(tab);
        setWidth("80%");
        add(materialsBoxX);
        add(materialsBoxY);
        add(minTemp);
        add(maxTemp);
        add(click);
//        add(html);
    }

}