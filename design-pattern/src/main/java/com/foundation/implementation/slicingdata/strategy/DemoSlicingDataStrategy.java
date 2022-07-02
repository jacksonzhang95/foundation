package com.foundation.implementation.slicingdata.strategy;

import com.foundation.demo.implementation.slicingdata.domianObj;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据邮箱类型(163,qq,outlook..)
 *
 * @author : jacksonz
 * @date : 2021/9/28 10:12
 */
@Component
public class DemoSlicingDataStrategy implements DataSlicingStrategy<DemoObj, String> {

    @Override
    public List<List<String>> splitData(List<DemoObj> dataList, Integer partitionTotalAmount) {
        // do something
        return new ArrayList<>();
    }

    @Override
    public String getStrategyName() {
        return DataSplicingStrategyConstant_SLICING_DATA_STRATEGY;
    }
}
