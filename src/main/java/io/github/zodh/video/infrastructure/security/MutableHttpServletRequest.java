package io.github.zodh.video.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MutableHttpServletRequest extends HttpServletRequestWrapper {

  private final Map<String, String> customHeaders = new HashMap<>();

  public MutableHttpServletRequest(HttpServletRequest request) {
    super(request);
  }

  public void putHeader(String name, String value) {
    this.customHeaders.put(name, value);
  }

  @Override
  public String getHeader(String name) {
    String headerValue = customHeaders.get(name);
    if (headerValue != null) {
      return headerValue;
    }
    return super.getHeader(name);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    Set<String> set = new HashSet<>(customHeaders.keySet());
    Enumeration<String> e = super.getHeaderNames();
    while (e.hasMoreElements()) {
      set.add(e.nextElement());
    }
    return Collections.enumeration(set);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    List<String> values = new ArrayList<>();
    if (customHeaders.containsKey(name)) {
      values.add(customHeaders.get(name));
    }
    Enumeration<String> e = super.getHeaders(name);
    while (e.hasMoreElements()) {
      values.add(e.nextElement());
    }
    return Collections.enumeration(values);
  }
}