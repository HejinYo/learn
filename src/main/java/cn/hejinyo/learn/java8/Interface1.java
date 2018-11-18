package cn.hejinyo.learn.java8;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/17 23:26
 */
public interface Interface1 {
    default void test1(){
        System.out.println("default method");
    }

    static void test2(){
        System.out.println("static method");
    }
}
