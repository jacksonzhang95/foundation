package com.foundation.theory.bridge;

/**
 * 桥接模式
 *
 * 利用组合形式抽象逻辑代码
 *
 * @author : jacksonz
 * @date : 2021/10/13 14:59
 */
public class BridgeDesignPattern {

    public static void main(String[] args) {
        SeverNotification severNotification = new SeverNotification(new WechatMsgSender(), new NormalMsgBuilder());
        severNotification.notify("123");
    }

}

interface MsgSender {
    void sendMsg(String message);
}

class WechatMsgSender implements MsgSender {

    @Override
    public void sendMsg(String message) {
        System.out.println("WechatMsgSender send message " + message);
    }
}

interface MsgBuilder {
    String buildMsg(String originMsg);
}

class NormalMsgBuilder implements MsgBuilder {

    @Override
    public String buildMsg(String originMsg) {
        return "origin: " + originMsg;
    }
}

abstract class Notification {

    protected MsgSender msgSender;

    protected MsgBuilder msgBuilder;

    public Notification(MsgSender msgSender, MsgBuilder msgBuilder) {
        this.msgSender = msgSender;
        this.msgBuilder = msgBuilder;
    }

    public final void notify(String message) {
        preSend();
        msgSender.sendMsg(msgBuilder.buildMsg(message));
        postSend();
    }

    public abstract void preSend();

    public abstract void postSend();
}

class SeverNotification extends Notification {

    public SeverNotification(MsgSender msgSender, MsgBuilder msgBuilder) {
        super(msgSender, msgBuilder);
    }

    @Override
    public void preSend() {

    }

    @Override
    public void postSend() {

    }
}

/**
 * 继承
 */
/*
abstract class Shape {

    public abstract void draw();

    public abstract void color();

    public void print() {
        draw();
        color();
    }
}
*/

/**
 * 组合
 */
interface IDrawService {
    void draw();
}

interface IColorService {
    void color();
}

class Shape {
    private IDrawService drawService;
    private IColorService colorService;

    public void print() {
        drawService.draw();
        colorService.color();
    }
}
