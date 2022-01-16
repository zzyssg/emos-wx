package com.example.emoswx.config.xss;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ZZy
 * @date 2022/1/8 14:51
 * @description
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        //非null且不为空串
        if (StrUtil.hasEmpty(name)) {
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                if (!StrUtil.hasEmpty(values[i])) {
                    String value = HtmlUtil.filter(values[i]);
                    values[i] = value;
                }
            }
        }
        return values;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameters = super.getParameterMap();
        Map<String, String[]> map = new LinkedHashMap<>();
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                //key、values过滤后放入put中
                String[] values = parameters.get(key);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        if (!StrUtil.hasEmpty(values[i])) {
                            String value = HtmlUtil.filter(values[i]);
                            values[i] = value;
                        }
                    }
                }
                map.put(key, values);
            }
        }
        return map;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (!StrUtil.hasEmpty(value)) {
            value = HtmlUtil.filter(value);
        }
        return value;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        //对传过来的数据进行取值
        InputStream in = super.getInputStream();
        InputStreamReader reader = new InputStreamReader(in, Charset.forName("UTF-8"));
        BufferedReader buffer = new BufferedReader(reader);
        StringBuffer body = new StringBuffer();
        String line = buffer.readLine();
        while (line != null) {
            body.append(line);
            line = buffer.readLine();
        }
        //关闭io流
        buffer.close();
        reader.close();
        in.close();

        //转义数据
        Map<String, Object> map = JSONUtil.parseObj(body.toString());
        Map<String, Object> resultMap = new LinkedHashMap<>();
        for (String key : map.keySet()) {
            Object obj = map.get(key);
            //仅对字符串的值进行转义
            if (obj instanceof String) {
                resultMap.put(key, HtmlUtil.filter((String) obj));
            } else {
                resultMap.put(key, obj);
            }
        }
        //返回一个io流对象
        String resStr = JSONUtil.toJsonStr(resultMap);
        final ByteArrayInputStream bain = new ByteArrayInputStream(resStr.getBytes());
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return bain.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }
}
