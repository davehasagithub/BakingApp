package com.example.android.baking;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.SubstringMatcher;

class StringContainsIgnoringCase extends SubstringMatcher {
    public StringContainsIgnoringCase(String substring) {
        super(substring);
    }

    @Override
    protected boolean evalSubstringOf(String s) {
        return s.toUpperCase().contains(substring.toUpperCase());
    }

    @Override
    protected String relationship() {
        return "containing";
    }

    @Factory
    public static Matcher<String> containsStringIgnoringCase(String substring) {
        return new StringContainsIgnoringCase(substring);
    }
}
