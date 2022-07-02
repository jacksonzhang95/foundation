package com.foundation.implementation.slicingdata;

import com.foundation.implementation.slicingdata.ortherservice.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 由于使用Redis进行缓存，因此这里输出的值都是String
 *
 * @author : jacksonz
 * @date : 2021/9/27 16:57
 * @param <I> 输入数据
 */
public abstract class AbstractDataSlicingCacheInRedisHandler<I> extends AbstractDataSlicingHandler<I, String> {

    @Autowired
    private RedisService redisService;

    private final String defaultCacheEffectiveTime = String.valueOf(2 * 60 * 60);

    @Override
    public boolean lock(String lockValue) {
        return redisService.setnx(getLockKey(), lockValue, getLockExpireSecond()) == 1L;
    }

    @Override
    public void unlock(String lockValue) {
        redisService.deleteLock(getLockKey(), lockValue);
    }

    @Override
    public List<String> getDataFromCache(Integer partitionId) {
        return redisService.lrange(getCacheKey(partitionId), 0, -1);
    }

    @Override
    public void saveDataInCache(List<List<String>> partitionDataList) {
        if (CollectionUtils.isEmpty(partitionDataList)) {
            return;
        }
        for (int partitionId = 0; partitionId < partitionDataList.size(); partitionId++) {
            String cacheKey = getCacheKey(partitionId);
            List<String> dataList = partitionDataList.get(partitionId);
            if (CollectionUtils.isEmpty(dataList)) {
                continue;
            }
            String cacheEffectiveTime = StringUtils.isNotBlank(getCacheEffectiveTime()) ? defaultCacheEffectiveTime : getCacheEffectiveTime();
            redisService.delAndLpushAndExpire(cacheKey, dataList, cacheEffectiveTime);
        }
    }

    @Override
    public String getLockValue() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void cleanCache(Integer lastPartitionTotalAmount) {
        if (Objects.isNull(lastPartitionTotalAmount)) {
            return;
        }
        for (int partitionId = 0; partitionId < lastPartitionTotalAmount; partitionId++) {
            String cacheKey = getCacheKey(partitionId);
            redisService.del(cacheKey);
        }
    }

    public abstract String getLockKey();

    public abstract int getLockExpireSecond();

    public abstract String getCacheEffectiveTime();
}
