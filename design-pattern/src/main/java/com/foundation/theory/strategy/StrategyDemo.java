package com.foundation.theory.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : jacksonz
 * @date : 2021/10/19 9:23
 */
public class StrategyDemo {
    public List<Integer> sort(String useSortType, List<Integer> originList) {
        SortStrategy sortStrategy = SortStrategyFactory.getByType(useSortType);
        return sortStrategy.sort(originList);
    }
}
class SortStrategyFactory {
    private static Map<String, SortStrategy> sortStrategyMap = new HashMap<>();
    static {
        sortStrategyMap.put("quickSort",new QuickSort());
        sortStrategyMap.put("mergeSort",new MergeSort());
    }
    public static SortStrategy getByType(String typeName) {
        return sortStrategyMap.get(typeName);
    }
}
interface SortStrategy {
    List<Integer> sort(List<Integer> originList);
}

class QuickSort implements SortStrategy {
    @Override
    public List<Integer> sort(List<Integer> originList) {
        // 实现快速排序
        return null;
    }
}
class MergeSort implements SortStrategy {
    @Override
    public List<Integer> sort(List<Integer> originList) {
        // 实现归并排序
        return null;
    }
}
