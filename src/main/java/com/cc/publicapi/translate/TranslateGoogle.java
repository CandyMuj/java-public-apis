package com.cc.publicapi.translate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @Description 谷歌翻译api接口 无需任何密钥 直接可使用
 * @Author CandyMuj
 * @Date 2023/4/7 14:19
 * @Version 1.0
 */
@Slf4j
public class TranslateGoogle {


    /**
     * 翻译文本
     * 接口地址：
     * https://translate.google.com/translate_a/single?client=gtx&dt=t&q=你好&sl=zh-CN&tl=en
     * https://translate.googleapis.com/translate_a/single?client=gtx&dt=t&q=你好&sl=zh-CN&tl=en
     *
     * @param text 翻译的文本
     * @param from 翻译源语言
     * @param to   翻译目标语言
     * @return 翻译后的内容
     */
    public static String translateText(String text, String from, String to) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost("https://translate.google.com/translate_a/single");

            // todo 设置代理  如果不需要代理，则注释 setConfig 即可
            post.setConfig(RequestConfig.custom().setProxy(new HttpHost(
                    "127.0.0.1",  // 代理服务器
                    7890,  // 代理端口
                    "HTTP"  // 代理模式
            )).build());
            post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
            post.setEntity(new UrlEncodedFormEntity(
                    Arrays.asList(
                            new BasicNameValuePair("client", "gtx"),
                            new BasicNameValuePair("dt", "t"),
                            new BasicNameValuePair("q", text),
                            new BasicNameValuePair("sl", from),
                            new BasicNameValuePair("tl", to)
                    ),
                    CharsetUtil.CHARSET_UTF_8
            ));

            String rep = EntityUtils.toString(client.execute(post).getEntity(), CharsetUtil.CHARSET_UTF_8);
            log.debug("响应：{}", rep);
            String res = JSONUtil.parseArray(rep).getJSONArray(0)
                    .stream()
                    .map(o -> ((JSONArray) o).getStr(0))
                    .collect(Collectors.joining(" "));

            log.debug("翻译结果：{}", res);
            return res;
        } catch (Exception e) {
            log.error("翻译异常：{}", e.getMessage());
        }

        return null;
    }


    public static void main(String[] args) {
        Console.log(translateText(
                "你好 hello 你好 he",
                "zh-CN",
                "en"
        ));
    }

}
