package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

public class TestConnectionPin
{
    private static final int CENTRE = 1;
    @Test
    public void test()
    {
        Router router = new Router(Router.RouterFlag.OrthogonalRouting);

        // Create the ShapeRef:
        Rectangle shapeRect1 = new Rectangle(new Point(0, 0), new Point(10, 10));
        ShapeRef shapeRef1 = new ShapeRef(router, shapeRect1);

        Rectangle shapeRect2 = new Rectangle(new Point(40, 20), new Point(50, 30));
        ShapeRef shapeRef2 = new ShapeRef(router, shapeRect2);

        new ShapeConnectionPin(shapeRef1, CENTRE, ShapeConnectionPin.ATTACH_POS_CENTRE,
            ShapeConnectionPin.ATTACH_POS_CENTRE, true, 0.0, ConnDirFlag.ConnDirNone);
        new ShapeConnectionPin(shapeRef2, CENTRE, ShapeConnectionPin.ATTACH_POS_CENTRE,
            ShapeConnectionPin.ATTACH_POS_CENTRE, true, 0.0, ConnDirFlag.ConnDirNone);

        ConnEnd dstEnd=new ConnEnd(shapeRef1, CENTRE);
        ConnEnd srcEnd=new ConnEnd(shapeRef2, CENTRE);
        new ConnRef(router, srcEnd, dstEnd);
        // Force inital callback:
        router.processTransaction();

        router.moveShape(shapeRef2, 5, 0);

        router.processTransaction();

        router.moveShape(shapeRef1, 0, -10);

        router.processTransaction();
    }
}