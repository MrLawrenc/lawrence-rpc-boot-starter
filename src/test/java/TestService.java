import java.util.Date;

/**
 * @author : Lawrence
 * date  2021/7/15 21:10
 */
public class TestService {
    public String testM(String p) {
        return "Hello " + p+" ! Server time -> "+ new Date();
    }
}