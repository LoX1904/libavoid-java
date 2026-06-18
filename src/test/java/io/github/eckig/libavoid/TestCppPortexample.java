package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

public class TestCppPortexample
{
    @Test
    public void testexample()
    {
    Router router = new Router(Router.RouterFlag.PolyLineRouting);
    Point srcPt = new Point(1.2, 0.5);
    Point dstPt = new Point(1.5, 4);
    ConnRef connRef = new ConnRef(router, srcPt, dstPt);
    connRef.setCallback(() -> {});
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
    router.outputDiagram("output/example");
    }

    private static void resizePolygon(Polygon polygon, int size)
    {
        polygon.ps.clear();
        while (polygon.ps.size() < size) {
            polygon.ps.add(new Point());
        }
    }
}
