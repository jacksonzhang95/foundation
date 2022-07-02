package com.foundation.id.generator.snowflake;

import com.foundation.id.generator.IDGenerator;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Random;

/**
 * @author : jacksonz
 * @date : 2022/5/29 21:37
 * @description : 雪花id生成器
 */
@Slf4j
public class SnowflakeIDGenerator implements IDGenerator {

    // 1位未使用符 41位时间戳 10位机器号 12位序列号
    //     0        x           x         x

    // 未使用符占用位数
    private final static long UNUSER_BIT = 1;
    // 时间戳占用位数
    private final static long TIMESTAMP_BIT = 41;
    // 数据中心占用的位数
    private final static long DATA_CENTER_BIT = 5;
    // 机器标识占用的位数
    private final static long MACHINE_BIT = 5;
    // 序列号占用的位数
    private final static long SEQUENCE_BIT = 12;

    /**
     * 起始的时间戳
     */
    private final static long START_TIMESTAMP = 1480166465631L;

    /**
     * 每一部分的最大值
     */
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    //数据中心
    private long dataCenterId;
    //机器标识
    private long machineId;
    //序列号
    private long sequence = 0L;
    //上一次时间戳
    private long lastTimeStamp = -1L;

    /**
     * 根据指定的数据中心ID和机器标志ID生成指定的序列号
     *
     * @param dataCenterId 数据中心ID
     * @param machineId    机器标志ID
     */
    public SnowflakeIDGenerator(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("DtaCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0！");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("MachineId can't be greater than MAX_MACHINE_NUM or less than 0！");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    /**
     * unsafe
     * ip转机器号
     */
    public SnowflakeIDGenerator(long dataCenterId, Integer version) {
        this(dataCenterId, createWorkId(version));
    }

    @Override
    public String generate() {
        return String.valueOf(nextId());
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    private synchronized long nextId() {
        long currTimeStamp = getNewTimeStamp();
        if (currTimeStamp < lastTimeStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currTimeStamp == lastTimeStamp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currTimeStamp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastTimeStamp = currTimeStamp;

        return (currTimeStamp - START_TIMESTAMP) << TIMESTAMP_LEFT //时间戳部分
                | dataCenterId << DATA_CENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }

    /**
     * 创建机器号
     * @param version
     * @return
     */
    private static long createWorkId(Integer version) {
        if (Objects.equals(version, 1)) {
            return initWorkIdV1();
        }
        return 0;
    }

    /**
     * 获取本机ip
     *
     * @return
     */
    private static String getHostIp() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && !ip.getHostAddress().contains(":")) {
                        System.out.println("本机的IP = " + ip.getHostAddress());
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取时间戳
     * @return
     */
    private long getNextMill() {
        long mill = getNewTimeStamp();
        while (mill <= lastTimeStamp) {
            mill = getNewTimeStamp();
        }
        return mill;
    }

    /**
     *
     * @return
     */
    private long getNewTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * UNSAFE
     * <p>
     * 用服务器ip生成，ip转化为long再模32
     * （如有集群部署，且机器ip是连续的可用，非连续的不可用）
     *
     * @return
     */
    private static long initWorkIdV1() {
        Random rd = new Random();
        long workerId = rd.nextInt(31);
        long datacenterId = rd.nextInt(31);

        //获取当前ip,生成工作id
        String ip = getHostIp();
        if (ip != null) {
            workerId = Long.parseLong(ip.replaceAll("\\.", ""));
            //因为占用5位，模32
            workerId = workerId % 32;
        }
        return workerId;
    }
}
