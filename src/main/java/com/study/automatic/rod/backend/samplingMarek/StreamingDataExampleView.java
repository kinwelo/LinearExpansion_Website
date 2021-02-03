package com.study.automatic.rod.backend.samplingMarek;


import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.DataLabelsBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.builder.YAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.config.chart.animations.Easing;
import com.github.appreciated.apexcharts.config.chart.animations.builder.DynamicAnimationBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.AnimationsBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.ToolbarBuilder;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
//
//@Route("stream")
//@Push
public class StreamingDataExampleView extends VerticalLayout {   // extends ExampleHolderView
    boolean clearChartData = true;
    static private boolean isThreadRunning = false;
    private double nextValueDouble = 0;
//
    /*private double oYmin = 0;
    private double oYmax = 100;
    private double oYtick = 10;*/

    private final ApexCharts chart;
    private Thread thread;

    public StreamingDataExampleView() {
        setSizeFull();

        chart = ApexChartsBuilder.get().withChart(ChartBuilder.get()
                .withType(Type.line)
                .withAnimations(AnimationsBuilder.get()
                        .withEnabled(true)
                        .withEasing(Easing.linear)
                        .withDynamicAnimation(DynamicAnimationBuilder.get()
                                .withSpeed(1000)
                                .build())
                        .build())
                .withToolbar(ToolbarBuilder.get().withShow(false).build())
                .withZoom(ZoomBuilder.get().withEnabled(false).build())
                .build())
                .withDataLabels(DataLabelsBuilder.get()
                        .withEnabled(false)
                        .build())
                .withXaxis(XAxisBuilder.get().withRange(10.0).build())
                /*.withYaxis(YAxisBuilder.get()
                        .withMin(oYmin)
                        .withMax(oYmax)
                        .withTickAmount(oYtick)
                        .build())*/
                .withSeries(new Series<>(0))
                .build();

        chart.setDebug(true);
        add(chart);
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
                    //clear chart before next run
                    if(clearChartData){
                        arrayList.clear();
                        clearChartData = false;
                    }
                    arrayList.add(nextValueDouble);
                    getUI().ifPresent(ui -> ui.access(() -> chart.updateSeries(new Series<>(arrayList.toArray(new Double[]{})))));
                } else {
                    clearChartData = true;
                }
                //save actual status to previous
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

    public double getNextValueDouble() {
        return nextValueDouble;
    }

    public void setNextValueDouble(double nextValueDouble) {
        this.nextValueDouble = nextValueDouble;
    }

    public boolean isRunThread() {
        return isThreadRunning;
    }

    public void setRunThread(boolean runThread) {
        isThreadRunning = runThread;
    }

    public void clearChartData(){this.clearChartData = true;}
}