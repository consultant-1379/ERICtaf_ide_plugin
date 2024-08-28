import org.testng.annotations.Test;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;

@Test(groups="GAT")
public class FullTestsWithImport {
    @TestId(id="123", title="My Title1")
    @Context(context=Context.REST)
    public void test1() {
        
    }
    
    @TestId(id="234", title="My Title2")
    @Context(context=Context.REST)
    @Test(groups={"GAT1","GAT2"})
    public void test2() {
        
    }
    
    @TestId(id="345", title="My Title3 ")
    @Context(context={Context.REST, Context.UI})
    public void test3() {
        
    }
}

