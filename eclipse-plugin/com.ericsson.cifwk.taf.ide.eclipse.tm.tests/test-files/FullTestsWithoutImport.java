
@org.testng.annotations.Test(groups="GAT")
public class FullTestsWithoutImport {
    @com.ericsson.cifwk.taf.annotations.TestId(id="123", title="My Title1")
    @com.ericsson.cifwk.taf.annotations.Context(context=com.ericsson.cifwk.taf.annotations.Context.REST)
    public void test1() {
        
    }
    
    @com.ericsson.cifwk.taf.annotations.TestId(id="234", title="My Title2")
    @com.ericsson.cifwk.taf.annotations.Context(context=com.ericsson.cifwk.taf.annotations.Context.REST)
    @org.testng.annotations.Test(groups={"GAT1","GAT2"})
    public void test2() {
        
    }
    
    @com.ericsson.cifwk.taf.annotations.TestId(id="345", title="My Title3 ")
    @com.ericsson.cifwk.taf.annotations.Context(context={com.ericsson.cifwk.taf.annotations.Context.REST, com.ericsson.cifwk.taf.annotations.Context.UI})
    public void test3() {
        
    }
}

