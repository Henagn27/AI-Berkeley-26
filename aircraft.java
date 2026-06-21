public class aircraft {
    String aircraftName;
    int fuel;
   

    public aircraft(String aircraftName, int fuel){
        this.aircraftName = aircraftName;
        this.fuel = fuel;
     
    }
    
    String getAircraftName(){
        return aircraftName;
    }

    int getFuel(){
        return fuel;
    }


    void setAircraftName(String tempAircraft){
        aircraftName = tempAircraft;
    }

    void setFuelRange(int tempFuel){
        fuel = tempFuel;
    }

}
