/************************************************************
* Name: Taylor Bethke
* Date: Sept 26, 2025
* Assignment: Text Search with Regular Expressions
* Class Number: CIS 217
* Description: Enrollment Search (RegEx)
 ***********************************************************/

// Console program: Searches Enrollment.txt with regex, print matching class line + all associated lines, loop until EXIT.
fun main() {
    printProgramHeader()

    val enrollmentFile = java.io.File("Enrollment.txt")
    if (!enrollmentFile.exists()) {
        println("Error: Enrollment.txt not found in current directory.")
        return
    }

    val lines = enrollmentFile.readLines()
    val blocks = parseClassBlocks(lines)

    val scanner = java.util.Scanner(System.`in`)
    while (true) {
        print("Enter Department (or 'exit' to quit): ")
        val deptInput = scanner.nextLine().trim()
        if (deptInput.equals("EXIT", ignoreCase = true)) {
            println("Goodbye.")
            return
        }
        val dept = deptInput.uppercase()
        if (!dept.matches(Regex("^[A-Z&]{2,6}$"))) {
            println("Invalid department. Use letters (and optional &), 2-6 characters. Try again.")
            continue
        }
        print("Enter Class Number (e.g., 111): ")
        val courseInput = scanner.nextLine().trim()
        if (courseInput.equals("EXIT", ignoreCase = true)) {
            println("Goodbye.")
            return
        }
        if (!courseInput.matches(Regex("^\\d{3}$"))) {
            println("Invalid class number. Use a 3-digit number like 101, 217, 282.")
            continue
        }

        // Find all matching blocks for dept + course
        val matches = blocks.filter { it.dept == dept && it.course == courseInput }

        if (matches.isEmpty()) {
            println()
            println("No classes found for $dept $courseInput.")
            continue
        }

        // Print column header once per search (ensures 3+ lines printed and clear formatting)
        println()
        printResultsHeader()

        // Print all matching blocks, preserving indentation from the file
        matches.forEach { block ->
            block.lines.forEach { println(it) }
        }
    }
}

private data class ClassBlock(
    val dept: String,
    val course: String,
    val lines: List<String>
)

private fun printProgramHeader() {
    println("\nSCC Enrollment Search (Regex) — Fall 2009 Data")
    println("============================================")
}

private fun printResultsHeader() {
    println("FOOT ITEM DEPT COURSE  CLUSTER ADMIN  COURSE          CR     INSTR     BLDG                  START  END   CLASS")
    println("NOTE NUM_ _DIV_ NUM_SECT _ID_  UNIT___TITLE____ _CR_EQUIV ___NAME_____ NUM _ROOM ___DAYS___ _TIME_ _TIME_ _CAP_  _ENR_ OPEN_ FTES")
}

/**
 * Parses the report into logical class blocks.
 * We anchor at line start, allow leading spaces, optionally match a 1-3 digit foot number, then a 4-digit item,
 * capture DEPT (letters or &), capture COURSE (3 digits), and require a 2-letter section code so we don't
 * accidentally match continuation lines.
 * Continuation lines for a class follow immediately after the main line and carry instructor, room, days/times, etc.
 *
 * We collect all subsequent lines until any of these boundaries:
 * - The next main class line (starts a new block)
 * - A page/header boundary (NOTE NUM_, FOOT ITEM, REPORT PERIOD, etc.)
 * - A fully blank line (page break spacing)
 *
 * This ensures we always include at least the immediate next line and, when present, all additional
 * continuation lines, as in the PE 139 example.
 */

private fun parseClassBlocks(lines: List<String>): List<ClassBlock> {
    // Main class line pattern: optional 1-3 digit footnote, 4-digit item, DEPT (letters or &), 3-digit course, 2-letter section code
    val mainLineRegex = Regex("""^\s*(?:\d{1,3}\s+)?\d{4}\s+([A-Z&]{2,6})\s+(\d{3})\s+[A-Z]{2}\b""")

    // Lines that mark the end of a block (do not include in results)
    val endMarkerRegex = Regex(
        pattern = """^\s*(?:\*\s*DEPARTMENT/DIVISION|_{5,}|FRI,\s|REPORT PERIOD:|PAGE\s|TOTAL CLASSES:|NOTE NUM_|FOOT ITEM)\b""",
        options = setOf(RegexOption.IGNORE_CASE)
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
                // If next is a new main line, stop before it
                if (mainLineRegex.containsMatchIn(next)) break
                // If next is an end marker or a hard page/header boundary, stop (don't include)
                if (endMarkerRegex.containsMatchIn(next)) break
                // Stop on fully blank line separating sections/pages
                if (next.isBlank()) break
                // Otherwise this line is associated (continuation, notes, online class, etc.)
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
