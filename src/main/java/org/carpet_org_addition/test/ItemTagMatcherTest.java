package org.carpet_org_addition.test;

import com.ibm.icu.impl.Assert;
import org.carpet_org_addition.util.matcher.ItemTagMatcher;
import org.junit.jupiter.api.Test;

public class ItemTagMatcherTest {

    @Test
    public void test1() {
        ItemTagMatcher matcher = new ItemTagMatcher("tag");
        System.out.println(matcher);
        Assert.assrt("#minecraft:tag".equals(matcher.toString()));
    }

    @Test
    public void test2() {
        ItemTagMatcher matcher = new ItemTagMatcher("#tag");
        System.out.println(matcher);
        Assert.assrt("#minecraft:tag".equals(matcher.toString()));
    }

    @Test
    public void test3() {
        ItemTagMatcher matcher = new ItemTagMatcher("#minecraft:tag");
        System.out.println(matcher);
        Assert.assrt("#minecraft:tag".equals(matcher.toString()));
    }
}
