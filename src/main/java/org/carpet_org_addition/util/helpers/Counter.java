package org.carpet_org_addition.util.helpers;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 虽然在大多数情况下使用int count = 0; count++;的方式能够解决统计数量的问题，但是这种方式有局限性，即一次只能统计一种事物的数量，当要同时统计多种事物的数量，或者根本就不知道有多少种事物要被统计时使用这种方式就非常不方便，举个例子，如果需要统计当前Minecraft世界中上每一种生物的数量，我们并不知道当前世界上一共有多少种生物要被统计，也就无法知道要到底定义几个计数器，这时就可以使用本类的计数器，它实际上包装了一个HashMap集合，利用向集合中添加元素时，如果元素是相同的，元素不会重复添加而是覆盖的特性来统计事物的数量。另外，本类实现了Iterable接口，因此，本类可以使用迭代器和增强for循环遍历
 *
 * @param <E> 计数器要统计数量的类
 */
public class Counter<E> implements Iterable<E> {
    private final HashMap<E, Integer> COUNTER;

    public Counter() {
        this.COUNTER = new HashMap<>();
    }

    public void add(E element) {
        add(element, 1);
    }

    public void add(E element, int count) {
        Integer i = this.COUNTER.get(element);
        this.COUNTER.put(element, i == null ? count : i + count);
    }

    public int getCount(E element) {
        Integer i = this.COUNTER.get(element);
        return i == null ? 0 : i;
    }

    public int size() {
        return COUNTER.size();
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private final Iterator<Map.Entry<E, Integer>> iterator = COUNTER.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return iterator.next().getKey();
            }
        };
    }

    @Override
    public String toString() {
        return COUNTER.toString();
    }

    /**
     * 计数器是通过对象内的equals和hashCode方法来判断是否为相同的事物的，但是并不是所有对象equals都能满足当前的需要，
     * 因此可以使用本类包装并根据实际情况来重写这个内部类中的方法
     */
    @SuppressWarnings("unused")
    public abstract static class Wrapper<T> {
        private final T value;

        public Wrapper(T value) {
            this.value = value;
        }

        public final T getValue() {
            return this.value;
        }

        @Override
        public final boolean equals(Object obj) {
            if (obj instanceof Counter.Wrapper<?> wrapper) {
                return this.valueEquals(this.value, wrapper.value);
            }
            return false;
        }

        @Override
        public final int hashCode() {
            return this.valueHashCode(value);
        }

        /**
         * 比较两个包装器内的值是否相等
         *
         * @param value1 当前包装器内的值
         * @param value2 另一个包装器内的值，但它需要手动强制成对应的数据类型
         */
        public abstract boolean valueEquals(T value1, Object value2);

        /**
         * 获取当前包装器内值的哈希值，按照约定，只要重写了equals方法，都应该重写hashCode方法，相同的对象需要有相同的哈希值
         *
         * @param value 当前包装器内的值
         */
        public abstract int valueHashCode(T value);

        @Override
        public String toString() {
            return value.toString();
        }
    }
}
