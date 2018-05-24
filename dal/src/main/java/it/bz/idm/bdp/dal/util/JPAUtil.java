package it.bz.idm.bdp.dal.util;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
public class JPAUtil {

	public static EntityManagerFactory emFactory;
	private static final Properties properties = new Properties();
	static {
		try {
			properties.load(JPAUtil.class.getClassLoader().getResourceAsStream("META-INF/persistence.properties"));
			emFactory = Persistence.createEntityManagerFactory(properties.getProperty("persistenceunit"));
		}catch(Throwable ex){
			System.err.println("Cannot create EntityManagerFactory.");
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static EntityManager createEntityManager() {
		return emFactory.createEntityManager();
	}

	public static void close(){
		emFactory.close();
	}

	public static Object getInstanceByType(EntityManager em, String type)
			throws Exception {
		Set<ManagedType<?>> managedTypes = em.getEntityManagerFactory().getMetamodel().getManagedTypes();
		for (ManagedType<?> entity: managedTypes){
			if (entity.getJavaType().getSimpleName().equals(type)){
				return entity.getJavaType().newInstance();
			}
		}
		throw new Exception("ERROR: Cannot get any instance of type " + type + ". Type not found.");
	}

	public static String getEntityNameByObject(Object obj) throws Exception {
		for (EntityType<?> type: emFactory.getMetamodel().getEntities()) {
			if (obj.getClass().getTypeName().equals(type.getJavaType().getName()))
					return type.getName();
		}
		throw new Exception("ERROR: Cannot get any entity name for object "
				+ obj.getClass().getTypeName() + ". Class not found.");
	}

	/**
	 * Emulates getSingleResult without not-found or non-unique-result exceptions. It
	 * simply returns null on no-results. Leaves exceptions to proper errors.
	 *
	 * @param query
	 * @return topmost result or null if not found
	 */
	public static <T> T getSingleResultOrNull(TypedQuery<T> query) {
		return getSingleResultOrAlternative(query, null);
	}

	/**
	 * Emulates getSingleResult without not-found or non-unique-result exceptions. It
	 * simply returns 'alternative' on no-results. Leaves exceptions to proper errors.
	 *
	 * @param query
	 * @param alternative
	 * @return topmost result or 'alternative' if not found
	 */
	public static <T> T getSingleResultOrAlternative(TypedQuery<T> query, T alternative) {
		query.setMaxResults(1);
		List<T> list = query.getResultList();
		if (list == null || list.isEmpty()) {
			return alternative;
		}
		return list.get(0);
	}
}
