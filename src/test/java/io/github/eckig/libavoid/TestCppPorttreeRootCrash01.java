package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

public class TestCppPorttreeRootCrash01
{
    @Test
    public void testtreeRootCrash01()
    {
	ConnEnd end1_0 = new ConnEnd();
	ConnEnd end2_0 = new ConnEnd();
	Polygon poly0 = new Polygon();
	Router router0 = new Router(Router.RouterFlag.OrthogonalRouting);
	HyperedgeNewAndDeletedObjectLists newanddeletedobjects_0 = null;
	router0.setTransactionUse(true);
	router0.setRoutingPenalty(Router.RoutingParameter.segmentPenalty);
	router0.setRoutingParameter(Router.RoutingParameter.portDirectionPenalty, 100);
	router0.setRoutingParameter(Router.RoutingParameter.idealNudgingDistance, 25);
	router0.setRoutingOption(Router.RoutingOption.improveHyperedgeRoutesMovingAddingAndDeletingJunctions, true);
	router0.setRoutingPenalty(Router.RoutingParameter.fixedSharedPathPenalty, 9000);
	router0.setRoutingOption(Router.RoutingOption.penaliseOrthogonalSharedPathsAtConnEnds, true);
	router0.setRoutingParameter(Router.RoutingParameter.crossingPenalty);
	router0.setRoutingOption(Router.RoutingOption.performUnifyingNudgingPreprocessingStep, true);
	router0.setRoutingPenalty(Router.RoutingParameter.fixedSharedPathPenalty, 9000);
	router0.setRoutingOption(Router.RoutingOption.penaliseOrthogonalSharedPathsAtConnEnds, true);
	router0.setRoutingParameter(Router.RoutingParameter.crossingPenalty);
	router0.setRoutingOption(Router.RoutingOption.performUnifyingNudgingPreprocessingStep, true);
	poly0 = new Polygon(4);
	poly0.setPoint(0, new Point(50410, 49965));
	poly0.setPoint(1, new Point(50410, 50710));
	poly0.setPoint(2, new Point(49975, 50710));
	poly0.setPoint(3, new Point(49975, 49965));
	ShapeRef shape0_1023802233 = new ShapeRef(router0, poly0, 1023802233);
	ShapeConnectionPin pin0_1023802233_2 = new ShapeConnectionPin(shape0_1023802233, 3, 0, 560, false, 10, 4);
	pin0_1023802233_2.setExclusive(true);
	ShapeConnectionPin pin0_1023802233_12 = new ShapeConnectionPin(shape0_1023802233, 13, 0, 635, false, 10, 4);
	pin0_1023802233_12.setExclusive(true);
	ShapeConnectionPin pin0_1023802233_19 = new ShapeConnectionPin(shape0_1023802233, 20, 0, 485, false, 10, 4);
	pin0_1023802233_19.setExclusive(true);
	poly0 = new Polygon(4);
	poly0.setPoint(0, new Point(49504, 50775));
	poly0.setPoint(1, new Point(49504, 50860));
	poly0.setPoint(2, new Point(49446, 50860));
	poly0.setPoint(3, new Point(49446, 50775));
	ShapeRef shape0_368159034 = new ShapeRef(router0, poly0, 368159034);
	ShapeConnectionPin pin0_368159034_0 = new ShapeConnectionPin(shape0_368159034, 1, 29, 0, false, 10, 1);
	pin0_368159034_0.setExclusive(true);
	JunctionRef junction0_526491597 = new JunctionRef(router0, new Point(49600, 50525), 526491597);
	junction0_526491597.setPositionFixed(false);
	JunctionRef junction0_30828132 = new JunctionRef(router0, new Point(49600, 50650), 30828132);
	junction0_30828132.setPositionFixed(false);
	end1_0 = new ConnEnd(shape0_1023802233, 20);
	end2_0 = new ConnEnd(junction0_526491597);
	ConnRef conn0_117136925 = new ConnRef(router0, end1_0, end2_0, 117136925);
	poly0 = new Polygon(3);
	poly0.setPoint(0, new Point(49975, 50450));
	poly0.setPoint(1, new Point(49600, 50450));
	poly0.setPoint(2, new Point(49600, 50525));
	conn0_117136925.setFixedRoute(poly0);
	conn0_117136925.setSourceEndpoint(end1_0);
	conn0_117136925.setDestEndpoint(end2_0);
	end1_0 = new ConnEnd(shape0_1023802233, 3);
	end2_0 = new ConnEnd(junction0_526491597);
	ConnRef conn0_224626788 = new ConnRef(router0, end1_0, end2_0, 224626788);
	end1_0 = new ConnEnd(new Point(49600, 50525), 15);
	end2_0 = new ConnEnd(junction0_30828132);
	ConnRef conn0_446423927 = new ConnRef(router0, end1_0, end2_0, 446423927);
	poly0 = new Polygon(3);
	poly0.setPoint(0, new Point(49600, 50525));
	poly0.setPoint(1, new Point(49600, 50587));
	poly0.setPoint(2, new Point(49600, 50650));
	conn0_446423927.setFixedRoute(poly0);
	conn0_446423927.setSourceEndpoint(end1_0);
	conn0_446423927.setDestEndpoint(end2_0);
	end1_0 = new ConnEnd(shape0_1023802233, 13);
	end2_0 = new ConnEnd(new Point(49600, 50650), 15);
	ConnRef conn0_11189244 = new ConnRef(router0, end1_0, end2_0, 11189244);
	poly0 = new Polygon(3);
	poly0.setPoint(0, new Point(49975, 50600));
	poly0.setPoint(1, new Point(49600, 50600));
	poly0.setPoint(2, new Point(49600, 50650));
	conn0_11189244.setFixedRoute(poly0);
	conn0_11189244.setSourceEndpoint(end1_0);
	conn0_11189244.setDestEndpoint(end2_0);
	end1_0 = new ConnEnd(shape0_368159034, 1);
	end2_0 = new ConnEnd(junction0_30828132);
	ConnRef conn0_280909026 = new ConnRef(router0, end1_0, end2_0, 280909026);
	router0.processTransaction();
	newanddeletedobjects_0 = router0.newAndDeletedObjectListsFromHyperedgeImprovement();
	router0.outputDiagram("output/treeRootCrash01-1");
	conn0_117136925.clearFixedRoute();
	conn0_224626788.makePathInvalid();
	conn0_446423927.clearFixedRoute();
	conn0_11189244.clearFixedRoute();
	conn0_280909026.makePathInvalid();
	junction0_526491597.setPositionFixed(false);
	junction0_30828132.setPositionFixed(false);
	router0.processTransaction();
	newanddeletedobjects_0 = router0.newAndDeletedObjectListsFromHyperedgeImprovement();
	router0.outputDiagram("output/treeRootCrash01-2");
	router0.processTransaction();
	newanddeletedobjects_0 = router0.newAndDeletedObjectListsFromHyperedgeImprovement();
	router0.outputDiagram("output/treeRootCrash01-3");
	router0.hyperedgeRerouter().registerHyperedgeForRerouting(junction0_526491597);
	router0.processTransaction();
	router0.outputDiagram("output/treeRootCrash01-4");
    }

    private static void resizePolygon(Polygon polygon, int size)
    {
        polygon.ps.clear();
        while (polygon.ps.size() < size) {
            polygon.ps.add(new Point());
        }
    }
}
