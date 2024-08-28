package com.ericsson.cifwk.taf.ide.eclipse.tm.tests.mock;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.TestIdChecker;

public class TestIdCheckerAllFalseMock extends TestIdChecker {

    @Override
    public List<Boolean> checkAll(List<String> testIds) {
        ArrayList<Boolean> result = new ArrayList<Boolean>();
        for(String id : testIds) {
            result.add(false);
        }
        return result;
    }
    
}
