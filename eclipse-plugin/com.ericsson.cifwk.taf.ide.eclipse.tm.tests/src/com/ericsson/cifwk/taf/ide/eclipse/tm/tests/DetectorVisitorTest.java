package com.ericsson.cifwk.taf.ide.eclipse.tm.tests;

import static org.junit.Assert.*;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.mock.TestIdCheckerAllFalseMock;
import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.mock.TestIdCheckerAllTrueMock;
import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.swtbot.JavaTestBase;
import com.ericsson.cifwk.taf.ide.eclipse.tm.tests.swtbot.UnresolvedJavaTestBase;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.DetectorVisitor;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.TestIdChecker;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.VisitAbortedException;
import com.ericsson.cifwk.taf.ide.eclipse.tm.utils.VisitCompleteException;

@RunWith(SWTBotJunit4ClassRunner.class)
public class DetectorVisitorTest extends JavaTestBase {
    
    @Before
    public void prepareTestIdCheckerMock() {
        TestIdChecker.setInstance(new TestIdCheckerAllFalseMock());
    }
    
    @Test
    public void testVisitorWithImport() throws Exception {
        try {
            minimalTestsWithImport.accept(new DetectorVisitor(100, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=123", vce.getURL());
            assertEquals(96, vce.getRegion().getOffset());
            assertEquals(42, vce.getRegion().getLength());            
        }
    }
    
    @Test
    public void testFullVisitorWithImportTest1() throws Exception {
        try {
            fullTestsWithImport.accept(new DetectorVisitor(216, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=123&title=My+Title1&context=REST&group=GAT", vce.getURL());
            assertEquals(203, vce.getRegion().getOffset());
            assertEquals(98, vce.getRegion().getLength());            
        }
    }
    
    @Test
    public void testFullVisitorWithImportTest2() throws Exception {
        try {
            fullTestsWithImport.accept(new DetectorVisitor(333, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=234&title=My+Title2&context=REST&group=GAT1&group=GAT2", vce.getURL());
            assertEquals(331, vce.getRegion().getOffset());
            assertEquals(133, vce.getRegion().getLength());            
        }
    }
    
    @Test
    public void testFullVisitorWithImportTest3() throws Exception {
        try {
            fullTestsWithImport.accept(new DetectorVisitor(495, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=345&title=My+Title3+&context=REST&context=UI&group=GAT", vce.getURL());
            assertEquals(494, vce.getRegion().getOffset());
            assertEquals(113, vce.getRegion().getLength());            
        }
    }

    @Test
    public void testFullVisitorWithImportTest3TestIdExists() throws Exception {
        try {
            TestIdChecker.setInstance(new TestIdCheckerAllTrueMock());            
            fullTestsWithImport.accept(new DetectorVisitor(495, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/editTC/345", vce.getURL());
            assertEquals(494, vce.getRegion().getOffset());
            assertEquals(113, vce.getRegion().getLength());            
        }
    }
    

    
    
    @Test
    public void testFullVisitorWithoutImportTest1() throws Exception {
        try {
            fullTestsWithoutImport.accept(new DetectorVisitor(93, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=123&title=My+Title1&context=REST&group=GAT", vce.getURL());
            assertEquals(89, vce.getRegion().getOffset());
            assertEquals(203, vce.getRegion().getLength());            
        }
    }
    
    @Test
    public void testFullVisitorWithoutImportTest2() throws Exception {
        try {
            fullTestsWithoutImport.accept(new DetectorVisitor(324, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=234&title=My+Title2&context=REST&group=GAT1&group=GAT2", vce.getURL());
            assertEquals(322, vce.getRegion().getOffset());
            assertEquals(261, vce.getRegion().getLength());            
        }
    }
    
    @Test
    public void testFullVisitorWithoutImportTest3() throws Exception {
        try {
            fullTestsWithoutImport.accept(new DetectorVisitor(615, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=345&title=My+Title3+&context=REST&context=UI&group=GAT", vce.getURL());
            assertEquals(613, vce.getRegion().getOffset());
            assertEquals(253, vce.getRegion().getLength());            
        }
    }
    

    @Test
    public void testInvalidTest1() throws Exception {
        try {
            invalidTests.accept(new DetectorVisitor(197, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=&title=", vce.getURL());
            assertEquals(193, vce.getRegion().getOffset());
            assertEquals(76, vce.getRegion().getLength());
        }
    }
    
    @Test
    public void testInvalidTest2() throws Exception {
        try {
            invalidTests.accept(new DetectorVisitor(301, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=%24%26%5E*+56+e+456y%25%5E%24%25%5E%40%7E%3E%3F%3E%7D&title=ASSDFG+%26%5E%26**%29%29_%60%22%21%AC154&context=2&group=GAT1&group=1", vce.getURL());
            assertEquals(299, vce.getRegion().getOffset());
            assertEquals(155, vce.getRegion().getLength());
        }
    }
    
    @Test
    public void testInvalidTest3() throws Exception {
        try {
            invalidTests.accept(new DetectorVisitor(488, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=345&title=My+Title3+&context=AAAA+AAAsft34t5+34534653%25%A3%24%5E%A3%24%25%26%5E%A3%25%5E%26*%A3%25%5E%26*%22%22", vce.getURL());
            assertEquals(484, vce.getRegion().getOffset());
            assertEquals(138, vce.getRegion().getLength());
        }
    }
    
    

    @Test(expected = VisitAbortedException.class)
    public void testVisitorWithImportWithAborted() throws Exception {
        minimalTestsWithImportWithAbort.accept(new DetectorVisitor(185, ResourcesPlugin.getEncoding()));
    }

    @Test
    public void testVisitorWithoutImport() throws Exception {
        try {
            minimalTestsWithoutImport.accept(new DetectorVisitor(100, ResourcesPlugin.getEncoding()));
            fail("Should throw VisitCompleteException");
        } catch (VisitCompleteException vce) {
            assertEquals("http://taftm.lmera.ericsson.se/#tm/createTC?executionType=Automated&testCaseId=123", vce.getURL());
            assertEquals(48, vce.getRegion().getOffset());
            assertEquals(77, vce.getRegion().getLength());            
        }
    }

}
