package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

public class TestCppPortlatesetup
{
    @Test
    public void testlatesetup()
    {
    Router router = new Router(Router.RouterFlag.PolyLineRouting);
    router.setTransactionUse(false);
    Point srcPt = new Point(1.2, 0.5);
    Point dstPt = new Point(1.5, 4);
    ConnRef connRef = new ConnRef(router);
    connRef.setCallback(() -> {});
    router.processTransaction();
    ConnRef connRef2 = new ConnRef(router);
    connRef2.setCallback(() -> {});
    router.processTransaction();
    router.deleteConnector(connRef2);
    connRef2 = null;
    router.processTransaction();
    connRef.setEndpoints(srcPt, dstPt);
    router.processTransaction();
    Polygon shapePoly = new Polygon(3);
    shapePoly.setPoint(0, new Point(1, 1));
    shapePoly.setPoint(1, new Point(2.5, 1.5));
    shapePoly.setPoint(2, new Point(1.5, 2.5));
    ShapeRef shapeRef = new ShapeRef(router, shapePoly);
    router.processTransaction();
    Point dstPt2 = new Point(6, 4.5);
    connRef.setDestEndpoint(dstPt2);
    router.processTransaction();
    router.moveShape(shapeRef, 0.5, 0);
    router.processTransaction();
    }

    private static void resizePolygon(Polygon polygon, int size)
    {
        polygon.ps.clear();
        while (polygon.ps.size() < size) {
            polygon.ps.add(new Point());
        }
    }
}
