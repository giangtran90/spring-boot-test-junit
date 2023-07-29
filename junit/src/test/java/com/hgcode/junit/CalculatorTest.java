package com.hgcode.junit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    Calculator calculator;

    @Test
    void multiply() {
        calculator = new Calculator();
        assertEquals(20,calculator.multiply(4,5));
//        assertEquals(25,calculator.multiply(5,5));
    }

    @Test
    void divide() {
        calculator = new Calculator();
        assertEquals(4,calculator.divide(20,5));
    }
}