package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCppPortcheckpoints
{
    @Test
    public void testcheckpoints01()
    {
    Router router = new Router(Router.RouterFlag.OrthogonalRouting);
    router.setRoutingPenalty(0, 50);
    router.setRoutingPenalty(1, 0);
    router.setRoutingPenalty(2, 0);
    router.setRoutingPenalty(3, 4000);
    router.setRoutingPenalty(4, 105);
    router.setRoutingParameter(Router.RoutingParameter.idealNudgingDistance, 25);
    Rectangle rect478845150 = new Rectangle(new Point(50695, 51070), new Point(50705, 51080));
    JunctionRef junctionRef478845150 = new JunctionRef(router, 
		    new Point(50700, 51075), 478845150);
    Rectangle rect92712048 = new Rectangle(new Point(51246, 50475), new Point(51304, 50585));
    new ShapeRef(router, rect92712048, 92712048);
    Rectangle rect23127921 = new Rectangle(new Point(50365, 50766), new Point(50485, 50834));
    new ShapeRef(router, rect23127921, 23127921);
    Rectangle rect159957148 = new Rectangle(new Point(51740, 50891), new Point(51860, 50959));
    new ShapeRef(router, rect159957148, 159957148);
    Rectangle rect1350214 = new Rectangle(new Point(50375, 51015), new Point(50625, 51135));
    new ShapeRef(router, rect1350214, 1350214);
    Rectangle rect247197258 = new Rectangle(new Point(50265, 51046), new Point(50375, 51104));
    new ShapeRef(router, rect247197258, 247197258);
    Rectangle rect281096654 = new Rectangle(new Point(50700, 50390), new Point(51000, 50610));
    new ShapeRef(router, rect281096654, 281096654);
    ConnEnd srcPt342420237 = new ConnEnd(new Point(50425, 50800), 15);
    ConnEnd dstPt342420237 = new ConnEnd(new Point(50710, 50450), 4);
    new ConnRef(router, srcPt342420237, dstPt342420237, 342420237);
    ConnEnd srcPt352019675 = new ConnEnd(new Point(50990, 50500), 8);
    ConnEnd dstPt352019675 = new ConnEnd(junctionRef478845150);
    new ConnRef(router, srcPt352019675, dstPt352019675, 352019675);
    ConnEnd srcPt42699400 = new ConnEnd(junctionRef478845150);
    ConnEnd dstPt42699400 = new ConnEnd(new Point(50615, 51075), 8);
    new ConnRef(router, srcPt42699400, dstPt42699400, 42699400);
    ConnEnd srcPt94712625 = new ConnEnd(new Point(50710, 50550), 4);
    ConnEnd dstPt94712625 = new ConnEnd(junctionRef478845150);
    new ConnRef(router, srcPt94712625, dstPt94712625, 94712625);
    ConnEnd srcPt92802970 = new ConnEnd(new Point(50990, 50450), 8);
    ConnEnd dstPt92802970 = new ConnEnd(new Point(51275, 50485), 1);
    ConnRef conn = new ConnRef(router, srcPt92802970, dstPt92802970, 92802970);
    List<Checkpoint> checkpoints = new ArrayList<>();
    checkpoints.add(new Checkpoint(new Point(50000, 50000)));
    checkpoints.add(new Checkpoint(new Point(50200, 50000)));
    checkpoints.add(new Checkpoint(new Point(50200, 50200)));
    conn.setRoutingCheckpoints(checkpoints);
    ConnEnd srcPt716502036 = new ConnEnd(new Point(50710, 50500), 4);
    ConnEnd dstPt716502036 = new ConnEnd(new Point(51800, 50925), 15);
    new ConnRef(router, srcPt716502036, dstPt716502036, 716502036);
    router.processTransaction();
    router.outputDiagram("output/checkpoints01-1");
    router.moveJunction(junctionRef478845150, 585, 0);
    router.processTransaction();
    router.outputDiagram("output/checkpoints01-2");
    }

    @Test
    public void testcheckpoints02()
    {
        Router router = new Router(Router.RouterFlag.OrthogonalRouting);
        router.setRoutingParameter(0, 50);
        router.setRoutingParameter(1, 0);
        router.setRoutingParameter(2, 0);
        router.setRoutingParameter(3, 4000);
        router.setRoutingParameter(4, 0);
        router.setRoutingParameter(5, 100);
        router.setRoutingParameter(6, 0);
        router.setRoutingParameter(7, 4);
        router.setRoutingOption(0, true);
        router.setRoutingOption(1, true);
        router.setRoutingOption(2, false);
        router.setRoutingOption(3, false);
        Polygon poly5 = new Polygon(4);
        poly5.setPoint(0, new Point(365.3425124847556, 152.0752314071785));
        poly5.setPoint(1, new Point(365.3425124847556, 214.0752314071785));
        poly5.setPoint(2, new Point(303.3425124847556, 214.0752314071785));
        poly5.setPoint(3, new Point(303.3425124847556, 152.0752314071785));
        new ShapeRef(router, poly5, 5);
        Polygon poly7 = new Polygon(4);
        poly7.setPoint(0, new Point(365.3425124847556, 24.07523140617849));
        poly7.setPoint(1, new Point(365.3425124847556, 86.07523140617849));
        poly7.setPoint(2, new Point(303.3425124847556, 86.07523140617849));
        poly7.setPoint(3, new Point(303.3425124847556, 24.07523140617849));
        new ShapeRef(router, poly7, 7);
        Polygon poly8 = new Polygon(4);
        poly8.setPoint(0, new Point(541.6758458190889, 24.07523140617849));
        poly8.setPoint(1, new Point(541.6758458190889, 44.07523140617849));
        poly8.setPoint(2, new Point(509.6758458190889, 44.07523140617849));
        poly8.setPoint(3, new Point(509.6758458190889, 24.07523140617849));
        new ShapeRef(router, poly8, 8);
        Polygon poly10 = new Polygon(4);
        poly10.setPoint(0, new Point(541.6758458190889, 66.07523140617849));
        poly10.setPoint(1, new Point(541.6758458190889, 86.07523140617849));
        poly10.setPoint(2, new Point(509.6758458190889, 86.07523140617849));
        poly10.setPoint(3, new Point(509.6758458190889, 66.07523140617849));
        new ShapeRef(router, poly10, 10);
        Polygon poly61 = new Polygon(4);
        poly61.setPoint(0, new Point(420.3425124847556, 101.0752314061785));
        poly61.setPoint(1, new Point(420.3425124847556, 141.0752314061785));
        poly61.setPoint(2, new Point(344.3425124847556, 141.0752314061785));
        poly61.setPoint(3, new Point(344.3425124847556, 101.0752314061785));
        new ShapeRef(router, poly61, 61);
        Polygon poly92 = new Polygon(4);
        poly92.setPoint(0, new Point(563.5758458190888, 34.07523140617849));
        poly92.setPoint(1, new Point(563.5758458190888, 76.07523140617849));
        poly92.setPoint(2, new Point(487.7758458190889, 76.07523140617849));
        poly92.setPoint(3, new Point(487.7758458190889, 34.07523140617849));
        new ShapeRef(router, poly92, 92);
        ConnRef connRef109 = new ConnRef(router, 109);
        ConnEnd srcPt109 = new ConnEnd(new Point(510.6758458190889, 34.07523140617849), 4);
        connRef109.setSourceEndpoint(srcPt109);
        ConnEnd dstPt109 = new ConnEnd(new Point(334.3425124847556, 183.0752314071785), 15);
        connRef109.setDestEndpoint(dstPt109);
        connRef109.setRoutingType(ConnType.Orthogonal);
        List<Checkpoint> checkpoints109 = new ArrayList<>();
        checkpoints109.add(new Checkpoint(new Point()));
        checkpoints109.set(0, new Checkpoint(new Point(487.6758458190889, 55.07523140617849), 15, 4));
        connRef109.setRoutingCheckpoints(checkpoints109);
        ConnRef connRef110 = new ConnRef(router, 110);
        ConnEnd srcPt110 = new ConnEnd(new Point(510.6758458190889, 76.07523140617849), 4);
        connRef110.setSourceEndpoint(srcPt110);
        ConnEnd dstPt110 = new ConnEnd(new Point(334.3425124847556, 183.0752314071785), 15);
        connRef110.setDestEndpoint(dstPt110);
        connRef110.setRoutingType(ConnType.Orthogonal);
        List<Checkpoint> checkpoints110 = new ArrayList<>();
        checkpoints110.add(new Checkpoint(new Point()));
        checkpoints110.set(0, new Checkpoint(new Point(487.6758458190889, 55.07523140617849), 15, 4));
        connRef110.setRoutingCheckpoints(checkpoints110);
        router.processTransaction();
        router.outputDiagram("output/checkpoints02");
        boolean succeeded = true;
        double checkpointY = 55.07523140617849;
        Polygon route109 = connRef109.displayRoute();
        if (Math.abs(route109.ps.get(route109.size() - 3).y - checkpointY) < 2)
        {
            succeeded = false;
        }
        Polygon route110 = connRef110.displayRoute();
        if (Math.abs(route110.ps.get(route110.size() - 3).y - checkpointY) < 2)
        {
            succeeded = false;
        }
        assertTrue(succeeded);
    }

    @Test
    public void testcheckpoints03()
    {
        Router router = new Router(
                Router.RouterFlag.PolyLineRouting.flag | Router.RouterFlag.OrthogonalRouting.flag);
        router.setRoutingParameter(0, 50);
        router.setRoutingParameter(1, 0);
        router.setRoutingParameter(2, 0);
        router.setRoutingParameter(3, 4000);
        router.setRoutingParameter(4, 0);
        router.setRoutingParameter(5, 100);
        router.setRoutingParameter(6, 0);
        router.setRoutingParameter(7, 4);
        router.setRoutingOption(0, true);
        router.setRoutingOption(1, true);
        router.setRoutingOption(2, false);
        router.setRoutingOption(3, false);
        Polygon poly41 = new Polygon(4);
        poly41.setPoint(0, new Point(918.6758458220888, 441.0752314081785));
        poly41.setPoint(1, new Point(918.6758458220888, 503.0752314081785));
        poly41.setPoint(2, new Point(856.6758458220888, 503.0752314081785));
        poly41.setPoint(3, new Point(856.6758458220888, 441.0752314081785));
        new ShapeRef(router, poly41, 41);
        Polygon poly43 = new Polygon(4);
        poly43.setPoint(0, new Point(710.6758458200889, 441.0752314081785));
        poly43.setPoint(1, new Point(710.6758458200889, 461.0752314081785));
        poly43.setPoint(2, new Point(678.6758458200889, 461.0752314081785));
        poly43.setPoint(3, new Point(678.6758458200889, 441.0752314081785));
        new ShapeRef(router, poly43, 43);
        Polygon poly44 = new Polygon(4);
        poly44.setPoint(0, new Point(710.6758458200889, 483.0752314081785));
        poly44.setPoint(1, new Point(710.6758458200889, 503.0752314081785));
        poly44.setPoint(2, new Point(678.6758458200889, 503.0752314081785));
        poly44.setPoint(3, new Point(678.6758458200889, 483.0752314081785));
        new ShapeRef(router, poly44, 44);
        ConnRef connRef149 = new ConnRef(router, 149);
        ConnEnd srcPt149 = new ConnEnd(new Point(709.6758458200889, 451.0752314081785), 8);
        connRef149.setSourceEndpoint(srcPt149);
        ConnEnd dstPt149 = new ConnEnd(new Point(887.6758458220888, 472.0752314081786), 15);
        connRef149.setDestEndpoint(dstPt149);
        connRef149.setRoutingType(ConnType.Orthogonal);
        List<Checkpoint> checkpoints149 = new ArrayList<>();
        checkpoints149.add(new Checkpoint(new Point()));
        checkpoints149.set(0, new Checkpoint(new Point(732.6758458200889, 472.0752314081785), 15, 8));
        connRef149.setRoutingCheckpoints(checkpoints149);
        ConnRef connRef150 = new ConnRef(router, 150);
        ConnEnd srcPt150 = new ConnEnd(new Point(709.6758458200889, 493.0752314081785), 8);
        connRef150.setSourceEndpoint(srcPt150);
        ConnEnd dstPt150 = new ConnEnd(new Point(887.6758458220888, 472.0752314081786), 15);
        connRef150.setDestEndpoint(dstPt150);
        connRef150.setRoutingType(ConnType.Orthogonal);
        List<Checkpoint> checkpoints150 = new ArrayList<>();
        checkpoints150.add(new Checkpoint(new Point()));
        checkpoints150.set(0, new Checkpoint(new Point(732.6758458200889, 472.0752314081785), 15, 8));
        connRef150.setRoutingCheckpoints(checkpoints150);
        router.processTransaction();
        router.outputDiagram("output/checkpoints03");
        boolean succeeded = true;
        Polygon route149 = connRef149.displayRoute();
        Polygon route150 = connRef150.displayRoute();
        if (route149.size() > 4 || route150.size() > 4)
        {
            succeeded = false;
        }
        if (succeeded)
        {
            double checkpointY = 472.0752314081785;
            if (Math.abs(route149.ps.get(route149.size() - 1).y - checkpointY) < 2)
            {
                succeeded = false;
            }
            if (Math.abs(route150.ps.get(route150.size() - 1).y - checkpointY) < 2)
            {
                succeeded = false;
            }
        }
        assertTrue(succeeded);
    }
}
