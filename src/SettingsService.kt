import com.intellij.configurationStore.APP_CONFIG
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
        name = "SettingsService", storages = arrayOf(
        Storage(id = "other", file = "$APP_CONFIG$/SimpleInjection.xml"))
)
class SettingsService : PersistentStateComponent<SettingsService> {

    var separateLines = false
    var emptyLineInbetweenInjections = false
    var injectionPrefix = ""
    var injectionPostfix = ";"
    //todo add bool flag for capitalizing first letter of declaration yes/no

    companion object {
        fun get() = ServiceManager.getService(SettingsService::class.java)

    }

    override fun getState(): SettingsService? {
        return this
    }

    override fun loadState(state: SettingsService?) {
        this.separateLines = state?.separateLines ?: false
        this.emptyLineInbetweenInjections = state?.emptyLineInbetweenInjections ?: false
        this.injectionPrefix = state?.injectionPrefix ?: ""
        this.injectionPostfix = state?.injectionPostfix ?: ";"
    }

    fun createInjectionText(injectionName : String, whitespaceOffset : String) : String
    {
        var inject = whitespaceOffset + "[Inject]"

        if(separateLines)
            inject += "\n" + whitespaceOffset
        else
            inject += " "

        var titleCase = capitalizeFirstLetter(injectionName)
        var injectText = "$titleCase $injectionName"

        inject += "$injectionPrefix$injectText$injectionPostfix"

        return inject
    }


    fun capitalizeFirstLetter(s: String): String {
        if (s.count() == 1)
            return s.toUpperCase()
        return s[0].toUpperCase() + s.substring(1)
    }
}


