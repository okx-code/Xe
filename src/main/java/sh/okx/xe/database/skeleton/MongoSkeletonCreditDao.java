package sh.okx.xe.database.skeleton;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoSkeletonCreditDao implements SkeletonCreditDao {
  private final MongoCollection<Document> collection;
  private final MongoClient client;

  public MongoSkeletonCreditDao(String url, String database, String collection) {
    this.client = MongoClients.create(url);
    this.collection = client.getDatabase(database).getCollection(collection);
  }

  @Override
  public long getCredits(String userId) {
    Document document = collection.find(eq("ID", Long.valueOf(userId))).first();
    if (document == null) {
      return 0;
    }
    return document.get("Credits", Number.class).longValue();
  }

  @Override
  public void saveCredits(String userId, SkeletonBalance balance) {
    collection.updateOne(eq("ID", Long.valueOf(userId)), inc("Credits", balance.getServerBalanceChange()));
  }

  @Override
  public void disable() {
    client.close();
  }
}
