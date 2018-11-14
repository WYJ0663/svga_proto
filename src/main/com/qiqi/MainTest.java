package com.qiqi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opensource.svgaplayer.proto.AudioEntity;
import com.opensource.svgaplayer.proto.MovieEntity;
import okio.ByteString;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class MainTest {

    public static String svga_name = "rose_2.0.0.svga";

    public static void main(String[] args) {
        File file = new File("assets/" + svga_name);
        log("file path:" + file.getAbsolutePath() + " " + file.exists());

        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = readAsBytes(inputStream);
            byte[] inflate = inflate(bytes);
            writeFile(inflate);
            MovieEntity movieEntity = MovieEntity.ADAPTER.decode(inflate);

//            log(movieEntity.toString());
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(movieEntity);
            log(json);

            writeFile(json);
            writeFile(movieEntity.images);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeFile(Map<String, ByteString> images) {
        for (Map.Entry<String, ByteString> image : images.entrySet()) {
            File file = newFile("product/" + svga_name + "/" + image.getKey() + ".png");
            ByteString value = image.getValue();
            writeFile(value.toByteArray(), file);
        }
    }

    private static File newFile(String path) {
        File file = new File(path);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private static void writeFile(String json) {
        File file = newFile("product/" + svga_name + "/" + svga_name + ".json");
        byte[] j = json.getBytes();
        writeFile(j, file);
    }

    private static void writeFile(byte[] j) {
        File file = newFile("product/" + svga_name + "/" + svga_name + ".unzip.json");
        writeFile(j, file);
    }

    private static void writeFile(byte[] j, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(j, 0, j.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void log(String s) {
        System.out.println(s);
    }

    private static byte[] inflate(byte[] byteArray) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(byteArray, 0, byteArray.length);
            byte[] inflatedBytes = new byte[2048];
            ByteArrayOutputStream inflatedOutputStream = new ByteArrayOutputStream();
            while (true) {
                int count = inflater.inflate(inflatedBytes, 0, 2048);
                if (count <= 0) {
                    break;
                } else {
                    inflatedOutputStream.write(inflatedBytes, 0, count);
                }
            }
            return inflatedOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] readAsBytes(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] byteArray = new byte[2048];
        while (true) {
            try {
                int count = inputStream.read(byteArray, 0, 2048);
                if (count <= 0) {
                    break;
                } else {
                    byteArrayOutputStream.write(byteArray, 0, count);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }


    private void unzip(ByteArrayInputStream inputStream, String cacheKey) {
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
        FileOutputStream fileOutputStream = null;
        try {
            while (true) {
                ZipEntry zipItem = zipInputStream.getNextEntry();
                if (zipItem == null) break;

                if (zipItem.getName().contains("/")) {
                    continue;
                }
                File file = cacheDir();
                fileOutputStream = new FileOutputStream(file);
                byte[] buff = new byte[2048];
                while (true) {
                    int readBytes = zipInputStream.read(buff);
                    if (readBytes <= 0) {
                        break;
                    }
                    fileOutputStream.write(buff, 0, readBytes);
                }
                fileOutputStream.close();
                zipInputStream.closeEntry();
            }
        } catch (Exception e) {

        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                zipInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private File cacheDir() {
        File file = new File("product/ss.w");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
