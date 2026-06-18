package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

public class TestCppPortconnendmove
{
    @Test
    public void testconnendmove()
    {
    Router router = new Router(Router.RouterFlag.OrthogonalRouting);
    Rectangle shapeRect1 = new Rectangle(new Point(0, 0), new Point(10, 10));
    ShapeRef shapeRef1 = new ShapeRef(router, shapeRect1);
    final int CENTRE = 1;
    new ShapeConnectionPin(shapeRef1, CENTRE, 
            ShapeConnectionPin.ATTACH_POS_CENTRE, ShapeConnectionPin.ATTACH_POS_CENTRE, true, 0.0, ConnDirFlag.ConnDirNone);
    Rectangle shapeRect2 = new Rectangle(new Point(0, 0), new Point(10, 10));
    ShapeRef shapeRef2 = new ShapeRef(router, shapeRect1);
    ConnEnd dstPt = new ConnEnd(shapeRef1, CENTRE); 
    Point srcPt = new Point(1.5, 4);
    ConnRef connRef = new ConnRef(router, srcPt, dstPt);
    router.processTransaction();
    router.outputDiagram("output/connendmove-1");
    Point dstPt2 = new Point(20, 20);
    connRef.setDestEndpoint(dstPt2);
    router.moveShape(shapeRef1, 0.5, 0);
    router.processTransaction();
    router.outputDiagram("output/connendmove-2");
    srcPt.x += 0.5;
    connRef.setSourceEndpoint(srcPt);
    router.moveShape(shapeRef1, 0.5, 0);
    router.moveShape(shapeRef2, 0, 0.5);
    srcPt.x += 0.5;
    connRef.setSourceEndpoint(srcPt);
    router.moveShape(shapeRef1, 0.5, 0);
    router.moveShape(shapeRef2, 0, 0.5);
    srcPt.x += 0.5;
    connRef.setSourceEndpoint(srcPt);
    router.moveShape(shapeRef1, 0.5, 0);
    router.moveShape(shapeRef2, 0, 0.5);
    srcPt.x += 0.5;
    connRef.setSourceEndpoint(srcPt);
    router.moveShape(shapeRef1, 0.5, 0);
    router.moveShape(shapeRef2, 0, 0.5);
    router.processTransaction();
    router.outputDiagram("output/connendmove-3");
    }

    private static void resizePolygon(Polygon polygon, int size)
    {
        polygon.ps.clear();
        while (polygon.ps.size() < size) {
            polygon.ps.add(new Point());
        }
    }
}
