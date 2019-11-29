import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.beanutils.BeanUtils;


/**
 * @Author: Andy Yu
 * @Version: Created Date：2019年11月25日 下午1:39:52
 * @Description:TODO
 */
@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
public class JsonUtils {

	/**
	 * Convert List to Json String
	 *
	 * @param list
	 * @return String
	 */
	public static <T> String toJSONString(List<T> list) {
		return JSONArray.fromObject(list).toString();
	}

	/***
	 * Convert Object to String
	 *
	 * @param object
	 * @return String
	 */
	public static String toJSONString(Object object) {
		return JSONArray.fromObject(object).toString();
	}

	/***
	 * Convert JSONArray to String
	 *
	 * @param jsonArray
	 * @return String
	 */
	public static String toJSONString(JSONArray jsonArray) {
		return jsonArray.toString();
	}

	/***
	 * Convert JSONObject to String
	 *
	 * @param jsonObject
	 * @return String
	 */
	public static String toJSONString(JSONObject jsonObject) {
		return jsonObject.toString();
	}

	/**
	 * Convert partial properties of Java Bean to JSON
	 *
	 * @param bean
	 *            Java Bean
	 * @param fields
	 *            properties to be JSONed
	 * @return String
	 * @throws Exception
	 */
	public static String toJSONString(Object bean, String[] fields) throws Exception {
		JSONObject jo = new JSONObject();
		if (fields != null && fields.length != 0) {
			for (String field : fields) {
				if (bean instanceof java.util.HashMap) {
					jo.put(field, ((java.util.HashMap) bean).get(field));
				} else {
					String valueGetter = "get"+ field.substring(0, 1).toUpperCase()	+ field.substring(1);
					jo.put(field, bean.getClass().getDeclaredMethod(valueGetter).invoke(bean));
				}
			}
			return toJSONString(jo);
		} else {
			return toJSONString(bean);
		}
	}

	/**
	 * Convert Object to JSON with JsonConfig
	 *
	 * @param bean
	 *            Java Bean
	 * @param JsonConfig jsonConfig
	 * @return String
	 */
	public static String toJSONString(Object bean, JsonConfig jsonConfig) {
		return JSONArray.fromObject(bean, jsonConfig).toString();
	}

