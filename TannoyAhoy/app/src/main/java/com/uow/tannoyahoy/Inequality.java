package com.uow.tannoyahoy;

import android.util.Log;

import java.math.BigDecimal;

/**
 * Created by Pwnbot on 30/05/2015.
 */
public class Inequality {
    public BigDecimal deltaX;
    public BigDecimal deltaY;
    public BigDecimal constant;
    public Boolean isAboveLine;

    public Inequality(BigDecimal xCoeff, BigDecimal yCoeff, BigDecimal k) {
        deltaX = xCoeff;
        deltaY = yCoeff;
        constant = k;
    }

    /**
     * Determines whether the 'good' side i.e the side of the line for which values will pass the testInequality(), is above or below the line
     * @param x The x-coord of the place this inequality refers to
     * @param y The y-coord of the place this inequality refers to
     */
    public void setIsAboveLine(BigDecimal x, BigDecimal y) {
        //deltaY * y + deltaX * x - k = 0
        BigDecimal xProduct = deltaX.multiply(x);
        BigDecimal yProduct = deltaY.multiply(y);

        //substitute into formula
        BigDecimal result = yProduct.add(xProduct).subtract(constant);
        // if the number is greater than 0, it is above the line
        isAboveLine = (result.compareTo(new BigDecimal(0)) > 0);
    }

    /**
     *
     * @param x The x-coord of the user's location
     * @param y The y-coord of the user's location
     * @return returns true if the user's location is on the same side of the line as the 'good side', other it returns false.
     */
    public Boolean testInequality(BigDecimal x, BigDecimal y) {
        //deltaY * y + deltaX * x - k = 0
        BigDecimal xProduct = deltaX.multiply(x);
        BigDecimal yProduct = deltaY.multiply(y);
        //substitute into formula
        BigDecimal result = yProduct.add(xProduct).subtract(constant);
        //test whether the point is on the same side as the boolean requires
        return (isAboveLine == (result.compareTo(new BigDecimal(0)) > 0)); // if on the correct side
    }

}
