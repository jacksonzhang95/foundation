package com.foundation.theory.chainofresponsibility;

import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2021/10/19 9:49
 */
public class ChainOfResponsibilityDemo {
}

abstract class BaseFilter {
    private BaseFilter nextFilter;
    public void setNextFilter(BaseFilter nextFilter) {
        this.nextFilter = nextFilter;
    }
    public void filter() {
        doFilter();
        if (Objects.nonNull(nextFilter)) {
            nextFilter.filter();
        }
    }
    protected abstract boolean doFilter();
}
class FilterChain {
    private BaseFilter head = null;
    private BaseFilter tail = null;

    public void addFilter(BaseFilter filter) {
        filter.setNextFilter(null);
        if (head == null) {
            head = filter;
            tail = filter;
            return;
        }
        tail.setNextFilter(filter);
        tail = filter;
    }

    public void filter() {
        if (head != null) {
            head.filter();
        }
    }
}