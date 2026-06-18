package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

public class TestCppPortinfinity
{
    @Test
    public void testinfinity()
    {
    Router router = new Router(Router.RouterFlag.OrthogonalRouting);
    router.setRoutingPenalty(Router.RoutingParameter.segmentPenalty, 50);
    router.setRoutingParameter(Router.RoutingParameter.idealNudgingDistance, 10);
    Rectangle rect47 = new Rectangle(new Point(51145, 50195), new Point(51145+360, 50195+685));
    new ShapeRef(router, rect47);
    Rectangle rect46 = new Rectangle(new Point(51920, 49770), new Point(51920+360, 49770+310));
    new ShapeRef(router, rect46);
    Rectangle rect45 = new Rectangle(new Point(51151, 49770), new Point(51151+360, 49770+310));
    new ShapeRef(router, rect45);
    ConnEnd srcPt53 = new ConnEnd(new Point(51150,49850), 4);
    ConnEnd dstPt53 = new ConnEnd(new Point(51500,50350), 8);
    new ConnRef(router, srcPt53, dstPt53);
    ConnEnd srcPt52 = new ConnEnd(new Point(52275,49850), 8);
    ConnEnd dstPt52 = new ConnEnd(new Point(51150,49925), 4);
    new ConnRef(router, srcPt52, dstPt52);
    ConnEnd srcPt6 = new ConnEnd(new Point(51150,50500), 4);
    ConnEnd dstPt6 = new ConnEnd(new Point(51925,50000), 4);
    new ConnRef(router, srcPt6, dstPt6, 6);
    router.processTransaction();
    router.outputDiagram("output/infinity");
    }

    private static void resizePolygon(Polygon polygon, int size)
    {
        polygon.ps.clear();
        while (polygon.ps.size() < size) {
            polygon.ps.add(new Point());
        }
    }
}
