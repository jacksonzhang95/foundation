package com.foundation.crawler.headlesschrom;

/**
 * @author : jacksonz
 * @date : 2022/6/26 12:41
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class M3u8File {

    private String basePath;

    private List<Ts> tsList = new ArrayList<>();

    private long startTime;

    private long endTime;

    private long startDownloadTime;

    private long endDownloadTime;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public List<Ts> getTsList() {
        return tsList;
    }

    public void setTsList(List<Ts> tsList) {
        this.tsList = tsList;
    }

    public void addTs(Ts ts) {
        this.tsList.add(ts);
    }

    public long getStartDownloadTime() {
        return startDownloadTime;
    }

    public void setStartDownloadTime(long startDownloadTime) {
        this.startDownloadTime = startDownloadTime;
    }

    public long getEndDownloadTime() {
        return endDownloadTime;
    }

    public void setEndDownloadTime(long endDownloadTime) {
        this.endDownloadTime = endDownloadTime;
    }

    /**
     * 获取开始时间
     *
     * @return
     */
    public long getStartTime() {
        if (tsList.size() > 0) {
            Collections.sort(tsList);
            startTime = tsList.get(0).getLongDate();
            return startTime;
        }
        return 0;
    }

    /**
     * 获取结束时间(加上了最后一段时间的持续时间)
     *
     * @return
     */
    public long getEndTime() {
        if (tsList.size() > 0) {
            Ts m3U8Ts = tsList.get(tsList.size() - 1);
            endTime = m3U8Ts.getLongDate() + (long) (m3U8Ts.getSeconds() * 1000);
            return endTime;
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("basePath: " + basePath);
        for (Ts ts : tsList) {
            sb.append("\nts_file_name = " + ts);
        }
        sb.append("\n\nstartTime = " + startTime);
        sb.append("\n\nendTime = " + endTime);
        sb.append("\n\nstartDownloadTime = " + startDownloadTime);
        sb.append("\n\nendDownloadTime = " + endDownloadTime);
        return sb.toString();
    }

    public static class Ts implements Comparable<Ts> {

        private Integer index;

        private String file;

        private float seconds;

        public Ts(Integer index, String file, float seconds) {
            this.index = index;
            this.file = file;
            this.seconds = seconds;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public float getSeconds() {
            return seconds;
        }

        public void setSeconds(float seconds) {
            this.seconds = seconds;
        }

        @Override
        public String toString() {
            return file + " (" + seconds + "sec)";
        }

        /**
         * 获取时间
         */
        public long getLongDate() {
            try {
                return Long.parseLong(file.substring(0, file.lastIndexOf(".")));
            } catch (Exception e) {
                return 0;
            }
        }

        @Override
        public int compareTo(Ts o) {
            return file.compareTo(o.file);
        }
    }
}


