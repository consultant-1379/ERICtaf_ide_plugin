import org.testng.annotations.Test;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;

@Test(groups={})
public class InvalidTests {
    @TestId(id="", title="")
    @Context(context={})
    public void test1() {
        
    }
    
    @TestId(id="$&^* 56 e 456y%^$%^@~>?>}", title="ASSDFG &^&**))_`\"!¬154")
    @Context(context=2)
    @Test(groups={"GAT1", 1L})
    public void test2() {
        
    }
    
    @TestId(id="345", title="My Title3 ")
    @Context(context={"AAAA AAAsft34t5 34534653%£$^£$%&^£%^&*£%^&*\"\""})
    public void test3() {
        
    }
}

