package pt.uminho.sysbio.biosynthframework.integration.literature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pt.uminho.sysbio.biosynthframework.SupplementaryMaterialEntity;

public class SpringerLinkScanner implements Function<String, List<SupplementaryMaterialEntity>> {

  @Override
  public List<SupplementaryMaterialEntity> apply(String page) {
    List<SupplementaryMaterialEntity> links = new ArrayList<> ();
    Document document = Jsoup.parse(page);
    Elements elements = document.getElementsByClass("filename");
    for (Element a : elements) {
      if (a.tagName() == "a") {
        String href = a.attr("href");
        SupplementaryMaterialEntity sup = new SupplementaryMaterialEntity();
        sup.setUrl(href);
        links.add(sup);
      }
    }
    
    return links;
  }

}
