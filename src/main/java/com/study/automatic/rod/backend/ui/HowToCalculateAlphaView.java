package com.study.automatic.rod.backend.ui;

import com.study.automatic.rod.backend.entity.Material;
import com.study.automatic.rod.backend.pdf.EmbeddedPdfDocument;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.*;
//
@Route(value = "how_to_calculate_alpha", layout = MainLayout.class)
//@CssImport("./styles/workstation-styles.css")
@PageTitle("Project info  | Automatic")
public class HowToCalculateAlphaView extends Div {
//tez
    public HowToCalculateAlphaView() {
        setHeight("100%");
        add(new EmbeddedPdfDocument(new StreamResource("zad1.pdf", () -> {
            try {
                return getPdfInputStream();
            } catch (FileNotFoundException e) {
                return new ByteArrayInputStream(new byte[]{});
            }
        })));

    }

    private InputStream getPdfInputStream() throws FileNotFoundException {
        File file = new File("kartaprojektu.pdf");   //windows
        //File file = new File("src\\main\\webapp\\pdf\\zad1.pdf");   //windows
        //File file = new File("src/main/webapp/pdf/zad1.pdf");   //linux
        System.out.println("Expecting to find file from " + file.getAbsolutePath());
        return new FileInputStream(file);
    }
    /*public HowToCalculateAlphaView() {
        //add(new EmbeddedPdfDocument("pdf/zad1.pdf"));
        setHeight("100%");

        runJavaScript();
    }//HowToCalculateAlphaView()

    public static void runJavaScript() {
        Page page = UI.getCurrent().getPage();
        page.executeJs("alert(\"Choose wisely your prey\");");
    }*/
}