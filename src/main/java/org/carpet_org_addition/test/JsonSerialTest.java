package org.carpet_org_addition.test;

import net.minecraft.util.math.Vec3d;
import org.carpet_org_addition.util.fakeplayer.actiondata.SortingData;
import org.junit.jupiter.api.Test;

public class JsonSerialTest {
    @Test
    public void test() {
        SortingData sortingData = new SortingData(null, new Vec3d(0, 0, 0), new Vec3d(1, 1, 1));
        System.out.println(sortingData.toJson());
    }
}
