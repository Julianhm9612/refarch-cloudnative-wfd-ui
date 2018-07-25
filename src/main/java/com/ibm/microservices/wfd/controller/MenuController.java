package com.ibm.microservices.wfd.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.lang.Object;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.boot.json.BasicJsonParser;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanAccessor;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Controller
public class MenuController {

  private static final Log log = LogFactory.getLog(MenuController.class);

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private SpanAccessor spanAccessor;

  @RequestMapping("/")
  @HystrixCommand(fallbackMethod="getDefaultMenu", groupKey="WhatsForDinnerUI", commandKey="GetMenuForUI")
  public String getMenuForIndex(Model model){

    String menuString = this.restTemplate.getForObject("http://menu-service/menu", String.class);
    log.info(menuString);

    BasicJsonParser jsonParser = new BasicJsonParser();
    Map<String, Object> jsonMap = jsonParser.parseMap(menuString);

    List<String> appetizerOptions = (List<String>)((Map<String, Object>)jsonMap.get("appetizers")).get("menu");
    model.addAttribute("appetizerOptions", appetizerOptions);

    List<String> entreeOptions = (List<String>)((Map<String, Object>)jsonMap.get("entrees")).get("menu");
    model.addAttribute("entreeOptions", entreeOptions);

    List<String> dessertOptions = (List<String>)((Map<String, Object>)jsonMap.get("desserts")).get("menu");
    model.addAttribute("dessertOptions", dessertOptions);

    Span span = this.spanAccessor.getCurrentSpan();
    String traceId = "UNSET";
    if(span != null){
      traceId = Span.idToHex(span.getTraceId());
    }
    model.addAttribute("TraceId", traceId);

    return "index";
  }

  public String getDefaultMenu(Model model){

    List<String> appetizerList = new ArrayList<String>();
                 appetizerList.add("Chips");
                 appetizerList.add("Salsa");
                 appetizerList.add("Bruschetta");

    model.addAttribute("appetizerOptions", appetizerList);

    List<String> entreeList = new ArrayList<String>();
                 entreeList.add("Hamburger");
                 entreeList.add("Hot Dog");
                 entreeList.add("Spaghetti");

    model.addAttribute("entreeOptions", entreeList);

    List<String> dessertList = new ArrayList<String>();
                 dessertList.add("Cookies");
                 dessertList.add("Candy");
                 dessertList.add("Cake");

    model.addAttribute("dessertOptions", dessertList);

    return "index";
  }

}
