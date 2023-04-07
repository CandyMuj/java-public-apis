package com.cc.publicapi.translate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @Description 百度翻译api接口  需要去百度开放平台申请appid和密钥（免费版个人完全够用了）
 * @Author CandyMuj
 * @Date 2023/4/7 14:18
 * @Version 1.0
 */
@Slf4j
public class TranslateBaidu {
    // 百度开放平台申请的appId
    private static final String appId = "xxx";
    // 百度开放平台申请的密钥
    private static final String key = "xxx";


    /**
     * 通用文本翻译
     * tips: 需修改上方全局常量 appId 和 key 为自己申请的
     *
     * @param text 翻译的文本
     * @param from 翻译源语言
     * @param to   翻译目标语言
     * @return 翻译后的内容
     */
    public static String translateText(String text, String from, String to) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost post = new HttpPost("https://fanyi-api.baidu.com/api/trans/vip/translate");

            String salt = RandomUtil.randomString(10);

            post.setEntity(new UrlEncodedFormEntity(
                    Arrays.asList(
                            new BasicNameValuePair("appid", appId),
                            new BasicNameValuePair("sign", SecureUtil.md5(appId + text + salt + key)),
                            new BasicNameValuePair("salt", salt),
                            new BasicNameValuePair("q", text),
                            new BasicNameValuePair("from", from),
                            new BasicNameValuePair("to", to)
                    ),
                    CharsetUtil.CHARSET_UTF_8
            ));

            String rep = EntityUtils.toString(client.execute(post).getEntity(), CharsetUtil.CHARSET_UTF_8);
            log.debug("响应：{}", rep);
            String res = JSONUtil.parseObj(rep).getJSONArray("trans_result")
                    .stream()
                    .map(o -> ((JSONObject) o).getStr("dst"))
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
                "你好",
                "zh",
                "en"
        ));
    }

}
