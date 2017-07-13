/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.framwork.databus;

import com.breeze.base.log.Logger;
import com.google.gson.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;



import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Administrator
 */
public class ContextTools {

	public static Logger log = Logger.getLogger("com.breeze.framwork.databus.ContextTools");
	/**
	 * 根据BreezeContext将其值转换成json对象
	 * 
	 * @param context
	 *            待转换的BreezeContext对象
	 * @param writeList
	 *            转换白名单，因为函数是遍历模式的，所以可以指定，某些值转换，某些值不转换
	 * @return
	 */
	public static String getJsonString(BreezeContext context, String[] writeList) {
		GsonBuilder gbuilder = new GsonBuilder();
		gbuilder.registerTypeAdapter(
				new com.google.gson.reflect.TypeToken<BreezeContext>() {
				}.getType(), new SerContext(writeList));
		Gson gson = gbuilder.create();
		return gson.toJson(context);
	}

	/**
	 * 根据json字符串返回BreezeContext值
	 * 
	 * @param json
	 *            字符串
	 * @return
	 */
	public static BreezeContext getBreezeContext4Json(String json) {
		GsonBuilder gbuilder = new GsonBuilder();
		gbuilder.registerTypeAdapter(
				new com.google.gson.reflect.TypeToken<BreezeContext>() {
				}.getType(), new DeserContext());
		Gson gson = gbuilder.create();
		return gson.fromJson(json,
				new com.google.gson.reflect.TypeToken<BreezeContext>() {
				}.getType());
	}

	/**
	 * 根据xml来返回BreezeContext对象，注意，xml和json有一个特殊的地方，就是xml除属性外，他又值，而这个值，
	 * 在breezeContext中一定也是表现成属性，所以第二个参数，就是要写明这个值的属性名是是什么
	 * 
	 * @param xml
	 *            原xml字符串
	 * @param pTagData
	 *            xml中标签名，在加入到BreezeContext后的key值，如果传入null，则为_tagName
	 * @param pTagData 
	 *            xml中的标签值，在加入到BreezeContext后的key名称，如果传入null，则为_tagData
	 * @return
	 */
	public static BreezeContext getBreezeContext4xml(String xml,
			String pTagName,String pTagData) {
		
		try{
			XMLContextParser p = new XMLContextParser(xml,pTagName,pTagData);
			String result = null;
		
		return p.go();
		}catch(Exception e){
			log.severe("exception:", e);
			return null;
		}
	}

	static class SerContext implements JsonSerializer<BreezeContext> {
		private String[] writeList;

		SerContext(String[] w) {
			this.writeList = w;
		}

		@Override
		public JsonElement serialize(BreezeContext t, Type type,
				JsonSerializationContext jsc) {
			// 根据白名单过滤
			if (this.writeList != null) {
				JsonObject result = new JsonObject();
				for (String name : this.writeList) {
					BreezeContext value = t.getContext(name);
					if (value == null){
					   continue;
					}
					switch (value.getType()) {
					case BreezeContext.TYPE_MAP:
						result.add(name, this.handleMap(value));
						break;
					case BreezeContext.TYPE_ARRAY:
						result.add(name, this.handleArray(value));
						break;
					case BreezeContext.TYPE_DATA:
						result.add(name, this.handleObj(value));
						break;
					}
				}
				return result;
			} else {
				if (BreezeContext.TYPE_ARRAY == t.getType()) {
					// 处理数组情况
					JsonArray result = new JsonArray();
					for (int i = 0; i < t.getArraySize(); i++) {
						BreezeContext value = t.getContext(i);
						switch (value.getType()) {
						case BreezeContext.TYPE_MAP:
							result.add(this.handleMap(value));
							break;
						case BreezeContext.TYPE_ARRAY:
							result.add(this.handleArray(value));
							break;
						case BreezeContext.TYPE_DATA:
							result.add(this.handleObj(value));
							break;
						default:
							// 2014-4-26 罗光瑜修改，否则加入空数组
							result.add(new JsonNull());
						}
					}
					return result;
				} else {
					JsonObject result = new JsonObject();
					for (String name : ((HashMap<String, BreezeContext>) t
							.getDataObj()).keySet()) {
						BreezeContext value = t.getContext(name);
						switch (value.getType()) {
						case BreezeContext.TYPE_MAP:
							result.add(name, this.handleMap(value));
							break;
						case BreezeContext.TYPE_ARRAY:
							result.add(name, this.handleArray(value));
							break;
						case BreezeContext.TYPE_DATA:
							result.add(name, this.handleObj(value));
							break;
						}
					}
					return result;
				}
			}
		}

