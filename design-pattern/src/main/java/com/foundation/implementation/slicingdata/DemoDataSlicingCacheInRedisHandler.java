package com.foundation.implementation.slicingdata;

import com.foundation.implementation.slicingdata.domian.DemoObj;
import com.foundation.implementation.slicingdata.strategy.DataSlicingStrategy;
import com.foundation.implementation.slicingdata.strategy.DataSlicingStrategyFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/9/27 16:57
 */
@Component
public class DemoDataSlicingCacheInRedisHandler extends AbstractDataSlicingCacheInRedisHandler<DemoObj> {


    private final String RECONCILE_DATA_SPLIT_LOCK = "lock:split:xxxxx";

    private final int LOCK_EXPIRE_SECOND = 15;

    private final String DOWNLOAD_MAIL_ACCOUNT_PARTITION_CACHE_PREFIX = "xxxxx:";

    private final long cacheEffectiveTime = 2 * 60 * 60;

    @Override
    @SuppressWarnings(value = "unchecked")
    public DataSlicingStrategy<DemoObj, String> getDataSplicingStrategy() {
        return DataSlicingStrategyFactory.getByName("");
    }

    @Override
    public String getCacheKeyPrefix() {
        return DOWNLOAD_MAIL_ACCOUNT_PARTITION_CACHE_PREFIX;
    }

    @Override
    public List<DemoObj> getAllData() {
        return new ArrayList<>();
    }

    @Override
    public int getRetryMaxTimes() {
        return 5;
    }

    @Override
    public long getWaitTimeWhenSplicing() {
        return 15 * 1000;
    }

    @Override
    public String getDataSlicingHandlerName() {
        return "DemoDataSlicingCacheInRedisHandler";
    }

    @Override
    public String getLockKey() {
        return RECONCILE_DATA_SPLIT_LOCK;
    }

    @Override
    public int getLockExpireSecond() {
        return LOCK_EXPIRE_SECOND;
    }

    @Override
    public String getCacheEffectiveTime() {
        return String.valueOf(cacheEffectiveTime);
    }

}
