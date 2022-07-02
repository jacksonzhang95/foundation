package com.foundation.implementation.slicingdata.strategy;

import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/9/27 15:20
 * @param <I> 输入的数据类型
 * @param <O> 输出的数据类型
 */
public interface DataSlicingStrategy<I, O> {

    /**
     * 切分数据
     *
     * @param dataList 切分前的数据结合
     * @param partitionTotalAmount 切换数据分片数
     * @return 切分后数数据集合
     */
    List<List<O>> splitData(List<I> dataList, Integer partitionTotalAmount);

    /**
     * 获取数据分片策略名
     *
     * @return 数据分片策略名
     */
    String getStrategyName();
}
