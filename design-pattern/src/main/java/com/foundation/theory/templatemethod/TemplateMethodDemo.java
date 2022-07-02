package com.foundation.theory.templatemethod;

/**
 * @author : jacksonz
 * @date : 2021/10/18 17:50
 */
public class TemplateMethodDemo {
    public static void main(String[] args) {
        FishCooking fishCooking = new FishCooking();
        fishCooking.cook();
        PorkCooking porkCooking = new PorkCooking();
        porkCooking.cook();
    }
}

abstract class Cooking {

    public void cook() {
        // 准备食材
        prepareFood();
        // 烹饪食材
        cookingFood();
        // 上菜
        sendFood();
    }
    public abstract void prepareFood();
    public abstract void cookingFood();
    public abstract void sendFood();
}

class FishCooking extends Cooking {

    @Override
    public void prepareFood() {
        System.out.println("清洗并去除鱼鳞");
    }

    @Override
    public void cookingFood() {
        System.out.println("红烧鱼");
    }

    @Override
    public void sendFood() {
        System.out.println("把红烧鱼踢上桌");
    }
}

class PorkCooking extends Cooking {

    @Override
    public void prepareFood() {
        System.out.println("清洗猪肉");
    }

    @Override
    public void cookingFood() {
        System.out.println("焖猪肉");
    }

    @Override
    public void sendFood() {
        System.out.println("把焖猪肉送上桌");
    }
}