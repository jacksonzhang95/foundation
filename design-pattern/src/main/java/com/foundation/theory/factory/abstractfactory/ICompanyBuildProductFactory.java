package com.foundation.theory.factory.abstractfactory;

import com.foundation.theory.factory.domain.car.AbstractCar;

/**
 * @author by jacksonz
 * @classname ICompanyBuildProdutFactory
 * @description TODO
 * @date 2020/8/15 11:32
 */
public interface ICompanyBuildProductFactory {

    AbstractCar buildCar();

    AbstractCar buildAircraft();
}
