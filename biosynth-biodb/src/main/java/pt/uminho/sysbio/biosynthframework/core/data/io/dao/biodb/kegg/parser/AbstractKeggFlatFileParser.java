package pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.parser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractKeggFlatFileParser {
  protected String flatfile_ = null;
  protected HashMap<Integer, String> tabName_ = null;
  protected HashMap<Integer, String> tabContent_ = null;
  private final String TAB_REGEX = "\n(\\w+)\\s+";
  private final String END_OF_FILE_DELIMITER = "///";

  public AbstractKeggFlatFileParser( String flatfile) {
    this.flatfile_ = flatfile;
  }

  public String getFlatFile() {
    return this.flatfile_;
  }

  protected int getTabIndex( String name) {
    String dcName = name.toLowerCase();
    for ( Integer i : this.tabContent_.keySet()) {
      String tabName = this.tabName_.get(i);
      if (tabName != null && tabName.equals(dcName)) {
        return i;
      }
    }

    return -1;
  }

  protected void parseContent() {
    if ( flatfile_ == null) return;
    this.tabContent_ = new HashMap<Integer, String> ();
    this.tabName_ = new HashMap<Integer, String> ();
    //PARSE FIRST LINE
    String[] header = this.flatfile_.split("\\n")[0].split("\\s+");
    //		System.out.println(Arrays.toString(header));
    tabName_.put(0, header[0].toLowerCase());
    tabContent_.put(0, header[1]);
    //PARSE SECOND TAB TO LAST - 1 (MISSING LAST AND FIRST)


    Pattern tabPattern = Pattern.compile( TAB_REGEX);
    Matcher parser = tabPattern.matcher( this.flatfile_);
    int index = 1;
    int start = 0;
    int end = 0;
    String tabName = null;
    while ( parser.find() ) {
      end = parser.start();
      if ( start != 0) {
        tabName_.put( index, tabName.toLowerCase());
        tabContent_.put( index, this.flatfile_.substring( start, end));
        index++;
      }

      tabName = parser.group( 1);
      start = parser.end();


    }
    //PARSE LAST
    if (tabName != null) {
      tabName_.put( index, tabName.toLowerCase());  
    }
    int endOfFile = this.flatfile_.indexOf( END_OF_FILE_DELIMITER, 0);
    tabContent_.put( index, this.flatfile_.substring( start, --endOfFile));
  }

  public int tabCount() {
    return this.tabContent_.size();
  }

  public String getContent( int index) {
    return this.tabContent_.get( index);
  }

  public List<String> getTabs() {
    return new ArrayList<> (this.tabName_.values());
  }

  public void readFile( String filepath) {

    try {
      FileInputStream fstream = new FileInputStream( filepath);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));

      String readLine;
      StringBuilder sb = new StringBuilder();
      while ((readLine = br.readLine()) != null) {
        sb.append( readLine).append("\n");
      }
      br.close();

      this.flatfile_ = sb.toString();

      this.parseContent();

    } catch ( FileNotFoundException fnfEx) {

    } catch ( IOException ioEx) {

    }
  }
}
