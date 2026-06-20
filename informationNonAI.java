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
}
