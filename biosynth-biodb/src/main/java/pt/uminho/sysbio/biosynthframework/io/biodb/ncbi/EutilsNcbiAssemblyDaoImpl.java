package pt.uminho.sysbio.biosynthframework.io.biodb.ncbi;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;

import pt.uminho.sysbio.biosynthframework.biodb.eutils.EntrezDatabase;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EntrezRetmode;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsAssemblyObject;
import pt.uminho.sysbio.biosynthframework.biodb.eutils.EutilsService;
import pt.uminho.sysbio.biosynthframework.io.BiosDao;
import pt.uminho.sysbio.biosynthframework.util.JsonMapUtils;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.client.OkClient;

public class EutilsNcbiAssemblyDaoImpl implements BiosDao<EutilsAssemblyObject> {

  
  private static final Logger logger = LoggerFactory.getLogger(EutilsNcbiAssemblyDaoImpl.class);
  
  private EutilsService service;
  
  public EutilsNcbiAssemblyDaoImpl() {
    String endPoint = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils";
    long connectionTimeout = 60;
    long readTimeout = 60;
    
    final OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setReadTimeout(readTimeout, TimeUnit.SECONDS);
    okHttpClient.setConnectTimeout(connectionTimeout, TimeUnit.SECONDS);
    RestAdapter restAdapter = new RestAdapter.Builder()
                  .setLogLevel(LogLevel.NONE)
                  .setClient(new OkClient(okHttpClient))
                  .setEndpoint(endPoint)
                  .build();
    service = restAdapter.create(EutilsService.class);
  }
  
  @Override
  public EutilsAssemblyObject getByEntry(String entry) {
    Map<String, Object> response = service.esearch(EntrezDatabase.assembly, entry, 10, 0, EntrezRetmode.json);
    List<Object> idlist = JsonMapUtils.getList("idlist", JsonMapUtils.getMap("esearchresult", response));
    if (idlist.size() == 1) {
      String id = (String) idlist.iterator().next();
      Map<String, Object> summary = service.esummary(
          EntrezDatabase.assembly.toString(), id, EntrezRetmode.json);
      Map<String, Object> data = JsonMapUtils.getMap(id, JsonMapUtils.getMap("result", summary));
      ObjectMapper om = new ObjectMapper();
      try {
        EutilsAssemblyObject assembly = om.readValue(om.writeValueAsString(data), EutilsAssemblyObject.class);
        if (entry.equals(assembly.assemblyaccession)) {
          return assembly;
        } else {
          logger.warn("result reject assemble accession does not match. {} -> {}", entry, assembly.assemblyaccession);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (idlist.size() > 1) {
      logger.warn("Results discarded. More than 1");
    }
    
    return null;
  }

  @Override
  public EutilsAssemblyObject getById(long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Long save(EutilsAssemblyObject o) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean delete(EutilsAssemblyObject o) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Set<Long> getAllIds() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<String> getAllEntries() {
    // TODO Auto-generated method stub
    return null;
  }

}
