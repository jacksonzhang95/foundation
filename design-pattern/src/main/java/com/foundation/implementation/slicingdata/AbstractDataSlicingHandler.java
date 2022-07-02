package com.foundation.implementation.slicingdata;

import com.foundation.implementation.slicingdata.strategy.DataSlicingStrategy;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2021/9/27 15:10
 * @param <I> 输入的数据类型
 * @param <O> 输出的数据类型
 */
public abstract class AbstractDataSlicingHandler<I, O> {

    private volatile Integer lastPartitionTotalAmount = null;

    private volatile Integer currentPartitionTotalAmount = null;

    private long lastUpdatePartitionDataTime = 0L;

    

    public List<O> getData(Integer partitionId, Integer partitionTotalAmount) {
        int retryTimes = 0;
        do {
            currentPartitionTotalAmount = partitionTotalAmount;
            if (needCleanCache()) {
                cleanCache(lastPartitionTotalAmount);
            }
            List<O> dataFromCache = getDataFromCache(partitionId);
            if (!CollectionUtils.isEmpty(dataFromCache)) {
                return dataFromCache;
            }
            String lockValue = getLockValue();
            try {
                if (!lock(lockValue)) {
                    Thread.sleep(getWaitTimeWhenSplicing());
                    continue;
                }
                DataSlicingStrategy<I, O> dataSplicingStrategy = getDataSplicingStrategy();
                List<List<O>> partitionDataList = dataSplicingStrategy.splitData(getAllData(), partitionTotalAmount);
                if (CollectionUtils.isEmpty(partitionDataList)) {
                    return null;
                }
                saveDataInCache(partitionDataList);
                lastUpdatePartitionDataTime = System.currentTimeMillis();
                lastPartitionTotalAmount = currentPartitionTotalAmount;
                currentPartitionTotalAmount = null;
                return partitionDataList.get(partitionId);

            } catch (InterruptedException e) {
                // 中断异常
            } finally {
                unlock(lockValue);
            }
            ++retryTimes;
        } while (retryTimes < getRetryMaxTimes());
        return null;
    }

    public abstract DataSlicingStrategy<I, O> getDataSplicingStrategy();

    public abstract String getCacheKeyPrefix();

    public abstract List<I> getAllData();

    public abstract boolean lock(String lockValue);

    public abstract void unlock(String lockValue);

    public abstract int getRetryMaxTimes();

    public abstract List<O> getDataFromCache(Integer partitionId);

    public abstract void saveDataInCache(List<List<O>> partitionDataList);

    public abstract long getWaitTimeWhenSplicing();

    public abstract String getLockValue();

    protected String getCacheKey(Integer partitionId) {
        String cacheKeyPrefix = getCacheKeyPrefix();
        return cacheKeyPrefix + ":partition:" + partitionId;
    }

    public boolean needCleanCache() {
        boolean needClean = !Objects.equals(lastPartitionTotalAmount, currentPartitionTotalAmount);
        return needClean;
    }

    public abstract void cleanCache(Integer lastPartitionTotalAmount );

    public long getLastUpdatePartitionDataTime() {
        return lastUpdatePartitionDataTime;
    }

    public abstract String getDataSlicingHandlerName();
}