		JsonElement handleMap(BreezeContext t) {
			JsonObject result = new JsonObject();
			for (String name : ((HashMap<String, BreezeContext>) t.getDataObj())
					.keySet()) {
				BreezeContext value = t.getContext(name);
				if (value == null) {
					continue;
				}
				switch (value.getType()) {
				case BreezeContext.TYPE_MAP:
					result.add(name, this.handleMap(value));
					break;
				case BreezeContext.TYPE_ARRAY:
					result.add(name, this.handleArray(value));
					break;
				case BreezeContext.TYPE_DATA:
					result.add(name, this.handleObj(value));
					break;
				}
			}
			return result;
		}

		JsonElement handleArray(BreezeContext t) {
			JsonArray result = new JsonArray();
			for (int i = 0; i < t.getArraySize(); i++) {
				BreezeContext value = t.getContext(i);
				if (value == null) {
					result.add(null);
					continue;
				}
				switch (value.getType()) {
				case BreezeContext.TYPE_MAP:
					result.add(this.handleMap(value));
					break;
				case BreezeContext.TYPE_ARRAY:
					result.add(this.handleArray(value));
					break;
				case BreezeContext.TYPE_DATA:
					result.add(this.handleObj(value));
					break;
				}
			}
			return result;
		}

		JsonElement handleObj(BreezeContext t) {
			Gson gson = new Gson();
			return gson.toJsonTree(t.getData());
		}
	}

	static class DeserContext implements JsonDeserializer<BreezeContext> {

		@Override
		public BreezeContext deserialize(JsonElement inputJson, Type _t,
				JsonDeserializationContext _c) throws JsonParseException {
			BreezeContext result = null;
			if (inputJson.isJsonArray()) {
				result = this.handleArray(inputJson.getAsJsonArray());
			} else if (inputJson.isJsonObject()) {
				result = this.handleObject(inputJson.getAsJsonObject());
			} else if (inputJson.isJsonPrimitive()) {
				result = this.handlePrimitive(inputJson.getAsJsonPrimitive());
			}
			return result;
		}

		private BreezeContext handleArray(JsonArray input) {
			BreezeContext result = new BreezeContext();
			Iterator<JsonElement> i = input.iterator();
			while (i.hasNext()) {
				JsonElement n = i.next();
				if (n.isJsonArray()) {
					result.pushContext(this.handleArray(n.getAsJsonArray()));
				} else if (n.isJsonObject()) {
					result.pushContext(this.handleObject(n.getAsJsonObject()));
				} else if (n.isJsonPrimitive()) {
					result.pushContext(this.handlePrimitive(n
							.getAsJsonPrimitive()));
				}
			}
			return result;
		}

		private BreezeContext handleObject(JsonObject input) {
			BreezeContext result = new BreezeContext();
			for (Entry<String, JsonElement> en : input.entrySet()) {
				String name = en.getKey();
				JsonElement value = en.getValue();
				if (value.isJsonArray()) {
					result.setContext(name,
							this.handleArray(value.getAsJsonArray()));
				} else if (value.isJsonObject()) {
					result.setContext(name,
							this.handleObject(value.getAsJsonObject()));
				} else if (value.isJsonPrimitive()) {
					result.setContext(name,
							this.handlePrimitive(value.getAsJsonPrimitive()));
				}
			}
			return result;
		}

		private BreezeContext handlePrimitive(JsonPrimitive input) {
			BreezeContext result = new BreezeContext();
			if (input.isBoolean()) {
				return new BreezeContext(input.getAsBoolean());
			}
			if (input.isNumber()) {
				return new BreezeContext(input.getAsInt());
			}
			if (input.isString()) {
				return new BreezeContext(input.getAsString());
			}
			return null;
		}
	}

