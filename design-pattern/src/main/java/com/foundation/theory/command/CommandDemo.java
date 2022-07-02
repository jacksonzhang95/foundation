package com.foundation.theory.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : jacksonz
 * @date : 2021/10/30 19:36
 */
public class CommandDemo {
    public static void main(String[] args) {
        CommandCenter commandCenter = new CommandCenter();
        commandCenter.register(new DataPrintCommand());
        commandCenter.register(new DataSplitCommand());

        Command command = commandCenter.getByName("split");
        command.execute();
    }
}

interface Command {
    void execute();
    String getCommandName();
}

class DataSplitCommand implements Command {

    @Override
    public void execute() {
        System.out.println("切分数据");
    }

    @Override
    public String getCommandName() {
        return "split";
    }
}

class DataPrintCommand implements Command {

    @Override
    public void execute() {
        System.out.println("数据打印");
    }

    @Override
    public String getCommandName() {
        return "print";
    }
}

class CommandCenter {
    private static final Map<String, Command> handlerMap = new ConcurrentHashMap<String, Command>();

    public void register(Command command) {
        handlerMap.put(command.getCommandName(), command);
    }

    public Command getByName(String name) {
        return handlerMap.get(name);
    }
}
