import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.Messages
import com.intellij.ui.DocumentAdapter
import javax.swing.JComponent
import javax.swing.SwingUtilities
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

        Messages.showWarningDialog(form.textArea?.text, "Info")

        settingsService.injectionPrefix = form.prefixTextField.text
        settingsService.injectionPostfix = form.postfixTextField.text
        settingsService.separateLines = form.separateLinesCheckBox.isSelected
        settingsService.emptyLineInbetweenInjections =form.emptyLineBetweenInjectionsCheckBox.isSelected
    }

    override fun reset() {
        changed = false
    }

    override fun createComponent(): JComponent? {

        form = SettingsForm()
        settingsService = SettingsService.get()

        // Register changes
        form.separateLinesCheckBox.addChangeListener({ onChange() })
        form.emptyLineBetweenInjectionsCheckBox.addChangeListener({ onChange() })
        form.propertyStartsWithCapital.addChangeListener({ onChange() })

        form.prefixTextField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                onChange()
            }
        })
        form.postfixTextField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                onChange()
            }
        })

        form.separateLinesCheckBox.isSelected = settingsService.separateLines
        form.emptyLineBetweenInjectionsCheckBox.isSelected = settingsService.emptyLineInbetweenInjections
        form.propertyStartsWithCapital.isSelected = settingsService.propertyStartsWithCapital

        form.prefixTextField.text = settingsService.injectionPrefix
        form.postfixTextField.text = settingsService.injectionPostfix

        refreshText()

        return form.`$$$getRootComponent$$$`()
    }

    fun onChange()
    {
        SwingUtilities.invokeLater( {
            settingsService.separateLines = form.separateLinesCheckBox.isSelected
            settingsService.emptyLineInbetweenInjections = form.emptyLineBetweenInjectionsCheckBox.isSelected
            settingsService.propertyStartsWithCapital = form.propertyStartsWithCapital.isSelected

            settingsService.injectionPrefix = form.prefixTextField.text
            settingsService.injectionPostfix = form.postfixTextField.text
        })

        refreshText()
    }

    fun refreshText() {
        SwingUtilities.invokeLater( {
            var start =  "class Example\n{\n"

            var injection1 = settingsService.createInjectionText("firstInjection", "\t")
            var injection2 = settingsService.createInjectionText("secondInjection", "\t")

            start += injection1
            if(settingsService.emptyLineInbetweenInjections)
                start += "\n"
            start += "\n"
            start += injection2

            start += "\n\n"

            var end : String

            if(settingsService.propertyStartsWithCapital)
                end = "\tvoid Example()\n\t{\n\t\tFirstInjection.Do()\n\t\tSecondInjection.Do()\n\t}\n}"
            else
                end = "\tvoid Example()\n\t{\n\t\tfirstInjection.Do()\n\t\tsecondInjection.Do()\n\t}\n}"

            form.textArea.text = start + end
        })
    }


}