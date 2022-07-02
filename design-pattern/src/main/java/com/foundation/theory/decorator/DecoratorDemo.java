package com.foundation.theory.decorator;

/**
 * @author by jacksonz
 * @classname a
 * @description TODO
 * @date 2020/8/28 22:03
 */

public class DecoratorDemo {

    public static void main(String[] args) {
        CarEngine carEngine = new CarEngine();
        PushFireEngine pushFireEngine = new PushFireEngine(carEngine);
        SoundWavesEngine soundWavesEngine = new SoundWavesEngine(pushFireEngine);
        soundWavesEngine.drive();
    }

}

interface Engine {
    void drive();
}

class CarEngine implements Engine {

    @Override
    public void drive() {
        System.out.println("drive car run");
    }
}

abstract class StrongerEngine implements Engine {
    protected Engine engine;

    public StrongerEngine(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void drive() {
        engine.drive();
    }
}

class PushFireEngine extends StrongerEngine {
    public PushFireEngine(Engine engine) {
        super(engine);
    }

    public void pushFire() {
        System.out.println("make the engine push fire");
    }

    @Override
    public void drive() {
        pushFire();
        engine.drive();
    }
}

class SoundWavesEngine extends StrongerEngine {
    public SoundWavesEngine(Engine engine) {
        super(engine);
    }

    public void soundWaves() {
        System.out.println("make the engine hava sound waves");
    }

    @Override
    public void drive() {
        soundWaves();
        engine.drive();
    }
}