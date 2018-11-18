package cn.hejinyo.learn.java8;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/17 23:31
 */
public class Interface2Impl implements Interface2 {

    @Override
    public void test() {
        System.out.println("Interface2");
    }

    public static void main(String[] args) {
        Interface2 interface2 = new Interface2Impl();
        interface2.test();

        Interface2 interface21 = ()-> System.out.println("12");
        interface21.test();
    }

}
