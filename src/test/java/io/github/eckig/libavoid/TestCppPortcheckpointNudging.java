package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestCppPortcheckpointNudging
{
    @Test
    public void testcheckpointNudging2()
    {
    Router router = new Router(
            Router.RouterFlag.PolyLineRouting.flag | Router.RouterFlag.OrthogonalRouting.flag);
    router.setRoutingParameter(0, 50);
    router.setRoutingParameter(1, 0);
    router.setRoutingParameter(2, 0);
    router.setRoutingParameter(3, 4000);
    router.setRoutingParameter(4, 0);
    router.setRoutingParameter(5, 100);
    router.setRoutingOption(0, true);
    router.setRoutingOption(1, true);
    router.setRoutingOption(2, false);
    Polygon poly1 = new Polygon(4);
    poly1.setPoint(0, new Point(1201.5, 1180.75));
    poly1.setPoint(1, new Point(1201.5, 1212.75));
    poly1.setPoint(2, new Point(1181.5, 1212.75));
    poly1.setPoint(3, new Point(1181.5, 1180.75));
    new ShapeRef(router, poly1, 1);
    Polygon poly6 = new Polygon(4);
    poly6.setPoint(0, new Point(1643.5, 817.25));
    poly6.setPoint(1, new Point(1643.5, 1194.25));
    poly6.setPoint(2, new Point(1421.5, 1194.25));
    poly6.setPoint(3, new Point(1421.5, 817.25));
    new ShapeRef(router, poly6, 6);
    Polygon poly60 = new Polygon(4);
    poly60.setPoint(0, new Point(1222.5, 1424.42));
    poly60.setPoint(1, new Point(1222.5, 1486.42));
    poly60.setPoint(2, new Point(1160.5, 1486.42));
    poly60.setPoint(3, new Point(1160.5, 1424.42));
    new ShapeRef(router, poly60, 60);
    Polygon poly81 = new Polygon(4);
    poly81.setPoint(0, new Point(1250.5, 1491.42));
    poly81.setPoint(1, new Point(1250.5, 1523.42));
    poly81.setPoint(2, new Point(1230.5, 1523.42));
    poly81.setPoint(3, new Point(1230.5, 1491.42));
    new ShapeRef(router, poly81, 81);
    Polygon poly82 = new Polygon(4);
    poly82.setPoint(0, new Point(1341.5, 1491.42));
    poly82.setPoint(1, new Point(1341.5, 1523.42));
    poly82.setPoint(2, new Point(1321.5, 1523.42));
    poly82.setPoint(3, new Point(1321.5, 1491.42));
    new ShapeRef(router, poly82, 82);
    Polygon poly84 = new Polygon(4);
    poly84.setPoint(0, new Point(1432.5, 1491.42));
    poly84.setPoint(1, new Point(1432.5, 1523.42));
    poly84.setPoint(2, new Point(1412.5, 1523.42));
    poly84.setPoint(3, new Point(1412.5, 1491.42));
    new ShapeRef(router, poly84, 84);
    Polygon poly88 = new Polygon(4);
    poly88.setPoint(0, new Point(1222.5, 1260.42));
    poly88.setPoint(1, new Point(1222.5, 1322.42));
    poly88.setPoint(2, new Point(1160.5, 1322.42));
    poly88.setPoint(3, new Point(1160.5, 1260.42));
    new ShapeRef(router, poly88, 88);
    Polygon poly139 = new Polygon(4);
    poly139.setPoint(0, new Point(1308.5, 1323.42));
    poly139.setPoint(1, new Point(1308.5, 1363.42));
    poly139.setPoint(2, new Point(1232.5, 1363.42));
    poly139.setPoint(3, new Point(1232.5, 1323.42));
    new ShapeRef(router, poly139, 139);
    Polygon poly140 = new Polygon(4);
    poly140.setPoint(0, new Point(1326.5, 1487.42));
    poly140.setPoint(1, new Point(1326.5, 1527.42));
    poly140.setPoint(2, new Point(1250.5, 1527.42));
    poly140.setPoint(3, new Point(1250.5, 1487.42));
    new ShapeRef(router, poly140, 140);
    Polygon poly141 = new Polygon(4);
    poly141.setPoint(0, new Point(1417.5, 1487.42));
    poly141.setPoint(1, new Point(1417.5, 1527.42));
    poly141.setPoint(2, new Point(1341.5, 1527.42));
    poly141.setPoint(3, new Point(1341.5, 1487.42));
    new ShapeRef(router, poly141, 141);
    Polygon poly147 = new Polygon(4);
    poly147.setPoint(0, new Point(1399.5, 1043.75));
    poly147.setPoint(1, new Point(1399.5, 1083.75));
    poly147.setPoint(2, new Point(1323.5, 1083.75));
    poly147.setPoint(3, new Point(1323.5, 1043.75));
    new ShapeRef(router, poly147, 147);
    ConnRef connRef256 = new ConnRef(router, 256);
    ConnEnd srcPt256 = new ConnEnd(new Point(1191.5, 1291.42), 15);
    connRef256.setSourceEndpoint(srcPt256);
    ConnEnd dstPt256 = new ConnEnd(new Point(1240.5, 1492.42), 1);
    connRef256.setDestEndpoint(dstPt256);
    connRef256.setRoutingType(ConnType.Orthogonal);
    List<Checkpoint> checkpoints256 = new ArrayList<>();
    checkpoints256.add(new Checkpoint(new Point()));
    checkpoints256.set(0, new Checkpoint(new Point(1331.5, 1477.42)));
    connRef256.setRoutingCheckpoints(checkpoints256);
    ConnRef connRef257 = new ConnRef(router, 257);
    ConnEnd srcPt257 = new ConnEnd(new Point(1191.5, 1291.42), 15);
    connRef257.setSourceEndpoint(srcPt257);
    ConnEnd dstPt257 = new ConnEnd(new Point(1331.5, 1492.42), 1);
    connRef257.setDestEndpoint(dstPt257);
    connRef257.setRoutingType(ConnType.Orthogonal);
    List<Checkpoint> checkpoints257 = new ArrayList<>();
    checkpoints257.add(new Checkpoint(new Point()));
    checkpoints257.set(0, new Checkpoint(new Point(1331.5, 1477.42)));
    connRef257.setRoutingCheckpoints(checkpoints256);
    ConnRef connRef258 = new ConnRef(router, 258);
    ConnEnd srcPt258 = new ConnEnd(new Point(1191.5, 1291.42), 15);
    connRef258.setSourceEndpoint(srcPt258);
    ConnEnd dstPt258 = new ConnEnd(new Point(1422.5, 1492.42), 1);
    connRef258.setDestEndpoint(dstPt258);
    connRef258.setRoutingType(ConnType.Orthogonal);
    List<Checkpoint> checkpoints258 = new ArrayList<>();
    checkpoints258.add(new Checkpoint(new Point()));
    checkpoints258.set(0, new Checkpoint(new Point(1331.5, 1477.42)));
    connRef258.setRoutingCheckpoints(checkpoints256);
    ConnRef connRef335 = new ConnRef(router, 335);
    ConnEnd srcPt335 = new ConnEnd(new Point(1191.5, 1291.42), 15);
    connRef335.setSourceEndpoint(srcPt335);
    ConnEnd dstPt335 = new ConnEnd(new Point(1427, 1026.25), 4);
    connRef335.setDestEndpoint(dstPt335);
    connRef335.setRoutingType(ConnType.Orthogonal);
    router.processTransaction();
    router.outputDiagram("output/checkpointNudging2");
    boolean areInline = (connRef258.displayRoute().ps.get(2).y == connRef258.displayRoute().ps.get(3).y) &&
    		     (connRef258.displayRoute().ps.get(2).y == connRef256.displayRoute().ps.get(2).y) &&
    		     (connRef258.displayRoute().ps.get(2).y == connRef256.displayRoute().ps.get(3).y);
    assertTrue(areInline);
    }

    @Test
    public void testcheckpointNudging3()
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
        Polygon poly16 = new Polygon(4);
        poly16.setPoint(0, new Point(-452.87164351602, -843.9812403239691));
        poly16.setPoint(1, new Point(-452.87164351602, -823.9812403239691));
        poly16.setPoint(2, new Point(-484.87164351602, -823.9812403239691));
        poly16.setPoint(3, new Point(-484.87164351602, -843.9812403239691));
        new ShapeRef(router, poly16, 16);
        Polygon poly17 = new Polygon(4);
        poly17.setPoint(0, new Point(-452.87164351602, -768.9812403239691));
        poly17.setPoint(1, new Point(-452.87164351602, -748.9812403239691));
        poly17.setPoint(2, new Point(-484.87164351602, -748.9812403239691));
        poly17.setPoint(3, new Point(-484.87164351602, -768.9812403239691));
        new ShapeRef(router, poly17, 17);
        Polygon poly99 = new Polygon(4);
        poly99.setPoint(0, new Point(-452.87164351602, -726.9812403239691));
        poly99.setPoint(1, new Point(-452.87164351602, -706.9812403239691));
        poly99.setPoint(2, new Point(-484.87164351602, -706.9812403239691));
        poly99.setPoint(3, new Point(-484.87164351602, -726.9812403239691));
        new ShapeRef(router, poly99, 99);
        Polygon poly118 = new Polygon(4);
        poly118.setPoint(0, new Point(-664.12164351602, -768.9812403239691));
        poly118.setPoint(1, new Point(-664.12164351602, -706.9812403239691));
        poly118.setPoint(2, new Point(-726.12164351602, -706.9812403239691));
        poly118.setPoint(3, new Point(-726.12164351602, -768.9812403239691));
        new ShapeRef(router, poly118, 118);
        Polygon poly315 = new Polygon(4);
        poly315.setPoint(0, new Point(-430.97164351602, -833.9812403239691));
        poly315.setPoint(1, new Point(-430.97164351602, -716.9812403239691));
        poly315.setPoint(2, new Point(-506.77164351602, -716.9812403239691));
        poly315.setPoint(3, new Point(-506.77164351602, -833.9812403239691));
        new ShapeRef(router, poly315, 315);
        ConnRef connRef518 = new ConnRef(router, 518);
        ConnEnd srcPt518 = new ConnEnd(new Point(-483.87164351602, -833.9812403239691), 4);
        connRef518.setSourceEndpoint(srcPt518);
        ConnEnd dstPt518 = new ConnEnd(new Point(-695.12164351602, -737.9812403239691), 15);
        connRef518.setDestEndpoint(dstPt518);
        connRef518.setRoutingType(ConnType.Orthogonal);
        List<Checkpoint> checkpoints518 = new ArrayList<>();
        checkpoints518.add(new Checkpoint(new Point()));
        checkpoints518.set(0, new Checkpoint(new Point(-506.87164351602, -737.9812403239691), 15, 4));
        connRef518.setRoutingCheckpoints(checkpoints518);
        ConnRef connRef519 = new ConnRef(router, 519);
        ConnEnd srcPt519 = new ConnEnd(new Point(-483.87164351602, -758.9812403239691), 4);
        connRef519.setSourceEndpoint(srcPt519);
        ConnEnd dstPt519 = new ConnEnd(new Point(-695.12164351602, -737.9812403239691), 15);
        connRef519.setDestEndpoint(dstPt519);
        connRef519.setRoutingType(ConnType.Orthogonal);
        List<Checkpoint> checkpoints519 = new ArrayList<>();
        checkpoints519.add(new Checkpoint(new Point()));
        checkpoints519.set(0, new Checkpoint(new Point(-506.87164351602, -737.9812403239691), 15, 4));
        connRef519.setRoutingCheckpoints(checkpoints519);
        ConnRef connRef520 = new ConnRef(router, 520);
        ConnEnd srcPt520 = new ConnEnd(new Point(-483.87164351602, -716.9812403239691), 4);
        connRef520.setSourceEndpoint(srcPt520);
        ConnEnd dstPt520 = new ConnEnd(new Point(-695.12164351602, -737.9812403239691), 15);
        connRef520.setDestEndpoint(dstPt520);
        connRef520.setRoutingType(ConnType.Orthogonal);
        List<Checkpoint> checkpoints520 = new ArrayList<>();
        checkpoints520.add(new Checkpoint(new Point()));
        checkpoints520.set(0, new Checkpoint(new Point(-506.87164351602, -737.9812403239691), 15, 4));
        connRef520.setRoutingCheckpoints(checkpoints520);
        router.processTransaction();
        router.outputDiagram("output/checkpointNudging3");
        boolean overlap = router.existsOrthogonalSegmentOverlap();
        assertFalse(overlap);
    }

}
