import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Properties;

/**
 * @author : Lawrence
 * date  2021/7/11 14:47
 */
public class NacosUtil {
    public static void main(String[] args) throws Exception {
        //引入的client必须和server版本匹配
        /* NamingService naming = NamingFactory.createNamingService("172.27.35.10:8850");
       Instance instance = new Instance();
        instance.setIp("127.0.0.1");
        instance.setPort(8899);
        instance.setHealthy(false);
        instance.setWeight(2.0);
        Map<String, String> instanceMeta = new HashMap<>(4);
        instanceMeta.put("site", "et2");
        instance.setMetadata(instanceMeta);

        naming.registerInstance("test", instance);*/
        Properties properties = new Properties();
        properties.setProperty("serverAddr", "172.27.35.10:8850");
        // properties.setProperty("namespace", "lmy");

        NamingService naming = NamingFactory.createNamingService(properties);

        naming.registerInstance("test", "11.11.11.11", 8888, "TEST1");

        naming.registerInstance("test", "2.2.2.2", 9999, "DEFAULT");

        System.out.println(naming.getAllInstances("test"));

        System.out.println(naming.getAllInstances("test"));

        naming.subscribe("test", new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println(((NamingEvent) event).getServiceName());
                System.out.println(((NamingEvent) event).getInstances());
            }
        });
        Thread.sleep(100000);
    }

    public List<Instance> services(String serviceName) throws NacosException {
        NamingService naming = NamingFactory.createNamingService("");
        return naming.getAllInstances(serviceName);
    }
}