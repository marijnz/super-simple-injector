import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.TextRange

class Injector : AnAction() {

    override fun actionPerformed(ae: AnActionEvent) {

        val editor = ae.getData(DataKeys.EDITOR)
        val project = ae.getData(DataKeys.PROJECT)
        val doc = editor!!.document

        val wordRange = getWordAtCaret(editor.document.charsSequence, editor.caretModel.offset)

        if (wordRange != null)
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

            var foundExistingInjections = lastInjectionIndex != -1;

            if(foundExistingInjections)
                lastInjectionIndex += injectionsFrom;

            var line : Int

            if(!foundExistingInjections){
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

            var word = editor.document.charsSequence.
                    subSequence(wordRange.startOffset, wordRange.endOffset).toString()

            var (fieldName, injectionText) = settings.createInjectionText(word, whitespacePrefix)

            // Check if injection already exists
            if(lastInjectionIndex != -1 && injectionTextBlock.contains(injectionText))
                return;

            //todo this doesn't work out nicely if there's text there already
            if(settings.separateLines)
                line++

            // Check if there's already something at the injection line or one line underneath it,
            // and push down if so.
            if(doc.text.lines()[line].isNotBlank())
                injectionText = "$injectionText\n\n"
            else if(doc.text.lines()[line+1].isNotBlank())
                injectionText = "$injectionText\n"


            val runnable = Runnable {

                // If there's text already - move the text one line down
                if(!foundExistingInjections && settings.separateLines && doc.text.lines()[line-1].isNotBlank())
                    doc.insertString(doc.getLineStartOffset(line-1),"\n")

                // Replace the reference name with the applied settings (capitalizing, prefix)
                doc.replaceString(wordRange.startOffset, wordRange.endOffset, fieldName)

                doc.insertString(doc.getLineStartOffset(line),injectionText)
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

        var firstClassIndex = textTillCaret.indexOf("class")

        if(classCount <= 1)
            return firstClassIndex

        var currentIndex = 0

        var classIndex = editorText.indexOf("class", currentIndex)

        var prevClassIndex = 0;

        var atClass = 0
        while(atClass < classCount){
            var indent = 0
            var classIndent = 0
            // Keep looking for either '{', '}' or "class"

            // Avoid infinite loop here in case brackets don't match up
            var k = 0
            while(k++ < 100)
            {
                var increment =  editorText.indexOf('{', currentIndex)
                var decrement = editorText.indexOf('}', currentIndex)

                var foundClassIndex =  editorText.indexOf("class", currentIndex)

                var foundClass = false
                // See if we found a class - otherwise look for brackets
                // and see if we hit the end of our current class
                if(foundClassIndex != -1
                        && foundClassIndex != classIndex
                        && foundClassIndex < decrement && foundClassIndex < increment){
                    prevClassIndex = classIndex;
                    classIndex = foundClassIndex+1
                    currentIndex = foundClassIndex+1
                    classIndent = 0
                    foundClass = true
                    atClass++
                }
                else{
                    if(decrement == -1) decrement = Int.MAX_VALUE
                    if(increment == -1) increment = Int.MAX_VALUE

                    var foundMinus = decrement < increment
                    if(foundMinus){
                        currentIndex = decrement+1
                        indent--
                        classIndent--
                    }
                    else{
                        currentIndex = increment+1
                        indent++
                        classIndent++
                    }
                }

                // indent is zero, so we hit the end of a class
                if(classIndent == 0 || foundClass){
                    if(currentIndex > caretOffset /* && caretOffset > classIndex*/)
                        return classIndex-1
                    else if(!foundClass)
                        classIndex = prevClassIndex // We're back to our prev class (because only one class layer supported)
                    break
                }


            }
        }

        // Something went wrong ({ and } don't match up, for example),
        // Return first class which is better than doing nothing(?)

        return firstClassIndex
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
    fun getWordAtCaret(editorText: CharSequence, caretOffset: Int): TextRange? {
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

            return TextRange(start, end)
        }

        return null
    }
}

