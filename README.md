# GrepKit

Console Kotlin program to search `Enrollment.txt` with regular expressions and print the matching class line plus all associated continuation lines. Loops until the user enters `EXIT`.

## How to run

Option A — IntelliJ IDEA (recommended):
- Open this project in IntelliJ.
- Ensure `Enrollment.txt` is at the project root (same directory you run from).
- Open `src/main/kotlin/Main.kt` and run the `main` function.

Option B — Kotlin CLI (if installed):
- From the project root (Windows cmd):

```cmd
mkdir build 2>NUL
kotlinc src\main\kotlin\Main.kt -include-runtime -d build\RegEx-TextSearch.jar
java -jar build\RegEx-TextSearch.jar
```

## Usage
- When prompted, enter a Department abbreviation (e.g., `CIS`) or `EXIT` to quit.
- Then enter a 3-digit Class Number (e.g., `282`).
- The program prints the two header rows for readability, then prints the main line that matches and all associated continuation lines from `Enrollment.txt`.

Example:
```
Enter Department (or type EXIT to quit): CIS
Enter Class Number (3 digits, e.g., 139): 282

FOOT ITEM DEPT COURSE  CLUSTER ADMIN  COURSE          CR     INSTR     BLDG                  START  END   CLASS
NOTE NUM_ _DIV_ NUM_SECT _ID_  UNIT___TITLE____ _CR_EQUIV ___NAME_____ NUM _ROOM ___DAYS___ _TIME_ _TIME_ _CAP_  _ENR_ OPEN_ FTES
     2007 CIS   282  AN        HD  PROG PRN I    5.0  5.0 Hybrid Class 001 1116  F          ARR    ARR 
                                                          Jones D      001 1116  MTWTh      10:30A 11:30A    24     15     9  5.0 
```

Extra credit: If a class has three or more associated lines, the program prints all of them. For example, `PE 139` returns all continuation lines for each matching section.

## Grading rubric coverage
- Indenting and Header (5 pts):
  - Prints a program header once and the two-column header lines above each result set. Original line indentation is preserved.
- Asks for Department and Class Number (10 pts):
  - Prompts for both; accepts `EXIT` to quit.
- Finds and prints line with text found (25 pts):
  - Uses regex to locate the main line containing both Department and Class Number.
- Prints second line that is associated (45 pts):
  - Prints all continuation lines directly following the main line; the immediate next line is always included when present.
- Loops for more input and searching until "EXIT" is typed by user (15 pts):
  - Continues prompting until `EXIT`.
- 3 lines or more printed (10 pts):
  - The two header lines plus at least the main line ensure >= 3 lines. Many classes also include multiple continuation lines.
- Extra Credit (10 pts):
  - Prints all lines associated with a class block (e.g., `PE 139`).

## Notes
- The program never modifies `Enrollment.txt`; it only reads it from the current working directory.
- Run the program from the project root so it can find `Enrollment.txt`.


## Author

**Taylor Bethke** — [GitHub (@TheSixEyes)](https://github.com/TheSixEyes)
