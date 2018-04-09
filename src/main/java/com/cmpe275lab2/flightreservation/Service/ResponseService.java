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


/*
    public String reservationToJSONString(Reservation reservation){

        JSONObject result = new JSONObject();
        JSONObject container = new JSONObject();
        JSONObject passengerJSON = new JSONObject();
        JSONObject flightsJSON = new JSONObject();
        JSONObject arr[] = new JSONObject[reservation.getFlights().size()];
        int i = 0, price = 0;
        Passenger passenger = reservation.getPassenger();

        System.out.println("inside reservationToJSONString()");
        System.out.println("getReservation() flight size "+reservation.getFlights().size());

        try {
            result.put("reservation", container);

            container.put("orderNumber", ""+reservation.getGenOrderNumber());

            passengerJSON.put("id", ""+passenger.getGenId());
            passengerJSON.put("firstname", passenger.getFirstname());
            passengerJSON.put("lastname", passenger.getLastname());
            passengerJSON.put("age", ""+passenger.getAge());
            passengerJSON.put("gender", passenger.getGender());
            passengerJSON.put("phone", passenger.getPhone());
            container.put("passenger", passengerJSON);

            for(Flight flight : reservation.getFlights()){
                arr[i++] =  flightToJSONString(flight);
                price += flight.getPrice();
                flight.getPassenger().add(passenger);
            }
            container.put("price", ""+price);
            flightsJSON.put("flight", arr);
            container.put("flights", flightsJSON);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result.toString();
    }

    public JSONObject flightToJSONString(Flight flight){
        JSONObject flightJSON = new JSONObject();
        System.out.println("inside flightToJSONString()");

        try {
            System.out.println("inside flightToJSONString() try 1");
            flightJSON.put("number", flight.getNumber());
            flightJSON.put("price", ""+flight.getPrice());
            flightJSON.put("from", flight.getFromSource());
            System.out.println("inside flightToJSONString() try 2");
            flightJSON.put("to", flight.getFromSource());
            flightJSON.put("departureTime", flight.getDepartureTime());
            flightJSON.put("arrivalTime", flight.getArrivalTime());
            flightJSON.put("description", flight.getDescription());
            flightJSON.put("seatsLeft", ""+flight.getSeatsLeft());
            flightJSON.put("plane", planeToJSONString(flight.getPlane()));
        } catch (JSONException e) {
            System.out.println("inside flightToJSONString() catch");
            e.printStackTrace();
        }
        System.out.println("inside flightToJSONString() retruning");
        return flightJSON;
    }

    public JSONObject planeToJSONString(Plane plane){
        JSONObject planeJSON = new JSONObject();

        try {
            planeJSON.put("capacity", ""+plane.getCapacity());
            planeJSON.put("model", plane.getModel());
            planeJSON.put("manufacturer", plane.getManufacturer());
            planeJSON.put("yearOfManufacture", ""+plane.getYearOfManufacture());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return planeJSON;
    }*/

}
