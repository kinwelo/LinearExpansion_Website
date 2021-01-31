package com.study.automatic.rod.backend.calculation;

/***
 * Class deliver all necessary calculations which may be needed for charts. There are variables describing: material A and B; space given to maintain between A and B; whole system length; temperature. Most of subjects have current, previous and delta values.
 * After setting values use recalculateAll to calculate changes;
 */
public class CalculationKrzychu {
    /*** current length of material A */
    private double x;
    /*** previous length of material A */
    private double xPrev;
    /*** difference between current and previous length of material A */
    private double xDelta;
    /*** current length of material B */
    private double y;
    /*** previous length of material B */
    private double yPrev;
    /*** difference between current and previous length of material B */
    private double yDelta;

    /*** current length of space given to maintain between materials A and B */
    private double z;
    /*** previous length of space given to maintain between materials A and B */
    private double zPrev;
    /*** difference between current and previous length of space to maintain between materials A and B */
    private double zDelta;
    /*** current length of whole system (material A and B with given space to maintain between them) */
    private double total;
    /*** prevoius length of whole system (material A and B with given space to maintain between them) */
    private double totalPrev;
    /*** difference between current and previous length of whole system (material A and B with given space to maintain between them) */
    private double totalDelta;

    /*** current linear expansion coefficient for material A */
    private double xAlpha;
    /*** prevoius linear expansion coefficient for material A */
    private double xAlphaPrev;
    /*** difference between current and previous linear expansion coefficient for material A */
    private double xAlphaDelta;
    /*** current linear expansion coefficient for material B */
    private double yAlpha;
    /*** previous linear expansion coefficient for material B */
    private double yAlphaPrev;
    /*** difference between current and previous linear expansion coefficient for material B */
    private double yAlphaDelta;

    /*** current temperature of all materials */
    private double temp;
    /*** previous temperature of all materials */
    private double tempPrev;
    /*** difference between current and previous temperature of all materials */
    private double tempDelta;

    /***
     * Constructor for all calculations
     * @param x length of material A
     * @param y length of material B
     * @param z length of given space to mantain
     * @param xAlpha linear expansion coefficient for material A
     * @param yAlpha linear expansion coefficient for material B
     * @param temp starting temperature
     */
    public CalculationKrzychu(double x, double y, double z, double xAlpha, double yAlpha, double temp) {
        setBeginValues(x, y,z, xAlpha, yAlpha, temp);
    }

    /***
     * Set begin values for all variables. Should be used at the beggining of each simulation.
     * @param x length of material A
     * @param y length of material B
     * @param z length of given space to mantain
     * @param xAlpha linear expansion coefficient for material A
     * @param yAlpha linear expansion coefficient for material B
     * @param temp starting temperature
     */
    public void setBeginValues(double x, double y, double z, double xAlpha, double yAlpha, double temp){
        this.x = this.xPrev = x;
        this.y = this.yPrev = y;
        this.z = this.zPrev = z;
        this.xAlpha = this.yAlphaPrev = xAlpha;
        this.yAlpha = this.yAlphaPrev = yAlpha;
        this.temp = temp; this.tempPrev = temp;

        this.total = this.totalPrev = x+y+z;
        this.xDelta = this.yDelta = this.zDelta = this.xAlphaDelta = this.yAlphaDelta = this.tempDelta = this.totalDelta = 0;
    }//setBeginValues()

    /***
     * Recalculate all variables. Used after any change in variables.
     */
    public void recalculateAll(){
        recalculateTemp();
        recalculateXAlpha();
        recalculateYAlpha();
        recalculateX();
        recalculateY();
        recalculateZ();
        recalculateTotal();
        passOutOfDateToPrevious();
    }

    /***
     * Pass values out of date from current to previous variables (ex.: from x to xPrev).
     */
    private void passOutOfDateToPrevious(){
        this.xPrev = this.x;
        this.yPrev = this.y;
        this.zPrev = this.z;
        this.xAlphaPrev = this.xAlpha;
        this.yAlphaPrev = this.yAlpha;
        this.tempPrev = this.temp;
        this.totalPrev = this.total;
    }//passOutOfDateToPrevious()

    /***
     * Recalculate variables describing temperature
     */
    private void recalculateTemp(){
        this.tempDelta = getTemp() - getTempPrev();
    }

    /***
     * Recalculate variables describing xAlpha
     */
    private void recalculateXAlpha(){
        this.xAlphaDelta = getxAlpha() - getxAlphaPrev();
    }

    /***
     * Recalculate variables describing yAlpha
     */
    private void recalculateYAlpha(){
        this.yAlphaDelta = getyAlpha() - getyAlphaPrev();
    }

    /***
     * Recalculate variables describing x length
     */
    private void recalculateX(){
        //this.xPrev = this.x;
        this.x = getX() * (1 + getxAlpha() * getTempDelta());
        this.xDelta = this.x - this.xPrev;
    }

