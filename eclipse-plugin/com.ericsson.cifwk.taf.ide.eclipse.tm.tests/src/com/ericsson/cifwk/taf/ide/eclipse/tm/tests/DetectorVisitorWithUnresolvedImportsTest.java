package com.ericsson.cifwk.taf.ide.eclipse.tm.tests;

import static org.junit.Assert.*;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.mock.TestIdCheckerAllFalseMock;
import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.swtbot.UnresolvedJavaTestBase;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.DetectorVisitor;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.TestIdChecker;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.VisitAbortedException;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.VisitCompleteException;

@RunWith(SWTBotJunit4ClassRunner.class)
public class DetectorVisitorWithUnresolvedImportsTest extends UnresolvedJavaTestBase {
    
    @Before
    public void prepareTestIdCheckerMock() {
        TestIdChecker.setInstance(new TestIdCheckerAllFalseMock());
    }
    
    @Test
    public void testVisitorWithImport() throws Exception {
        // cannot resolve @TestId even with location matching range
        // this is because bindings cannot resolve missing imports.
        minimalTestsWithImportUnresolved.accept(new DetectorVisitor(100, ResourcesPlugin.getEncoding()));
        // this should not throw anything, just silently exit
    }

    @Test(expected = VisitAbortedException.class)
    public void testVisitorWithImportWithAborted() throws Exception {
        minimalTestsWithImportWithAbortUnresolved.accept(new DetectorVisitor(140, ResourcesPlugin.getEncoding()));
    }

    @Test
    public void testVisitorWithoutImport() throws Exception {
        try {
            minimalTestsWithoutImportUnresolved.accept(new DetectorVisitor(100, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=123", vce.getURL());
            assertEquals(48, vce.getRegion().getOffset());
            assertEquals(77, vce.getRegion().getLength());            
        }
    }

}
