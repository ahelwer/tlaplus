package tla2sany.parser;

/**
 * This used to be part of ParseException.java but was extracted to restore
 * the ability to auto-generate the files with JavaCC.
 */
public class ParseExceptionExtended extends ParseException {
  /**
   *  Shorter variation of ParseException.getMessage()
   */
  public static String getShortMessage(ParseException e) {
    if (!e.specialConstructor) {
      return e.getMessage();
    }
    int maxSize = 0;
    for (int i = 0; i < e.expectedTokenSequences.length; i++) {
      if (maxSize < e.expectedTokenSequences[i].length) {
        maxSize = e.expectedTokenSequences[i].length;
      }
    }
    String retval = "Encountered \"";
    Token tok = e.currentToken.next;

    for (int i = 0; i < maxSize; i++) {
      if (i != 0) retval += " ";
      if (tok.kind == 0) {
        retval += e.tokenImage[0];
        break;
      }
      retval += tok.image;
      //      retval += add_escapes(tok.image);
      tok = tok.next;
    }
    retval += "\" at line " + e.currentToken.next.beginLine + ", column " + e.currentToken.next.beginColumn
        + " and token \"" + escape(e.currentToken.image != null ? e.currentToken.image : "") + "\" ";
    return retval;
  }
  
  private static String escape(String str) {
      StringBuffer retval = new StringBuffer();
      char ch;
      for (int i = 0; i < str.length(); i++) {
        switch (str.charAt(i))
        {
           case 0 :
              continue;
           case '\b':
              retval.append("\\b");
              continue;
           case '\t':
              retval.append("\\t");
              continue;
           case '\n':
              retval.append("\\n");
              continue;
           case '\f':
              retval.append("\\f");
              continue;
           case '\r':
              retval.append("\\r");
              continue;
           case '\"':
              retval.append("\\\"");
              continue;
           case '\'':
              retval.append("\\\'");
              continue;
           case '\\':
              retval.append("\\\\");
              continue;
           default:
              retval.append(str.charAt(i));
              continue;
        }
      }
      return retval.toString();
   }
}

