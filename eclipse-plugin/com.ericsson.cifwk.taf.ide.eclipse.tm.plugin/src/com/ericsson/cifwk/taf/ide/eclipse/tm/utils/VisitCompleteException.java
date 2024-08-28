package com.ericsson.cifwk.taf.ide.eclipse.tm.utils;

import org.eclipse.jface.text.IRegion;

public class VisitCompleteException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String url;
    private IRegion region;
    public IRegion getRegion() {
        return region;
    }

    public String getURL() {
        return url;
    }

    public VisitCompleteException(String url, IRegion region) {
        this.url = url;
        this.region = region;
    }
}
