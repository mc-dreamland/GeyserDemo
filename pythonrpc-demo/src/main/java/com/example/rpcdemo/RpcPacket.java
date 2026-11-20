package com.example.rpcdemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageTypeException;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ValueType;

final class RpcPacket {
    private static final MessagePack.UnpackerConfig READER =
        new MessagePack.UnpackerConfig().withStringSizeLimit(256);
    private static final MessagePack.PackerConfig WRITER =
        new MessagePack.PackerConfig().withStr8FormatSupport(false);

    static String readRequest(byte[] data) throws IOException {
        if (data.length == 0 || data.length > 4096) {
            return null;
        }

        try (var input = READER.newUnpacker(data)) {
            boolean outerTypeFollows = beginArray(input, 3);
            if (!"ModEventC2S".equals(input.unpackString())) {
                return null;
            }

            boolean innerTypeFollows = beginArray(input, 4);
            if (!"RpcDemo".equals(input.unpackString())
                    || !"RpcDemoClientSystem".equals(input.unpackString())
                    || !"QueryServerMessage".equals(input.unpackString())) {
                return null;
            }

            int fieldCount = input.unpackMapHeader();
            if (fieldCount < 1 || fieldCount > 8) {
                return null;
            }

            String requestId = null;
            for (int i = 0; i < fieldCount; i++) {
                if ("requestId".equals(input.unpackString())) {
                    if (requestId != null) {
                        return null;
                    }
                    requestId = input.unpackString();
                } else {
                    input.skipValue();
                }
            }
            endArray(input, innerTypeFollows);
            input.unpackNil();
            endArray(input, outerTypeFollows);

            if (input.hasNext() || requestId == null || !requestId.matches("[A-Za-z0-9_-]{1,32}")) {
                return null;
            }
            return requestId;
        }
    }

    private static boolean beginArray(MessageUnpacker input, int expectedSize) throws IOException {
        boolean typeFollows = false;
        if (input.getNextFormat().getValueType() == ValueType.MAP) {
            if (input.unpackMapHeader() != 2) {
                throw new MessageTypeException("Expected a tuple wrapper");
            }

            String firstKey = input.unpackString();
            if ("value".equals(firstKey)) {
                typeFollows = true;
            } else if (!"__type__".equals(firstKey)
                    || !"tuple".equals(input.unpackString())
                    || !"value".equals(input.unpackString())) {
                throw new MessageTypeException("Invalid tuple wrapper");
            }
        }
        if (input.unpackArrayHeader() != expectedSize) {
            throw new MessageTypeException("Unexpected PythonRPC array length");
        }
        return typeFollows;
    }

    private static void endArray(MessageUnpacker input, boolean typeFollows) throws IOException {
        if (typeFollows && (!"__type__".equals(input.unpackString())
                || !"tuple".equals(input.unpackString()))) {
            throw new MessageTypeException("Invalid tuple wrapper");
        }
    }

    static byte[] writeReply(String requestId) throws IOException {
        var bytes = new ByteArrayOutputStream();
        try (var gzip = new GZIPOutputStream(bytes); var output = WRITER.newPacker(gzip)) {
            output.packArrayHeader(3).packString("ModEventS2C");
            output.packArrayHeader(4)
                .packString("RpcDemo")
                .packString("RpcDemoServerSystem")
                .packString("ServerMessage");
            output.packMapHeader(2)
                .packString("requestId").packString(requestId)
                .packString("tipKey").packString("welcome");
            output.packNil();
        }
        return bytes.toByteArray();
    }
}
