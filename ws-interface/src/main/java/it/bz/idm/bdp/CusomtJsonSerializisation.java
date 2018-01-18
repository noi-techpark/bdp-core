package it.bz.idm.bdp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.SerializeExceptFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
@Configuration
public class CusomtJsonSerializisation {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper oMapper = new ObjectMapper();
		Map<String, PropertyFilter> mapping = new HashMap<>();
		SimpleBeanPropertyFilter serializeAllExcept = SerializeExceptFilter.serializeAllExcept("_type");
		mapping.put("excludeTypeFilter", serializeAllExcept);
		oMapper.setFilterProvider(new SimpleFilterProvider(mapping));
		return oMapper;	
	}
}
