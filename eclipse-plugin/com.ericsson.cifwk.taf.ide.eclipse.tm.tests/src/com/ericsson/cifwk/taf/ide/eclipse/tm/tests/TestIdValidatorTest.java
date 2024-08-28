package com.ericsson.cifwk.taf.ide.eclipse.tm.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.wst.validation.ValidationResult;
import org.junit.Before;
import org.junit.Test;

import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.mock.TestIdCheckerAllFalseMock;
import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.mock.TestIdCheckerAllTrueMock;
import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.swtbot.JavaTestBase;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.DetectorVisitor;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.TestIdChecker;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.VisitAbortedException;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.VisitCompleteException;
import com.ericsson.cifwk.taf.ide.eclipse.tm.validator.TestIdValidator;

public class TestIdValidatorTest extends JavaTestBase {

    @Before
    public void prepareTestIdCheckerMock() {
        TestIdChecker.setInstance(new TestIdCheckerAllFalseMock());
    }

    private TestIdValidator validator = new TestIdValidator();

    @Test
    public void testMinimalTestsWithImportFalse() throws Exception {
        ValidationResult result = validator.validateResource(minimalTestsWithImport.getTypeRoot().getResource());
        assertEquals(1, result.getMessages().length);
        assertEquals(4, result.getMessages()[0].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[0].getAttributes().get("severity"));
        assertEquals(112, result.getMessages()[0].getAttributes().get("charEnd"));
        assertEquals(104, result.getMessages()[0].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[0].getAttributes()
                .get("sourceId"));
    }

    @Test
    public void testMinimalTestsWithoutImportFalse() throws Exception {
        ValidationResult result = validator.validateResource(minimalTestsWithoutImport.getTypeRoot().getResource());
        assertEquals(1, result.getMessages().length);
        assertEquals(3, result.getMessages()[0].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[0].getAttributes().get("severity"));
        assertEquals(99, result.getMessages()[0].getAttributes().get("charEnd"));
        assertEquals(91, result.getMessages()[0].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[0].getAttributes()
                .get("sourceId"));
        
    }

    @Test
    public void testFullTestsWithImportFalse() throws Exception {
        ValidationResult result = validator.validateResource(fullTestsWithImport.getTypeRoot().getResource());
        assertEquals(3, result.getMessages().length);

        assertEquals(7, result.getMessages()[0].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[0].getAttributes().get("severity"));
        assertEquals(219, result.getMessages()[0].getAttributes().get("charEnd"));
        assertEquals(211, result.getMessages()[0].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[0].getAttributes()
                .get("sourceId"));
        assertEquals(13, result.getMessages()[1].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[1].getAttributes().get("severity"));
        assertEquals(347, result.getMessages()[1].getAttributes().get("charEnd"));
        assertEquals(339, result.getMessages()[1].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[1].getAttributes()
                .get("sourceId"));
        assertEquals(20, result.getMessages()[2].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[2].getAttributes().get("severity"));
        assertEquals(510, result.getMessages()[2].getAttributes().get("charEnd"));
        assertEquals(502, result.getMessages()[2].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[2].getAttributes()
                .get("sourceId"));
        
    }

    @Test
    public void testFullTestsWithoutImportFalse() throws Exception {
        ValidationResult result = validator.validateResource(fullTestsWithoutImport.getTypeRoot().getResource());
        assertEquals(3, result.getMessages().length);

        assertEquals(4, result.getMessages()[0].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[0].getAttributes().get("severity"));
        assertEquals(140, result.getMessages()[0].getAttributes().get("charEnd"));
        assertEquals(132, result.getMessages()[0].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[0].getAttributes()
                .get("sourceId"));

        assertEquals(10, result.getMessages()[1].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[1].getAttributes().get("severity"));
        assertEquals(373, result.getMessages()[1].getAttributes().get("charEnd"));
        assertEquals(365, result.getMessages()[1].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[1].getAttributes()
                .get("sourceId"));

        assertEquals(17, result.getMessages()[2].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[2].getAttributes().get("severity"));
        assertEquals(664, result.getMessages()[2].getAttributes().get("charEnd"));
        assertEquals(656, result.getMessages()[2].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[2].getAttributes()
                .get("sourceId"));
                
    }

    @Test
    public void testInvalidTestsFalse() throws Exception {
        ValidationResult result = validator.validateResource(invalidTests.getTypeRoot().getResource());
        assertEquals(3, result.getMessages().length);
        
        assertEquals(7, result.getMessages()[0].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[0].getAttributes().get("severity"));
        assertEquals(206, result.getMessages()[0].getAttributes().get("charEnd"));
        assertEquals(201, result.getMessages()[0].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[0].getAttributes()
                .get("sourceId"));
        
        assertEquals(13, result.getMessages()[1].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[1].getAttributes().get("severity"));
        assertEquals(337, result.getMessages()[1].getAttributes().get("charEnd"));
        assertEquals(307, result.getMessages()[1].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[1].getAttributes()
                .get("sourceId"));

        assertEquals(20, result.getMessages()[2].getAttributes().get("lineNumber"));
        assertEquals(2, result.getMessages()[2].getAttributes().get("severity"));
        assertEquals(500, result.getMessages()[2].getAttributes().get("charEnd"));
        assertEquals(492, result.getMessages()[2].getAttributes().get("charStart"));
        assertEquals("org.eclipse.core.resources.problemmarker", result.getMessages()[2].getAttributes()
                .get("sourceId"));        
    }
    
    @Test
    public void testInvalidTestsTrue() throws Exception {
        TestIdChecker.setInstance(new TestIdCheckerAllTrueMock());
        
        ValidationResult result = validator.validateResource(invalidTests.getTypeRoot().getResource());
        assertEquals(0, result.getMessages().length);        
    }
    
    @Test
    public void testFullTestsWithoutImportTrue() throws Exception {
        TestIdChecker.setInstance(new TestIdCheckerAllTrueMock());
        
        ValidationResult result = validator.validateResource(fullTestsWithoutImport.getTypeRoot().getResource());
        assertEquals(0, result.getMessages().length);                
    }
    
    
}
