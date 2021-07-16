import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author : Lawrence
 * date  2021/7/15 21:10
 */
public class TestService {
    public String testM(String p) throws Exception {
        TimeUnit.SECONDS.sleep(3);
        return "Hello " + p + " ! Server time -> " + new Date();
    }

    public String testMEx(String p) throws Exception {
        TimeUnit.SECONDS.sleep(1);
        throw new RuntimeException("test server exception!");
    }
}