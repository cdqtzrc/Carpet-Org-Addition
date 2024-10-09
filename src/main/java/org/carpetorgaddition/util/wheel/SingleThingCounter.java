package org.carpetorgaddition.util.wheel;

/**
 * 统计单一种类的事物数量的计数器
 */
public class SingleThingCounter {
    private int count = 0;

    public SingleThingCounter() {
    }

    /**
     * 计数器递增
     */
    public void add() {
        this.add(1);
    }

    /**
     * 计数器递减
     */
    public void decrement() {
        this.add(-1);
    }

    /**
     * 将计数器增加指定值
     *
     * @param number 要增加的数量
     */
    public void add(int number) {
        this.count += number;
    }

    /**
     * 获取计算器的值
     */
    public int get() {
        return count;
    }

    /**
     * 设置计数器当前的数量
     *
     * @param count 要设置的值
     */
    public void set(int count) {
        this.count = count;
    }

    /**
     * 判断计数器是否已经归零
     */
    public boolean isZero() {
        return this.count == 0;
    }

    /**
     * 判断计数器是否没有归零
     */
    public boolean nonZero() {
        return this.count != 0;
    }
}
