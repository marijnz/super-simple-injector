import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.Messages
import com.intellij.ui.DocumentAdapter
import javax.swing.JComponent
import javax.swing.event.DocumentEvent

/**
 * Created by marijnzwemmer on 15/07/17.
 */
class Settings : Configurable
{
    lateinit var form : SettingsForm
    lateinit var settingsService : SettingsService

    var changed : Boolean = false

    override fun isModified(): Boolean {
        return changed
    }

    override fun getDisplayName(): String {
        return "Injector"
    }

    override fun apply() {

        var textArea = form.textArea;

        Messages.showWarningDialog(form.textArea?.text, "Info")
    }

    override fun reset() {
        changed = false
    }

    override fun createComponent(): JComponent? {

        form = SettingsForm()

        form.textArea.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
               changed = true
            }
        })

       // settingsService = SettingsService.getInstance()

        form.textArea.text = getDefaultText()

        return form.`$$$getRootComponent$$$`()
    }


    fun getDefaultText() : String{
        var start =  "class Example\n{\n"

        var end = "\tvoid Example()\n\t{\n\t\tfirstInjection.Do()\n\t\tsecondInjection.Do()\n\t}\n}"

        return start + end
    }


}