import com.github.lawrence.client.RpcClient;
import com.github.lawrence.codes.RpcMsg;
import com.github.lawrence.config.RpcConfig;
import com.github.lawrence.config.StartRegistryRpcService;
import com.github.lawrence.utils.CacheUtil;
import com.github.lawrence.utils.SyncInvokeUtil;
import io.netty.channel.Channel;

/**
 * @author : Lawrence
 * date  2021/7/15 20:49
 */

public class Mock {

    public void mock() {
        RpcConfig rpcConfig = new RpcConfig();
        StartRegistryRpcService rpcService = new StartRegistryRpcService(rpcConfig);
        String serviceName = "service";

        CacheUtil.addServiceInfo(serviceName, new TestService());

        rpcService.startRpcListener();
        Channel channel = RpcClient.connect0(rpcConfig.getServiceIp(), rpcConfig.getServicePort());


        RpcMsg rpcMsg = new RpcMsg(RpcMsg.Data.createReq(serviceName, "testM", "Rpc"));

        String rpcResp = SyncInvokeUtil.syncRequest(channel, rpcMsg, String.class);
        System.out.println(rpcResp);
    }

    public static void main(String[] args) {
        new Mock().mock();
    }

}