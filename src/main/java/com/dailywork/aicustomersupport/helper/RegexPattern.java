package com.dailywork.aicustomersupport.helper;

import java.util.regex.Pattern;

public class RegexPattern {
    public static final Pattern EMAIl_PATTERN=Pattern.compile(
            "([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,})",Pattern.CASE_INSENSITIVE
    );
    public static final Pattern PHONE_PATTERN=Pattern.compile(
            "(\\+\\d{1,3}[\\s-]?\\d{6,12})",Pattern.CASE_INSENSITIVE
    );
    public static final Pattern ORDER_PATTERN= Pattern.compile(
            "(\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,3}?\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9})",Pattern.CASE_INSENSITIVE
    );
}
