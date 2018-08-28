package pt.uminho.sysbio.biosynthframework.io.biodb.modelseed;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import pt.uminho.sysbio.biosynthframework.biodb.modelseed.ModelSeedSubsystem;

public class JsonModelSeedSubsystemDao {
  
  private static final Logger logger = LoggerFactory.getLogger(JsonModelSeedSubsystemDao.class);
  
  public Map<String, ModelSeedSubsystem> data = new HashMap<> ();

  public JsonModelSeedSubsystemDao(Resource subsystemsJson) {
    ObjectMapper m = new ObjectMapper();
    m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    CollectionType ctype = m.getTypeFactory()
                            .constructCollectionType(List.class, 
                                                     ModelSeedSubsystem.class);
    
    try {
      List<ModelSeedSubsystem> subsystems = m.readValue(subsystemsJson.getInputStream(), ctype);
      for (ModelSeedSubsystem subsystem : subsystems) {
        if (subsystem != null && subsystem.id != null && !subsystem.id.trim().isEmpty()) {
          if (data.put(subsystem.id, subsystem) != null) {
            logger.warn("duplicate ID - {}", subsystem.id);
          }
        } else {
          logger.warn("invalid record - {}", subsystem);
        }
      }
    } catch (IOException e) {
      logger.error("IO Error: {}", e.getMessage());
    }
  }
}
