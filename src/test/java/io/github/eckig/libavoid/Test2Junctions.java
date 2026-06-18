package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Test2Junctions
{

    @Test
    public void test() {
        ConnEnd end1;
        ConnEnd end2;
        Polygon poly;
        Router router1 = new Router(Router.RouterFlag.OrthogonalRouting);
        router1.setTransactionUse(true);
        router1.setRoutingPenalty(Router.RoutingParameter.segmentPenalty);
        router1.setRoutingPenalty(Router.RoutingParameter.crossingPenalty);
        router1.setRoutingPenalty(Router.RoutingParameter.fixedSharedPathPenalty);
        router1.setRoutingOption(Router.RoutingOption.penaliseOrthogonalSharedPathsAtConnEnds, true);
        router1.setRoutingParameter(Router.RoutingParameter.idealNudgingDistance, 25);

        poly = new Polygon(4);
        poly.setPoint(0, new Point(51410, 50640));
        poly.setPoint(1, new Point(51410, 50960));
        poly.setPoint(2, new Point(51050, 50960));
        poly.setPoint(3, new Point(51050, 50640));
        ShapeRef shape365249936 = new ShapeRef(router1, poly, 365249936);

        ShapeConnectionPin pin365249936_2 = new ShapeConnectionPin(shape365249936, 3, 0, 0.5, true, 10, 4);
        pin365249936_2.setExclusive(true);

        ShapeConnectionPin pin365249936_4 = new ShapeConnectionPin(shape365249936, 5, 0, 0.734375, true, 10, 4);
        pin365249936_4.setExclusive(true);

        poly = new Polygon(4);
        poly.setPoint(0, new Point(50760, 50691));
        poly.setPoint(1, new Point(50760, 50775));
        poly.setPoint(2, new Point(50640, 50775));
        poly.setPoint(3, new Point(50640, 50691));
        ShapeRef shape386142480 = new ShapeRef(router1, poly, 386142480);

        ShapeConnectionPin pin386142480_1 = new ShapeConnectionPin(shape386142480, 2, 0.5, 1, true, 10, 2);
        pin386142480_1.setExclusive(true);

        JunctionRef junction328922310 = new JunctionRef(router1, new Point(50700, 50800), 328922310);
        junction328922310.setPositionFixed(true);

        end1 = new ConnEnd(shape365249936, 3);
        end2 = new ConnEnd(junction328922310);
        new ConnRef(router1, end1, end2);

        end1 = new ConnEnd(junction328922310);
        end2 = new ConnEnd(shape386142480, 2);
        new ConnRef(router1, end1, end2);

        end1 = new ConnEnd(shape365249936, 5);
        end2 = new ConnEnd(junction328922310);
        ConnRef conn18168360 = new ConnRef(router1, end1, end2);

        router1.processTransaction();
        router1.outputDiagram("output/2junctions-1");

        poly = new Polygon(4);
        poly.setPoint(0, new Point(50879, 50885));
        poly.setPoint(1, new Point(50879, 50960));
        poly.setPoint(2, new Point(50821, 50960));
        poly.setPoint(3, new Point(50821, 50885));
        ShapeRef shape150429385 = new ShapeRef(router1, poly, 150429385);

        ShapeConnectionPin pin150429385_0 = new ShapeConnectionPin(shape150429385, 1, 0.5, 0, true, 10, 1);
        pin150429385_0.setExclusive(true);

        JunctionRef junction550292508 = new JunctionRef(router1, new Point(50850, 50875), 550292508);

        router1.deleteConnector(conn18168360);
        conn18168360 = null;

        end1 = new ConnEnd(shape365249936, 5);
        end2 = new ConnEnd(junction550292508);
        ConnRef conn795337150 = new ConnRef(router1, end1, end2);
        conn795337150.makePathInvalid();

        end1 = new ConnEnd(junction550292508);
        end2 = new ConnEnd(junction328922310);
        ConnRef conn151961380 = new ConnRef(router1, end1, end2);
        conn151961380.makePathInvalid();

        end1 = new ConnEnd(shape150429385, 1);
        end2 = new ConnEnd(junction550292508);
        ConnRef conn149180423 = new ConnRef(router1, end1, end2);
        conn149180423.makePathInvalid();

        router1.processTransaction();
        router1.outputDiagram("output/2junctions-2");

        boolean atEnds = true;
        boolean overlap = router1.existsOrthogonalFixedSegmentOverlap(atEnds);
//        return overlap ? 1 : 0;
        final var retCode = overlap ? 1 : 0;
        assertEquals(0, retCode);
    }
}