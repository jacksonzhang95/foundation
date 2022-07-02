package com.foundation.theory.factory.abstractmethod;

import com.foundation.theory.factory.domain.car.AbstractCar;
import com.foundation.theory.factory.domain.car.BlueCar;

/**
 * @author by jacksonz
 * @classname BlueCarFactory
 * @description TODO
 * @date 2020/8/15 11:30
 */
public class BlueCarFactory implements ICarFactoryMethod {
    @Override
    public AbstractCar buildCar() {
        BlueCar blueCar = new BlueCar();
        // .....省略一大段复杂构造
        return blueCar;
    }
}
