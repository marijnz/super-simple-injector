import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.util.xmlb.XmlSerializerUtil

class SettingsService : PersistentStateComponent<SettingsService.State> {

    class State {
        var value: String? = null
    }

    lateinit var myState: State

    override fun getState(): State? {
        return myState
    }

    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(myState, this);
    }

    companion object Factory{
        fun getInstance(): SettingsService {
            return ServiceManager.getService(SettingsService::class.java)
        }
    }
}