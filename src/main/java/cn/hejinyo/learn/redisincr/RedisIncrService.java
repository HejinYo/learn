package cn.hejinyo.learn.redisincr;

import cn.hejinyo.learn.redis.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * @author : heshuangshuang
 * @date : 2017/11/24 10:26
 */
@Service
public class RedisIncrService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RedisUtils redisUtils;

    public MqResourceDTO incrementValue(List<MqResourceDTO> list, String noniusName, Date pexpireAt) {
        if (list != null && list.size() > 0) {
            //只有一条直接返回
            if (list.size() == 1) {
                return list.get(0);
            }
            //redis incr获得游标,key不存在时返回1，所以第一次运行会跳过第一个人员，直接分配给第二个人员，以后获得累加1的返回
            Long nonius = redisUtils.increment(noniusName, 1L, pexpireAt);
            //用户数量
            int listSize = list.size();
            //最大的技能值，代表第几轮
            int maxValue = list.get(0).getValue();
            //周期
            int cycle = 0;
            //遍历出相同技能值的数量,存入Map, key：技能值-1；value：数量
            TreeMap<Integer, Integer> valueMap = new TreeMap<>();
            int tempValue = maxValue;
            int valueCount = 0;
            for (MqResourceDTO aList : list) {
                //不相等开始计算下一个
                if (tempValue != aList.getValue()) {
                    valueMap.put(tempValue, valueCount);
                    tempValue = aList.getValue();
                    valueCount = 0;
                }
                valueCount++;
                if (tempValue == 1) {
                    valueMap.put(tempValue, valueCount);
                }
                //计算周期
                cycle += aList.getValue();
            }

            //模才其实是真正使用的游标
            Long modulo = nonius % cycle;
            logger.debug("获得游标：{},游标取模：{}，list长度：{}，周期：{}", nonius, modulo, listSize, cycle);
            //第一轮直接返回
            if (modulo < listSize) {
                return list.get(modulo.intValue());
            }
            //记录一次回合完成后资源的下标
            int[] roundAttr = new int[maxValue];
            //记录当前资源下标，跳过第一轮，直接设置为list长度-1
            int count = listSize;
            //跳过第一轮，直接设置第一轮为 count
            roundAttr[0] = count;
            //从第二回合开始遍历所有回合，回合次数为最大技能值
            for (int i = 1; i < maxValue; i++) {
                //倒叙遍历技能值，因为从技能值最大开始分配
                for (int j = maxValue; j > 0; j--) {
                    if (j >= i + 1 && valueMap.get(j) != null) {
                        roundAttr[i] += valueMap.get(j);
                        count += valueMap.get(j);
                        if (modulo < count) {
                            return list.get(modulo.intValue() - roundAttr[i - 1]);
                        }
                    }
                }
                roundAttr[i] = (count);
            }
        }
        return null;
    }
}
