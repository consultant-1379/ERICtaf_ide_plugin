package com.ericsson.cifwk.taf.ide.eclipse.tm.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.wst.validation.ValidationResult;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.mock.TestIdCheckerAllFalseMock;
import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.mock.TestIdCheckerAllTrueMock;
import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.swtbot.UnresolvedJavaTestBase;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.TestIdChecker;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.VisitAbortedException;
import com.ericsson.cifwk.taf.ide.eclipse.tm.validator.TestIdValidator;

public class TestIdValidatorWithUnresolvedImportsTest extends UnresolvedJavaTestBase {
    private TestIdValidator validator = new TestIdValidator();

    @Before
    public void prepareTestIdCheckerMock() {
        TestIdChecker.setInstance(new TestIdCheckerAllFalseMock());
    }
    
    @Test
    public void testMinimalTestsWithImportUnresolvedFalse() throws Exception {
        ValidationResult result = validator.validateResource(minimalTestsWithImportUnresolved.getTypeRoot().getResource());
        assertEquals(0, result.getMessages().length);
        
    }

    @Test
    public void testMinimalTestsWithImportWithAbortUnresolvedFalse() throws Exception {
        
        ValidationResult result = validator.validateResource(minimalTestsWithImportWithAbortUnresolved.getTypeRoot().getResource());
        assertEquals(0, result.getMessages().length);
        
    }

    @Test
    public void testMinimalTestsWithoutImportUnresolvedFalse() throws Exception {
        ValidationResult result = validator.validateResource(minimalTestsWithoutImportUnresolved.getTypeRoot().getResource());
        assertEquals(1, result.getMessages().length);
        assertEquals(3, result.getMessages()[0].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[0].getAttributes().get("severity"));
        assertEquals(99, result.getMessages()[0].getAttributes().get("charEnd"));
        assertEquals(91, result.getMessages()[0].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[0].getAttributes()
                .get("sourceId"));
    }
    
    
    @Test
    public void testMinimalTestsWithImportUnresolvedTrue() throws Exception {
        TestIdChecker.setInstance(new TestIdCheckerAllTrueMock());

        ValidationResult result = validator.validateResource(minimalTestsWithImportUnresolved.getTypeRoot().getResource());
        assertEquals(0, result.getMessages().length);        
    }

    @Test()
    public void testMinimalTestsWithImportWithAbortUnresolvedTrue() throws Exception {
        TestIdChecker.setInstance(new TestIdCheckerAllTrueMock());
        
        ValidationResult result = validator.validateResource(minimalTestsWithImportWithAbortUnresolved.getTypeRoot().getResource());
        assertEquals(0, result.getMessages().length);
    }

    @Test
    public void testMinimalTestsWithoutImportUnresolvedTrue() throws Exception {
        TestIdChecker.setInstance(new TestIdCheckerAllTrueMock());        
        ValidationResult result = validator.validateResource(minimalTestsWithoutImportUnresolved.getTypeRoot().getResource());
        assertEquals(0, result.getMessages().length);
    }
    
    
}
