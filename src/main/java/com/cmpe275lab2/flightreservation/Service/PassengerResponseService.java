package com.cmpe275lab2.flightreservation.Service;

import com.cmpe275lab2.flightreservation.Entity.Flight;
import com.cmpe275lab2.flightreservation.Entity.Passenger;
import com.cmpe275lab2.flightreservation.Entity.Reservation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class PassengerResponseService {

    public JSONObject getPassengerJSON(Passenger passenger) {

        JSONObject finalJSON = new JSONObject();
        JSONObject passengerJSON = new JSONObject();

        try {
            finalJSON.put("passenger", passengerJSON);
            passengerJSON.put("id", passenger.getPassengerId());
            passengerJSON.put("firstname", passenger.getFirstName());
            passengerJSON.put("lastname", passenger.getLastName());
            passengerJSON.put("age", passenger.getAge());
            passengerJSON.put("gender", passenger.getGender());
            passengerJSON.put("phone", passenger.getPhone());
            passengerJSON.put("reservations", getReservationJSON(passenger));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalJSON;

    }


    public JSONObject getReservationJSON(Passenger p) {

        JSONObject reservationJSON = new JSONObject();// return this
        JSONArray reservationJSONArray = new JSONArray();
        JSONObject flightJSON = new JSONObject();
        JSONArray flightJSONArray = new JSONArray();

        try {
            for (Reservation r : p.getReservations()) {
                JSONObject reservation = new JSONObject();
                reservation.put("reservationNumber", r.getReservationNumber());
                reservation.put("price", r.getPrice());
                reservation.put("flights", flightJSON);
                for (Flight f : r.getFlights()) {
                    flightJSONArray.put(getFlightJSON(f));
                }
                flightJSON.put("flight", flightJSONArray);
                reservationJSONArray.put(reservation);
            }

            reservationJSON.put("reservation", reservationJSONArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reservationJSON;

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
