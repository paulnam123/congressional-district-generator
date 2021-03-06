package cse308.Simulation;

import cse308.Areas.DistrictForMap;
import cse308.Areas.Map;
import cse308.Areas.PrecinctForMap;
import java.util.ArrayList;

public class SimulationWorker{
    private ArrayList<Simulation> queue=new ArrayList<>();
    
    public void addToRunQueue(Simulation sim){
        queue.add(sim);
    }
    
    public void removeFromRunQueue(Simulation sim){
        queue.remove(sim);
    }
    
    public void runNextSimulation(){
        System.out.println("Simworker, getting next simulation");
        if(!queue.isEmpty()){
            Simulation sim=queue.get(0);
            Map result;
            if(sim.savedSim != null) {
                System.out.println("got sim: " + sim.savedSim.getId() +", about to dostep");
            }
            while (!sim.isDone() && !sim.isPaused){
                sim.doStep();
            }
            result=sim.currentMap;
            if(sim.isDone() && sim.getClass().equals(SimulatedAnnealingSimulation.class)){
                Map bestMap=((SimulatedAnnealingSimulation)sim).bestMap;
                if(ObjectiveFuncEvaluator.evaluateObjective(sim.params.functionWeights, bestMap)>sim.getGoodness()){
                    result=bestMap;
                }
            }            
            if (sim.isDone()){ 
                queue.remove(0);
            }
        }
    }
    
    public void pause(){
        queue.get(0).isPaused=true;
    }
    
    
    public void proceed(){
        if(queue.get(0).isPaused!=false){
            queue.get(0).isPaused=false;
            runNextSimulation();
        }
    }
    
    public void terminate(){
        queue.get(0).isPaused=true;
        queue.remove(0);        
    }
    /*
    @Override
    public void run(){
        if(!queue.isEmpty()){
            runNextSimulation();
        }
    }*/
}