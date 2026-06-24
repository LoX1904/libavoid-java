package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

public class TestInline
{
    @Test
    public void testInline()
    {
        Router router = new Router(Router.RouterFlag.OrthogonalRouting);

        // Create the ShapeRef:
        Rectangle shapePoly = new Rectangle(new Point(0, 0), new Point(100, 100));
        new ShapeRef(router, shapePoly);

        ConnEnd srcPt = new ConnEnd(new Point(200, 200));

        ConnEnd dstPt = new ConnEnd(new Point(95, 25), ConnDirFlag.ConnDirUp | ConnDirFlag.ConnDirRight | ConnDirFlag.ConnDirDown);
        new ConnRef(router, srcPt, dstPt);

        dstPt = new ConnEnd(new Point(95, 75), ConnDirFlag.ConnDirUp | ConnDirFlag.ConnDirRight | ConnDirFlag.ConnDirDown);
        new ConnRef(router, srcPt, dstPt);

        // Up goes towards negative coordinates.
        dstPt = new ConnEnd(new Point(25, 5), ConnDirFlag.ConnDirLeft | ConnDirFlag.ConnDirRight | ConnDirFlag.ConnDirUp);
        new ConnRef(router, srcPt, dstPt);

        dstPt = new ConnEnd(new Point(75, 5), ConnDirFlag.ConnDirLeft | ConnDirFlag.ConnDirRight | ConnDirFlag.ConnDirUp);
        new ConnRef(router, srcPt, dstPt);

        dstPt = new ConnEnd(new Point(25, 95), ConnDirFlag.ConnDirLeft | ConnDirFlag.ConnDirRight | ConnDirFlag.ConnDirDown);
        new ConnRef(router, srcPt, dstPt);

        dstPt = new ConnEnd(new Point(75, 95), ConnDirFlag.ConnDirLeft | ConnDirFlag.ConnDirRight | ConnDirFlag.ConnDirDown);
        new ConnRef(router, srcPt, dstPt);

        router.processTransaction();
    }
}
