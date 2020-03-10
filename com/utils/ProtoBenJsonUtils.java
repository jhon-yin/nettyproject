package com.utils;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;

public class ProtoBenJsonUtils {
    public static String toJson(Message sourceMessage)
            throws IOException {
        String json = JsonFormat.printer().print(sourceMessage);
        return json;
    }

    public static Message toProtoBean(Message.Builder targetBuilder, String json) throws IOException {
        JsonFormat.parser().merge(json, targetBuilder);
        return targetBuilder.build();
    }
}
