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

        settingsService.injectionDeclarationPrefix = form.declarationPrefixTextField.text
        settingsService.injectionNamePrefix = form.namePrefixTextField.text
        settingsService.injectionDeclarationPostfix = form.declarationPostfixTextField.text
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

        form.declarationPrefixTextField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                onChange()
            }
        })
        form.namePrefixTextField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                onChange()
            }
        })

        form.declarationPostfixTextField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                onChange()
            }
        })

        form.separateLinesCheckBox.isSelected = settingsService.separateLines
        form.emptyLineBetweenInjectionsCheckBox.isSelected = settingsService.emptyLineInbetweenInjections
        form.propertyStartsWithCapital.isSelected = settingsService.propertyStartsWithCapital

        form.declarationPrefixTextField.text = settingsService.injectionDeclarationPrefix
        form.namePrefixTextField.text = settingsService.injectionNamePrefix
        form.declarationPostfixTextField.text = settingsService.injectionDeclarationPostfix

        refreshText()

        return form.`$$$getRootComponent$$$`()
    }

    fun onChange()
    {
        SwingUtilities.invokeLater( {
            settingsService.separateLines = form.separateLinesCheckBox.isSelected
            settingsService.emptyLineInbetweenInjections = form.emptyLineBetweenInjectionsCheckBox.isSelected
            settingsService.propertyStartsWithCapital = form.propertyStartsWithCapital.isSelected

            settingsService.injectionDeclarationPrefix = form.declarationPrefixTextField.text
            settingsService.injectionNamePrefix = form.namePrefixTextField.text
            settingsService.injectionDeclarationPostfix = form.declarationPostfixTextField.text
        })

        refreshText()
    }

    fun refreshText() {
        SwingUtilities.invokeLater( {
            var start =  "class Example\n{\n"

            var (fieldName1, injection1) = settingsService.createInjectionText("firstInjection", "\t")
            var (fieldName2, injection2) = settingsService.createInjectionText("secondInjection", "\t")

            start += injection1
            if(settingsService.emptyLineInbetweenInjections)
                start += "\n"
            start += "\n"
            start += injection2

            start += "\n\n"

            var end = "\tvoid Example()\n\t{\n\t\t$fieldName1.Do()\n\t\t$fieldName2.Do()\n\t}\n}"

            form.textArea.text = start + end
        })
    }


}