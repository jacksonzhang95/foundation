package com.foundation.theory.factory.abstractmethod;

import com.foundation.theory.factory.domain.car.AbstractCar;
import com.foundation.theory.factory.domain.car.RedCar;

/**
 * @author by jacksonz
 * @classname a
 * @description TODO
 * @date 2020/8/15 11:29
 */
public class RedCarFactory implements ICarFactoryMethod {
    @Override
    public AbstractCar buildCar() {
        RedCar redCar = new RedCar();
        // .....省略一大段复杂构造
        return redCar;
    }
}
