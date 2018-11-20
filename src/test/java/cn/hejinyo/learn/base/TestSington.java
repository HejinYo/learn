package cn.hejinyo.learn.base;

/**
 * 单例模式，双检锁
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/11/20 18:01
 */
public class TestSington {

    private static volatile TestSington testSington;

    public static TestSington getInstance() {
        if (testSington == null) {
            synchronized (TestSington.class) {
                if (testSington == null) {
                    return new TestSington();
                }
            }
        }
        return testSington;
    }
}
