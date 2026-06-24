package io.github.eckig.libavoid;

import org.junit.jupiter.api.Test;

public class TestCppPorthyperedgeLoop1
{
    @Test
    public void testhyperedgeLoop1()
    {
	ConnEnd end1 = new ConnEnd();
	ConnEnd end2 = new ConnEnd();
	Polygon poly = new Polygon();
	Router router1 = new Router(Router.RouterFlag.OrthogonalRouting);
	router1.setTransactionUse(true);
	router1.setRoutingPenalty(Router.RoutingParameter.segmentPenalty);
	router1.setRoutingPenalty(Router.RoutingParameter.crossingPenalty);
	router1.setRoutingPenalty(Router.RoutingParameter.fixedSharedPathPenalty, 9000);
	router1.setRoutingParameter(Router.RoutingParameter.idealNudgingDistance, 25);
	poly = new Polygon(4);
	poly.setPoint(0, new Point(50760, 50620));
	poly.setPoint(1, new Point(50760, 50680));
	poly.setPoint(2, new Point(50590, 50680));
	poly.setPoint(3, new Point(50590, 50620));
	ShapeRef shape147006780 = new ShapeRef(router1, poly, 147006780);
	ShapeConnectionPin pin147006780_1 = new ShapeConnectionPin(shape147006780, 2, 0.941176, 0.5, true, 10, 8);
	pin147006780_1.setExclusive(true);
	poly = new Polygon(4);
	poly.setPoint(0, new Point(50585, 50765));
	poly.setPoint(1, new Point(50585, 50935));
	poly.setPoint(2, new Point(50365, 50935));
	poly.setPoint(3, new Point(50365, 50765));
	ShapeRef shape69758810 = new ShapeRef(router1, poly, 69758810);
	ShapeConnectionPin pin69758810_1 = new ShapeConnectionPin(shape69758810, 2, 0.954545, 0.5, true, 10, 8);
	pin69758810_1.setExclusive(true);
	poly = new Polygon(4);
	poly.setPoint(0, new Point(51060, 51020));
	poly.setPoint(1, new Point(51060, 51080));
	poly.setPoint(2, new Point(50890, 51080));
	poly.setPoint(3, new Point(50890, 51020));
	ShapeRef shape149922619 = new ShapeRef(router1, poly, 149922619);
	ShapeConnectionPin pin149922619_1 = new ShapeConnectionPin(shape149922619, 2, 0.941176, 0.5, true, 10, 8);
	pin149922619_1.setExclusive(true);
	JunctionRef junction228834480 = new JunctionRef(router1, new Point(51050, 51050), 228834480);
	end1 = new ConnEnd(shape69758810, 2);
	end2 = new ConnEnd(junction228834480);
	ConnRef conn159270000 = new ConnRef(router1, end1, end2);
	conn159270000.makePathInvalid();
	end1 = new ConnEnd(junction228834480);
	end2 = new ConnEnd(shape149922619, 2);
	ConnRef conn199495942 = new ConnRef(router1, end1, end2);
	router1.processTransaction();
	end1 = new ConnEnd(shape147006780, 2);
	end2 = new ConnEnd(junction228834480);
	ConnRef conn8326760 = new ConnRef(router1, end1, end2);
	conn8326760.makePathInvalid();
	router1.processTransaction(); 
	router1.deleteShape(shape147006780);
	shape147006780 = null;
	router1.deleteShape(shape69758810);
	shape69758810 = null;
	router1.deleteShape(shape149922619);
	shape149922619 = null;
	router1.deleteJunction(junction228834480);
	junction228834480 = null;
	router1.deleteConnector(conn159270000);
	conn159270000 = null;
	router1.deleteConnector(conn199495942);
	conn199495942 = null;
	router1.deleteConnector(conn8326760);
	conn8326760 = null;
	router1.processTransaction();
    }

}
