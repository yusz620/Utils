package com.bs.apps.doms.common.util;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
import org.apache.log4j.Logger;


/**
 * @Author: Andy Yu
 * @Version: Created Date：2021年11月17日 下午10:39:52
 */
@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
public class JsonUtils {
	private static Logger logger = Logger.getLogger(JsonUtils.class);
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
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		return JSONObject.fromObject(object, jsonConfig).toString();
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
	  *
	  * @param map
	  * @return jsonString
     */
   public static String toJSONString(Map map) {
	   return JSONObject.fromObject(map).toString();
   }

	 /**
     * 数组转换成Array字符串
     * @return
     */
    public static final String listToStringArray(List<?> list,String[] fields) {
    	StringBuffer json = new StringBuffer();
    	json.append("[");
    	  for(int i=0;i<list.size();i++){
    		   json.append("[");
    		   Object obj = list.get(i);
    		   for(int j=0;j<fields.length;j++){
    			   if(j!=0){
    				   json.append(",");
    			   }
    			   if(fields[j]==null ||"".endsWith(fields[j].trim())){
    				   json.append("''");
    				   continue;
    			   }
    			   String methodName = "get"+ fields[j].substring(0, 1).toUpperCase()+ fields[j].substring(1);
    			   Method method = null;
    			   try {
    				   method = obj.getClass().getMethod(methodName,new Class[] {});
				   } catch (Exception e) {
					  logger.error(e.getMessage());
				   }

    			   Object object=null;
    			   try {
    				   object=method.invoke(obj, new Object[] {});
    			   } catch (Exception e) {
    				   logger.error(e.getMessage());
    			   }
    			   if(null!=object){
    				   json.append("'"+object+"'");
    			   }else{
    				  json.append("''");
    			   }
    		   }
    		   if(i<list.size()-1){
    			   json.append("],");
    		   }else{
    			   json.append("]");
    		   }
    	  }
    	json.append("]");
    	return json.toString();
    }

