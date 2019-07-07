import com.intellij.configurationStore.APP_CONFIG
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
        name = "SettingsService", storages = arrayOf(
        Storage(file = "$APP_CONFIG$/SimpleInjection.xml"))
)
class SettingsService : PersistentStateComponent<SettingsService> {

    var separateLines = false
    var emptyLineInbetweenInjections = false
    var injectionDeclarationPrefix = ""
    var injectionNamePrefix = ""
    var injectionDeclarationPostfix = ";"
    var propertyStartsWithCapital = false;

    //todo add bool flag for capitalizing first letter of declaration yes/no

    companion object {
        fun get() = ServiceManager.getService(SettingsService::class.java)

    }

    override fun getState(): SettingsService? {
        return this
    }

    override fun loadState(state: SettingsService) {
        this.separateLines = state.separateLines
        this.emptyLineInbetweenInjections = state.emptyLineInbetweenInjections
        this.propertyStartsWithCapital = state.propertyStartsWithCapital
        this.injectionDeclarationPrefix = state.injectionDeclarationPrefix
        this.injectionNamePrefix = state.injectionNamePrefix
        this.injectionDeclarationPostfix = state.injectionDeclarationPostfix
    }

    fun createInjectionText(propertyName: String, whitespaceOffset : String) : Pair<String, String>
    {
        var inject = whitespaceOffset + "[Inject]"

        if(separateLines)
            inject += "\n" + whitespaceOffset
        else
            inject += " "

        var propertyNameRulesApplied : String

        if(propertyStartsWithCapital)
            propertyNameRulesApplied = capitalizeFirstLetter(propertyName)
        else
            propertyNameRulesApplied = deCapitalizeFirstLetter(propertyName)

        var startsWithPrefix = injectionNamePrefix.isNotEmpty() && propertyName.startsWith(injectionNamePrefix)
        // Only add if the the name hasn't been written with the prefix in mind
        if(!startsWithPrefix)
            propertyNameRulesApplied = "$injectionNamePrefix$propertyNameRulesApplied"

        var className = propertyName

        if(startsWithPrefix)
            className = className.removePrefix(injectionNamePrefix)

        className = capitalizeFirstLetter(className)

        var injectText = "$className $propertyNameRulesApplied"

        inject += "$injectionDeclarationPrefix$injectText$injectionDeclarationPostfix"

        return Pair(propertyNameRulesApplied, inject)
    }

    fun capitalizeFirstLetter(s: String): String {
        return s[0].toUpperCase() + s.substring(1)
    }

    fun deCapitalizeFirstLetter(s: String): String {
        return s[0].toLowerCase() + s.substring(1)
    }
}


