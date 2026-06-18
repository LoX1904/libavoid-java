package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

public class TestCppPortmulticonnact
{
    @Test
    public void testmulticonnact()
    {
    Router router = new Router(Router.RouterFlag.OrthogonalRouting);
    Point srcPt = new Point(0,400);
    Point dstPt = new Point(775,400);
    ConnRef connRef = new ConnRef(router, srcPt, dstPt);
    connRef.setCallback(() -> {});
    Point srcPt2 = new Point(775,625);
    Point dstPt2 = new Point(350,475);
    ConnRef connRef2 = new ConnRef(router, srcPt2, dstPt2);
    connRef2.setCallback(() -> {});
    router.processTransaction();
    connRef.setEndpoints(new Point(0,375), new Point(775,400));
    connRef2.setEndpoints(new Point(775,625), new Point(350,450));
    router.processTransaction();
    connRef.setEndpoints(new Point(0,400), new Point(775,400));
    connRef2.setEndpoints(new Point(775,625), new Point(350,475));
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