	/**
	 * 使用sax进行解析xml，规则是这样的 每个xml标签都是一个对象，对象有两个特殊的属性值，一个是标签名，一个是标签值，这两个标签属性如果是空的，
	 * 默认是_tagName和_tagData 每个标签的值，默认是不能和子标签并列的，有并列则会出错
	 * 每个标签的子标签如果有，则将子标签作为breezeContext的一个属性节点，属性名为子标签名
	 * 标签的属性，也转换成该BreezeContext的一个属性 如果有两个相同的名字的子标签，那么这个会把这个属性的对象作为一个数组 例如： <a>
	 * <b a=1/> <b a=33/> </a> 其转换结果如下： { _tagName:'a', b:[ { _tagName:'b', a:1
	 * }, { _tagName:'b', a:33 } ] }
	 * 
	 * @author Loganluo
	 *
	 */
	private static class XMLContextParser extends DefaultHandler {
		private String inputStr;
		private BreezeContext outputCtx;
		private String tagName = "_tagName";
		private String tagData = "_tagData";

		private Stack<BreezeContext> contextStack = new Stack<BreezeContext>();

		public XMLContextParser(String in, String tagName, String tagData) {
			super();
			this.inputStr = in;
			if (tagName != null) {
				this.tagName = tagName;
			}
			if (tagData != null) {
				this.tagData = tagData;
			}
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attrs) {
			// 利用当前信息构建当前节点信息的BreezeContext，只处理到属性
			BreezeContext curCtx = new BreezeContext();
			curCtx.setContext(this.tagName, new BreezeContext(qName));
			for (int i = 0; i < attrs.getLength(); i++) {
				curCtx.setContext(attrs.getQName(i),
						new BreezeContext(attrs.getValue(i)));
			}
			// 从堆栈中获取当前的父亲,注意，不是出堆栈，只是取堆栈顶元素，出栈统一在结束标签事件中处理
			BreezeContext fatherCtx = null;
			if (!this.contextStack.isEmpty()){
				fatherCtx = this.contextStack.peek();
			}
			// if (有父亲){拿到父亲对象，将自己设置进去
			if (fatherCtx != null) {
				// 从父亲中获取当前节点名的BreezeContext对象
				BreezeContext lastCtx = fatherCtx.getContext(qName);
				// if (该对象存在){说明是数组，按照数组处理
				if (lastCtx != null) {
					// if (如果该对象类型是数组){说明已经按照数组处理过了
					if (lastCtx.getType() == BreezeContext.TYPE_ARRAY) {
						// 直接push进去
						lastCtx.pushContext(curCtx);
					}
					// }

					// else{要将原来的改成数组
					else {
						// 和原来的取出的元素一起，将父亲对应该儿子下的元素改成数组
						BreezeContext newArr = new BreezeContext();
						newArr.pushContext(lastCtx);
						newArr.pushContext(curCtx);
						fatherCtx.setContext(qName, newArr);
					}
					// }
				}
				// }
				// else{不是数组，直接加入到父亲中
				else {
					// 将节点加入到父亲中，key值就是节点名
					fatherCtx.setContext(qName, curCtx);
				}
				// }
			}
			// }

			// 将构建好的压入堆栈
			this.contextStack.push(curCtx);

		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// 出栈操作,并赋值给输出对象
			this.outputCtx = this.contextStack.pop();
		}

		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			// 合成字符串
			String data = new String(ch, start, length);
			// trim掉
			data = data.trim();
			// if (不为空字符串){
			if (!"".equals(data)){
			// 从堆栈中获取当前的父亲节点
				BreezeContext father = this.contextStack.peek();
			// 构建好BreezeContext并加入父节点BreezeContext的一个成员中,name为成员变量tagData
				father.setContext(this.tagData, new BreezeContext(data));
			}
			// }
		}
		
		public BreezeContext go() throws ParserConfigurationException, SAXException, IOException{
			SAXParserFactory sf = SAXParserFactory.newInstance();
			SAXParser sp = sf.newSAXParser();
			ByteArrayInputStream in = new ByteArrayInputStream(this.inputStr.getBytes("UTF-8"));
			sp.parse(in, this);
			in.close();
			return this.outputCtx;
		}

	}

	public static void main(String[] args) {
		String val = "{\"a\":[\"a1\"],\"b\":[]}";
		BreezeContext valCtx = ContextTools.getBreezeContext4Json(val);
		System.out.println(valCtx);
	}
}
