package com.goldenkoi.progresstracker;

import junit.framework.TestCase;

public class FormatsOperationsTest extends TestCase {
    FormatsOperations formatsOperations = new FormatsOperations();

    public void testCheckCharacteristic() {
        assertFalse(formatsOperations.checkCharacteristic("ÒÜ"));
        assertFalse(formatsOperations.checkCharacteristic("sql инъекция\";'"));
        // больше размера
        assertFalse(formatsOperations.checkCharacteristic("00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"));
        // на повторение
        assertTrue(formatsOperations.checkCharacteristic("хорошая строчка"));
    }

    public void testRemake_str() {
        // проверка на удаление пробелов, табуляций, знаков препинания и приведения в нижний регистр
        assertEquals("тест0ваяline10", formatsOperations.remake_str("ТеСт0вая,   lIne 10."));
        // проверка на невхождение непринятных символов unicode
        assertEquals("", formatsOperations.remake_str("ÒÜ"));
    }
}