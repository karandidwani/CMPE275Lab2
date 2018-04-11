package com.cmpe275lab2.flightreservation.Service;

import com.cmpe275lab2.flightreservation.Entity.Flight;
import com.cmpe275lab2.flightreservation.Entity.Passenger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightResponseService {

    public JSONObject getFlightJSON(Flight flight) {

        JSONObject finalJSON = new JSONObject();
        JSONObject flightJSON = new JSONObject();

        try {
            finalJSON.put("flight", flightJSON);
            flightJSON.put("flightNumber", flight.getFlightNumber());
            flightJSON.put("price", flight.getPrice());
            flightJSON.put("origin", flight.getFromCity());
            flightJSON.put("to", flight.getToCity());
            flightJSON.put("departureTime", flight.getDepartureTime());
            flightJSON.put("arrivalTime", flight.getArrivalTime());
            flightJSON.put("description", flight.getDescription());
            flightJSON.put("seatsLeft", flight.getSeatsLeft());

            JSONObject planeJson = new JSONObject();
            planeJson.put("capacity", flight.getPlane().getCapacity());
            planeJson.put("model", flight.getPlane().getModel());
            planeJson.put("manufacturer", flight.getPlane().getManufacturer());
            planeJson.put("year", flight.getPlane().getYear());
            flightJSON.put("plane", planeJson);

            List<Passenger> passengerList = flight.getPassengerList();
            if (passengerList != null) {
                System.out.println("Passenger List not Found");
                flightJSON.put("passengers", getPassengerJSON(passengerList));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalJSON;

    }

    public JSONObject getPassengerJSON(List<Passenger> passengerList) {

        JSONObject passengerJSON = new JSONObject();// return this
        JSONArray passengerJSONArray = new JSONArray();

        try {
            for (Passenger passenger : passengerList) {
                JSONObject pObj = new JSONObject();
                pObj.put("id", passenger.getPassengerId());
                pObj.put("firstname", passenger.getFirstName());
                pObj.put("lastname", passenger.getLastName());
                pObj.put("age", passenger.getAge());
                pObj.put("gender", passenger.getGender());
                pObj.put("phone", passenger.getPhone());
                passengerJSONArray.put(pObj);
            }
            passengerJSON.put("passenger", passengerJSONArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return passengerJSON;

    }

}
