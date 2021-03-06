package cse308.Simulation;

import cse308.Areas.DistrictForMap;
import cse308.Areas.Map;
import cse308.Areas.MasterPrecinct;
import cse308.Areas.PrecinctForMap;
import cse308.Users.UserAccount;

import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;


public class RegionGrowingSimulation extends Simulation{
    int numOfPrecincts;
    private Random rand = new Random();

    public RegionGrowingSimulation(RegionGrowingParams s){
        super(s);
        startingMap=new Map(params.getState()); //create new blank map
        currentMap=startingMap.clone();    //for regiongrowing, blankmap=startingmap=currentmap
        getSeedPrecinctsOne();
    }

    public void getSeedPrecincts(){
        boolean var=params.algorithm.contains("Random");
        if(var){
            getSeedPrecinctsOne();
        }
        else{
            getSeedPrecinctsTwo();
        }
    }
    
    /*
    Description:
        Gets the list of precincts for the state for which the algorithm is running.
        Picks random precincts to be chosen as the seeds, one for each district.
    */
    private void getSeedPrecinctsOne(){
        /*Object[] precincts=startingMap.getAllPrecincts().toArray();
        Collection <DistrictForMap> districts=startingMap.getAllDistricts();
        for(DistrictForMap d: districts){
            PrecinctForMap p=(PrecinctForMap)precincts[precincts.length*(int)Math.random()];
            
            p.isAssigned=true;
            Move move=new Move(p, d);
            moves.add(move);
            currentMap.apply(move);
        }
        System.out.println("");*/
    	
    	PrecinctForMap[] pm;
    	Collection <DistrictForMap> districts = new ArrayList<>();
    	for(DistrictForMap d : startingMap.getAllDistricts()) {
    		if(!d.getMaster().getID().equals("CT_NULL") &&
    				!d.getMaster().getID().equals("NJ_NULL") &&
    				!d.getMaster().getID().equals("NE_NULL")) {
    			
    			districts.add(d);
    			
    		}
    	}
    	
    	for(DistrictForMap d : districts) {
    		pm = startingMap.getNullDistrict().getPrecincts().toArray(new PrecinctForMap[startingMap.getAllPrecincts().size()]);
			PrecinctForMap p = pm[rand.nextInt(pm.length)];
			
			Move move = new Move(p, d);
			moves.add(move);
			currentMap.apply(move);
    	}
    }
    
    private void getSeedPrecinctsTwo(){
        //seeds are the n precincts with the smallest opulations
    }
        
    /*
    Description:
        Checks if there are still precincts to be assigned.
        Runs pickMove(), updates the progress of the simulation, and a call to updateGUI()
    */
    @Override
    public void doStep(){
        if(!isDone()){
        	
            pickMove();
            System.out.println("Finished pickMove, updating progress");
            updateProgress();
            System.out.println("Done");
            //updateGUI();
        }
    }
                 
    /*
    Description:
        Chooses the neighboring precinct that results in the best goodness once added, for each district
    */
    @Override
    public void pickMove(){     
    	
    	System.out.println("Picking Move");
    	/*
        Set<MoveTriple> goodnesses=new HashSet<>();
        for(DistrictForMap d: currentMap.getAllDistricts()){
            for(PrecinctForMap p: d.getBorderPrecincts()){ //updates each time
                for(PrecinctForMap pm: p.getNeighborPrecincts()){ //neighbors of precincts on the border of the district
                    if(!p.isAssigned){
                        Move move=new Move(p, d);
                        Map m=currentMap.cloneApply(move);
                        double goodness = ObjectiveFuncEvaluator.evaluateObjective(params.functionWeights,m);
                        goodnesses.add(new MoveTriple(goodness, m, move));
                    }
                }
            }
        }
        //sort map by goodnesses and add precinct that results in the best goodness
        MoveTriple bestTriple=null;
        for (MoveTriple t: goodnesses){
            bestTriple= t.compareTo(bestTriple)>0 ? t:bestTriple;
        }
        currentMap=bestTriple.map;
        currentGoodness=bestTriple.goodness;
        moves.add(bestTriple.move);
        */
    	
    	//System.out.println(currentMap.toString());
    	
    	

    	Set <PrecinctForMap> borderPrecincts=new HashSet<>();
    	Set <PrecinctForMap> nullP = currentMap.getNullDistrict().getPrecincts();
    	
    	System.out.println("Precincts Remaining: " + nullP.size());
    	for(PrecinctForMap p : currentMap.getNullDistrict().getPrecincts()) {	// returning empty
    		if(p.isDistrictBorder()) {
    			borderPrecincts.add(p);
    		}
    	}
 
    	//for(PrecinctForMap p : borderPrecincts) {
    	
    	
    	PrecinctForMap[] pm = (PrecinctForMap[]) borderPrecincts.toArray(new PrecinctForMap[borderPrecincts.size()]);
    	
    	PrecinctForMap randP = pm[rand.nextInt(pm.length)];
    	
    	DistrictForMap[] dm = (DistrictForMap[]) randP.getNeighborDistricts().toArray(new DistrictForMap[randP.getNeighborDistricts().size()]);
    	
    	DistrictForMap randD = dm[rand.nextInt(dm.length)];
    		
    	Move move=new Move(randP, randD);
    	
    	
        Map m=currentMap.cloneApply(move);

        
        double goodness = ObjectiveFuncEvaluator.evaluateObjective(params.functionWeights,m);
        ObjectiveFuncEvaluator.showObjective(params.functionWeights,m);
        currentMap = m;
        
        moves.add(move);
        
        
        //System.out.println("goodness: " + goodness);
        //System.out.println("number in null district left: " + currentMap.getNullDistrict().getPrecincts().size());
    	//}
    	
    }
    
    /*
    Description:
        Updates the simulations progress, which is based on the # of moves 
        # of moves/ # of precincts in state -> one move per precinct
    */
    @Override
    public void updateProgress(){
        progress=moves.size()/1;
        System.out.println("Updating Map");
        this.savedSim.setCurrentMap(currentMap);
        System.out.println(savedSim.getId() + ":" + savedSim.getCurrentMap().getNullDistrict().getPrecincts().size());
    }
    
    @Override
    public boolean isDone() {
    	return (currentMap.getNullDistrict().getPrecincts().isEmpty());
    }
}