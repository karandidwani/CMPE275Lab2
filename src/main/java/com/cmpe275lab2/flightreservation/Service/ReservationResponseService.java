package com.cmpe275lab2.flightreservation.Service;

import com.cmpe275lab2.flightreservation.Entity.Flight;
import com.cmpe275lab2.flightreservation.Entity.Passenger;
import com.cmpe275lab2.flightreservation.Entity.Reservation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ReservationResponseService {


    public JSONObject getReservationJSON(Reservation reservation) {

        JSONObject finalJSON = new JSONObject();
        JSONObject reservationJSON = new JSONObject();
        JSONObject flightJSON = new JSONObject();
        JSONArray flightJSONArray = new JSONArray();

        try {
            finalJSON.put("reservation", reservationJSON);
            reservationJSON.put("reservationNumber", reservation.getReservationNumber());
            reservationJSON.put("price", reservation.getPrice());
            reservationJSON.put("passenger", getPassengerJSON(reservation.getPassenger()));

            for (Flight f : reservation.getFlights()) {
                flightJSONArray.put(getFlightJSON(f));
            }

            reservationJSON.put("flights", flightJSON);
            flightJSON.put("flight", flightJSONArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(finalJSON.toString());
        return finalJSON;

    }

    public JSONObject getPassengerJSON(Passenger p) {

        JSONObject passengerJSON = new JSONObject();

        try {
            passengerJSON.put("id", p.getPassengerId());
            passengerJSON.put("firstname", p.getFirstName());
            passengerJSON.put("lastname", p.getLastName());
            passengerJSON.put("age", p.getAge());
            passengerJSON.put("gender", p.getGender());
            passengerJSON.put("phone", p.getPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return passengerJSON;

    }

    public JSONObject getFlightJSON(Flight f) {

        JSONObject fJSON = new JSONObject();

        try {
            fJSON.put("number", f.getFlightNumber());
            fJSON.put("price", f.getPrice());
            fJSON.put("origin", f.getFromCity());
            fJSON.put("to", f.getToCity());
            fJSON.put("departureTime", f.getDepartureTime());
            fJSON.put("arrivalTime", f.getArrivalTime());
            fJSON.put("seatsLeft", f.getSeatsLeft());
            fJSON.put("description", f.getDescription());
            //get plane object in JSON
            JSONObject planeJson = new JSONObject();
            planeJson.put("capacity", f.getPlane().getCapacity());
            planeJson.put("model", f.getPlane().getModel());
            planeJson.put("manufacturer", f.getPlane().getManufacturer());
            planeJson.put("year", f.getPlane().getYear());
            fJSON.put("plane", planeJson);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fJSON;

    }
}
