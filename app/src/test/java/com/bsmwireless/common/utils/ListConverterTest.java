package com.bsmwireless.common.utils;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.bsmuniversal.com.RxSchedulerRule;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ListConverter
 */
@RunWith(MockitoJUnitRunner.class)
public class ListConverterTest {

    @ClassRule
    public static final RxSchedulerRule RULE = new RxSchedulerRule();

    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testToString() {
        // given
        List<Integer> noIntegers = new ArrayList<>();
        String expectedNone = "";

        Integer[] arrSingleInt = {10};
        List<Integer> singleInt = Arrays.asList(arrSingleInt);
        String expectedSingle = "10";

        Integer[] arrMultipleInt = {10, 20, 314, 15, 9265, 358, 9, 7};
        List<Integer> multiInt = Arrays.asList(arrMultipleInt);
        String expectedMulti = "10,20,314,15,9265,358,9,7";

        // when
        String resultNone = ListConverter.integerListToString(noIntegers);
        String resultSingle = ListConverter.integerListToString(singleInt);
        String resultMulti = ListConverter.integerListToString(multiInt);

        // then
        assertEquals(expectedNone, resultNone);
        assertEquals(expectedSingle, resultSingle);
        assertEquals(expectedMulti, resultMulti);
    }

    @Test
    public void testToIntegerListValid() {
        // given
        String strNone = "";
        List<Integer> expectedNone = Arrays.asList();

        String strSingle = "1235321";
        List<Integer> expectedSingle = Arrays.asList(1235321);

        String strMulti = "10,20,50,900,2,4";
        List<Integer> expectedMulti = Arrays.asList(10, 20, 50, 900, 2, 4);

        String strErrExtraComma = "10,";
        List<Integer> expectedErrComma = Arrays.asList(10);

        String strNumberFormatException = "10,20,abcq,30,40,50";
        List<Integer> expectedErrNumberFormat = Arrays.asList(10, 20);

        // when
        List<Integer> resultNone = ListConverter.toIntegerList(strNone);
        List<Integer> resultSingle = ListConverter.toIntegerList(strSingle);
        List<Integer> resultMulti = ListConverter.toIntegerList(strMulti);

        List<Integer> resultErrExtraComma = ListConverter.toIntegerList(strErrExtraComma);
        List<Integer> resultErrNumberFormat = ListConverter.toIntegerList(strNumberFormatException);

        // then
        assertEquals(expectedNone, resultNone);
        assertEquals(expectedSingle, resultSingle);
        assertEquals(expectedMulti, resultMulti);

        assertEquals(expectedErrComma, resultErrExtraComma);
        assertEquals(expectedErrNumberFormat, resultErrNumberFormat);
    }

}
