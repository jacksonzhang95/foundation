package com.foundation.algorithm.hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * MurmurHash算法：
 *  高运算性能，低碰撞率，由Austin Appleby创建于2008年
 *  2011年 Appleby被Google雇佣，随后Google推出其变种的CityHash算法
 *
 * @author : jacksonz
 * @date : 2021/10/18 9:54
 */
public class MurMurHashServiceImpl implements IHashService {

    @Override
    public Long hash(Object key) {
        ByteBuffer buf = ByteBuffer.wrap(key.toString().getBytes());
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }
}
