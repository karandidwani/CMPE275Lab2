package com.cmpe275lab2.flightreservation.Service;

import com.cmpe275lab2.flightreservation.Entity.Passenger;
import com.cmpe275lab2.flightreservation.Repository.PassengerRepository;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private ResponseService responseService;

    public List<Passenger> getAllPassengers() {
        return (List<Passenger>) passengerRepository.findAll();
    }

    public ResponseEntity<?> createPassenger(String firstName, String lastName, int age, String gender, String phone) {

        Passenger passenger = new Passenger(firstName, lastName, age, gender, phone, null);

        passengerRepository.save(passenger);

        Passenger passengerCreated = passengerRepository.findByPhone(phone);

        int id = passengerCreated.getPassengerId();

        return getPassenger(id, "JSON");

    }

    public ResponseEntity<?> getPassenger(int id, String format) {

        if (id != 0) {

            Passenger passenger = passengerRepository.findFirstByPassengerId(id);
            HttpHeaders httpHeaders = new HttpHeaders();

            if (passenger != null) {
                if (format.equals("JSON")) {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    return new ResponseEntity<>(passenger, httpHeaders, HttpStatus.OK);
                } else {
                    httpHeaders.setContentType(MediaType.APPLICATION_XML);
                    return new ResponseEntity<>(passenger, httpHeaders, HttpStatus.OK);
                }
            } else {
                try {
                    return new ResponseEntity<>(responseService.getResponse("BadRequest", "400", "No Passenger details are found for Id " + id + "").toString(), HttpStatus.NOT_FOUND);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(e, HttpStatus.BAD_GATEWAY);
                }
            }


        }

        try {
            return new ResponseEntity<>(responseService.getResponse("BadRequest", "404", " Invalid data, please provide proper Id ").toString(), HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e, HttpStatus.BAD_GATEWAY);
        }

    }
}
