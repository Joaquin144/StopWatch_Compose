import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class StopWatch {
    var formattedTime by mutableStateOf("00:00:000")
    //In desktop we don't have lifecyle like in Adnroid so we also don't have cncept of viewModels. We directly launch our coroutines in Application scope and cancel them if necessary.
    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var isActive = false

    private var timeMillis = 0L
    private var lastTimestamp = 0L

    fun start(){
        if(isActive)    return
        coroutineScope.launch {
            lastTimestamp = System.currentTimeMillis()
            this@StopWatch.isActive = true//corutineScope has its own varibale isActive but we want to change our custom created variable so we use this@StopWatch
            while(this@StopWatch.isActive){
                delay(10L)
                timeMillis += System.currentTimeMillis() - lastTimestamp
                lastTimestamp = System.currentTimeMillis()
                formattedTime = formatTime(timeMillis)
            }
        }
    }

    fun pause(){
        isActive = false//It will cause the while loop inside start function to stop
    }

    fun reset(){
        coroutineScope.cancel()
        //Important Note: Whenever we cancel any coroutine we need to reassign it to some Dispatcher if we want to use it again otherwise it won't launch
        coroutineScope = CoroutineScope(Dispatchers.Main)
        timeMillis = 0L
        lastTimestamp = 0L
        formattedTime = "00:00:000"
        isActive = false
    }

    //E! --> Each line of this function body
    private fun formatTime(timeMillis: Long): String{
        //This function takes time in milliseconds [Long] and returns the formatted time as String
        val localDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timeMillis),
            ZoneId.systemDefault()
        )
        val formatter = DateTimeFormatter.ofPattern(
            "mm:ss:SSS",
            Locale.getDefault()
        )
        return localDateTime.format(formatter)
    }
}