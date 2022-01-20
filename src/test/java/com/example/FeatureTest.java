package com.example;

import junit.framework.TestCase;

@SuppressWarnings("ALL")
public class FeatureTest extends TestCase {

    public void testExecute() {
        while (true) {
            Feature f = new Feature();
            f.execute();
        }
    }
}