package com.foundation.theory.factory.abstractfactory;

import com.foundation.theory.factory.domain.car.AbstractCar;

/**
 * @author by jacksonz
 * @classname jacksonzCommpany
 * @description TODO
 * @date 2020/8/15 11:34
 */
public class jacksonzCommpany implements ICompanyBuildProductFactory {

    @Override
    public AbstractCar buildCar() {
        return null;
    }

    @Override
    public AbstractCar buildAircraft() {
        return null;
    }
}
