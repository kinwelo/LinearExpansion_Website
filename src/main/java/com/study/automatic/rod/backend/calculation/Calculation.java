package com.study.automatic.rod.backend.calculation;

public class Calculation {
    private double xPrev;//previous
    private double xActual;//actual
    private double xAlpha;//const
    private double yPrev;
    private double yActual;
    private double yAlpha;
    private double z;
    private double tPrev; //temperatura poprzedniego kroku
    private double licznik = 0;//licznik iteracji w zasadzie wazne tylko na poczatek
    private double tActual;//aktualna temperatura
    private double psi;//litera grecka patrz karta projektu
    private double Lprev;
    private double Lactual;

    public Calculation(double x, double y, double t, double z, double xAlpha, double yAlpha) {
        this.xPrev = x;//na poczatku x poprzednie to x podane
        this.yPrev = y;//na poczatku y poprzednie to y podane
        this.z = z;
        this.tPrev = t;
        this.xAlpha = xAlpha;
        this.yAlpha = yAlpha;
    }


    private void calculateChanges() {//finalny krok. wynik do glownego wykresu L w kroku wybrania tej temperatury
        setLprev(Lactual);
        if (licznik == 0) {
            Lactual = xPrev + yPrev + z;
            licznik++;//zwiekszmy licznik zeby kolejne iteracji juz szly zgodnie
            setLprev(Lactual);
            setxPrev(xPrev);
            setyPrev(yPrev);
            setZ(z);
            psi=0;

        } else {
            double delta = tActual - tPrev;//dla 1 obliczenia t0 = stałe 20 potem to wynik z poprzedniego kroku
            xActual = xPrev * (1 + xAlpha * delta);//calculate actual length of x using prev x
            yActual = yPrev * (1 + yAlpha * delta);//calculate actual length of x using previousy
            psi = xActual + yActual - xPrev - yPrev;

            setxPrev(xActual);//set previous as actual for next step.
            setyPrev(yActual);//set previous as actual for next step.
            settPrev(tActual);//set previous as actual for next step.
            Lactual = Lprev + psi;//w pierwszym kroku l0 ma byc rowne Lstart, potem to kolejne wyniki
        }
    }

    public void settActual(double tActual) {
        this.tActual = tActual;
        calculateChanges();
    }

    public double gettActual() {
        return tActual;
    }


    public double getxPrev() {
        return xPrev;
    }

    public double getxAlpha() {
        return xAlpha;
    }

    public double getyPrev() {
        return yPrev;
    }

    public double getyAlpha() {
        return yAlpha;
    }

    public double gettPrev() {
        return tPrev;
    }


    public double getLactual() {
        return Lactual;
    }

    public void setxPrev(double xPrev) {
        this.xPrev = xPrev;
    }

    public void setxActual(double xActual) {
        this.xActual = xActual;
    }

    public void setxAlpha(double xAlpha) {
        this.xAlpha = xAlpha;
    }

    public void setyPrev(double yPrev) {
        this.yPrev = yPrev;
    }


    public void setyActual(double yActual) {
        this.yActual = yActual;
    }

    public void setyAlpha(double yAlpha) {
        this.yAlpha = yAlpha;
    }

    public void settPrev(double tPrev) {
        this.tPrev = tPrev;


    }



    public double getPsi() {
        return psi;
    }

    public void setPsi(double psi) {
        this.psi = psi;
    }


    public double getLprev() {
        return Lprev;
    }



    public void setLprev(double lprev) {
        Lprev = lprev;
    }
    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
