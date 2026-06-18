package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCheckpointNudging
{
    @Test
    public void testCheckpointNudging1()
    {
        Router router = new Router(Router.RouterFlag.PolyLineRouting.flag | Router.RouterFlag.OrthogonalRouting.flag);
        router.setRoutingParameter(Router.RoutingParameter.segmentPenalty, 50);
        router.setRoutingParameter(Router.RoutingParameter.anglePenalty, 0);
        router.setRoutingParameter(Router.RoutingParameter.crossingPenalty, 0);
        router.setRoutingParameter(Router.RoutingParameter.clusterCrossingPenalty, 4000);
        router.setRoutingParameter(Router.RoutingParameter.fixedSharedPathPenalty, 0);
        router.setRoutingParameter(Router.RoutingParameter.portDirectionPenalty, 100);
        router.setRoutingOption(Router.RoutingOption.nudgeOrthogonalSegmentsConnectedToShapes, true);
        router.setRoutingOption(Router.RoutingOption.improveHyperedgeRoutesMovingJunctions, true);
        router.setRoutingOption(Router.RoutingOption.penaliseOrthogonalSharedPathsAtConnEnds, false);

        Polygon poly75 = new Polygon(4);
        poly75.setPoint(0, new Point(715.439, 1924.42));
        poly75.setPoint(1, new Point(715.439, 1944.42));
        poly75.setPoint(2, new Point(683.439, 1944.42));
        poly75.setPoint(3, new Point(683.439, 1924.42));
        new ShapeRef(router, poly75, 75);

        Polygon poly76 = new Polygon(4);
        poly76.setPoint(0, new Point(282.5, 1872.42));
        poly76.setPoint(1, new Point(282.5, 1934.42));
        poly76.setPoint(2, new Point(220.5, 1934.42));
        poly76.setPoint(3, new Point(220.5, 1872.42));
        new ShapeRef(router, poly76, 76);

        Polygon poly78 = new Polygon(4);
        poly78.setPoint(0, new Point(715.439, 1862.42));
        poly78.setPoint(1, new Point(715.439, 1882.42));
        poly78.setPoint(2, new Point(683.439, 1882.42));
        poly78.setPoint(3, new Point(683.439, 1862.42));
        new ShapeRef(router, poly78, 78);

        Polygon poly136 = new Polygon(4);
        poly136.setPoint(0, new Point(727.439, 1944.42));
        poly136.setPoint(1, new Point(727.439, 2004.42));
        poly136.setPoint(2, new Point(671.439, 2004.42));
        poly136.setPoint(3, new Point(671.439, 1944.42));
        new ShapeRef(router, poly136, 136);

        Polygon poly138 = new Polygon(4);
        poly138.setPoint(0, new Point(727.439, 1802.42));
        poly138.setPoint(1, new Point(727.439, 1862.42));
        poly138.setPoint(2, new Point(671.439, 1862.42));
        poly138.setPoint(3, new Point(671.439, 1802.42));
        new ShapeRef(router, poly138, 138);

        ConnRef connRef250 = new ConnRef(router, 250);
        ConnEnd srcPt250 = new ConnEnd(new Point(251.5, 1903.42), 15);
        connRef250.setSourceEndpoint(srcPt250);
        ConnEnd dstPt250 = new ConnEnd(new Point(684.439, 1934.42), 4);
        connRef250.setDestEndpoint(dstPt250);
        connRef250.setRoutingType(ConnType.Orthogonal);
        List<Checkpoint> checkpoints250 = new ArrayList<>();
        checkpoints250.add(new Checkpoint(new Point(661.439, 1903.42)));
        connRef250.setRoutingCheckpoints(checkpoints250);

        ConnRef connRef251 = new ConnRef(router, 251);
        ConnEnd srcPt251 = new ConnEnd(new Point(251.5, 1903.42), 15);
        connRef251.setSourceEndpoint(srcPt251);
        ConnEnd dstPt251 = new ConnEnd(new Point(684.439, 1872.42), 4);
        connRef251.setDestEndpoint(dstPt251);
        connRef251.setRoutingType(ConnType.Orthogonal);
        List<Checkpoint> checkpoints251 = new ArrayList<>();
        checkpoints251.add(new Checkpoint(new Point(661.439, 1903.42)));
        connRef251.setRoutingCheckpoints(checkpoints251);

        router.processTransaction();
        router.outputDiagram("output/checkpointNudging1");
        boolean atEnds = true;
        boolean overlap = router.existsOrthogonalFixedSegmentOverlap(atEnds);
        final var retCode = overlap ? 1 : 0;
        assertEquals(0, retCode);
    }
}
