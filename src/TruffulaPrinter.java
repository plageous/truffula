import java.io.PrintStream;
import java.util.List;
import java.io.File;

/**
 * TruffulaPrinter is responsible for printing a directory tree structure
 * with optional colored output. It supports sorting files and directories
 * in a case-insensitive manner and cycling through colors for visual clarity.
 */
public class TruffulaPrinter {
  
  /**
   * Configuration options that determine how the tree is printed.
   */
  private TruffulaOptions options;
  
  /**
   * The sequence of colors to use when printing the tree.
   */
  private List<ConsoleColor> colorSequence;
  
  /**
   * The output printer for displaying the tree.
   */
  private ColorPrinter out;

  /**
   * Default color sequence used when no custom colors are provided.
   */
  private static final List<ConsoleColor> DEFAULT_COLOR_SEQUENCE = List.of(
      ConsoleColor.WHITE, ConsoleColor.PURPLE, ConsoleColor.YELLOW
  );

  /**
   * Constructs a TruffulaPrinter with the given options, using the default
   * output stream and the default color sequence.
   *
   * @param options the configuration options for printing the tree
   */
  public TruffulaPrinter(TruffulaOptions options) {
    this(options, System.out, DEFAULT_COLOR_SEQUENCE);
  }

  /**
   * Constructs a TruffulaPrinter with the given options and color sequence,
   * using the default output stream.
   *
   * @param options the configuration options for printing the tree
   * @param colorSequence the sequence of colors to use when printing
   */
  public TruffulaPrinter(TruffulaOptions options, List<ConsoleColor> colorSequence) {
    this(options, System.out, colorSequence);
  }

  /**
   * Constructs a TruffulaPrinter with the given options and output stream,
   * using the default color sequence.
   *
   * @param options the configuration options for printing the tree
   * @param outStream the output stream to print to
   */
  public TruffulaPrinter(TruffulaOptions options, PrintStream outStream) {
    this(options, outStream, DEFAULT_COLOR_SEQUENCE);
  }

  /**
   * Constructs a TruffulaPrinter with the given options, output stream, and color sequence.
   *
   * @param options the configuration options for printing the tree
   * @param outStream the output stream to print to
   * @param colorSequence the sequence of colors to use when printing
   */
  public TruffulaPrinter(TruffulaOptions options, PrintStream outStream, List<ConsoleColor> colorSequence) {
    this.options = options;
    this.colorSequence = colorSequence;
    out = new ColorPrinter(outStream);
  }

  /**
   * WAVE 4: Prints a tree representing the directory structure, with directories and files
   * sorted in a case-insensitive manner. The tree is displayed with 3 spaces of
   * indentation for each directory level.
   * 
   * WAVE 5: If hidden files are not to be shown, then no hidden files/folders will be shown.
   *
   * WAVE 6: If color is enabled, the output cycles through colors at each directory level
   * to visually differentiate them. If color is disabled, all output is displayed in white.
   *
   * WAVE 7: The sorting is case-insensitive. If two files have identical case-insensitive names,
   * they are sorted lexicographically (Cat.png before cat.png).
   *
   * Example Output:
   *
   * myFolder/
   *    Apple.txt
   *    banana.txt
   *    Documents/
   *       images/
   *          Cat.png
   *          cat.png
   *          Dog.png
   *       notes.txt
   *       README.md
   *    zebra.txt
   */
  public void printTree(TruffulaOptions to) {
    printTree(to.getRoot(), 0, to.isShowHidden(), to.isUseColor(), 0);
  }
  
  /**
   * Helper method to printTree. Recursively moves through file trees, printing the name of the files
   * and indenting them based on their file level.
   * 
   * @param file The current file.
   * @param level The current file level. This is relative to the root file.
   * @param showHidden Shows hidden files while true, doesn't when false.
   * @param showColor Adds the default color tags to the beginning of the message if true, doesn't if false.
   * @param colorIndex Current index inside the list of default colors.
   */
  private void printTree(File file, int level, boolean showHidden, boolean showColor, int colorIndex) {
    String output = "";

    // adds a color code to the beginning of the message if printing in color is permitted.
    if (showColor) out.setCurrentColor(DEFAULT_COLOR_SEQUENCE.get(colorIndex));

    // indentation for directory level
    for (int i = 0; i < level ; i++) output += "   ";

    // if file IS hidden
    if (file.isHidden()) {
      // if true ADD file to output
      // else DO NOTHING
      if (showHidden) {
        output += file.getName();
        // directory extension for output if the file is a directory
        if (file.isDirectory()) output += "/";
        out.println(output);
      }
    // if file IS NOT hidden
    } else {
      output += file.getName();
      // directory extension for output if the file is a directory
      if (file.isDirectory()) output += "/";
      out.println(output);
    }

    // recursion
    // increments level by 1 every file going down
    // increments colorIndex by 1, resetting it to 0 if the end of the list is hit
    if (file.isDirectory()) {
      for (File subfile : AlphabeticalFileSorter.sort(file.listFiles())) {
        printTree(subfile, level + 1, showHidden, showColor, (colorIndex + 1) % DEFAULT_COLOR_SEQUENCE.size());
      }  
    }
  }
}
