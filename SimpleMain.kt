import java.io.File
import java.util.Scanner

fun main() {
    kotlin.io.println("\nSCC Enrollment Search (Regex) — Fall 2009 Data")
    kotlin.io.println("============================================")

    val enrollmentFile = File("Enrollment.txt")
    if (!enrollmentFile.exists()) {
        kotlin.io.println("Error: Enrollment.txt not found in current directory.")
        return
    }

    val lines = enrollmentFile.readLines()
    val blocks = parseClassBlocks(lines)

    val scanner = Scanner(System.`in`)
    while (true) {
        kotlin.io.println()
        kotlin.io.print("Enter Department: ")
        val deptInput = scanner.nextLine().trim()
        if (deptInput.equals("EXIT", ignoreCase = true)) {
            kotlin.io.println("Goodbye.")
            return
        }
        val dept = deptInput.uppercase()
        if (!dept.matches(kotlin.text.Regex("^[A-Z&]{2,6}$"))) {
            kotlin.io.println("Invalid department. Use letters (and optional &), 2-6 characters. Try again.")
            continue
        }

        kotlin.io.print("Enter Class Number: ")
        val courseInput = scanner.nextLine().trim()
        if (courseInput.equals("EXIT", ignoreCase = true)) {
            kotlin.io.println("Goodbye.")
            return
        }
        if (!courseInput.matches(kotlin.text.Regex("^\\d{3}$"))) {
            kotlin.io.println("Invalid class number. Use a 3-digit number like 101, 217, 282.")
            continue
        }

        val matches = blocks.filter { it.dept == dept && it.course == courseInput }

        if (matches.isEmpty()) {
            kotlin.io.println()
            kotlin.io.println("No classes found for $dept $courseInput.")
            continue
        }

        kotlin.io.println()
        kotlin.io.println("FOOT ITEM DEPT COURSE  CLUSTER ADMIN  COURSE          CR     INSTR     BLDG                  START  END   CLASS")
        kotlin.io.println("NOTE NUM_ _DIV_ NUM_SECT _ID_  UNIT___TITLE____ _CR_EQUIV ___NAME_____ NUM _ROOM ___DAYS___ _TIME_ _TIME_ _CAP_  _ENR_ OPEN_ FTES")

        matches.forEach { block ->
            block.lines.forEach { kotlin.io.println(it) }
        }
    }
}

data class ClassBlock(
    val dept: String,
    val course: String,
    val lines: List<String>
)

fun parseClassBlocks(lines: List<String>): List<ClassBlock> {
    val mainLineRegex = kotlin.text.Regex("""^\s*(?:\d{1,3}\s+)?\d{4}\s+([A-Z&]{2,6})\s+(\d{3})\s+[A-Z]{2}\b""")
    val endMarkerRegex = kotlin.text.Regex(
        pattern = """^\s*(?:\*\s*DEPARTMENT/DIVISION|_{5,}|FRI,\s|REPORT PERIOD:|PAGE\s|TOTAL CLASSES:|NOTE NUM_|FOOT ITEM)\b""",
        options = setOf(kotlin.text.RegexOption.IGNORE_CASE)
    )

    val result = mutableListOf<ClassBlock>()
    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        val m = mainLineRegex.find(line)
        if (m != null) {
            val dept = m.groupValues[1]
            val course = m.groupValues[2]
            val blockLines = mutableListOf(line)
            var j = i + 1
            while (j < lines.size) {
                val next = lines[j]
                if (mainLineRegex.containsMatchIn(next)) break
                if (endMarkerRegex.containsMatchIn(next)) break
                if (next.isBlank()) break
                blockLines.add(next)
                j++
            }
            result.add(ClassBlock(dept = dept, course = course, lines = blockLines))
            i = j
        } else {
            i++
        }
    }
    return result
}
