package cn.hejinyo.learn.redisincr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com     2017/11/22 21:33
 * @apiNote :
 */
@RestController
@RequestMapping("/incr")
public class RedisIncrController {
    @Autowired
    private RedisIncrService test;

    @RequestMapping
    public MqResourceDTO index() {
        MqResourceDTO mqResourceDTO = test.incrementValue(getList(), "test", null);
        return mqResourceDTO;
    }

    private List<MqResourceDTO> getList() {
        List<MqResourceDTO> list = new ArrayList<>();
        list.add(mock("A1", 5));
        list.add(mock("A2", 5));
        list.add(mock("B1", 4));
        list.add(mock("C1", 3));
        list.add(mock("C2", 3));
        list.add(mock("C3", 3));
        list.add(mock("C4", 3));
        list.add(mock("C5", 3));
        list.add(mock("D1", 2));
        list.add(mock("D2", 2));
        list.add(mock("D3", 2));
        list.add(mock("E1", 1));
        list.add(mock("E2", 1));
        return list;
    }

    private MqResourceDTO mock(String name, int value) {
        MqResourceDTO message = new MqResourceDTO();
        message.setValue(value);
        message.setUserName(name);
        return message;
    }
}