    /***
     * Recalculate variables describing y length
     */
    private void recalculateY(){
        //this.yPrev = this.y;
        this.y = getY() * (1 + getyAlpha() * getTempDelta());
        this.yDelta = this.y - this.yPrev;
    }

    /***
     * Recalculate variables describing z length (space between materials to maintain)
     */
    private void recalculateZ(){
        this.zDelta = getZ() - getzPrev();
    }

    /***
     * Recalculate variables describing total length
     */
    private void recalculateTotal(){
        this.total = getX() + getY() + getZ();
        this.totalDelta = getTotal() - getTotalPrev();
    }

    //----SET VALUES SECTION------------------------------------------------------------------------------------------------

    /***
     * Set new length for material A.
     * @param x new length
     */
    public void setX(double x) {
        this.x = x;
    }

    /***
     * Set new length form aterial B.
     * @param y new length
     */
    public void setY(double y) {
        this.y = y;
    }

    /***
     * Set new length given to maintain between materials A and B.
     * @param z new length
     */
    public void setZ(double z) {
        this.z = z;
    }

    /***
     * set new linear expansion coefficient for material A.
     * @param xAlpha new linear expansion coefficient xAlpha
     */
    public void setxAlpha(double xAlpha) {
        this.xAlpha = xAlpha;
    }

    /***
     * set new linear expansion coefficient for material B.
     * @param yAlpha new linear expansion coefficient yAlpha
     */
    public void setyAlpha(double yAlpha) {
        this.yAlpha = yAlpha;
    }

    /***
     * Set new temperature for all materials, set prevoius value.
     * @param temp new temperature
     */
    public void setTemp(double temp) {
        this.temp = temp;
    }

    //----GET VALUES SECTION------------------------------------------------------------------------------------------------

    /***
     * Return current length of material A
     * @return value of x length
     */
    public double getX() {
        return x;
    }

    /***
     * Return previous length of material A
     * @return value of xPrev length
     */
    public double getxPrev() {
        return xPrev;
    }

    /***
     * Return difference between current and previous length of material A
     * @return value of z length
     */
    public double getxDelta() {
        return xDelta;
    }

    /***
     * Return current length of material B
     * @return value of y length
     */
    public double getY() {
        return y;
    }

    /***
     * Return previous length of material B
     * @return value of yPrev length
     */
    public double getyPrev() {
        return yPrev;
    }

    /***
     * Return difference between current and previous length of material B
     * @return value of yDelta length
     */
    public double getyDelta() {
        return yDelta;
    }

    /***
     * Return current length of space given to maintain between materials A and B
     * @return value of z length
     */
    public double getZ() {
        return z;
    }

    /***
     * Return previous length of space given to maintain between materials A and B
     * @return value of zPrev length
     */
    public double getzPrev() {
        return zPrev;
    }

    /***
     * Return difference between current and previous length of space given to maintain between materials A and B
     * @return value of zDelta length
     */
    public double getzDelta() {
        return zDelta;
    }

    /***
     * Return current length of whole system (length = x+y+z)
     * @return value of total length
     */
    public double getTotal() {
        return total;
    }

    /***
     * Return previous length of whole system (length = x+y+z)
     * @return value of totalPrev length
     */
    public double getTotalPrev() {
        return totalPrev;
    }

    /***
     * Return difference between current and previous length of whole system (length = x+y+z)
     * @return value of totalDelta length
     */
    public double getTotalDelta() {
        return totalDelta;
    }

    /***
     * Return current value of linear expansion coefficient for material A
     * @return value of xAlpha
     */
    public double getxAlpha() {
        return xAlpha;
    }

    /***
     * Return previous value of linear expansion coefficient for material A
     * @return value of xAlphaPrev
     */
    public double getxAlphaPrev() {
        return xAlphaPrev;
    }

    /***
     * Return difference between current and previous value of linear expansion coefficient for material A
     * @return value of xAlphaDelta
     */
    public double getxAlphaDelta() {
        return xAlphaDelta;
    }

    /***
     * Return current value of linear expansion coefficient for material B
     * @return value of yAlpha
     */
    public double getyAlpha() {
        return yAlpha;
    }

    /***
     * Return previous value of linear expansion coefficient for material B
     * @return value of yAlphaPrev
     */
    public double getyAlphaPrev() {
        return yAlphaPrev;
    }

    /***
     * Return difference between current and previous value of linear expansion coefficient for material B
     * @return value of yAlphaDelta
     */
    public double getyAlphaDelta() {
        return yAlphaDelta;
    }

    /***
     * Return current value of temperature
     * @return value of temp
     */
    public double getTemp() {
        return temp;
    }

    /***
     * Return previous value of temperature
     * @return value of tempPrev
     */
    public double getTempPrev() {
        return tempPrev;
    }

    /***
     * Return difference between current and previous value of temperature
     * @return value of tempDelta
     */
    public double getTempDelta() {
        return tempDelta;
    }
}//CalculationKrzychu
