/**
 * This is config class which was inherit to annotation based and only intended for Swagger api.
 * @author Sambed
 * @date 23/05/2019
 * @date last update - 
 * @change by -
 */

package com.bb.kalah.swagger;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  public static final Contact DEFAULT_CONTACT = new Contact(
		  "Sambed Pattanaik", "Kalah Game Test Rest API through Swagger", "");
  
  public static final ApiInfo DEFAULT_API_INFO = new ApiInfo(
      "Kalah Game API", "Kalah Game API for Backbase", "1.0",
      "urn:tos", DEFAULT_CONTACT, 
      "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0");

  private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = 
		 new HashSet<String>(Arrays.asList("application/json"));

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(DEFAULT_API_INFO)
        .produces(DEFAULT_PRODUCES_AND_CONSUMES)
        .consumes(DEFAULT_PRODUCES_AND_CONSUMES);
  }
}