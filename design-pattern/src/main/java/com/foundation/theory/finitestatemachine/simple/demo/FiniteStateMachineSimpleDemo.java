package com.foundation.theory.finitestatemachine.simple.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : jacksonz
 * @date : 2021/10/19 10:54
 */
public class FiniteStateMachineSimpleDemo {
}

interface ISoldier {
    void meetMonster(Integer monsterLevel, SoldierLevelMachine levelMachine);
}
class SoldierLevelOne implements ISoldier {
    @Override
    public void meetMonster(Integer monsterLevel, SoldierLevelMachine levelMachine) {
        if (monsterLevel > 2) {
            levelMachine.defeated();
        } else {
            levelMachine.victory();
        }
    }
}
class SoldierLevelTwo implements ISoldier {
    @Override
    public void meetMonster(Integer monsterLevel, SoldierLevelMachine levelMachine) {
        if (monsterLevel > 3) {
            levelMachine.defeated();
        } else {
            levelMachine.victory();
        }
    }
}
class SoldierFactory {
    private static Map<Integer, ISoldier> soldierMap = new HashMap<>();
    static {
        soldierMap.put(1, new SoldierLevelOne());
        soldierMap.put(2, new SoldierLevelTwo());
    }
    public static ISoldier getByLevel(Integer level) {
        return soldierMap.get(level);
    }
}
class SoldierLevelMachine {
    private Integer level = 1;
    private ISoldier currentSoldier;
    private void upgrade() {
        level = level + 1;
        currentSoldier = SoldierFactory.getByLevel(level);
    }
    private void degrade() {
        if (level == 1) {
            throw new RuntimeException("you die");
        }
        level = level - 1;
        currentSoldier = SoldierFactory.getByLevel(level);
    }
    public void victory() {
        upgrade();
    }
    public void defeated() {
        degrade();
    }
    public void meetMonster(Integer monsterLevel) {
        currentSoldier.meetMonster(monsterLevel, this);
    }
}