package cse308.Areas;


import cse308.Data.GeoRegion;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.jts.geom.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class DistrictForMap implements GeoRegion{
    private final MasterDistrict master;
    private Map map;
    
    public DistrictForMap(MasterDistrict md, Map map){
        master=md;
        this.map = map;
    }
    
    public Set<PrecinctForMap> getBorderPrecincts(){
        //get the precincts that are on te border of the district
    	
    	Set <PrecinctForMap> precincts=new HashSet<>();
    	for(PrecinctForMap p : this.getPrecincts()) {
    		if(p.isDistrictBorder()) {
    			precincts.add(p);
    		}
    	}
    	
    	return precincts;
    }
    
    /*
    Description:
        //Returns the districts holding the precincts that neighbor a precinct on the border of this districtformap
    */
    public Set<DistrictForMap> getNeighborDistricts(){        
        Set<DistrictForMap> neighbors=new HashSet<>();
        for(PrecinctForMap border: getBorderPrecincts()){
            for(PrecinctForMap neighbor: border.getNeighborPrecincts()){
                neighbors.add(neighbor.getParentDistrict());
            }
        }
        return neighbors;
    }
    
    public Map getMap(){
        return map;
    }
    public void setMap(Map m){
        map=m;
    }
    
    public MasterDistrict getMaster(){
        return master;
    }
    
    public Set<PrecinctForMap> getPrecincts(){
        Set <PrecinctForMap> precincts=new HashSet<>();
        for(PrecinctForMap p: map.getAllPrecincts()){
            if (p.getParentDistrict()==this){
                precincts.add(p);
            }
        }
        return precincts;
    }
    
//    public Geometry getDistrictBoundary() {
//    	GeometryFactory geometryFactory = new GeometryFactory();
//    	Coordinate a = new Coordinate(-83.86962890625,40.94671366508002);
//    	Coordinate b = new Coordinate(-84.24316406249999,40.12849105685408);
//    	Coordinate c = new Coordinate(-83.75976562499999, 39.825413103424786);
//    	Coordinate d = new Coordinate(-83.07861328125,40.06125658140474);
//    	Coordinate e = new Coordinate(-83.86962890625,40.94671366508002);
//    	Coordinate[] coord = new Coordinate[5];
//    	coord[0]=a;
//    	coord[1]=b;
//    	coord[2]=c;
//    	coord[3]=d;
//    	coord[4]=e;
//    	Polygon pol1 = geometryFactory.createPolygon(coord);
//    	
//    	Coordinate f = new Coordinate(-83.86962890625,40.94671366508002);
//    	Coordinate g = new Coordinate(-83.07861328125,40.094882122321145);
//        Coordinate h = new Coordinate(-82.705078125,40.329795743702064);
//    	Coordinate i = new Coordinate(-82.55126953124999,40.730608477796636);
//    	Coordinate j = new Coordinate(-82.5732421875,41.11246878918088);
//    	Coordinate k = new Coordinate(-83.86962890625,40.94671366508002);
//    	Coordinate[] coord1 = new Coordinate[5];
//    	coord1[0]=f;
//    	coord1[1]=g;
//    	coord1[2]=h;
//    	coord1[3]=i;
//    	coord1[4]=j;
//    	coord1[5]=k;
//    	Polygon pol2 = geometryFactory.createPolygon(coord1);
//    	
////    	Polygon[] polArray = new Polygon[2];
////    	polArray[0]=pol1;
////    	polArray[1]=pol2;
//    	
//    	ArrayList<Polygon> polArray = new ArrayList<>();
//    	polArray.add(pol1);
//    	polArray.add(pol2);
//    	
//    	CascadedPolygonUnion polUnion = new CascadedPolygonUnion(polArray);
//    	return null;
//    }

	@Override
	public int getVotingPopulation() {
		int votingPopulation = 0;
		Set<PrecinctForMap> precincts = getPrecincts();
		for (PrecinctForMap pr: precincts) {
			votingPopulation += pr.getVotingPopulation();
		}
		return votingPopulation;
	}

	@Override
	public int getTotalVotes() {
		int totalVotes = 0;
		Set<PrecinctForMap> precincts = getPrecincts();
		for (PrecinctForMap pr: precincts) {
			totalVotes += pr.getTotalVotes();
		}
		return totalVotes;
	}

	@Override
	public double getPercentDemocrat() {
		int totalVotes = 0;
		double demVotes = 0;
		Set<PrecinctForMap> precincts = getPrecincts();
		for (PrecinctForMap pr: precincts) {
			totalVotes += pr.getTotalVotes();
			demVotes = pr.getPercentDemocrat()*totalVotes;
		}

		if(totalVotes < 1) {
			return 0;
		} else {
			return demVotes/totalVotes;
		}
	}

	@Override
	public Geometry getGeometry() {
		Set<PrecinctForMap> precincts = getPrecincts();

		ArrayList<Geometry> polArray = new ArrayList<>();
		for (PrecinctForMap pr: precincts) {
			polArray.add(pr.getGeometry());
		}
		Geometry geoms = new GeometryFactory().buildGeometry(polArray);

		// buffering it unions them all together faster than cascadedpolygonunion
		// buffer and unbuffer to get rid of gaps
		Geometry unioned = geoms.buffer(0.0);
		return unioned.buffer(0.005).buffer(-0.005);
	}

	@Override
	public int getPopulation() {
		int population = 0;
		Set<PrecinctForMap> precincts = getPrecincts();
		for (PrecinctForMap pr: precincts) {
			population += pr.getPopulation();
		}
		return population;
	}

	public double getArea() {
		return getGeometry().getArea();
	}

	public double getPerimeter() {
		return getGeometry().getLength();
	}
}