	public static String toJSONString(List bean, String[] fields,boolean removeDuplicate) {
		JSONArray jo = new JSONArray();
		Map keyValuePairs= new HashMap<>();
		if(fields!=null && fields.length!=0) {
			for(String field : fields) {
				for(Object obj:bean) {
					JSONObject js = new JSONObject();
					String valueGetter = "get"+ field.substring(0, 1).toUpperCase()	+ field.substring(1);
					Object value = "";
					try {
						value=obj.getClass().getDeclaredMethod(valueGetter).invoke(obj);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					js.put(field, value);

					if(removeDuplicate) {
						if(keyValuePairs.get(field+value)==null) {
							jo.add(js);
							keyValuePairs.put(field+value,field+value);
						}
					}else {
						jo.add(js);
					}
				}
			}

		}else {
			return toJSONString(bean, removeDuplicate);
		}
		return jo.toString();
	}

	public static String toJSONString(List bean, boolean removeDuplicate) {
		JSONArray jo = new JSONArray();
		Map keyValuePairs= new HashMap<>();
		for(Object obj:bean) {
			JSONObject js = JSONObject.fromObject(obj);
			if(removeDuplicate) {
				if(keyValuePairs.get(js.toString())==null) {
					jo.add(js);
					keyValuePairs.put(js.toString(),js.toString());
				}
			}else {
				jo.add(js);
			}
		}

		return jo.toString();
	}

	/**
	 *
	 * @param list
	 * @param fields fields to be included
	 * @return String
	 */
	public static String toJSONString(List list, String[] fields) {
		JSONArray jo = new JSONArray();
		if(fields!=null && fields.length!=0) {
			for (String field : fields) {
				for(Object obj:list) {
					JSONObject js = new JSONObject();
					String valueGetter = "get"+ field.substring(0, 1).toUpperCase()	+ field.substring(1);
					Object value = "";
					try {
						value=obj.getClass().getDeclaredMethod(valueGetter).invoke(obj);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					js.put(field, value);

					jo.add(js);
				}
			}
		}else {
			return toJSONString(list);
		}
		return jo.toString();
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
	public static String toJSONString(Object bean, String[] fields,boolean removeDuplicate) {
		JSONObject jo = new JSONObject();
		if (fields != null && fields.length != 0) {
			for (String field : fields) {
				if (bean instanceof java.util.HashMap) {
					jo.put(field, ((java.util.HashMap) bean).get(field));
				}if(bean instanceof java.util.List) {
					return toJSONString((List)bean,fields,removeDuplicate);
				} else {
					String valueGetter = "get"+ field.substring(0, 1).toUpperCase()	+ field.substring(1);
					Object value = "";
					try {
						value=bean.getClass().getDeclaredMethod(valueGetter).invoke(bean);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					jo.put(field, value);
				}
			}
			return toJSONString(jo);
		} else {
			return toJSONString(bean,removeDuplicate);
		}
	}

	public static String toJSONString(Object bean, boolean removeDuplicate) {
		if(bean==null) {
			return null;
		}
		if (bean instanceof java.util.HashMap) {
			JSONObject jo = new JSONObject();
			Iterator it =((Map)bean).keySet().iterator();
			while(it.hasNext()) {
				jo.put(it.next(), ((java.util.HashMap) bean).get(it.next()));
			}
			return jo.toString();
		}if(bean instanceof java.util.List) {
			return toJSONString((List)bean,null,removeDuplicate);
		} else {
			return JSONObject.fromObject(bean).toString();
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
		return JSONObject.fromObject(bean, jsonConfig).toString();
	}

	/**
	 * Convert Object to JSON with JsonConfig
	 *
	 * @param bean
	 *            Java Bean
	 * @param JsonConfig jsonConfig
	 * @return String
	 */
	public static String toJSONString(List list, JsonConfig jsonConfig) {
		return JSONArray.fromObject(list, jsonConfig).toString();
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
		if(bean instanceof List) {
			return toJSONStringIgnoreFields((List) bean, fields);
		}
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(fields);
		return JSONObject.fromObject(bean, jsonConfig).toString();
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
	public static String toJSONStringIgnoreFields(List list, String[] fields) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.setExcludes(fields);
		return JSONArray.fromObject(list, jsonConfig).toString();
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

	public static JSONArray toJSONArray(String jsonString,JsonConfig jsonConfig) {
		return JSONArray.fromObject(jsonString, jsonConfig);
	}

	/**
     * Convert List to JSONArray
     * @param list
     * @return JSONArray
     */
    public static JSONArray toJSONArray(List list) {
        return JSONArray.fromObject(list);
    }

    /**
     *
     * @param String jsonString
     * @return
     */
    public static JSONArray toJSONArray(String json) {
        return JSONArray.fromObject(json);
    }

	/***
	 * Convert Object to JSONObject
	 *
	 * @param object
	 * @return JSONObject
	 */
	public static JSONObject toJSONObject(Object object) {
		JSONObject jsonobj = JSONObject.fromObject(object);
		return jsonobj;
	}

	/***
	 * Convert Object to JSONObject
	 *
	 * @param object
	 * @return JSONObject
	 */
	public static JSONObject toJSONObject(Object object,JsonConfig jsonConfig) {
		/** in case to filter the null properties,open the piece of code
			PropertyFilter filter = new PropertyFilter() {
				@Override
				public boolean apply(Object object, String fieldName,Object fieldValue) {
					return null == fieldValue;
				}
			};
			jsonConfig.setJsonPropertyFilter(filter);
		*/
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
		List<Map> resultList = new ArrayList<Map>();

		JSONArray jsonArray = JSONArray.fromObject(list);
		Iterator it = jsonArray.iterator();
		while (it.hasNext()) {
			JSONObject jsonObject = (JSONObject) it.next();
			Iterator keys = jsonObject.keys();
			Map map = new HashMap<Object, Object>();
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
	 * Convert Object to JSONArray
	 *
	 * @param object
	 * @return List
	 */
	public static List toList(Object object,JsonConfig jsonConfig) {
		List arrayList = new ArrayList();
		JSONArray jsonArray = JSONArray.fromObject(object,jsonConfig);
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
	 * Convert JSONArray to ArrayList
	 *
	 * @param
	 * @param jsonArray
	 * @param objectClass
	 * @return List<T>
	 */
	public static <T,D> List<T> toList(String jsonArray, Class<T> mainClass,String detailName, Class<D> detailClass) throws Exception{
		List<T> list=new ArrayList<>();
		JSONArray jsonArrayObject=  JSONArray.fromObject(jsonArray);
		for(int i=0;i<jsonArrayObject.size();i++) {
			JSONObject obj = jsonArrayObject.getJSONObject(i);
			T mainEntity = toBean(obj, mainClass);
			List<D> detaiList = toList((JSONArray) obj.get(detailName), detailClass);
			BeanUtils.setProperty(mainEntity, detailName, detaiList);
			list.add(mainEntity);
		}

		return list;
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
	public static <T, D> T toBean(String jsonString, Class<T> mainClass,String detailName, Class<D> detailClass) throws Exception {
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
	public static <T, D1, D2> T toBean(String jsonString, Class<T> mainClass,String detailName1, Class<D1> detailClass1, String detailName2,Class<D2> detailClass2) throws Exception {
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
	public static <T, D1, D2, D3> T toBean(String jsonString,Class<T> mainClass, String detailName1, Class<D1> detailClass1,String detailName2, Class<D2> detailClass2, String detailName3,Class<D3> detailClass3) throws Exception{
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
	 * Convert Java Bean to Map
	 *
	 * @param Map
	 *            <String,Object>
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	public static Map<String, Object> bean2Map(Object bean) throws Exception{
		return toMap(bean, new JsonConfig());
	}

	/**
	 * Convert Java Bean to Map
	 *
	 * @param Map
	 *            <String,Object>
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	public static Map<String, Object> toMap(Object bean,JsonConfig jsonConfig) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		BeanInfo b = Introspector
				.getBeanInfo(bean.getClass(), Object.class);
		PropertyDescriptor[] pds = b.getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			String propertyName = pd.getName();
			Method m = pd.getReadMethod();
			Object properValue = m.invoke(bean);
			if(properValue instanceof List) {
				map.put(propertyName, toJSONString((List)properValue,jsonConfig));
			}else {
				map.put(propertyName, properValue);
			}

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
	public static <T> T toBean(Map<String, Object> map, Class<T> clazz) throws Exception{
		T obj = clazz.newInstance();
		BeanInfo b = Introspector.getBeanInfo(clazz, Object.class);
		PropertyDescriptor[] pds = b.getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			Method setter = pd.getWriteMethod();
			Type[] types=setter.getGenericParameterTypes();
			if(types!=null && types.length>0) {
				String type=types[0].getTypeName();
				if(type.contains("java.util.List") || type.contains("java.util.ArrayList")) {
					String className = type.substring(type.indexOf("<")+1, type.indexOf(">"));
					setter.invoke(obj, toList(map.get(pd.getName()), Class.forName(className)));
				}else {
					setter.invoke(obj, map.get(pd.getName()));
				}
			}

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
            }else if(value.startsWith("[") && value.endsWith("]")){
            	map.put(key, toMap(toList(value)));
            } else {
                map.put(key, value);
            }

        }
        return map;
    }
    /**
	 * Convert properties of JSONObject which is null or JSONNull to blank
	 *
	 * @param jsonObj
	 */
	public static JSONObject filterNull(JSONObject jsonObj) {
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
			if (obj == null || obj instanceof JSONNull || "".equals(obj.toString().trim())) {
					jsonObj.put(key, "");
			}
		}
		return jsonObj;
	}

	/**
	 * Convert properties of JSONObject which is null or JSONNull to blank
	 *
	 * @param jsonObj
	 */
	public static JSONObject removeNull(JSONObject jsonObj) {
		Iterator<String> it = jsonObj.keys();
		Object obj = null;
		String key = null;
		while (it.hasNext()) {
			key = it.next();
			obj = jsonObj.get(key);
			if (obj instanceof JSONObject) {
				removeNull((JSONObject) obj);
			}
			if (obj instanceof JSONArray) {
				JSONArray objArr = (JSONArray) obj;
				for (int i = 0; i < objArr.size(); i++) {
					removeNull(objArr.getJSONObject(i));
				}
			}
			if (obj == null || obj instanceof JSONNull || "".equals(obj.toString().trim())) {
					jsonObj.remove(key);
			}
		}
		return jsonObj;
	}

	/**
	 * Convert properties of JSONObject which is null or JSONNull to blank
	 *
	 * @param jsonObj
	 */
	public static JSONArray removeNull(JSONArray jsonArray) {
		for(int i=0;i<jsonArray.size();i++) {
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			Iterator<String> it = jsonObj.keys();
			Object obj = null;
			String key = null;
			while (it.hasNext()) {
				key = it.next();
				obj = jsonObj.get(key);
				if (obj instanceof JSONObject) {
					removeNull((JSONObject) obj);
				}
				if (obj instanceof JSONArray) {
					JSONArray objArr = (JSONArray) obj;
//					for (int j = 0; j < objArr.size(); j++) {
//						removeNull(objArr.getJSONObject(j));
//					}
					removeNull(objArr);
				}
				if (obj == null || obj instanceof JSONNull || "".equals(obj.toString().trim())) {
						jsonObj.remove(key);
				}
			}
		}
		return jsonArray;
	}

	/**
	 * Convert properties of JSONObject which is null or JSONNull to blank
	 *
	 * @param jsonObj
	 */
	public static String removeNull(String jsonString) {
		if(jsonString==null || "".equals(jsonString.trim())) {
			return null;
		}

		JsonConfig jsonConfig = new JsonConfig();
		PropertyFilter filter = new PropertyFilter() {
			@Override
			public boolean apply(Object object, String fieldName,Object fieldValue) {
				return  fieldValue== null || fieldValue instanceof JSONNull || "".equals(fieldValue.toString().trim());
			}
		};
		jsonConfig.setJsonPropertyFilter(filter);

		if(jsonString.startsWith("{")&&jsonString.endsWith("}")){
			JSONObject jsonObject=toJSONObject(jsonString,jsonConfig);
			removeNull(jsonObject);
			jsonString = jsonObject.toString();
		}else if(jsonString.startsWith("[")&&jsonString.endsWith("]")) {
			JSONArray jsonArray=toJSONArray(jsonString,jsonConfig);
			removeNull(jsonArray);
			jsonString = jsonArray.toString();
		}
		return jsonString;
	}

}
