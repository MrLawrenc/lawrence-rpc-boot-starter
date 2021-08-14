package com.github.lawrence.codes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : Lawrence
 * date  2021/7/11 18:45
 */
@Data
@NoArgsConstructor
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

    public boolean success() {
        return Objects.nonNull(this.data) && this.data.type == 2;
    }

    public boolean exception() {
        return Objects.nonNull(this.data) && this.data.type == 3;
    }

    public String respResult() {
        return Objects.isNull(this.data) ? null : this.data.respJson;
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

        public static Data createReq(String serviceName, String methodName, Object... args) throws JsonProcessingException {
            Data data = new Data();
            data.type = 1;
            data.methodName = serviceName + "#" + methodName;
            if (Objects.nonNull(args)) {
                data.argsJson = new ArrayList<>(args.length);
                data.argsType = new ArrayList<>(args.length);
                for (Object arg : args) {
                    data.argsJson.add(new ObjectMapper().writeValueAsString(arg));
                    data.argsType.add(arg.getClass().getName());
                }
            }
            return data;
        }

        public static Data createSuccessResp(String result) {
            return createResp(result, (byte) 2);
        }

        public static Data createExceptionResp(String result) {
            return createResp(result, (byte) 3);
        }

        public static Data createResp(String result, byte type) {
            Data data = new Data();
            data.respJson = result;
            data.type = type;
            return data;
        }

        public String findServiceOrMethod(boolean methodName) {
            return this.methodName.split("#")[methodName ? 1 : 0];
        }

        public boolean req() {
            return type == 1;
        }


    }
}