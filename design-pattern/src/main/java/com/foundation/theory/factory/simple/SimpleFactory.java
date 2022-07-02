package com.foundation.theory.factory.simple;

import com.foundation.theory.factory.domain.car.AbstractCar;
import com.foundation.theory.factory.domain.car.BlueCar;
import com.foundation.theory.factory.domain.car.RedCar;

/**
 * 简单工厂
 *  作用于
 *
 * @author by jacksonz
 * @classname StaticFactory
 * @description TODO
 * @date 2020/8/15 10:52
 */
public class SimpleFactory {

    public AbstractCar getCar(String carType) {
        AbstractCar car = null;
        switch (carType) {
            case "red":
                car = new RedCar();
                break;
            case "blue":
                car = new BlueCar();
                break;
            default:
                break;

        }
        return car;
    }
}
