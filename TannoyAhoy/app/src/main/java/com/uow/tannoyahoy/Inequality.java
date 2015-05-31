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

    public void setIsAboveLine(BigDecimal x, BigDecimal y) {
        BigDecimal xProduct = deltaX.multiply(x);
        BigDecimal yProduct = deltaY.multiply(y);

        BigDecimal result = yProduct.add(xProduct).subtract(constant);
        isAboveLine = (result.compareTo(new BigDecimal(0)) > 0); // if the number is greater than 0, it is above the line
    }

    public Boolean testInequality(BigDecimal x, BigDecimal y) {
        BigDecimal xProduct = deltaX.multiply(x);
        BigDecimal yProduct = deltaY.multiply(y);
        BigDecimal result = yProduct.add(xProduct).subtract(constant);
        return (isAboveLine == (result.compareTo(new BigDecimal(0)) > 0)); // if on the correct side
    }

}