	/**
	 * Convert partial properties of Java Bean to JSON
	 *
	 * @param bean
	 *            Java Bean
	 * @param fields
	 *            properties to be ignored
	 * @return String
	 */
	public static String toJSONStringIgnoreFields(Object bean, String[] fields) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(fields);
		return JSONArray.fromObject(bean, jsonConfig).toString();
	}

	/***
	 * Convert Object to JSONArray
	 *
	 * @param object
	 * @return List
	 */
	public static List toArrayList(Object object) {
		List arrayList = new ArrayList();
		JSONArray jsonArray = JSONArray.fromObject(object);
		Iterator it = jsonArray.iterator();
		while (it.hasNext()) {
			JSONObject jsonObject = (JSONObject) it.next();
			Iterator keys = jsonObject.keys();
			while (keys.hasNext()) {
				Object key = keys.next();
				Object value = jsonObject.get(key);
				arrayList.add(value);
			}
		}
		return arrayList;
	}

	/***
	 * Convert Object to Collection
	 *
	 * @param object
	 * @return Collection
	 */
	public static Collection toCollection(Object object) {
		JSONArray jsonArray = JSONArray.fromObject(object);
		return JSONArray.toCollection(jsonArray);
	}

	/***
	 * Convert Object to Collection
	 *
	 * @param Class
	 *            clazz
	 *
	 * @param object
	 * @return Collection
	 */
	public static Collection toCollection(String jsonString, Class clazz) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		return JSONArray.toCollection(jsonArray, clazz);
	}

	/***
	 * Convert Object to JSONArray
	 *
	 * @param object
	 * @return JSONArray
	 */
	public static JSONArray toJSONArray(Object object) {
		return JSONArray.fromObject(object);
	}

	/**
     * Convert List to JSONArray
     * @param list
     * @return JSONArray
     */
    public static JSONArray toJSONArray(List list) {
        return JSONArray.fromObject(list);
    }

	/***
	 * Convert Object to JSONObject
	 *
	 * @param object
	 * @return JSONObject
	 */
	public static JSONObject toJSONObject(Object object) {
		JsonConfig jsonConfig = new JsonConfig();
		PropertyFilter filter = new PropertyFilter() {
			@Override
			public boolean apply(Object object, String fieldName,
					Object fieldValue) {
				return null == fieldValue;
			}
		};
		jsonConfig.setJsonPropertyFilter(filter);
		JSONObject jsonobj = JSONObject.fromObject(object, jsonConfig);
		return jsonobj;
	}

	/***
	 * Convert Object to HashMap
	 *
	 * @param object
	 * @return Map
	 */
	public static Map toMap(Object object) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		JSONObject jsonObject = toJSONObject(object);
		Iterator ito = jsonObject.keys();
		while (ito.hasNext()) {
			String key = String.valueOf(ito.next());
			Object value = jsonObject.get(key);
			data.put(key, value);
		}
		return data;
	}

    /**
     * Convert list to List<Map>
     * @param <T>
     * @param object
     * @return List<Map>
     */
	public static List<?> toMap(List list) {
		List<Map> resultList = new ArrayList<>();

		JSONArray jsonArray = JSONArray.fromObject(list);
		Iterator it = jsonArray.iterator();
		while (it.hasNext()) {
			JSONObject jsonObject = (JSONObject) it.next();
			Iterator keys = jsonObject.keys();
			Map map = new HashMap<>();
			while (keys.hasNext()) {
				Object key = keys.next();
				Object value = jsonObject.get(key);
				map.put(key,value);
			}
			resultList.add(map);
		}
		return resultList;
	}

	/***
	 * Convert Object to ArrayList
	 *
	 * @param object
	 * @return List<Map<String, Object>>
	 */
	public static List<Map<String, Object>> toList(Object object) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JSONArray jsonArray = JSONArray.fromObject(object);
		for (Object obj : jsonArray) {
			JSONObject jsonObject = (JSONObject) obj;
			Map<String, Object> map = new HashMap<String, Object>();
			Iterator it = jsonObject.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = jsonObject.get(key);
				map.put(key, value);
			}
			list.add(map);
		}
		return list;
	}

	/***
	 * Convert JSONArray to ArrayList
	 *
	 * @param
	 * @param jsonArray
	 * @param objectClass
	 * @return List<T>
	 */
	public static <T> List<T> toList(JSONArray jsonArray, Class<T> objectClass) {
		return JSONArray.toList(jsonArray, objectClass);
	}

	/***
	 * Convert Object to ArrayList
	 *
	 * @param Object
	 *            object
	 * @param objectClass
	 * @return List<T>
	 */
	public static <T> List<T> toList(Object object, Class<T> objectClass) {
		JSONArray jsonArray = JSONArray.fromObject(object);
		return JSONArray.toList(jsonArray, objectClass);
	}

	/***
	 * Convert jsonString to ArrayList
	 *
	 * @param jsonString
	 * @param objectClass
	 * @return List<T>
	 */
	public static <T> List<T> toList(String jsonString, Class<T> objectClass) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		return JSONArray.toList(jsonArray, objectClass);
	}

	/***
	 * Convert JSONObject to Object
	 *
	 * @param
	 * @param jsonObject
	 * @param beanClass
	 * @return Class<T>
	 */
	public static <T> T toBean(JSONObject jsonObject, Class<T> beanClass) {
		return (T) JSONObject.toBean(jsonObject, beanClass);
	}

	/***
	 * Convert Object to Object
	 *
	 * @param
	 * @param object
	 * @param beanClass
	 * @return Class<T>
	 */
	public static <T> T toBean(Object object, Class<T> beanClass) {
		JSONObject jsonObject = JSONObject.fromObject(object);

		return (T) JSONObject.toBean(jsonObject, beanClass);
	}

	/***
	 * Convert JSON String to Java Bean,and set the detail List to the main form
	 * field
	 *
	 * @param jsonString
	 *            JSON String
	 * @param mainClass
	 *            Main form class
	 * @param detailName
	 *            Property name of the main class
	 * @param detailClass
	 *            Detail class
	 * @return T
	 */
	public static <T, D> T toBean(String jsonString, Class<T> mainClass,
			String detailName, Class<D> detailClass) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		JSONArray jsonArray = (JSONArray) jsonObject.get(detailName);

		T mainEntity = toBean(jsonObject, mainClass);
		List<D> detailList = toList(jsonArray, detailClass);

		BeanUtils.setProperty(mainEntity, detailName, detailList);

		return mainEntity;
	}

	/***
	 * Convert JSON String to Java Bean,and set the detail List to the main form
	 * field
	 *
	 * @param jsonString
	 *            JSON String
	 * @param mainClass
	 *            Main form class
	 * @param detailName1
	 *            Property name of the main class
	 * @param detailClass1
	 *            Detail class
	 * @param detailName2
	 *            Property name of the main class
	 * @param detailClass2
	 *            Detail class
	 * @return T
	 */
	public static <T, D1, D2> T toBean(String jsonString, Class<T> mainClass,
			String detailName1, Class<D1> detailClass1, String detailName2,
			Class<D2> detailClass2) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		JSONArray jsonArray1 = (JSONArray) jsonObject.get(detailName1);
		JSONArray jsonArray2 = (JSONArray) jsonObject.get(detailName2);

		T mainEntity = toBean(jsonObject, mainClass);
		List<D1> detailList1 = toList(jsonArray1, detailClass1);
		List<D2> detailList2 = toList(jsonArray2, detailClass2);

		BeanUtils.setProperty(mainEntity, detailName1, detailList1);
		BeanUtils.setProperty(mainEntity, detailName2, detailList2);

		return mainEntity;
	}

	/***
	 * Convert JSON String to Java Bean,and set the detail List to the main form
	 * field
	 *
	 * @param jsonString
	 *            JSON String
	 * @param mainClass
	 *            Main form class
	 * @param detailName1
	 *            Property name of the main class
	 * @param detailClass1
	 *            Detail class
	 * @param detailName2
	 *            Property name of the main class
	 * @param detailClass2
	 *            Detail class
	 * @param detailName3
	 *            Property name of the main class
	 * @param detailClass3
	 *            Detail class
	 * @return T
	 */
	public static <T, D1, D2, D3> T toBean(String jsonString,
			Class<T> mainClass, String detailName1, Class<D1> detailClass1,
			String detailName2, Class<D2> detailClass2, String detailName3,
			Class<D3> detailClass3) throws Exception{
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		JSONArray jsonArray1 = (JSONArray) jsonObject.get(detailName1);
		JSONArray jsonArray2 = (JSONArray) jsonObject.get(detailName2);
		JSONArray jsonArray3 = (JSONArray) jsonObject.get(detailName3);

		T mainEntity = toBean(jsonObject, mainClass);
		List<D1> detailList1 = toList(jsonArray1, detailClass1);
		List<D2> detailList2 = toList(jsonArray2, detailClass2);
		List<D3> detailList3 = toList(jsonArray3, detailClass3);

		BeanUtils.setProperty(mainEntity, detailName1, detailList1);
		BeanUtils.setProperty(mainEntity, detailName2, detailList2);
		BeanUtils.setProperty(mainEntity, detailName3, detailList3);

		return mainEntity;
	}

	/***
	 * Convert JSON String to Java Bean
	 *
	 * @param jsonString
	 *            JSON String
	 * @param mainClass
	 *            Main form class
	 * @param detailClass
	 *            Detail class
	 * @return T
	 */
	public static <T> T toBean(String jsonString, Class<T> mainClass,HashMap<String, Class> detailClass) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		T mainEntity = toBean(jsonObject, mainClass);
		for (Object key : detailClass.keySet()) {
			Class value = detailClass.get(key);
			BeanUtils.setProperty(mainEntity, key.toString(), value);
		}
		return mainEntity;
	}


	/**
	 * Convert json String to Java Bean
	 *
	 * @param jsonString
	 * @param javaClass
	 * @return javaClass
	 */
	public static <T> T toArrayList(String jsonString, Class<T> javaClass) {
		return (T) JSONArray.toList(JSONArray.fromObject(jsonString), javaClass);
	}

	/**
	 * Convert Java Bean to Map
	 *
	 * @param Map
	 *            <String,Object>
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	public static Map<String, Object> bean2Map(Object bean) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		BeanInfo b = Introspector
				.getBeanInfo(bean.getClass(), Object.class);
		PropertyDescriptor[] pds = b.getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			String propertyName = pd.getName();
			Method m = pd.getReadMethod();
			Object properValue = m.invoke(bean);
			map.put(propertyName, properValue);
		}
		return map;
	}

	/**
	 * Convert Map to Java Bean
	 *
	 * @param Map
	 *            <String, Object> map
	 * @param Class
	 *            <T> clazz
	 * @return T
	 * @throws Exception
	 */
	public static <T> T map2Bean(Map<String, Object> map, Class<T> clazz) throws Exception{
		T obj = clazz.newInstance();
		BeanInfo b = Introspector.getBeanInfo(clazz, Object.class);
		PropertyDescriptor[] pds = b.getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			Method setter = pd.getWriteMethod();
			setter.invoke(obj, map.get(pd.getName()));
		}
		return obj;
	}


	/**
	 * Switch the quotation from double to single
	 *
	 * @param String
	 *
	 * @return String
	 */
	public static String switchQuoteD2S(String jsonString) {
		if(jsonString == null || "".equals(jsonString.trim())) {
			return null;
		}else {
			return jsonString.replaceAll("\"", "'");
		}
	}

	/**
	 * Switch the quotation from single to double
	 *
	 * @param String
	 *
	 * @return String
	 */
	public static String switchQuoteS2D(String jsonString) {
		if(jsonString == null || "".equals(jsonString.trim())) {
			return null;
		}else {
			return jsonString.replaceAll("'", "\"");
		}
	}

	/**
	 * Convert List<Map<?,?>> to Json
	 * @param list Data List
	 * @param fields Properties to be Jsoned,if null,then all;
	 * @return String
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String listMapToJson(List<?> list, String[] fields) throws Exception {
		if(fields != null && fields.length != 0) {
			JSONArray array = new JSONArray();
			for(Object map : list) {
				JSONObject jo = new JSONObject();
				for(String field:fields) {
					jo.put(field, ((HashMap<String, Object>)map).get(field));
				}
				array.add(jo);
			}
			return array.toString();
		} else {
			return toJSONString(list);
		}
	}

	/**
	 * Convert Json String to Map
	 * @param jsonString
	 * @return
	 */
    public static Map toMap(String jsonString) {
        Map map = new HashMap();
        JSONObject json = JSONObject.fromObject(jsonString);
        Iterator keys = json.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = json.get(key).toString();
            if (value.startsWith("{") && value.endsWith("}")) {
                map.put(key, toMap(value));
            } else {
                map.put(key, value);
            }

        }
        return map;
    }
    /**
	 * Convert properties of JSONObject which is null or JSONNull to blank or
	 * remove it
	 *
	 * @param jsonObj
	 */
	public static void filterNull(JSONObject jsonObj) {
		Iterator<String> it = jsonObj.keys();
		Object obj = null;
		String key = null;
		while (it.hasNext()) {
			key = it.next();
			obj = jsonObj.get(key);
			if (obj instanceof JSONObject) {
				filterNull((JSONObject) obj);
			}
			if (obj instanceof JSONArray) {
				JSONArray objArr = (JSONArray) obj;
				for (int i = 0; i < objArr.size(); i++) {
					filterNull(objArr.getJSONObject(i));
				}
			}
			if (obj == null || obj instanceof JSONNull
					|| "".equals(obj.toString().trim())) {
					jsonObj.put(key, "");
			}
		}
	}
}
