package com.qiqi;


import com.google.gson.annotations.Expose;
import com.squareup.wire.FieldEncoding;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import okio.ByteString;

import java.io.IOException;

public class Student extends Message<Student, Student.Builder> {
    public static final ProtoAdapter<Student> ADAPTER = new ProtoAdapter_Transform();

    @WireField(
            tag = 1,
            adapter = "com.squareup.wire.ProtoAdapter#FLOAT"
    )
    @Expose
    public final Float id;

    @WireField(
            tag = 1,
            adapter = "com.squareup.wire.ProtoAdapter#STRING"
    )
    @Expose
    public final String name;

    public Student(Float id, String name) {
        this(id, name, ByteString.EMPTY);
    }

    public Student(Float id, String name, ByteString unknownFields) {
        super(ADAPTER, unknownFields);
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public Builder newBuilder() {
        Builder builder = new Builder();
        builder.id = id;
        builder.name = name;
        builder.addUnknownFields(unknownFields());
        return builder;
    }

    public static final class Builder extends Message.Builder<Student, Builder> {
        public Float id;
        public String name;

        @Override
        public Student build() {
            return new Student(id, name);
        }

        public void id(Float decode) {
            id = decode;
        }

        public void name(String decode) {
            name = decode;
        }
    }

    private static final class ProtoAdapter_Transform extends ProtoAdapter<Student> {

        public ProtoAdapter_Transform() {
            super(FieldEncoding.LENGTH_DELIMITED, Student.class);
        }

        @Override
        public int encodedSize(Student value) {
            return (value.id != null ? ProtoAdapter.FLOAT.encodedSizeWithTag(1, value.id) : 0)
                    + (value.name != null ? ProtoAdapter.STRING.encodedSizeWithTag(2, value.name) : 0)
                    + value.unknownFields().size();
        }

        @Override
        public void encode(ProtoWriter writer, Student value) throws IOException {
            if (value.id != null) ProtoAdapter.FLOAT.encodeWithTag(writer, 1, value.id);
            if (value.name != null) ProtoAdapter.STRING.encodeWithTag(writer, 2, value.name);
            writer.writeBytes(value.unknownFields());
        }

        @Override
        public Student decode(ProtoReader reader) throws IOException {
            Builder builder = new Builder();
            long token = reader.beginMessage();
            for (int tag; (tag = reader.nextTag()) != -1;) {
                System.out.println("tag "+tag);
                switch (tag) {
                    case 1: builder.id(ProtoAdapter.FLOAT.decode(reader)); break;
                    case 2: builder.name(ProtoAdapter.STRING.decode(reader)); break;
                    default: {
                        FieldEncoding fieldEncoding = reader.peekFieldEncoding();
                        Object value = fieldEncoding.rawProtoAdapter().decode(reader);
                        builder.addUnknownField(tag, fieldEncoding, value);
                    }
                }
            }
            reader.endMessage(token);
            return builder.build();
        }

        @Override
        public Student redact(Student value) {
            Builder builder = value.newBuilder();
            builder.clearUnknownFields();
            return builder.build();
        }
    }
}
