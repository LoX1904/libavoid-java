package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestImproveHyperEdge
{
    @Test
    public void testImproveHyperEdge01()
    {
        // The contents of this function can be replaced by the automatically generated test code from the debug svg file
        ConnEnd end1;
        ConnEnd end2;
        Polygon poly;
        Router router = new Router(Router.RouterFlag.OrthogonalRouting);
        router.setTransactionUse(true);
        router.setRoutingPenalty(Router.RoutingParameter.segmentPenalty);
        router.setRoutingPenalty(Router.RoutingParameter.crossingPenalty);
        router.setRoutingPenalty(Router.RoutingParameter.fixedSharedPathPenalty);
        router.setRoutingParameter(Router.RoutingParameter.idealNudgingDistance, 25);
        router.setRoutingOption(Router.RoutingOption.improveHyperedgeRoutesMovingJunctions, true);
        poly = new Polygon(4);
        poly.setPoint(0, new Point(51160, 50670));
        poly.setPoint(1, new Point(51160, 50730));
        poly.setPoint(2, new Point(50950, 50730));
        poly.setPoint(3, new Point(50950, 50670));
        ShapeRef shape341041314 = new ShapeRef(router, poly, 341041314);
        ShapeConnectionPin pin341041314_0 = new ShapeConnectionPin(shape341041314, 1, 0.952381, 0.5, true, 10, 8);
        pin341041314_0.setExclusive(true);
        ShapeConnectionPin pin341041314_1 = new ShapeConnectionPin(shape341041314, 2, 0, 0.5, true, 10, 4);
        pin341041314_1.setExclusive(true);
        poly = new Polygon(4);
        poly.setPoint(0, new Point(51160, 50395));
        poly.setPoint(1, new Point(51160, 50455));
        poly.setPoint(2, new Point(50950, 50455));
        poly.setPoint(3, new Point(50950, 50395));
        ShapeRef shape47540376 = new ShapeRef(router, poly, 47540376);
        ShapeConnectionPin pin47540376_0 = new ShapeConnectionPin(shape47540376, 1, 0.952381, 0.5, true, 10, 8);
        pin47540376_0.setExclusive(true);
        ShapeConnectionPin pin47540376_1 = new ShapeConnectionPin(shape47540376, 2, 0, 0.5, true, 10, 4);
        pin47540376_1.setExclusive(true);
        poly = new Polygon(4);
        poly.setPoint(0, new Point(50850, 50770));
        poly.setPoint(1, new Point(50850, 50830));
        poly.setPoint(2, new Point(50665, 50830));
        poly.setPoint(3, new Point(50665, 50770));
        ShapeRef shape81920772 = new ShapeRef(router, poly, 81920772);
        ShapeConnectionPin pin81920772_0 = new ShapeConnectionPin(shape81920772, 1, 0.047619, 0.5, true, 10, 4);
        pin81920772_0.setExclusive(true);
        ShapeConnectionPin pin81920772_1 = new ShapeConnectionPin(shape81920772, 2, 1, 0.5, true, 10, 8);
        pin81920772_1.setExclusive(true);
        JunctionRef junction878282342 = new JunctionRef(router, new Point(50875, 50700), 878282342);
        junction878282342.setPositionFixed(false);
        poly = new Polygon(4);
        poly.setPoint(0, new Point(50850, 50470));
        poly.setPoint(1, new Point(50850, 50530));
        poly.setPoint(2, new Point(50665, 50530));
        poly.setPoint(3, new Point(50665, 50470));
        ShapeRef shape206564314 = new ShapeRef(router, poly, 206564314);
        ShapeConnectionPin pin206564314_0 = new ShapeConnectionPin(shape206564314, 1, 0.047619, 0.5, true, 10, 4);
        pin206564314_0.setExclusive(true);
        ShapeConnectionPin pin206564314_1 = new ShapeConnectionPin(shape206564314, 2, 1, 0.5, true, 10, 8);
        pin206564314_1.setExclusive(true);
        JunctionRef junction213455341 = new JunctionRef(router, new Point(50875, 50500), 213455341);
        junction213455341.setPositionFixed(false);
        end1 = new ConnEnd(junction878282342);
        end2 = new ConnEnd(shape341041314, 2);
        ConnRef conn290033688 = new ConnRef(router, end1, end2);
        conn290033688.makePathInvalid();
        end1 = new ConnEnd(shape81920772, 2);
        end2 = new ConnEnd(junction878282342);
        ConnRef conn281807178 = new ConnRef(router, end1, end2);
        conn281807178.makePathInvalid();
        end1 = new ConnEnd(shape47540376, 2);
        end2 = new ConnEnd(junction213455341);
        ConnRef conn762966660 = new ConnRef(router, end1, end2);
        conn762966660.makePathInvalid();
        end1 = new ConnEnd(junction213455341);
        end2 = new ConnEnd(junction878282342);
        ConnRef conn46508714 = new ConnRef(router, end1, end2);
        conn46508714.makePathInvalid();
        end1 = new ConnEnd(shape206564314, 2);
        end2 = new ConnEnd(junction213455341);
        ConnRef conn385831514 = new ConnRef(router, end1, end2);
        conn385831514.makePathInvalid();
        router.processTransaction();

        router.outputDiagram("output/improveHyperedge01-1");
        poly = new Polygon(4);
        poly.setPoint(0, new Point(51160, 50645));
        poly.setPoint(1, new Point(51160, 50705));
        poly.setPoint(2, new Point(50950, 50705));
        poly.setPoint(3, new Point(50950, 50645));
        router.moveShape(shape341041314, poly);
        conn290033688.makePathInvalid();
        conn281807178.makePathInvalid();
        conn46508714.makePathInvalid();
        conn762966660.makePathInvalid();
        router.processTransaction();

        router.outputDiagram("output/improveHyperedge01-2");
        Point junctionPos = junction878282342.recommendedPosition();

        // Regression test to check that a junction is correctly moved.
        assertEquals(50675, junctionPos.y);
    }

    @Test
    public void testImproveHyperEdge02()
    {
        Router router = new Router(Router.RouterFlag.PolyLineRouting.flag | Router.RouterFlag.OrthogonalRouting.flag);
        router.setRoutingPenalty(Router.RoutingParameter.segmentPenalty, 50);
        router.setRoutingPenalty(Router.RoutingParameter.anglePenalty, 0);
        router.setRoutingPenalty(Router.RoutingParameter.crossingPenalty, 200);
        router.setRoutingPenalty(Router.RoutingParameter.clusterCrossingPenalty, 4000);
        router.setRoutingPenalty(Router.RoutingParameter.fixedSharedPathPenalty, 9000);
        router.setRoutingPenalty(Router.RoutingParameter.portDirectionPenalty, 100);
        router.setRoutingOption(Router.RoutingOption.nudgeOrthogonalSegmentsConnectedToShapes, false);
        router.setRoutingOption(Router.RoutingOption.improveHyperedgeRoutesMovingJunctions, true);
        router.setRoutingOption(Router.RoutingOption.penaliseOrthogonalSharedPathsAtConnEnds, true);
        router.setRoutingOption(Router.RoutingOption.improveHyperedgeRoutesMovingAddingAndDeletingJunctions, true);
        router.setRoutingParameter(Router.RoutingParameter.idealNudgingDistance, 25);

        Polygon poly39761567=new Polygon(4);
        poly39761567.setPoint(0, new Point(50754, 50975));
        poly39761567.setPoint(1, new Point(50754, 51060));
        poly39761567.setPoint(2, new Point(50696, 51060));
        poly39761567.setPoint(3, new Point(50696, 50975));
        ShapeRef shapeRef39761567 = new ShapeRef(router, poly39761567, 39761567);
        new ShapeConnectionPin(shapeRef39761567, 1, 0.5, 0, true, 10, ConnDirFlag.ConnDirUp);

        Polygon poly115776716=new Polygon(4);
        poly115776716.setPoint(0, new Point(51504, 50975));
        poly115776716.setPoint(1, new Point(51504, 51060));
        poly115776716.setPoint(2, new Point(51446, 51060));
        poly115776716.setPoint(3, new Point(51446, 50975));
        ShapeRef shapeRef115776716 = new ShapeRef(router, poly115776716, 115776716);
        new ShapeConnectionPin(shapeRef115776716, 1, 0.5, 0, true, 10, ConnDirFlag.ConnDirUp);

        Polygon poly238475293=new Polygon(4);
        poly238475293.setPoint(0, new Point(50554, 50975));
        poly238475293.setPoint(1, new Point(50554, 51060));
        poly238475293.setPoint(2, new Point(50496, 51060));
        poly238475293.setPoint(3, new Point(50496, 50975));
        ShapeRef shapeRef238475293 = new ShapeRef(router, poly238475293, 238475293);
        new ShapeConnectionPin(shapeRef238475293, 1, 0.5, 0, true, 10, ConnDirFlag.ConnDirUp);

        Polygon poly430971008=new Polygon(4);
        poly430971008.setPoint(0, new Point(51004, 50465));
        poly430971008.setPoint(1, new Point(51004, 50550));
        poly430971008.setPoint(2, new Point(50946, 50550));
        poly430971008.setPoint(3, new Point(50946, 50465));
        ShapeRef shapeRef430971008 = new ShapeRef(router, poly430971008, 430971008);
        new ShapeConnectionPin(shapeRef430971008, 1, 0.5, 1, true, 10, ConnDirFlag.ConnDirDown);

        JunctionRef junctionRef513246008 = new JunctionRef(router, new Point(50925, 50725), 513246008);
        // This may be useful if junction pins are modified.
        new ShapeConnectionPin(junctionRef513246008, 2147483646, ConnDirFlag.ConnDirAll);

        ConnRef connRef513246009 = new ConnRef(router, 513246009);
        ConnEnd srcPt513246009=new ConnEnd(shapeRef39761567, 1);
        connRef513246009.setSourceEndpoint(srcPt513246009);
        ConnEnd dstPt513246009=new ConnEnd(junctionRef513246008);
        connRef513246009.setDestEndpoint(dstPt513246009);
        connRef513246009.setRoutingType(ConnType.Orthogonal);

        ConnRef connRef513246010 = new ConnRef(router, 513246010);
        ConnEnd srcPt513246010=new ConnEnd(junctionRef513246008);
        connRef513246010.setSourceEndpoint(srcPt513246010);
        ConnEnd dstPt513246010=new ConnEnd(shapeRef430971008, 1);
        connRef513246010.setDestEndpoint(dstPt513246010);
        connRef513246010.setRoutingType(ConnType.Orthogonal);

        ConnRef connRef513246011 = new ConnRef(router, 513246011);
        ConnEnd srcPt513246011=new ConnEnd(junctionRef513246008);
        connRef513246011.setSourceEndpoint(srcPt513246011);
        ConnEnd dstPt513246011=new ConnEnd(shapeRef238475293, 1);
        connRef513246011.setDestEndpoint(dstPt513246011);
        connRef513246011.setRoutingType(ConnType.Orthogonal);

        ConnRef connRef513246012 = new ConnRef(router, 513246012);
        ConnEnd srcPt513246012=new ConnEnd(shapeRef115776716, 1);
        connRef513246012.setSourceEndpoint(srcPt513246012);
        ConnEnd dstPt513246012=new ConnEnd(junctionRef513246008);
        connRef513246012.setDestEndpoint(dstPt513246012);
        connRef513246012.setRoutingType(ConnType.Orthogonal);

        router.processTransaction();
        router.outputDiagram("output/improveHyperedge02");

        boolean overlaps = router.existsOrthogonalSegmentOverlap(true);
        assertFalse(overlaps);
    }
}
