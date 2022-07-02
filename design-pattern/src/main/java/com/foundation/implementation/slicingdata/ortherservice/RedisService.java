package com.foundation.implementation.slicingdata.ortherservice;

import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/10/13 11:18
 */
public interface RedisService {

    void del(String cacheKey);

    void delAndLpushAndExpire(String cacheKey, List<String> dataList, String cacheEffectiveTime);

    List<String> lrange(String cacheKey, int start, int end);

    void deleteLock(String lockKey, String lockValue);

    long setnx(String lockKey, String lockValue, int lockExpireSecond);
}
