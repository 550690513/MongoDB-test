package com.mongo.test;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.Set;

/**
 * 特别注意：
 * mongodb开启权限认证启动后，若使用本测试类一直报Unauthorized认证失败错，
 * 别忘了修改mongodb的默认认证机制为MONGODB-CR
 * 具体请看我的另一篇博文：https://blog.csdn.net/sanpangouba/article/details/78953556
 *
 * @author Cheung
 * @date 2017/12/29
 */
public class MongoConnTest {

	private static final String MONGO_HOST = "localhost";
	private static final Integer MONGO_PORT = 27017;
	private static final String MONGO_DB_NAME = "test";
	private static final String MONGO_USERNAME = "tester";
	private static final String MONGO_PASSWORD = "123456";
	private static final String MONGO_COLLECTION_NAME = "collection_test";


	public static void main(String[] args) throws UnknownHostException {

		// 获取Mongo客户端
		MongoClient mongoClient = new MongoClient(MONGO_HOST, MONGO_PORT);
		/**
		 * 1.获取所有db名称并打印
		 * （注意：在MongoDB未开启auth权限认证下可用，若已经开启权限校验，则会报Unauthorized错）
		 */
		/*List<String> dbs = mongoClient.getDatabaseNames();
		System.out.println(MONGO_HOST + ":" + MONGO_PORT.toString() + "包含如下数据库：");
		for (String db : dbs) {
			System.out.println(db);
		}*/

		/**
		 * 2.获取到指定db（若不存在，则mongo会创建该db）
		 */
		DB db = mongoClient.getDB(MONGO_DB_NAME);
		// 2.1用户名&密码校验
		boolean auth = db.authenticate(MONGO_USERNAME, MONGO_PASSWORD.toCharArray());
		if (!auth) {
			System.out.println(MONGO_DB_NAME + "连接失败！");
			return;
		}
		System.out.println(MONGO_DB_NAME + "连接成功！");

		// 2.2获取该db下所有集合名称并打印
		Set<String> collections = db.getCollectionNames();
		System.out.println(db.getName() + "包含如下集合：");
		for (String collection : collections) {
			System.out.println(collection);
		}

		// 2.3获取指定集合(若不存在，则mongo会创建该集合)
		DBCollection collection = db.getCollection(MONGO_COLLECTION_NAME);


		/**
		 * 3.增删查改
		 */
		// 3.1插入一条文档
		BasicDBObject document = new BasicDBObject();
		document.put("name", "Cheung");
		document.put("age", 24);
		document.put("address", "Beijing");
		document.put("date", new Date());
		collection.insert(document);

		// 3.2查询一条文档
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("name", "Cheung");
		DBCursor cursor = collection.find(searchObj);
		if (cursor.hasNext()) {
			System.out.println("查询到的文档为：");
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}
		} else {
			System.out.println("该文档不存在！");
		}

		// 3.3修改一条文档
		BasicDBObject newDocument = new BasicDBObject();
		newDocument.put("name", "Cheung-updated");// 新文档
		BasicDBObject updateObj = new BasicDBObject();
		updateObj.put("$set", newDocument);
		collection.update(searchObj, updateObj);// 更新

		// 3.4查询修改后的文档（修改确认）
		DBCursor cursor2 = collection.find(newDocument);
		if (cursor2.hasNext()) {
			System.out.println("修改后的文档为：");
			while (cursor2.hasNext()) {
				System.out.println(cursor2.next());
			}
		} else {
			System.out.println("该文档不存在！");
		}

		// 3.5删除该文档
		collection.remove(newDocument);

		// 3.6查询该文档是否存在（删除确认）
		DBCursor cursor3 = collection.find(newDocument);
		if (cursor3.hasNext()) {
			System.out.println("查询到的文档为：");
			while (cursor3.hasNext()) {
				System.out.println(cursor3.next());
			}
		} else {
			System.out.println("该文档不存在！");
		}

	}

}
