package com.qiqi;


import java.io.IOException;

class DataTest {
    public static void main(String[] args) {
        Student student = new Student(26f, "qiqi");
        byte[] en = Student.ADAPTER.encode(student);
        System.out.println("en " + new String(en));
        try {
            Student s = Student.ADAPTER.decode(en);
            System.out.println("de " + s.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
