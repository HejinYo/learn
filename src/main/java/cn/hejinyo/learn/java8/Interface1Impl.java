package cn.hejinyo.learn.java8;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/17 23:27
 */
public class Interface1Impl implements  Interface1 {
    public static void main(String[] args) {
        Interface1 interface1 = new Interface1Impl();
        interface1.test1();
        Interface1.test2();
    }
}
