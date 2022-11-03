package com.bussin.SpringBack.utils;

import com.bussin.SpringBack.exception.DriverNotFoundException;
import com.bussin.SpringBack.exception.WrongDriverException;
import com.bussin.SpringBack.exception.WrongUserException;
import com.bussin.SpringBack.models.user.User;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Slf4j
@UtilityClass
public class NotAuthorizedUtil {

    /**
     * Check if the User querying for the method is the same user using UserID
     * @param userID The UUID of the User to be accessed
     */
    public static void isSameUserId(UUID userID) {
        User loggedIn =
                (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!loggedIn.getId().toString().equals(userID.toString())){
            wrongUserResponse(loggedIn.getId().toString(), userID.toString());
        }
    }

    /**
     * Check if the User querying for the method is the same user using Email
     * @param email The email of the User to be accessed
     */
    public static void isSameUserEmail(String email) {
        User loggedIn =
                (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!loggedIn.getEmail().equals(email)){
            wrongUserResponse(loggedIn.getEmail(), email);
        }
    }

    /**
     * Throw new WrongUserException when User is not the same
     * @param loggedIn The UUID of User
     * @param attempted The UUID of the User to be accessed
     */
    private static void wrongUserResponse(String loggedIn, String attempted) {
        String response = String.format("Attempted modification of another user! " +
                "%s tried to modify %s", loggedIn, attempted);
        log.warn(response);
        throw new WrongUserException(response);
    }

    /**
     * Check if the Driver querying for the method is the same Driver using car plate
     * @param carPlate Car plate of the Driver to be accessed
     */
    public static void isSameDriver(String carPlate) {
        User loggedIn =
                (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loggedIn.getDriver() == null) {
            throw new DriverNotFoundException("No car plate found with " + carPlate);
        } else if(!loggedIn.getDriver().getCarPlate().equals(carPlate)) {
            wrongDriverResponse(loggedIn.getDriver().getCarPlate(), carPlate);
        }
    }

    /**
     * Throw new WrongDriverException when Driver is not the same
     * @param loggedIn Car plate of the Driver
     * @param attempted Car plate of the Driver to be accessed
     */
    private static void wrongDriverResponse(String loggedIn, String attempted) {
        String response = String.format("Attempted modification of another driver! " +
                "%s tried to modify %s", loggedIn, attempted);
        log.warn(response);
        throw new WrongDriverException(response);
    }
}
