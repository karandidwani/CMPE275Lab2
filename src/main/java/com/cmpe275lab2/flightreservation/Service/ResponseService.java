package com.cmpe275lab2.flightreservation.Service;

import com.cmpe275lab2.flightreservation.Entity.Flight;
import com.cmpe275lab2.flightreservation.Entity.Reservation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {

    //Create a Response object for requests which got error or ran successfully
    public JSONObject getResponse(String Response, String code, String message) throws org.json.JSONException {

        //create error message in JSON
        JSONObject errorJSON = new JSONObject();
        // add error attributes
        errorJSON.put("code", code);
        errorJSON.put("msg", message);
        //create parent response JSON
        JSONObject response = new JSONObject();
        //add error JSON in response JSON
        response.put(Response, errorJSON);

        return response;

    }


    public JSONObject getReservationJSON(Reservation reservation) {

        JSONObject finalJSON = new JSONObject();
        JSONObject reservationJSON = new JSONObject();
        JSONObject passengerJSON = new JSONObject();
        JSONObject flightJSON = new JSONObject();
        JSONArray flightJSONArray = new JSONArray();
        int i = 0;

        try {
            finalJSON.put("reservation", reservationJSON);
            reservationJSON.put("reservationNumber", reservation.getReservationNumber());
            reservationJSON.put("price", reservation.getPrice());
            reservationJSON.put("passenger", passengerJSON);
            reservationJSON.put("flights", flightJSON);

            passengerJSON.put("id", reservation.getPassenger().getPassengerId());
            passengerJSON.put("firstname", reservation.getPassenger().getFirstName());
            passengerJSON.put("lastname", reservation.getPassenger().getLastName());
            passengerJSON.put("age", reservation.getPassenger().getAge());
            passengerJSON.put("gender", reservation.getPassenger().getGender());
            passengerJSON.put("phone", reservation.getPassenger().getPhone());

            flightJSON.put("flight", flightJSONArray);
            for (Flight f : reservation.getFlights()) {
                JSONObject fJSON = new JSONObject();
                fJSON.put("number", f.getFlightNumber());
                fJSON.put("price", f.getPrice());
                fJSON.put("origin", f.getFromCity());
                fJSON.put("to", f.getToCity());
                fJSON.put("departureTime", f.getDepartureTime());
                fJSON.put("arrivalTime", f.getArrivalTime());
                fJSON.put("seatsLeft", f.getSeatsLeft());
                fJSON.put("description", f.getDescription());

                JSONObject planeJson = new JSONObject();
                planeJson.put("capacity", f.getPlane().getCapacity());
                planeJson.put("model", f.getPlane().getModel());
                planeJson.put("manufacturer", f.getPlane().getManufacturer());
                planeJson.put("year", f.getPlane().getYear());

                fJSON.put("plane", planeJson);
                flightJSONArray.put(fJSON);
            }

            //flightJSONArray.put("a", );

        } catch (JSONException e) {
            e.printStackTrace();

        }

        System.out.println(finalJSON.toString());

        return finalJSON;

    }

}
