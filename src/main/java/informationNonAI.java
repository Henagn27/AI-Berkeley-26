public class informationNonAI {
    String departingAirport;
    String arrivingAirport;
    int time;
    String date;
    aircraft aircraftType;
    String planType;
    String weather;
    int fuel;


    public informationNonAI(String departingAirport, String arrivingAirport, int time, String date, String planType, String weather, int fuel){
        this.departingAirport = departingAirport;
        this.arrivingAirport = arrivingAirport;
        this.time = time;
        this.date = date;
        this.planType = planType;
        this.weather = weather;
        this.fuel = fuel;
    }

    String getDepartingAirport(){
        return departingAirport;
    }

      String getArrivingAirport(){
        return arrivingAirport;
    }

      String getDate(){
        return date;
    }

      String getPlanType(){
        return planType;
    }

    String getWeather(){
        return weather;
    }

     int getTime(){
        return time;
    }

    int getFuel(){
        return fuel;
    }

    void setDepartingAirport(String tempAirport){
     departingAirport = tempAirport;
    }

    void setArrivingAirport(String tempAirport){
     arrivingAirport = tempAirport;
    }

    void setDate(String tempDate){
     date = tempDate;
    }

    void setPlanType(String tempPlan){
     planType = tempPlan;
    }

    void setWeather(String tempWeather){
     weather = tempWeather;
    }

    void setTime(int tempTime){
     time = tempTime;
    }

    void setFuel(int tempFuel){
     fuel = tempFuel;
    }

    
}
