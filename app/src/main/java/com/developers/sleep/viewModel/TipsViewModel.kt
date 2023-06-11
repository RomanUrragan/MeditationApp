import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.developers.sleep.R
import com.developers.sleep.TIPS_LIST
import com.developers.sleep.dataModels.Tip
import java.util.Calendar
import javax.inject.Inject

class TipsViewModel @Inject constructor(
    private val application: Application,
) : AndroidViewModel(application) {

    val tipsList = TIPS_LIST

    private val _tipOfTheDay = MutableLiveData<Tip>()
    val tipOfTheDay: LiveData<Tip>
        get() = _tipOfTheDay

    private val _currentTip = MutableLiveData<Tip>()
    val currentTip: LiveData<Tip>
        get() = _currentTip

    init {
        setDrawableResource()
        updateTipOfTheDay()
    }

    fun setCurrentTip(tip: Tip) {
        _currentTip.value = tip
    }

    private fun updateTipOfTheDay() {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val tipIndex = (dayOfYear - 1) % tipsList.size
        val selectedTip = tipsList[tipIndex]
        val tip = Tip(selectedTip.name, selectedTip.content, selectedTip.drawableRes)
        _tipOfTheDay.value = tip
    }

    private fun setDrawableResource() {
        for (i in tipsList.indices) {
            tipsList[i].drawableRes = getDrawableResource(i)
        }
    }

    private fun getDrawableResource(listIndex: Int): Int {
        val index = listIndex % 4
        return when (index) {
            0 -> R.drawable.rectangle_half_green
            1 -> R.drawable.rectangle_half_blue
            2 -> R.drawable.rectangle_half_yellow
            3 -> R.drawable.rectangle_half_crimson
            else -> R.drawable.rectangle_half_green
        }
    }


}
