import com.alibaba.nacos.api.naming.pojo.Instance
import com.github.lawrence.LoadBalance
import org.springframework.stereotype.Component

import java.security.SecureRandom

/**
 *
 * 随机选择
 * @author : Lawrence
 * date  2021/7/11 21:51
 *
 */
@Component
class RandomLB implements LoadBalance {
    @Override
    String name() {
        return "Random algorithm"
    }

    @Override
    Instance select(List<Instance> instances) {
        if (!instances) {
            throw new RuntimeException("No service list available")
        }
        SecureRandom random = SecureRandom.getInstanceStrong()
        random.setSeed(instances.size())
        instances.get(random.nextInt())
    }
}
