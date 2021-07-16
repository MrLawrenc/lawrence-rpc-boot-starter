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
    public void mock(boolean exception) {
        //本地测试配置
        RpcConfig rpcConfig = new RpcConfig();
        rpcConfig.setServiceIp("127.0.0.1");
        rpcConfig.setServicePort(9010);

        //设置缓存服务，并启动rpc服务端
        StartRegistryRpcService rpcService = new StartRegistryRpcService(rpcConfig);
        String serviceName = "service";
        CacheUtil.addServiceInfo(serviceName, new TestService());
        rpcService.startRpcListener();

        //rpc客户端连接
        Channel channel = RpcClient.connect0(rpcConfig.getServiceIp(), rpcConfig.getServicePort());
        //发送rpc同步消息并阻塞等待返回
        RpcMsg rpcMsg = new RpcMsg(RpcMsg.Data.createReq(serviceName, exception ? "testMEx" : "testM", "Rpc"));
        String rpcResp = SyncInvokeUtil.syncRequest(channel, rpcMsg, String.class);

        System.out.println("rps invoke result:" + rpcResp);
    }

    public static void main(String[] args) {
        //new Mock().mock(false);
        new Mock().mock(true);
    }

}