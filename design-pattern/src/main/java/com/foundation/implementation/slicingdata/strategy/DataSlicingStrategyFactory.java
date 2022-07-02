package com.foundation.implementation.slicingdata.strategy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : jacksonz
 * @date : 2021/9/28 13:45
 */
@Component
public class DataSlicingStrategyFactory implements BeanPostProcessor {

    private static Map<String, DataSlicingStrategy> DATA_SPLICING_STRATEGY_MAP = new ConcurrentHashMap<>(8);

    public static DataSlicingStrategy getByName(String strategyName) {
        return DATA_SPLICING_STRATEGY_MAP.get(strategyName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSlicingStrategy) {
            DataSlicingStrategy slicingStrategy = (DataSlicingStrategy) bean;
            DATA_SPLICING_STRATEGY_MAP.put(slicingStrategy.getStrategyName(), slicingStrategy);
        }
        return bean;
    }

}
