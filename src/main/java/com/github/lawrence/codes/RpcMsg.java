package com.github.lawrence.codes;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : Lawrence
 * date  2021/7/11 18:45
 */
@Data
public class RpcMsg {
    /**
     * 魔数 定值
     */
    private byte magic = 0x35;
    private byte version = 1;

    private Data data;

    public RpcMsg(byte version, Data data) {
        this.version = version;
        this.data = data;
    }

    public RpcMsg(Data data) {
        this.data = data;
    }

    @lombok.Data
    public static class Data {
        /**
         * req or resp
         * req -> 1
         * resp success -> 2
         * resp exception-> 3
         */
        private byte type;

        private String methodName;
        //每个参数的json串
        private List<String> argsJson;
        //如java.lang.String
        private List<String> argsType;

        private String respJson;

        public static Data createReq(String methodName, Object... args) {
            Data data = new Data();
            data.type = 1;
            data.methodName = methodName;
            if (Objects.nonNull(args)) {
                data.argsJson = new ArrayList<>(args.length);
                data.argsType = new ArrayList<>(args.length);
                for (Object arg : args) {
                    data.argsJson.add(JSON.toJSONString(arg));
                    data.argsType.add(arg.getClass().getName());
                }
            }
            return data;
        }

        public boolean req() {
            return type == 1;
        }

        public boolean success() {
            return type == 2;
        }

        public boolean exception() {
            return type == 3;
        }
    }
}