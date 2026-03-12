package com.adamk33n3r.runelite.watchdog;

import net.runelite.api.WorldView;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class RegionTest extends TestBase {
    @InjectMocks
    EventHandler eventHandler;

    @Test
    public void testCoxBanned() {
        var mockWorldView = Mockito.mock(WorldView.class);
        Mockito.when(mockWorldView.isInstance()).thenReturn(false);
        Mockito.when(mockWorldView.getPlane()).thenReturn(0);

//        var regions = Arrays.stream(Region.values())
//            .filter(r -> mockWorldView.isInstance() || !r.config.onlyInInstance)
//            .filter(r -> r.config.planes.isEmpty() || r.config.planes.contains(mockWorldView.getPlane()))
//            .flatMap(r -> r.config.regionIDs.stream())
//            .collect(Collectors.toList());
//        for (var region : regions) {
//            System.out.println(region);
//        }
        var res = Region.isBannedRegion(12889, mockWorldView);
        Assert.assertTrue(res);
    }
}
