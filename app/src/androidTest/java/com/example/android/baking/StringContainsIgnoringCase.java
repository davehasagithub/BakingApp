package com.example.android.baking;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.core.SubstringMatcher;

class StringContainsIgnoringCase extends SubstringMatcher {
    private StringContainsIgnoringCase(String substring) {
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
    static Matcher<String> containsStringIgnoringCase(@SuppressWarnings("SameParameterValue") String substring) {
        return new StringContainsIgnoringCase(substring);
    }
}
