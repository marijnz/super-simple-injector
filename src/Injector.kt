import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.command.WriteCommandAction

class Injector : AnAction() {

    override fun actionPerformed(ae: AnActionEvent) {


        val editor = ae.getData(DataKeys.EDITOR)
        val project = ae.getData(DataKeys.PROJECT)
        val doc = editor!!.document

        val word = getWordAtCaret(editor.document.charsSequence, editor.caretModel.offset)

        if (word != null)
        {
            var settings = SettingsService.get()

            var whitespacePrefix = ""

            var titleCase = capitalizeFirstLetter(word);

            var injectText = "[Inject] $titleCase $word;";


            // Look backwards from current selection to ensure we look at the current class
            // (in case there's multiple classes in a file)
            var lastInjectIndex = doc.text.lastIndexOf("[Inject]", editor.caretModel.offset)
            var lastClassIndex = doc.text.lastIndexOf("class", editor.caretModel.offset)

            var line = -1

            if(lastInjectIndex == -1 || lastInjectIndex < lastClassIndex){
                // If no injections yet, inject at top
                line = doc.getLineNumber(lastClassIndex)
                whitespacePrefix = getPrefix(doc.text.lines()[line]) + "\t"
                line +=2;

            } else{
                // Otherwise, inject undeneath lowest one
                line = doc.getLineNumber(lastInjectIndex)
                whitespacePrefix =getPrefix(doc.text.lines()[line])
                line++
                if(settings.emptyLineInbetweenInjections)
                    line++
            }

            if(settings.separateLines)
                line++

            var text = settings.createInjectionText(word, whitespacePrefix)

            if(doc.text.indexOf(text) != -1)
                return;

            // Check if there's already something at the injection line or one line underneath it,
            // and push down if so.
            if(doc.text.lines()[line].isNotBlank())
                text = "$text\n\n"
            else if(doc.text.lines()[line+1].isNotBlank())
                text = "$text\n"


            val runnable = Runnable {
                doc.insertString(doc.getLineStartOffset(line),text)
            }

            WriteCommandAction.runWriteCommandAction(project, runnable)
        }
    }

    fun getPrefix(line: String) : String {
        var index= getIndexOfFirstNonWhitespaceChar(line)
        return line.substring(0, index);
    }

    fun getIndexOfFirstNonWhitespaceChar(s: String) : Int {
        for (i in s.indices)
            if (!s[i].isWhitespace()) return i;
        return -1;
    }

    // todo put in utility
    fun capitalizeFirstLetter(s: String): String {
        if (s.count() == 1)
            return s.toUpperCase()
        return s[0].toUpperCase() + s.substring(1)
    }

    // From Rider
    fun getWordAtCaret(editorText: CharSequence, caretOffset: Int): String? {
        var caretOffset = caretOffset

        if (editorText.isEmpty()) return null

        if (caretOffset > 0 && !Character.isJavaIdentifierPart(editorText[caretOffset])
                && Character.isJavaIdentifierPart(editorText[caretOffset - 1])) {
            caretOffset--
        }

        if (Character.isJavaIdentifierPart(editorText[caretOffset])) {
            var start = caretOffset
            var end = caretOffset

            while (start > 0 && Character.isJavaIdentifierPart(editorText[start - 1])) {
                start--
            }

            while (end < editorText.length && Character.isJavaIdentifierPart(editorText[end])) {
                end++
            }

            return editorText.subSequence(start, end).toString()
        }

        return null
    }
}
