
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.regex.Pattern

/**
 * Acuity Function Documentation
 *
 * GetAppointmentList(LocalDate) generates a Array of the appointment on a given day.
 *
 * GetAppointment(LocalDate, String) generates a Appointment object of a given day and email/phone number.
 *
 * PrintInfo(Appointment) will print the necessary information of the appointment such as:
 * ID
 * Name
 * Email
 * Phone
 * Date
 * Time
 * Check-In Status
 *
 * ConfirmCheckIn(Appointment) sets the Check-In status to true.
 *
 * SaveListToJSON(Array<Appointment>) saves the list of appointments to a json file.
 *
 * LoadJSONFIle(LocalDate) loads the appointment list from a JSON file to an Array of Appointment.
 *
 *LoadAppointmentList(LocalDate) loads the activates LoadJSONFile(LocalDate) if a JSON file of that date is available, otherwise, it loads the appointment list from the API.
 *
 * checkEmail(String) and checkPhoneNum(String) is used to check if the email/phone number enter is valid.
 */


class Acuity{
    fun GetAppointmentList(date: Calendar): Array<Appointment> {
        val mapper = ObjectMapper()
        var maxMonth = date.get(Calendar.MONTH)+1;
        var maxYear = date.get(Calendar.YEAR)
        if(maxMonth == 12){maxMonth = 0; maxYear++}
        return try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://acuityscheduling.com/api/v1/appointments?max=100&minDate=${date.get(Calendar.MONTH)+1}%2F${date.get(Calendar.DATE)}%2F${date.get(Calendar.YEAR)}&maxDate=${maxMonth+1}%2F${10}%2F${maxYear}&canceled=false&excludeForms=true&direction=DESC")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("authorization", "Basic MjgxMzM5MzU6NGUwY2Q5ZmY3MWM5NjU0YTM2MDZiMjViZTg5YmMxMGI=")
                .build()

            val response = client.newCall(request).execute();
            val json = response.body?.string()
            mapper.readValue(json, Array<Appointment>::class.java)
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    fun GetAppointment(date: Calendar, user: String): Appointment {
        val mapper = ObjectMapper()
        var req = ""
        var maxMonth = date.get(Calendar.MONTH)+1;
        var maxYear = date.get(Calendar.YEAR)
        if(maxMonth == 12){maxMonth = 0; maxYear++}
        if (checkEmail(user)) {
            req = "https://acuityscheduling.com/api/v1/appointments?max=1&minDate=${date.get(Calendar.MONTH)+1}%2F${date.get(Calendar.DATE)}%2F${date.get(Calendar.YEAR)}&maxDate=${maxMonth+1}%2F${10}%2F${maxYear}&canceled=false&email=${user}&excludeForms=true&direction=DESC"
        } else if (checkPhoneNum(user)) {
            req = "https://acuityscheduling.com/api/v1/appointments?max=1&minDate=${date.get(Calendar.MONTH)+1}%2F${date.get(Calendar.DATE)}%2F${date.get(Calendar.YEAR)}&maxDate=${maxMonth+1}%2F${10}%2F${maxYear}&canceled=false&phone=${user}&excludeForms=true&direction=DESC"
        }
        if(req=="")
        {
            return Appointment()
        }
        return try {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(req)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("authorization", "Basic MjgxMzM5MzU6NGUwY2Q5ZmY3MWM5NjU0YTM2MDZiMjViZTg5YmMxMGI=")
                .build()

            val response = client.newCall(request).execute()
            val json = response.body?.string()
            val app: Array<Appointment> = mapper.readValue(json, Array<Appointment>::class.java)
            if (app.isNotEmpty()) {
                return app[0]
            } else {
                return Appointment()
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    fun ConfirmCheckIn(appointment: Appointment) {
        appointment.checkedIn = true
    }

    fun toString(appointment: Appointment): String {

           return """ID: ${appointment.id}
Name: ${appointment.firstName} ${appointment.lastName}
Email: ${appointment.email}
Phone: ${appointment.phone}
Date: ${appointment.date}
Time: ${appointment.time}"""

    }


    fun SaveListToJSON(appointments: Array<Appointment>, date: Calendar){
        val mapper = ObjectMapper()
        val saveDataJSON = mapper.writeValueAsString(appointments)
        File("savedata_$date.json").printWriter().use { out -> out.println(saveDataJSON) }
    }

    fun LoadJSONFile(date: Calendar): Array<Appointment>{
        val mapper = ObjectMapper()
        return mapper.readValue(File("savedata_$date.json"),Array<Appointment>::class.java)
    }

    //Create a JSON file for a certain appointment date
    fun LoadAppointmentList(date: Calendar): Array<Appointment>{
        val mapper = ObjectMapper()
        if(File("savedata_$date.json").canRead())
        {
            return LoadJSONFile(date)
        }
        else
        {
            return GetAppointmentList(date)
        }
    }

    private fun checkEmail(email: String?): Boolean {
        val pattern = Pattern.compile("^(.+)@(\\S+)$")
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun checkPhoneNum(phoneNum: String?): Boolean {
        val pattern = Pattern.compile("^(\\d{3}[- .]?){2}\\d{4}$")
        val matcher = pattern.matcher(phoneNum)
        return matcher.matches()
    }
}

