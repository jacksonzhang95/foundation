package com.foundation.algorithm.hash.consistency;

/**
 * @author : jacksonz
 * @date : 2021/10/18 9:57
 */
public class Node<T> {
    private String ip;
    private String name;

    public Node(String ip, String name) {
        this.ip = ip;
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 使用IP当做hash的Key
     *
     * @return String
     */
    @Override
    public String toString() {
        return ip;
    }
}
