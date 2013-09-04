//package com.gigaspaces.persistency.metadata;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.gigaspaces.persistency.error.MongoMetadataException;
//
//public class CollectionMetadata {
//
//	private String name;
//	private List<String> fields=new ArrayList<String>();
//	
//	
//	public CollectionMetadata(String name) throws MongoMetadataException {
//		if(name == null || name.isEmpty())
//			throw new MongoMetadataException(name);
//		this.name = name;
//	}
//
//	public String getName() {
//		return name;
//	}
//	
//	public void setName(String name) {
//		this.name = name;
//	}
//	
//	public void addField(String field){
//		
//		if(field.contains(field))
//			return;
//		
//		fields.add(field);
//	}
//
//	public List<String> getFields() {
//		return fields;
//	}
//
//}
