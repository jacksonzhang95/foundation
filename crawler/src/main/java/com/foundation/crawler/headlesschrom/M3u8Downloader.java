package com.foundation.crawler.headlesschrom;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 下载m3u8
 *
 * @author : jacksonz
 * @date : 2022/6/26 12:42
 */
public class M3u8Downloader {

    public static String TEMP_DIR = "D:/temp";

    public static int connTimeout = 30 * 60 * 1000;

    public static int readTimeout = 30 * 60 * 1000;

    public static void downloadM3u8(String targetFileName, String url) throws Exception {
        M3u8File m3u8ByURL = getM3u8ByURL(url);
        List<String> filePaths = downloadTsFile(m3u8ByURL, TEMP_DIR, targetFileName);
        mergeTs(filePaths);
    }

    private static M3u8File getM3u8ByURL(String m3u8URL) throws IOException {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(m3u8URL).openConnection();
            if (!Objects.equals(conn.getResponseCode(), 200)) {
                throw new RuntimeException("无法下载M3U8文件");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String realUrl = conn.getURL().toString();
            String basePath = realUrl.substring(0, realUrl.lastIndexOf("/") + 1);

            M3u8File ret = new M3u8File();
            ret.setBasePath(basePath);

            String line;
            float seconds = 0;
            int mIndex;
            int fileIndex = 0;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    if (line.startsWith("#EXTINF:")) {
                        line = line.substring(8);
                        if ((mIndex = line.indexOf(",")) != -1) {
                            line = line.substring(0, mIndex);
                        }
                        try {
                            seconds = Float.parseFloat(line);
                        } catch (Exception e) {
                            seconds = 0;
                        }
                        ret.addTs(new M3u8File.Ts(fileIndex, line, seconds));
                        seconds = 0;
                        ++fileIndex;
                    }
                    continue;
                }
            }
            reader.close();

            return ret;

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static List<String> downloadTsFile(M3u8File m3u8File, String tsFileBasePath, String targetFileName) throws Exception {

        boolean allSuccess = true;
        List<String> filePathList = new ArrayList<>();

        for (M3u8File.Ts ts : m3u8File.getTsList()) {

            String filePath = tsFileBasePath + File.separator + targetFileName + File.separator + ts.getIndex();
            final File file = new File(filePath);

            if (file.exists()) {
                file.delete();
            }

            if (!file.createNewFile()) {
                throw new Exception("创建文件失败");
            }

            FileOutputStream fos = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(ts.getFile());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(connTimeout);
                conn.setReadTimeout(readTimeout);
                if (conn.getResponseCode() != 200) {
                    throw new Exception("文件下载非200");
                }
                inputStream = conn.getInputStream();
                fos = new FileOutputStream(file);
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = inputStream.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();

                filePathList.add(filePath);

            } catch (Exception e) {
                allSuccess = false;
                throw e;

            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!allSuccess) {
            return null;
        }

        return filePathList;
    }

    private static void mergeTs(List<String> urls) {
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            File file = new File("D:/test123.ts");
            fos = new FileOutputStream(file);
            byte[] buf = new byte[4096];
            int len;
            for (int i = 0; i < urls.size(); i++) {
                fis = new FileInputStream("D:/temp/" + i);
                while ((len = fis.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                fis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
