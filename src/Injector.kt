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

            var classIndex = findCurrentClassIndex(editor.document.charsSequence, editor.caretModel.offset)

            // Getting the sub string of the class that contains potentially existing injections
            // This assumes that injections are always first, directly after the class declaration
            var injectionsFrom = doc.text.indexOf('{', classIndex)
            var injectionsTo = doc.text.indexOf("class", injectionsFrom+1)

            if(injectionsTo == -1)
                injectionsTo = doc.textLength;

            var injectionTextBlock = doc.text.substring(injectionsFrom, injectionsTo)

            // Determine where the injection should come
            var lastInjectionIndex = injectionTextBlock.lastIndexOf("[Inject]");

            if(lastInjectionIndex != -1)
                lastInjectionIndex += injectionsFrom;

            var line : Int

            if(lastInjectionIndex == -1){
                // If no injections yet, inject at top
                line = doc.getLineNumber(classIndex)
                whitespacePrefix = getPrefix(doc.text.lines()[line]) + "\t"
                line +=2;

            } else{
                // Otherwise, inject underneath lowest one
                line = doc.getLineNumber(lastInjectionIndex)
                whitespacePrefix =getPrefix(doc.text.lines()[line])
                line++
                if(settings.emptyLineInbetweenInjections)
                    line++
            }

            var text = settings.createInjectionText(word, whitespacePrefix)

            // Check if injection already exists
            if(lastInjectionIndex != -1 && injectionTextBlock.contains(text))
                return;

            //todo this doesn't work out nicely if there's text there already
            if(settings.separateLines)
                line++

            // Check if there's already something at the injection line or one line underneath it,
            // and push down if so.
            if(doc.text.lines()[line].isNotBlank())
                text = "$text\n\n"
            else if(doc.text.lines()[line+1].isNotBlank())
                text = "$text\n"


            val runnable = Runnable {
                // If there's text already - move the text one line down
                if(settings.separateLines && doc.text.lines()[line-1].isNotBlank())
                    doc.insertString(doc.getLineStartOffset(line-1),"\n")

                doc.insertString(doc.getLineStartOffset(line),text)
            }

            WriteCommandAction.runWriteCommandAction(project, runnable)
        }
    }

    // Find the index of the class that the caret position is in.
    // Doesn't support more than one layer deep classes
    //todo ensure classes are actual classes and not vars or comments
    fun findCurrentClassIndex(editorText: CharSequence, caretOffset: Int) : Int
    {
        var textTillCaret = editorText.substring(0, caretOffset)

        // Most common case, there's just one class, keep it simple
        var classCount = textTillCaret.split("class").count()-1

        if(classCount <= 1)
            return textTillCaret.indexOf("class")

        var i = 0
        var currentIndex = 0

        var classIndex = editorText.indexOf("class", currentIndex)

        while(i < classCount+1){
            i++
            var indent = 0
            // Keep looking for either '{', '}' or "class"
            do
            {

                var increment =  editorText.indexOf('{', currentIndex)
                var decrement = editorText.indexOf('}', currentIndex)

                var foundClassIndex =  editorText.indexOf("class", currentIndex)

                if(foundClassIndex != -1
                        && foundClassIndex != classIndex
                        && foundClassIndex < decrement && foundClassIndex < increment){
                    classIndex = foundClassIndex+1
                    currentIndex = foundClassIndex+1
                    break
                }

                if(decrement == -1) decrement = Int.MAX_VALUE
                if(increment == -1) increment = Int.MAX_VALUE

                var foundMinus = decrement < increment
                if(foundMinus){
                    currentIndex = decrement+1
                    indent--
                }
                else{
                    currentIndex = increment+1
                    indent++
                }
            } while(indent != 0)

            if(indent == 0 && currentIndex > caretOffset && caretOffset > classIndex)
                return classIndex-1
        }

        // Something went wrong ({ and } don't match up, for example),
        // Return first class which is better than doing nothing(?)

        return textTillCaret.indexOf("class")
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

